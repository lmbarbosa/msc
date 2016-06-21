/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ArticleExtraction;

/**
 *
 * @author leo
 */
public class UtilFunctions {
    
    // Function to return the number of string occurrencies
    public static int countStringOccurrency(String srtText, String srtFind) 
    {
        int intCounter = srtText.split(srtFind).length;
        
        return (intCounter-1);
    }
    
    // Function to normalize the text
    public static String stringNormalizeFunction(String srtText) 
    {
        String srtTextReturn = "";
        
        if (srtText != null && srtText != "")
        {
            // remove spaces
            srtText = srtText.trim();

            // ATTENTION
            // FIX PGSQL BUG RELATED TO THE UTF-8 ENCODING 
            // REMOVE ALL NULL CHAR FROM THE TEXT IN ORDER TO AVOID PGDB ERROR 
            srtText = srtText.replace((char)0,(char)32);
        
            // remove quotation marks (all quotas) in order to avoid SQL Errors
            srtText = srtText.replace("“", "");
            srtText = srtText.replace("”", "");
            srtText = srtText.replace("‘", "");
            srtText = srtText.replace("’", "");
            srtText = srtText.replace("'", "");
            srtText = srtText.replace("\"", "");
            srtText = srtText.replace("`", "");
            //srtText = srtText.replace("-\r\n", "");
            srtText = srtText.replace(" \r\n", " "); 
            srtText = srtText.replace("\r\n", " ");
            srtText = srtText.replace("ﬁ", "fi");
            srtText = srtText.replace("l<", "k");
            srtText = srtText.replace("l(", "k"); 
            srtText = srtText.replace("ﬂ", "fl");
            srtText = srtText.replace("–", "-");
            srtText = srtText.replaceAll("	", " ");

            srtTextReturn = srtText;
        }
        return srtTextReturn;
    }
    
    public static String stringRemoveIlegalChars(String srtText) 
    {
        srtText = srtText.replaceAll("[\\x00-\\x1F]", " ");
        srtText = srtText.replaceAll("[\\xA8-\\xFE]", " ");
        srtText = srtText.replaceAll("\t", " ");
        srtText = srtText.replaceAll("\n", " ");
        srtText = srtText.replaceAll("	", " ");
        
        return srtText;
    }
    
    public static String stringReferenceRemoveIlegalChars(String srtText) 
    {
        srtText = srtText.replaceAll("	", " ");
        srtText = srtText.replaceAll(";", ",");
        
        return srtText;
    }
    
    public static String stringNormalizeAuthorName(String srtText) 
    {
        srtText = srtText.replace("*", "");
        srtText = srtText.replace("'", "");
        srtText = srtText.replace("\"", "");
        srtText = srtText.replace("´ı", "i");
        srtText = srtText.replace("´", "");
        srtText = srtText.replace("`", "");
        srtText = srtText.replace("^", "");
        srtText = srtText.replace("~", "");
        srtText = srtText.replace("˜", "");
        srtText = srtText.replace("!", "");
        srtText = srtText.replace("¨", "");
        srtText = srtText.replace("?", "");
        srtText = srtText.replace("‡", "");
        srtText = srtText.replace("†", ""); 
        //srtText = srtText.replaceAll("[\\xB0-\\xFE]", "");
        srtText = srtText.replaceAll("[\\x80-\\xBF]|[^\\x00-\\xFF]", ""); // ilegal chars 
        srtText = srtText.replaceAll("[\\x30-\\x39]", ""); // numbers
        
        return srtText;
    }
    
    public static String stringNormalizeKeywords(String srtText) 
    {
        if (srtText != null && srtText != "null")
        {
        srtText = srtText.replaceAll("((\\n|\\s).*(\\n|\\s))(1\\s?$)", "");
        srtText = srtText.replaceAll("[\\x80-\\xBF]|[^\\x00-\\xFF]", ""); // illegal chars 
        }
        return srtText;
    }
    
    // Function to remove all quotes
    public static String stringRemoveQuotes(String srtText) 
    {
        String srtTextReturn = srtText;
        
        if (srtText != null && srtText != "")
        {
             srtTextReturn = srtText.trim();
             srtTextReturn = srtText.replace("\"", "");
             srtTextReturn = srtText.replace("\t", "");
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
    
    // Functio to short names
    public static String stringShorteningNames(String srtName) 
    {
        String srtNameReturn = "";  
        srtName = srtName.trim();
        
        if (srtName != null && srtName != "")
        {
            if (srtName.indexOf(" ") != -1 && srtName.indexOf(".") == -1)
            {
                String tempArrayName[] = srtName.split(" ");
                {
                    for(int i=0; i<(tempArrayName.length-1); i++)
                    {
                        if (tempArrayName[i].length() > 1)
                        {
                            srtNameReturn += tempArrayName[i].charAt(0);
                            srtNameReturn += ". ";
                        }
                        else 
                        {
                            srtNameReturn += tempArrayName[i] + " ";
                        }
                    }
                }
                srtNameReturn += tempArrayName[tempArrayName.length-1];
            }
            else 
            {
                srtNameReturn = srtName;
            }
        }
        
        return srtNameReturn;
    }
}
