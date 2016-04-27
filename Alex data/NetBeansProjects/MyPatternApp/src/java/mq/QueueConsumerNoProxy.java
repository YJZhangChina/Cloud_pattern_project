package mq;

import db.ConnectionParam;
import db.MasterDBConnection;
import db.SqlRequest;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author benjamin
 */
public class QueueConsumerNoProxy implements Runnable {

    private final BlockingQueue<SqlRequest> queue;
    MasterDBConnection db;

    public QueueConsumerNoProxy(BlockingQueue<SqlRequest> queue) {
        this.queue = queue;
        db = MasterDBConnection.getDbCon(
                new ConnectionParam("host_ip", 3306, "sakila", "root",
                        "pass", "com.mysql.jdbc.Driver", false));
    }

    @Override
    public void run() {
        while (!queue.isEmpty()) {
            SqlRequest req;
            try {
                req = queue.take();
                ResultSet rs = db.insert(req.getQuery());
                ResultQueue.getQueue().put(req.getUid(), rs);
            } catch (InterruptedException ex) {
                Logger.getLogger(QueueConsumerNoProxy.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SQLException ex) {
                Logger.getLogger(QueueConsumerNoProxy.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }

}
