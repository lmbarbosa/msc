/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ArticleExtraction;

/**
 *
 * @author leo
 */

// Country ISO Database 
public class ClassCountryIsoDB {
    
    // Country ID
    private int country_id = 0;
    
    // Country ISO AlphaCode 2 
    private String country_iso_alphacode2 = null;
    
    // Country ISO AlphaCode 3 
    private String country_iso_alphacode3 = null;
    
    // Country Name 1
    private String country_name1 = null;
    
    // Country Name 2
    private String country_name2 = null;
    
    // Country Name 3
    private String country_name3 = null;
    
    // Country Continent
    private String country_continent = null;
    
    public ClassCountryIsoDB()
    {
      country_id = 0;
      country_iso_alphacode2 = null;
      country_iso_alphacode3 = null;
      country_name1 = null;
      country_name2 = null;
      country_name3 = null;
    }

    public int getCountry_id() {
        return country_id;
    }

    public void setCountry_id(int country_id) {
        this.country_id = country_id;
    }

    public String getCountry_iso_alphacode2() {
        return country_iso_alphacode2;
    }

    public void setCountry_iso_alphacode2(String country_iso_alphacode2) {
        this.country_iso_alphacode2 = country_iso_alphacode2;
    }

    public String getCountry_iso_alphacode3() {
        return country_iso_alphacode3;
    }

    public void setCountry_iso_alphacode3(String country_iso_alphacode3) {
        this.country_iso_alphacode3 = country_iso_alphacode3;
    }

    public String getCountry_name1() {
        return country_name1;
    }

    public void setCountry_name1(String country_name1) {
        this.country_name1 = country_name1;
    }

    public String getCountry_name2() {
        return country_name2;
    }

    public void setCountry_name2(String country_name2) {
        this.country_name2 = country_name2;
    }

    public String getCountry_name3() {
        return country_name3;
    }

    public void setCountry_name3(String country_name3) {
        this.country_name3 = country_name3;
    }

    public String getCountry_continent() {
        return country_continent;
    }

    public void setCountry_continent(String country_continent) {
        this.country_continent = country_continent;
    }

    
    
    
}
