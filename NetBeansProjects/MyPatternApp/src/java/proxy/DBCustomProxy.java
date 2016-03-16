package proxy;

import db.ConnectionParam;
import db.MasterDBConnection;
import db.SlaveDBConnection;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import monitoring.Watcher;

public class DBCustomProxy implements InvocationHandler {

    private final List<ConnectionParam> slaves = new ArrayList<>();
    private final ConnectionParam master;
    private static Object instance;
    private int counter = 0;
    private long lastCheck = 0;

    public Object newInstance(Class cl, Class[] interfaces) {
        if (instance == null) {
            instance = Proxy.newProxyInstance(cl.getClassLoader(),
                    interfaces,
                    new DBCustomProxy());
        }
        return instance;
    }

    public DBCustomProxy() {
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
                Watcher.getInstance(slaves).startMonitoring();
                
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
                ConnectionParam bestdb = Watcher.getInstance(slaves).getBestDb();
//                System.out.println(bestdb.getHost());
                Object res = SlaveDBConnection.getDbCon(bestdb).query((String) args[0]);
                return res == null ? MasterDBConnection.getDbCon(master).insert((String) args[0]) : res;
            } else {
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
                
                return MasterDBConnection.getDbCon(master).insert((String) args[0]);
            }
        }

        return null;
    }
}
