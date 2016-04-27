package db;

public class ConnectionParam {

    private String host;
    private int port;
    private String dbName;
    private String user;
    private String password;
    private boolean isMaster;
    private boolean isSafe;

    public ConnectionParam(String host, int port, String dbName, String user,
            String password, String driver, boolean isMaster) {
        super();
        this.host = host;
        this.port = port;
        this.dbName = dbName;
        this.user = user;
        this.password = password;
        this.isMaster = isMaster;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String url) {
        this.host = url;
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean getIsMaster() {
        return isMaster;
    }

    public void setIsMaster(boolean isMaster) {
        this.isMaster = isMaster;
    }

    public boolean isSafe() {
        return isSafe;
    }

    public void setSafe(boolean isSafe) {
        this.isSafe = isSafe;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
