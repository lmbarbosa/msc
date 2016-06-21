/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package plosonexmlimport;

import java.util.ArrayList;

/**
 *
 * @author leo
 */

public class ClassArticle {
    
private String strArticleID;
private String strArticleTitle;
private String strArticleFileName;
private String strArticlePublicationYear;
private String strArticleCitations;
private String strArticleViews;
private String strArticleSaves;
private String strArticleShares;
private String strArticleReferences;
private ArrayList<String> arrayListAuthor = new ArrayList<>();
private ArrayList<String> arrayListCountry = new ArrayList<>();
private ArrayList<String> arrayListKeywords= new ArrayList<>(); 

    public String getStrArticleID() {
        return strArticleID;
    }

    public void setStrArticleID(String strArticleID) {
        this.strArticleID = strArticleID;
    }

    public String getStrArticleTitle() {
        return strArticleTitle;
    }

    public void setStrArticleTitle(String strArticleTitle) {
        this.strArticleTitle = strArticleTitle;
    }

    public String getStrArticlePublicationYear() {
        return strArticlePublicationYear;
    }

    public void setStrArticlePublicationYear(String strArticlePublicationYear) {
        this.strArticlePublicationYear = strArticlePublicationYear;
    }

    public String getStrArticleCitations() {
        return strArticleCitations;
    }

    public void setStrArticleCitations(String strArticleCitations) {
        this.strArticleCitations = strArticleCitations;
    }

    public String getStrArticleViews() {
        return strArticleViews;
    }

    public void setStrArticleViews(String strArticleViews) {
        this.strArticleViews = strArticleViews;
    }

    public String getStrArticleSaves() {
        return strArticleSaves;
    }

    public void setStrArticleSaves(String strArticleSaves) {
        this.strArticleSaves = strArticleSaves;
    }

    public String getStrArticleShares() {
        return strArticleShares;
    }

    public void setStrArticleShares(String strArticleShares) {
        this.strArticleShares = strArticleShares;
    }

    public ArrayList<String> getArrayListAuthor() {
        return arrayListAuthor;
    }

    public void setArrayListAuthor(ArrayList<String> arrayListAuthor) {
        this.arrayListAuthor = arrayListAuthor;
    }

    public ArrayList<String> getArrayListCountry() {
        return arrayListCountry;
    }

    public void setArrayListCountry(ArrayList<String> arrayListCountry) {
        this.arrayListCountry = arrayListCountry;
    }

    public ArrayList<String> getArrayListKeywords() {
        return arrayListKeywords;
    }

    public void setArrayListKeywords(ArrayList<String> arrayListKeywords) {
        this.arrayListKeywords = arrayListKeywords;
    }

    public String getStrArticleReferences() {
        return strArticleReferences;
    }

    public void setStrArticleReferences(String strArticleReferences) {
        this.strArticleReferences = strArticleReferences;
    }

    public String getStrArticleFileName() {
        return strArticleFileName;
    }

    public void setStrArticleFileName(String strArticleFileName) {
        this.strArticleFileName = strArticleFileName;
    }
    
    

}
