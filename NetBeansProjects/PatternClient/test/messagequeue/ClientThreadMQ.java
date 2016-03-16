package messagequeue;

import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import static logic.MQRestCaller.GetFromMessageQueue;
import static logic.MQRestCaller.PlaceRequest;

public class ClientThreadMQ extends Thread {
    
    int idc;
    public static int numberOfClientsEnd;
    
    ClientThreadMQ(int idc) {
        this.idc = idc;
    }
    
    public void run() {
        
        String modeSelect = "placeIntoMessageQueueCustomProxy";
        //String modeSelect = "placeIntoMessageQueueNoProxy";
        //String modeSelect = "placeIntoMessageQueueRandomProxy";
        //String modeSelect = "placeIntoMessageQueueRobinProxy";
        
        System.out.println("Starting Clients Connections .. " + getIDC());
        
        int c;
        UUID uid = PlaceRequest(modeSelect);
        Object res = GetFromMessageQueue(uid);
        System.out.println("Connecting Clients to the Server .. " + getIDC());
        try {
            for (int j = 0; j < 10; j++) {
               uid = PlaceRequest(modeSelect);
               res = GetFromMessageQueue(uid);
            }
        sleep(30000);
        
        int f;
        for (int j = 0; j < 5; j++) {
            uid = PlaceRequest(modeSelect);
            res = GetFromMessageQueue(uid);
            }
        sleep(10000);
            
        int i;
        uid = PlaceRequest(modeSelect);
        res = GetFromMessageQueue(uid);
            
        sleep(5000);
        System.out.println("Disconnecting Clients .. " + getIDC());
        } 
        catch (InterruptedException ex) {
            Logger.getLogger(ClientThreadMQ.class.getName()).log(Level.SEVERE, null, ex);
        }
        numberOfClientsEnd++;
    }  
    
    public int getIDC() {
        return idc;
    }
    
    public static void startClients( int numberOfClients) {
        System.out.println("START OF THE TEST");
        for (int j = 0; j < numberOfClients; j++) {
            new ClientThreadMQ(j).start();
        }
    }
    
    
    public static void main (String [] args) {        
        
        int numberOfClients = 1;
        int numberOfTests = 1;
        
        for (int y = 0; y < numberOfTests; y++) {
            startClients(numberOfClients);
        }
        System.out.println("END OF THE TEST");
    }
}