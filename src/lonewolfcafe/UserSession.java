/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package lonewolfcafe;

/**
 *
 * @author ysabel
 */
public class UserSession {
    private static int userId;
    private static String username;

    public static void setUser(int id, String user) {
        userId = id;
        username = user;
    }

    public static int getUserId() {
        return userId;
    }

    public static String getUsername() {   
        return username;
    }

    public static boolean isLoggedIn() {    
        return username != null;
    }
    
}
