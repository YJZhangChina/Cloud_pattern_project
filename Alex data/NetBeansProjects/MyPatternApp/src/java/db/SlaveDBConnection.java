package db;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import com.mysql.jdbc.Connection;

public final class SlaveDBConnection {

    public Connection conn;
    private Statement statement;
    public static SlaveDBConnection instance;
    private int counter = 0;
    private long lastCheck = 0;

    public SlaveDBConnection(List<ConnectionParam> dbs) {
        String url = "jdbc:mysql:loadbalance://";

        for (ConnectionParam cp : dbs) {
            url += String.format("%s:%d,", cp.getHost(), cp.getPort());
        }
        url = url.substring(0, url.length() - 1);
        url += "/" + dbs.get(0).getDbName();
        
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            this.conn = (Connection) DriverManager.
                    getConnection(url, dbs.get(0).getUser(), dbs.get(0).getPassword());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | SQLException err) {
            err.printStackTrace();
        }

    }

    public SlaveDBConnection(ConnectionParam db) {
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            String url = String.format("jdbc:mysql://%s:%d/%s", db.getHost(), db.getPort(), db.getDbName());
            this.conn = (Connection) DriverManager.
                    getConnection(url, db.getUser(), db.getPassword());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | SQLException err) {
            err.printStackTrace();
        }
    }

    public static synchronized SlaveDBConnection getDbCon(List<ConnectionParam> params) {
        if (instance == null) {
            instance = new SlaveDBConnection(params);
        }
        return instance;
    }

    public static synchronized SlaveDBConnection getDbCon(ConnectionParam params) {
        if (instance == null) {
            instance = new SlaveDBConnection(params);
        }
        return instance;
    }

    public ResultSet query(String query) throws SQLException {
//        counter++;
//        if(lastCheck == 0){
//            lastCheck = System.nanoTime();
//            counter = 0;
//        }
//
//        if(System.nanoTime()-lastCheck > 1000000000){
//            System.out.println(counter);
//            counter = 0;
//            lastCheck = System.nanoTime();
//        }
        statement = instance.conn.createStatement();

        return statement.executeQuery(query);
    }

    public Connection getConn() {
        return conn;
    }
}
