/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ArticleExtraction;

import java.awt.Rectangle;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.util.PDFTextStripperByArea;

/**
 *
 * @author leo
 */
public class ArticleStripperByArea {
    
    static PDDocument document = null;
    static int pageHeight = 0;
    static int pageWidth = 0;
    private static String YEAR_PATTERN = ArticleExtractionMain.getYEAR_PATTERN(); 
    private static String EMAIL_PATTERN = ArticleExtractionMain.getEMAIL_PATTERN(); 
    static String pageTopRectData = null;
    static String pageEndRectData = null;
    static String pageCoverRectData = null;
    static String pageCountry2RectData = null;
    static Matcher matcherTop = null;
    static Matcher matcherEnd = null;
    private static ArrayList<ClassCountryIsoDB> mainCountryIsoDBObj = ArticleExtractionMain.getCountryIsoDBArray();
    // stop at . or : or abstract or introduction or i
    //private static final String COUNTRY_END_PATTERN = "(abstract|keywords|introduction|\\r)";
    private static final String COUNTRY_END_PATTERN = "(abstract|keywords|introduction)";
    private static final String COUNTRY_END_PATTERN_2 = "(Digital Object Identifier| DOI )";
    
    public static void ArticleStripperByAreaInit ()
    {
        document = null;
        pageHeight = 0;
        pageWidth = 0;
        pageTopRectData = "";
        pageEndRectData = "null";
        pageCountry2RectData = "null";
        pageCoverRectData = "";
        matcherTop = null;
        matcherEnd = null;
    }
      
    public static void ArticleStripperByAreaFunc (ClassArticleData objArticle) throws IOException 
    {
                
        ArticleStripperByAreaInit ();
        
        document = PDDocument.load( objArticle.getInputDocFile() );
        List allPages = document.getDocumentCatalog().getAllPages();
        PDPage firstPage = (PDPage)allPages.get( 0 );
        PDRectangle mediaBox = firstPage.getMediaBox();
        
        // mediaBox does not work for some specif PDFs
        try
        {
            
            pageWidth = (int)mediaBox.getWidth();
            pageHeight = (int)mediaBox.getHeight();
        }
        catch (Exception e)
        {
             
            //System.err.println(e.getMessage());
            
            //Default Page Size Values
            pageWidth = 612 ;
            pageHeight = 792 ;
        }
        
        /*
        System.out.print( "Width:" + pageWidth );
        System.out.print( " / " );
        System.out.print( "Height:" + pageHeight );
        System.out.print( "  " );
        */
        
        //Extract Rectangle from Page Top
        try
        {
            PDFTextStripperByArea stripperPageTop = new PDFTextStripperByArea();
            stripperPageTop.setSortByPosition( true );
            //Rectangle rectPageTop = new Rectangle( 20, 20, pageWidth-20, 60 );
            Rectangle rectPageTop = new Rectangle( 20, 10, pageWidth-20, 70 );
            stripperPageTop.addRegion( "rectPageTop", rectPageTop );
            stripperPageTop.extractRegions( firstPage );
            pageTopRectData =  stripperPageTop.getTextForRegion( "rectPageTop" );
            //System.out.println( "stripperPageTop:" + rectPageTop );
            //System.out.println( pageTopRectData.length() );
        }
        catch (Exception e)
        {
            System.out.println( "[error] stripperPageTop" + e.getMessage()  );
        }
        
        //Extract Rectangle from Page End
        try
        {
            PDFTextStripperByArea stripperPageEnd = new PDFTextStripperByArea();
            stripperPageEnd.setSortByPosition( true );
            //Rectangle rectPageEnd = new Rectangle( 20, pageHeight-60, pageWidth-20, 40 );
            //Rectangle rectPageEnd = new Rectangle( 20, 792-60, pageWidth-20, 40 );
            //Rectangle rectPageEnd = new Rectangle( 20, 792-50, pageWidth-20, 50 );
            //Rectangle rectPageEnd = new Rectangle( 20, 792-40, 612-20, 76 );
            Rectangle rectPageEnd = new Rectangle( 20, 792-70, 612-20, 76 );
            stripperPageEnd.addRegion( "rectPageEnd", rectPageEnd );
            stripperPageEnd.extractRegions( firstPage );
            //System.out.println( "stripperPageEnd:" + rectPageEnd );
            pageEndRectData =  stripperPageEnd.getTextForRegion( "rectPageEnd" ); 
        }
        catch (Exception e)
        {
             System.out.println( "[error] stripperPageEnd" + e.getMessage()  );
        }
        //System.out.println( "pageEndRectData:" + pageEndRectData );
        
        final Pattern pattern = Pattern.compile(YEAR_PATTERN, Pattern.DOTALL);
        //final Pattern pattern = Pattern.compile(YEAR_PATTERN,Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
               
        matcherTop = pattern.matcher(pageTopRectData);
        matcherEnd = pattern.matcher(pageEndRectData);

        
        //if (pageTopRectData.length() > 10 && matcherTop.find( )) 
        if (pageTopRectData.length() > 4 && matcherTop.find( ))
        {
            objArticle.setDocExtractYear(Integer.parseInt(matcherTop.group()));
        }
        //else if (pageEndRectData.length() > 10 && matcherEnd.find( )) 
        else if (pageEndRectData.length() > 4 && matcherEnd.find()) 
        {
            objArticle.setDocExtractYear(Integer.parseInt(matcherEnd.group()));
        }   
        else
        {
            
            objArticle.setDocExtractYear(0);

        }
        
        // country2 rectangle
        // if the country information is not found at the article head, then search for the article first page down left
        // author information at the artcile down left
        try
        {
            PDFTextStripperByArea stripperCountry2 = new PDFTextStripperByArea();
            stripperCountry2.setSortByPosition( true );
            //Rectangle rectCountry2 = new Rectangle( 20, 792-360, 612-20, 300 );
            //Rectangle rectCountry2 = new Rectangle( 20, 792-380, 612-20, 300 );
            Rectangle rectCountry2 = new Rectangle( 20, 792-300, 612-20, 300 );
            stripperCountry2.addRegion( "rectCountry2", rectCountry2 );
            stripperCountry2.extractRegions( firstPage );
            pageCountry2RectData =  stripperCountry2.getTextForRegion( "rectCountry2" ); 
            //System.out.println( "pageCountry2RectData:" + pageCountry2RectData );
        }
        catch (Exception e)
        {
             System.out.println( "[error] stripperCountry2" + e.getMessage()  );
        }
        
        if (pageCountry2RectData.equals("null") )
        {
           try
           {
               PDFTextStripperByArea stripperCountry2 = new PDFTextStripperByArea();
               stripperCountry2.setSortByPosition( true );
               //Rectangle rectCountry2 = new Rectangle( 20, 792-360, 612-20, 300 );
               //Rectangle rectCountry2 = new Rectangle( 20, 792-380, 612-20, 300 );
               Rectangle rectCountry2 = new Rectangle( 20, 792-360, 612-20, 300 );
               stripperCountry2.addRegion( "rectCountry2", rectCountry2 );
               stripperCountry2.extractRegions( firstPage );
               pageCountry2RectData =  stripperCountry2.getTextForRegion( "rectCountry2" ); 
               //System.out.println( "pageCountry2RectData:" + pageCountry2RectData );
           }
           catch (Exception e)
           {
                System.out.println( "[error] stripperCountry2" + e.getMessage()  );
           }
        }
        
        if (pageCountry2RectData.equals("null") )
        {
           try
           {
               PDFTextStripperByArea stripperCountry2 = new PDFTextStripperByArea();
               stripperCountry2.setSortByPosition( true );
               //Rectangle rectCountry2 = new Rectangle( 20, 792-360, 612-20, 300 );
               //Rectangle rectCountry2 = new Rectangle( 20, 792-380, 612-20, 300 );
               Rectangle rectCountry2 = new Rectangle( 20, 792-380, 612-20, 300 );
               stripperCountry2.addRegion( "rectCountry2", rectCountry2 );
               stripperCountry2.extractRegions( firstPage );
               pageCountry2RectData =  stripperCountry2.getTextForRegion( "rectCountry2" ); 
               //System.out.println( "pageCountry2RectData:" + pageCountry2RectData );
           }
           catch (Exception e)
           {
                System.out.println( "[error] stripperCountry2" + e.getMessage()  );
           }
        }
        
        
        // ############################################ INFORMATION EXTRACTION (EMAIL, COUNTRY)
        try
        {
            // Extract Rectangle from Page Cover
            PDFTextStripperByArea stripperPageCover = new PDFTextStripperByArea();
            stripperPageCover.setSortByPosition( true );
            Rectangle rectPageCover = new Rectangle( 20, 60, pageWidth-20, 300 );
            stripperPageCover.addRegion( "rectPageCover", rectPageCover );
            stripperPageCover.extractRegions( firstPage );
            pageCoverRectData =  stripperPageCover.getTextForRegion( "rectPageCover" );
            //System.out.println( "pageCoverRectData:" + pageCoverRectData );
            //System.out.println( "stripperPageCover:" + rectPageCover );
            //System.out.println( pageCoverRectData.length() );
        }
        catch (Exception e)
        {
            System.out.println( "[error] pageCoverRectData" + e.getMessage()  );
        }
        
        if (pageCoverRectData.equals(""))
        {
            try
            {
                // Extract Rectangle from Page Cover
              PDFTextStripperByArea stripperPageCover = new PDFTextStripperByArea();
              stripperPageCover.setSortByPosition( true );
              //Rectangle rectPageCover = new Rectangle( 20, 60, pageWidth-20, 300 );
              Rectangle rectPageCover = new Rectangle( 20, 80, pageWidth-20, 300 );
              stripperPageCover.addRegion( "rectPageCover", rectPageCover );
              stripperPageCover.extractRegions( firstPage );
              pageCoverRectData =  stripperPageCover.getTextForRegion( "rectPageCover" );
              //System.out.println( "pageCoverRectData:" + pageCoverRectData );
              //System.out.println( "stripperPageCover:" + rectPageCover );
              //System.out.println( pageCoverRectData.length() );
            }
            catch (Exception e)
            {
                System.out.println( "[error] pageCoverRectData" + e.getMessage()  );
            }
        }
        
        // ############################################ EMAIL EXTRACTION
        final Pattern email_pattern = Pattern.compile(EMAIL_PATTERN, Pattern.DOTALL);
        final Matcher email_matcher = email_pattern.matcher(pageCoverRectData);
        String stringExtractEmailConcat = "";
        
        while (email_matcher.find( )) 
        {
            if (stringExtractEmailConcat.length() > 0)
                stringExtractEmailConcat += " , ";
            
            stringExtractEmailConcat += email_matcher.group();

            //System.out.println("Found value:" + email_matcher.group() );
            
        }
        if (stringExtractEmailConcat == "")
        {
            stringExtractEmailConcat = "";
            //stringExtractEmailConcat = "null";
        }
            
        objArticle.setDocExtractEmail(stringExtractEmailConcat);
        
        // ############################################ COUNTRY EXTRACTION

        String pageCoverRectDataCountryCut = pageCoverRectData;
        
        try
        {
             pageCoverRectData = pageCoverRectData.substring(60);
        }
        catch (Exception e)
        {
            System.out.println("[error] pageCoverRectData.substring(60)");
        }

        final Pattern pattern_country = Pattern.compile(COUNTRY_END_PATTERN, Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
        final Matcher matcher_country = pattern_country.matcher(pageCoverRectData);
        
        if(matcher_country.find())
        {
            pageCoverRectDataCountryCut = pageCoverRectData.substring(0, matcher_country.start());
        }
        
        //country2    
        String pageCoverRectDataCountryCut2 = pageCountry2RectData;
        
        final Pattern pattern_country2 = Pattern.compile(COUNTRY_END_PATTERN_2, Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
        final Matcher matcher_country2 = pattern_country.matcher(pageCountry2RectData);
        
        /*
        if(matcher_country2.find())
        {
            pageCoverRectDataCountryCut2 = pageCountry2RectData.substring(0, matcher_country2.start());
        }
        */
        
        // Debbuging purposes
        //System.out.println("pageCoverRectDataCountryCut:" + pageCoverRectDataCountryCut );
        //System.out.println("pageCoverRectDataCountryCut2:" + pageCoverRectDataCountryCut2 );

        // Country search
        // 1) Search by country name
        // 2) If the country name is not found, then search by country acronym/iso_alpha_code
        for(int i=0; i<mainCountryIsoDBObj.size(); i++)
        {

            final Pattern pattern_country_name1 = Pattern.compile("\\b" + mainCountryIsoDBObj.get(i).getCountry_name1() + "\\b", Pattern.CASE_INSENSITIVE);
            final Matcher matcher_country_name1 = pattern_country_name1.matcher(pageCoverRectDataCountryCut);
            
            final Pattern pattern_country_name2 = Pattern.compile("\\b" + mainCountryIsoDBObj.get(i).getCountry_name2() + "\\b");
            final Matcher matcher_country_name2 = pattern_country_name2.matcher(pageCoverRectDataCountryCut);
            
            final Pattern pattern_country_name3 = Pattern.compile("\\b" + mainCountryIsoDBObj.get(i).getCountry_name3() + "\\b");
            final Matcher matcher_country_name3 = pattern_country_name3.matcher(pageCoverRectDataCountryCut);
            
            final Pattern pattern_country_isocode3 = Pattern.compile("\\b" + mainCountryIsoDBObj.get(i).getCountry_iso_alphacode3() + "\\b");
            final Matcher matcher_country_isocode3 = pattern_country_isocode3.matcher(pageCoverRectDataCountryCut);
            
            if (
                    (mainCountryIsoDBObj.get(i).getCountry_name1().length() > 0 && matcher_country_name1.find())
                    || (mainCountryIsoDBObj.get(i).getCountry_name2().length() > 0 && matcher_country_name2.find())
                    || (mainCountryIsoDBObj.get(i).getCountry_name3().length() > 0 && matcher_country_name3.find())
                    || (mainCountryIsoDBObj.get(i).getCountry_iso_alphacode3().length() > 0 && matcher_country_isocode3.find())
               )
            {
                // Debbuging purposes
                /*
                System.out.println("i:" + i );
                System.out.println("getCountry_id:" + mainCountryIsoDBObj.get(i).getCountry_id() );
                System.out.println("getCountry_name1:" + mainCountryIsoDBObj.get(i).getCountry_name1() );
                System.out.println("getCountry_name2:" + mainCountryIsoDBObj.get(i).getCountry_name2() );
                System.out.println("getCountry_name3:" + mainCountryIsoDBObj.get(i).getCountry_name3() );
                System.out.println("getCountry_iso_alphacode2:" + mainCountryIsoDBObj.get(i).getCountry_iso_alphacode2() );
                System.out.println("getCountry_iso_alphacode3:" + mainCountryIsoDBObj.get(i).getCountry_iso_alphacode3() );  
                */
                objArticle.getCountryArray().add(i);
                objArticle.setCountryNameConcat(mainCountryIsoDBObj.get(i).getCountry_name1());
            }
        }
        
         // country2 rectangle
        if (objArticle.getCountryArray().size() == 0)
        {
            
            for(int i=0; i<mainCountryIsoDBObj.size(); i++)
            {

                final Pattern pattern_country_name1 = Pattern.compile("\\b" + mainCountryIsoDBObj.get(i).getCountry_name1() + "\\b", Pattern.CASE_INSENSITIVE);
                final Matcher matcher_country_name1 = pattern_country_name1.matcher(pageCoverRectDataCountryCut2);

                final Pattern pattern_country_name2 = Pattern.compile("\\b" + mainCountryIsoDBObj.get(i).getCountry_name2() + "\\b");
                final Matcher matcher_country_name2 = pattern_country_name2.matcher(pageCoverRectDataCountryCut2);

                final Pattern pattern_country_name3 = Pattern.compile("\\b" + mainCountryIsoDBObj.get(i).getCountry_name3() + "\\b");
                final Matcher matcher_country_name3 = pattern_country_name3.matcher(pageCoverRectDataCountryCut2);

                final Pattern pattern_country_isocode3 = Pattern.compile("\\b" + mainCountryIsoDBObj.get(i).getCountry_iso_alphacode3() + "\\b");
                final Matcher matcher_country_isocode3 = pattern_country_isocode3.matcher(pageCoverRectDataCountryCut2);

                if (
                        (mainCountryIsoDBObj.get(i).getCountry_name1().length() > 0 && matcher_country_name1.find())
                        || (mainCountryIsoDBObj.get(i).getCountry_name2().length() > 0 && matcher_country_name2.find())
                        || (mainCountryIsoDBObj.get(i).getCountry_name3().length() > 0 && matcher_country_name3.find())
                        || (mainCountryIsoDBObj.get(i).getCountry_iso_alphacode3().length() > 0 && matcher_country_isocode3.find()
                        && !(mainCountryIsoDBObj.get(i).getCountry_iso_alphacode3().equals("AND")))
                   )
                {
                    // Debbuging purposes
                    /*
                    System.out.println("i:" + i );
                    System.out.println("getCountry_id:" + mainCountryIsoDBObj.get(i).getCountry_id() );
                    System.out.println("getCountry_name1:" + mainCountryIsoDBObj.get(i).getCountry_name1() );
                    System.out.println("getCountry_name2:" + mainCountryIsoDBObj.get(i).getCountry_name2() );
                    System.out.println("getCountry_name3:" + mainCountryIsoDBObj.get(i).getCountry_name3() );
                    System.out.println("getCountry_iso_alphacode2:" + mainCountryIsoDBObj.get(i).getCountry_iso_alphacode2() );
                    System.out.println("getCountry_iso_alphacode3:" + mainCountryIsoDBObj.get(i).getCountry_iso_alphacode3() );  
                    */
                    objArticle.getCountryArray().add(i);
                    objArticle.setCountryNameConcat(mainCountryIsoDBObj.get(i).getCountry_name1());
                }
            
            }
        }
        
        document.close();
    }
}
