package monitoring;

import db.ConnectionParam;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author benjamin
 */
public class Watcher {

    private static Watcher instance;
    private boolean isStarted;
    private int limit = 0;
    private LatencyMonitor lm = new LatencyMonitor(null, 3306);
    private ActiveConnectionMonitor acm = new ActiveConnectionMonitor(null);
    private List<ConnectionParam> dbs = new ArrayList();
    private List<MonitoringResult> lastResults = new ArrayList<>();

    private Watcher(List<ConnectionParam> dbs) {
        this.dbs = dbs;
    }

    public static synchronized Watcher getInstance(List<ConnectionParam> dbs) {
        if (instance == null) {
            instance = new Watcher(dbs);
        }

        return instance;
    }

    public void startMonitoring() {
        if (isStarted) {
            return;
        }
        
        isStarted = true;
//        System.out.println("Started");        

        Thread t = new Thread(new Runnable() {
            
            @Override
            public void run() {
//                while (true) {
//                    limit++;
//                    if (limit > 10) {
////                        System.out.println("Stoped");
//                        limit = 0;
//                        isStarted = false;
//                        break;
//                    }

                    List<MonitoringResult> results = new ArrayList<>();

                    for (ConnectionParam slave : dbs) {
                        lm.setIp(slave.getHost());
                        acm.setDb(slave);

                        results.add(new MonitoringResult(lm.Ping(), acm.GetNumberOfActiveConnections()));
                    }
                    lastResults = results;

                    try {
                        Thread.sleep(500);
                        isStarted = false;
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Watcher.class.getName()).log(Level.SEVERE, null, ex);
                    }
//                }
            }
        });
        t.start();
       
    }

    public ConnectionParam getBestDb() {
        int loopIndex = 0;
        long bestMin = Integer.MAX_VALUE;
        int bestSlaveIndex = 0;

        for (MonitoringResult res : lastResults) {
            long score = res.getActiveConnections() + res.getPing();
            if (score < bestMin) {
                bestMin = score;
                bestSlaveIndex = loopIndex;
            }
            loopIndex++;
        }
        return dbs.get(bestSlaveIndex);
    }

    /**
     * @return the isStarted
     */
    public boolean isIsStarted() {
        return isStarted;
    }
}
