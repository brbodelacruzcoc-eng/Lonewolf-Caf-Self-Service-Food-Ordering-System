/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package lonewolfcafe;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
/**
 *
 * @author ysabel
 */
public class DBConnection {
    public static Connection connect() { // Connection - return type, connect() - method
        try {
            String url = "jdbc:mysql://localhost:3306/lonewolf_cafe_db";
            String user = "root";
            String pass = "";
            Connection conn = DriverManager.getConnection(url, user, pass);
            return conn;
        } catch (SQLException e) {
            System.out.println("Database connection failed: " + e.getMessage());
            return null;
        }
    }
}
