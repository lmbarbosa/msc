/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package plosonexmlimport;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
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
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.PathExpanders;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.ResourceIterator;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.index.UniqueFactory;
import org.neo4j.helpers.collection.IteratorUtil;
import org.neo4j.kernel.Traversal;
import org.neo4j.kernel.impl.util.*;


public class OutputNeo4jAssortativity {
    
    private static final String DB_PATH = PlosOneXMLImport.getStrNeo4DBPath()+ "-Assortative";
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
            nodeArticle.setProperty( "filename", objArticle.getStrArticleFileName().trim());
            nodeArticle.setProperty( "id", objArticle.getStrArticleID().trim());
            nodeArticle.setProperty( "views", objArticle.getStrArticleViews().trim());
            nodeArticle.setProperty( "citations", objArticle.getStrArticleCitations().trim());
            nodeArticle.setProperty( "saves", objArticle.getStrArticleSaves().trim());
            nodeArticle.setProperty( "shares", objArticle.getStrArticleShares().trim());
            
            String strArticleReferences = objArticle.getStrArticleReferences();
            nodeArticle.setProperty( "references", strArticleReferences );
            String articlePubYear = objArticle.getStrArticlePublicationYear();
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
            
            for (int i=0; i<objArticle.getArrayListAuthor().size() ;i++)
            {    
                String strAuthorTmp = objArticle.getArrayListAuthor().get(i).trim();
                
                if (!strAuthorTmp.equals(""))
                {
                    //Node node  = graphDb.createNode();
                    Node nodeAuthor = factoryAuthor.getOrCreate( "author", strAuthorTmp );
                    nodeAuthorArray.add(nodeAuthor);

                    Label labelAuthor = DynamicLabel.label("author");
                    nodeAuthor.addLabel(labelAuthor);
                    nodeAuthor.setProperty( "name", strAuthorTmp);

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

            for (int i=0; i<objArticle.getArrayListCountry().size() ;i++)
            {    
                String strCountryTemp = objArticle.getArrayListCountry().get(i).trim();
                if (!strCountryTemp.equals("") && (!strCountryTemp.equals("null")))
                {    
                    Node nodeCountry = factoryCountry.getOrCreate( "country", strCountryTemp);
                    nodeCountryArray.add(nodeCountry);

                    Label labelCountry = DynamicLabel.label("country");
                    nodeCountry.addLabel(labelCountry);

                    String str_country_continent = CountryContinent.pgsqlQueryCountryContinent(strCountryTemp);
                    String str_country_alphacode3 = CountryContinent.pgsqlQueryCountryAlphaCode3(strCountryTemp);

                    nodeCountry.setProperty( "country_alphacode3", str_country_alphacode3);
                    nodeCountry.setProperty( "country_continent", str_country_continent);
                    

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
                       
            articlePubYear = objArticle.getStrArticlePublicationYear();
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
            
            for (int i=0; i<objArticle.getArrayListKeywords().size() ;i++)
            {    
                
                
                //Node node  = graphDb.createNode();
                String strArticleKeyword = objArticle.getArrayListKeywords().get(i).trim();
                
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
            
 
            
            // @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ RELATIONSHIP ARTICLE x ARTICLE @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ 
            //function QueryNeo4jArticleRelationship ()
            
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
            
                                  
            // @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ RELATIONSHIP ARTICLE x YEAR @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
            
            if (objArticle.getStrArticlePublicationYear() != "0")
            {
                if (QueryNeo4jCreateRelationshipValidation(nodeArticle, nodeYear, RelTypes.ARTICLE_YEAR ))
                {
                    relationship = nodeArticle.createRelationshipTo( nodeYear, RelTypes.ARTICLE_YEAR );
                    relationship.setProperty( "weight", 1 );
                    relationship.setProperty( "direction", "directional" );
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
            
            // @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ RELATIONSHIP AUTHOR x YEAR @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
                
            for (int i=0; i<nodeAuthorArray.size() ;i++)
            {   
                Node currentNode = nodeAuthorArray.get(i);

                if (QueryNeo4jCreateRelationshipValidation(currentNode, nodeYear, RelTypes.AUTHOR_YEAR ))
                {
                    relationship = currentNode.createRelationshipTo( nodeYear, RelTypes.AUTHOR_YEAR );
                    relationship.setProperty( "weight", 1 );
                    relationship.setProperty( "direction", "directional" );
                }
                else // else this relationship already exists
                {
                    relationship = QueryNeo4jGetRelationship(currentNode, nodeYear, RelTypes.AUTHOR_YEAR);
                    int objRelWeight = (int)relationship.getProperty("weight");
                    objRelWeight++;
                    relationship.setProperty( "weight", objRelWeight );
                }
            }
  
            
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
            
            // @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ RELATIONSHIP KEYWORD x YEAR @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
            
            for (int i=0; i<nodeKeywordArray.size() ;i++)
            {   
                Node currentNode = nodeKeywordArray.get(i);

                if (QueryNeo4jCreateRelationshipValidation(currentNode, nodeYear, RelTypes.KEYWORD_YEAR ))
                {
                    relationship = currentNode.createRelationshipTo( nodeYear, RelTypes.KEYWORD_YEAR );
                    relationship.setProperty( "weight", 1 );
                    relationship.setProperty( "direction", "directional" );
                }
                else // else this relationship already exists
                {
                    relationship = QueryNeo4jGetRelationship(currentNode, nodeYear, RelTypes.KEYWORD_YEAR);
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
            
             // @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ RELATIONSHIP COUNTRY x KEYWORD @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
            
            for (int i=0; i<nodeCountryArray.size() ;i++)
            {   
                Node currentNode = nodeCountryArray.get(i);
                for (int j=i; j<nodeKeywordArray.size() ;j++)
                {
                    Node nextNode = nodeKeywordArray.get(j);
                    if (QueryNeo4jCreateRelationshipValidation(currentNode, nextNode, RelTypes.COUNTRY_KEYWORD ))
                    {
                        relationship = currentNode.createRelationshipTo( nextNode, RelTypes.COUNTRY_KEYWORD );
                        relationship.setProperty( "weight", 1 );
                        relationship.setProperty( "direction", "directional" );
                    }
                    else // else this relationship already exists
                    {
                        relationship = QueryNeo4jGetRelationship(currentNode, nextNode, RelTypes.COUNTRY_KEYWORD);
                        int objRelWeight = (int)relationship.getProperty("weight");
                        objRelWeight++;
                        relationship.setProperty( "weight", objRelWeight );
                    }
                }
            }
            
            // @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ RELATIONSHIP COUNTRY x YEAR @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
            
            for (int i=0; i<nodeCountryArray.size() ;i++)
            {   
                Node currentNode = nodeCountryArray.get(i);

                if (QueryNeo4jCreateRelationshipValidation(currentNode, nodeYear, RelTypes.COUNTRY_YEAR ))
                {
                    relationship = currentNode.createRelationshipTo( nodeYear, RelTypes.COUNTRY_YEAR );
                    relationship.setProperty( "weight", 1 );
                    relationship.setProperty( "direction", "directional" );
                }
                else // else this relationship already exists
                {
                    relationship = QueryNeo4jGetRelationship(currentNode, nodeYear, RelTypes.COUNTRY_YEAR);
                    int objRelWeight = (int)relationship.getProperty("weight");
                    objRelWeight++;
                    relationship.setProperty( "weight", objRelWeight );
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
                            && nodeArticle.getId() != nodeArticle2.getId())
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
    

    
 
    public static void QueryNeo4jYearAssortativityDiscrete()
    {
        String nodeType = "year";
        
        GraphDatabaseService graphDb = new GraphDatabaseFactory().newEmbeddedDatabase( DB_PATH );
        Transaction transaction = graphDb.beginTx();
        
        ExecutionEngine engine = new ExecutionEngine( graphDb );
        ExecutionResult resultNode, resultRel;
        
        Integer intRowCounter = 0;
        Integer intColCounter = 0;
        Integer intTrCounter = 0;
               
        try 
        {

            String strNodeQuery =  "MATCH (n:`"+nodeType+"`) RETURN n as n_obj ORDER BY id(n)" ;
            resultNode = engine.execute(strNodeQuery);
            
            Iterator<Node> n_object = resultNode.columnAs( "n_obj" );
            
            Set<Node> setNode = new HashSet<Node>() {};
            
            for ( Node nodeStart : IteratorUtil.asIterable( n_object ) )
            {
                setNode.add(nodeStart);
            }
            Integer intMatrixSize = setNode.size();
            Double[][] doubleNodeMatrix = new Double[intMatrixSize][intMatrixSize];
            Double[] doubleRowVector = new Double[intMatrixSize];
            Double[] doubleColVector = new Double[intMatrixSize];
            Double[] doubleTrVector = new Double[intMatrixSize];
            
            Double doubleRowColTotal = 0.0;
            Double doubleTrTotal = 0.0;
            Double doubleR = 0.0;
            
            String strRelTotalQuery =  " MATCH (a)-[r:`ARTICLE_REFERENCEDBY_ARTICLE`]->(b) RETURN count(distinct r) as relcount " ;
            resultRel = engine.execute(strRelTotalQuery);
            Iterator<Long> itRelTotal = resultRel.columnAs( "relcount" );
            Integer intRelTotal = itRelTotal.next().intValue();
            
            
            for (int i=0; i<intMatrixSize; i++)
            {  
                doubleRowVector[i] = 0.0;
                doubleColVector[i] = 0.0;
                doubleTrVector[i] = 0.0;
            }
            
            for (Node node1:setNode)
            {     
                
               
                for (Node node2:setNode)
                {     

                    String strRelQuery =  " MATCH (a)-[r:`ARTICLE_REFERENCEDBY_ARTICLE`]->(b) WHERE a.year = '"+node1.getProperty("year")+"' AND b.year = '"+node2.getProperty("year")+"' RETURN count(r) as relcount " ;
                    resultRel = engine.execute(strRelQuery);
                    
                    
                    //System.out.println(strRelQuery);
                    
                    Iterator<Long> itRelCount = resultRel.columnAs( "relcount" );
                    Integer intRelCount = itRelCount.next().intValue();
                    System.out.println(node1.getProperty("year") + " x " + node2.getProperty("year") + ":" + intRelCount);
                    double doubleRelFraction = (intRelCount.doubleValue()/intRelTotal.doubleValue());
                    
                    doubleNodeMatrix[intRowCounter][intColCounter] = doubleRelFraction;
                    
                    doubleRowVector[intRowCounter] += doubleRelFraction;
                    doubleColVector[intColCounter] += doubleRelFraction;
                    
                    if(node1 == node2)
                    {
                        doubleTrVector[intTrCounter++] += doubleRelFraction;
                    }
                    
                    intColCounter++;
                }
                
                intRowCounter++;
                intColCounter = 0;
            }
            
            for (int i=0; i<intMatrixSize; i++)
            {
                doubleTrTotal += doubleTrVector[i];
                doubleRowColTotal += doubleRowVector[i]*doubleColVector[i];
            }
            
            doubleR = (doubleTrTotal - doubleRowColTotal) / (1.00 - doubleRowColTotal);
            
            System.out.println("Node Year Assortativity: " + doubleR);   
                
            /*
            for(int linha=0 ; linha < intMatrixSize ; linha++){
                System.out.println("doubleRowVector["+linha+"]" + doubleRowVector[linha]);
                System.out.println("doubleColVector["+linha+"]" + doubleColVector[linha]);
                System.out.println("doubleTrVector["+linha+"]" + doubleTrVector[linha]);
                for(int coluna = 0; coluna < intMatrixSize ; coluna ++){
                    //System.out.println("intNodeMatrix["+linha+"]["+coluna+"]: " + doubleNodeMatrix[linha][coluna]);
                }
                System.out.println();
            }
            */
            
        }
        catch (Exception e)
        {
            System.out.println("Node YearAssortativity error: " + e);
        }
        finally
        {
            transaction.success();
        }
        
        transaction.finish();
        graphDb.shutdown();
    }
    
    
    public static void QueryNeo4jYearKeywordArticleAssortativityDiscrete()
    {
        String nodeType = "year";
        
        GraphDatabaseService graphDb = new GraphDatabaseFactory().newEmbeddedDatabase( DB_PATH );
        Transaction transaction = graphDb.beginTx();
        
        ExecutionEngine engine = new ExecutionEngine( graphDb );
        ExecutionResult resultNode, resultRel;
        
        Integer intRowCounter = 0;
        Integer intColCounter = 0;
        Integer intTrCounter = 0;
               
        try 
        {

            String strNodeQuery =  "MATCH (n:`"+nodeType+"`) RETURN n as n_obj ORDER BY id(n)" ;
            resultNode = engine.execute(strNodeQuery);
            
            Iterator<Node> n_object = resultNode.columnAs( "n_obj" );
            
            Set<Node> setNode = new HashSet<Node>() {};
            
            for ( Node nodeStart : IteratorUtil.asIterable( n_object ) )
            {
                setNode.add(nodeStart);
            }
            Integer intMatrixSize = setNode.size();
            Double[][] doubleNodeMatrix = new Double[intMatrixSize][intMatrixSize];
            Double[] doubleRowVector = new Double[intMatrixSize];
            Double[] doubleColVector = new Double[intMatrixSize];
            Double[] doubleTrVector = new Double[intMatrixSize];
            
            Double doubleRowColTotal = 0.0;
            Double doubleTrTotal = 0.0;
            Double doubleR = 0.0;
            
            String strRelTotalQuery =  " MATCH (a1)-[r1:`ARTICLE_KEYWORD`]->(k)<-[r2:`ARTICLE_KEYWORD`]-(a2) WHERE a1 <> a2 RETURN SUM(r1.weight+r2.weight) as relcount " ;
            resultRel = engine.execute(strRelTotalQuery);
            Iterator<Integer> itRelTotal = resultRel.columnAs( "relcount" );
            Integer intRelTotal = itRelTotal.next().intValue();
            System.out.println("intRelTotal: " + intRelTotal);
            
            Integer intRelCountTest = 0;
            
            for (int i=0; i<intMatrixSize; i++)
            {  
                doubleRowVector[i] = 0.0;
                doubleColVector[i] = 0.0;
                doubleTrVector[i] = 0.0;
            }
            
            for (Node node1:setNode)
            {     
                
               
                for (Node node2:setNode)
                {     

                    String strRelQuery =  " MATCH (a1)-[r1:`ARTICLE_KEYWORD`]->(k)<-[r2:`ARTICLE_KEYWORD`]-(a2) WHERE a1 <> a2 AND a1.year = '"+node1.getProperty("year")+"' AND a2.year = '"+node2.getProperty("year")+"' RETURN SUM(r1.weight+r2.weight) as relcount " ;
                    resultRel = engine.execute(strRelQuery);
                    
                    
                    //System.out.println(strRelQuery);
                    
                    Iterator<Integer> itRelCount = resultRel.columnAs( "relcount" );
                    Integer intRelCount = itRelCount.next();
                    
                    intRelCountTest += intRelCount;
                    
                    System.out.println(node1.getProperty("year") + " x " + node2.getProperty("year") + ":" + intRelCount);
                    
                    double doubleRelFraction = (intRelCount.doubleValue()/intRelTotal.doubleValue());
                    
                    doubleNodeMatrix[intRowCounter][intColCounter] = doubleRelFraction;
                    
                    doubleRowVector[intRowCounter] += doubleRelFraction;
                    doubleColVector[intColCounter] += doubleRelFraction;
                    
                    if(node1 == node2)
                    {
                        doubleTrVector[intTrCounter++] += doubleRelFraction;
                    }
                    
                    intColCounter++;
                    
                }
                
                intRowCounter++;
                intColCounter = 0;
            }
            
            for (int i=0; i<intMatrixSize; i++)
            {
                doubleTrTotal += doubleTrVector[i];
                doubleRowColTotal += doubleRowVector[i]*doubleColVector[i];
            }
            System.out.println("intRelCountTest: " + intRelCountTest);
            doubleR = (doubleTrTotal - doubleRowColTotal) / (1.00 - doubleRowColTotal);
            
            System.out.println("Node Year Keyword Assortativity: " + doubleR);   
                
            /*
            for(int linha=0 ; linha < intMatrixSize ; linha++){
                System.out.println("doubleRowVector["+linha+"]" + doubleRowVector[linha]);
                System.out.println("doubleColVector["+linha+"]" + doubleColVector[linha]);
                System.out.println("doubleTrVector["+linha+"]" + doubleTrVector[linha]);
                for(int coluna = 0; coluna < intMatrixSize ; coluna ++){
                    //System.out.println("intNodeMatrix["+linha+"]["+coluna+"]: " + doubleNodeMatrix[linha][coluna]);
                }
                System.out.println();
            }
            */
            
        }
        catch (Exception e)
        {
            System.out.println("Node YearAssortativity error: " + e);
        }
        finally
        {
            transaction.success();
        }
        
        transaction.finish();
        graphDb.shutdown();
    }
    
    
    public static void QueryNeo4jContinentCountryAssortativityDiscrete()
    {

        String relType = "COUNTRY_COUNTRY";
        
        GraphDatabaseService graphDb = new GraphDatabaseFactory().newEmbeddedDatabase( DB_PATH );
        Transaction transaction = graphDb.beginTx();
        
        ExecutionEngine engine = new ExecutionEngine( graphDb );
        ExecutionResult resultNode, resultRel;
        
        Integer intRowCounter = 0;
        Integer intColCounter = 0;
        Integer intTrCounter = 0;
               
        try 
        {

            String strNodeQuery =  "MATCH (a)-[:`"+relType+"`]-(b) RETURN DISTINCT b.country_continent as cc ORDER BY cc" ;
            resultNode = engine.execute(strNodeQuery);
            
            Iterator<String> itCountryContinent = resultNode.columnAs( "cc" );
            
            Set<String> setCountryContinent = new HashSet<String>() {};
            
            for ( String strContinent : IteratorUtil.asIterable( itCountryContinent ) )
            {
                setCountryContinent.add(strContinent);
            }
            
            Integer intMatrixSize = setCountryContinent.size();
            Double[][] doubleNodeMatrix = new Double[intMatrixSize][intMatrixSize];
            Double[] doubleRowVector = new Double[intMatrixSize];
            Double[] doubleColVector = new Double[intMatrixSize];
            Double[] doubleTrVector = new Double[intMatrixSize];
            
            Double doubleRowColTotal = 0.0;
            Double doubleTrTotal = 0.0;
            Double doubleR = 0.0;
            
            String strRelTotalQuery =  " MATCH (a)-[r:`"+relType+"`]-(b) RETURN SUM(r.weight) as relcount " ;
            resultRel = engine.execute(strRelTotalQuery);
            Iterator<Integer> itRelTotal = resultRel.columnAs( "relcount" );
            Integer intRelTotal = itRelTotal.next().intValue();
            
            System.out.println("intRelTotal: " + intRelTotal);
            
            for (int i=0; i<intMatrixSize; i++)
            {  
                doubleRowVector[i] = 0.0;
                doubleColVector[i] = 0.0;
                doubleTrVector[i] = 0.0;
            }
            
            for (String strContinent1:setCountryContinent)
            {     
                
               
                for (String strContinent2:setCountryContinent)
                {     

                    String strRelQuery =  " MATCH (a)-[r:`"+relType+"`]-(b) WHERE a.country_continent = '"+strContinent1+"' AND b.country_continent = '"+strContinent2+"' RETURN SUM(r.weight) as relcount " ;
                    resultRel = engine.execute(strRelQuery);
                    
                    //System.out.println(strRelQuery);
                    
                    Iterator<Integer> itRelCount = resultRel.columnAs( "relcount" );
                    Integer intRelCount = itRelCount.next().intValue();
                    
                    System.out.println(strContinent1 + " x " + strContinent2 + " : " + intRelCount);
                    
                    double doubleRelFraction = (intRelCount.doubleValue()/intRelTotal.doubleValue());
                    
                    doubleNodeMatrix[intRowCounter][intColCounter] = doubleRelFraction;
                    
                    doubleRowVector[intRowCounter] += doubleRelFraction;
                    doubleColVector[intColCounter] += doubleRelFraction;
                    
                    if(strContinent1 == strContinent2)
                    {
                        doubleTrVector[intTrCounter++] += doubleRelFraction;
                    }
                    
                    intColCounter++;
                }
                
                intRowCounter++;
                intColCounter = 0;
            }
            
            for (int i=0; i<intMatrixSize; i++)
            {
                doubleTrTotal += doubleTrVector[i];
                doubleRowColTotal += doubleRowVector[i]*doubleColVector[i];
            }
            
            doubleR = (doubleTrTotal - doubleRowColTotal) / (1.00 - doubleRowColTotal);
            
            System.out.println("Node Continent Country Assortativity: " + doubleR);   
                
            /*
            for(int linha=0 ; linha < intMatrixSize ; linha++){
                System.out.println("doubleRowVector["+linha+"]" + doubleRowVector[linha]);
                System.out.println("doubleColVector["+linha+"]" + doubleColVector[linha]);
                System.out.println("doubleTrVector["+linha+"]" + doubleTrVector[linha]);
                for(int coluna = 0; coluna < intMatrixSize ; coluna ++){
                    System.out.println("intNodeMatrix["+linha+"]["+coluna+"]: " + doubleNodeMatrix[linha][coluna]);
                }
                System.out.println();
            }
            */
            
        }
        catch (Exception e)
        {
            System.out.println("Node Continent Country Assortativity error: " + e);
        }
        finally
        {
            transaction.success();
        }
        
        transaction.finish();
        graphDb.shutdown();
    }
    
    
    // INFORMATION FLOW
    public static void QueryNeo4jContinentCitationAssortativityDiscrete()
    {


        GraphDatabaseService graphDb = new GraphDatabaseFactory().newEmbeddedDatabase( DB_PATH );
        Transaction transaction = graphDb.beginTx();
        
        ExecutionEngine engine = new ExecutionEngine( graphDb );
        ExecutionResult resultNode, resultRel;
        
        Integer intRowCounter = 0;
        Integer intColCounter = 0;
        Integer intTrCounter = 0;
               
        try 
        {

            String strNodeQuery =  "MATCH (c1)<-[relArticleCountry:`ARTICLE_COUNTRY`]-(a1)-[relArticleArticle:`ARTICLE_REFERENCEDBY_ARTICLE`]->(a2)-[relArticleCountry2:`ARTICLE_COUNTRY`]->(c2) return DISTINCT c1.country_continent as cc ORDER BY cc ASC" ;
            resultNode = engine.execute(strNodeQuery);
            
            Iterator<String> itCountryContinent = resultNode.columnAs( "cc" );
            
            Set<String> setCountryContinent = new HashSet<String>() {};
            
            for ( String strContinent : IteratorUtil.asIterable( itCountryContinent ) )
            {
                setCountryContinent.add(strContinent);
            }
            
            Integer intMatrixSize = setCountryContinent.size();
            Double[][] doubleNodeMatrix = new Double[intMatrixSize][intMatrixSize];
            Double[] doubleRowVector = new Double[intMatrixSize];
            Double[] doubleColVector = new Double[intMatrixSize];
            Double[] doubleTrVector = new Double[intMatrixSize];
            
            Double doubleRowColTotal = 0.0;
            Double doubleTrTotal = 0.0;
            Double doubleR = 0.0;
            
            String strRelTotalQuery =  " MATCH (c1)<-[relArticleCountry:`ARTICLE_COUNTRY`]-(a1)-[relArticleArticle:`ARTICLE_REFERENCEDBY_ARTICLE`]->(a2)-[relArticleCountry2:`ARTICLE_COUNTRY`]->(c2) return count(relArticleArticle) as relcount " ;
            resultRel = engine.execute(strRelTotalQuery);
            Iterator<Long> itRelTotal = resultRel.columnAs( "relcount" );
            Integer intRelTotal = itRelTotal.next().intValue();
            
            Integer intRelCountTest = 0;
            
            System.out.println("intRelTotal: " + intRelTotal);
            
            for (int i=0; i<intMatrixSize; i++)
            {  
                doubleRowVector[i] = 0.0;
                doubleColVector[i] = 0.0;
                doubleTrVector[i] = 0.0;
            }
            
            for (String strContinent1:setCountryContinent)
            {     
                
               
                for (String strContinent2:setCountryContinent)
                {     

                    String strRelQuery =  "MATCH (c1)<-[relArticleCountry:`ARTICLE_COUNTRY`]-(a1)-[relArticleArticle:`ARTICLE_REFERENCEDBY_ARTICLE`]->(a2)-[relArticleCountry2:`ARTICLE_COUNTRY`]->(c2) WHERE c1.country_continent = '"+strContinent1+"' and c2.country_continent = '"+strContinent2+"' return count (relArticleArticle) as relcount " ;
                    resultRel = engine.execute(strRelQuery);
                    
                    //System.out.println(strRelQuery);
                    
                    Iterator<Long> itRelCount = resultRel.columnAs( "relcount" );
                    Integer intRelCount = itRelCount.next().intValue();
                    
                    System.out.println(strContinent1 + " x " + strContinent2 + " : " + intRelCount);
                    //System.out.println("intRelCount: " + intRelCount);
                    intRelCountTest += intRelCount; 
                   
                    double doubleRelFraction = (intRelCount.doubleValue()/intRelTotal.doubleValue());
                    
                    doubleNodeMatrix[intRowCounter][intColCounter] = doubleRelFraction;
                    
                    doubleRowVector[intRowCounter] += doubleRelFraction;
                    doubleColVector[intColCounter] += doubleRelFraction;
                    
                    if(strContinent1 == strContinent2)
                    {
                        doubleTrVector[intTrCounter++] += doubleRelFraction;
                    }
                    
                    intColCounter++;
                }
                
                intRowCounter++;
                intColCounter = 0;
            }
            
            //System.out.println("teste: " + teste);
            
            for (int i=0; i<intMatrixSize; i++)
            {
                doubleTrTotal += doubleTrVector[i];
                doubleRowColTotal += doubleRowVector[i]*doubleColVector[i];
            }
            
            doubleR = (doubleTrTotal - doubleRowColTotal) / (1.00 - doubleRowColTotal);
            
            System.out.println("Node Continent Citation Assortativity: " + doubleR);   
                
            /*
            for(int linha=0 ; linha < intMatrixSize ; linha++){
                System.out.println("doubleRowVector["+linha+"]" + doubleRowVector[linha]);
                System.out.println("doubleColVector["+linha+"]" + doubleColVector[linha]);
                System.out.println("doubleTrVector["+linha+"]" + doubleTrVector[linha]);
                for(int coluna = 0; coluna < intMatrixSize ; coluna ++){
                    System.out.println("intNodeMatrix["+linha+"]["+coluna+"]: " + doubleNodeMatrix[linha][coluna]);
                }
                System.out.println();
            }
            */
            
        }
        catch (Exception e)
        {
            System.out.println("Node Continent Citation Assortativity error: " + e);
        }
        finally
        {
            transaction.success();
        }
        
        transaction.finish();
        graphDb.shutdown();
    }
        
    
     
    // SUBJECT SINTONY or KEYWORD SINTONY    
    public static void QueryNeo4jContinentKeywordAssortativityDiscrete()
    {
        GraphDatabaseService graphDb = new GraphDatabaseFactory().newEmbeddedDatabase( DB_PATH );
        Transaction transaction = graphDb.beginTx();
        
        ExecutionEngine engine = new ExecutionEngine( graphDb );
        ExecutionResult resultNode, resultRel;
        
        Integer intRowCounter = 0;
        Integer intColCounter = 0;
        Integer intTrCounter = 0;
               
        try 
        {

            String strNodeQuery =  "MATCH (c1)-[relCountry:`COUNTRY_KEYWORD`]->(k1)<-[relCountry2:`COUNTRY_KEYWORD`]-(c2) WHERE c1 <> c2 RETURN DISTINCT c1.country_continent as cc ORDER BY cc" ;
            resultNode = engine.execute(strNodeQuery);
            
            Iterator<String> itKeywordContinent = resultNode.columnAs( "cc" );
            
            Set<String> setKeywordContinent = new HashSet<String>() {};
            
            for ( String strKeyword : IteratorUtil.asIterable( itKeywordContinent ) )
            {
                setKeywordContinent.add(strKeyword);
            }
            
            Integer intMatrixSize = setKeywordContinent.size();
            Double[][] doubleNodeMatrix = new Double[intMatrixSize][intMatrixSize];
            Double[] doubleRowVector = new Double[intMatrixSize];
            Double[] doubleColVector = new Double[intMatrixSize];
            Double[] doubleTrVector = new Double[intMatrixSize];
            
            Double doubleRowColTotal = 0.0;
            Double doubleTrTotal = 0.0;
            Double doubleR = 0.0;
            
            String strRelTotalQuery =  "MATCH (c1)-[relKeyword1:`COUNTRY_KEYWORD`]->(k1)<-[relKeyword2:`COUNTRY_KEYWORD`]-(c2) WHERE c1 <> c2 RETURN (SUM(relKeyword1.weight)+SUM(relKeyword2.weight)) as relcount " ;
            //String strRelTotalQuery =  "MATCH (c1)-[relCountry:`COUNTRY_KEYWORD`]->(k1)-[relKeyword:`KEYWORD_KEYWORD`]-(k2)<-[relCountry2:`COUNTRY_KEYWORD`]-(c2) RETURN count(relKeyword) as relcount " ;
            resultRel = engine.execute(strRelTotalQuery);
            Iterator<Integer> itRelTotal = resultRel.columnAs( "relcount" );
            //Iterator<Long> itRelTotal = resultRel.columnAs( "relcount" );
            Integer intRelTotal = itRelTotal.next().intValue();
            
            Integer intRelCountTest = 0;
            
            System.out.println("intRelTotal: " + intRelTotal);
            
            for (int i=0; i<intMatrixSize; i++)
            {  
                doubleRowVector[i] = 0.0;
                doubleColVector[i] = 0.0;
                doubleTrVector[i] = 0.0;
            }
            
            for (String strContinent1:setKeywordContinent)
            {     
                
               
                for (String strContinent2:setKeywordContinent)
                {     

                    String strRelQuery =  "MATCH (c1)-[relKeyword1:`COUNTRY_KEYWORD`]->(k1)<-[relKeyword2:`COUNTRY_KEYWORD`]-(c2) WHERE c1 <> c2 AND c1.country_continent = '"+strContinent1+"' and c2.country_continent = '"+strContinent2+"' RETURN (SUM(relKeyword1.weight)+SUM(relKeyword2.weight)) as relcount " ;
                    //String strRelQuery =  "MATCH (c1)-[relCountry:`COUNTRY_KEYWORD`]->(k1)-[relKeyword:`KEYWORD_KEYWORD`]-(k2)<-[relCountry2:`COUNTRY_KEYWORD`]-(c2) WHERE c1.country_continent = '"+strContinent1+"' and c2.country_continent = '"+strContinent2+"' RETURN COUNT(relKeyword) as relcount " ;
                    resultRel = engine.execute(strRelQuery);
                    
                    //System.out.println(strRelQuery);
                    
                    
                    
                    Iterator<Integer> itRelCount = resultRel.columnAs( "relcount" );
                    //Iterator<Long> itRelCount = resultRel.columnAs( "relcount" );
                    Integer intRelCount = itRelCount.next().intValue();
                    //System.out.println("intRelCount: " + intRelCount);
                    
                    System.out.println(strContinent1 + " x " + strContinent2 + ":" + intRelCount);
                    intRelCountTest += intRelCount; 
                   
                    double doubleRelFraction = (intRelCount.doubleValue()/intRelTotal.doubleValue());
                    
                    doubleNodeMatrix[intRowCounter][intColCounter] = doubleRelFraction;
                    
                    doubleRowVector[intRowCounter] += doubleRelFraction;
                    doubleColVector[intColCounter] += doubleRelFraction;
                    
                    if(strContinent1 == strContinent2)
                    {
                        doubleTrVector[intTrCounter++] += doubleRelFraction;
                    }
                    
                    intColCounter++;
                }
                
                intRowCounter++;
                intColCounter = 0;
            }
            
            System.out.println("intRelCountTest: " + intRelCountTest);
            
            for (int i=0; i<intMatrixSize; i++)
            {
                doubleTrTotal += doubleTrVector[i];
                doubleRowColTotal += doubleRowVector[i]*doubleColVector[i];
            }
            
            doubleR = (doubleTrTotal - doubleRowColTotal) / (1.00 - doubleRowColTotal);
            
            System.out.println("Node Continent Keyword Assortativity: " + doubleR);   
                
            /*
            for(int linha=0 ; linha < intMatrixSize ; linha++){
                System.out.println("doubleRowVector["+linha+"]" + doubleRowVector[linha]);
                System.out.println("doubleColVector["+linha+"]" + doubleColVector[linha]);
                System.out.println("doubleTrVector["+linha+"]" + doubleTrVector[linha]);
                for(int coluna = 0; coluna < intMatrixSize ; coluna ++){
                    System.out.println("intNodeMatrix["+linha+"]["+coluna+"]: " + doubleNodeMatrix[linha][coluna]);
                }
                System.out.println();
            }
            
            */
        }
        catch (Exception e)
        {
            System.out.println("Node Continent Keyword Assortativity error: " + e);
        }
        finally
        {
            transaction.success();
        }
        
        transaction.finish();
        graphDb.shutdown();
    }
    
    /*
    
    
     
    // SUBJECT SINTONY or KEYWORD SINTONY    
    public static void QueryNeo4jContinentKeywordAssortativityDiscrete()
    {
        GraphDatabaseService graphDb = new GraphDatabaseFactory().newEmbeddedDatabase( DB_PATH );
        Transaction transaction = graphDb.beginTx();
        
        ExecutionEngine engine = new ExecutionEngine( graphDb );
        ExecutionResult resultNode, resultRel;
        
        Integer intRowCounter = 0;
        Integer intColCounter = 0;
        Integer intTrCounter = 0;
               
        try 
        {

            String strNodeQuery =  "MATCH (c1)-[relCountry:`COUNTRY_KEYWORD`]->(k1)-[relKeyword:`KEYWORD_KEYWORD`]-(k2)<-[relCountry2:`COUNTRY_KEYWORD`]-(c2) return DISTINCT c1.country_continent as cc ORDER BY cc" ;
            resultNode = engine.execute(strNodeQuery);
            
            Iterator<String> itKeywordContinent = resultNode.columnAs( "cc" );
            
            Set<String> setKeywordContinent = new HashSet<String>() {};
            
            for ( String strKeyword : IteratorUtil.asIterable( itKeywordContinent ) )
            {
                setKeywordContinent.add(strKeyword);
            }
            
            Integer intMatrixSize = setKeywordContinent.size();
            Double[][] doubleNodeMatrix = new Double[intMatrixSize][intMatrixSize];
            Double[] doubleRowVector = new Double[intMatrixSize];
            Double[] doubleColVector = new Double[intMatrixSize];
            Double[] doubleTrVector = new Double[intMatrixSize];
            
            Double doubleRowColTotal = 0.0;
            Double doubleTrTotal = 0.0;
            Double doubleR = 0.0;
            
            String strRelTotalQuery =  "MATCH (c1)-[relCountry:`COUNTRY_KEYWORD`]->(k1)-[relKeyword:`KEYWORD_KEYWORD`]-(k2)<-[relCountry2:`COUNTRY_KEYWORD`]-(c2) RETURN SUM(relKeyword.weight) as relcount " ;
            //String strRelTotalQuery =  "MATCH (c1)-[relCountry:`COUNTRY_KEYWORD`]->(k1)-[relKeyword:`KEYWORD_KEYWORD`]-(k2)<-[relCountry2:`COUNTRY_KEYWORD`]-(c2) RETURN count(relKeyword) as relcount " ;
            resultRel = engine.execute(strRelTotalQuery);
            Iterator<Integer> itRelTotal = resultRel.columnAs( "relcount" );
            //Iterator<Long> itRelTotal = resultRel.columnAs( "relcount" );
            Integer intRelTotal = itRelTotal.next().intValue();
            
            Integer intRelCountTest = 0;
            
            //System.out.println("intRelTotal: " + intRelTotal);
            
            for (int i=0; i<intMatrixSize; i++)
            {  
                doubleRowVector[i] = 0.0;
                doubleColVector[i] = 0.0;
                doubleTrVector[i] = 0.0;
            }
            
            for (String strContinent1:setKeywordContinent)
            {     
                
               
                for (String strContinent2:setKeywordContinent)
                {     

                    String strRelQuery =  "MATCH (c1)-[relCountry:`COUNTRY_KEYWORD`]->(k1)-[relKeyword:`KEYWORD_KEYWORD`]-(k2)<-[relCountry2:`COUNTRY_KEYWORD`]-(c2) WHERE c1.country_continent = '"+strContinent1+"' and c2.country_continent = '"+strContinent2+"' RETURN SUM(relKeyword.weight) as relcount " ;
                    //String strRelQuery =  "MATCH (c1)-[relCountry:`COUNTRY_KEYWORD`]->(k1)-[relKeyword:`KEYWORD_KEYWORD`]-(k2)<-[relCountry2:`COUNTRY_KEYWORD`]-(c2) WHERE c1.country_continent = '"+strContinent1+"' and c2.country_continent = '"+strContinent2+"' RETURN COUNT(relKeyword) as relcount " ;
                    resultRel = engine.execute(strRelQuery);
                    
                    //System.out.println(strRelQuery);
                    
                    
                    
                    Iterator<Integer> itRelCount = resultRel.columnAs( "relcount" );
                    //Iterator<Long> itRelCount = resultRel.columnAs( "relcount" );
                    Integer intRelCount = itRelCount.next().intValue();
                    //System.out.println("intRelCount: " + intRelCount);
                    
                    System.out.println(strContinent1 + " x " + strContinent2 + ":" + intRelCount);
                    intRelCountTest += intRelCount; 
                   
                    double doubleRelFraction = (intRelCount.doubleValue()/intRelTotal.doubleValue());
                    
                    doubleNodeMatrix[intRowCounter][intColCounter] = doubleRelFraction;
                    
                    doubleRowVector[intRowCounter] += doubleRelFraction;
                    doubleColVector[intColCounter] += doubleRelFraction;
                    
                    if(strContinent1 == strContinent2)
                    {
                        doubleTrVector[intTrCounter++] += doubleRelFraction;
                    }
                    
                    intColCounter++;
                }
                
                intRowCounter++;
                intColCounter = 0;
            }
            
            //System.out.println("intRelCountTest: " + intRelCountTest);
            
            for (int i=0; i<intMatrixSize; i++)
            {
                doubleTrTotal += doubleTrVector[i];
                doubleRowColTotal += doubleRowVector[i]*doubleColVector[i];
            }
            
            doubleR = (doubleTrTotal - doubleRowColTotal) / (1.00 - doubleRowColTotal);
            
            System.out.println("Node Continent Keyword Assortativity: " + doubleR);   
                
            
            for(int linha=0 ; linha < intMatrixSize ; linha++){
                System.out.println("doubleRowVector["+linha+"]" + doubleRowVector[linha]);
                System.out.println("doubleColVector["+linha+"]" + doubleColVector[linha]);
                System.out.println("doubleTrVector["+linha+"]" + doubleTrVector[linha]);
                for(int coluna = 0; coluna < intMatrixSize ; coluna ++){
                    System.out.println("intNodeMatrix["+linha+"]["+coluna+"]: " + doubleNodeMatrix[linha][coluna]);
                }
                System.out.println();
            }
            
            
        }
        catch (Exception e)
        {
            System.out.println("Node Continent Keyword Assortativity error: " + e);
        }
        finally
        {
            transaction.success();
        }
        
        transaction.finish();
        graphDb.shutdown();
    }
    */
    
    public static void QueryNeo4jDegreeAssortativityUndirected(String relName)
    {
        String strDirection = "BOTH";
        GraphDatabaseService graphDb = new GraphDatabaseFactory().newEmbeddedDatabase( DB_PATH );
        Transaction transaction = graphDb.beginTx();
        
        ExecutionEngine engine = new ExecutionEngine( graphDb );
        ExecutionResult resultRel;
        
        Double rdegree = 0.0;
        
        try 
        {
            String strRelTotalQuery =  " MATCH (a)-[r:`"+relName+"`]-(b) RETURN r as rel " ;
            resultRel = engine.execute(strRelTotalQuery);
            
            Iterator<Relationship> itRel = resultRel.columnAs( "rel" );
            Set<Relationship> setRel = new HashSet<Relationship>() {};
            
            for ( Relationship relStart : IteratorUtil.asIterable( itRel ) )
            {
                setRel.add(relStart);
            }

            Integer intRelTotal = setRel.size();
            
            System.out.println("intRelTotal ["+relName+"]: "  + intRelTotal);     
                        
            double num1  = 0 , num2 = 0 , den = 0; 
            for (Relationship rel : setRel)
            {
                Node startNode = rel.getStartNode();
                Node endNode = rel.getEndNode();
                Integer intStartNodeDegree = QueryNeo4jSingleNodeCentralityDegree (engine, startNode, rel, strDirection);
                Integer intEndNodeDegree = QueryNeo4jSingleNodeCentralityDegree (engine, endNode, rel, strDirection);

                //System.out.println("Node intStartNodeDegree["+startNode.getId()+"]: " + intStartNodeDegree);  
                //System.out.println("Node intEndNodeDegree["+endNode.getId()+"]:: " + intEndNodeDegree);  

                num1 += intStartNodeDegree*intEndNodeDegree;
                num2 += (intStartNodeDegree + intEndNodeDegree);

                den += ((intStartNodeDegree*intStartNodeDegree)+(intEndNodeDegree*intEndNodeDegree));                   
            }
            
            num1 = num1 / intRelTotal;
            num2 = (num2/(2 * intRelTotal)) * (num2/(2 * intRelTotal));
            den = den / (2 * intRelTotal);
                    
            rdegree = (num1-num2)/(den-num2);
            
            System.out.println("Node Degree Assortativity (Undirected) ["+relName+"]: "  + rdegree);                      
            
        }
        catch (Exception e)
        {
            System.out.println("Node DegreeAssortativity (Undirected) error: " + e);
        }
        finally
        {
            transaction.success();
        }
        
        transaction.finish();
        graphDb.shutdown();
    
    } // END QueryNeo4jDegreeAssortativityUndirected
    
    public static void QueryNeo4jDegreeAssortativityDirected(String relName)
    {
        
        GraphDatabaseService graphDb = new GraphDatabaseFactory().newEmbeddedDatabase( DB_PATH );
        Transaction transaction = graphDb.beginTx();
        
        ExecutionEngine engine = new ExecutionEngine( graphDb );
        ExecutionResult resultRel;
        
        Double rdegree = 0.0;
        
        try 
        {
            String strRelTotalQuery =  " MATCH (a)-[r:`"+relName+"`]-(b) RETURN r as rel " ;
            resultRel = engine.execute(strRelTotalQuery);
            
            Iterator<Relationship> itRel = resultRel.columnAs( "rel" );
            Set<Relationship> setRel = new HashSet<Relationship>() {};
            
            for ( Relationship relStart : IteratorUtil.asIterable( itRel ) )
            {
                setRel.add(relStart);
            }

            Integer intRelTotal = setRel.size();
            
            System.out.println("intRelTotal ["+relName+"]: "  + intRelTotal);   
                        
            double num1  = 0 , num2 = 0 , den = 0; 
            for (Relationship rel : setRel)
            {
                Node startNode = rel.getStartNode();
                Node endNode = rel.getEndNode();
                Integer intStartNodeDegree = QueryNeo4jSingleNodeCentralityDegree (engine, startNode, rel, "IN");
                Integer intEndNodeDegree = QueryNeo4jSingleNodeCentralityDegree (engine, endNode, rel, "OUT");

                //System.out.println("Node intStartNodeDegree: " + intStartNodeDegree);  
                //System.out.println("Node intEndNodeDegree: " + intEndNodeDegree);  

                num1 += intStartNodeDegree*intEndNodeDegree;
                num2 += (intStartNodeDegree + intEndNodeDegree);

                den += ((intStartNodeDegree*intStartNodeDegree)+(intEndNodeDegree*intEndNodeDegree));                   
            }
            
            num1 = num1 / intRelTotal;
            num2 = (num2/(intRelTotal)) * (num2/(intRelTotal));
            den = (den / ( intRelTotal));
                    
            rdegree = (num1-num2)/(den-num2);
            
            System.out.println("Node Degree Assortativity (Directed) ["+relName+"]: "  + rdegree);                      
            
        }
        catch (Exception e)
        {
            System.out.println("Node DegreeAssortativity (Directed) error: " + e);
        }
        finally
        {
            transaction.success();
        }
        
        transaction.finish();
        graphDb.shutdown();
    
    } // END QueryNeo4jDegreeAssortativityUndirected
    
    public static Integer QueryNeo4jSingleNodeCentralityDegree (ExecutionEngine engine, Node n, Relationship r, String strDirection)
    {

        ExecutionResult resultNode;
        
        Integer intRCount = 0;
               
        try 
        {
            String strNodeQuery = "";
            if (strDirection.equalsIgnoreCase("IN"))
                strNodeQuery =  "MATCH (n)<-[r:`"+r.getType().name()+"`]-(m) WHERE id(n) = "+n.getId()+" RETURN count(r) as rcount " ;
            else if (strDirection.equalsIgnoreCase("OUT"))
                strNodeQuery =  "MATCH (n)-[r:`"+r.getType().name()+"`]->(m) WHERE id(n) = "+n.getId()+" RETURN count(r) as rcount " ;
            else
                strNodeQuery =  "MATCH (n)-[r:`"+r.getType().name()+"`]-(m) WHERE id(n) = "+n.getId()+" RETURN count(r) as rcount " ;
            resultNode = engine.execute(strNodeQuery);
             
            //System.out.println(strNodeQuery);
             
            Iterator<Long> itRCount = resultNode.columnAs( "rcount" );
            intRCount = itRCount.next().intValue();
            //System.out.println("intRCount: " + intRCount);
            
            // END SNIPPET: items
        }
        catch (Exception e)
        {
            System.out.println("Single Node Centrality Degree error: " + e);
        }      
        
        return intRCount;
    } // END QueryNeo4jSingleNodeCentralityDegree
    
} // END OutputNeo4jAssortativity