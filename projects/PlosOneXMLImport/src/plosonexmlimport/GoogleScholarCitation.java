/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package plosonexmlimport;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 *
 * @author leo
 */
public class GoogleScholarCitation {
    
    public static String strGoogleScholarCitationLink = "https://scholar.google.com.br/scholar?q="; 
    public static String strGoogleScholarCitationLinkEnd = "&btnG=&hl=pt-BR&as_sdt=0%2C5&google_abuse=GOOGLE_ABUSE_EXEMPTION%3DID%3D53988dbbca548005:TM%3D1433688553:C%3Dc:IP%3D179.159.56.177-:S%3DAPGng0u1Xmac1MPmt9rK1CRHQI-BljHmMQ%3B+path%3D/%3B+domain%3Dgoogle.com%3B+expires%3DSun,+07-Jun-2015+17:49:13+GMT";
    public static Document doc;    
    
    public static String GoogleScholarCitationRetrieveDataFunction(String strArticleTitle)
    {

        String strArticleCitationValue = "null";
        
        try 
        {

            strArticleTitle = UtilFunctions.stringRemoveSpecialChars(strArticleTitle);
            
            String strGoogleScholarCitationLinkComplete = strGoogleScholarCitationLink + strArticleTitle + strGoogleScholarCitationLinkEnd;
            //System.out.println(strGoogleScholarCitationLinkComplete);

            String cookie = "PREF=ID=069832ba0f6d495b:U=c5fc6c14368d5c75:FF=0:TM=1432506410:LM=1433206117:S=mW_hdHTr2QBRcDQb; NID =68=j-V7M_xcliln6UyVdUQUW9WDdA4CEk_4IKp27I4kGU8LYQpaVdWWsoHwlwnOMo_cx-I9tyxD3cIte5AwUsKEN06D85c0W4xP A_9J4jWx5nWbs0DgGYlCyuOC6QJRhe0MVX2-YQzaO7PFgPiz6PCJonsBnPsEXpZssdGUToGDmNLd4AX7c7JHXBWBAr9mk52xZwswjSyJyBjEqCqtms6h98VqO0aOtdRTVmP1 ; GSP=A=6x468g:CPTS=1433648671:LM=1433648671:S=pNyYYBXRld8zuaqJ; SID=DQAAAAIBAAAD62OQM4BlIlPOzJZeEG2 9wLtwYGHk5zelnREKKz13MbGO8Qrkz6FPwEkOjAyRuOEc5kF6NlztXpQ03UwLUqJNtRVSWVunv_V7FS0HPb64W4cKle6GfFfSOln w2vfo3YYvHok2ar9b96aWArrmHtYLvVQiab9mCFAAhEit6c90DcUB6b4hgtXQKUqFl15b0OhIstV3IsO8sWotNceyiB234FGAYXH 7nbBYi-3y3TTMJmp75VZzb9hLgcC-N0VkpED2frC1dzyCq14vR8oXyVbbSyzTjEfn_7ix2cF4DwS6pgaxHmGnXzkO_wFOYSNGgKVXI9zLqhAbXGoKogsE-nXB-IyiGySFRjlxTaotCkYgaA ; HSID=A4nbEtvTcbRq7kKDU; SSID=AlGVViUKQs1bbdpws; APISID=0drGWYXb12NiUzNP/A5I1yonPHZiSd4xS7; SAPISID =JkRjpfnvVUwT3VmQ/A-WnsLYOkvX9vNxeS";
            
            Document doc = Jsoup.connect(strGoogleScholarCitationLinkComplete)
                .header("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
                .header("Accept-Encoding","gzip,deflate,sdch")
                .header("Accept-Language","pt-BR,pt;q=0.8,en-US;q=0.5,en;q=0.3")
                .header("Connection","keep-alive")
                .header("Cookie",cookie.split(";", 1)[0])
                .header("Host","scholar.google.com.br")
                .header("Referer","https://scholar.google.com.br/")
                .header("User-Agent","Mozilla/5.0 (Windows NT 6.1; WOW64; rv:38.0) Gecko/20100101 Firefox/38.0")
                //.header("X-Requested-With", "XMLHttpRequest")
                        //.cookie(genUrl(),cookie)
                .get();

            if (doc.data().toLowerCase().contains("robô"))
            {
                System.out.println("[error] Google Scholar Captcha!");
                System.exit(0);
            }
            
            Elements links = doc.select("a[href]");
            for (Element link : links) 
            {
                String strTempLinkTexts = link.text().toString();
                //System.out.println(strTempLinkTexts);
                if (strTempLinkTexts.contains("Citado por"))
                {   
                    strArticleCitationValue = strTempLinkTexts.replaceAll("Citado por", "").trim();
                }
            }
            
            
        }
        catch (IOException e) 
        {
            e.printStackTrace();
            System.out.println("[error] GoogleScholarCitation: " + e.getMessage());
        }
       
        return strArticleCitationValue;
    }
    
}
