package monitoring;

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.rowset.JdbcRowSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import proxy.DBProxy;

import com.mysql.jdbc.Connection;
import com.sun.rowset.JdbcRowSetImpl;

import db.ConnectionParam;
import db.SlaveDBConnection;

public class ReplicationWatchDog implements Runnable {

    static final Logger logger = LogManager.getLogger(ReplicationWatchDog.class.getName());
    private boolean isSafe = true;
    private ConnectionParam dbParams;
    private String hostname = null;
    private DBProxy proxy;
    private Connection conn;

    public ReplicationWatchDog(Connection conn) {
        this.conn = conn;
    }

    public void run() {

//		conn.getM
        if (dbParams.getIsMaster()) {
//            conn = MasterDBConnection.getDbCon(dbParams).getConn();
//
//            JdbcRowSet jdbcRS;
//            try {
//                jdbcRS = new JdbcRowSetImpl(conn);
//                jdbcRS.setType(ResultSet.TYPE_SCROLL_INSENSITIVE);
//
//                String sql = "SHOW MASTER STATUS;";
//                jdbcRS.setCommand(sql);
//                jdbcRS.execute();
//                jdbcRS.addRowSetListener(new MyRowListener());
//
//                while (jdbcRS.next()) {
//                    // each call to next, generates a cursorMoved event
//                    System.out.println("id=" + jdbcRS.getInt(1));
//                    System.out.println("data=" + jdbcRS.getString(2));
//                }
//                conn.close();
//            } catch (SQLException e) {
//                e.printStackTrace();
//            }

        } else {
            conn = SlaveDBConnection.getDbCon(dbParams).getConn();

            try {
                monitorSlave(conn);

                if (!isSafe) {
                    for (ConnectionParam slave : proxy.getSlaves()) {
                        if (slave.getHost().equals(hostname)) {
                            slave.setSafe(false);
                            break;
                        }
                    }
//					restartSlave(conn);
                }
//				conn.close();
            } catch (SQLException e) {
                logger.error(hostname + " - " + e.getMessage());
            }
        }
    }

    private void restartSlave(Connection conn) throws SQLException {
        JdbcRowSet jdbcRS;
        jdbcRS = new JdbcRowSetImpl(conn);
        jdbcRS.setType(ResultSet.TYPE_SCROLL_INSENSITIVE);

        logger.trace("Stopping Slave " + hostname + " to skip 1 statement");

        jdbcRS.setCommand("set global SQL_SLAVE_SKIP_COUNTER = 1;");
        jdbcRS.execute();
        jdbcRS.setCommand("stop slave;");
        jdbcRS.execute();
        jdbcRS.setCommand("start slave;");
        jdbcRS.execute();
        jdbcRS.setCommand("select sleep(1);");
        jdbcRS.execute();

        logger.trace("Starting Slave " + hostname);

//		jdbcRS.close();
    }

    private void monitorSlave(Connection conn) throws SQLException {
        JdbcRowSet jdbcRS;

        jdbcRS = new JdbcRowSetImpl(conn);
        jdbcRS.setType(ResultSet.TYPE_SCROLL_INSENSITIVE);

        jdbcRS.setCommand("SELECT @@hostname;");
        jdbcRS.execute();
        jdbcRS.next();
        hostname = jdbcRS.getString(1);

        jdbcRS.setCommand("SHOW GLOBAL STATUS like 'slave_running';");
        jdbcRS.execute();
        jdbcRS.next();
        boolean slave_running = jdbcRS.getString("Value").equals("ON") ? true : false;

        if (!slave_running) {
            logger.error(hostname + " - Slave not running");
        }

        jdbcRS.setCommand("SHOW SLAVE STATUS;");
        jdbcRS.execute();
//				jdbcRS.addRowSetListener(new MyRowListener());
        jdbcRS.next();

        boolean slave_io_running = jdbcRS.getString("Slave_IO_Running").equals("Yes") ? true : false;
        boolean slave_sql_running = jdbcRS.getString("Slave_SQL_Running").equals("Yes") ? true : false;
        int delay = jdbcRS.getInt("Seconds_Behind_Master");
        int errorNo = jdbcRS.getInt("Last_Errno");
        int master_pos = jdbcRS.getInt("Read_Master_Log_Pos");
        int slave_pos = jdbcRS.getInt("Exec_Master_Log_Pos");

        if (!slave_io_running) {
            logger.error(hostname + " - Slave IO not running");
            isSafe = false;
        }

        if (!slave_sql_running) {
            logger.error(hostname + " - Slave SQL not running");
            isSafe = false;
        }

        if (delay > 0) {
            logger.error(hostname + " - Seconds_Behind_Master : " + delay);
            isSafe = false;
        }

        if (errorNo > 0) {
            logger.error(hostname + " - ErrorNo : " + errorNo);
            isSafe = false;
        }

        if (master_pos != slave_pos) {
            logger.error(hostname + " - Replication failed");
            isSafe = false;
        }
//		logger.trace("%s - Replication is working !", hostname);
//		jdbcRS.close();
    }
}
