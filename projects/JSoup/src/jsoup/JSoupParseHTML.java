/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package jsoup;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.logging.Level;
import java.util.logging.Logger;
 
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
 
public class JSoupParseHTML {
    
    public static void JSoupPartHTML_PlosOne_CheckFiles()
    {
        String htmlFolder = "C:\\barbosalm\\Data\\html-phatomjs\\plos-one-computer-science\\arquive-2015-10-03\\" ;

        for (int i=0; i<=877; i++)
        {
            String strHTMLFile = htmlFolder+"startPage"+i+".html";
            File fileHTML = new File(strHTMLFile);
            
            try
            {
                Document doc = Jsoup.parse(fileHTML, "UTF-8", "");
            }
            catch (Exception ex) 
            {
                //System.out.println("[JSoupParseHTML: error]" + ex.getMessage() + strHTMLFile  );   
                System.out.print(i + ", ");
            }
            
        }
        
    }
    
    
    public static void JSoupParseHTML_PlosOne ()    
    {
        String htmlFolder = "C:\\barbosalm\\Data\\html-phatomjs\\plos-one-computer-science\\arquive-2015-10-03\\" ;
        Writer out = null;

        for (int i=0; i<=877; i++)
        {
            File fileHTML = new File(htmlFolder+"startPage"+i+".html");
   
            String str_article_id = "";
            String str_article_title = "";
            String str_article_views = "";
            String str_article_citations = "";
            String str_article_saves = "";
            String str_article_shares = "";

            try
            {
                Document doc = Jsoup.parse(fileHTML, "UTF-8", "");

                Elements elements_li_article = doc.select("div#subject-list-view > ul > li");
                Elements elements_div_article_title = doc.select("div#subject-list-view > ul > li > h2");
                Elements elements_span_metrics = doc.select("span.metrics");
                
                for (int j=0; j<elements_li_article.size(); j++)
                {
                    Element li_article_id = elements_li_article.get(j);

                    str_article_id = "\"" + li_article_id.attributes().get("data-doi").trim() + "\"";
                    //System.out.println(str_article_id);

                    Element article_div_title = elements_div_article_title.get(j);
                    str_article_title = "\"" + article_div_title.text().trim() + "\"";
                    //System.out.println(str_article_title);

                    Element article_span_metrics = elements_span_metrics.get(j);
                    if (article_span_metrics.text().contains("•"))
                    {
                        String[] strVectorMetrics = article_span_metrics.text().split("•") ;
                        str_article_views = "\"" + strVectorMetrics[0].replace(",", "").trim() + "\"";
                        str_article_citations = "\"" + strVectorMetrics[1].replace(",", "").trim() + "\"";
                        str_article_saves = "\"" + strVectorMetrics[2].replace(",", "").trim() + "\"";
                        str_article_shares = "\"" + strVectorMetrics[3].replace(",", "").trim() + "\"";       

                    }

                    try
                    {
                        out = new BufferedWriter(new OutputStreamWriter(
                        new FileOutputStream(htmlFolder + "plosone-metrics.csv", true), "ISO-8859-1"));

                        out.write(str_article_id);
                        out.write('\t');
                        out.write(str_article_title);
                        out.write('\t');
                        out.write(str_article_views);
                        out.write('\t');
                        out.write(str_article_citations);
                        out.write('\t');
                        out.write(str_article_saves);
                        out.write('\t');
                        out.write(str_article_shares);
                        out.write('\n');
                    }
                    catch (Exception ex)
                    {
                        System.out.println("[error] JSoupParseHTML output file cannot be write");
                        System.out.println(ex.getMessage());                      
                    } 
                    finally 
                    {
                        out.close();
                    }
                }
                
            }     
            catch (IOException ex) 
            {
                Logger.getLogger(JSoupParseHTML.class.getName()).log(Level.SEVERE, null, ex);
            }
            finally 
            {
                System.out.println("[JSoupParseHTML: ok]" + str_article_id + " - " + "startPage"+i+".html" );
            }
        }
        
    }
    
    
    
}
