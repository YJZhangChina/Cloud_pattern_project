import mysql.connector

cnx = mysql.connector.connect(user='root', password='root',
                              host='172.31.30.76',
                              database='sakila')




cnx.close()