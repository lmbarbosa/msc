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

public class ClassArticleMetrics {
    
private String strArticleID;
private String strArticleTitle;
private String strArticleCitations;
private String strArticleViews;
private String strArticleSaves;
private String strArticleShares;

    public void ClassArticleMetrics() 
    {
        strArticleID = "0";
        strArticleTitle = "0";
        strArticleCitations = "0";
        strArticleViews = "0";
        strArticleSaves = "0";
        strArticleShares = "0";
    }

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


}
