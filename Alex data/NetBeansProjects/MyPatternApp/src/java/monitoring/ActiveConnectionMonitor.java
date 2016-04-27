package monitoring;

import com.mysql.jdbc.Connection;
import db.ConnectionParam;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author benjamin
 */
public class ActiveConnectionMonitor {

    private ConnectionParam db;

    public ActiveConnectionMonitor(ConnectionParam db) {
        this.db = db;
    }

    public int GetNumberOfActiveConnections() {
        int min = Integer.MAX_VALUE;
        try {
            Connection conn;
            String url = String.format("jdbc:mysql://%s:%d/%s",
                    db.getHost(), db.getPort(), db.getDbName());

            Class.forName("com.mysql.jdbc.Driver").newInstance();
            conn = (Connection) DriverManager.
                    getConnection(url, db.getUser(), db.getPassword());
            Statement statement = conn.createStatement();

            ResultSet res = statement.executeQuery("SHOW STATUS WHERE "
                    + "`variable_name` = 'Threads_connected';");

            while (res.next()) {
                min = res.getInt("Value");
            }
            res.close();
            statement.close();
            conn.close();

        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | SQLException e) {
            e.printStackTrace();
        }
        return min;
    }

    /**
     * @param db the db to set
     */
    public void setDb(ConnectionParam db) {
        this.db = db;
    }
}
