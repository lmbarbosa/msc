/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package plosonexmlimport;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.jdom2.input.sax.XMLReaders;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

/**
 *
 * @author leo
 */
public class ImportXML {
    
    public static ClassArticle importXMLRetrieveDataFunction (File strXMLFile) 
    {
        // VARIABLES
        String strTempArticleID = "";
        String strTempArticleTitle = "";
        String strTempArticleFileName = "";
        String strTempArticlePublicationYear = "";
        String strTempArticleReferences = "";
        String strTempAuthorName = "";
        ArrayList<String> arrayListAuthor = new ArrayList<>();
        ArrayList<String> arrayListCountry = new ArrayList<>();
        ArrayList<String> arrayListKeywords = new ArrayList<>();
        
        
          SAXBuilder builder = new SAXBuilder(XMLReaders.NONVALIDATING);  
          
          // Disable XML Validation
          builder.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
          builder.setExpandEntities(false);
          
	  File xmlFile = strXMLFile;

          ClassArticle objArticle = new ClassArticle();
          
	  try 
          {
 
            Document document = (Document) builder.build(xmlFile);
            Element nodeRoot = document.getRootElement();
            Element nodeArticleMeta = nodeRoot.getChildren("front").get(0).getChild("article-meta");
            Element nodeArticleRef = nodeRoot.getChildren("back").get(0).getChild("ref-list");
            
            // ARTICLE ID
            if (nodeArticleMeta.getChildText("article-id") != null)
            {
                List listArticleID =  nodeArticleMeta.getChildren("article-id");
                
                for (int i = 0; i < listArticleID.size(); i++) 
                {
                    Element elementArticleID = (Element) listArticleID.get(i);

                    if (elementArticleID.getAttribute("pub-id-type").getValue().toString().equalsIgnoreCase("doi"))
                    {
                        strTempArticleID = elementArticleID.getTextTrim();
                    }
                }
                
            }
            
            // ARTICLE FILE NAME
            if (strXMLFile.getName() != null)
            {
                strTempArticleFileName =  strXMLFile.getName().toString().trim();
            }
                
            // ARTICLE TITLE 
            if (nodeArticleMeta.getChild("title-group").getChildTextTrim("article-title") != null)
            {
                strTempArticleTitle = nodeArticleMeta.getChild("title-group").getChild("article-title").getValue().toString().trim();
            }
            
            // ARTICLE PUB YEAR 
            if (nodeArticleMeta.getChild("permissions").getChildText("copyright-year") != null )
            {
                strTempArticlePublicationYear = nodeArticleMeta.getChild("permissions").getChild("copyright-year").getText().trim();
            }
            else if (nodeArticleMeta.getChild("pub-date").getChildText("year") != null)
            {
                strTempArticlePublicationYear = nodeArticleMeta.getChild("pub-date").getChildText("year");
            }
            
            // Removing quotes
            strTempArticleTitle = UtilFunctions.stringRemoveQuotes(strTempArticleTitle);
            
            // AUTHOR
            try 
            {
                
                List listAuthor = nodeArticleMeta.getChild("contrib-group").getChildren("contrib");
                
                for (int i = 0; i < listAuthor.size(); i++) 
                {
                    Element elementAuthor = (Element) listAuthor.get(i);

                    if (elementAuthor.getAttribute("contrib-type").getValue().toString().equalsIgnoreCase("author"))
                    {
                        if (elementAuthor.getChildTextTrim("name") != null)
                        {
                            strTempAuthorName = elementAuthor.getChild("name").getChild("given-names").getText() + " " + elementAuthor.getChild("name").getChild("surname").getText();
                            strTempAuthorName = UtilFunctions.stringRemoveQuotes(strTempAuthorName);
                            arrayListAuthor.add(strTempAuthorName);
                        }
                    }
                }
            }
            catch (Exception e) 
            {
		System.out.println( "[error] ImportXML (Author):" + e.getMessage()  );
            }
            
            // COUNTRY
            try 
            {
                
                List listCountry = nodeArticleMeta.getChildren("aff");

                for (int j = 0; j < listCountry.size(); j++) 
                {
                    Element elementCountry = (Element) listCountry.get(j);

                    if (elementCountry.getAttribute("id").getValue().contains("aff"))
                    {
                        if (elementCountry.getChildTextTrim("addr-line") != null)
                        {
                            String str_country = elementCountry.getChildTextTrim("addr-line");
                            
                            str_country = UtilFunctions.stringRemoveQuotes(str_country);
                            str_country = str_country.substring(str_country.lastIndexOf(',')+1);
                            str_country = UtilFunctions.stringCountryNameFix(str_country);
                            str_country = CountryContinent.pgsqlQueryCountryNameStandard(str_country);
                            
                            arrayListCountry.add(str_country);
                        }
                    }
                }
            }
            catch (Exception e) 
            {
		System.out.println( "[error] ImportXML (Country):" + e.getMessage()  );
            }
            
            // KEYWORDS
            try 
            {
                if (nodeArticleMeta.getChild("article-categories").getChildText("subj-group") != null)
                {
                
                    List listSubjGroup = nodeArticleMeta.getChild("article-categories").getChildren("subj-group");

                    for (int k = 0; k < listSubjGroup.size(); k++) 
                    {
                        Element elementSubjGroup = (Element) listSubjGroup.get(k);
                        
                        if (elementSubjGroup.getAttributeValue("subj-group-type").equalsIgnoreCase("Discipline") || 
                                elementSubjGroup.getAttributeValue("subj-group-type").equalsIgnoreCase("Discipline-v2"))
                        {
                            
                            List listSubjects = elementSubjGroup.getChildren("subject");
                            
                            for (int k2 = 0; k2 < listSubjects.size(); k2++) 
                            {
                                Element elementSubject = (Element) listSubjects.get(k2);
                                String str_keyword = elementSubject.getText().trim();
                                str_keyword = UtilFunctions.stringRemoveQuotes(str_keyword);
                                arrayListKeywords.add(str_keyword);
                            }
                        }

                    }
                }
            }
            catch (Exception e) 
            {
		System.out.println( "[error] ImportXML (Keywords):" + e.getMessage()  );
            }
            
            // REFERENCES
            try 
            {
                List listReferences = nodeArticleRef.getChildren("ref");

                for (int l = 0; l < listReferences.size(); l++) 
                {
                // Temp var
                String str_references = "";
                
                
                    Element elementReferences = (Element) listReferences.get(l);
                
                    // ELEMENT CITATION
                    if (elementReferences.getChildTextTrim("element-citation") != null)
                    {
                        if (elementReferences.getChild("element-citation").getChildTextTrim("article-title") != null)
                        {
                            if (elementReferences.getChild("element-citation").getChild("article-title").getChildTextTrim("italic") != null)
                            {
                                str_references = elementReferences.getChild("element-citation").getChild("article-title").getChildTextTrim("italic");
                            }
                            else
                            {
                                str_references = elementReferences.getChild("element-citation").getChildTextTrim("article-title");
                            }

                        }   
                        else if (elementReferences.getChild("element-citation").getChildTextTrim("source") != null)
                        {
                            if (elementReferences.getChild("element-citation").getChild("source").getChildTextTrim("italic") != null)
                            {
                                str_references = elementReferences.getChild("element-citation").getChild("source").getChildTextTrim("italic");
                            }
                            else
                            {
                                str_references = elementReferences.getChild("element-citation").getChildTextTrim("source");
                            }
                        }  
                    }
                    // MIXED CITATION
                    else if (elementReferences.getChildTextTrim("mixed-citation") != null)
                    {
                        if (elementReferences.getChild("mixed-citation").getChildTextTrim("article-title") != null)
                        {
                            if (elementReferences.getChild("mixed-citation").getChild("article-title").getChildTextTrim("italic") != null)
                            {
                                str_references = elementReferences.getChild("mixed-citation").getChild("article-title").getChildTextTrim("italic");
                            }
                            else
                            {
                                str_references = elementReferences.getChild("mixed-citation").getChildTextTrim("article-title");
                            }

                        }   
                    }
                    
                    //System.out.println("str_references:" + str_references);
                    str_references = UtilFunctions.stringRemoveQuotes(str_references);
                    strTempArticleReferences += str_references + " ";
                }
            }
            catch (Exception e) 
            {
                System.out.println("[error] ImportXML(References) " + e.getMessage());
            }
            
            // Google Scholar Query 
            String strTempArticleCitationValue = "null";
            //strTempArticleCitationValue = GoogleScholarCitation.GoogleScholarCitationRetrieveDataFunction(strTempArticleTitle);
            
            // OBJECT CLASS ARTICLE
            objArticle.setStrArticleID(strTempArticleID);
            objArticle.setStrArticleTitle(strTempArticleTitle);
            objArticle.setStrArticleFileName(strTempArticleFileName);
            objArticle.setStrArticlePublicationYear(strTempArticlePublicationYear);
            objArticle.setStrArticleCitations(strTempArticleCitationValue);
            objArticle.setStrArticleReferences(strTempArticleReferences);
            objArticle.setArrayListAuthor(arrayListAuthor);
            objArticle.setArrayListCountry(arrayListCountry);
            objArticle.setArrayListKeywords(arrayListKeywords);
             
           
            /*
            // Debugging purposes
            System.out.println( objArticle.getStrArticleID() );
            System.out.println( objArticle.getStrArticleTitle() );
            System.out.println( objArticle.getStrArticleFileName());
            System.out.println( objArticle.getStrArticlePublicationYear() );
            System.out.println( objArticle.getStrArticleGoogleScholarCitation() );
            System.out.println( objArticle.getStrArticleReferences() );
            System.out.println( objArticle.getArrayListAuthor().toString() );
            System.out.println( objArticle.getArrayListCountry().toString() );
            System.out.println( objArticle.getArrayListKeywords().toString() );
            System.out.println( "" );
            */
            
	  } 
          catch (Exception e) 
          {
		System.out.println( "[error] ImportXML:" + e.getMessage()  );
	  }

          return objArticle;
	}
    
    
}
