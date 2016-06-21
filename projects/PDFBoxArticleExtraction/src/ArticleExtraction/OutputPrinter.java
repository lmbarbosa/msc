/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ArticleExtraction;

import java.util.ArrayList;
import java.util.Date;

/**
 *
 * @author leo
 */
public class OutputPrinter {
    
       public static ArrayList<ClassCountryIsoDB> mainCountryIsoDBArray = ArticleExtractionMain.getCountryIsoDBArray();
    
       public static void OutputPrinterFunc(ClassArticleData objArticle) 
       {
            
           
            System.out.println( "" );   
            System.out.println( "docFolder=" + ArticleExtractionMain.getDocFolder() );
            System.out.println( "" );   
            System.out.println( "InputFile=" + objArticle.getInputDocFile().getName() );
            System.out.println( "DocEncoding=" + objArticle.getDocEncoding() );
            System.out.println( "" );        
            System.out.println( "Meta:Page Count=" + objArticle.getDocMetaPageCount() );
            System.out.println( "Meta:Title=" + objArticle.getDocMetaTitle() );
            System.out.println( "Meta:TitleClean=" + objArticle.getDocMetaTitleClean() );
            System.out.println( "Meta:Author=" + objArticle.getDocMetaAuthor() );
            System.out.println( "Meta:Subject=" + objArticle.getDocMetaSubject() );
            System.out.println( "Meta:Keywords=" + objArticle.getDocMetaKeywords() );
            System.out.println( "Meta:Creator=" + objArticle.getDocMetaCreator() );
            System.out.println( "Meta:Producer=" + objArticle.getDocMetaProducer() );
            //System.out.println( "Meta:Creation Date=" + objArticle.getDocMetaCreationDate() );
            System.out.println( "Meta:Year=" + objArticle.getDocMetaYear() );
            System.out.println( "" );    
            //System.out.println( "Extract:Title=" + objArticle.getDocExtractTitle());
            System.out.println( "Extract:Title=" + UtilFunctions.stringNormalizeFunction(objArticle.getDocExtractTitle()));
            System.out.println( "Extract:Authors=" + objArticle.getDocExtractAuthor()); 
            System.out.println( "Extract:Email=" + objArticle.getDocExtractEmail());
            System.out.println( "Extract:Year=" + objArticle.getDocExtractYear()); 
            System.out.println( "Extract:Keywords=" + UtilFunctions.stringNormalizeKeywords(objArticle.getDocExtractKeywords())); 
            
            for (int i=0; i<objArticle.getCountryArray().size() ;i++)
            {
                //System.out.println( "Extract:CountryObjID=" + objArticle.getCountryIsoDBArray().get(i));
                System.out.println( "Extract:Country=" + mainCountryIsoDBArray.get(objArticle.getCountryArray().get(i)).getCountry_name1());
            }
            for (int i=0; i<objArticle.getKeywordsArray().size() ;i++)
            {
                System.out.println( "Extract:KeywordArray=" + objArticle.getKeywordsArray().get(i));
            }
            for (int i=0; i<objArticle.getAuthorsArray().size() ;i++)
            {
                System.out.println( "Extract:AuthorArray=" + objArticle.getAuthorsArray().get(i));
            }
                        
            System.out.println( "" );    
            //System.out.println( "Extract:References=" + objArticle.getDocExtractReferences() );
            System.out.println( "" );
            System.out.println( "" );    
            //System.out.println( "DocFileData=" + objArticle.getDocFileData() );
            System.out.println( "" );
       }
    
}
