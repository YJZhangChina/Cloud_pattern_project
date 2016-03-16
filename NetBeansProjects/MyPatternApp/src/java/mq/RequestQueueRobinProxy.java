
package mq;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import proxy.DBRobinProxy;
import db.SqlRequest;
import storage.MyClass;

/**
 *
 * @author benjamin
 */
public class RequestQueueRobinProxy {
    private static final Map<UUID, SqlRequest> queue1 = new HashMap<>();
    private static final Map<UUID, SqlRequest> queue2 = new HashMap<>();
    private static final Map<UUID, SqlRequest> queue3 = new HashMap<>();
    private static final Map<UUID, SqlRequest> queue4 = new HashMap<>();
    DBRobinProxy robinInstance = new DBRobinProxy();
    MyClass robinProx;
    
    Thread t;
    private static RequestQueueRobinProxy instance = null;
    
    private RequestQueueRobinProxy(){ 
        robinProx = (MyClass) robinInstance.newInstance(
                MyClass.class, new Class[]{MyClass.class});
    }
    
    public static RequestQueueRobinProxy getInstance() {
        if(instance == null){
            instance = new RequestQueueRobinProxy();
        }
        return instance;
    }
    
    public void AddRequest(UUID uid, SqlRequest req) {
        switch (req.getPriority()) {
            case 1:
                queue1.put(uid, req);
                break;
            case 2:
                queue2.put(uid, req);
                break;
            case 3:
                queue3.put(uid, req);
                break;
            case 4:
                queue4.put(uid, req);
                break;
            default:
                queue4.put(uid, req);
                break;
        }

    }
    
    public void ProcessRequests() {
        if (t == null || Thread.State.NEW == t.getState() || Thread.State.TERMINATED == t.getState()) {
            t = new Thread(new Runnable() {
                @Override
                public void run() {
                    if (queue1.isEmpty() && queue2.isEmpty() && queue3.isEmpty() && queue4.isEmpty()) {
                        try {
                            Thread.sleep(200);
                        } catch (InterruptedException ex) {
                            Logger.getLogger(RequestQueueCustomProxy.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    } else {
                        if (!queue1.isEmpty()) {
                            for (Iterator<Map.Entry<UUID, SqlRequest>> iterator = queue1.entrySet().iterator(); iterator.hasNext();) {
                                Map.Entry<UUID, SqlRequest> entry = iterator.next();
                                ResultSet rs = robinProx.query(entry.getValue().getQuery());
                                ResultQueue.getQueue().put(entry.getKey(), rs);
                                iterator.remove();
                            }
                        }

                        if (!queue2.isEmpty()) {
                            for (Iterator<Map.Entry<UUID, SqlRequest>> iterator = queue2.entrySet().iterator(); iterator.hasNext();) {
                                Map.Entry<UUID, SqlRequest> entry = iterator.next();
                                ResultSet rs = robinProx.query(entry.getValue().getQuery());
                                ResultQueue.getQueue().put(entry.getKey(), rs);
                                iterator.remove();
                            }
                        }

                        if (!queue3.isEmpty()) {
                            for (Iterator<Map.Entry<UUID, SqlRequest>> iterator = queue3.entrySet().iterator(); iterator.hasNext();) {
                                Map.Entry<UUID, SqlRequest> entry = iterator.next();
                                ResultSet rs = robinProx.query(entry.getValue().getQuery());
                                ResultQueue.getQueue().put(entry.getKey(), rs);
                                iterator.remove();
                            }
                        }

                        if (!queue4.isEmpty()) {
                            for (Iterator<Map.Entry<UUID, SqlRequest>> iterator = queue4.entrySet().iterator(); iterator.hasNext();) {
                                Map.Entry<UUID, SqlRequest> entry = iterator.next();
                                ResultSet rs = robinProx.query(entry.getValue().getQuery());
                                ResultQueue.getQueue().put(entry.getKey(), rs);
                                iterator.remove();
                            }
                        }
                    }
                }
            });
            t.start();
        }
    }
}
