/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gatekeeper;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

/**
 *
 * @author LE AN
 */
public class DataQuery {    
    //	Server's IP address
    public static final String GATEKEEPER_IP = "52.25.127.42";
    //	Server's port number
    public static final int PORT = 8888;
    //  SQL query statement
    public static final String SQL_INSERT = "INSERT INTO film (title, description, release_year, language_id) "
                                         + "VALUES ('sample_movie', 'This is just a test', 2016, 1)";
    public static final String SQL_SELECT = "SELECT release_year FROM film WHERE film_id = 500";
    
//   Send the client hash string to the server 
    public static void request2Server (String hashToken, String mode) {
        Socket socket;
    	try {
            // Create a stream socket and connect it to the server
	    socket = new Socket(GATEKEEPER_IP, PORT);  
            //	Input and output variables
            DataOutputStream sent = new DataOutputStream(socket.getOutputStream()); 
            DataInputStream received = new DataInputStream(socket.getInputStream());
            
            //  Send data to the server
            if (mode.equals("read"))
                sent.writeUTF(hashToken + "||" + SQL_SELECT);
            else 
                sent.writeUTF(hashToken + "||" + SQL_INSERT);
            
            
            //  Message sent back from the server
            String ret = received.readUTF();   
            System.out.println("Returned message: \"" + ret + "\"");
            
            //  Close connection
            sent.close();
            received.close();
            socket.close();
            
    	} catch (Exception e) {
            System.out.println("Client exception: " + e.getMessage()); 
    	}
    }
}
