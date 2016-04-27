package db;

import com.mysql.jdbc.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public final class MasterDBConnection {

    private Connection conn;
    private Statement statement;
    public static MasterDBConnection instance;
    private int counter = 0;
    private long lastCheck = 0;

    private MasterDBConnection(ConnectionParam db) {
        String url = String.format("jdbc:mysql://%s:%d/%s", db.getHost(), db.getPort(), db.getDbName());

        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            this.conn = (Connection) DriverManager.
                    getConnection(url, db.getUser(), db.getPassword());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | SQLException err) {
            err.printStackTrace();
        }
    }

    public static synchronized MasterDBConnection getDbCon(ConnectionParam db) {
        if (instance == null) {
            instance = new MasterDBConnection(db);
        }
        return instance;
    }
    
    public ResultSet insert(String insertQuery) throws SQLException {
//        counter++;
//        if (lastCheck == 0) {
//            lastCheck = System.nanoTime();
//            counter = 0;
//        }
//
//        if (System.nanoTime() - lastCheck > 1000000000) {
//            System.out.println(counter);
//            counter = 0;
//            lastCheck = System.nanoTime();
//        }

        statement = conn.createStatement();
        statement.executeUpdate(insertQuery);
        ResultSet res = statement.executeQuery("SELECT LAST_INSERT_ID();");
        //        this.conn.close();
        return res;
    }

    public ResultSet query(String query) throws SQLException {
        statement = conn.createStatement();

        return statement.executeQuery(query);
    }

    public Connection getConn() {
        return conn;
    }
}
