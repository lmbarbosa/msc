/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ArticleExtraction;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDStream;
import org.apache.pdfbox.pdmodel.font.PDSimpleFont;
import org.apache.pdfbox.util.PDFTextStripper;
import org.apache.pdfbox.util.TextPosition;

/**
 *
 * @author leo
 */
public class ArticleStream extends PDFTextStripper {
    
    // Analysis End Page
    static int intEndPage = 1;
    // Array containg all chars TextPosition
    static ArrayList<TextPosition> charFontInfo = new ArrayList<> ();
    
    
    
    
    static ArrayList<ClassBiggestFont> arrayBiggestFont = new ArrayList<> ();
    
    ClassBiggestFont objBiggestFont = new ClassBiggestFont();
    
    
    // Document biggest Font Size BaseFont
    static String charBiggestFontSizeBase = null;
    // Document biggest Font Size YDirAjd
    static float charBiggestFontSizeYLimit = 0;
    // Document biggest Font Size
    static float charBiggestFontSize = 0;
    static int charBiggestFontSizeOcurrence = 0;
    static float charSecBiggestFontSize = 0;
    
    
    
    
    // Index to define the end of the title search
    static int lastIndexCharTitle = 300;
    // First char YDirAdj
    static float firstCharYDirAdj = 0;
    // Iterator used to scan the document
    static int charIterator = 0;
    static int charIterator2 = 0;
    // Title minimun Length
    static int minTitleLength = 5; 
    // Title Minimum XDirAdj Alignment 
    static int minStartTitleXDirAdj = 44;    
    static int maxStartTitleXDirAdj = 300;
    //static int minTitleXDirAdj = 400;    // IEE
    // Title Minimum YDirAdj Alignment 
    //static int minTitleYDirAdj = 100; // Nature 
    //static int minTitleYDirAdj = 60; // IEEE 
    static int minTitleYDirAdj = 40; // IEEE 
    // Title Maximum YDirAdj Alignment 
    //static int maxTitleYDirAdj = 300; 
    static int maxTitleYDirAdj = 400; 
    // Limit error for the FontSize
    static float errorLimit = 2;
    // Limit error for the YScale [Title - Subtitle - Author]
    static int YScaleLimit = 4;
    // Limit error the the YDirAdj [Title - Subtitle - Author]
    static int YDirAdjLimit = 19;
    // Global counter for Title Index
    static int intTitleGlobalCounter = 0;
    
    // White Space character counter
    static int whiteSpaceCounter = 0;
    // Special character counter
    static int specialCharCounter = 0;
    // White Space Minimal Limit
    static int whiteSpaceMinLimit = 100;
    
    // TITLE
    static int charTitleInitIndex = 0;
    static int charTitleEndIndex = 0;
    static float charTitleYAxis = 0;
    static float charTitleFontSize = 0;
    static String charTitleData = "";
    
    //SUBTITLE
    static int charSubTitleInitIndex = 0;
    static float charSubTitleYAxis = 0;
    static float charSubTitleFontSize = 0;
    static String charSubTitleData = "";
    
    //AUTHOR
    static int charAuthorInitIndex = 0;
    static float charAuthorYAxis = 0;
    static float charAuthorSecLnYAxis = 0;
    static float charAuthorFontSize = 0;
    static float charDistanceXDiff = 0;
    static String charAuthorData = "";
    static float lastCharXAdj = 0; // keep the last char X coordinate
    static int charIteratorAuthor = 0;
    static float charAuthorTextHeight = 0;
    static String charAuthorFontName = "";
  
    
    //ARTICLE PUBLICATION YEAR
    //private static String YEAR_PATTERN = ArticleExtractionMain.getYEAR_PATTERN(); //spaceYYYYspace ; 1900 <= YYYY <= 2199
    static String articlePubYear = null;
    //AUTHOR SPLIT PATTERN = 2 or more spaces
    private static final String AUTHOR_SPLIT_PATTERN = "[\\s]{2,}";
  
    
    public ArticleStream() throws IOException
    {
        super.setSortByPosition( true );
        
        
    }
    
    // This function fix a PDFBox bug to detect white space between chars
    // Bug: For some documents the PDFBox is not able to identify the white space length, therefore there are no spaces between chars.
    public static String bugfixPDFBoxWhiteSpace (int varIterator)
    {
        String varCharReturn = "";
        try 
        {
            
            if (
                 (charFontInfo.get(varIterator+1).getXDirAdj() ) > 
                 (charFontInfo.get(varIterator).getXDirAdj() + charFontInfo.get(varIterator).getWidth() + errorLimit)  
                  || (charFontInfo.get(varIterator+1).getYDirAdj() - charFontInfo.get(varIterator).getYDirAdj()) >  YDirAdjLimit - errorLimit 
               ) 
            {
              varCharReturn = " ";
            }
        }
        catch (Exception e)
        {
            
        }
        
        return varCharReturn;
        
    }
    
    public static void ArticleVariablesInitializer()
    {
        charTitleData = "";
        charSubTitleData = "";
        charAuthorData = "";
        charFontInfo = new ArrayList<> ();
   
        arrayBiggestFont = new ArrayList<> ();
        charBiggestFontSize = 0;
        charSecBiggestFontSize = 0;
        charBiggestFontSizeOcurrence = 0;
        firstCharYDirAdj = 0;
        charIterator = 0;
        charIterator2 = 0; 

        whiteSpaceCounter = 0;
        specialCharCounter= 0;

        charTitleInitIndex = 0;
        charTitleEndIndex = 0;
        charTitleYAxis = 0;
        charTitleFontSize = 0;
        charTitleData = "";

        charSubTitleInitIndex = 0;
        charSubTitleYAxis = 0;
        charSubTitleFontSize = 0;
        charSubTitleData = "";

        charAuthorInitIndex = 0;
        charAuthorYAxis = 0;
        charAuthorSecLnYAxis = 0;
        charAuthorFontSize = 0;
        charDistanceXDiff = 0;
        charAuthorData = "";
        charIteratorAuthor = 0;
        charAuthorTextHeight = 0;
        charAuthorFontName = "";
        
        intTitleGlobalCounter = 0;
        
    }
    
    public static boolean verifyAuthorName(String strAuthorData)
    {
        boolean intVarAuthorValidReturn = false;
        
        if (strAuthorData.length() > 2 
                && strAuthorData.trim().toLowerCase().indexOf("ieee") < 0 
                && strAuthorData.trim().toLowerCase().indexOf("member") < 0 
                && strAuthorData.trim().toLowerCase().indexOf("fellow") < 0
                 && strAuthorData.trim().toLowerCase().indexOf("abstract") < 0)
            intVarAuthorValidReturn = true;
        
        return intVarAuthorValidReturn;
    }
    
    public static String removeCharAuthorName(String strAuthorData)
    {
        String strVarAuthorDatraReturn = strAuthorData;
        
        if (strAuthorData.toLowerCase().indexOf("and ") >= 0)
        {
            strVarAuthorDatraReturn = strVarAuthorDatraReturn.replace("and ", "");
        }
        
        strVarAuthorDatraReturn = UtilFunctions.stringNormalizeAuthorName(strVarAuthorDatraReturn);
        return strVarAuthorDatraReturn;
    }
    
    public static void setStreamExtractAuthor (ClassArticleData objArticle, String varExtractAuthorConcat)
    {
        final Pattern pattern_start_author_split = Pattern.compile(AUTHOR_SPLIT_PATTERN, Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
        final Matcher matcher_start_author_split = pattern_start_author_split.matcher(varExtractAuthorConcat);
        varExtractAuthorConcat = varExtractAuthorConcat.trim();
        
        if (varExtractAuthorConcat.length() > 0 && varExtractAuthorConcat.isEmpty() == false)
        {
            if (varExtractAuthorConcat.indexOf(",") > 0)

            {
                String tempArrayAuthors[] = varExtractAuthorConcat.split(",");
             
                for(int i=0; i<tempArrayAuthors.length; i++)
                {
                    String tempStringAuthor = tempArrayAuthors[i].trim().toLowerCase();
                    
                    if (tempStringAuthor.indexOf(" and ") > 0)
                    {
                         String tempArrayAuthors2[] = tempStringAuthor.split(" and ");
                
                        for(int j=0; j<tempArrayAuthors2.length; j++)
                        {
                            String tempStringAuthor2 = tempArrayAuthors2[j].trim().toLowerCase();
                            if (verifyAuthorName(tempStringAuthor2) == true)
                            {
                                tempStringAuthor2 = removeCharAuthorName(tempStringAuthor2);
                                objArticle.getAuthorsArray().add(tempStringAuthor2);
                            }
                        }
                        
                    }
                    else
                    {
                        if (verifyAuthorName(tempStringAuthor) == true)
                        {
                            tempStringAuthor = removeCharAuthorName(tempStringAuthor);
                            objArticle.getAuthorsArray().add(tempStringAuthor);
                        }
                    }
                }
            }
            else if (varExtractAuthorConcat.indexOf(";") > 0)
            {
                String tempArrayAuthors[] = varExtractAuthorConcat.split(";");
                
                for(int i=0; i<tempArrayAuthors.length; i++)
                {
                    String tempStringAuthor = tempArrayAuthors[i].trim().toLowerCase();

                    if (tempStringAuthor.indexOf(" and ") > 0)
                    {
                         String tempArrayAuthors2[] = tempStringAuthor.split(" and ");
                
                        for(int j=0; j<tempArrayAuthors2.length; j++)
                        {
                            String tempStringAuthor2 = tempArrayAuthors2[j].trim().toLowerCase();
                            if (verifyAuthorName(tempStringAuthor2) == true)
                            {
                                tempStringAuthor2 = removeCharAuthorName(tempStringAuthor2);
                                objArticle.getAuthorsArray().add(tempStringAuthor2);
                            }
                        }
                        
                    }
                    else
                    {
                        if (verifyAuthorName(tempStringAuthor) == true)
                        {
                            tempStringAuthor = removeCharAuthorName(tempStringAuthor);
                            objArticle.getAuthorsArray().add(tempStringAuthor);
                        }
                    }
                }
            }
            else if (varExtractAuthorConcat.indexOf("  ") > 0)
            {
                String tempArrayAuthors[] = varExtractAuthorConcat.split("  ");
                
                for(int i=0; i<tempArrayAuthors.length; i++)
                {
                    String tempStringAuthor = tempArrayAuthors[i].trim().toLowerCase();

                    if (verifyAuthorName(tempStringAuthor) == true)
                    {
                        tempStringAuthor = removeCharAuthorName(tempStringAuthor);
                        objArticle.getAuthorsArray().add(tempStringAuthor);
                    }
                }
            }
            
            /*
            else if (matcher_start_author_split.find())
            {
                String tempStringAuthor = matcher_start_author_split.group();
                
                if (tempStringAuthor.length() > 2)
                {
                    objArticle.getAuthorsArray().add(tempStringAuthor);
                }
                
                while (matcher_start_author_split.find())
                {
                    String tempStringAuthor = tempArrayAuthors[i].trim().toLowerCase();

                    if (tempStringAuthor.length() > 2)
                    {
                        objArticle.getAuthorsArray().add(tempStringAuthor);
                    }
                }
            }
            
            
            else if (varExtractAuthorConcat.indexOf(" and ") > 0)
            {
                String tempArrayAuthors[] = varExtractAuthorConcat.split(" and ");
                
                for(int i=0; i<tempArrayAuthors.length; i++)
                {
                    String tempStringAuthor = tempArrayAuthors[i].trim().toLowerCase();

                    if (verifyAuthorName(tempStringAuthor) == true)
                    {
                        tempStringAuthor = removeCharAuthorName(tempStringAuthor);
                        objArticle.getAuthorsArray().add(tempStringAuthor);
                    }
                }
            }
            */
            // Single Author
            else
            {
                String tempStringAuthor = varExtractAuthorConcat;
                
                if (tempStringAuthor.indexOf(" and ") > 0)
                {
                     String tempArrayAuthors2[] = tempStringAuthor.split(" and ");

                    for(int j=0; j<tempArrayAuthors2.length; j++)
                    {
                        String tempStringAuthor2 = tempArrayAuthors2[j].trim().toLowerCase();
                        if (verifyAuthorName(tempStringAuthor2) == true)
                        {
                            tempStringAuthor2 = removeCharAuthorName(tempStringAuthor2);
                            objArticle.getAuthorsArray().add(tempStringAuthor2);
                        }
                    }

                }
                else
                {
                    objArticle.getAuthorsArray().add(tempStringAuthor.trim().toLowerCase());
                }
                
            }
        }
        else
        {
            varExtractAuthorConcat = null; // Author variable is empty
        }
            
            
    }
    
    public static void mainInit(ClassArticleData objArticle) throws Exception
    {
        ArticleVariablesInitializer();
        
        
        PDDocument pdDoc = null;
        pdDoc = PDDocument.load( objArticle.getInputDocFile() );

        PDFTextStripper stripper = new PDFTextStripper();
        stripper.setSortByPosition( true );
        stripper.setSpacingTolerance((float)0.2); // useless
            
        ArticleStream printer = new ArticleStream();
        printer.setSpacingTolerance((float)0.2);

        List allPages = pdDoc.getDocumentCatalog().getAllPages();
                
        for( int i=0; i<intEndPage; i++ )
        {
            PDPage page = (PDPage)allPages.get( i );
            //System.out.println( "Processing page: " + i );
            PDStream contents = page.getContents();
      
            //System.out.print(" page.findResources()= " + page.findResources());

            if( contents != null )
            {
                printer.processStream( page, page.findResources(), page.getContents().getStream() );
                
            }

        }
        
        /*
        System.out.print(" arrayBiggestFont.size(): " + arrayBiggestFont.size());
        
        if (charBiggestFontSizeOcurrence < minTitleLength)
        {
            System.out.print("&&&&&&&&&&");    
            for(int i=0; i<arrayBiggestFont.size(); i++)
            {
                System.out.print(i + "  " + arrayBiggestFont.get(i).getCharBiggestFontSize() + "_" + arrayBiggestFont.get(i).getCharBiggestFontSizeOcurrence());
                if(arrayBiggestFont.get(i).getCharBiggestFontSizeOcurrence() > minTitleLength)
                {
                    charBiggestFontSize = arrayBiggestFont.get(i).getCharBiggestFontSize();
                    charBiggestFontSizeBase = arrayBiggestFont.get(i).getCharBiggestFontSizeBase();
                    charBiggestFontSizeYLimit = arrayBiggestFont.get(i).getCharBiggestFontSizeYLimit();
                    // END LOOP
                    i = 0;
                                    
                }
            }
        }
        
        System.out.print(" charBiggestFontSizeOcurrence: " + charBiggestFontSizeOcurrence);
        System.out.print(" objBiggestFont.size(): " + arrayBiggestFont.size());
        */
        
        //System.out.print(" specialCharCounter: " + specialCharCounter);
        //System.out.print(" charFontInfo.size(): " + charFontInfo.size() );
       
        //System.out.print(" charBiggestFontSize: " + charBiggestFontSize);
        
         // specialCharCounter > 500; then Unrecognized PDF
         // charFontInfo.size() == 0; then PDF image
        if (specialCharCounter < 500 && charFontInfo.size() > 500)
        {
                //System.out.print(" title= ");
                // TITLE
                 // MELHORAR FOR, NAO PRECISA IR ATE FINAL
                // TEM QUE IR ATE O FINAL POR CAUSA DE UM ARTIGO LAZARENTO QUE POS O TITLE NO FINAL
                for(charIterator=0; charIterator<charFontInfo.size(); charIterator++)
                {
                    try
                    {

                            //System.out.println(" charBiggestFontSizeYLimit: " + charBiggestFontSizeYLimit);
                            //System.out.println(" charFontInfo.get(charIterator).getYDirAdj(): " + charFontInfo.get(charIterator).getYDirAdj());
                        
                            if( charFontInfo.get(charIterator).getFont().getBaseFont().equals(charBiggestFontSizeBase)
                                 && charFontInfo.get(charIterator).getYDirAdj() < charBiggestFontSizeYLimit
                                 && charFontInfo.get(charIterator).getFontSizeInPt() <= charBiggestFontSize+errorLimit
                                 && charFontInfo.get(charIterator).getFontSizeInPt() >= charBiggestFontSize-errorLimit 
                                    )
                            {
                                 //System.out.print(charFontInfo.get(charIterator).getCharacter());
                                 charTitleData+=charFontInfo.get(charIterator).getCharacter();
                                 charTitleYAxis = charFontInfo.get(charIterator).getYDirAdj();
                                 
                                 if (charTitleInitIndex == 0)
                                    charTitleInitIndex = charIterator;
                                 
                                 
                                 // PDFBox White Space Detection Problem
                                if(whiteSpaceCounter < whiteSpaceMinLimit)
                                {     
                                    String varCharReturn = bugfixPDFBoxWhiteSpace(charIterator);
                                    
                                    charTitleData += varCharReturn;
                                    /*
                                    //if(charFontInfo.get(charIterator+1).getFontSizeInPt() == charBiggestFontSize)
                                    {

                                        if ((charFontInfo.get(charIterator+1).getXDirAdj() ) > 
                                                (charFontInfo.get(charIterator).getXDirAdj() + charFontInfo.get(charIterator).getWidth() + errorLimit)  
                                                || (charFontInfo.get(charIterator+1).getYDirAdj() - charFontInfo.get(charIterator).getYDirAdj()) >  YDirAdjLimit - errorLimit 
                                                ) 

                                        {
                                            System.out.print(" ");
                                            charTitleData += " ";
                                        }
                                    }
                                    */

                                }    
                                
                                
                                if (charAuthorInitIndex == 0)
                                    charAuthorInitIndex = charIterator;

                                charTitleEndIndex = charIterator;
                                
                                // FUNTION TO FINALIZE THE TITLE SEARCH = END LOOP
                                
                                if (
                                        charFontInfo.get(charIterator+1).getXDirAdj() < charFontInfo.get(charIterator).getXDirAdj()
                                        &&  charFontInfo.get(charIterator+1).getFontSizeInPt() != charFontInfo.get(charIterator).getFontSizeInPt() // FIX A PROBLEM WHERE THE TITLE IS NOT ENDING PROPERLY WHEN IT HAS A CARRIAGE KEY INSIDE
                                      && ((charFontInfo.get(charIterator+1).getYDirAdj()) > (charFontInfo.get(charIterator).getYDirAdj() + charFontInfo.get(charIterator).getYScale()) + charFontInfo.get(charIterator).getHeight() - errorLimit)
                                        )
                                    charIterator = charFontInfo.size();

                            }
                    }
                    catch (Exception e)
                    {
                        //System.err.println(e.getMessage());
                    }
                      
                }

                
                charIterator = charTitleEndIndex;

                /*
                while (charFontInfo.get(charIterator).getCharacter().equals(" "))
                {
                    charIterator++;
                }

                charSubTitleInitIndex = charIterator;
                charSubTitleYAxis = charFontInfo.get(charIterator).getYDirAdj();
                charSubTitleFontSize = charFontInfo.get(charIterator).getFontSizeInPt();
                */
                //System.out.print(" charSubTitleYAxis: " + charSubTitleYAxis + "#");
               // System.out.print(" charSubTitleFontSize: " + charSubTitleFontSize + "#");

                // SECOND LINE = AUTHOR; SUBTITLE IS NULL ;
                /*
                if ( charFontInfo.get(charSubTitleInitIndex-1).getCharacter().equals(stripper.getWordSeparator()) && 
                        charFontInfo.get(charSubTitleInitIndex).getCharacter().equals(stripper.getWordSeparator()) || 
                        (charFontInfo.get(charSubTitleInitIndex-1).getYScale() - charFontInfo.get(charSubTitleInitIndex).getYScale() >= YScaleLimit ) ||
                         (charFontInfo.get(charSubTitleInitIndex).getYDirAdj()- charFontInfo.get(charSubTitleInitIndex-1).getYDirAdj() >= YDirAdjLimit ))     
                */    
                {

                    // empty newline
                    //System.out.print(" subtitle=null ");
                    
                    //System.out.print(" whiteSpaceCounter: " + whiteSpaceCounter);

                    // FIXING WHITE SPACE PROBLEMS
                    if(whiteSpaceCounter < whiteSpaceMinLimit || !charFontInfo.get(charIterator+1).getCharacter().equals(" ")) 
                        charIterator++;
                    else if ((int)charFontInfo.get(charIterator).getCharacter().charAt(0) > 48 && (int)charFontInfo.get(charIterator).getCharacter().charAt(0) < 122 )
                        charIterator++;
                    
                    //leonardo.m
                    //fixing unknow chars
                    // (int)charFontInfo.get(charIterator).getCharacter().charAt(0) > 127)
                    while(
                            charFontInfo.get(charIterator).getCharacter().equals(" ")
                            || ((int)charFontInfo.get(charIterator).getCharacter().charAt(0) > 127)
                            //|| ((int)charFontInfo.get(charIterator).getCharacter().charAt(0) == 32) 
                            )
                    {    
                        charIterator++;
                    }
                    
                    charAuthorInitIndex = charIterator;
                    
                    // Fixing (invited paper)
                    if ((charFontInfo.get(charIterator).getCharacter().equalsIgnoreCase("(") ))
                    {
                        while (!charFontInfo.get(charIterator).getCharacter().equals(")"))
                        {
                            charIterator++;
                        }
                        charIterator++;
                    }
                    else if (
                                (charFontInfo.get(charIterator).getCharacter().equalsIgnoreCase("i") )
                                && (charFontInfo.get(charIterator+1).getCharacter().equalsIgnoreCase("n") )
                                && (charFontInfo.get(charIterator+2).getCharacter().equalsIgnoreCase("v") )
                                && (charFontInfo.get(charIterator+3).getCharacter().equalsIgnoreCase("i") )
                                && (charFontInfo.get(charIterator+4).getCharacter().equalsIgnoreCase("t") )
                                && (charFontInfo.get(charIterator+5).getCharacter().equalsIgnoreCase("e") )
                                && (charFontInfo.get(charIterator+6).getCharacter().equalsIgnoreCase("d") )
                            )
                    {
                        while (!charFontInfo.get(charIterator).getCharacter().equalsIgnoreCase("r"))
                        {
                            charIterator++;
                        }
                        charIterator++;
                    }
                    
                    /*
                    //FIXING STREAM SORT BUG
                    for(charIterator2=charAuthorInitIndex; charIterator2<(charAuthorInitIndex+100); charIterator2++)
                        {
                            if (charFontInfo.get(charIterator2).getYDirAdj() < charFontInfo.get(charAuthorInitIndex).getYDirAdj()
                                    && charFontInfo.get(charIterator2).getYDirAdj() > charFontInfo.get(charTitleEndIndex).getYDirAdj()
                                    && charFontInfo.get(charIterator2).getFontSizeInPt() >= charFontInfo.get(charAuthorInitIndex).getFontSizeInPt())
                            {
                                charIterator = charIterator2;
                                charAuthorInitIndex = charIterator2;
                                
                                // END LOOP
                                charIterator2 = charAuthorInitIndex+100;
                                 System.out.print("**************");
                            }   
                        }
                     */   
                    charAuthorYAxis = charFontInfo.get(charIterator).getYDirAdj();
                    charAuthorFontSize = charFontInfo.get(charIterator).getFontSizeInPt();
                    charAuthorTextHeight = charFontInfo.get(charIterator).getHeight();
                    charAuthorFontName = charFontInfo.get(charIterator).getFont().getBaseFont();
                    
                    
                    //System.out.print(" author= " + (int)charFontInfo.get(charAuthorInitIndex).getCharacter().charAt(0));
                    //System.out.print(" author= " + (int)charFontInfo.get(charAuthorInitIndex+1).getCharacter().charAt(0));
                    //System.out.print(" author= " + (int)charFontInfo.get(charAuthorInitIndex+2).getCharacter().charAt(0));
                    String varCharReturn = "";
                    // MELHORAR FOR, NAO PRECISA IR ATE FINAL
                    for(charIterator=charIterator; charIterator<charFontInfo.size(); charIterator++)
                    {
                        //if (charFontInfo.get(charIterator).getYDirAdj() == charAuthorYAxis-errorLimit)
                        //    charDistanceXDiff = charFontInfo.get(charIterator).getXDirAdj() - charFontInfo.get(charIterator+1).getXDirAdj();
                        if (charFontInfo.get(charIterator).getYDirAdj() >= charAuthorYAxis-errorLimit 
                            && charFontInfo.get(charIterator).getYDirAdj() <= charAuthorYAxis+errorLimit)
                        {
                            
                            // Insert comma for large spaces between authors names
                            //if (lastCharXAdj != 0 && (charFontInfo.get(charIterator).getXDirAdj() - lastCharXAdj > 30+errorLimit))
                            if (lastCharXAdj != 0 && (charFontInfo.get(charIterator).getXDirAdj() - lastCharXAdj > 26+errorLimit))
                            {
                                charAuthorData += ", ";
                            }
                            
                            //System.out.print(charFontInfo.get(charIterator).getCharacter());

                            {
                                charAuthorData+=charFontInfo.get(charIterator).getCharacter();
                                 
                                //System.out.println(charFontInfo.get(charIterator).getCharacter());
                                //System.out.println((int)charFontInfo.get(charIterator).getCharacter().charAt(0));
                            }

                            // PDFBox White Space Detection Problem
                            if(whiteSpaceCounter < whiteSpaceMinLimit)
                            {     
                                varCharReturn = bugfixPDFBoxWhiteSpace(charIterator);

                                charAuthorData += varCharReturn;
                            }
                            
                            // Insert comma for large spaces between authors names
                            lastCharXAdj = charFontInfo.get(charIterator).getXDirAdj();
                            charIteratorAuthor = charIterator; 

                        }
                       
                        //leonardo.m
                        //fixing unknow chars between authors. example: "†"
                        else
                        {
                            //System.out.print(charFontInfo.get(charIterator).getCharacter()); 
                            charAuthorData += "  ";
                        }
                        
                    }
                }
                // ************* AUTHOR SECOND LINE 
                try
                {
                     if (charFontInfo.get(charIteratorAuthor-2).getCharacter().equalsIgnoreCase(",") 
                       || charFontInfo.get(charIteratorAuthor-1).getCharacter().equalsIgnoreCase(",")
                       || charFontInfo.get(charIteratorAuthor).getCharacter().equalsIgnoreCase(",")
                       || charFontInfo.get(charIteratorAuthor+1).getCharacter().equalsIgnoreCase(","))
                    {
                        
                         charAuthorData = charAuthorData.trim();
                          lastCharXAdj = 0;

                        //System.out.print(charFontInfo.get(charIteratorAuthor).getCharacter());
                        // next line
                        while (charFontInfo.get(charIteratorAuthor).getYDirAdj() == charAuthorYAxis)
                        {
                            charIteratorAuthor++;
                        }
                        String varCharReturn = "";
                        charAuthorSecLnYAxis = charFontInfo.get(charIteratorAuthor).getYDirAdj();

                        // get line text
                        for(charIteratorAuthor=charIteratorAuthor; charIteratorAuthor<charFontInfo.size(); charIteratorAuthor++)
                        {
                            if ( 
                                (charFontInfo.get(charIteratorAuthor).getYDirAdj() >= charAuthorSecLnYAxis-errorLimit 
                                && charFontInfo.get(charIteratorAuthor).getYDirAdj() <= charAuthorSecLnYAxis+errorLimit
                                && charFontInfo.get(charIteratorAuthor).getFontSizeInPt() == charAuthorFontSize)
                                )
                            {
                                
                                
                                // Insert comma for large spaces between authors names
                                //if (lastCharXAdj != 0 && (charFontInfo.get(charIterator).getXDirAdj() - lastCharXAdj > 30+errorLimit))
                                if (lastCharXAdj == 0 || (charFontInfo.get(charIteratorAuthor).getXDirAdj() - lastCharXAdj > 26+errorLimit))
                                {
                                    charAuthorData += ",";
                                }
                                //System.out.println(charFontInfo.get(charIteratorAuthor).getCharacter());
                                //System.out.println((int)charFontInfo.get(charIteratorAuthor).getCharacter().charAt(0));
                                 
                                {
                                    charAuthorData += charFontInfo.get(charIteratorAuthor).getCharacter();
                                }
                                    // PDFBox White Space Detection Problem
                                if(whiteSpaceCounter < whiteSpaceMinLimit)
                                {     
                                    varCharReturn = bugfixPDFBoxWhiteSpace(charIteratorAuthor);

                                    charAuthorData += varCharReturn;
                                }
                                
                                // Insert comma for large spaces between authors names
                                lastCharXAdj = charFontInfo.get(charIteratorAuthor).getXDirAdj();

                            }
                        }
                    }
                    
                }
                catch (Exception e)
                {
                     //System.err.println(e.getMessage());
                }
               
                /*
                System.out.println((int)charAuthorFontSize);
                System.out.println(charAuthorFontName);
                System.out.println(charFontInfo.get(charIteratorAuthor).getCharacter());
                */
                
                // ************* AUTHOR ANOTHER LINE BOLD
                try
                {
                    // Identify Bold 
                    if (charAuthorFontName.toLowerCase().contains("bold"))
                    {
                        
                        charAuthorData = charAuthorData.trim();
                        lastCharXAdj = 0;
                        
                        for(int charIteratorAuthor2=charIteratorAuthor; charIteratorAuthor2<charIteratorAuthor+200; charIteratorAuthor2++)
                        {
                            /*
                            System.out.println(charFontInfo.get(charIteratorAuthor).getCharacter());
                            System.out.println(charFontInfo.get(charIteratorAuthor).getFont().getBaseFont());
                            System.out.println((int)charFontInfo.get(charIteratorAuthor).getFontSizeInPt());
                            */
                            
                            if (charFontInfo.get(charIteratorAuthor2).getFont().getBaseFont().equalsIgnoreCase(charAuthorFontName)
                                    && charFontInfo.get(charIteratorAuthor2).getFontSizeInPt() == charAuthorFontSize)
                            {
                                
                                //System.out.print(charFontInfo.get(charIteratorAuthor).getCharacter());
                                
                                
                                // Insert comma for large spaces between authors names
                                //if (lastCharXAdj != 0 && (charFontInfo.get(charIterator).getXDirAdj() - lastCharXAdj > 30+errorLimit))
                                if (lastCharXAdj == 0 || (charFontInfo.get(charIteratorAuthor2).getXDirAdj() - lastCharXAdj > 26+errorLimit))
                                {
                                    charAuthorData += ",";
                                }

                                charAuthorData += charFontInfo.get(charIteratorAuthor2).getCharacter();
                                
                                String varCharReturn = "";
                                // PDFBox White Space Detection Problem
                                if(whiteSpaceCounter < whiteSpaceMinLimit)
                                {     
                                    varCharReturn = bugfixPDFBoxWhiteSpace(charIteratorAuthor2);

                                    charAuthorData += varCharReturn;
                                }
                                
                                // Insert comma for large spaces between authors names
                                lastCharXAdj = charFontInfo.get(charIteratorAuthor2).getXDirAdj();
                                
                            }
                        
                        }
                        
                    }
            
                }
                catch (Exception e)
                {
                   //System.err.println(e.getMessage()); 
                }
                
                
                // SECOND LINE = SUBTITLE; THIRD LINE = AUTHOR;
                /*
                else
                {
                    System.out.print(" subtitle= "); 
                    charIterator = charSubTitleInitIndex;
                    while(charFontInfo.get(charIterator).getFontSizeInPt() == charSubTitleFontSize)
                    {
                        System.out.print(charFontInfo.get(charIterator).getCharacter());
                        charIterator++;
                        charSubTitleData+=charFontInfo.get(charIterator).getCharacter();
                    }
                }
                */
                
               /*
                if ( 
                   charFontInfo.get(charAuthorInitIndex).getXDirAdj() < maxStartTitleXDirAdj
                   && charFontInfo.get(charAuthorInitIndex).getYDirAdj() >= (minTitleYDirAdj)
                   && charFontInfo.get(charAuthorInitIndex).getYDirAdj() <= (firstCharYDirAdj+maxTitleYDirAdj)
                   && charTitleData.length() > minTitleLength
                   )    
               */ 
                // Changing reference to TitleInitIndex
                //System.out.print("charTitleData:" + charTitleData);
                
                if ( 
                        charFontInfo.get(charTitleInitIndex).getXDirAdj() < maxStartTitleXDirAdj
                        && charFontInfo.get(charTitleInitIndex).getYDirAdj() >= (minTitleYDirAdj)
                        && charFontInfo.get(charTitleInitIndex).getYDirAdj() <= (firstCharYDirAdj+maxTitleYDirAdj)
                        && charTitleData.length() > minTitleLength
                        )
                {
                    

                    objArticle.setDocExtractTitle(charTitleData);
                    //objArticle.setDocExtractSubTitle(charSubTitleData);
                    objArticle.setDocExtractAuthor(charAuthorData);    
                    // 1) Extract Keyword from article data
                    // 2) If Keyword from article data == null; then get keywords from metadata
                    // 3) If Keyword from metadata data has more ",;" than extract data; then get keywords from metadata
                    if (
                          objArticle.getDocMetaAuthor() != null && objArticle.getDocMetaAuthor() != "null" &&
                             !objArticle.getDocMetaAuthor().isEmpty() &&
                           (UtilFunctions.countStringOccurrency(objArticle.getDocMetaAuthor(), ",") >= (UtilFunctions.countStringOccurrency(charAuthorData, ",")+1)  
                            || UtilFunctions.countStringOccurrency(objArticle.getDocMetaAuthor(), ";") >= (UtilFunctions.countStringOccurrency(charAuthorData, ";")+1)
                            || UtilFunctions.countStringOccurrency(objArticle.getDocMetaAuthor(), " and ") >= (UtilFunctions.countStringOccurrency(charAuthorData, " and ")+1))  
                            && (!objArticle.getDocMetaTitle().equals("Layout 1"))
                         )
                    {

                            setStreamExtractAuthor(objArticle,objArticle.getDocMetaAuthor());
                             
                             //IF THE AUTHOR META INFORMATION IS RIGHT; THEN WE HAVE HIGH PROBABILITY OF TITLE META DATA ALSO BEING RIGHT
                             //TITLE
                            if (objArticle.getDocMetaTitle().length() > 5 )
                            {
                                //objArticle.setDocExtractTitle(objArticle.getDocMetaTitle());
                                objArticle.setDocExtractTitle(objArticle.getDocMetaTitleClean());
                            }
                    }
                    else
                    {
                        setStreamExtractAuthor(objArticle,charAuthorData);
                    }
                        
                }
                else
                {
                    System.out.print(" Unable to extract data from document. Get meta data ");
                    
                    //AUTHOR    
                    if (objArticle.getDocMetaAuthor() != null && objArticle.getDocMetaAuthor() != "null" )
                    {
                        if (objArticle.getDocMetaAuthor().length() > 5 )
                        {
                            setStreamExtractAuthor(objArticle,objArticle.getDocMetaAuthor());
                        }
                    }
                    else
                    {
                       objArticle.setDocExtractAuthor("null"); 
                    }
                    
                    //TITLE
                    if (objArticle.getDocMetaTitle() != null && objArticle.getDocMetaTitle() != "null" )
                    {
                        if (objArticle.getDocMetaTitle().length() > 5 )
                        {
                               //objArticle.setDocExtractTitle(objArticle.getDocMetaTitle());
                               objArticle.setDocExtractTitle(objArticle.getDocMetaTitleClean());
                        }
                    }
                    else
                    {
                        objArticle.setDocExtractTitle("null");
                    }
                }

        }
        else
        {
            System.out.print(" Unrecognized document. Get meta data ");
            
            //AUTHOR    
            if (objArticle.getDocMetaAuthor() != null && objArticle.getDocMetaAuthor() != "null" )
            {
                if (objArticle.getDocMetaAuthor().length() > 5 )
                    setStreamExtractAuthor(objArticle,objArticle.getDocMetaAuthor());
            }
            else
            {
               objArticle.setDocExtractAuthor("null"); 
            }

            //TITLE
            if (objArticle.getDocMetaTitle() != null && objArticle.getDocMetaTitle() != "null" )
            {
                if (objArticle.getDocMetaTitle().length() > 5 )
                {
                    //objArticle.setDocExtractTitle(objArticle.getDocMetaTitle());
                    objArticle.setDocExtractTitle(objArticle.getDocMetaTitleClean());
                }
            }
            else
            {
                objArticle.setDocExtractTitle("null");
            }
        }
        
      
        
        
        
        pdDoc.close();
        
    }

    /**
     * A method provided as an event interface to allow a subclass to perform
     * some specific functionality when text needs to be processed.
     *
     * @param text The text to be processed
     */
    @Override
    protected void processTextPosition( TextPosition text )
    {
       
        if (//!text.getCharacter().equals(" ") && 
                
                text.getFontSizeInPt() >= charBiggestFontSize
                && text.getXDirAdj() > minStartTitleXDirAdj
                && text.getXDirAdj() < maxStartTitleXDirAdj
                && text.getYDirAdj() > minTitleYDirAdj
                && text.getYDirAdj() < maxTitleYDirAdj
                && ( text.getCharacter().codePointAt(0) > 65 && text.getCharacter().codePointAt(0) < 90 
                    || text.getCharacter().codePointAt(0) > 97 && text.getCharacter().codePointAt(0) < 122) 
                //&& text.getYDirAdj() < lastIndexCharTitle
                ) 
                 //&& (text.getFont().getFontDescriptor().getFontName().toLowerCase().contains("bold") // This verification fails for several font types
        {
            
            objBiggestFont = new ClassBiggestFont();
            
            if(charBiggestFontSize == text.getFontSizeInPt())
            {
                charBiggestFontSizeOcurrence = charBiggestFontSizeOcurrence + 1;
               /*
                arrayBiggestFont.get(intTitleGlobalCounter-1).setCharBiggestFontSizeOcurrence(charBiggestFontSizeOcurrence);
                 System.out.print("  " + arrayBiggestFont.get(intTitleGlobalCounter-1).getCharBiggestFontSize() + "_" + arrayBiggestFont.get(intTitleGlobalCounter-1).getCharBiggestFontSizeOcurrence()); 
                System.out.println("%%%%%%% igual " + charBiggestFontSize);
                 */
            }
            else if (text.getFontSizeInPt() > charBiggestFontSize)
            {             
                
                charSecBiggestFontSize = charBiggestFontSize;
                charBiggestFontSize = text.getFontSizeInPt();
                charBiggestFontSizeBase = text.getFont().getBaseFont();
                //charBiggestFontSizeYLimit = text.getYDirAdj()+(text.getYScale()*3);
                charBiggestFontSizeYLimit = text.getYDirAdj()+(text.getYScale()*4);
                charBiggestFontSizeOcurrence = 0;
                
                /*
                objBiggestFont.setCharBiggestFontSizeBase(charBiggestFontSizeBase);
                objBiggestFont.setCharBiggestFontSize(charBiggestFontSize);
                objBiggestFont.setCharBiggestFontSizeOcurrence(charBiggestFontSizeOcurrence);
                objBiggestFont.setCharBiggestFontSizeYLimit(charBiggestFontSizeYLimit);
                arrayBiggestFont.add(objBiggestFont);
                System.out.print(intTitleGlobalCounter + "  " + arrayBiggestFont.get(intTitleGlobalCounter).getCharBiggestFontSize() + "_" + arrayBiggestFont.get(intTitleGlobalCounter).getCharBiggestFontSizeOcurrence());
                intTitleGlobalCounter++;
                */

                //charBiggestFontSize = text.getFontSizeInPt();
                //charBiggestFontSizeBase = text.getFont().getBaseFont();
                //charBiggestFontSizeYLimit = text.getYDirAdj()+(text.getYScale()*3); // LIMITING TITLE VALID SPACE
                
                //charSecBiggestFontSize = charBiggestFontSize;
                 //   System.out.println("####### diferente " + charBiggestFontSize);
 
            }    
   
        }
        
        if (text.getCharacter().equals(" "))
            whiteSpaceCounter++;
        
        // Special characters
        if (text.getCharacter().codePointAt(0) < 32)
            specialCharCounter++;
        
        if (firstCharYDirAdj == 0
                && !text.getCharacter().equals(" ") 
                && (text.getCharacter().codePointAt(0) > 32)
                )
            firstCharYDirAdj = text.getYDirAdj();
        
        charFontInfo.add(text);

        
        /*
        try
        {
            System.out.println( "String[ xyAdj=" + text.getXDirAdj() + "," + text.getYDirAdj() + 
                    " xy= " + text.getX() + "," + text.getY() + " fs=" + text.getFontSizeInPt() +
                     " xscale=" + text.getXScale() + " yscale=" + text.getYScale() 
                    + " height=" + text.getHeightDir() + " width=" + text.getWidthDirAdj()
                     + " space=" + text.getWidthOfSpace() + " fontBase=" + text.getFont().getBaseFont()
                    + " fontType=" + text.getFont().getType()
                    + " fontFamily=" + text.getFont().getFontDescriptor().getFontFamily()
                    + " fontAllCap=" + text.getFont().getFontDescriptor().isAllCap()
                     + " fontFixedPitch=" + text.getFont().getFontDescriptor().isFixedPitch()
                     + " fontItalic=" + text.getFont().getFontDescriptor().isItalic()
                     + " fontNSymX=" + text.getFont().getFontDescriptor().isNonSymbolic()
                     + " fontIsScript=" + text.getFont().getFontDescriptor().isScript()
                    + " fontIsSerif=" + text.getFont().getFontDescriptor().isSerif()
                    + " fontIsSmallCap=" + text.getFont().getFontDescriptor().isSmallCap()
                    + " fontIsSym=" + text.getFont().getFontDescriptor().getFontBoundingBox()
                    + " bold=" + text.getFont().getFontDescriptor().isForceBold() 
                    + " dir=" + text.getDir() + " diacritic=" + text.isDiacritic()
                    + "]" 
                    + text.getCharacter());
        }
        catch (Exception e)
        {
            System.err.println(e.getMessage());
        }
        */
        
     }

    
}
