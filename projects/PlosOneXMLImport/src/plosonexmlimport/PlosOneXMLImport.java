/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package plosonexmlimport;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jsoup.nodes.Document;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

/**
 *
 * @author leo
 */
public class PlosOneXMLImport {

    /**
     * @param args the command line arguments
     */
    
    // Repository Path
    public static String str_xml_repository_path;
    // Keep the document folder path to be analized 
    private static File fileDocFolder;
    // Keep each document file from docFolder (current loop value)
    private static File fileDocFolderFile = null;
    // PDFBox Known File Types to import (valid extensions)
    private static final String strXMLKnownFileTypes[] = {".xml"};
    // Neo4j DB Path
    private static String strNeo4DBPath = "C:\\barbosalm\\Databases\\Neo4j\\plosone_computer_science";
    // Article Metrics 
    public static ArrayList<ClassArticleMetrics> arraylistArticleMetrics = new ArrayList<ClassArticleMetrics> ();

    
    // MAIN
    public static void main(String[] args) throws InterruptedException 
    {
       
       
         
        // Complex Network Functions
        try 
        { 
            //Import data and create database
            //ProcessData();
            
            //Create or Update database relationships
            //DatabaseRelationships();
            
            //Update metrics 
            //updateArticleMetrics();
            
            //Complex network functions like centrality etc.
            ComplexNetworkFunctions();
            
            //Assortativity functions 
            //ComplexNetworkAssortativityFunctions();
            
        } 
        catch (Exception ex)
        {
            System.out.println("[error] ComplexNetworkFunctions: " + ex.getMessage());
        }
            

    }
    
    public static void DatabaseRelationships() throws Exception
    {
    
        System.out.println("[Neo4j Database]"); 
        System.out.println("Updating Article Relationships..."); 
        OutputNeo4j.QueryNeo4jArticleRelationship();
    }
        
    public static void ProcessData() throws Exception
    {
          
            
        PlosOneXMLImport.str_xml_repository_path = "C:\\barbosalm\\Data\\xml\\plosone-computer-science";
        fileDocFolder = new File(str_xml_repository_path);

        // Google Scholar Query
        //String strTempArticleCitationValue = GoogleScholarCitation.GoogleScholarCitationRetrieveDataFunction("Self-Correcting Maps of Molecular Pathways");
        //.out.println("strTempArticleCitationValue: " + strTempArticleCitationValue);

        // TODO code application logic here
        System.out.println("[Jsoup] Extracting article information...");
        //System.out.println("[Neo4j] Creating Nodes and Relationships...");
        System.out.println("Please wait, this task can take several minutes...");

        //Container with the doc folder path
        File[] listOfFiles = fileDocFolder.listFiles();

        //Doc Folder processing: loop to analyze each file of the docFolder directory
        for (File docFolderFileLocal : listOfFiles)
            {
            fileDocFolderFile = docFolderFileLocal;
            // check if it is a valid file
            if (fileDocFolderFile.isFile())  
                {

                // loop to check all the PDF files compatibility
                for (String varString : strXMLKnownFileTypes)
                {

                    String docFolderFileName = fileDocFolderFile.getName();
                    // check if the file is compatible
                    if (docFolderFileName.toLowerCase().endsWith(varString))
                    {
                        ImportXML objImportXML = new ImportXML();
                        ClassArticle objClassArticle = new ClassArticle();

                        // Retrieve Data from XML
                        objClassArticle = objImportXML.importXMLRetrieveDataFunction(fileDocFolderFile);

                        try 
                        {
                            // Export Data to csv file
                            OutputWrite.OutputWriteArticleCitation(objClassArticle);

                            // Insert Data into Neo4j DB
                            OutputNeo4j.QueryNeo4j(objClassArticle);
                        }
                        catch (Exception ex)
                        {
                            System.out.println("[error] " + fileDocFolderFile + " : " + ex.getMessage());        
                        }
                   

                    }
                }

            }
        }
    
    }
    
    public static void ComplexNetworkFunctions() throws Exception
    {
        
        //System.out.println("Updating Node Centrality Degree Values..."); 
        //OutputNeo4j.QueryNeo4jNodeCentralityDegree();
        
        //System.out.println("Updating Node Centrality Closeness Values (lbarbosa1)..."); 
        //OutputNeo4j.QueryNeo4jNodeCentralityClosenessOwnImplementation();
        
        //System.out.println("Updating Node Centrality Closeness Values"); 
        //OutputNeo4j.QueryNeo4jNodeCentralityCloseness();

        System.out.println("Updating Node Centrality Betweeness Values..."); 
        OutputNeo4j.QueryNeo4jNodeCentralityBetweeness();
                  /* 
        System.out.println("Updating Node Centrality Stress Values..."); 
        OutputNeo4j.QueryNeo4jNodeCentralityStress();
        
        
        System.out.println("Updating Node Centrality Eccentricity Values..."); 
        OutputNeo4j.QueryNeo4jNodeCentralityEccentricity();
        
        //System.out.println("Updating Node Centrality Radiality Values..."); 
        //OutputNeo4j.QueryNeo4jNodeCentralityRadiality();
        
        //System.out.println("Updating Node Centrality Centroid Values..."); 
        //OutputNeo4j.QueryNeo4jNodeCentralityCentroid();
        
        System.out.println("Updating Node Centrality Eigenvector (Power Method) ..."); 
        OutputNeo4j.QueryNeo4jNodeEigenvectorCentralityPowerMethod();
        
        System.out.println("Updating Node Local Clustering Coefficient..."); 
        OutputNeo4j.QueryNeo4jNodeLocalClusteringCoefficient();
        */        
        
    }
    
      public static void ComplexNetworkAssortativityFunctions() throws Exception
    {
        
        //System.out.println("YearAssortativityDiscrete..."); 
        //OutputNeo4j.QueryNeo4jYearAssortativityDiscrete();  
        
        //System.out.println("YearKeywordArticleAssortativityDiscreteCountKeyword..."); 
        //OutputNeo4j.QueryNeo4jYearKeywordArticleAssortativityDiscreteSingle(); 
        
        //System.out.println("YearKeywordArticleAssortativityCountRelationships..."); 
        //OutputNeo4j.QueryNeo4jYearKeywordArticleAssortativityDiscreteMultiple(); 

        //System.out.println("ContinentCountryAssortativityDiscrete..."); 
        //OutputNeo4j.QueryNeo4jContinentCountryAssortativityDiscrete();
        
        //System.out.println("ContinentCitationAssortativityDiscrete..."); 
        //OutputNeo4j.QueryNeo4jContinentCitationAssortativityDiscrete();
        
        // BANCO DE DADOS EXCLUSIVO ASSORTATIVITY
        // System.out.println("ContinentKeywordAssortativityDiscrete..."); 
        //OutputNeo4j.QueryNeo4jContinentKeywordAssortativityDiscrete();
        
        System.out.println("DegreeAssortativityUndirected(\"AUTHOR_AUTHOR\")..."); 
        OutputNeo4j.QueryNeo4jDegreeAssortativityUndirected("AUTHOR_AUTHOR");
        
        System.out.println("NodeWeightAssortativityUndirected(\"AUTHOR_AUTHOR\")..."); 
        OutputNeo4j.QueryNeo4jNodeWeightAssortativityUndirected("AUTHOR_AUTHOR");
        
        System.out.println("\nDegreeAssortativityUndirected(\"KEYWORD_KEYWORD\")..."); 
        OutputNeo4j.QueryNeo4jDegreeAssortativityUndirected("KEYWORD_KEYWORD");
        
        System.out.println("NodeWeightAssortativityUndirected(\"KEYWORD_KEYWORD\")..."); 
        OutputNeo4j.QueryNeo4jNodeWeightAssortativityUndirected("KEYWORD_KEYWORD");
        
        System.out.println("\nDegreeAssortativityUndirected(\"COUNTRY_COUNTRY\")..."); 
        OutputNeo4j.QueryNeo4jDegreeAssortativityUndirected("COUNTRY_COUNTRY");
        
        System.out.println("NodeWeightAssortativityUndirected(\"COUNTRY_COUNTRY\")..."); 
        OutputNeo4j.QueryNeo4jNodeWeightAssortativityUndirected("COUNTRY_COUNTRY");
         
        //System.out.println("\nDegreeAssortativityUndirected(\"ARTICLE_REFERENCEDBY_ARTICLE\")..."); 
        //OutputNeo4j.QueryNeo4jDegreeAssortativityDirected("ARTICLE_REFERENCEDBY_ARTICLE");
        
    }
      
    public static void updateArticleMetrics() 
    {
         // Article Metrics DB
        ImportCSVMetrics objImportCSVMetrics = new ImportCSVMetrics();
        try {
            objImportCSVMetrics.ImportCSVMetricsFunction();
        } catch (IOException ex) {
            Logger.getLogger(PlosOneXMLImport.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        OutputNeo4jUpdateMetrics.UpdateMetricsQueryNeo4j();
        
    }
    
       
    
    public static String getStrNeo4DBPath() {
        return strNeo4DBPath;
    }

    public static File getFileDocFolder() {
        return fileDocFolder;
    }

    public static String getStr_xml_repository_path() {
        return str_xml_repository_path;
    }

    public static void setStr_xml_repository_path(String str_xml_repository_path) {
        PlosOneXMLImport.str_xml_repository_path = str_xml_repository_path;
    }

    public static ArrayList<ClassArticleMetrics> getArraylistArticle() {
        return arraylistArticleMetrics;
    }

    public static void setArraylistArticle(ArrayList<ClassArticleMetrics> arraylistArticle) {
        PlosOneXMLImport.arraylistArticleMetrics = arraylistArticle;
    }


}
