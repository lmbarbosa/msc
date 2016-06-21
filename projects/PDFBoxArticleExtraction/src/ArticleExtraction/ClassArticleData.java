/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ArticleExtraction;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author leo
 */
public class ClassArticleData {
    
    // Input document
    private File inputDocFile;
    
    // Document data
    private String docFileData;
    
    // Document Encoding
    private String docEncoding;
    
    // Document MetaData
    private String docMetaTitle;
    private String docMetaTitleClean;
    private String docMetaAuthor;
    private int docMetaPageCount;
    private String docMetaSubject;
    private String docMetaKeywords;
    private String docMetaCreator;
    private String docMetaProducer;
    private Calendar docMetaCreationDate;
    private int docMetaYear;
    
    // Document Extracted Data
    private String docExtractTitle;
    private String docExtractAuthor;
    private String docExtractEmail;
    private String docExtractKeywords;   
    private int docExtractYear;
    private String docExtractReferences;
    private String docExtractDOI;
    
     // Country ISO DB Obj Array
    private ArrayList<Integer> CountryArray = new ArrayList<>() ;
    private ArrayList<String> KeywordsArray = new ArrayList<>() ;
    private ArrayList<String> AuthorsArray = new ArrayList<>() ;
    private String CountryNameConcat;
    
    public ClassArticleData()
    {
        CountryArray = new ArrayList<>();
        KeywordsArray = new ArrayList<>();
        AuthorsArray = new ArrayList<>();
        
        inputDocFile = null;
        docFileData = "null";
        docEncoding = "null";
        
        docMetaTitle = "null";
        docMetaTitleClean = "null";
        docMetaAuthor = "null";
        docMetaPageCount = 0;
        docMetaSubject = "null";
        docMetaKeywords = "null";
        docMetaCreator = "null";
        docMetaProducer = "null";
        docMetaCreationDate = null;
        docMetaYear = 0;
        
        docExtractTitle = "null";
        docExtractAuthor = "null";
        docExtractEmail = "null";
        docExtractKeywords = "null"; 
        docExtractYear = 0;
        docExtractReferences  = "null";
        docExtractDOI = "null";
        
        CountryNameConcat = "null" ;
        
        
    }

    public String getDocMetaTitle() {
        return docMetaTitle;
    }

    public void setDocMetaTitle(String docMetaTitle) {
        this.docMetaTitle = docMetaTitle;
    }

    public String getDocMetaTitleClean() {
        return docMetaTitleClean;
    }

    public void setDocMetaTitleClean(String docMetaTitleClean) {
        this.docMetaTitleClean = docMetaTitleClean;
    }

    public String getDocMetaAuthor() {
        return docMetaAuthor;
    }

    public void setDocMetaAuthor(String docMetaAuthor) {
        this.docMetaAuthor = docMetaAuthor;
    }

    public int getDocMetaPageCount() {
        return docMetaPageCount;
    }

    public void setDocMetaPageCount(int docMetaPageCount) {
        this.docMetaPageCount = docMetaPageCount;
    }

    public String getDocMetaSubject() {
        return docMetaSubject;
    }

    public void setDocMetaSubject(String docMetaSubject) {
        this.docMetaSubject = docMetaSubject;
    }

    public String getDocMetaKeywords() {
        return docMetaKeywords;
    }

    public void setDocMetaKeywords(String docMetaKeywords) {
        this.docMetaKeywords = docMetaKeywords;
    }

    public String getDocMetaCreator() {
        if (docMetaCreator != null)
            return docMetaProducer;
        else 
            return "null";
    }

    public void setDocMetaCreator(String docMetaCreator) {
        this.docMetaCreator = docMetaCreator;
    }

    public String getDocMetaProducer() {
        if (docMetaProducer != null)
            return docMetaProducer;
        else 
            return "null";
    }

    public void setDocMetaProducer(String docMetaProducer) {
        this.docMetaProducer = docMetaProducer;
    }

    public Calendar getDocMetaCreationDate() {
        return docMetaCreationDate;
    }

    public void setDocMetaCreationDate(Calendar docMetaCreationDate) {
        this.docMetaCreationDate = docMetaCreationDate;
    }
    

    public File getInputDocFile() {
        return inputDocFile;
    }

    public void setInputDocFile(File inputDocFile) {
        this.inputDocFile = inputDocFile;
    }

    public String getDocFileData() {
        return docFileData;
    }

    public void setDocFileData(String docFileData) {
        this.docFileData = docFileData;
    }

    public String getDocEncoding() {
        return docEncoding;
    }

    public void setDocEncoding(String docEncoding) {
        this.docEncoding = docEncoding;
    }

    public String getDocExtractTitle() {
        return docExtractTitle;
    }

    public void setDocExtractTitle(String docArticleTitle) {
        this.docExtractTitle = docArticleTitle;
    }

    public String getDocExtractAuthor() {
        return docExtractAuthor;
    }

    public void setDocExtractAuthor(String docExtractAuthor) {
        this.docExtractAuthor = docExtractAuthor;
    }

    public int getDocExtractYear() {
        return docExtractYear;
    }

    public void setDocExtractYear(int docExtractYear) {
        this.docExtractYear = docExtractYear;
    }

    public int getDocMetaYear() {
        return docMetaYear;
    }

    public void setDocMetaYear(int docMetaYear) {
        this.docMetaYear = docMetaYear;
    }

    public String getDocExtractKeywords() {
        return docExtractKeywords;
    }

    public void setDocExtractKeywords(String docExtractKeywords) {
        this.docExtractKeywords = docExtractKeywords;
    }

    public String getDocExtractReferences() {
        return docExtractReferences;
    }

    public void setDocExtractReferences(String docExtractReferences) {
        this.docExtractReferences = docExtractReferences;
    }
    
    public String getDocExtractEmail() {
        return docExtractEmail;
    }

    public void setDocExtractEmail(String docExtractEmail) {
        this.docExtractEmail = docExtractEmail;
    }

    public String getDocExtractDOI() {
        return docExtractDOI;
    }

    public void setDocExtractDOI(String docExtractDOI) {
        this.docExtractDOI = docExtractDOI;
    }

    public ArrayList<Integer> getCountryArray() {
        return CountryArray;
    }

    public void setCountryArray(ArrayList<Integer> CountryIsoDBArray) {
        this.CountryArray = CountryIsoDBArray;
    }

    public ArrayList<String> getKeywordsArray() {
        return KeywordsArray;
    }

    public void setKeywordsArray(ArrayList<String> KeywordsArray) {
        this.KeywordsArray = KeywordsArray;
    }
    
    public ArrayList<String> getAuthorsArray() {
        return AuthorsArray;
    }

    public void setAuthorsArray(ArrayList<String> AuthorsArray) {
        this.AuthorsArray = AuthorsArray;
    } 

    public String getCountryNameConcat() {
        return CountryNameConcat;
    }

    public void setCountryNameConcat(String CountryNameConcat) {
        this.CountryNameConcat += CountryNameConcat + ", ";
    }

    
    
    
}
