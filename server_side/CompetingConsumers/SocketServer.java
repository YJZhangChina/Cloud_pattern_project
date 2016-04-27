//import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

import java.sql.*;

/**
 *
 * @author LE AN
 */

public class SocketServer {
    // Socket port number
	public static final int PORT = 8888;	
	//	Variables for node IPs
	private static final String urlMaster = "jdbc:mysql://localhost:3306/sakila";
	private static final String urlSlave1 = "jdbc:mysql://172.31.23.42:3306/sakila";
	private static final String urlSlave2 = "jdbc:mysql://172.31.45.6:3306/sakila";
	private static final String urlSlave3 = "jdbc:mysql://172.31.45.5:3306/sakila";
	private static final String user = "root";
	private static final String password = "poly";
	// Variables for connection to different nodes
	static Connection connMaster = null;
	static Statement stmtMaster = null;
	static Connection connSlave1 = null;
	static Statement stmtSlave1 = null;
	static Connection connSlave2 = null;
	static Statement stmtSlave2 = null;
	static Connection connSlave3 = null;
	static Statement stmtSlave3 = null;
	

    public void init() {  
        try {  
            ServerSocket serverSocket = new ServerSocket(PORT);  
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
  
        public void run() {
			// Initialize variables
			String sql = null;
			ResultSet res = null;
			int priority;
			
            try {  
                // Read data from the client  
                DataInputStream input = new DataInputStream(socket.getInputStream());
                String clientInputStr = input.readUTF();
				
				//	Extract data then write them into a MySQL node 
				String hashStr = clientInputStr.split("\\|\\|")[0];
				priority = parsePriority(hashStr);
				sql = clientInputStr.split("\\|\\|")[1];

				//	Choose a node to write based on the priority value
				String nodeName = null;
				if (priority == 0) {
					nodeName = "Master";
					System.out.println("Inserting data into Master");
					writeQuery(sql, stmtMaster);
				} else if (priority == 1) {
					nodeName = "Slave1";
					System.out.println("Inserting data into Slave1");
					writeQuery(sql, stmtSlave1);
				} else if (priority == 2) {
					nodeName = "Slave2";
					System.out.println("Inserting data into Slave2");
					writeQuery(sql, stmtSlave2);
				} else if (priority == 3) {
					nodeName = "Slave3";
					System.out.println("Inserting data into Slave3");
					writeQuery(sql, stmtSlave3);
				}
				
				// Response to clients
				DataOutputStream out = new DataOutputStream(socket.getOutputStream());
				out.writeUTF(String.format("Data inserted into %s.", nodeName));  
  
                out.close();  
                input.close();  
            } catch (Exception e) {  
                System.out.println("Server runtime exception: " + e.getMessage());  
            } 
        }  
    }
	
	//	Make connection to different DB nodes
	private static void initDB () {
		try {
			Class.forName("com.mysql.jdbc.Driver");
            //	Connect to the DB
			connMaster = DriverManager.getConnection(urlMaster, user, password);
            System.out.println("Successfully connected to Master.");
			stmtMaster = connMaster.createStatement();
			//	Connect to Slave1
			connSlave1 = DriverManager.getConnection(urlSlave1, user, password);
            System.out.println("Successfully connected to Slave1.");
			stmtSlave1 = connSlave1.createStatement();
			//	Connect to Slave2
			connSlave2 = DriverManager.getConnection(urlSlave2, user, password);
            System.out.println("Successfully connected to Slave2.");
			stmtSlave2 = connSlave2.createStatement();
			//	Connect to Slave3
			connSlave3 = DriverManager.getConnection(urlSlave3, user, password);
            System.out.println("Successfully connected to Slave3.");
			stmtSlave3 = connSlave3.createStatement();
			System.out.println("********************");
		} catch (Exception e) {
			System.out.println("DB connection exception.");
            e.printStackTrace();
        }
	}
	
	private static void closeDBConnection () {
		try {
			stmtMaster.close();
			stmtSlave1.close();
			stmtSlave2.close();
			stmtSlave3.close();
			connMaster.close();
			connSlave1.close();
			connSlave2.close();
			connSlave3.close();
		} catch (Exception e) {
			System.out.println("DB disconnection exception.");
            e.printStackTrace();
        }
	}
	
    private static void readQuery (String sql, Statement stmt) {
		try {
			ResultSet res = stmt.executeQuery(sql);
			while(res.next())
			{
				System.out.println(res.getString(2));
			}
			System.out.println("--------------------");
			res.close();
		} catch (Exception e) {
			System.out.println("DB query exception.");
            e.printStackTrace();
        }
	}
	
    private static void writeQuery (String sql, Statement stmt) {
		try {
			stmt.executeUpdate(sql);
		} catch (Exception e) {
			System.out.println("DB query exception.");
            e.printStackTrace();
        }
	}
	
	//	Sum characters by their ASCII value (only consider [0-9a-z])
	//	Return a priority value based on the sum ASCII value modulo by 4
	private static int parsePriority (String hashStr) {
        int digitSum = 0;
        for (char ch : hashStr.toCharArray()){
            int digitValue = (int) ch;
            if ((digitValue>=48 && digitValue<=57) || (digitValue>=97 && digitValue<=122))
                digitSum += digitValue;
        }
        return digitSum%4;
    }
	
    public static void main(String[] args) {  
		//	Connection to different DB nodes
		initDB();
		// 	Start socket server
		System.out.println("Socket server is running ...\n");  
        SocketServer server = new SocketServer();  
        server.init(); 
		
    }   
}
