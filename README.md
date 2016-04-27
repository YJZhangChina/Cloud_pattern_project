# Scaling Databases and Implementing Cloud Patterns #

### What is this repository for? ###

- Comparing standalone MySQL database against MySQL cluster database. 
- Compare the performance and energy consumption of the Competing Consumers pattern against the Gatekeeper pattern.

###File description###
- **client_side** folder contains Java source code to implement the client side of the Competing Consumers pattern and the Gatekeeper pattern. For each pattern, the code implements the clients' multi-threading scenario and a socket connection client. 
- **server_side** folder contains Java source code to implement the server side of the two subject cloud design patterns. 
	- For the Competing Consumers pattern, we implement a class ```SocketServer.java``` to deploy in the master data node. The class acts as a socket connection server, and can also distribute workload into different data nodes.
	- For the Gatekeeper pattern, we implement two classes ```Gatekeeper.java``` and ```TrustedHost.java``` to respectively deploy in the gatekeeper node and in the trusted host node.
		- **Gatekeeper.java**: connects clients and the trusted host. It can send queries from clients to the trusted host, and send back the response from the trusted host to clients.
		- **TrustedHost.java**: connects the gatekeeper and the sensitive data. I can send queries from the gatekeeper to the sensitive database, and send back the database's response to the gatekeeper.
- **pattern_measurement** folder contains output of PowerAPI for each number of clients and a script to parse the results.

### How should I run these programs? ###
- **Competing Consumers pattern**
	1. Run the GlassFish server.
	2. Start MySQL for all data nodes.
	3. Compile and run ```SocketServer.java``` in the master data node.
	4. Fill the **MASTER_IP** as the master data node's public IP in the ```SocketConnection.java``` of the client side code.
	5. Change the **numberOfClients** variable to the subject client number in ```CompetingConsumersMQ.java```.
	6. Compile and run ```CompetingConsumersMQ.java```.
	
### Contact ###

le.an@polymtl.ca  
alexandre.courouble@polymtl.ca  
