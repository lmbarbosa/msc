/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ArticleExtraction;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author leo
 */
public class CSVImport {
    
    //private final String TEXT_DELIMITER = "\"(.*?)\"";
    private final String TEXT_DELIMITER = "(.*?)(\\t|$)"; // tab separated
    private final String DELIMITER = ",";
    
    private static final String CSV_PATH = ArticleExtractionMain.getStrCSVImportPath();
    
    public ArrayList<ClassArticle> arraylistArticle = new ArrayList<ClassArticle> ();

    public ArrayList<ClassArticle> getArraylistArticle() {
        return arraylistArticle;
    }

    public void setArraylistArticle(ArrayList<ClassArticle> arraylistArticle) {
        this.arraylistArticle = arraylistArticle;
    }
    
    
    
    public void CSVImportFunction() throws IOException
    {
        //Input file which needs to be parsed
        String fileToParse = CSV_PATH;
        String strArticleFilename = "";
        String strArticleTitle = "";
        String strArticleAuthor = "";
        String strArticleCountry = "";
        String strArticleYear = "";
        String strArticleKeywords = "";   
        String strArticlePageNumber = "";
        String strArticleEmail = "";
        String strArticleCreationDate = "";
        String strArticleReferences = "";
        ClassArticle objArticle = null;
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

                objArticle = new ClassArticle();

                //Get all tokens available in line
                final Pattern pattern_text_delimiter = Pattern.compile(TEXT_DELIMITER, Pattern.MULTILINE);
                final Matcher matcher_text_delimiter = pattern_text_delimiter.matcher(line);
               
                if (matcher_text_delimiter.find( )) 
                    strArticleFilename = matcher_text_delimiter.group();
                if (matcher_text_delimiter.find( )) 
                    strArticleTitle = matcher_text_delimiter.group();
                if (matcher_text_delimiter.find( )) 
                    strArticleAuthor = matcher_text_delimiter.group();
                if (matcher_text_delimiter.find( ))     
                    strArticleCountry = matcher_text_delimiter.group();
                if (matcher_text_delimiter.find( )) 
                    strArticleYear = matcher_text_delimiter.group();
                if (matcher_text_delimiter.find( )) 
                    strArticleKeywords = matcher_text_delimiter.group();
                if (matcher_text_delimiter.find( )) 
                    strArticlePageNumber = matcher_text_delimiter.group();
                if (matcher_text_delimiter.find( )) 
                    strArticleEmail = matcher_text_delimiter.group();
                if (matcher_text_delimiter.find( )) 
                    strArticleCreationDate = matcher_text_delimiter.group();
                if (matcher_text_delimiter.find( )) 
                    strArticleReferences = matcher_text_delimiter.group();
                
                
                strArticleFilename = UtilFunctions.stringRemoveQuotes(strArticleFilename);
                strArticleTitle = UtilFunctions.stringRemoveQuotes(strArticleTitle);
                strArticleAuthor = UtilFunctions.stringRemoveQuotes(strArticleAuthor);       
                strArticleCountry = UtilFunctions.stringRemoveQuotes(strArticleCountry);
                strArticleKeywords = UtilFunctions.stringRemoveQuotes(strArticleKeywords);
                strArticleYear = UtilFunctions.stringRemoveQuotes(strArticleYear);
                strArticlePageNumber = UtilFunctions.stringRemoveQuotes(strArticlePageNumber);
                strArticleEmail = UtilFunctions.stringRemoveQuotes(strArticleEmail);
                strArticleCreationDate = UtilFunctions.stringRemoveQuotes(strArticleCreationDate);
                strArticleReferences = UtilFunctions.stringRemoveQuotes(strArticleReferences);
                
                //System.out.println("CSV Import:" + strArticleAuthor);
                
                objArticle.setStrArticleFilename(strArticleFilename);
                objArticle.setStrArticleTitle(strArticleTitle);
                objArticle.setStrArticleAuthor(strArticleAuthor);
                objArticle.setStrArticleCountry(strArticleCountry);
                objArticle.setStrArticleKeywords(strArticleKeywords);
                objArticle.setStrArticleYear(strArticleYear);
                objArticle.setStrArticlePageNumber(strArticlePageNumber);
                objArticle.setStrArticleEmail(strArticleEmail);
                objArticle.setStrArticleCreationDate(strArticleCreationDate);
                objArticle.setStrArticleReferences(strArticleReferences);
                
                if (strArticleAuthor.indexOf(DELIMITER) > 0)
                {
                    String tempArrayAuthors[] = strArticleAuthor.split(DELIMITER);
                    for(int j=0; j<tempArrayAuthors.length; j++)
                    {
                        objArticle.getAuthorsArray().add(tempArrayAuthors[j]);
                    }
                }
                else
                    objArticle.getAuthorsArray().add(strArticleAuthor);
                
                if (strArticleCountry.indexOf(DELIMITER) > 0)
                {
                    String tempArrayCountry[] = strArticleCountry.split(DELIMITER);
                    for(int j=0; j<tempArrayCountry.length; j++)
                    {
                        objArticle.getCountryArray().add(tempArrayCountry[j]);
                    }
                }
                else
                    objArticle.getCountryArray().add(strArticleCountry);
                
                if (strArticleKeywords.indexOf(DELIMITER) > 0)
                {
                    String tempArrayKeywords[] = strArticleKeywords.split(DELIMITER);
                    for(int j=0; j<tempArrayKeywords.length; j++)
                    {
                        objArticle.getKeywordsArray().add(tempArrayKeywords[j]);
                    }
                }
                else
                    objArticle.getKeywordsArray().add(strArticleKeywords);
                
                
                arraylistArticle.add(objArticle);
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
