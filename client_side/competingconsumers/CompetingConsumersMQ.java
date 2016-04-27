/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package competingconsumers;

import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import static logic.MQRestCaller.GetFromMessageQueue;
import static logic.MQRestCaller.PlaceRequest;
import static competingconsumers.SocketConnection.sendData2Server;

/**
 *
 * @author LE AN
 */
public class CompetingConsumersMQ extends Thread {
    int idc;
    public static int numberOfClientsEnd;
    
    CompetingConsumersMQ(int idc) {
        this.idc = idc;        
    }
    
    @Override
    public void run() {
        
        //String modeSelect = "placeIntoMessageQueueCustomProxy";
        //String modeSelect = "placeIntoMessageQueueNoProxy";
        String modeSelect = "placeIntoMessageQueueRandomProxy";
        //String modeSelect = "placeIntoMessageQueueRobinProxy";
        
        System.out.println("Starting Clients Connections .. " + getIDC());
//        System.out.println("Connecting Clients to the Server .. " + getIDC());
        

        try 
        {   
            System.out.println("Connecting Clients to the Server .. " + getIDC());
                        
            //  Insert request for 5 times, then wait for 5 seconds
            for (int i = 0; i < 5; i++) {
                UUID uid = PlaceRequest(modeSelect);
                Object res = GetFromMessageQueue(uid);
                String hashToken = uid.toString() + "+" +res.toString();
                sendData2Server(hashToken);
            }
            System.out.println("Waiting for 5 seconds (1st stop)");
            sleep(5000);
            
            //  Insert request for 100 times, then wait for 5 seconds
            for (int i = 0; i < 100; i++) {
                UUID uid = PlaceRequest(modeSelect);
                Object res = GetFromMessageQueue(uid);
                String hashToken = uid.toString() + "+" +res.toString();
                sendData2Server(hashToken);
            }
            System.out.println("Waiting for 5 seconds (2nd stop)");
            sleep(5000);
            
            //  Insert request for 5 times
            for (int i = 0; i < 5; i++) {
                UUID uid = PlaceRequest(modeSelect);
                Object res = GetFromMessageQueue(uid);
                String hashToken = uid.toString() + "+" +res.toString();
                sendData2Server(hashToken);
            }
            
            System.out.println("Disconnecting Clients .. " + getIDC());
        } 
        catch (InterruptedException ex) {
            Logger.getLogger(CompetingConsumersMQ.class.getName()).log(Level.SEVERE, null, ex);
        }
        numberOfClientsEnd++;
    }  
    
    public int getIDC() {
        return idc;
    }
    
    public static void startClients( int numberOfClients) {
        System.out.println("START OF THE TEST");
        for (int j = 0; j < numberOfClients; j++) {
            new CompetingConsumersMQ(j).start();
        }
    }
    
    public static void main (String [] args) {        
        
        int numberOfClients = 10;
        int numberOfTests = 1;
                
        for (int y = 0; y < numberOfTests; y++) {
            startClients(numberOfClients);
        }
        System.out.println("END OF THE TEST");        
    }
}
