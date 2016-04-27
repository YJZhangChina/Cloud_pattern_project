package mq;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import proxy.DBCustomProxy;
import db.SqlRequest;
import storage.MyClass;

/**
 *
 * @author benjamin
 */
public class RequestQueueCustomProxy {

    private static final Map<UUID, SqlRequest> queue1 = new HashMap<>();
    private static final Map<UUID, SqlRequest> queue2 = new HashMap<>();
    private static final Map<UUID, SqlRequest> queue3 = new HashMap<>();
    private static final Map<UUID, SqlRequest> queue4 = new HashMap<>();
    
    final BlockingQueue<SqlRequest> queueA = new LinkedBlockingQueue<>();
    final BlockingQueue<SqlRequest> queueB = new LinkedBlockingQueue<>();
    final BlockingQueue<SqlRequest> queueC = new LinkedBlockingQueue<>();
    final BlockingQueue<SqlRequest> queueD = new LinkedBlockingQueue<>();
    
    DBCustomProxy customInstance = new DBCustomProxy();
    MyClass customProx;

    Thread t;
    Thread t1;
    Thread t2;
    Thread t3;
    Thread t4;
    
    private static RequestQueueCustomProxy instance = null;

    private RequestQueueCustomProxy() {
        customProx = (MyClass) customInstance.newInstance(
                MyClass.class, new Class[]{MyClass.class});
    }

    public static RequestQueueCustomProxy getInstance() {
        if (instance == null) {
            instance = new RequestQueueCustomProxy();
        }
        return instance;
    }

    public void AddRequest(UUID uid, SqlRequest req) {
        switch (req.getPriority()) {
            case 1:
//                queue1.put(uid, req);
                queueA.add(req);
                break;
            case 2:
//                queue2.put(uid, req);
                queueB.add(req);
                break;
            case 3:
//                queue3.put(uid, req);
                queueC.add(req);
                break;
            case 4:
//                queue4.put(uid, req);
                queueD.add(req);
                break;
            default:
//                queue4.put(uid, req);
                queueD.add(req);
                break;
        }

    }
    
    private void InitT1(){
        t1 = new Thread(new Runnable() {

            @Override
            public void run() {
                for (Iterator<Map.Entry<UUID, SqlRequest>> iterator = queue1.entrySet().iterator(); iterator.hasNext();) {
                    Map.Entry<UUID, SqlRequest> entry = iterator.next();
                    String q = entry.getValue().getQuery().replace("SELECT ", "SELECT HIGH_PRIORITY ");
                    ResultSet rs = customProx.query(q);
                    ResultQueue.getQueue().put(entry.getKey(), rs);
                    iterator.remove();
                }
            }
        });
    }
    
    private void InitT2(){
        t2 = new Thread(new Runnable() {

            @Override
            public void run() {
                for (Iterator<Map.Entry<UUID, SqlRequest>> iterator = queue2.entrySet().iterator(); iterator.hasNext();) {
                    Map.Entry<UUID, SqlRequest> entry = iterator.next();
                    ResultSet rs = customProx.query(entry.getValue().getQuery());
                    ResultQueue.getQueue().put(entry.getKey(), rs);
                    iterator.remove();
                }
            }
        });
    }
    
    private void InitT3(){
        t3 = new Thread(new Runnable() {
            @Override
            public void run() {
                for (Iterator<Map.Entry<UUID, SqlRequest>> iterator = queue3.entrySet().iterator(); iterator.hasNext();) {
                    Map.Entry<UUID, SqlRequest> entry = iterator.next();
                    ResultSet rs = customProx.query(entry.getValue().getQuery());
                    ResultQueue.getQueue().put(entry.getKey(), rs);
                    iterator.remove();
                }
            }
        });
    }
    
    private void InitT4(){
        t4 = new Thread(new Runnable() {

            @Override
            public void run() {
                for (Iterator<Map.Entry<UUID, SqlRequest>> iterator = queue4.entrySet().iterator(); iterator.hasNext();) {
                    Map.Entry<UUID, SqlRequest> entry = iterator.next();
                    ResultSet rs = customProx.query(entry.getValue().getQuery());
                    ResultQueue.getQueue().put(entry.getKey(), rs);
                    iterator.remove();
                }
            }
        });
    }

    public void ProcessRequests() {
        if(!queueA.isEmpty()){
            new Thread(new QueueConsumer(customProx, queueA)).start();
            new Thread(new QueueConsumer(customProx, queueA)).start();
            new Thread(new QueueConsumer(customProx, queueA)).start();
            new Thread(new QueueConsumer(customProx, queueA)).start();
            
//            if(t1 == null || Thread.State.NEW == t1.getState() || Thread.State.TERMINATED == t1.getState()){
//                InitT1();
//                t1.start();
//            }
        }
        
        if(!queueB.isEmpty()){
            new Thread(new QueueConsumer(customProx, queueB)).start();
            new Thread(new QueueConsumer(customProx, queueB)).start();
            new Thread(new QueueConsumer(customProx, queueB)).start();
            new Thread(new QueueConsumer(customProx, queueB)).start();
//            if(t2 == null || Thread.State.NEW == t2.getState() || Thread.State.TERMINATED == t2.getState()){
//                InitT2();
//                t2.start();
//            }
        }
        
        if(!queueC.isEmpty()){
            new Thread(new QueueConsumer(customProx, queueC)).start();
            new Thread(new QueueConsumer(customProx, queueC)).start();
            new Thread(new QueueConsumer(customProx, queueC)).start();
            new Thread(new QueueConsumer(customProx, queueC)).start();
//            if(t3 == null || Thread.State.NEW == t3.getState() || Thread.State.TERMINATED == t3.getState()){
//                InitT3();
//                t3.start();
//            }
        }
        
        if(!queueD.isEmpty()){
            new Thread(new QueueConsumer(customProx, queueD)).start();
            new Thread(new QueueConsumer(customProx, queueD)).start();
            new Thread(new QueueConsumer(customProx, queueD)).start();
            new Thread(new QueueConsumer(customProx, queueD)).start();
//            if(t4 == null || Thread.State.NEW == t4.getState() || Thread.State.TERMINATED == t4.getState()){
//                InitT4();
//                t4.start();
//            }
        }
        
//        if (t == null || Thread.State.NEW == t.getState() || Thread.State.TERMINATED == t.getState()) {
//            t = new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    if (queue1.isEmpty() && queue2.isEmpty() && queue3.isEmpty() && queue4.isEmpty()) {
//                        try {
//                            Thread.sleep(200);
//                        } catch (InterruptedException ex) {
//                            Logger.getLogger(RequestQueueCustomProxy.class.getName()).log(Level.SEVERE, null, ex);
//                        }
//                    } else {
//                        if (!queue1.isEmpty()) {
//                            for (Iterator<Map.Entry<UUID, SqlRequest>> iterator = queue1.entrySet().iterator(); iterator.hasNext();) {
//                                Map.Entry<UUID, SqlRequest> entry = iterator.next();
//                                String q = entry.getValue().getQuery().replace("SELECT ", "SELECT HIGH_PRIORITY ");
//                                ResultSet rs = customProx.query(q);
//                                ResultQueue.getQueue().put(entry.getKey(), rs);
//                                iterator.remove();
//                            }
//                        }
//
//                        if (!queue2.isEmpty()) {
//                            for (Iterator<Map.Entry<UUID, SqlRequest>> iterator = queue2.entrySet().iterator(); iterator.hasNext();) {
//                                Map.Entry<UUID, SqlRequest> entry = iterator.next();
//                                ResultSet rs = customProx.query(entry.getValue().getQuery());
//                                ResultQueue.getQueue().put(entry.getKey(), rs);
//                                iterator.remove();
//                            }
//                        }
//
//                        if (!queue3.isEmpty()) {
//                            for (Iterator<Map.Entry<UUID, SqlRequest>> iterator = queue3.entrySet().iterator(); iterator.hasNext();) {
//                                Map.Entry<UUID, SqlRequest> entry = iterator.next();
//                                ResultSet rs = customProx.query(entry.getValue().getQuery());
//                                ResultQueue.getQueue().put(entry.getKey(), rs);
//                                iterator.remove();
//                            }
//                        }
//
//                        if (!queue4.isEmpty()) {
//                            for (Iterator<Map.Entry<UUID, SqlRequest>> iterator = queue4.entrySet().iterator(); iterator.hasNext();) {
//                                Map.Entry<UUID, SqlRequest> entry = iterator.next();
//                                ResultSet rs = customProx.query(entry.getValue().getQuery());
//                                ResultQueue.getQueue().put(entry.getKey(), rs);
//                                iterator.remove();
//                            }
//                        }
//                    }
//                }
//            });
//            t.start();
//        }
    }
}
