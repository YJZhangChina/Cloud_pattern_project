/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package competingconsumers;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

/**
 *
 * @author LE AN
 */

public class SocketConnection {
    //	Server's IP address
    public static final String MASTER_IP = "52.38.139.34";
    //	Server's port number
    public static final int PORT = 8888;
    //  SQL query statement
    public static final String SQL_QUERY = "INSERT INTO film (title, description, release_year, language_id) "
                                         + "VALUES ('sample_movie', 'This is just a test', 2016, 1)";
   
    //   Send the client hash string to the server 
    public static void sendData2Server (String hashToken) {
        Socket socket;
    	try {
            // Create a stream socket and connect it to the server
	    socket = new Socket(MASTER_IP, PORT);  
            //	Input and output variables
            DataOutputStream out = new DataOutputStream(socket.getOutputStream()); 
            DataInputStream input = new DataInputStream(socket.getInputStream());
            
            //  Send data to the server             
            out.writeUTF(hashToken + "||" + SQL_QUERY);
            
            //  Message sent back from the server
            String ret = input.readUTF();   
            System.out.println("Returned message: \"" + ret + "\"");
            
            //  Close connection
            input.close();
            out.close();
            socket.close();
            
    	} catch (Exception e) {
            System.out.println("Client exception: " + e.getMessage()); 
    	}
    }
}
