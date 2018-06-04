package army2.server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author ASD
 */
public class SQLManager {

    protected static Connection conn;
    protected static Statement stat;

    protected static synchronized void create(String host, String database, String user, String pass) {
        try {
            Class.forName("com.mysql.jdbc.Driver");  // Kiểm tra driver
        } catch(ClassNotFoundException e) {
            System.out.println("driver mysql not found!");
            System.exit(0);
        }
        String url = "jdbc:mysql://" + host + "/" + database;
        System.out.println("MySQL connect: "+url);
        try {
            conn = DriverManager.getConnection(url, user, pass);
            stat = conn.createStatement();
            System.out.println("Success!");
        } catch(SQLException e) {
            e.printStackTrace();
            System.exit(0);
        }
    }

    protected static synchronized boolean close() {
        System.out.println("Close connection to database");
        try {
            if(stat != null)
                stat.close();
            if(conn != null)
                conn.close();
            return true;
        } catch(SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

}
