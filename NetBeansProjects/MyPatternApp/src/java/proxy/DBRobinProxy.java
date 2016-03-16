package proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import db.ConnectionParam;
import db.MasterDBConnection;
import db.SlaveDBConnection;

public class DBRobinProxy implements InvocationHandler {

    private List<ConnectionParam> slaves = new ArrayList<>();
    private final ConnectionParam master;
    private static Object instance;
    private int lastDbIndex = 0;
    private int counter = 0;
    private long lastCheck = 0;

    public Object newInstance(Class cl, Class[] interfaces) {
        if (instance == null) {
            instance = Proxy.newProxyInstance(cl.getClassLoader(),
                    interfaces,
                    new DBRobinProxy());
        }
        return instance;
    }

    public DBRobinProxy() {
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
                    if (slaves.size() == 1) {
                        return SlaveDBConnection.getDbCon(slaves.get(0)).query((String) args[0]);
                    } else {
                        lastDbIndex++;
                        if (lastDbIndex >= slaves.size()) {
                            lastDbIndex = 0;
                        }
                        
                        counter++;
                        if(lastCheck == 0){
                            lastCheck = System.nanoTime();
                            counter = 0;
                        }

                        if(System.nanoTime()-lastCheck > 1000000000){
                            System.out.println(counter);
                            counter = 0;
                            lastCheck = System.nanoTime();
                        }
                        
                        return SlaveDBConnection.getDbCon(slaves.get(lastDbIndex)).query((String) args[0]);

                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } else {
                return MasterDBConnection.getDbCon(master).insert((String) args[0]);
            }
        }

        return null;
    }
}
