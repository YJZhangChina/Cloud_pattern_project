/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gatekeeper;


import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import static logic.MQRestCaller.GetFromMessageQueue;
import static logic.MQRestCaller.PlaceRequest;
import static gatekeeper.DataQuery.request2Server;

/**
 *
 * @author LE AN
 */
public class GatekeeperMQ extends Thread {
    int idc;
    public static int numberOfClientsEnd;
    
    GatekeeperMQ (int idc) {
        this.idc = idc;        
    }
    
    @Override
    public void run() {
        String modeSelect = "placeIntoMessageQueueRandomProxy";
        
        System.out.println("Starting Clients Connections .. " + getIDC());
        
        try 
        {   
            System.out.println("Connecting Clients to the Server .. " + getIDC());
           
            //  Insert request for 5 times, then wait for 5 seconds
            for (int i = 0; i < 5; i++) {
                UUID uid = PlaceRequest(modeSelect);
                Object res = GetFromMessageQueue(uid);
                String hashToken = uid.toString() + "+" +res.toString();
                request2Server(hashToken, "read");
            }
            System.out.println("Waiting for 5 seconds");
            sleep(5000);
            
            //  Insert request for 100 times, then wait for 5 seconds
            for (int i = 0; i < 100; i++) {
                UUID uid = PlaceRequest(modeSelect);
                Object res = GetFromMessageQueue(uid);
                String hashToken = uid.toString() + "+" +res.toString();
                request2Server(hashToken, "read");
            }
            System.out.println("Waiting for 5 seconds");
            sleep(5000);
            
            System.out.println("Waiting for 5 seconds");
            //  Insert request for 5 times
            for (int i = 0; i < 5; i++) {
                UUID uid = PlaceRequest(modeSelect);
                Object res = GetFromMessageQueue(uid);
                String hashToken = uid.toString() + "+" +res.toString();
                request2Server(hashToken, "write");
            }
            
            System.out.println("Disconnecting Clients .. " + getIDC());
        } 
        catch (InterruptedException ex) {
            Logger.getLogger(GatekeeperMQ.class.getName()).log(Level.SEVERE, null, ex);
        }
        numberOfClientsEnd++;
    }  
    
    public int getIDC() {
        return idc;
    }
    
    public static void startClients( int numberOfClients) {
        System.out.println("START OF THE TEST");
        for (int j = 0; j < numberOfClients; j++) {
            new GatekeeperMQ(j).start();
        }
    }
    
    public static void main (String [] args) {        
        
        int numberOfClients = 100;
        int numberOfTests = 1;
                
        for (int y = 0; y < numberOfTests; y++) {
            startClients(numberOfClients);
        }
        System.out.println("END OF THE TEST");        
    }
}
