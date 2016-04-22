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

def readQuery(query, nb):
	cursor = cnx.cursor()
	timeA = time.time()
	print "executing: ", query, " for ", nb, "times"
	for i in range(nb):
		cursor.execute(query)
		row = cursor.fetchall()	
	cursor.close()
	return time.time() - timeA 


def writeQuery(nb):
	cursor = cnx.cursor()
	timeA = time.time()
	add_row = ("INSERT INTO test "
               "(id, name)"
               "VALUES (%s, %s)")
	for i in range(nb):
		data_row = (i,"lol")
		cursor.execute(add_row,data_row)
	cursor.close()
	return time.time() - timeA 


#latency = readQuery("SELECT * FROM film;", 1000)
#print "Latency: 		", latency

latencyWrite = writeQuery(100)

print latencyWrite


cnx.close()