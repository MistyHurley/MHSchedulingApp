package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
/** Handles database connection */
public class DBConnection {
    //JDBC URL PARTS
    private static final String protocol ="jdbc";
    private static final String vendorName =":mysql:";
    private static final String ipAddress ="//wgudb.ucertify.com:3306/";
    private static final String dbName = "WJ06EIr";
    //JDBC URL
    private static final String jdbcURL = protocol + vendorName + ipAddress + dbName + "?connectionTimeZone=SERVER";

    //v8.0.23 driver and connection interface reference
    private static final String MYSQLJDBCDriver = "com.mysql.jdbc.Driver";
    private static Connection conn;

    private static final String username = "U06EIr"; //username
    private static final String password = "53688739501"; //password leave final

    /** Initiates database connection
     * @return Database Connection object
     */
    public static Connection startConnection() {
        try
        {
            Class.forName(MYSQLJDBCDriver);
            conn = DriverManager.getConnection(jdbcURL, username, password);
            System.out.println("Database connection successful");
        }
        catch(ClassNotFoundException e) //use printStackTrace for printing exceptions
        {
            e.printStackTrace();
            //System.out.println(e.getMessage());
        }
        catch(SQLException e)
        {
            e.printStackTrace();
            //System.out.println(e.getMessage());
        }

        return conn;
    }


    /** Get the connection */
    public static Connection getConnection()
    {
        return conn;
    }
/** Disconnects from the database */
    public static void closeConnection() {
        try {
            conn.close();
            System.out.println("Database connection closed");
        }
        catch(SQLException e) {
            //do nothing

        }

    }

}



