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
 
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
 

/**
 *
 * @author leo
 */
public class JSoupGetLinks {

    /**
     * @param args the command line arguments
     */
    public static void JSoupGetLinksFunction() {
        Document doc;
	try {
 
            for (int i=0; i<874; i++)
            {
		// need http protocol
		
                // Computer and information sciences -> Network Analysis
                /* 
                doc = Jsoup.connect("http://www.plosone.org/browse/Network+analysis?startPage="+i+"&filterAuthors=&filterSubjectsDisjunction=&filterArticleTypes=&pageSize=13&filterKeyword=&filterJournals=PLoSONE&query=&ELocationId=&id=&resultView=list&sortValue=&unformattedQuery=*%3A*&sortKey=Most+views%2C+all+time&filterSubjects=Network%20analysis&volume=&")
                        .userAgent("Mozilla").get();
                */
                
                // Computer and information sciences 
                
                doc = Jsoup.connect("http://www.plosone.org/browse/Computer+and+information+sciences?startPage="+i+"&filterAuthors=&filterSubjectsDisjunction=&filterArticleTypes=&pageSize=13&filterKeyword=&filterJournals=PLoSONE&query=&ELocationId=&id=&resultView=list&sortValue=&unformattedQuery=*%3A*&sortKey=&filterSubjects=Computer%20and%20information%20sciences&volume=&")
                        .header("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
                        .header("Accept-Encoding","gzip,deflate,sdch")
                        .header("Accept-Language","pt-BR,pt;q=0.8,en-US;q=0.5,en;q=0.3")
                        .header("Connection","keep-alive")
                        .header("Host","scholar.google.com.br")
                        .header("Referer","https://scholar.google.com.br/")
                        .timeout(720000)
                        .header("User-Agent","Mozilla/5.0 (Windows NT 6.1; WOW64; rv:38.0) Gecko/20100101 Firefox/38.0")
                        .get();
                
		// get page title
		//String title = doc.title();
		//System.out.println("title : " + title);
 
		// get all links
		Elements links = doc.select("a[href]");
		for (Element link : links) 
                {
                        /*
                        if (link.attr("href").contains("/article/info") && !link.text().contains("Full Text"))
                        {
                            System.out.print("text : " + link.text());  
                        }
                        */
                    
                        if (link.text().contains("Download PDF"))
                        {
                            // get the value from href attribute
                            //System.out.print("\nlink : " + link.attr("href"));
                           // System.out.println("");  
                           // System.out.println("");  
                            
                            String str_temp_article_link = link.attr("href").toString();
                            
                            Writer out = new BufferedWriter(new OutputStreamWriter(
                            new FileOutputStream("plos-one-network-analysis.txt", true), "ISO-8859-1"));
                            try 
                            {
                                
                                str_temp_article_link = str_temp_article_link.replaceAll("PDF", "XML");
                                out.write("http://www.plosone.org");
                                out.write(str_temp_article_link);
                                out.write("\n");
                            }
                            catch (Exception ex)
                            {
                                System.out.println("[error] output file cannot be write");
                                System.out.println(ex.getMessage());                      
                            } 
                            finally {
                                out.close();
                                System.out.println(str_temp_article_link + " [OutputWrite: ok]");
                            }
                        
                        }

			
            }            
		}
 
	} catch (IOException e) {
		e.printStackTrace();
	}
    }
    
}
