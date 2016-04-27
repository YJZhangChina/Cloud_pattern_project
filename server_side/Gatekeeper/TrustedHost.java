import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.*;

public class TrustedHost {
    // Socket port number
	public static final int PORT = 6666;	
	//	Variables for the sensitive node IP
	private static final String url = "jdbc:mysql://172.31.26.111:3306/sakila";
	private static final String user = "root";
	private static final String password = "poly";
	// Variables for the connection
	static Connection conn = null;
	static Statement stmt = null;
	
    public void initSocket() {  
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
	
	//	Make connection to the DB node
	private static void initDBConnection () {
		try {
			Class.forName("com.mysql.jdbc.Driver");
            //	Connect to the DB
			conn = DriverManager.getConnection(url, user, password);
            System.out.println("Successfully connected to DataNode.");
			stmt = conn.createStatement();
			System.out.println("********************");
		} catch (Exception e) {
			System.out.println("DB connection exception.");
            e.printStackTrace();
        }
	}
	
	//	Close connection with the DB node
	private static void closeDBConnection () {
		try {
			conn.close();
			stmt.close();
		} catch (Exception e) {
			System.out.println("DB disconnection exception.");
            e.printStackTrace();
        }
	}
	
    //	Read from DB
	private static String readQuery (String sql, Statement stmt) {
		try {
			//	Receive and concatenate results (in this TP, there is only one result)
			ResultSet res = stmt.executeQuery(sql);
			res.last();
			String dataStr = res.getString(1);
			//	Close the result set
			res.close();
			//	Return results without the last character (last comma)
			return dataStr;//.substring(0, dataStr.length()-1);
			
		} catch (Exception e) {
			System.out.println("DB query exception.");
            e.printStackTrace();
        }
		return null;
	}
	
    //	Write data to DB
	private static void writeQuery (String sql, Statement stmt) {
		try {
			stmt.executeUpdate(sql);
		} catch (Exception e) {
			System.out.println("DB query exception.");
            e.printStackTrace();
        }
	}
	
	
    private class HandlerThread implements Runnable {  
        private Socket socket;  
        public HandlerThread(Socket client) {  
            socket = client;  
            new Thread(this).start();  
        }  
		public void run () {
			try { 
	            // 	Read data from the client  
	            DataInputStream input = new DataInputStream(socket.getInputStream());
				DataOutputStream output = new DataOutputStream(socket.getOutputStream());
	            String clientInputStr = input.readUTF();
				
				//	Send query to the MySQL datanode
				String sql = clientInputStr;
				if (sql.startsWith("INSERT")) {
					System.out.println("Insert data into the database.");
					writeQuery(sql, stmt);
					output.writeUTF("Data inserted into the database.");
				} else if (sql.startsWith("SELECT")) {
					System.out.println("Read data from the database.");
					String dataStr = readQuery(sql, stmt);
					output.writeUTF(dataStr);
				}
			
				// 	Response to clients			
				input.close();
				output.close();
			} catch (Exception e) {  
                System.out.println("Server runtime exception: " + e.getMessage());  
            }
		}
	}

    public static void main(String[] args) {  
		//	Connection to different DB nodes
		initDBConnection();
		//	Handle queries
		// 	Start socket server
		System.out.println("Trusted host is running ...\n");  
        TrustedHost server = new TrustedHost();  
        server.initSocket();
		//	Close the connectin with the DB node
		closeDBConnection();
    }   
}