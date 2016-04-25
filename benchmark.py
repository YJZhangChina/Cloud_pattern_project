import subprocess

"""
benchMark for STANDALONE DB

"""


threads = [1,2,4,8,16,32,64]

theFile = open("output.txt", "w")

def printToFile(toPrint, i):
	theFile.write(str(i))
	theFile.write("\n")
	theFile.write(toPrint)
	theFile.write("\n")	

def bashCommand(command_str, i):
    cmd = subprocess.Popen(command_str.split(' '), stdout=subprocess.PIPE)
    cmd_out, cmd_err = cmd.communicate()
    printToFile(cmd_out, i)
for j in range(5):
	theFile.write("=====================================   RUN " + str(j+1) + " ======================================")
	for i in threads:
		string = "sysbench --test=oltp --num-threads=" + str(i) + " --max-requests=10000 --db-driver=mysql --mysql-user=root --mysql-password=root --mysql-table-engine=ndbcluster --mysql-db=sakila run"
		bashCommand(string,i)

theFile.close()