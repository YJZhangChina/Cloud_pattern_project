package messagequeue;

import java.util.UUID;
import org.junit.Test;
import static org.junit.Assert.*;
import static logic.MQRestCaller.SelectSimple;
import static logic.MQRestCaller.PlaceRequest;
import static logic.MQRestCaller.GetFromMessageQueue;

public class MQPerformanceTest {
        
    public MQPerformanceTest() {
    }

    @Test
    public void RandomQueryWithMessageQueueCustomProxy() {
        int numberOfLoops = 10;
        int numQueries = 1000;
        
        long start = -System.nanoTime();
        for (int j = 0; j < numberOfLoops; j++) {
            for (int i = 0; i < numQueries; i++) {
                UUID uid = PlaceRequest("placeIntoMessageQueueCustomProxy");
                Object res = GetFromMessageQueue(uid);
                
                assertNotNull(res);
            }
        }

        start += System.nanoTime();
        System.out.println("Time for "+numQueries+" simple select with custom proxy: " 
                + (start / 1000000) / numberOfLoops + "ms");
    }
    
    @Test
    public void RandomQueryWithMessageQueueRobinProxy() {
        int numberOfLoops = 1;
        int numQueries = 1000;
        long start = -System.nanoTime();
        for (int j = 0; j < numberOfLoops; j++) {
            for (int i = 0; i < numQueries; i++) {
                UUID uid = PlaceRequest("placeIntoMessageQueueRobinProxy");
                Object res = GetFromMessageQueue(uid);
                
                assertNotNull(res);
            }
        }

        start += System.nanoTime();
        System.out.println("Time for "+numQueries+" simple select with round robin proxy: " 
                + (start / 1000000) / numberOfLoops + "ms");
    }
    
    @Test
    public void RandomQueryWithMessageQueueRandomProxy() {
        int numberOfLoops = 2;
        int numQueries = 1000;
        long start = -System.nanoTime();
        for (int j = 0; j < numberOfLoops; j++) {
            for (int i = 0; i < numQueries; i++) {
                UUID uid = PlaceRequest("placeIntoMessageQueueRandomProxy");
                Object res = GetFromMessageQueue(uid);
                
                assertNotNull(res);
            }
        }

        start += System.nanoTime();
        System.out.println("Time for "+numQueries+" simple select with random proxy: " 
                + (start / 1000000) / numberOfLoops + "ms");
    }
    
    @Test
    public void RandomQueryWithMessageQueueNoProxy() {
        int numberOfLoops = 1;
        int numQueries = 1000;
        long start = -System.nanoTime();
        
        for (int j = 0; j < numberOfLoops; j++) {
            for (int i = 0; i < numQueries; i++) {
                UUID uid = PlaceRequest("placeIntoMessageQueueNoProxy");
                Object res = GetFromMessageQueue(uid);
                
                assertNotNull(res);
            }
        }

        start += System.nanoTime();
        System.out.println("Time for "+numQueries+" simple select without proxy: " 
                + (start / 1000000) / numberOfLoops + "ms");
    }
    
    @Test
    public void RandomQueryNoMQCustomProxy(){        
        long start = -System.nanoTime();
        for (int j = 0; j < 1; j++) {
            for (int i = 0; i < 10000; i++) {
                int o = Integer.parseInt(SelectSimple("getAvgCustom", 4));
                assertNotNull(o);
                assertFalse(o == -1);
            }
        }

        start += System.nanoTime();
        System.out.println("Time for x simple select with custom proxy: " + (start / 1000000) / 1 + "ms");
    }
    
    @Test
    public void RandomQueryNoMQNoProxy(){        
        long start = -System.nanoTime();
        for (int j = 0; j < 1; j++) {
            for (int i = 0; i < 10000; i++) {
                int o = Integer.parseInt(SelectSimple("getRandomNoProxy", 4));
                assertNotNull(o);
                assertFalse(o == -1);
            }
        }

        start += System.nanoTime();
        System.out.println("Time for 10000 random select without proxy and MQ : " + (start / 1000000) / 1 + "ms");
    }
    
    @Test
    public void RandomQueryNoMQRandomProxy(){        
        long start = -System.nanoTime();
        for (int j = 0; j < 1; j++) {
            for (int i = 0; i < 10000; i++) {
                int o = Integer.parseInt(SelectSimple("getAvgRandom", 4));
                assertNotNull(o);
                assertFalse(o == -1);
            }
        }

        start += System.nanoTime();
        System.out.println("Time for x simple select with random proxy: " + (start / 1000000) / 1 + "ms");
    }

    @Test
    public void RandomQueryNoMQRobinProxy(){        
        long start = -System.nanoTime();
        for (int j = 0; j < 1; j++) {
            for (int i = 0; i < 10000; i++) {
                int o = Integer.parseInt(SelectSimple("getAvgRobin", 4));
                assertNotNull(o);
                assertFalse(o == -1);
            }
        }

        start += System.nanoTime();
        System.out.println("Time for x simple select with robin proxy: " + (start / 1000000) / 1 + "ms");
    }
}
