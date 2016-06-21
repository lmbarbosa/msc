/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ArticleExtraction;

import static ArticleExtraction.OutputNeo4jAssortativity.QueryNeo4jCreateRelationshipValidation;
import static ArticleExtraction.OutputPrinter.mainCountryIsoDBArray;
import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.helpers.collection.IteratorUtil;
import org.neo4j.kernel.impl.util.*;
import org.neo4j.cypher.javacompat.ExecutionEngine;
import org.neo4j.cypher.javacompat.ExecutionResult;
import org.neo4j.graphalgo.CostEvaluator;
import org.neo4j.graphalgo.GraphAlgoFactory;
import org.neo4j.graphalgo.PathFinder;
import org.neo4j.graphalgo.WeightedPath;
import org.neo4j.graphalgo.impl.centrality.BetweennessCentrality;
import org.neo4j.graphalgo.impl.centrality.ClosenessCentrality;
import org.neo4j.graphalgo.impl.centrality.CostDivider;
import org.neo4j.graphalgo.impl.centrality.Eccentricity;
import org.neo4j.graphalgo.impl.centrality.EigenvectorCentralityPower;
import org.neo4j.graphalgo.impl.centrality.NetworkDiameter;
import org.neo4j.graphalgo.impl.centrality.NetworkRadius;
import org.neo4j.graphalgo.impl.centrality.StressCentrality;
import org.neo4j.graphalgo.impl.shortestpath.Dijkstra;
import org.neo4j.graphalgo.impl.shortestpath.SingleSourceShortestPath;
import org.neo4j.graphalgo.impl.shortestpath.SingleSourceShortestPathBFS;
import org.neo4j.graphalgo.impl.shortestpath.SingleSourceShortestPathDijkstra;
import org.neo4j.graphalgo.impl.shortestpath.SingleSourceSingleSinkShortestPath;
import org.neo4j.graphalgo.impl.util.DoubleAdder;
import org.neo4j.graphalgo.impl.util.DoubleComparator;
import org.neo4j.graphalgo.impl.util.DoubleEvaluator;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.PathExpanders;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.ResourceIterator;
import org.neo4j.graphdb.index.UniqueFactory;
import org.neo4j.kernel.Traversal;


public class OutputNeo4j {
    
    private static final String DB_PATH = ArticleExtractionMain.getStrNeo4DBPath();
    static String resultString;
    static String columnsString;
    static String nodeResult;
    static String nodeResult2;
    static String rows = "";  
    static Double doubleNetworkRadius = 0.0; // Smaller Eccentricity Value  
    static Double doubleNetworkDiameter = 0.0; // Biggest Eccentricity Value
        
    private static final int intMaxRelationshipValue = 15;
    
    // START SNIPPET: createReltype
    private static enum RelTypes implements RelationshipType
    {
        AUTHOR_ARTICLE,
        AUTHOR_AUTHOR,
        AUTHOR_KEYWORD,
        AUTHOR_YEAR,
        ARTICLE_COUNTRY,
        ARTICLE_KEYWORD,
        ARTICLE_REFERENCEDBY_ARTICLE,
        ARTICLE_YEAR,
        COUNTRY_COUNTRY,
        COUNTRY_KEYWORD,
        COUNTRY_YEAR,
        KEYWORD_KEYWORD,
        KEYWORD_YEAR
    }
    
    public static void QueryNeo4j (ClassArticle objArticle)
    {
        // START SNIPPET: addData
        GraphDatabaseService graphDb = new GraphDatabaseFactory().newEmbeddedDatabase( DB_PATH );
        Transaction transaction = graphDb.beginTx();
        Relationship relationship;
        
        ExecutionEngine engine = new ExecutionEngine( graphDb );
        ExecutionResult result;
        
        try
        {
            // FACTORY AUTHOR
            UniqueFactory<Node> factoryAuthor = new UniqueFactory.UniqueNodeFactory( graphDb, "author" )
            {
                @Override
                protected void initialize( Node created, Map<String, Object> properties )
                {
                    created.setProperty( "author", properties.get( "author" ) );
                }
            };
            // FACTORY ARTICLE
            UniqueFactory<Node> factoryArticle = new UniqueFactory.UniqueNodeFactory( graphDb, "article" )
            {
                @Override
                protected void initialize( Node created, Map<String, Object> properties )
                {
                    created.setProperty( "article", properties.get( "article" ) );
                }
            };
            // FACTORY COUNTRY
            UniqueFactory<Node> factoryCountry = new UniqueFactory.UniqueNodeFactory( graphDb, "country" )
            {
                @Override
                protected void initialize( Node created, Map<String, Object> properties )
                {
                    created.setProperty( "country", properties.get( "country" ) );
                }
            };
            // FACTORY YEAR
            UniqueFactory<Node> factoryYear = new UniqueFactory.UniqueNodeFactory( graphDb, "year" )
            {
                @Override
                protected void initialize( Node created, Map<String, Object> properties )
                {
                    created.setProperty( "year", properties.get( "year" ) );
                }
            };
            // FACTORY KEYWORD
            UniqueFactory<Node> factoryKeyword = new UniqueFactory.UniqueNodeFactory( graphDb, "keyword" )
            {
                @Override
                protected void initialize( Node created, Map<String, Object> properties )
                {
                    created.setProperty( "keyword", properties.get( "keyword" ) );
                }
            };
            // ************************************* NODE ARTICLE *************************************
            
            String strArticleTileTmp = objArticle.getStrArticleTitle().trim();
            
            Node nodeArticle = factoryArticle.getOrCreate( "article", strArticleTileTmp );

            Label labelArticle = DynamicLabel.label("article");
            nodeArticle.addLabel(labelArticle);

            nodeArticle.setProperty( "title", objArticle.getStrArticleTitle().trim());
            nodeArticle.setProperty( "filename", objArticle.getStrArticleFilename().trim());

            nodeArticle.setProperty( "email", objArticle.getStrArticleEmail().trim());

            String dateCreationArticleDate = "null";
            if (objArticle.getStrArticleCreationDate() != null)
                dateCreationArticleDate = objArticle.getStrArticleCreationDate();

            nodeArticle.setProperty( "pages_number", objArticle.getStrArticlePageNumber());
            String strArticleReferences = UtilFunctions.stringNormalizeFunction(objArticle.getStrArticleReferences());
            nodeArticle.setProperty( "references", strArticleReferences );
            String articlePubYear = objArticle.getStrArticleYear();
            nodeArticle.setProperty( "year", articlePubYear );
            
            nodeArticle.setProperty( "nodeCentralityDegree", 0);
            nodeArticle.setProperty( "nodeCentralityCloseness", 0);
            nodeArticle.setProperty( "nodeCentralityStress", 0);
            nodeArticle.setProperty( "nodeCentralityBetweeness", 0);
            nodeArticle.setProperty( "nodeCentralityEccentricity", 0);
            nodeArticle.setProperty( "nodeCentralityRadiality", 0);
            nodeArticle.setProperty( "nodeCentralityCentroid", 0);
            nodeArticle.setProperty( "nodeCentralityEigenVector", 0);
            nodeArticle.setProperty( "nodeLocalClusteringCoefficient", 0);
            nodeArticle.setProperty( "isValid", "?");

           
            

            // *************************************  NODE AUTHOR *************************************
            
            ArrayList<Node> nodeAuthorArray = new ArrayList<>() ;
            
            for (int i=0; i<objArticle.getAuthorsArray().size() ;i++)
            {    
                String strAuthorTmp = objArticle.getAuthorsArray().get(i).trim();
                
                if (!strAuthorTmp.equals(""))
                {
                    //Node node  = graphDb.createNode();
                    Node nodeAuthor = factoryAuthor.getOrCreate( "author", strAuthorTmp );
                    nodeAuthorArray.add(nodeAuthor);

                    Label labelAuthor = DynamicLabel.label("author");
                    nodeAuthor.addLabel(labelAuthor);
                    nodeAuthor.setProperty( "name", strAuthorTmp);
                    nodeAuthor.setProperty( "name_abbreviated", UtilFunctions.stringShorteningNames(strAuthorTmp));

                    nodeAuthor.setProperty( "nodeCentralityDegree", 0);
                    nodeAuthor.setProperty( "nodeCentralityCloseness", 0);
                    nodeAuthor.setProperty( "nodeCentralityStress", 0);
                    nodeAuthor.setProperty( "nodeCentralityBetweeness", 0);    
                    nodeAuthor.setProperty( "nodeCentralityEccentricity", 0);
                    nodeAuthor.setProperty( "nodeCentralityRadiality", 0);
                    nodeAuthor.setProperty( "nodeCentralityCentroid", 0);
                    nodeAuthor.setProperty( "nodeCentralityEigenVector", 0);
                    nodeAuthor.setProperty( "nodeLocalClusteringCoefficient", 0);
                    nodeAuthor.setProperty( "isValid", "?");
                
                    //System.out.println(objArticle.getAuthorsArray().get(i));
                }
            }    
            
            // *************************************  NODE COUNTRY *************************************
            
            ArrayList<Node> nodeCountryArray = new ArrayList<>() ;
            String strCountryFullName = "";
            String strCountryContinent = "";
            
            for (int i=0; i<objArticle.getCountryArray().size() ;i++)
            {    
                String strCountryTemp = objArticle.getCountryArray().get(i).trim();
                if (!strCountryTemp.equals(""))
                {    
                    Node nodeCountry = factoryCountry.getOrCreate( "country", strCountryTemp);
                    nodeCountryArray.add(nodeCountry);

                    Label labelCountry = DynamicLabel.label("country");
                    nodeCountry.addLabel(labelCountry);

                    for (int k=0; k<mainCountryIsoDBArray.size(); k++)
                    {

                        if (strCountryTemp.equalsIgnoreCase(mainCountryIsoDBArray.get(k).getCountry_iso_alphacode3().trim()))
                        {

                            strCountryFullName = mainCountryIsoDBArray.get(k).getCountry_name1();
                            strCountryContinent = mainCountryIsoDBArray.get(k).getCountry_continent();
                            //System.out.println(strCountryFullName);
                        }
                    }

                    nodeCountry.setProperty( "country_name", strCountryFullName);
                    nodeCountry.setProperty( "country_continent", strCountryContinent);
                    

                    nodeCountry.setProperty( "nodeCentralityDegree", 0);
                    nodeCountry.setProperty( "nodeCentralityCloseness", 0);
                    nodeCountry.setProperty( "nodeCentralityStress", 0);
                    nodeCountry.setProperty( "nodeCentralityBetweeness", 0);
                    nodeCountry.setProperty( "nodeCentralityEccentricity", 0);
                    nodeCountry.setProperty( "nodeCentralityRadiality", 0);
                    nodeCountry.setProperty( "nodeCentralityCentroid", 0);
                    nodeCountry.setProperty( "nodeCentralityEigenVector", 0);
                    nodeCountry.setProperty( "nodeLocalClusteringCoefficient", 0);
                    nodeCountry.setProperty( "isValid", "?");

                    //System.out.println(objArticle.getCountryArray().get(i));
                }
            }    
            
            // ************************************* NODE YEAR *************************************
            Node nodeYear = null;
                       
            articlePubYear = objArticle.getStrArticleYear();
            if (!articlePubYear.equals(""))
            {    
                nodeYear = factoryYear.getOrCreate( "year", articlePubYear);

                Label labelYear = DynamicLabel.label("year");
                nodeYear.addLabel(labelYear);
                nodeYear.setProperty( "yearDate", articlePubYear);

                nodeYear.setProperty( "nodeCentralityDegree", 0);
                nodeYear.setProperty( "nodeCentralityCloseness", 0);
                nodeYear.setProperty( "nodeCentralityStress", 0);
                nodeYear.setProperty( "nodeCentralityBetweeness", 0);
                nodeYear.setProperty( "nodeCentralityEccentricity", 0);
                nodeYear.setProperty( "nodeCentralityRadiality", 0);
                nodeYear.setProperty( "nodeCentralityCentroid", 0);
                nodeYear.setProperty( "nodeCentralityEigenVector", 0);
                nodeYear.setProperty( "nodeLocalClusteringCoefficient", 0);
                nodeYear.setProperty( "isValid", "?");
            }
            
            // *************************************  NODE KEYWORDS *************************************
            
            ArrayList<Node> nodeKeywordArray = new ArrayList<>() ;
            
            for (int i=0; i<objArticle.getKeywordsArray().size() ;i++)
            {    
                
                
                //Node node  = graphDb.createNode();
                String strArticleKeyword = UtilFunctions.stringNormalizeFunction(objArticle.getKeywordsArray().get(i).trim());
                
                if (!strArticleKeyword.equals(""))
                { 

                    Node nodeKeyword = factoryKeyword.getOrCreate( "keyword", strArticleKeyword );
                    nodeKeywordArray.add(nodeKeyword);

                    Label labelKeyword = DynamicLabel.label("keyword");
                    nodeKeyword.addLabel(labelKeyword);

                    nodeKeyword.setProperty( "keyword", strArticleKeyword);  
                    //nodeKeyword.setProperty( "keyword", objArticle.getKeywordsArray().get(i));

                    nodeKeyword.setProperty( "nodeCentralityDegree", 0);
                    nodeKeyword.setProperty( "nodeCentralityCloseness", 0);
                    nodeKeyword.setProperty( "nodeCentralityStress", 0);
                    nodeKeyword.setProperty( "nodeCentralityBetweeness", 0);
                    nodeKeyword.setProperty( "nodeCentralityEccentricity", 0);
                    nodeKeyword.setProperty( "nodeCentralityRadiality", 0);
                    nodeKeyword.setProperty( "nodeCentralityCentroid", 0);
                    nodeKeyword.setProperty( "nodeCentralityEigenVector", 0);
                    nodeKeyword.setProperty( "nodeLocalClusteringCoefficient", 0);
                    nodeKeyword.setProperty( "isValid", "");
                }
            }    
            
 
            
            // @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ RELATIONSHIP AUTHOR x AUTHOR @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
            
            for (int i=0; i<nodeAuthorArray.size() ;i++)
            {   
                Node currentNode = nodeAuthorArray.get(i);
                for (int j=i+1; j<nodeAuthorArray.size() ;j++)
                {
                    Node nextNode = nodeAuthorArray.get(j);
                    if (QueryNeo4jCreateRelationshipValidation(currentNode, nextNode, RelTypes.AUTHOR_AUTHOR ))
                    {
                        relationship = currentNode.createRelationshipTo( nextNode, RelTypes.AUTHOR_AUTHOR );
                        relationship.setProperty( "weight", 1 );
                        relationship.setProperty( "direction", "bidirectional" );
                    }
                    else // else this relationship already exists
                    {
                        relationship = QueryNeo4jGetRelationship(currentNode, nextNode, RelTypes.AUTHOR_AUTHOR);
                        int objRelWeight = (int)relationship.getProperty("weight");
                        objRelWeight++;
                        relationship.setProperty( "weight", objRelWeight );
                    }
                }

            }
              /*         
            // @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ RELATIONSHIP AUTHOR x KEYWORD @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
            
            for (int i=0; i<nodeAuthorArray.size() ;i++)
            {   
                Node currentNode = nodeAuthorArray.get(i);
                for (int j=i; j<nodeKeywordArray.size() ;j++)
                {
                    Node nextNode = nodeKeywordArray.get(j);
                    if (QueryNeo4jCreateRelationshipValidation(currentNode, nextNode, RelTypes.AUTHOR_KEYWORD ))
                    {
                        relationship = currentNode.createRelationshipTo( nextNode, RelTypes.AUTHOR_KEYWORD );
                        relationship.setProperty( "weight", 1 );
                        relationship.setProperty( "direction", "directional" );
                    }
                    else // else this relationship already exists
                    {
                        relationship = QueryNeo4jGetRelationship(currentNode, nextNode, RelTypes.AUTHOR_KEYWORD);
                        int objRelWeight = (int)relationship.getProperty("weight");
                        objRelWeight++;
                        relationship.setProperty( "weight", objRelWeight );
                    }
                }
            }
            */
            
            // @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ RELATIONSHIP KEYWORD x KEYWORD @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
            
            for (int i=0; i<nodeKeywordArray.size()  ;i++)
            {   
                Node currentNode = nodeKeywordArray.get(i);
                for (int j=i+1; j<nodeKeywordArray.size()  ;j++)
                {
                    Node nextNode = nodeKeywordArray.get(j);
                    if (QueryNeo4jCreateRelationshipValidation(currentNode, nextNode, RelTypes.KEYWORD_KEYWORD ))
                    {
                        relationship = currentNode.createRelationshipTo( nextNode, RelTypes.KEYWORD_KEYWORD);
                        relationship.setProperty( "weight", 1 );
                        relationship.setProperty( "direction", "bidirectional" );
                    }
                    else // else this relationship already exists
                    {
                        relationship = QueryNeo4jGetRelationship(currentNode, nextNode, RelTypes.KEYWORD_KEYWORD);
                        int objRelWeight = (int)relationship.getProperty("weight");
                        objRelWeight++;
                        relationship.setProperty( "weight", objRelWeight );
                    }
                }

            }
            
            // @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ RELATIONSHIP ARTICLE x AUTHOR @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
            
            for (int i=0; i<nodeAuthorArray.size() ;i++)
            {   
                if (QueryNeo4jCreateRelationshipValidation(nodeAuthorArray.get(i), nodeArticle, RelTypes.AUTHOR_ARTICLE ))
                {
                    relationship = nodeAuthorArray.get(i).createRelationshipTo( nodeArticle, RelTypes.AUTHOR_ARTICLE );
                    relationship.setProperty( "weight", 1 );
                    relationship.setProperty( "direction", "directional" );
               }
               else // else this relationship already exists
               {
                   relationship = QueryNeo4jGetRelationship(nodeAuthorArray.get(i), nodeArticle, RelTypes.AUTHOR_ARTICLE );
                   int objRelWeight = (int)relationship.getProperty("weight");
                   objRelWeight++;
                   relationship.setProperty( "weight", objRelWeight );
               }
            }
            
            // @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ RELATIONSHIP ARTICLE x COUNTRY @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
            
            for (int i=0; i<nodeCountryArray.size() ;i++)
            {   
                if (QueryNeo4jCreateRelationshipValidation(nodeCountryArray.get(i), nodeArticle, RelTypes.ARTICLE_COUNTRY ))
                {
                    relationship = nodeArticle.createRelationshipTo( nodeCountryArray.get(i), RelTypes.ARTICLE_COUNTRY );
                    relationship.setProperty( "weight", 1 );
                    relationship.setProperty( "direction", "directional" );
               }
               else // else this relationship already exists
               {
                   relationship = QueryNeo4jGetRelationship( nodeCountryArray.get(i), nodeArticle, RelTypes.ARTICLE_COUNTRY );
                   int objRelWeight = (int)relationship.getProperty("weight");
                   objRelWeight++;
                   relationship.setProperty( "weight", objRelWeight );
               }
            }
            
             // @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ RELATIONSHIP COUNTRY x COUNTRY @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
            
            for (int i=0; i<nodeCountryArray.size() ;i++)
            {   
                Node currentNode = nodeCountryArray.get(i);
                for (int j=i+1; j<nodeCountryArray.size() ;j++)
                {
                    Node nextNode = nodeCountryArray.get(j);
                    if (QueryNeo4jCreateRelationshipValidation(currentNode, nextNode, RelTypes.COUNTRY_COUNTRY ))
                    {
                        relationship = currentNode.createRelationshipTo( nextNode, RelTypes.COUNTRY_COUNTRY );
                        relationship.setProperty( "weight", 1 );
                        relationship.setProperty( "direction", "bidirectional" );
                    }
                    else // else this relationship already exists
                    {
                        relationship = QueryNeo4jGetRelationship(currentNode, nextNode, RelTypes.COUNTRY_COUNTRY );
                        int objRelWeight = (int)relationship.getProperty("weight");
                        objRelWeight++;
                        relationship.setProperty( "weight", objRelWeight );
                    }
                }
            }
            
            
            // @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ RELATIONSHIP ARTICLE x YEAR @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
            if (objArticle.getStrArticleYear() != "0")
            {
                if (QueryNeo4jCreateRelationshipValidation(nodeArticle, nodeYear, RelTypes.ARTICLE_YEAR ))
                {
                    relationship = nodeArticle.createRelationshipTo( nodeYear, RelTypes.ARTICLE_YEAR );
                    relationship.setProperty( "weight", 1 );
                    relationship.setProperty( "direction", "directional" );
                }
            }
            
            // @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ RELATIONSHIP ARTICLE x KEYWORD @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
            
            for (int i=0; i<nodeKeywordArray.size() ;i++)
            {   
                if (QueryNeo4jCreateRelationshipValidation(nodeArticle, nodeKeywordArray.get(i), RelTypes.ARTICLE_KEYWORD ))
                {
                    relationship = nodeArticle.createRelationshipTo( nodeKeywordArray.get(i), RelTypes.ARTICLE_KEYWORD );
                    relationship.setProperty( "weight", 1 );
                    relationship.setProperty( "direction", "directional" );
                }
            }
            
            
        }
        catch(Exception e)
        {
            System.out.println("Node Creation error: " + e);
            System.out.println(objArticle.getStrArticleTitle().trim());
        }
        finally
        {
            transaction.success();
        }

       
        transaction.finish();
        graphDb.shutdown();
    } // END QueryNeo4j
    
    public static boolean QueryNeo4jCreateRelationshipValidation (Node nodeStart, Node nodeEnd, RelationshipType reltype)
    {
        boolean relationship_validation = true;
        
        for(Relationship r : nodeStart.getRelationships(reltype)) 
        {
            if(r.getOtherNode(nodeStart).equals(nodeEnd)) 
            { // put other conditions here, if needed
              relationship_validation = false;
            }
        }
        
        return relationship_validation;
    }
    
    public static Relationship QueryNeo4jGetRelationship (Node nodeStart, Node nodeEnd, RelationshipType reltype)
    {
        Relationship relGetRelationship = null;
        
        for(Relationship r : nodeStart.getRelationships(reltype)) 
        {
            if(r.getOtherNode(nodeStart).equals(nodeEnd)) 
            { // put other conditions here, if needed
              relGetRelationship = r;
            }
        }
        
        return relGetRelationship;
    }
    
    public static void QueryNeo4jArticleRelationship ()
    {
        
        GraphDatabaseService graphDb = new GraphDatabaseFactory().newEmbeddedDatabase( DB_PATH );
        Transaction transaction = graphDb.beginTx();
        Relationship relationship;
        
        ExecutionEngine engine = new ExecutionEngine( graphDb );
        ExecutionResult resultArticle, resultArticleReference;
        String strArticleTile = "";
        
        // @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ RELATIONSHIP ARTICLE x ARTICLE @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
     
        try 
        {

            String strArticleQuery =  "MATCH (n:`article`) RETURN n, n.title";
            resultArticle = engine.execute(strArticleQuery);
            
            //System.out.println("All Article Query:" + resultArticle);

            Iterator<Node> n_column = resultArticle.columnAs( "n" );
            for ( Node nodeArticle : IteratorUtil.asIterable( n_column ) )
            {              
                // note: we're grabbing the property from the node,
                // not from the n.property in this case.
                nodeResult = nodeArticle + ": " + nodeArticle.getProperty( "title" );
                
                // Fixing Neo4j Regex bug for Hyphen/Dash
                strArticleTile = nodeArticle.getProperty( "title" ).toString();
                strArticleTile = strArticleTile.replaceAll("-", ".*");
                
                String strArticleReferenceQuery =  "MATCH (n:`article`) WHERE TRIM(LOWER(n.references)) =~ TRIM(LOWER('.*" + strArticleTile + ".*')) RETURN n, n.title";
                resultArticleReference = engine.execute(strArticleReferenceQuery);
                //System.out.println("strArticleReferenceQuery:" + strArticleReferenceQuery);
                //System.out.println("##### Article Loop #####:" + nodeArticle.getProperty( "title" ));
                
                Iterator<Node> n_column2 = resultArticleReference.columnAs( "n" );
                
                for ( Node nodeArticle2 : IteratorUtil.asIterable( n_column2 ) )
                {
                
                    nodeResult2 = nodeArticle2 + ": " + nodeArticle2.getProperty( "title" );    

                    if (QueryNeo4jCreateRelationshipValidation(nodeArticle, nodeArticle2, RelTypes.ARTICLE_REFERENCEDBY_ARTICLE )
                            && nodeArticle.getId() != nodeArticle2.getId() )
                    {
                        relationship = nodeArticle.createRelationshipTo( nodeArticle2, RelTypes.ARTICLE_REFERENCEDBY_ARTICLE );
                        //relationship.setProperty( "IS REFERENCED BY", "RelProperty1Description" );
                        relationship.setProperty( "weight", 1 );
                        relationship.setProperty( "direction", "directional" );
                        
                        System.out.println("##### FOUND #####:" + nodeArticle2.getProperty( "title" ));
                    }// else this relationship already exists 
                    else if (!QueryNeo4jCreateRelationshipValidation(nodeArticle, nodeArticle2, RelTypes.ARTICLE_REFERENCEDBY_ARTICLE )
                            && nodeArticle.getId() != nodeArticle2.getId())
                    {
                        relationship = QueryNeo4jGetRelationship(nodeArticle, nodeArticle2, RelTypes.ARTICLE_REFERENCEDBY_ARTICLE);
                        int objRelWeight = (int)relationship.getProperty("weight");
                        objRelWeight++;
                        relationship.setProperty( "weight", objRelWeight );
                    }
                }
            }
            
            // END SNIPPET: items
        }
        catch (Exception e)
        {
            System.out.println("Article Relationship error: " + e);
        }
        finally
        {
            transaction.success();
        }
        
        transaction.finish();
        graphDb.shutdown();
    } // END QueryNeo4jArticleRelationship
    
   
    
    public static void QueryNeo4jNodeCentralityDegree ()
    {
        
        GraphDatabaseService graphDb = new GraphDatabaseFactory().newEmbeddedDatabase( DB_PATH );
        Transaction transaction = graphDb.beginTx();
        
        ExecutionEngine engine = new ExecutionEngine( graphDb );
        ExecutionResult resultNode;
        
        
        
        try 
        {

            String strNodeQuery =  "START n=node(*) " +
                                    "MATCH n-[r]-n2 " +
                                    "RETURN DISTINCT n as n_obj " +
                                    "ORDER BY id(n) ASC; " ;
            resultNode = engine.execute(strNodeQuery);

            //System.out.println("Node Centrality Query:" + resultNode);

            Iterator<Node> n_object = resultNode.columnAs( "n_obj" );
            
            Set<Node> setNode = new HashSet<Node>() {};
            
            for ( Node nodeStart : IteratorUtil.asIterable( n_object ) )
            {
                setNode.add(nodeStart);
            }

            for (Node node:setNode)
            {              
                
                Iterator iteratorRelationship = node.getRelationships().iterator();
                
                int intRelCount = 0;
                
                while(iteratorRelationship.hasNext() )
                {
                    intRelCount++;
                    iteratorRelationship.next();
                }    
                
                Double doubleNodeDegree = ((double)intRelCount/(setNode.size()-1)); // relative value
                //node.setProperty( "nodeCentralityDegree", doubleNodeDegree); // relative value
                node.setProperty( "nodeCentralityDegree", intRelCount); // absolute value
                
                //Debugging purpuses
                //System.out.println("nodeCentralityDegree[" + node.getId() + "] :" + intRelCount);
            }
            
            // END SNIPPET: items
        }
        catch (Exception e)
        {
            System.out.println("Node Centrality Degree error: " + e);
        }
        finally
        {
            transaction.success();
        }
        
        transaction.finish();
        graphDb.shutdown();
    } // END QueryNeo4jNodeCentralityDegree
    
    /*  barbosalm - Implementação própria para Centraliade de proximidade

    public static void QueryNeo4jNodeCentralityCloseness ()
    {
        
        // IMPORTANTE 
        // FAZER VERFICACAO PREVIA SE GRAFO E CONEXO
        
        GraphDatabaseService graphDb = new GraphDatabaseFactory().newEmbeddedDatabase( DB_PATH );
        Transaction transaction = graphDb.beginTx();
        
        ExecutionEngine engine = new ExecutionEngine( graphDb );
        ExecutionResult resultNode1, resultNode2;
        
        double doubleNodeCentralityCloseness = 0;
        
        try 
        {

            String strNodeQuery =  "START n=node(*) " +
                                    "RETURN n AS n_obj " +
                                    "ORDER BY id(n_obj) ASC; " ;
            resultNode1 = engine.execute(strNodeQuery);

            //System.out.println("Node Centrality Closeness Query:" + resultNode);

            Iterator<Node> n_object1 = resultNode1.columnAs( "n_obj" );
        
            for ( Node nodeStart : IteratorUtil.asIterable( n_object1 ) )
            {
                String strNodeQuery2 =  "MATCH p = shortestPath((a1)-[*..15]-(a2)) " +
                                        " WHERE (id(a1) = " + nodeStart.getId() + " AND id(a2) <> " + nodeStart.getId() + ") " +
                                        " RETURN SUM(LENGTH(p)) as shortestPathSum" ;
                resultNode2 = engine.execute(strNodeQuery2);
                
                Iterator<Integer> n_object2 = resultNode2.columnAs( "shortestPathSum" );
                doubleNodeCentralityCloseness = n_object2.next();
                               
                doubleNodeCentralityCloseness = 1/doubleNodeCentralityCloseness;
                nodeStart.setProperty( "nodeCentralityCloseness", doubleNodeCentralityCloseness);
                //System.out.println("nodeCentralityCloseness[" + nodeStart.getId() + "] :" + doubleNodeCentralityCloseness);
            }

        // END SNIPPET: shortestPathUsage
        }
        catch (Exception e)
        {
            System.out.println("Node Centrality Closeness error: " + e);
        }
        finally
        {
            transaction.success();
        }
        
        transaction.finish();
        graphDb.shutdown();
    } // END QueryNeo4jNodeCentralityCloseness
    */
    
    
    // Node centrality closeness using Djistra to find the shortest path
    public static void QueryNeo4jNodeCentralityCloseness ()
    {
        GraphDatabaseService graphDb = new GraphDatabaseFactory().newEmbeddedDatabase( DB_PATH );
        Transaction transaction = graphDb.beginTx();
        
        ExecutionEngine engine = new ExecutionEngine( graphDb );
        ExecutionResult resultNode1;

        try 
        {

            String strNodeQuery =  "START n=node(*) " +
                                    "RETURN n AS n_obj " +
                                    "ORDER BY id(n_obj) ASC; " ;
            resultNode1 = engine.execute(strNodeQuery);

            //System.out.println("Node Centrality Closeness Query:" + resultNode);

            Iterator<Node> n_object1 = resultNode1.columnAs( "n_obj" );
            
            Set<Node> setNode = new HashSet<Node>() {};

            for ( Node nodeStart : IteratorUtil.asIterable( n_object1 ) )
            {
                setNode.add(nodeStart);

            }
            
            ClosenessCentrality<Double> closenessCentrality = new ClosenessCentrality<Double>(
            getSingleSourceShortestPath(), new DoubleAdder(), 0.0, setNode,
            new CostDivider<Double>()
                {
                public Double divideByCost( Double d, Double c )
                {
                    return d / c;
                }

                public Double divideCost( Double c, Double d )
                {
                    return c / d;
                }
            });

            closenessCentrality.calculate();

            for (Node node:setNode){
                    Double doubleNodeCentralityCloseness = closenessCentrality.getCentrality(node);
                    
                    //doubleNodeCentralityCloseness = (setNode.size()-1)/doubleNodeCentralityCloseness;
                    //node.setProperty( "nodeCentralityCloseness", doubleNodeCentralityCloseness); //relative value
                    
                    node.setProperty( "nodeCentralityCloseness", doubleNodeCentralityCloseness); //absolute value
            //System.out.print("closeness centrality of node: " + node.getId() + " is ");
            //System.out.println(result);
            }
            
        // END SNIPPET: shortestPathUsage
        }
        catch (Exception e)
        {
            System.out.println("Node Centrality Closeness error: " + e);
            System.out.println("IMPORTANT: Please check if this is a connected graph.");
        }
        finally
        {
            transaction.success();
        }
        
        transaction.finish();
        graphDb.shutdown();
        
    }// END QueryNeo4jNodeCentralityCloseness
    
     // Node centrality stress using Djistra to find the shortest path
    public static void QueryNeo4jNodeCentralityStress ()
    {
        GraphDatabaseService graphDb = new GraphDatabaseFactory().newEmbeddedDatabase( DB_PATH );
        Transaction transaction = graphDb.beginTx();
        
        ExecutionEngine engine = new ExecutionEngine( graphDb );
        ExecutionResult resultNode1;

        try 
        {

            String strNodeQuery =  "START n=node(*) " +
                                    "RETURN n AS n_obj " +
                                    "ORDER BY id(n_obj) ASC; " ;
            resultNode1 = engine.execute(strNodeQuery);

            //System.out.println("Node Centrality Stress Query:" + resultNode);

            Iterator<Node> n_object1 = resultNode1.columnAs( "n_obj" );
            
            Set<Node> setNode = new HashSet<Node>() {};

            for ( Node nodeStart : IteratorUtil.asIterable( n_object1 ) )
            {
                setNode.add(nodeStart);

            }
            
            StressCentrality<Double> stressCentrality = new StressCentrality<Double>(
            getSingleSourceShortestPath(), setNode);
            
            stressCentrality.calculate();

            for (Node node:setNode){
                    Double doubleStressCentralityResult = stressCentrality.getCentrality(node);
                    node.setProperty( "nodeCentralityStress", doubleStressCentralityResult);
                    
                   //System.out.print("stressCentrality of node: " + node.getId() + " is ");
                   //System.out.println(doubleStressCentralityResult);
            }
            
        // END SNIPPET: shortestPathUsage
        }
        catch (Exception e)
        {
            System.out.println("Node Centrality Stress error: " + e);
        }
        finally
        {
            transaction.success();
        }
        
        transaction.finish();
        graphDb.shutdown();
        
    }// END QueryNeo4jNodeCentralityStress
    
    // Node centrality betweeness using Djistra to find the shortest path
    public static void QueryNeo4jNodeCentralityBetweeness ()
    {
        GraphDatabaseService graphDb = new GraphDatabaseFactory().newEmbeddedDatabase( DB_PATH );
        Transaction transaction = graphDb.beginTx();
        
        ExecutionEngine engine = new ExecutionEngine( graphDb );
        ExecutionResult resultNode1;

        try 
        {

            String strNodeQuery =  "START n=node(*) " +
                                    "RETURN n AS n_obj " +
                                    "ORDER BY id(n_obj) ASC; " ;
            resultNode1 = engine.execute(strNodeQuery);

            //System.out.println("Node Centrality Betweeness Query:" + resultNode);

            Iterator<Node> n_object1 = resultNode1.columnAs( "n_obj" );
            
            Set<Node> setNode = new HashSet<Node>() {};

            for ( Node nodeStart : IteratorUtil.asIterable( n_object1 ) )
            {
                setNode.add(nodeStart);

            }
            
            BetweennessCentrality<Double> betweennessCentrality = new BetweennessCentrality<Double>(
            getSingleSourceShortestPath(), setNode);
            
            betweennessCentrality.calculate();

            for (Node node:setNode){
                   
                    Double doubleBetweennessCentralityResult = betweennessCentrality.getCentrality(node);
                    node.setProperty( "nodeCentralityBetweeness", doubleBetweennessCentralityResult);
                    
                   //System.out.print("betweenness centrality of node: " + node.getId() + " is ");
                   //System.out.println(doubleBetweennessCentralityResult);
            }
            
        // END SNIPPET: shortestPathUsage
        }
        catch (Exception e)
        {
            System.out.println("Node Centrality Betweeness error: " + e);
        }
        finally
        {
            transaction.success();
        }
        
        transaction.finish();
        graphDb.shutdown();
        
    }// END QueryNeo4jNodeCentralityBetweeness
    
    // Node centrality Eccentricity using ShortesPath to find the shortest path
    public static void QueryNeo4jNodeCentralityEccentricity ()
    {
        GraphDatabaseService graphDb = new GraphDatabaseFactory().newEmbeddedDatabase( DB_PATH );
        Transaction transaction = graphDb.beginTx();
        
        ExecutionEngine engine = new ExecutionEngine( graphDb );
        ExecutionResult resultNode1;
        
       

        try 
        {

            String strNodeQuery =  "START n=node(*) " +
                                    "RETURN n AS n_obj " +
                                    "ORDER BY id(n_obj) ASC; " ;
            resultNode1 = engine.execute(strNodeQuery);

            //System.out.println("Node Centrality Eccentricity Query:" + resultNode);

            Iterator<Node> n_object1 = resultNode1.columnAs( "n_obj" );
            
            Set<Node> setNode = new HashSet<Node>() {};

            for ( Node nodeStart : IteratorUtil.asIterable( n_object1 ) )
            {
                setNode.add(nodeStart);
            }
            
            // Smaller Eccentricity Value
            doubleNetworkRadius = (double)setNode.size();
            //Double doubleFunctionNetworkRadius = 0.0;

            // Biggest Eccentricity Value
            doubleNetworkDiameter = 0.0;
            //Double doubleFunctionNetworkDiameter = 0.0;
                        
            Eccentricity<Double> eccentricityCentrality = new Eccentricity<Double>(
            getSingleSourceShortestPath(), 0.0, setNode, new DoubleComparator());
            /*
            NetworkRadius<Double> radius = new NetworkRadius<Double>(
            getSingleSourceShortestPath(), 0.0, setNode, new DoubleComparator());
            
            NetworkDiameter<Double> diameter = new NetworkDiameter<Double>(
            getSingleSourceShortestPath(), 0.0, setNode, new DoubleComparator());
            
            doubleFunctionNetworkRadius = radius.getCentrality(null);
            doubleFunctionNetworkDiameter= diameter.getCentrality(null);   
            */
                       
            eccentricityCentrality.calculate();
       
            for (Node node:setNode){
                   
                    Double doubleEccentricityCentralityCentralityResult = eccentricityCentrality.getCentrality(node);
                    node.setProperty( "nodeCentralityEccentricity", doubleEccentricityCentralityCentralityResult);
                    
                    if (doubleEccentricityCentralityCentralityResult < doubleNetworkRadius)
                        doubleNetworkRadius = doubleEccentricityCentralityCentralityResult;
                    
                    if (doubleEccentricityCentralityCentralityResult > doubleNetworkDiameter)
                        doubleNetworkDiameter = doubleEccentricityCentralityCentralityResult;
                   //System.out.print("eccentricity centrality of node: " + node.getId() + " is ");
                   //System.out.println(doubleEccentricityCentralityResult);
            }       
            
            System.out.println("Network Radius: " + doubleNetworkRadius);
            System.out.println("Network Diameter: " + doubleNetworkDiameter);
            /*
            System.out.println("Function Network Radius: " + doubleFunctionNetworkRadius);
            System.out.println("Function Network Diameter: " + doubleFunctionNetworkDiameter);
            */
            
        // END SNIPPET: shortestPathUsage
        }
        catch (Exception e)
        {
            System.out.println("Node Centrality Eccentricity error: " + e);
        }
        finally
        {
            transaction.success();
        }
              
        transaction.finish();
        graphDb.shutdown();
        
    }// END QueryNeo4jNodeCentralityEccentricity
    
    // Node centrality Eccentricity Centroid 
    public static void QueryNeo4jNodeCentralityCentroid ()
    {
        GraphDatabaseService graphDb = new GraphDatabaseFactory().newEmbeddedDatabase( DB_PATH );
        Transaction transaction = graphDb.beginTx();
        
        ExecutionEngine engine = new ExecutionEngine( graphDb );
        ExecutionResult resultNode, resultNodeCentral;
        
        Integer intNodeCentroid = 0;
        
        try 
        {
            // ALL NODES
            String strNodeQuery =  "START n=node(*) " +
                                    " RETURN n AS n_obj " +
                                    " ORDER BY id(n_obj) ASC; " ;
            resultNode = engine.execute(strNodeQuery);
            
            Iterator<Node> nodeIterator = resultNode.columnAs( "n_obj" );
            
            Set<Node> setNode = new HashSet<Node>() {};
            
            for ( Node nodeStart : IteratorUtil.asIterable( nodeIterator ) )
            {
                setNode.add(nodeStart);
            }

            // CENTRAL NODES
            String strNodeCentralQuery =  "MATCH s" +
                                            " WITH max(s.nodeCentralityCloseness) as maxCc" +
                                            " MATCH n " +
                                            " WHERE n.nodeCentralityCloseness = maxCc" +
                                            " RETURN n AS n_obj " +
                                            " ORDER BY id(n_obj) ASC; " ;
                    
            resultNodeCentral = engine.execute(strNodeCentralQuery);

            //System.out.println("Node Centrality Eccentricity Query:" + resultNode);

            Iterator<Node> nodeCentralIterator = resultNodeCentral.columnAs( "n_obj" );
            
            Set<Node> setNodeCentral = new HashSet<Node>() {};

            for ( Node nodeStart : IteratorUtil.asIterable( nodeCentralIterator ) )
            {
                setNodeCentral.add(nodeStart);
            }
            
            PathFinder<Path> finder = GraphAlgoFactory.shortestPath(PathExpanders.allTypesAndDirections(), 15 );
                        
            for (Node startNode:setNode)
            {
                Iterator<Node> iteratorCentralNode = setNodeCentral.iterator();    
                intNodeCentroid = 0;
                
		while (iteratorCentralNode.hasNext()) 
                {
                    Node endNode = iteratorCentralNode.next();
                    //PathFinder<WeightedPath> finder = GraphAlgoFactory.dijkstra(PathExpanders.allTypesAndDirections(), "cost" );
                    //WeightedPath path = finder.findSinglePath( node, nodeB );
                    //path.weight();
                    
                    Iterable<Path> paths = finder.findAllPaths( startNode, endNode );
                    Path path = paths.iterator().next();  
                    if (intNodeCentroid == 0)
                        intNodeCentroid = path.length();
                    else if (path.length() < intNodeCentroid)
                        intNodeCentroid = path.length();
                    
                    if (startNode.getId() == endNode.getId())
                        intNodeCentroid = 0;
                    
                    
                //System.out.print("centrality centroid of node: " + node.getId() + " is ");
                //System.out.println(intNodeRadiality);
                }
                
                startNode.setProperty( "nodeCentralityCentroid", intNodeCentroid);
                //System.out.println("nodeCentralityEccentricityCentroid: " + intNodeRadiality);
            }       

        }
        catch (Exception e)
        {
            System.out.println("Node Centrality Centroid error: " + e);
        }
        finally
        {
            transaction.success();
        }
              
        transaction.finish();
        graphDb.shutdown();
        
    }// END QueryNeo4jNodeCentralityCentroid 

    // Node centrality Eccentricity Radiality using Djistra to find the shortest path
    public static void QueryNeo4jNodeCentralityRadiality ()
    {
        GraphDatabaseService graphDb = new GraphDatabaseFactory().newEmbeddedDatabase( DB_PATH );
        Transaction transaction = graphDb.beginTx();
        
        ExecutionEngine engine = new ExecutionEngine( graphDb );
        ExecutionResult resultNode1;
        
        Double doubleNodeRadiality = 0.0;
        
        try 
        {

            String strNodeQuery =  "START n=node(*) " +
                                    "RETURN n AS n_obj " +
                                    "ORDER BY id(n_obj) ASC; " ;
            resultNode1 = engine.execute(strNodeQuery);

            //System.out.println("Node Centrality Eccentricity Query:" + resultNode);

            Iterator<Node> n_object1 = resultNode1.columnAs( "n_obj" );
            
            Set<Node> setNode = new HashSet<Node>() {};
            
            PathFinder<Path> finder = GraphAlgoFactory.shortestPath(PathExpanders.allTypesAndDirections(), 15 );

            for ( Node nodeStart : IteratorUtil.asIterable( n_object1 ) )
            {
                setNode.add(nodeStart);
            }
            
          
            for (Node startNode:setNode)
            {
                Iterator<Node> iterator = setNode.iterator();    
                doubleNodeRadiality = 0.0;
                
		while (iterator.hasNext()) 
                {
                    Node endNode = iterator.next();
                    //PathFinder<WeightedPath> finder = GraphAlgoFactory.dijkstra(PathExpanders.allTypesAndDirections(), "cost" );
                    //WeightedPath path = finder.findSinglePath( node, nodeB );
                    //path.weight();
                    if (startNode.getId() != endNode.getId())
                    {
                        Iterable<Path> paths = finder.findAllPaths( startNode, endNode );
                        Path path = paths.iterator().next();  
                        doubleNodeRadiality += doubleNetworkDiameter+1.00-path.length();
                    }
                    
                //System.out.print("eccentricity centrality of node: " + node.getId() + " is ");
                //System.out.println(doubleEccentricityCentralityResult);
                }
                doubleNodeRadiality = doubleNodeRadiality/(setNode.size()-1); // radiality formula
                doubleNodeRadiality = doubleNodeRadiality/(setNode.size()-1); // normalize
                
                startNode.setProperty( "nodeCentralityRadiality", doubleNodeRadiality);
                //System.out.println("nodeCentralityEccentricityRadiality: " + doubleNodeRadiality);
            }       

        }
        catch (Exception e)
        {
            System.out.println("Node Centrality Radiality error: " + e);
        }
        finally
        {
            transaction.success();
        }
              
        transaction.finish();
        graphDb.shutdown();
        
    }// END QueryNeo4jNodeCentralityRadiality 
    
    // Function used by QueryNeo4jNodeCentralityFunctions
    protected static SingleSourceShortestPath<Double> getSingleSourceShortestPath()
       {
           return new SingleSourceShortestPathDijkstra<Double>( 0.0, null,
               new CostEvaluator<Double>()
               {
                   public Double getCost( Relationship relationship,
                               Direction direction )
                   {
                       return 1.0;
                   }
               }, new org.neo4j.graphalgo.impl.util.DoubleAdder(),
               new org.neo4j.graphalgo.impl.util.DoubleComparator(),
               Direction.BOTH, RelTypes.values() );
       }

    public static void QueryNeo4jNodeLocalClusteringCoefficient()
    {
        
        GraphDatabaseService graphDb = new GraphDatabaseFactory().newEmbeddedDatabase( DB_PATH );
        Transaction transaction = graphDb.beginTx();
        
        ExecutionEngine engine = new ExecutionEngine( graphDb );
        ExecutionResult resultNode, resultNode2;
        
        Integer intNodeNeighborNumber = 0;
        Integer intNodeNeighborRelationshipNumber = 0;
        Double doubleNodeLocalClusteringCoefficient = 0.0;
        
        try 
        {

            String strNodeQuery =  "START n=node(*) " +
                                    "MATCH n-[r]-n2 " +
                                    "RETURN DISTINCT n as n_obj " +
                                    "ORDER BY id(n) ASC; " ;
            resultNode = engine.execute(strNodeQuery);
            
            //System.out.println("Node Centrality Query:" + resultNode);

            Iterator<Node> n_object1 = resultNode.columnAs( "n_obj" );
            

            for ( Node nodeStart : IteratorUtil.asIterable( n_object1 ) )
            {
                doubleNodeLocalClusteringCoefficient = 0.0;
                
                // Conta o numero de triangulos entre os vizinhos e o respectivo nó analisado
                String strNodeQuery2 =  "MATCH (a)--(b)" +
                                        "WHERE id(a)= " + nodeStart.getId() + 
                                        " WITH a, count(DISTINCT b) AS n" +
                                        " MATCH (a)--()-[r]-()--(a)" +
                                        " RETURN count(DISTINCT r) AS nodeNeighborTriplets";
                resultNode2 = engine.execute(strNodeQuery2);
                
                Iterator<Long> n_object2 = resultNode2.columnAs( "nodeNeighborTriplets" );

                intNodeNeighborNumber = (Integer)nodeStart.getProperty("nodeCentralityDegree");
                intNodeNeighborRelationshipNumber = n_object2.next().intValue();
                
                //Debugging purpuses
                //System.out.println("intNodeNeighborNumber[" + nodeStart.getId() + "] :" + intNodeNeighborNumber);
                //System.out.println("intNodeNeighborRelationshipNumber[" + nodeStart.getId() + "] :" + intNodeNeighborRelationshipNumber);
                
                if (intNodeNeighborNumber > 1)
                {
                    doubleNodeLocalClusteringCoefficient = ((intNodeNeighborRelationshipNumber.doubleValue())/((intNodeNeighborNumber)*(intNodeNeighborNumber-1)))/2;
                }
                
                nodeStart.setProperty( "nodeLocalClusteringCoefficient", doubleNodeLocalClusteringCoefficient);

                //Debugging purpuses
                //System.out.println("nodeLocalClusteringCoefficient[" + nodeStart.getId() + "] :" + doubleNodeLocalClusteringCoefficient);
            }
        }
        catch (Exception e)
        {
            System.out.println("Node Local Clustering Coefficient error: " + e);
        }
        finally
        {
            transaction.success();
        }
        
        transaction.finish();
        graphDb.shutdown();
    } // END QueryNeo4jNodeLocalClusteringCoefficient
    
    public static void QueryNeo4jNodeEigenvectorCentralityPowerMethod()
    {
        
        Double doubleEigenVectorPrecision = 0.01; // Power Method Implementation
        
        GraphDatabaseService graphDb = new GraphDatabaseFactory().newEmbeddedDatabase( DB_PATH );
        Transaction transaction = graphDb.beginTx();
        
        ExecutionEngine engine = new ExecutionEngine( graphDb );
        ExecutionResult resultNode, resultRel;
        
        Double doubleNodeEigenVector = 0.0;
        EigenvectorCentralityPower eigenvectorCentralityPower;
        
        try 
        {
        
            String strNodeQuery =  "START n=node(*) " +
                                    "RETURN n AS n_obj " +
                                    "ORDER BY id(n_obj) ASC " ;
            resultNode = engine.execute(strNodeQuery);

            //System.out.println("Node Centrality Eccentricity Query:" + resultNode);

            Iterator<Node> n_object1 = resultNode.columnAs( "n_obj" );

            Set<Node> setNode = new HashSet<Node>() {};


            for ( Node nodeStart : IteratorUtil.asIterable( n_object1 ) )
            {
                setNode.add(nodeStart);
                //System.out.println("Node: " + nodeStart.getId());
            }

            String strRelQuery =  "MATCH (a)-[r]-(b)" +
                                    " RETURN DISTINCT (r) as r_obj" +
                                    " ORDER BY id(r_obj) ASC " ;
            resultRel = engine.execute(strRelQuery);

            //System.out.println("Node Centrality Eccentricity Query:" + resultNode);

            Iterator<Relationship> r_object1 = resultRel.columnAs( "r_obj" );

            Set<Relationship> setRel = new HashSet<Relationship>() {};

            for ( Relationship relStart : IteratorUtil.asIterable( r_object1 ) )
            {
                setRel.add(relStart);
                //System.out.println("Relationship: " + relStart.getId());
            }

            eigenvectorCentralityPower = new EigenvectorCentralityPower( Direction.BOTH, new CostEvaluator<Double>()
                 {
                     public Double getCost( Relationship relationship,
                                 Direction direction )
                     {
                         return 1.0;
                     }
                 }, setNode, setRel, doubleEigenVectorPrecision );

            eigenvectorCentralityPower.calculate();

            for (Node startNode:setNode)
            {

                doubleNodeEigenVector = 0.0;

                doubleNodeEigenVector = eigenvectorCentralityPower.getCentrality( startNode);
                startNode.setProperty( "nodeCentralityEigenVector", doubleNodeEigenVector);

                //System.out.print("eigenvectorCentrality of node: " + startNode.getId() + " is ");
                //System.out.println(doubleNodeEigenVector);

            }   
        }
        catch (Exception e)
        {
            System.out.println("Node Centrality EigenVector error: " + e);
        }
        finally
        {
            transaction.success();
        }
        
        transaction.finish();
        graphDb.shutdown();

    } // END QueryNeo4jNodeEigenVectorCentralityPowerMethod
    
} // END OutputNeo4j