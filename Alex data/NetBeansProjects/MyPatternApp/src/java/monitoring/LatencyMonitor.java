package monitoring;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;

/**
 *
 * @author benjamin
 */
public class LatencyMonitor {

    private String ip;
    private int port;

    public LatencyMonitor(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public long Ping() {
        String hostaddr = null;
        try {
            hostaddr = InetAddress.getByName(getIp()).getHostAddress();
        } catch (UnknownHostException e) {
            System.out.println("Invalid Host entered.");
//            System.exit(0);
        }
                
        int total = 0;
        long totalping = 0;
        Socket s = null;
        
        while (total < 2) {
            total++;
            long start = -System.currentTimeMillis();

            SocketAddress sockaddr = new InetSocketAddress(hostaddr, port);
            s = new Socket();
            try {
                s.connect(sockaddr, 1000);
            } catch (IOException ex) {
                System.out.println("Socket Request[" + ip + "]: Connection timed out.");
                return -1;
            }

            start += System.currentTimeMillis();
            totalping += start;
        }
        return (long) (totalping / 2);
    }

    /**
     * @return the ip
     */
    public String getIp() {
        return ip;
    }

    /**
     * @param ip the ip to set
     */
    public void setIp(String ip) {
        this.ip = ip;
    }

    /**
     * @return the port
     */
    public int getPort() {
        return port;
    }

    /**
     * @param port the port to set
     */
    public void setPort(int port) {
        this.port = port;
    }
}
