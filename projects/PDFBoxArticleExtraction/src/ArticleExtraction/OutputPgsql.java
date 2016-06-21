/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ArticleExtraction;

import static ArticleExtraction.OutputPrinter.mainCountryIsoDBArray;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author leo
 */
public class OutputPgsql {
    
    private static DateFormat dateDocFormat = new SimpleDateFormat("yyyy-MM-dd");  
    
    public static PGSqlConn OutputPgSqlConnect() 
    {
        PGSqlConn pgsqlconn_obj = new PGSqlConn();
        
        
        //pgsqlconn_obj.pgSqlConnect();

        return pgsqlconn_obj;
    }
    
     // Check if the Insert is valid. Duplicated filename values are not allowed.
     public static boolean OutputPgSqlInsertValid(String varArticleFileName, String varArticleExtractTitle)
     {
        
        boolean pgSqlCheckInsertReturn = true; 
        Connection pgconn = OutputPgSqlConnect().getPgconn();
        PreparedStatement pgst = null; 
        ResultSet rs = null;
        Integer varArticleIDCount = 0;

        String stm = "SELECT COUNT(article_id) as article_id_count "
                + "FROM article "
                + "WHERE (article_filename = '" + varArticleFileName + "' "
                + "OR article_extract_title = '" + varArticleExtractTitle + "') ";
        try {
             //System.out.println(stm);
             pgst = pgconn.prepareStatement(stm);
             rs = pgst.executeQuery();
             rs.next();
             varArticleIDCount = rs.getInt("article_id_count");
             if (varArticleIDCount > 0)
                 pgSqlCheckInsertReturn = false;
             
             pgst.close();
             
        } catch(SQLException e) {
             System.out.println(e.getMessage());
             System.exit(1);
        }
    
        return pgSqlCheckInsertReturn;
     }    
     
     
     public static void OutputPgSqlInsert(ClassArticleData objArticle)
     {
        
        Connection pgconn = OutputPgSqlConnect().getPgconn();
         
        String strArticleFileName = objArticle.getInputDocFile().getName();
        //String varArticleXMLData = PDFBoxArticleExtraction.getGateDocXMLData();
        // ATTENTION
        // FIX PGSQL BUG RELATED TO THE UTF-8 ENCODING 
        // REMOVE ALL NULL CHAR FROM THE TEXT IN ORDER TO AVOID PGDB ERROR 
        String strArticleStringData = UtilFunctions.stringNormalizeFunction(objArticle.getDocFileData());
        String strArticleDocEncoding = UtilFunctions.stringNormalizeFunction(objArticle.getDocEncoding().toString());
        
        // MetaData Information
        int intDocMetaPageCount = objArticle.getDocMetaPageCount();        
        String strArticleMetaTitle = UtilFunctions.stringNormalizeFunction(objArticle.getDocMetaTitleClean()); 
        String strArticleMetaAuthor = UtilFunctions.stringNormalizeFunction(objArticle.getDocMetaAuthor()); 
        String strArticleMetaSubject = UtilFunctions.stringNormalizeFunction(objArticle.getDocMetaSubject()); 
        String strArticleMetaKeywords = UtilFunctions.stringNormalizeFunction(objArticle.getDocMetaKeywords());
        String strArticleMetaCreator = UtilFunctions.stringNormalizeFunction(objArticle.getDocMetaCreator());
        String strArticleMetaProducer = UtilFunctions.stringNormalizeFunction(objArticle.getDocMetaProducer());
        Calendar calArticleMetaCreationDate = objArticle.getDocMetaCreationDate();
        //String varArticleMetaYear = objArticle.getDocMetaYear();

        // Extracted Information
        String strArticleExtractTitle = UtilFunctions.stringNormalizeFunction(objArticle.getDocExtractTitle());
        String strArticleExtractAuthor = UtilFunctions.stringNormalizeFunction(objArticle.getDocExtractAuthor());
        String strArticleExtractEmail = UtilFunctions.stringNormalizeFunction(objArticle.getDocExtractEmail());
        int intArticleExtractYear = objArticle.getDocExtractYear();
        String strArticleExtractKeywords = UtilFunctions.stringNormalizeFunction(objArticle.getDocExtractKeywords());
        String strArticleExtractCountry = UtilFunctions.stringNormalizeFunction(objArticle.getCountryNameConcat());
        
        Timestamp timestampArticleMetaCreationDate = null;
        if (calArticleMetaCreationDate != null)
        {
            timestampArticleMetaCreationDate = new Timestamp(calArticleMetaCreationDate.getTimeInMillis());
            
        }
       
        PreparedStatement pgst = null; 
        
        // Check if the Insert is valid. Duplicated filename values are not allowed.
        if (OutputPgSqlInsertValid(strArticleFileName, strArticleExtractTitle) == true)
        {                      
            
             // INSERT ARTICLE GENERAL INFORMATION
            String stmArticle = "INSERT INTO article(article_filename, article_data_string, article_doc_encoding, "
                    + "article_metadata_pagecount, article_metadata_title, article_metadata_author, "
                    + "article_metadata_subject, article_metadata_keywords, article_metadata_creator, "
                    + "article_metadata_producer, article_metadata_filecreationdate,"
                    + "article_extract_title, article_extract_author, article_extract_email,"
                    + "article_extract_year, article_extract_keywords, article_extract_country) "
                    + "VALUES(?, ?, ?, ?, ?, ?, ?, ?,"
                    + " ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            try {
                 
                 pgst = pgconn.prepareStatement(stmArticle);
                 pgst.setString(1, strArticleFileName);
                 pgst.setString(2, strArticleStringData);
                 pgst.setString(3, strArticleDocEncoding);
                 
                 pgst.setInt(4, intDocMetaPageCount);
                 pgst.setString(5, strArticleMetaTitle);
                 pgst.setString(6, strArticleMetaAuthor);
                 pgst.setString(7, strArticleMetaSubject);
                 pgst.setString(8, strArticleMetaKeywords);
                 pgst.setString(9, strArticleMetaCreator);
                 pgst.setString(10, strArticleMetaProducer);
                 pgst.setTimestamp(11, timestampArticleMetaCreationDate);
                 
                 pgst.setString(12, strArticleExtractTitle);
                 pgst.setString(13, strArticleExtractAuthor);
                 pgst.setString(14, strArticleExtractEmail);
                 pgst.setInt(15, intArticleExtractYear);
                 pgst.setString(16, strArticleExtractKeywords);
                 pgst.setString(17, strArticleExtractCountry);
                 
                 pgst.executeUpdate();
                 System.out.print(" [db-insert stmArticle: ok] ");
                 
                 pgst.close();
                
            } catch(SQLException e) {
                System.out.println("[error] wrong stmArticle statement");
                 System.out.println(e.getMessage());
                 System.exit(1);
            }
           

            // INSERT ARTICLE AUTHOR NAME BY NAME
            for (int i=0; i<objArticle.getAuthorsArray().size() ;i++)
            {
                
                String stmAuthor = "INSERT INTO article_author (article_author_name, article_id) "
                    + "VALUES(?, (SELECT article_id FROM article WHERE article_filename = '" + strArticleFileName + "'))";
                                
                try {
                 System.out.println(stmAuthor);
                 pgst = pgconn.prepareStatement(stmAuthor);
                 pgst.setString(1, objArticle.getAuthorsArray().get(i));
               
                 pgst.executeUpdate();
                 System.out.print(" [db-insert stmAuthor: ok] ");
                 
                 pgst.close();
                
                } catch(SQLException e) {
                    System.out.println("[error] wrong stmAuthor statement");
                     System.out.println(e.getMessage());
                     System.exit(1);
                }
            }
            
            // INSERT ARTICLE KEYWORD ONE BY ONE
            for (int i=0; i<objArticle.getKeywordsArray().size() ;i++)
            {
                
                String stmKeyword = "INSERT INTO article_keyword (article_keyword_data, article_id) "
                    + "VALUES(?, (SELECT article_id FROM article WHERE article_filename = '" + strArticleFileName + "'))";
                                
                try {
                 System.out.println(stmKeyword);
                 pgst = pgconn.prepareStatement(stmKeyword);
                 pgst.setString(1, objArticle.getKeywordsArray().get(i));
               
                 pgst.executeUpdate();
                 System.out.print(" [db-insert stmKeyword: ok] ");
                 
                 pgst.close();
                
                } catch(SQLException e) {
                    System.out.println("[error] wrong stmKeyword statement");
                     System.out.println(e.getMessage());
                     System.exit(1);
                }
            }
            
            // INSERT ARTICLE COUNTRY ONE BY ONE
            for (int i=0; i<objArticle.getCountryArray().size() ;i++)
            {
                
                String stmCountry = "INSERT INTO article_countryisodb (countryisodb_id, article_id) "
                    + "VALUES(?, (SELECT article_id FROM article WHERE article_filename = '" + strArticleFileName + "'))";
                                
                try {
                 System.out.println(stmCountry);
                 pgst = pgconn.prepareStatement(stmCountry);
                 pgst.setInt(1, mainCountryIsoDBArray.get(objArticle.getCountryArray().get(i)).getCountry_id());
               
                 pgst.executeUpdate();
                 System.out.print(" [db-insert stmCountry: ok] ");
                 
                 pgst.close();
                
                } catch(SQLException e) {
                    System.out.println("[error] wrong stmCountry statement");
                     System.out.println(e.getMessage());
                     System.exit(1);
                }
            }
            
            
        }
        else
        {
            System.out.print(" [db-insert: 'filename or title' already exists in the database] ");    
        }   
        
     }
     
    
}
