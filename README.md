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
- **MySQL_benchmark** This folder contains all the files related to the steps we took to benchmark the standalone MySQL db and our MySQL cluster. You will find:
	- An Excel table containing the results of the benchmarking.
	- A file describing the steps we took to setup the cluster.
	- A folder containing the scripts used to benchmark our databases and the raw outputs.
	
### How should I run these programs? ###
- **Competing Consumers pattern**
	1. Run the GlassFish server.
	2. Start MySQL in all data nodes.
	3. Compile and run ```SocketServer.java``` in the master node.
	4. Fill the **MASTER_IP** as the master node's public IP in the ```SocketConnection.java``` of the client side code.
	5. Change the **numberOfClients** variable to the subject client number in ```CompetingConsumersMQ.java```.
	6. Compile and run ```CompetingConsumersMQ.java```.
- **Gatekeeper pattern**
	1. Configure the security rule (in AWS security groups) to restrict that the sensitive data node only accept MySQL connection from the trusted host node, and the trusted host only accept TCP connection from the gatekeeper node.
	2. Run the GlassFish server.
	3. Start MySQL in the sensitive data node.
	4. Compile and run ```TrustedHost.java``` in the trusted host node.
	5. Compile and run ```Gatekeeper.java``` in the gatekeeper node.
	6. Fill the **GATEKEEPER_IP** as the gatekeeper node's public IP in the ```DataQuery.java``` of the client side code.
	7. Change the **numberOfClients** variable to the subject client number in ```GatekeeperMQ.java```.
	8. Compile and run ```GatekeeperMQ.java```.
- **Benchmark**
	1. SCP the script (benchmarkCluster.py or benchmark.py) to the correct instance (master/sql node in the case of the MySQL cluster).
	2. Modify the credential regarding the database in the script.
	3. Install sysbench on your instance with: sudo apt-get install sysbench
	4. If you are benchmarking a Cluster, you will need to delete /etc/my.cnf and recreate it.
	5. Run the script wiht '''python benchmark.py''' or '''python benchmarkCluster.py'''
	6. The output is being printed to a file, which you can SCP to your own machine.
	
### Contact ###

le.an@polymtl.ca  
alexandre.courouble@polymtl.ca  
