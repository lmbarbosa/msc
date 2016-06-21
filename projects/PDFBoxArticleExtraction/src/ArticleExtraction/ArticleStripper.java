/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ArticleExtraction;

import static ArticleExtraction.ArticleStream.charAuthorData;
import org.apache.pdfbox.cos.COSDocument;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;
import java.io.IOException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.common.PDMetadata;

/**
 *
 * @author leo
 */
public class ArticleStripper {
    
    static java.io.InputStream docInputStream = null;
    static final String metaTitleEndChar1 = "- "; 
    static final String metaTitleEndChar2 = ". "; 
    static final String metaTitleEndChar3 = ", "; 
    
    //spaceYYYYspace ; 1900 <= YYYY <= 2199
    private static String YEAR_PATTERN = ArticleExtractionMain.getYEAR_PATTERN(); 
    
    //key words ; keyword; keywords ; key-words; key-word; key - word
    private static final String KEYWORDS_START_PATTERN_1 = "(^ ?key ?-? ?words?)";
    //index terms ; indexterm; indexterms ; index-terms; index-term
    private static final String KEYWORDS_START_PATTERN_2 = "(^ ?index(ing)? ?-? ?terms?)";
    // stop at . or : or abstract or introduction or i
    //private static final String KEYWORDS_END_PATTERN = "((\\. |\\si\\.|\\s1\\.|:|abstract|introduction|\\si\\s|nomenclature))";
    //private static final String KEYWORDS_END_PATTERN = "((\\. |\\si\\.|\\s1\\.|\\s:|abstract|introduction|\\si\\s|nomenclature))";
    //private static final String KEYWORDS_END_PATTERN = "((\\. |\\si\\.|\\s1\\.|\\s1\\)|\\s:|abstract|introduction|\\si\\s|nomenclature))";
    private static final String KEYWORDS_END_PATTERN = "((\\. |\\si\\.|\\s1\\.|\\s1\\)|\\s:|abstract|introduction|\\si\\s|nomenclature|\\s1\\s))";
    
    //reference, references
    //private static final String REFERENCES_START_PATTERN = "(r\\s?e\\s?f\\s?e\\s?r\\s?e\\s?n\\s?c\\s?e\\s?s)";
    //private static final String REFERENCES_START_PATTERN = "(^(r\\s?e\\s?f\\s?e\\s?r\\s?e\\s?n\\s?c\\s?e\\s?s))|((r\\s?e\\s?f\\s?e\\s?r\\s?e\\s?n\\s?c\\s?e\\s?s\\s?)$)";
      private static final String REFERENCES_START_PATTERN = "(^(R\\s?e\\s?f\\s?e\\s?r\\s?e\\s?n\\s?c\\s?e\\s?s?\\s?))|^(R\\s?E\\s?F\\s?E\\s?R\\s?E\\s?N\\s?C\\s?E\\s?S?\\s?)|(R\\s?e\\s?f\\s?e\\s?r\\s?e\\s?n\\s?c\\s?e\\s?s?\\s?\\s?)$|((R\\s?E\\s?F\\s?E\\s?R\\s?E\\s?N\\s?C\\s?E\\s?S?\\s?\\s?)$)";
    
    //appendix, bibliography(ies)
    private static final String REFERENCES_END_PATTERN = "((b\\s?i\\s?b\\s?l\\s?i\\s?o\\s?g\\s?r\\s?a\\s?f\\s?(y|ies)\\s?)|a\\s?p\\s?p\\s?e\\s?n\\s?d\\s?i\\s?x\\s?|(b\\s?i\\s?o\\s?g\\s?r\\s?a\\s?p\\s?h\\s?(y|ies)\\s?))";
    
    //private static final String REFERENCES_INDIVIDUAL_PATTERN_1 = "((^\\[\\s?[0-9]{1,2}\\s?\\])(.*))";
    //private static final String REFERENCES_INDIVIDUAL_PATTERN_2 = "((^[0-9]{1,2}(\\s|\\.))(.*))";
    static int varTempTitleEnd = -1;
    
    public static void setExtractKeywords(ClassArticleData objArticle, String varKeywords)
    {
        
        if (varKeywords.length() > 0 && varKeywords.isEmpty() == false)
        {
            //if (varKeywords.indexOf(",") > 0 )
            if (varKeywords.indexOf(",") > 0 && varKeywords.indexOf(";") < 0)
            {
                String tempArrayKeywords[] = varKeywords.split(",");
                
                for(int i=0; i<tempArrayKeywords.length; i++)
                {
                    String tempStringKeywords = tempArrayKeywords[i].trim().toLowerCase();

                    if (tempStringKeywords.length() > 2)
                    {
                        if(tempStringKeywords.charAt(tempStringKeywords.length()-1) == '.'
                           || tempStringKeywords.charAt(tempStringKeywords.length()-1) == ';'
                           || tempStringKeywords.charAt(tempStringKeywords.length()-1) == ',')
                            tempStringKeywords = tempStringKeywords.substring(0, tempStringKeywords.length()-1);
                            
                        objArticle.getKeywordsArray().add(tempStringKeywords);
                    }
                }
               
            }
            else if (varKeywords.indexOf(";") > 0)
            {
                String tempArrayKeywords[] = varKeywords.split(";");            
                
                for(int i=0; i<tempArrayKeywords.length; i++)
                {
                    String tempStringKeywords = tempArrayKeywords[i].trim().toLowerCase();;
                        
                    if (tempStringKeywords.length() > 2)
                    {
                        if(tempStringKeywords.charAt(tempStringKeywords.length()-1) == '.'
                           || tempStringKeywords.charAt(tempStringKeywords.length()-1) == ';'
                           || tempStringKeywords.charAt(tempStringKeywords.length()-1) == ',')
                            tempStringKeywords = tempStringKeywords.substring(0, tempStringKeywords.length()-1);
                            
                        objArticle.getKeywordsArray().add(tempStringKeywords);
                    }
                }
            }
        }
        else
        {
            varKeywords = null;
        }
    }
    
    public static void ArticleStripperFunc (ClassArticleData objArticle) throws IOException 
    {
        
        
        // LEMBRAR DE FECHAR COM TRY CATCH
        
        // Get Document Data
        docInputStream = new java.io.FileInputStream(objArticle.getInputDocFile());
        PDFParser parser = new PDFParser(docInputStream);
        parser.parse();
        COSDocument cosDoc = parser.getDocument();
        PDFTextStripper stripper = new PDFTextStripper();
        stripper.setSpacingTolerance((float)0.2);  // PDFBox WhiteSpace bug fixing: Adjust the Spacing Tolerance 
        String docText = stripper.getText(new PDDocument(cosDoc));
        objArticle.setDocFileData(docText); 
        
        PDFTextStripper stripper_lastpage = new PDFTextStripper();
        stripper_lastpage.setSpacingTolerance((float)0.2);  // PDFBox WhiteSpace bug fixing: Adjust the Spacing Tolerance 
        stripper_lastpage.setStartPage(parser.getPDDocument().getNumberOfPages()-1);
        String docTextLastPage = stripper_lastpage.getText(new PDDocument(cosDoc));
        
        
        //final Pattern pattern_start_references = Pattern.compile(REFERENCES_START_PATTERN, Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
        //final Pattern pattern_start_references = Pattern.compile(REFERENCES_START_PATTERN, Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
        final Pattern pattern_start_references = Pattern.compile(REFERENCES_START_PATTERN, Pattern.MULTILINE);
        final Pattern pattern_end_references = Pattern.compile(REFERENCES_END_PATTERN, Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
        //final Pattern pattern_individual_references_1 = Pattern.compile(REFERENCES_INDIVIDUAL_PATTERN_1, Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
        //final Pattern pattern_individual_references_2 = Pattern.compile(REFERENCES_INDIVIDUAL_PATTERN_2, Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
        //System.out.println("################## docTextLastPage:" + docTextLastPage);
        final Matcher matcher_start_references = pattern_start_references.matcher(docTextLastPage);
        final Matcher matcher_end_references = pattern_end_references.matcher(docTextLastPage);
        String docTextLastPageReferences = null;
        
        if (matcher_start_references.find())
        {
            
            if (matcher_end_references.find() && matcher_end_references.start() > matcher_start_references.end())
            {
                docTextLastPageReferences = docTextLastPage.substring(matcher_start_references.end(), matcher_end_references.start());
            }
            else
            {
                docTextLastPageReferences = docTextLastPage.substring(matcher_start_references.end(), docTextLastPage.length());
            }
            
            //System.out.println("################## docTextLastPageReferences:" + docTextLastPageReferences);
            objArticle.setDocExtractReferences(docTextLastPageReferences);
            
            /*
            final Matcher matcher_individual_references_1 = pattern_individual_references_1.matcher(docTextLastPageReferences);
            final Matcher matcher_individual_references_2 = pattern_individual_references_2.matcher(docTextLastPageReferences);
            
            if (matcher_individual_references_1.find())
            {
                //System.out.println("################## matcher1.group():" + matcher_individual_references_1.group()) ;
                int varTempIndividualReferenceStart = matcher_individual_references_1.start();
                while (matcher_individual_references_1.find())
                {
                    System.out.println("################## matcher1.group():" + docTextLastPageReferences.substring(varTempIndividualReferenceStart,matcher_individual_references_1.start())) ;
                    varTempIndividualReferenceStart = matcher_individual_references_1.start();
                }
            }
            else if (matcher_individual_references_2.find())
            {
                //System.out.println("################## matcher1.group():" + matcher_individual_references_2.group()) ;
                int varTempIndividualReferenceStart = matcher_individual_references_2.start();
                while (matcher_individual_references_2.find())
                {
                    System.out.println("################## matcher2.group():" + docTextLastPageReferences.substring(varTempIndividualReferenceStart,matcher_individual_references_2.start())) ;
                    varTempIndividualReferenceStart = matcher_individual_references_2.start();
                }
            }
            */
        }
        
        docInputStream.close();// Closing documents
        cosDoc.close(); // Closing documents 
        
        // Get Meta Data
        PDDocument PDDoc = PDDocument.load(objArticle.getInputDocFile());
        PDDocumentInformation info = PDDoc.getDocumentInformation();
        try
        {
            objArticle.setDocMetaAuthor(info.getAuthor());
            objArticle.setDocMetaCreationDate(info.getCreationDate());
            objArticle.setDocMetaCreator(info.getCreator());
            objArticle.setDocMetaKeywords(info.getKeywords());
            objArticle.setDocMetaPageCount(PDDoc.getNumberOfPages());
            objArticle.setDocMetaProducer(info.getProducer());
            objArticle.setDocMetaSubject(info.getSubject());
            objArticle.setDocMetaTitle(info.getTitle());
            objArticle.setDocMetaTitleClean(info.getTitle());
            objArticle.setDocMetaYear(info.getCreationDate().get(Calendar.YEAR));
        }
        catch (Exception e)
        {
            System.out.println("################## ERROR: Article stripper - Get Meta Data ");
        }
        
        //Clean the Title Data
        if (info.getTitle() != null && !info.getTitle().isEmpty())
        {
            if(info.getTitle().indexOf(metaTitleEndChar1) != -1)
                varTempTitleEnd = info.getTitle().indexOf(metaTitleEndChar1);
            else if(info.getTitle().indexOf(metaTitleEndChar2) != -1)
                varTempTitleEnd = info.getTitle().indexOf(metaTitleEndChar2);
            else if(info.getTitle().indexOf(metaTitleEndChar3) != -1)
                varTempTitleEnd = info.getTitle().indexOf(metaTitleEndChar3);
            else
                varTempTitleEnd = -1;

            if (varTempTitleEnd > 0)
                objArticle.setDocMetaTitleClean(info.getTitle().substring(0, varTempTitleEnd));
            
            final Pattern pattern_year = Pattern.compile(YEAR_PATTERN, Pattern.DOTALL);
            final Matcher matcher_year = pattern_year.matcher(info.getTitle());
            
            if (matcher_year.find( )) 
                objArticle.setDocMetaYear(Integer.parseInt(matcher_year.group()));
        }
 
        final Pattern pattern_keywords_1 = Pattern.compile(KEYWORDS_START_PATTERN_1, Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
        final Pattern pattern_keywords_2 = Pattern.compile(KEYWORDS_START_PATTERN_2, Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
        final Pattern pattern_keywords_3 = Pattern.compile(KEYWORDS_END_PATTERN, Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
        
        final Matcher matcher_keywords_1 = pattern_keywords_1.matcher(docText);
        final Matcher matcher_keywords_2 = pattern_keywords_2.matcher(docText);
        
        objArticle.setDocExtractKeywords(null);
        
        //System.out.println("################## docText:" + docText);
        
        // 1) Extract Keyword from article data
        // 2) If Keyword from article data == null; then get keywords from metadata
        // 3) If Keyword from metadata data has more ",;" than extract data; then get keywords from metadata
        if(matcher_keywords_1.find() && matcher_keywords_1.start() < 5000)
        {
            //System.out.println("################## matcher_keywords_1.start():" + matcher_keywords_1.start());
            String varTempKeywords = docText.substring(matcher_keywords_1.end()+1, matcher_keywords_1.start()+300);
            //System.out.println("################## varTempKeywords:" + varTempKeywords);
             
            final Matcher matcher_keywords_3 = pattern_keywords_3.matcher(varTempKeywords);
            if (matcher_keywords_3.find())
            {
                //System.out.println("################## varTempKeywords:" + varTempKeywords);
                varTempKeywords = varTempKeywords.substring(0, matcher_keywords_3.start());
                //System.out.println("@@@@@@@@@@@@@@@@@@ varTempKeywords:" + varTempKeywords);
                //setExtractKeywords(objArticle,varTempKeywords );
                objArticle.setDocExtractKeywords(varTempKeywords);
                
                if (
                          objArticle.getDocMetaKeywords() != null  &&
                          (UtilFunctions.countStringOccurrency(objArticle.getDocMetaKeywords(), ",") > UtilFunctions.countStringOccurrency(varTempKeywords, ",")  
                            && UtilFunctions.countStringOccurrency(objArticle.getDocMetaKeywords(), ";") > UtilFunctions.countStringOccurrency(varTempKeywords, ";"))  
                            
                         )
                    {
                        setExtractKeywords(objArticle,objArticle.getDocMetaKeywords());
                        
                    }
                            
                else
                    {
                        //System.out.println("$$$$$$$$$$$$$$$$$$$ varTempKeywords:" + varTempKeywords);
                        varTempKeywords = UtilFunctions.stringNormalizeKeywords(varTempKeywords);
                        setExtractKeywords(objArticle,varTempKeywords);
                    }
            }    
        }
        else if (matcher_keywords_2.find() && matcher_keywords_2.start() < 5000)
        {
            //System.out.println("################## matcher2.start():" + matcher_keywords_2.group());
            String varTempKeywords = docText.substring(matcher_keywords_2.end()+1, matcher_keywords_2.start()+300);

            final Matcher matcher_keywords_3 = pattern_keywords_3.matcher(varTempKeywords);
            if (matcher_keywords_3.find())
            {
                 //System.out.println("################## matcher_keywords_3.find():" + matcher_keywords_3.group());
                 varTempKeywords = varTempKeywords.substring(0, matcher_keywords_3.start());
                 
                 //setExtractKeywords(objArticle,varTempKeywords );
                 objArticle.setDocExtractKeywords(varTempKeywords);
                                 if (
                          objArticle.getDocMetaKeywords() != null  &&
                          (UtilFunctions.countStringOccurrency(objArticle.getDocMetaKeywords(), ",") > UtilFunctions.countStringOccurrency(varTempKeywords, ",")  
                            && UtilFunctions.countStringOccurrency(objArticle.getDocMetaKeywords(), ";") > UtilFunctions.countStringOccurrency(varTempKeywords, ";"))  
                            
                         )
                    {
                        setExtractKeywords(objArticle,objArticle.getDocMetaKeywords());
                    }
                            
                else
                    {
                        varTempKeywords = UtilFunctions.stringNormalizeKeywords(varTempKeywords);    
                        setExtractKeywords(objArticle,varTempKeywords);
                    }
            }
        }
        else
        {
            if (objArticle.getDocMetaKeywords() != null)
            {
                setExtractKeywords(objArticle,objArticle.getDocMetaKeywords());
            }
        }
        
        PDDoc.close();
        
    }
    
}
