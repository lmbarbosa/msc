/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package plosonexmlimport;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author leo
 */
public class PGSqlConn {
    
    private static Connection pgconn = null;
    private static String pgdatabase = "jdbc:postgresql://127.0.0.1:5432/articledb";
    private static String pguser = "postgres";
    private static String pgpwd = "postgres";
    
    public static void pgSqlConnect() 
    {
        try 
        {
            pgconn = DriverManager.getConnection(
                            pgdatabase, 
                            pguser,     
                            pgpwd);

        } catch (SQLException e) {

                System.out.println("[error] connection failed");
                System.out.println(e.getMessage());
                System.exit(1);

        }

        if (pgconn == null) {
                System.out.println("[error] postgresql connection falied");
                System.exit(1);
        } 
        
    }

    public static Connection getPgconn() {
        return pgconn;
    }

    public static void setPgconn(Connection pgconn) {
        PGSqlConn.pgconn = pgconn;
    }
    
}
