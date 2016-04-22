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

def query(query, nb):
	cursor = cnx.cursor()
	timeA = time.time()
	print "executing: ", query, " for ", nb, "times"
	for i in range(nb):
		cursor.execute(query)
		row = cursor.fetchall()	
	cursor.close()
	return time.time() - timeA 



latency = query("SELECT * FROM film;", 1000)
print "Latency: 		", latency


cnx.close()