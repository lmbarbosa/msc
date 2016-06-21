/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ArticleExtraction;

import java.util.ArrayList;

/**
 *
 * @author leo
 */
public class ClassArticle {
    
    // Article Data
    private String strArticleFilename;
    private String strArticleTitle;
    private String strArticleAuthor;
    private String strArticleCountry;
    private String strArticleYear;
    private String strArticleKeywords;   
    private String strArticlePageNumber;
    private String strArticleEmail;
    private String strArticleCreationDate;
    private String strArticleReferences;
    
    // Country ISO DB Obj Array
    private ArrayList<String> CountryArray = new ArrayList<>() ;
    private ArrayList<String> KeywordsArray = new ArrayList<>() ;
    private ArrayList<String> AuthorsArray = new ArrayList<>() ;

    public String getStrArticleFilename() {
        return strArticleFilename;
    }

    public void setStrArticleFilename(String strArticleFilename) {
        this.strArticleFilename = strArticleFilename;
    }

    public String getStrArticleTitle() {
        return strArticleTitle;
    }

    public void setStrArticleTitle(String strArticleTitle) {
        this.strArticleTitle = strArticleTitle;
    }

    public String getStrArticleAuthor() {
        return strArticleAuthor;
    }

    public void setStrArticleAuthor(String strArticleAuthor) {
        this.strArticleAuthor = strArticleAuthor;
    }

    public String getStrArticleCountry() {
        return strArticleCountry;
    }

    public void setStrArticleCountry(String strArticleCountry) {
        this.strArticleCountry = strArticleCountry;
    }

    public String getStrArticleYear() {
        return strArticleYear;
    }

    public void setStrArticleYear(String strArticleYear) {
        this.strArticleYear = strArticleYear;
    }

    public String getStrArticleKeywords() {
        return strArticleKeywords;
    }

    public void setStrArticleKeywords(String strArticleKeywords) {
        this.strArticleKeywords = strArticleKeywords;
    }

    public String getStrArticlePageNumber() {
        return strArticlePageNumber;
    }

    public void setStrArticlePageNumber(String strArticlePageNumber) {
        this.strArticlePageNumber = strArticlePageNumber;
    }

    public String getStrArticleEmail() {
        return strArticleEmail;
    }

    public void setStrArticleEmail(String strArticleEmail) {
        this.strArticleEmail = strArticleEmail;
    }

    public String getStrArticleCreationDate() {
        return strArticleCreationDate;
    }

    public void setStrArticleCreationDate(String strArticleCreationDate) {
        this.strArticleCreationDate = strArticleCreationDate;
    }

    public String getStrArticleReferences() {
        return strArticleReferences;
    }

    public void setStrArticleReferences(String strArticleReferences) {
        this.strArticleReferences = strArticleReferences;
    }

    public ArrayList<String> getCountryArray() {
        return CountryArray;
    }

    public void setCountryArray(ArrayList<String> CountryArray) {
        this.CountryArray = CountryArray;
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

    

    
}
