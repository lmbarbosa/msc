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
public class JSoup {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        // Extract all XML paths to download
        //JSoupGetLinks.JSoupGetLinksFunction();
        
        // Check HTML pages
        //JSoupParseHTML.JSoupPartHTML_PlosOne_CheckFiles();
        
        // Extract metrics from HTML pages
        JSoupParseHTML.JSoupParseHTML_PlosOne();
    }
    
}
