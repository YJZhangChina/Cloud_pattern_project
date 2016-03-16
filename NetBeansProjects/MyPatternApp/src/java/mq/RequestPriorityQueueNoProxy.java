package mq;

import java.util.Comparator;
import java.util.UUID;
import java.util.concurrent.PriorityBlockingQueue;
import db.SqlRequest;

/**
 *
 * @author benjamin
 */
public class RequestPriorityQueueNoProxy {

    Comparator<SqlRequest> comparator;
    PriorityBlockingQueue<SqlRequest> queue;
    
    Thread t1;
    Thread t2;
    Thread t3;
    Thread t4;
    Thread t5;
    Thread t6;
    Thread t7;
    Thread t8;
    Thread t9;
    Thread t10;

    private static RequestPriorityQueueNoProxy instance = null;

    private RequestPriorityQueueNoProxy() {
        comparator = new SqlRequestComparator();
        queue = new PriorityBlockingQueue<>(10, comparator);
        
        t1 = new Thread(new QueueConsumerNoProxy(queue));
        t2 = new Thread(new QueueConsumerNoProxy(queue));
        t3 = new Thread(new QueueConsumerNoProxy(queue));
        t4 = new Thread(new QueueConsumerNoProxy(queue));
        t5 = new Thread(new QueueConsumerNoProxy(queue));
        t6 = new Thread(new QueueConsumerNoProxy(queue));
        t7 = new Thread(new QueueConsumerNoProxy(queue));
        t8 = new Thread(new QueueConsumerNoProxy(queue));
        t9 = new Thread(new QueueConsumerNoProxy(queue));
        t10 = new Thread(new QueueConsumerNoProxy(queue));
    }

    public static RequestPriorityQueueNoProxy getInstance() {
        if (instance == null) {
            instance = new RequestPriorityQueueNoProxy();
        }
        return instance;
    }

    public void AddRequest(UUID uid, SqlRequest req) {
        queue.add(req);
    }

    public void ProcessRequests() {
        if(!queue.isEmpty() ){
            if(t1.getState() == Thread.State.NEW || t1.getState() == Thread.State.TERMINATED){
                t1 = new Thread(new QueueConsumerNoProxy(queue));
                t1.start();
            }
            if(t2.getState() == Thread.State.NEW || t2.getState() == Thread.State.TERMINATED){
                t2 = new Thread(new QueueConsumerNoProxy(queue));
                t2.start();
            }
            if(t3.getState() == Thread.State.NEW || t3.getState() == Thread.State.TERMINATED){
                t3 = new Thread(new QueueConsumerNoProxy(queue));
                t3.start();
            }
            if(t4.getState() == Thread.State.NEW || t4.getState() == Thread.State.TERMINATED){
                t4 = new Thread(new QueueConsumerNoProxy(queue));
                t4.start();
            }
            if(t5.getState() == Thread.State.NEW || t5.getState() == Thread.State.TERMINATED){
                t5 = new Thread(new QueueConsumerNoProxy(queue));
                t5.start();
            }
            if(t6.getState() == Thread.State.NEW || t6.getState() == Thread.State.TERMINATED){
                t6 = new Thread(new QueueConsumerNoProxy(queue));
                t6.start();
            }
            if(t7.getState() == Thread.State.NEW || t7.getState() == Thread.State.TERMINATED){
                t7 = new Thread(new QueueConsumerNoProxy(queue));
                t7.start();
            }
            if(t8.getState() == Thread.State.NEW || t8.getState() == Thread.State.TERMINATED){
                t8 = new Thread(new QueueConsumerNoProxy(queue));
                t8.start();
            }
            if(t9.getState() == Thread.State.NEW || t9.getState() == Thread.State.TERMINATED){
                t9 = new Thread(new QueueConsumerNoProxy(queue));
                t9.start();
            }
            if(t10.getState() == Thread.State.NEW || t10.getState() == Thread.State.TERMINATED){
                t10 = new Thread(new QueueConsumerNoProxy(queue));
                t10.start();
            }
        }
    }
}


