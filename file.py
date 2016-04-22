"""
Author: Alex Courouble
"""
import mysql.connector
import time
"""
Database info:
"""

USER = 'root'
PASSWORD = 'root'
HOST = 'localhost'
DB = 'sakila'

cnx = mysql.connector.connect(user=USER, password=PASSWORD,
                              host=HOST,
                              database=DB)

def writeQuery(nb):
	cursor = cnx.cursor()
	timeA = time.time()
	for i in range(nb):
		add_row = ("INSERT INTO test "
               "(id, name)"
               "VALUES (%s, %s)")
		data_row = (i,"lol")
		cursor.execute(add_row,data_row)
	cnx.commit()
	cursor.close()
	return time.time() - timeA 

def readQuery(query, nb):
	cursor = cnx.cursor()
	timeA = time.time()
	print "executing: ", query, " for ", nb, "times"
	for i in range(nb):
		cursor.execute(query)
		row = cursor.fetchall()	
	cursor.close()
	return time.time() - timeA 

def deleteQuery(query):
	cursor = cnx.cursor()
	timeA = time.time()
	print "executing: ", query
	for i in range(nb):
		cursor.execute(query)
	cnx.commit()
	cursor.close()
	return time.time() - timeA 

latencyWrite = writeQuery(100)
latencyRead = readQuery("SELECT * FROM test;", 1000)
latencyDelete = deleteQuery("DELETE * FROM test;")
print "Latency write:		", latencyWrite
print "Latency read: 		", latencyRead

cnx.close()