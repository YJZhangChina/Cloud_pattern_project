import mysql.connector

cnx = mysql.connector.connect(user='root', password='root',
                              host='localhost',
                              database='sakila')


cursor = cnx.cursor()

query = "SELECT COUNT(*) FROM film;"


cursor.execute(query)

print cursor

cursor.close()
cnx.close()