/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package plosonexmlimport;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author leo
 */
public class ImportCSVMetrics {
    
    
    //private final String TEXT_DELIMITER = "\"(.*?)\"";
    private final String TEXT_DELIMITER = "(.*?)(\\t|$)"; // tab separated
    
    private static final String CSV_PATH = "C:\\barbosalm\\Projects\\PlosOneXMLImport\\plosone-metrics.csv" ;

    
    public void ImportCSVMetricsFunction() throws IOException
    {
        //Input file which needs to be parsed
        String fileToParse = CSV_PATH;
        ClassArticleMetrics objArticleMetrics = null;
        BufferedReader in = null;
         
        //Delimiter used in CSV file
        
        try
        {
            String line = "";
            //Create the file reader
            
            in = new BufferedReader(
            new InputStreamReader(new FileInputStream(fileToParse), "UTF-8"));
            //new InputStreamReader(new FileInputStream(fileToParse), "ISO-8859-1"));
             
            //Read the file line by line
            while ((line = in.readLine()) != null) 
            {
                //System.out.println(line);
                
                String strArticleID = "";
                String strArticleTitle = "";
                String strArticleViews = "0";
                String strArticleCitations = "0";
                String strArticleSaves = "0";
                String strArticleShares = "0";

                objArticleMetrics = new ClassArticleMetrics();

                //Get all tokens available in line
                final Pattern pattern_text_delimiter = Pattern.compile(TEXT_DELIMITER, Pattern.MULTILINE);
                final Matcher matcher_text_delimiter = pattern_text_delimiter.matcher(line);
               
                if (matcher_text_delimiter.find( )) 
                    strArticleID = matcher_text_delimiter.group();
                if (matcher_text_delimiter.find( )) 
                    strArticleTitle = matcher_text_delimiter.group();
                if (matcher_text_delimiter.find( )) 
                    strArticleViews = matcher_text_delimiter.group();
                if (matcher_text_delimiter.find( ))     
                    strArticleCitations = matcher_text_delimiter.group();
                if (matcher_text_delimiter.find( )) 
                    strArticleSaves = matcher_text_delimiter.group();
                if (matcher_text_delimiter.find( )) 
                    strArticleShares = matcher_text_delimiter.group();
                
                strArticleID = UtilFunctions.stringRemoveQuotes(strArticleID);
                strArticleTitle = UtilFunctions.stringRemoveQuotes(strArticleTitle);     
                strArticleViews = UtilFunctions.stringRemoveQuotes(strArticleViews);
                strArticleCitations = UtilFunctions.stringRemoveQuotes(strArticleCitations);  
                strArticleSaves = UtilFunctions.stringRemoveQuotes(strArticleSaves);
                strArticleShares = UtilFunctions.stringRemoveQuotes(strArticleShares);
                
                strArticleViews = UtilFunctions.stringCleanArticleMetrics(strArticleViews); 
                strArticleCitations = UtilFunctions.stringCleanArticleMetrics(strArticleCitations);           
                strArticleSaves = UtilFunctions.stringCleanArticleMetrics(strArticleSaves);     
                strArticleShares = UtilFunctions.stringCleanArticleMetrics(strArticleShares);     
                
                //Debbuging purposes 
                /*
                System.out.println("strArticleID:" + strArticleID);
                System.out.println("strArticleTitle:" + strArticleTitle);
                System.out.println("strArticleCitations:" + strArticleCitations);
                System.out.println("strArticleViews:" + strArticleViews);
                System.out.println("strArticleSaves:" + strArticleSaves);
                System.out.println("strArticleShares:" + strArticleShares);
                */
                
                objArticleMetrics.setStrArticleID(strArticleID);
                objArticleMetrics.setStrArticleTitle(strArticleTitle);
                objArticleMetrics.setStrArticleCitations(strArticleCitations);
                objArticleMetrics.setStrArticleViews(strArticleViews);
                objArticleMetrics.setStrArticleSaves(strArticleSaves);
                objArticleMetrics.setStrArticleShares(strArticleShares);
               
                PlosOneXMLImport.getArraylistArticle().add(objArticleMetrics);
            }
            
            
        } 
        catch (Exception e) {
            e.printStackTrace();
        } 
        finally
        {
            in.close();
        }
        
        
        // TODO code application logic here
    }
}
