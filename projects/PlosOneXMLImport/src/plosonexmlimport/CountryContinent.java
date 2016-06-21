/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package plosonexmlimport;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author leo
 */
public class CountryContinent {
    
     // Country Continent Query
     public static String pgsqlQueryCountryContinent (String strCountryName)
     {
        String strCountryContinent = "null";

        try 
        {
            PGSqlConn pgsqlconn_obj = new PGSqlConn();
            pgsqlconn_obj.pgSqlConnect(); 
            Connection pgconn = pgsqlconn_obj.getPgconn();

            String stm = "SELECT country_continent  "
                    + "FROM countryisodb "
                    + "WHERE (country_name_1 = '" + strCountryName + "' "
                    + "OR country_name_2 = '" + strCountryName + "' "
                    + "OR country_name_3 = '" + strCountryName + "' "
                    + "OR country_name_4 = '" + strCountryName + "' "
                    + "OR country_iso_alphacode_3 = '" + strCountryName + "' "
                    + "OR country_iso_alphacode_2 = '" + strCountryName + "') ";
        
             //System.out.println(stm);
             
             PreparedStatement pgst = pgconn.prepareStatement(stm);
             ResultSet rs = pgst.executeQuery();
             while(rs.next())
             {
                strCountryContinent = rs.getString("country_continent").trim();
             }
             pgst.close();
             pgconn.close();
             
        } 
        catch(SQLException e) 
        {
             System.out.println("[error] pgsqlQueryCountryContinent: " + e.getMessage());
        }

        return strCountryContinent;
     }
     
     public static String pgsqlQueryCountryAlphaCode3 (String strCountryName)
     {
        String strCountryAlphaCode3 = "null";

        try 
        {
            PGSqlConn pgsqlconn_obj = new PGSqlConn();
            pgsqlconn_obj.pgSqlConnect(); 
            Connection pgconn = pgsqlconn_obj.getPgconn();

            String stm = "SELECT country_iso_alphacode_3  "
                    + "FROM countryisodb "
                    + "WHERE (country_name_1 = '" + strCountryName + "' "
                    + "OR country_name_2 = '" + strCountryName + "' "
                    + "OR country_name_3 = '" + strCountryName + "' "
                    + "OR country_name_4 = '" + strCountryName + "') ";
        
             //System.out.println(stm);
             
             PreparedStatement pgst = pgconn.prepareStatement(stm);
             ResultSet rs = pgst.executeQuery();
             while(rs.next())
             {
                strCountryAlphaCode3 = rs.getString("country_iso_alphacode_3").trim();
             }
             pgst.close();
             pgconn.close();
             
        } 
        catch(SQLException e) 
        {
             System.out.println("[error] pgsqlQueryCountryContinent: " + e.getMessage());
        }

        return strCountryAlphaCode3;
     }
     
     public static String pgsqlQueryCountryNameStandard (String strCountryName)
     {
        String strCountryCountryNameStandard = "null";

        try 
        {
            PGSqlConn pgsqlconn_obj = new PGSqlConn();
            pgsqlconn_obj.pgSqlConnect(); 
            Connection pgconn = pgsqlconn_obj.getPgconn();

            String stm = "SELECT country_name_1  "
                    + "FROM countryisodb "
                    + "WHERE (country_name_1 = '" + strCountryName + "' "
                    + "OR country_name_2 = '" + strCountryName + "' "
                    + "OR country_name_3 = '" + strCountryName + "' "
                    + "OR country_name_4 = '" + strCountryName + "') ";
        
             //System.out.println(stm);
             
             PreparedStatement pgst = pgconn.prepareStatement(stm);
             ResultSet rs = pgst.executeQuery();
             while(rs.next())
             {
                strCountryCountryNameStandard = rs.getString("country_name_1").trim();
             }
             pgst.close();
             pgconn.close();
             
        } 
        catch(SQLException e) 
        {
             System.out.println("[error] pgsqlQueryCountryContinent: " + e.getMessage());
        }

        return strCountryCountryNameStandard;
     }
    
}
