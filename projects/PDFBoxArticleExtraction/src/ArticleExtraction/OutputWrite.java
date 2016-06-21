/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ArticleExtraction;

import static ArticleExtraction.OutputPrinter.mainCountryIsoDBArray;
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
    
    public static void OutputWriteArticleCitation(ClassArticleData objArticle) throws Exception
    {
        // Article Data
        String strArticleTitle = "\"" + UtilFunctions.stringNormalizeFunction(objArticle.getDocExtractTitle().trim()) + "\""  ;
        String strArticleFileName = "\"" + objArticle.getInputDocFile().getName().trim() + "\""  ;
        String strArticleEmail = "\"" + UtilFunctions.stringNormalizeFunction(objArticle.getDocExtractEmail().trim()) + "\""  ;
        String strArticleCreationDate = "null";
        if (objArticle.getDocMetaCreationDate() != null)
            strArticleCreationDate = "\"" + objArticle.getDocMetaCreationDate().getTime().toLocaleString().toString() + "\"";
        else
            strArticleCreationDate = "\"" + "null" + "\"";
        String strArticlePageNumber = "\"" + objArticle.getDocMetaPageCount() + "\"";
        String strArticleReferences = UtilFunctions.stringNormalizeFunction(objArticle.getDocExtractReferences());
        strArticleReferences = UtilFunctions.stringReferenceRemoveIlegalChars(strArticleReferences);
        strArticleReferences = "\"" + UtilFunctions.stringRemoveIlegalChars(strArticleReferences)  + "\"";
        
        
        // Author data
        String strAuthorNameConcat = "\"";
        for (int i=0; i<objArticle.getAuthorsArray().size() ;i++)
        {  
            if (strAuthorNameConcat != "\"")
                strAuthorNameConcat += "," + objArticle.getAuthorsArray().get(i).trim();
            else
                strAuthorNameConcat += objArticle.getAuthorsArray().get(i).trim();
        }
        strAuthorNameConcat += "\"";
        
        // Country Data
        String strCountryCodeConcat = "\"";
        for (int i=0; i<objArticle.getCountryArray().size() ;i++)
        {    
            if (strCountryCodeConcat != "\"")
                strCountryCodeConcat += "," + mainCountryIsoDBArray.get(objArticle.getCountryArray().get(i)).getCountry_iso_alphacode3().trim();
            else    
                strCountryCodeConcat += mainCountryIsoDBArray.get(objArticle.getCountryArray().get(i)).getCountry_iso_alphacode3().trim();
        }
        strCountryCodeConcat += "\"";
        
        // Year Data
        String strArticlePubYear =  "\"" + "0" + "\"";
        if (objArticle.getDocExtractYear() != 0)
            strArticlePubYear = "\"" + objArticle.getDocExtractYear() + "\"";
        else 
            strArticlePubYear = "\"" + objArticle.getDocMetaYear() + "\"";
         
        // Keyword Data
        String strArticleKeywordConcat = "\"";
        for (int i=0; i<objArticle.getKeywordsArray().size() ;i++)
        {    
            if (strArticleKeywordConcat != "\"")
                strArticleKeywordConcat += "," + UtilFunctions.stringNormalizeFunction(objArticle.getKeywordsArray().get(i).trim());
            else
               strArticleKeywordConcat += UtilFunctions.stringNormalizeFunction(objArticle.getKeywordsArray().get(i).trim());
        }
        strArticleKeywordConcat += "\"";
        
        try
        {
            File docFolderFile = ArticleExtractionMain.getDocFolderFile();    
           
            Writer out = new BufferedWriter(new OutputStreamWriter(
            new FileOutputStream(docFolderFile.getParentFile() + "\\pdfbox-output2.csv", true), "ISO-8859-1"));
            try {
                out.write(strArticleFileName);
                out.write('\t');
                out.write(strArticleTitle);
                out.write('\t');
                out.write(strAuthorNameConcat);
                out.write('\t');
                out.write(strCountryCodeConcat);
                out.write('\t');
                out.write(strArticlePubYear);
                out.write('\t');
                out.write(strArticleKeywordConcat);
                out.write('\t');
                out.write(strArticlePageNumber);
                out.write('\t');
                out.write(strArticleEmail);
                out.write('\t');
                out.write(strArticleCreationDate);
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
                System.out.print(strArticleFileName + " [OutputWrite: ok]");
            }
            
            //System.out.println(docFolderFile.getParentFile());    
            // FileWriter writer = new FileWriter(docFolderFile.getParentFile() + "\\pdfbox-output.csv",true);
            // Header
            /*
            writer.append("filename");
            writer.append(',');
            writer.append("title");
            writer.append('\n');
            
            
            writer.append(strArticleFileName);
            writer.append(',');
            writer.append(strArticleTitle);
            writer.append(',');
            writer.append(strAuthorNameConcat);
            writer.append(',');
            writer.append(strCountryCodeConcat);
            writer.append(',');
            writer.append(intArticlePubYear.toString());
            writer.append(',');
            writer.append(strArticleKeywordConcat);
            writer.append(',');
            writer.append(intArticlePageNumber.toString());
            writer.append(',');
            writer.append(strArticleEmail);
            writer.append(',');
            writer.append(strArticleCreationDate);
            writer.append(',');
            writer.append(strArticleReferences);
            writer.append('\n');

            //generate whatever data you want
            writer.flush();
            writer.close();
            
            
            */
        
        }
        catch (Exception ex)
        {
            System.out.println("[error] output file cannot be write");
            System.out.println(ex.getMessage());                      
        } 
               

    }
    
    public static void OutputWriteArticleFull(ClassArticleData objArticle) throws Exception
    {
        // Variables
        String outputDirName = "\\pdfbox-output";
        File docFolderFile = ArticleExtractionMain.getDocFolderFile();
        String outputDirPathComplete = docFolderFile.getParentFile() + outputDirName;
        File outputDir = new File(outputDirPathComplete);
        File outputFile = new File(outputDirPathComplete, objArticle.getInputDocFile().getName() + ".txt");
        String varArticleData = objArticle.getDocFileData();
        String docEncoding = objArticle.getDocEncoding();
        
        //Create gate-output directory
        outputDir.mkdir();
        
        // GATE Output write for each file
        FileOutputStream fos = new FileOutputStream(outputFile);
        BufferedOutputStream bos = new BufferedOutputStream(fos);
        OutputStreamWriter out;
        if(docEncoding == null) {
          out = new OutputStreamWriter(bos);
        }
        else {
          out = new OutputStreamWriter(bos, docEncoding);
        }
        
        try{
            out.write(varArticleData);
        }
        catch (Exception ex)
        {
            System.out.println("[error] output file cannot be write");
            System.out.println(ex.getMessage());                      
        } 
        
        System.out.print(" [output: ok]");
        out.close();
        
    }
    
}
