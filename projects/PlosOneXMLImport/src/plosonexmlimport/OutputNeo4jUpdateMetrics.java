/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package plosonexmlimport;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.neo4j.cypher.javacompat.ExecutionEngine;
import org.neo4j.cypher.javacompat.ExecutionResult;
import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.index.UniqueFactory;
import org.neo4j.helpers.collection.IteratorUtil;
import static plosonexmlimport.PlosOneXMLImport.arraylistArticleMetrics;



public class OutputNeo4jUpdateMetrics {
    
    
    private static final String DB_PATH = PlosOneXMLImport.getStrNeo4DBPath();
    static String resultString;
    static String columnsString;
    static String nodeResult;
    static String nodeResult2;
    static String rows = "";  
    
   
    public static void UpdateMetricsQueryNeo4j ()
    {
        
        GraphDatabaseService graphDb = new GraphDatabaseFactory().newEmbeddedDatabase( DB_PATH );
        Transaction transaction = graphDb.beginTx();
        
        ExecutionEngine engine = new ExecutionEngine( graphDb );
        ExecutionResult resultNode;

        try 
        {

            String strNodeQuery =  "MATCH (n:article) RETURN n as n_obj;" ;
            resultNode = engine.execute(strNodeQuery);

            //System.out.println("Node Centrality Query:" + resultNode);

            Iterator<Node> n_object = resultNode.columnAs( "n_obj" );
            
            Set<Node> setNode = new HashSet<Node>() {};
            
            for ( Node nodeStart : IteratorUtil.asIterable( n_object ) )
            {
                setNode.add(nodeStart);
            }

            for (Node nodeArticle:setNode)
            {         
                /*
                // Zero filling
                nodeArticle.setProperty( "views", 0);
                nodeArticle.setProperty( "citations", 0);
                nodeArticle.setProperty( "saves", 0);
                nodeArticle.setProperty( "shares", 0);
                */
                for(int i=0; i<arraylistArticleMetrics.size(); i++)
                {
                    if (nodeArticle.getProperty("id").toString().equalsIgnoreCase(arraylistArticleMetrics.get(i).getStrArticleID()))
                    {
                        
                        nodeArticle.setProperty( "views", arraylistArticleMetrics.get(i).getStrArticleViews().trim());
                        nodeArticle.setProperty( "citations", arraylistArticleMetrics.get(i).getStrArticleCitations().trim());
                        nodeArticle.setProperty( "saves", arraylistArticleMetrics.get(i).getStrArticleSaves().trim());
                        nodeArticle.setProperty( "shares", arraylistArticleMetrics.get(i).getStrArticleShares().trim());
                        
                        //Debugging purpuses
                        System.out.println("METRICS : " + nodeArticle.getProperty("id").toString() + " = citations: " + arraylistArticleMetrics.get(i).getStrArticleCitations().trim());
                    }    
                    
                }
            }
            
            // END SNIPPET: items
        }
        catch (Exception e)
        {
            System.out.println("Article Metrics error: " + e);
        }
        finally
        {
            transaction.success();
        }
        
        transaction.finish();
        graphDb.shutdown();
    } // END QueryNeo4jNodeCentralityDegree
        

} // END OutputNeo4j