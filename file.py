import mysql.connector

cnx = mysql.connector.connect(user='root', password='root',
                              host='localhost',
                              database='sakila')


cursor = cnx.cursor()

cursor.execute("SELECT * FROM film;")

row = cursor.fetchone()
while row is not None:
  print(row)
  row = cursor.fetchone()

cursor.close()
cnx.close()