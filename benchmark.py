import subprocess

def bashCommand(command_str):
    cmd = subprocess.Popen(command_str.split(' '), stdout=subprocess.PIPE)
    cmd_out, cmd_err = cmd.communicate()
    return cmd_out

print bashCommand('sysbench  --test=oltp --num-threads=32 --max-requests=10000 --db-driver=mysql --mysql-user=root --mysql-password=root --mysql-table-engine=ndbcluster  --mysql-db=sakila run')