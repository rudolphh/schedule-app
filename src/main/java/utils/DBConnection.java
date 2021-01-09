package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class DBConnection {

    private static final ResourceBundle rb = ResourceBundle.getBundle("config");

    // JDBC URL parts
    private static final String protocol = rb.getString("protocol");
    private static final String vendorName = ":" + rb.getString("vendorName") + ":";
    private static final String options = "?autoReconnect=true&useSSL=false";
    private static final String ipAddress = "//" + rb.getString("ipAddress") + "/" + rb.getString("dbName") + options;

    // JDBC URL
    private static final String jdbcURL = protocol + vendorName + ipAddress;

    // Driver Interface reference
    private static final String MYSQLJDBCDriver = "com.mysql.cj.jdbc.Driver";
    private static Connection conn = null;

    private static final String username = rb.getString("dbUser");
    private static final String password = rb.getString("dbPass");


    public static Connection startConnection() {
        if(conn != null) return conn;//
        try{
            Class.forName(MYSQLJDBCDriver);
            conn = DriverManager.getConnection(jdbcURL, username, password);
            System.out.println("Connection successful");
        }
        catch (SQLException | ClassNotFoundException e){
            System.out.println("Error: " + e.getMessage());
        }
        return conn;
    }

    public static void closeConnection(){
        try{
            conn.close();
            System.out.println("Connection closed.");
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

}// end DBConnection
