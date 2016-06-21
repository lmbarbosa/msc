/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ArticleExtraction;

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
import org.neo4j.graphalgo.impl.centrality.BetweennessCentrality;
import org.neo4j.graphalgo.impl.centrality.ClosenessCentrality;
import org.neo4j.graphalgo.impl.centrality.CostDivider;
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
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.ResourceIterator;
import org.neo4j.graphdb.index.UniqueFactory;
import org.neo4j.kernel.Traversal;


public class OutputNeo4j_DirectInput {
    
    private static final String DB_PATH = "C:\\Users\\leo\\Documents\\Neo4j\\academic_documents_openflow";
    static String resultString;
    static String columnsString;
    static String nodeResult;
    static String nodeResult2;
    static String rows = "";
    
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
    
    public static void QueryNeo4j (ClassArticleData objArticle)
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
            
            Node nodeArticle = factoryArticle.getOrCreate( "article", objArticle.getDocExtractTitle() );
            
            Label labelArticle = DynamicLabel.label("article");
            nodeArticle.addLabel(labelArticle);

            nodeArticle.setProperty( "title", objArticle.getDocExtractTitle().trim());
            nodeArticle.setProperty( "filename", objArticle.getInputDocFile().getName().trim());
            
            nodeArticle.setProperty( "email", objArticle.getDocExtractEmail().trim());
            
            String dateCreationArticleDate = "null";
            if (objArticle.getDocMetaCreationDate() != null)
                dateCreationArticleDate = objArticle.getDocMetaCreationDate().getTime().toLocaleString().toString();
           
            //nodeArticle.setProperty( "creation_date", dateCreationArticleDate);
            nodeArticle.setProperty( "pages_number", objArticle.getDocMetaPageCount());
            //nodeArticle.setProperty( "producer", objArticle.getDocMetaProducer());
            //nodeArticle.setProperty( "creator", objArticle.getDocMetaCreator());
            String strArticleReferences = UtilFunctions.stringNormalizeFunction(objArticle.getDocExtractReferences());
            nodeArticle.setProperty( "references", strArticleReferences );
            
            nodeArticle.setProperty( "nodeCentralityDegree", 0);
            nodeArticle.setProperty( "nodeCentralityCloseness", 0);
            nodeArticle.setProperty( "nodeCentralityBetweeness", 0);
            nodeArticle.setProperty( "nodeLocalClusteringCoefficient", 0);
            nodeArticle.setProperty( "isValid", "?");
            

            // *************************************  NODE AUTHOR *************************************
            
            ArrayList<Node> nodeAuthorArray = new ArrayList<>() ;
            
            for (int i=0; i<objArticle.getAuthorsArray().size() ;i++)
            {    
                
                //Node node  = graphDb.createNode();
                Node nodeAuthor = factoryAuthor.getOrCreate( "author", objArticle.getAuthorsArray().get(i) );
                nodeAuthorArray.add(nodeAuthor);
                
                Label labelAuthor = DynamicLabel.label("author");
                nodeAuthor.addLabel(labelAuthor);
                nodeAuthor.setProperty( "name", objArticle.getAuthorsArray().get(i).trim());
                nodeAuthor.setProperty( "name_abbreviated", UtilFunctions.stringShorteningNames(objArticle.getAuthorsArray().get(i).trim()));
                
                nodeAuthor.setProperty( "nodeCentralityDegree", 0);
                nodeAuthor.setProperty( "nodeCentralityCloseness", 0);
                nodeAuthor.setProperty( "nodeCentralityBetweeness", 0);
                nodeAuthor.setProperty( "nodeLocalClusteringCoefficient", 0);
                nodeAuthor.setProperty( "isValid", "?");
                
                //System.out.println(objArticle.getAuthorsArray().get(i));
            }    
            
            // *************************************  NODE COUNTRY *************************************
            
            ArrayList<Node> nodeCountryArray = new ArrayList<>() ;
            
            for (int i=0; i<objArticle.getCountryArray().size() ;i++)
            {    
                
                Node nodeCountry = factoryCountry.getOrCreate( "country", mainCountryIsoDBArray.get(objArticle.getCountryArray().get(i)).getCountry_name1());
                nodeCountryArray.add(nodeCountry);
                
                Label labelCountry = DynamicLabel.label("country");
                nodeCountry.addLabel(labelCountry);
                nodeCountry.setProperty( "name", mainCountryIsoDBArray.get(objArticle.getCountryArray().get(i)).getCountry_name1().trim());
                
                nodeCountry.setProperty( "nodeCentralityDegree", 0);
                nodeCountry.setProperty( "nodeCentralityCloseness", 0);
                nodeCountry.setProperty( "nodeCentralityBetweeness", 0);
                nodeArticle.setProperty( "nodeLocalClusteringCoefficient", 0);
                nodeCountry.setProperty( "isValid", "?");
                
                //System.out.println(objArticle.getAuthorsArray().get(i));
            }    
            
            // ************************************* NODE YEAR *************************************
            Node nodeYear = null;
            
            int articlePubYear = 0;
            
            if (objArticle.getDocExtractYear() != 0)
                articlePubYear = objArticle.getDocExtractYear();
            else 
                articlePubYear = objArticle.getDocMetaYear();
            
            nodeYear = factoryYear.getOrCreate( "year", articlePubYear);

            Label labelYear = DynamicLabel.label("year");
            nodeYear.addLabel(labelYear);
            nodeYear.setProperty( "yearDate", articlePubYear);

            nodeYear.setProperty( "nodeCentralityDegree", 0);
            nodeYear.setProperty( "nodeCentralityCloseness", 0);
            nodeYear.setProperty( "nodeCentralityBetweeness", 0);
            nodeArticle.setProperty( "nodeLocalClusteringCoefficient", 0);
            nodeYear.setProperty( "isValid", "?");
            
            
            // *************************************  NODE KEYWORDS *************************************
            
            ArrayList<Node> nodeKeywordArray = new ArrayList<>() ;
            
            for (int i=0; i<objArticle.getKeywordsArray().size() ;i++)
            {    
                
                //Node node  = graphDb.createNode();
                String strArticleKeyword = UtilFunctions.stringNormalizeFunction(objArticle.getKeywordsArray().get(i).trim());
                
                Node nodeKeyword = factoryKeyword.getOrCreate( "keyword", strArticleKeyword );
                nodeKeywordArray.add(nodeKeyword);
                
                Label labelKeyword = DynamicLabel.label("keyword");
                nodeKeyword.addLabel(labelKeyword);
                
                nodeKeyword.setProperty( "keyword", strArticleKeyword);  
                //nodeKeyword.setProperty( "keyword", objArticle.getKeywordsArray().get(i));
                
                nodeKeyword.setProperty( "nodeCentralityDegree", 0);
                nodeKeyword.setProperty( "nodeCentralityCloseness", 0);
                nodeKeyword.setProperty( "nodeCentralityBetweeness", 0);
                nodeArticle.setProperty( "nodeLocalClusteringCoefficient", 0);
                nodeKeyword.setProperty( "isValid", "?");
                
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
                        relationship = QueryNeo4jGetRelationship(currentNode, nextNode, RelTypes.COUNTRY_KEYWORD );
                        int objRelWeight = (int)relationship.getProperty("weight");
                        objRelWeight++;
                        relationship.setProperty( "weight", objRelWeight );
                    }
                }
            }
            
            
            
            
            /*
            // @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ RELATIONSHIP COUNTRY x YEAR @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
            
            if (objArticle.getDocExtractYear() != 0)
            {
                
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
            
                    
            // @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ RELATIONSHIP AUTHOR x YEAR @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
            if (objArticle.getDocExtractYear() != 0)
            {
                
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
            }
              
            // @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ RELATIONSHIP KEYWORD x YEAR @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
            
            if (objArticle.getDocExtractYear() != 0)
            {
                
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
            }
            */
            
            
            
            // @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ RELATIONSHIP ARTICLE x YEAR @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
            if (objArticle.getDocExtractYear() != 0)
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
        }
        finally
        {
            transaction.success();
        }

        /*
          // START SNIPPET: execute
        ExecutionEngine engine = new ExecutionEngine( graphDb );
        ExecutionResult result = engine.execute( "start n=node(*) return n" );
        // END SNIPPET: execute
        // START SNIPPET: columns
        List<String> columns = result.columns();
        // END SNIPPET: columns
        // START SNIPPET: items
        Iterator<Node> n_column = result.columnAs( "n" );
        for ( Node node : IteratorUtil.asIterable( n_column ) )
        {
            // note: we're grabbing the name property from the node,
            // not from the n.name in this case.
           
            try{
                 nodeResult = node + ":" + node.getProperty( "name" );
                  System.out.println(nodeResult);
            }
            catch(Exception e)
            {
                 System.out.println("Node query error: " + e);
            }
                    
           
        }
        */ 
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
                
                String strArticleReferenceQuery =  "MATCH (n:`article`) WHERE TRIM(LOWER(n.references)) =~ TRIM(LOWER('.*" + nodeArticle.getProperty( "title" ) + ".*')) RETURN n, n.title";
                resultArticleReference = engine.execute(strArticleReferenceQuery);
                
                //System.out.println("##### Article Loop #####:" + nodeArticle.getProperty( "title" ));
                
                Iterator<Node> n_column2 = resultArticleReference.columnAs( "n" );
                
                for ( Node nodeArticle2 : IteratorUtil.asIterable( n_column2 ) )
                {
                
                    nodeResult2 = nodeArticle2 + ": " + nodeArticle2.getProperty( "title" );    

                    if (QueryNeo4jCreateRelationshipValidation(nodeArticle, nodeArticle2, RelTypes.ARTICLE_REFERENCEDBY_ARTICLE ))
                    {
                        relationship = nodeArticle.createRelationshipTo( nodeArticle2, RelTypes.ARTICLE_REFERENCEDBY_ARTICLE );
                        //relationship.setProperty( "IS REFERENCED BY", "RelProperty1Description" );
                        relationship.setProperty( "weight", 1 );
                        relationship.setProperty( "direction", "directional" );
                        
                        System.out.println("##### FOUND #####:" + nodeArticle2.getProperty( "title" ));
                    }// else this relationship already exists 
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

            while (n_object.hasNext())
            {              
                Node node = n_object.next();
                Iterator iteratorRelationship = node.getRelationships().iterator();
                
                int intRelCount = 0;
                
                while(iteratorRelationship.hasNext() )
                {
                    intRelCount++;
                    iteratorRelationship.next();
                }    

                node.setProperty( "nodeCentralityDegree", intRelCount);
                
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

            //System.out.println("Node Centrality Betweeness Query:" + resultNode);

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
                    Double result = closenessCentrality.getCentrality(node);
                    node.setProperty( "nodeCentralityCloseness", result);
                    
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
                    Double result = betweennessCentrality.getCentrality(node);
                    node.setProperty( "nodeCentralityBetweeness", result);
                    
                   //System.out.print("betweenness centrality of node: " + node.getId() + " is ");
                   //System.out.println(result);
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

    // Function used by QueryNeo4jNodeCentralityBetweeness
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

    
} // END OutputNeo4j