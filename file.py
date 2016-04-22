import mysql.connector
import time

cnx = mysql.connector.connect(user='root', password='root',
                              host='localhost',
                              database='sakila')


cursor = cnx.cursor()


timeA = time.time()

for i in range(1000):
	cursor.execute("SELECT * FROM film;")
	print i

latency = time.time() - timeA 

print "Latency: 		", latency




cursor.close()
cnx.close()