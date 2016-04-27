package proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import monitoring.WatchDog;
import db.ConnectionParam;
import db.MasterDBConnection;
import db.SlaveDBConnection;

public class DBProxy implements InvocationHandler {

    private List<ConnectionParam> slaves = new ArrayList<>();
    private final ConnectionParam master;

    @SuppressWarnings("rawtypes")
    public Object newInstance(Class cl, Class[] interfaces) {
//		watchDBs();
        return Proxy.newProxyInstance(cl.getClassLoader(),
                interfaces,
                new DBProxy());
    }

    private void watchDBs() {
        for (ConnectionParam cp : getSlaves()) {
            new Thread(new WatchDog(cp, this)).start();
        }
    }

    public DBProxy() {
        this.master = new ConnectionParam("host_ip", 3306, "sakila", "root",
                "pass", "com.mysql.jdbc.Driver", true);
        this.slaves.add(new ConnectionParam("host_ip", 3306, "sakila", "root",
                "pass", "com.mysql.jdbc.Driver", false));
        this.slaves.add(new ConnectionParam("host_ip", 3306, "sakila", "root",
                "pass", "com.mysql.jdbc.Driver", false));
        this.slaves.add(new ConnectionParam("host_ip", 3306, "sakila", "root",
                "pass", "com.mysql.jdbc.Driver", false));
    }

    @Override
    public Object invoke(Object proxy, Method m, Object[] args) throws Throwable {
        if (args != null) {
            if (((String) args[0]).toLowerCase().startsWith("select")) {
                try {
                    if(getSlaves().size() == 1){
                        return SlaveDBConnection.getDbCon(getSlaves().get(0)).query((String) args[0]);
                    }else{
                        return SlaveDBConnection.getDbCon(getSlaves()).query((String) args[0]);
                    }
//                  return ReplicationConnection.getInstance().select((String)args[0]);
                    
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } else {
//		ReplicationConnection.getInstance().insert((String)args[0]);
                return MasterDBConnection.getDbCon(master).insert((String) args[0]);
            }
        }

        return null;
    }

    /**
     * @return the slaves
     */
    public List<ConnectionParam> getSlaves() {
        return slaves;
    }
}
