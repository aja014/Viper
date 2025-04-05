/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author Asus
 */
public class database {

    // return Database value from database
    public static String getStringRecord(String column, String name) {
        String sql = "select " + column + " from account where username = '" + name + "'";
        String retValue = "";
        try {
            Connection con = DriverManager.getConnection("jdbc:derby://localhost:1527/ViperDB", "root", "root");
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                retValue = rs.getString(column);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            System.out.println("Failed");
        }
        if (retValue == "") {
            return "null";
        } else {
            return retValue;
        }
    }
    
    // return Integer value from database
    public static int getIntRecord(String column, String name) {
        String sql = "select " + column + " from account where username = '" + name + "'";
        int retValue = 0;
        try {
            Connection con = DriverManager.getConnection("jdbc:derby://localhost:1527/ViperDB", "root", "root");
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                retValue = rs.getInt(column);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            System.out.println("Failed");
        }
        if (retValue == 0) {
            return 0;
        } else {
            return retValue;
        }
    }

}
