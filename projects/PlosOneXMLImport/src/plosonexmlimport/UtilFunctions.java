/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package plosonexmlimport;

/**
 *
 * @author leo
 */
public class UtilFunctions {
    
    // Function to remove all quotes
    public static String stringCountryNameFix(String srtText) 
    {
        String srtTextReturn = srtText;
        
        if (srtText != null && srtText != "")
        {
             // Error hacking
             if (srtText.matches("(.*)?China(.*)?")){srtText = "China";}
             if (srtText.matches("(.*)?ROC(.*)?")){srtText = "China";}
             if (srtText.matches("(.*)?PRC(.*)?")){srtText = "China";}
             if (srtText.matches("(.*)?R.O.C.?(.*)?")){srtText = "China";}
             if (srtText.matches("(.*)?Beijing(.*)?")){srtText = "China";}
             if (srtText.matches("(.*)?Yangzhou(.*)?")){srtText = "China";}
             if (srtText.matches("(.*)?Guangxi(.*)?")){srtText = "China";}
             if (srtText.matches("(.*)?Shanghai(.*)?")){srtText = "China";}  
             if (srtText.matches("(.*)?Hong Kong SAR(.*)?")){srtText = "China";}
             if (srtText.matches("(.*)?Argentine(.*)?")){srtText = "Argentina";}
             if (srtText.matches("(.*)?Japan(.*)?")){srtText = "Japan";}
             if (srtText.matches("(.*)?Turkey(.*)?")){srtText = "Turkey";}
             if (srtText.matches("(.*)?Canada(.*)?")){srtText = "Canada";}
             if (srtText.matches("(.*)?Nova Scotia(.*)?")){srtText = "Canada";}
             if (srtText.matches("(.*)?Germany(.*)?")){srtText = "Germany";}
             if (srtText.matches("(.*)?Deutschland(.*)?")){srtText = "Germany";}
             if (srtText.matches("(.*)?Australia(.*)?")){srtText = "Australia";}
             if (srtText.matches("(.*)?Scotland(.*)?")){srtText = "United Kingdom";}
             if (srtText.matches("(.*)?England(.*)?")){srtText = "United Kingdom";}
             if (srtText.matches("(.*)?United Kii?n?g?dom(.*)?")){srtText = "United Kingdom";}
             if (srtText.matches("(.*)?United Kindgom")){srtText = "United Kingdom";}
             if (srtText.matches("(.*)?Northern Ireland(.*)?")){srtText = "United Kingdom";}
             if (srtText.matches("(.*)?Great Britain(.*)?")){srtText = "United Kingdom";}
             if (srtText.matches("(.*)?Wales(.*)?")){srtText = "United Kingdom";}
             if (srtText.matches("(.*)?European Union(.*)?")){srtText = "United Kingdom";}
             if (srtText.matches("(.*)?Bangkok(.*)?")){srtText = "Thailand";}
             if (srtText.matches("(.*)?Brazil(.*)?")){srtText = "Brazil";}
             if (srtText.matches("(.*)?USA(.*)?")){srtText = "United States of America";}
             if (srtText.matches("(.*)?U. S. A.(.*)?")){srtText = "United States of America";}
             if (srtText.matches("(.*)?Unik?ted States? ?o?f? ?Amer?ica?")){srtText = "United States of America";}
             if (srtText.matches("(.*)?Tennessee(.*)?")){srtText = "United States of America";}    
             if (srtText.matches("(.*)?States of America(.*)?")){srtText = "United States of America";}
             if (srtText.matches("(.*)?Hawaii(.*)?")){srtText = "United States of America";}
             if (srtText.matches("(.*)?North Carolina(.*)?")){srtText = "United States of America";}
             if (srtText.matches("(.*)?Florida(.*)?")){srtText = "United States of America";}
             if (srtText.matches("(.*)?Colorado(.*)?")){srtText = "United States of America";}
             if (srtText.matches("(.*)?Pennsylvania(.*)?")){srtText = "United States of America";}
             if (srtText.matches("(.*)?New Mexico(.*)?")){srtText = "United States of America";}
             if (srtText.matches("(.*)?Idaho(.*)?")){srtText = "United States of America";}
             if (srtText.matches("(.*)?Kansas(.*)?")){srtText = "United States of America";}
             if (srtText.matches("(.*)?Republic of Korea(.*)?")){srtText = "Republic of Korea";}
             if (srtText.matches("(.*)?Spain?(.*)?")){srtText = "Spain";}
             if (srtText.matches("(.*)?Universitat Pompeu Fabra?(.*)?")){srtText = "Spain";}
             if (srtText.matches("(.*)?Catalonia?(.*)?")){srtText = "Spain";}
             if (srtText.matches("(.*)?Rome?(.*)?")){srtText = "Italy";}
             if (srtText.matches("(.*)?Portugal(.*)?")){srtText = "Portugal";}
             if (srtText.matches("(.*)?Singapure(.*)?")){srtText = "Singapure";}
             if (srtText.matches("(.*)?Sweden(.*)?")){srtText = "Sweden";}
             if (srtText.matches("(.*)?Pakistan(.*)?")){srtText = "Pakistan";}
             if (srtText.matches("(.*)?Japan(.*)?")){srtText = "Japan";}
             if (srtText.matches("(.*)?Israel(.*)?")){srtText = "Israel";}
             if (srtText.matches("(.*)?Russia(.*)?")){srtText = "Russia";}
             if (srtText.matches("(.*)?Singapore(.*)?")){srtText = "Singapore";} 
             if (srtText.matches("(.*)?France(.*)?")){srtText = "France";} 
             if (srtText.matches("(.*)?Norway(.*)?")){srtText = "Norway";} 
              
             srtTextReturn = srtText.trim();
        }
        return srtTextReturn;
    }   
    
    // Function to remove all quotes
    public static String stringRemoveQuotes(String srtText) 
    {
        String srtTextReturn = srtText;
        
        if (srtText != null && srtText != "")
        {
             srtTextReturn = srtText.trim();
             srtTextReturn = srtTextReturn.replaceAll("\"", "");
             srtTextReturn = srtTextReturn.replaceAll("\\t", "");
             srtTextReturn = srtTextReturn.replaceAll("'", "");
             srtTextReturn = srtTextReturn.replaceAll("\\r\\n|\\r|\\n", " ");
             srtTextReturn = srtTextReturn.replaceAll("(\\s\\s+)", " ");
             srtTextReturn = srtTextReturn.replaceAll("“", "");
             srtTextReturn = srtTextReturn.replaceAll("”", "");
             srtTextReturn = srtTextReturn.replaceAll("‘", "");
             srtTextReturn = srtTextReturn.replaceAll("’", "");
        }
        return srtTextReturn;
    }   
    
    public static String stringRemoveSpecialChars (String strText)
    {
        String strTextReturn = strText;
        
        if (strText != null && strText != "")
        {
            strText = strText.replaceAll("[\\x00-\\x1F]", "");
            strText = strText.replaceAll("[\\x21-\\x2F]", "");
            strText = strText.replaceAll("[\\x3A-\\x40]", "");
            strText = strText.replaceAll("[\\x5B-\\x60]", "");
            strText = strText.replaceAll("[\\x7B-\\x7F]", "");
            strText = strText.replaceAll("[\\xA8-\\xFE]", "");
            strText = strText.replaceAll("\\s", "+");
            
            strTextReturn = strText;
        }
        
        return strTextReturn;
    }
    
    public static String stringCleanArticleMetrics (String strText)
    {
        String strTextReturn = strText;
        
        if (strText != null && strText != "")
        {
            strText = strText.replaceAll("�", "");
            strText = strText.replaceAll(",", "");
            strText = strText.replaceAll("(.*):\\s", "");
            strText = strText.replaceAll("None", "0");
            strText = strText.replaceAll("null", "0");
            strTextReturn = strText;
        }
        
        return strTextReturn;
    }
}
