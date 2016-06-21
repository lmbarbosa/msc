/*
 * Package: ArticleExtractionMain
 * 
 * Class: PDFBoxArticleExtractionMain
 *
 * Version: 0.1a
 *
 * Date: 2013-07-01
 * 
 * Author: Leonardo Maia Barbosa
 *
 * Copyright: Apache License v2.0
 * http://pdfbox.apache.org/
 * http://www.apache.org/licenses/LICENSE-2.0
 */

package ArticleExtraction;

// Java IO Imports used for reading and writing files
import static ArticleExtraction.OutputPrinter.mainCountryIsoDBArray;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author leo
 */
public class ArticleExtractionMain {

    /****************************** Variables [START] *************************/
   
    // Neo4j DB Path
    private static String strNeo4DBPath = "C:\\Users\\leo\\Documents\\Neo4j\\academic_documents_collaborative";
    
    // CSV Import File Path
    private static String strCSVImportPath = "C:\\barbosalm\\DataSamples\\pdfs\\ieeesamples\\collaboration\\pdfbox-output2.tsv";
    
    // Keep the document folder path to be analized 
    private static File docFolder = null;

    // Keep each document file from docFolder (current loop value)
    private static File docFolderFile = null;

    // Keep the character encoding
    private static String docEncoding = System.getProperty("file.encoding");

    // PDFBox Known File Types to import (valid extensions)
    private static final String pdfKnownFileTypes[] = {".pdf"};

    //ARTICLE PUBLICATION YEAR
    //private static final String YEAR_PATTERN = "(19|20)([0-9]{2})"; //YYYY ; 1900 <= YYYY <= 2099
    private static final String YEAR_PATTERN = "(19[0-9]{2}|20[0-1]{1}[0-9]{1})"; //YYYY ; 1900 <= YYYY <= 2099
         
    // Email Patter
    //private static final String EMAIL_PATTERN = "[_.A-Za-z0-9-]*@([A-Za-z0-9]+)+([.A-Za-z0-9]+)"; 
    //private static final String EMAIL_PATTERN = "\\{?[_.A-Za-z0-9-(, )]*\\}?@([A-Za-z0-9]+)+([.A-Za-z0-9]+)"; 
     private static final String EMAIL_PATTERN = "\\{?[_.A-Za-z0-9-(, )]*\\}?@([A-Za-z0-9-]+)+([.A-Za-z0-9]+)"; 
    // Country ISO DB Total Number
    private static final int COUNTRYDB_MAXNUM = 250; 
    
    // Country ISO DB Obj Array
    private static ArrayList<ClassCountryIsoDB> CountryIsoDBArray = new ArrayList<>() ;
    
            
    /****************************** Variables [END] ******************************/  
    
    public static File getDocFolder() {
        return docFolder;
    }

    public static void setDocFolder(File docFolder) {
        ArticleExtractionMain.docFolder = docFolder;
    }

    public static File getDocFolderFile() {
        return docFolderFile;
    }

    public static void setDocFolderFile(File docFolderFile) {
        ArticleExtractionMain.docFolderFile = docFolderFile;
    }

    public static String getDocEncoding() {
        return docEncoding;
    }

    public static void setDocEncoding(String docEncoding) {
        ArticleExtractionMain.docEncoding = docEncoding;
    }

    public static String getYEAR_PATTERN() {
        return YEAR_PATTERN;
    }

    public static String getEMAIL_PATTERN() {
        return EMAIL_PATTERN;
    }

    public static ArrayList<ClassCountryIsoDB> getCountryIsoDBArray() {
        return CountryIsoDBArray;
    }

    public static void setCountryIsoDBArray(ArrayList<ClassCountryIsoDB> CountryIsoDBArray) {
        ArticleExtractionMain.CountryIsoDBArray = CountryIsoDBArray;
    }

    public static String getStrNeo4DBPath() {
        return strNeo4DBPath;
    }

    public static void setStrNeo4DBPath(String strNeo4DBPath) {
        ArticleExtractionMain.strNeo4DBPath = strNeo4DBPath;
    }

    public static String getStrCSVImportPath() {
        return strCSVImportPath;
    }

    public static void setStrCSVImportPath(String strCSVPath) {
        ArticleExtractionMain.strCSVImportPath = strCSVPath;
    }

 
    

    
    /******************************  [END] ******************************/  
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        

        // TODO code application logic here
        ArticleExtractionMain objArticleExtractionMain = new ArticleExtractionMain();
        //mainMenu: Parameters parsing 
        objArticleExtractionMain.mainMenuOptionsParser(args);
        
        objArticleExtractionMain.countryIsoBDSelect();
        
        objArticleExtractionMain.docFolderProcessing();

    }
    
    
  
    // mainMenuOptionsParser
    private static void mainMenuOptionsParser(String[] args) throws Exception {
      int i;
      // iterate over all options (arguments starting with '-')
      for(i = 0; i < args.length && args[i].charAt(0) == '-'; i++) {
        switch(args[i].charAt(1)) {

          // -e encoding = character encoding 
          case 'e':
            docEncoding = args[++i];
            break;        

          // -d = document folder
          case 'd':
            docFolder = new File(args[++i]);
            break;       

          default:
            System.err.println("[error] Invalid option " + args[i]);
            mainMenuOptions();
        }
      }

      // validating the docFolder value
      if (!(docFolder.isDirectory()))
      {
          System.err.println("[error] the parameter docFolder is not a valid directory");
          mainMenuOptions();
      }    
    }
    
    
    //mainMenuOptions: Helpful information about the parameters
    private static void mainMenuOptions() {
      System.err.println(
     "Usage:\n" +
     "   java PDFBoxArticleExtraction -d <doc_folder> [-e encoding]  \n" +
     "\n" +
     "-d docFolder: (required) doc folder\n" +
     "\n" + 
     "-e encoding : (optional) character encoding.\n" +
     "              If not specified, the platform default encoding (\n" +
     "              \"" + System.getProperty("file.encoding") + "\") is used.\n" 
     );

      System.exit(1);
    } // [end] mainMenuOptions
    
    public static void docFolderProcessing() throws Exception
    {
       
        
               
        //Container with the doc folder path
        File[] listOfFiles = docFolder.listFiles();

        //Doc Folder processing: loop to analyze each file of the docFolder directory
        for (File docFolderFileLocal : listOfFiles) {
            
            docFolderFile = docFolderFileLocal;
            // check if it is a valid file
            if (docFolderFile.isFile())  {

                // loop to check all the PDF files compatibility
                for (String varString : pdfKnownFileTypes) {

                    String docFolderFileName = docFolderFile.getName();


                    // check if the file is compatible with GATE
                    if (docFolderFileName.toLowerCase().endsWith(varString)) 
                    {
                        
                        ClassArticleData objArticle = new ClassArticleData();
                        
                        objArticle.setDocEncoding(docEncoding);
                        objArticle.setInputDocFile(docFolderFile);

                       
                        ArticleStripperByArea.ArticleStripperByAreaFunc(objArticle);
                        ArticleStripper.ArticleStripperFunc(objArticle);
                        ArticleStream.mainInit(objArticle);
                        //OutputPrinter.OutputPrinterFunc(objArticle);                    
                        OutputWrite.OutputWriteArticleCitation(objArticle);
                        //OutputPgsql.OutputPgSqlInsert(objArticle);
                        
                        //OutputNeo4j.QueryNeo4j(objArticle);

                        //System.out.println("Press Enter to continue..."); 
                        //System.in.read(); 
                        
                        System.out.println("Finished."); 
                        
                        objArticle = null;
                        
                    } // [end] check if the file is compatible 
                }  // [end] loop to check all the PDF files compatibility
            }  // [end] check if it is a valid file
        } // [end] Doc Folder processing
        
        // Neo4Import Default 
        //Neo4jImport();
        
        // Neo4jImportAssortative
        //Neo4jImportAsssortative();
        
    } // [end]docFolderProcessing()    
    
    public static void Neo4jImport () throws Exception
    {
        
        CSVImport csvimportObj = new CSVImport();
        try 
        {
            csvimportObj.CSVImportFunction();
        } 
        catch (Exception ex) 
        {
            System.out.println("Error: Neo4jImport");
        }
        
        ArrayList<ClassArticle> arraylistArticleObj = csvimportObj.getArraylistArticle();
        
        System.out.println("[Neo4j Database]"); 
        System.out.println("Creating Nodes and Relationships..."); 
        for(int i=0; i<arraylistArticleObj.size(); i++)
        {
             OutputNeo4j.QueryNeo4j(arraylistArticleObj.get(i));
        }
        
        System.out.println("Updating Article Relationships..."); 
        OutputNeo4j.QueryNeo4jArticleRelationship();
        
        System.out.println("Updating Node Centrality Degree Values..."); 
        OutputNeo4j.QueryNeo4jNodeCentralityDegree();
        
        System.out.println("Updating Node Centrality Closeness Values..."); 
        OutputNeo4j.QueryNeo4jNodeCentralityCloseness();
        
        System.out.println("Updating Node Centrality Stress Values..."); 
        OutputNeo4j.QueryNeo4jNodeCentralityStress();
               
        System.out.println("Updating Node Centrality Betweeness Values..."); 
        OutputNeo4j.QueryNeo4jNodeCentralityBetweeness();
        
        System.out.println("Updating Node Centrality Eccentricity Values..."); 
        OutputNeo4j.QueryNeo4jNodeCentralityEccentricity();
        
        System.out.println("Updating Node Centrality Radiality Values..."); 
        OutputNeo4j.QueryNeo4jNodeCentralityRadiality();
        
        System.out.println("Updating Node Centrality Centroid Values..."); 
        OutputNeo4j.QueryNeo4jNodeCentralityCentroid();
        
        System.out.println("Updating Node Centrality Eigenvector (Power Method) ..."); 
        OutputNeo4j.QueryNeo4jNodeEigenvectorCentralityPowerMethod();
        
        System.out.println("Updating Node Local Clustering Coefficient..."); 
        OutputNeo4j.QueryNeo4jNodeLocalClusteringCoefficient();
                
        
    }
    
    public static void Neo4jImportAsssortative () throws Exception
    {
        
        CSVImport csvimportObj = new CSVImport();
        try 
        {
            csvimportObj.CSVImportFunction();
        } 
        catch (Exception ex) 
        {
            System.out.println("Error: Neo4jImport");
        }
        
        ArrayList<ClassArticle> arraylistArticleObj = csvimportObj.getArraylistArticle();
        
        System.out.println("[Neo4j Database]"); 
        System.out.println("Creating Nodes and Relationships..."); 
        for(int i=0; i<arraylistArticleObj.size(); i++)
        {
             OutputNeo4jAssortativity.QueryNeo4j(arraylistArticleObj.get(i));
        }
        
        System.out.println("Updating Article Relationships..."); 
        OutputNeo4jAssortativity.QueryNeo4jArticleRelationship();
        
        System.out.println("Updating Node Centrality Degree Values..."); 
        OutputNeo4jAssortativity.QueryNeo4jNodeCentralityDegree();
              
       
        
        OutputNeo4jAssortativity.QueryNeo4jDegreeAssortativityUndirected("AUTHOR_AUTHOR");
        
        OutputNeo4jAssortativity.QueryNeo4jDegreeAssortativityUndirected("KEYWORD_KEYWORD");
       
        
        OutputNeo4jAssortativity.QueryNeo4jDegreeAssortativityUndirected("COUNTRY_COUNTRY");
        
        OutputNeo4jAssortativity.QueryNeo4jDegreeAssortativityDirected("ARTICLE_REFERENCEDBY_ARTICLE");
                
       // OutputNeo4jAssortativity.QueryNeo4jYearAssortativityDiscrete();
         
        OutputNeo4jAssortativity.QueryNeo4jContinentCountryAssortativityDiscrete();
        
        OutputNeo4jAssortativity.QueryNeo4jContinentCitationAssortativityDiscrete();
        
        OutputNeo4jAssortativity.QueryNeo4jContinentKeywordAssortativityDiscrete();
              
        OutputNeo4jAssortativity.QueryNeo4jYearKeywordArticleAssortativityDiscrete();
                
    }
    
    public static void countryIsoBDSelect () throws Exception
    {
       
        PGSqlConn pgsqlconn_obj = null;
        
        pgsqlconn_obj.pgSqlConnect();
        
        PreparedStatement pgst = null; 
        ResultSet rs = null;
        Connection pgconn = pgsqlconn_obj.getPgconn();
        Integer varCountryCounter = 0;

        String stm = "SELECT country_id, country_iso_alphacode_2, country_iso_alphacode_3, country_name_1, country_name_2, country_name_3, country_continent "
                + "FROM countryisodb ";
        try {
             pgst = pgconn.prepareStatement(stm);
             rs = pgst.executeQuery();
             while (rs.next())
             {
                 
                 CountryIsoDBArray.add(new ClassCountryIsoDB()); 
                 CountryIsoDBArray.get(varCountryCounter).setCountry_id(rs.getInt("country_id"));
                 CountryIsoDBArray.get(varCountryCounter).setCountry_iso_alphacode2(rs.getString("country_iso_alphacode_2"));
                 CountryIsoDBArray.get(varCountryCounter).setCountry_iso_alphacode3(rs.getString("country_iso_alphacode_3"));
                 CountryIsoDBArray.get(varCountryCounter).setCountry_name1(rs.getString("country_name_1"));
                 CountryIsoDBArray.get(varCountryCounter).setCountry_name2(rs.getString("country_name_2"));
                 CountryIsoDBArray.get(varCountryCounter).setCountry_name3(rs.getString("country_name_3"));
                 CountryIsoDBArray.get(varCountryCounter).setCountry_continent(rs.getString("country_continent"));
                 
                 //System.out.println("Country_id:" + CountryIsoDBArray.get(varCountryCounter).getCountry_id() );  
                 //System.out.println("country_name_1:" + CountryIsoDBArray.get(varCountryCounter).getCountry_name1() );  
                 varCountryCounter++;

             } 
                              
             pgst.close();
             
        } catch(SQLException e) {
             System.out.println(e.getMessage());
             System.exit(1);
        }

    } //[end] countryIsoBDSelect()
     
} //[end] ArticleExtractionMain
