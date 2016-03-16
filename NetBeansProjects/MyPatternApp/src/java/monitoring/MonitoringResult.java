/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package monitoring;

/**
 *
 * @author benjamin
 */
public class MonitoringResult {
    private long ping;
    private int activeConnections;

    public MonitoringResult(long ping, int activeConnections) {
        this.ping = ping;
        this.activeConnections = activeConnections;
    }

    /**
     * @return the ping
     */
    public long getPing() {
        return ping;
    }

    /**
     * @return the activeConnections
     */
    public int getActiveConnections() {
        return activeConnections;
    }
    
}
