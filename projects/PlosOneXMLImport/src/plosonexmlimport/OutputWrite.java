/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package plosonexmlimport;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.OutputStreamWriter;
import java.io.Writer;

/**
 *
 * @author leo
 */
public class OutputWrite {
    
    public static void OutputWriteArticleCitation(ClassArticle objArticle) 
    {
        // Article Data
        String strArticleTitle = "\"" + objArticle.getStrArticleTitle() + "\""  ;
        String strArticleFileName = "\"" + objArticle.getStrArticleFileName() + "\""  ;
        String strArticleID = "\"" + objArticle.getStrArticleID() + "\""  ;
        String strArticleViews = "\"" + objArticle.getStrArticleViews()  + "\"";
        String strArticleCitations = "\"" + objArticle.getStrArticleCitations()  + "\"";
        String strArticleSaves = "\"" + objArticle.getStrArticleSaves()  + "\"";
        String strArticleShares = "\"" + objArticle.getStrArticleShares()  + "\"";
        String strArticleReferences = "\"" + objArticle.getStrArticleReferences()  + "\"";

        // Author data
        String strAuthorNameConcat = "\"";
        for (int i=0; i<objArticle.getArrayListAuthor().size() ;i++)
        {  
            if (strAuthorNameConcat != "\"")
                strAuthorNameConcat += "," + objArticle.getArrayListAuthor().get(i).trim();
            else
                strAuthorNameConcat += objArticle.getArrayListAuthor().get(i).trim();
        }
        strAuthorNameConcat += "\"";
        
        // Country Data
        String strCountryCodeConcat = "\"";
        for (int i=0; i<objArticle.getArrayListCountry().size() ;i++)
        {    
            if (strCountryCodeConcat != "\"")
                strCountryCodeConcat += "," + objArticle.getArrayListCountry().get(i).trim();
            else    
                strCountryCodeConcat += objArticle.getArrayListCountry().get(i).trim();
        }
        strCountryCodeConcat += "\"";
        
        // Country Continent Data
        String strCountryContinentConcat = "\"";
        for (int i=0; i<objArticle.getArrayListCountry().size() ;i++)
        {    
            String str_country_continent = CountryContinent.pgsqlQueryCountryContinent(objArticle.getArrayListCountry().get(i).trim());
            //String str_country_alphacode3 = CountryContinent.pgsqlQueryCountryAlphaCode3(strCountryTemp);
            
            if (strCountryContinentConcat != "\"")
                strCountryContinentConcat += "," + str_country_continent;
            else    
                strCountryContinentConcat += str_country_continent;
        }
        strCountryContinentConcat += "\"";
   
        // Year Data
        String strArticlePubYear = "\"" + objArticle.getStrArticlePublicationYear() + "\"";
         
        // Keyword Data
        String strArticleKeywordConcat = "\"";
        for (int i=0; i<objArticle.getArrayListKeywords().size() ;i++)
        {    
            if (strArticleKeywordConcat != "\"")
                strArticleKeywordConcat += "," + objArticle.getArrayListKeywords().get(i).trim();
            else
               strArticleKeywordConcat += objArticle.getArrayListKeywords().get(i).trim();
        }
        strArticleKeywordConcat += "\"";
        
        try
        {
            File docFolderFile = PlosOneXMLImport.getFileDocFolder();
           
            Writer out = new BufferedWriter(new OutputStreamWriter(
            new FileOutputStream("C:\\barbosalm\\Projects\\PlosOneXMLImport\\xml-outputdata.csv", true), "ISO-8859-1"));
            try {
                out.write(strArticleFileName);
                out.write('\t');
                out.write(strArticleID);
                out.write('\t');
                out.write(strArticleTitle);
                out.write('\t');
                out.write(strAuthorNameConcat);
                out.write('\t');
                out.write(strCountryCodeConcat);
                out.write('\t');
                out.write(strCountryContinentConcat);
                out.write('\t');
                out.write(strArticlePubYear);
                out.write('\t');
                out.write(strArticleKeywordConcat);
                out.write('\t');
                out.write(strArticleViews);
                out.write('\t');
                out.write(strArticleCitations);
                out.write('\t');
                out.write(strArticleSaves);
                out.write('\t');
                out.write(strArticleShares);
                out.write('\t');
                out.write(strArticleReferences);
                out.write('\n');
                
            }
            catch (Exception ex)
            {
                System.out.println("[error] output file cannot be write");
                System.out.println(ex.getMessage());                      
            } 
            finally {
                out.close();
                System.out.println(strArticleFileName + " [OutputWrite: ok]");
                
                if (strCountryContinentConcat.contains("null"))
                    System.out.println(strArticleFileName + "[country-null]" + strCountryCodeConcat +  " " + strCountryContinentConcat);
                    
            }
            
        
        }
        catch (Exception ex)
        {
            System.out.println("[error] output file cannot be write");
            System.out.println(ex.getMessage());                      
        } 
               

    }
    
}
