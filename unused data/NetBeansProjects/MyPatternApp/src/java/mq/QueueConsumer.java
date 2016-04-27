package mq;

import db.SqlRequest;
import java.sql.ResultSet;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import storage.MyClass;

/**
 *
 * @author benjamin
 */
public class QueueConsumer implements Runnable {

    private final BlockingQueue<SqlRequest> queue;
    MyClass prox;

    public QueueConsumer(MyClass proxy, BlockingQueue<SqlRequest> queue) {
        this.queue = queue;
        this.prox = proxy;
    }

    @Override
    public void run() {
        while (!queue.isEmpty()) {
            SqlRequest req;
            try {
                req = queue.take();
                ResultSet rs = prox.query(req.getQuery());
                ResultQueue.getQueue().put(req.getUid(), rs);
            } catch (InterruptedException ex) {
                Logger.getLogger(QueueConsumer.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }

}
