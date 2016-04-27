import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author LE AN
 */

public class Gatekeeper {
    // Socket port number
	private static final int PORT_CLIENT = 8888;
	private static final int PORT_TH = 6666;
	
    public void init() {  
        try {  
            ServerSocket serverSocket = new ServerSocket(PORT_CLIENT);  
            while (true) {  
                // Once blocking, it means the connection is built between the server and the client  
                Socket client = serverSocket.accept();  
                // Handle a connection
                new HandlerThread(client);  
            }  
        } catch (Exception e) {  
            System.out.println("Server exception: " + e.getMessage());  
        }  
    }  
  
    private class HandlerThread implements Runnable {  
        private Socket socket;  
        public HandlerThread(Socket client) {  
            socket = client;  
            new Thread(this).start();  
        }  
  	  	
		private String communicateWithTrustedHost (String str2TH) {
			try {
				// Create a stream socket and connect it to the server
				Socket socketWithTH = new Socket("172.31.21.220", PORT_TH);  
				//	Input and output variables
				DataOutputStream streamSent = new DataOutputStream(socketWithTH.getOutputStream()); 
				DataInputStream streamReceived = new DataInputStream(socketWithTH.getInputStream());				
				//  Send data to the server    
				streamSent.writeUTF(str2TH); 
				//  Message sent back from the server
				String ret = streamReceived.readUTF();   
				//  Close connection
				streamSent.close();
				streamReceived.close();
				socketWithTH.close();
				return ret;
			} catch (Exception e) {
            	System.out.println("Client exception: " + e.getMessage()); 
    		}
			return null;
		}
		
        public void run() {
            try {  
                // Read data from the client  
                DataInputStream received = new DataInputStream(socket.getInputStream());
				DataOutputStream response = new DataOutputStream(socket.getOutputStream());
                String clientInputStr = received.readUTF();
				
				//	Extract received data 
				String elems[] = clientInputStr.split("\\|\\|");
				String hashStr = elems[0];
				String sqlQuery = elems[1];
				//	Filter request by the first character ([a-z]: safe, [0-9]: malicious)
				int firstChar = hashStr.toCharArray()[0];
				if (firstChar>=97 && firstChar<=122) {
					String dataTH = communicateWithTrustedHost(sqlQuery);
					// Response to clients
					response.writeUTF(dataTH);  
					System.out.println("Accept and process a valid request.");
				} else {
					// Response to clients
					response.writeUTF("Query refused by the gatekeeper.");
					System.out.println("Refuse an invalid request.");  
				}
				
                response.close();  
                received.close();  
            } catch (Exception e) {  
                System.out.println("Server runtime exception: " + e.getMessage());  
            } 
        }  
    }
	
    public static void main(String[] args) {  
		// 	Start socket server
		System.out.println("Gatekeeper is running ...\n");  
        Gatekeeper server = new Gatekeeper();  
        server.init(); 
    }   
}
