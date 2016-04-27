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

### How should I run these programs? ###
- **???**
	- ???
	
### Contact ###

le.an@polymtl.ca  
alexandre.courouble@polymtl.ca  
