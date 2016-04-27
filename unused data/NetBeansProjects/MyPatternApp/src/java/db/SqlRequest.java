package db;


import java.io.Serializable;
import java.sql.ResultSet;
import java.util.UUID;

/**
 *
 * @author benjamin
 */
public class SqlRequest implements Serializable{
    private UUID uid;
    private String query;
    private ResultSet result;
    private int priority;
    private boolean isFilmRequest;
    private boolean isInsertRequest;

    public SqlRequest(UUID uid, ResultSet result) {
        this.uid = uid;
        this.result = result;
    }

    public SqlRequest(UUID uid, String query, int priority) {
        this.uid = uid;
        this.query = query;
        this.priority = priority;
    }

    public SqlRequest(UUID uid, String query, int priority, boolean isFilmQuery) {
        this.uid = uid;
        this.query = query;
        this.priority = priority;
        this.isFilmRequest = isFilmQuery;
    }

    public SqlRequest(UUID uid, String query, int priority, boolean isFilmQuery, boolean isInsertQuery) {
        this.uid = uid;
        this.query = query;
        this.priority = priority;
        this.isFilmRequest = isFilmQuery;
        this.isInsertRequest = isInsertQuery;
    }

    /**
     * @return the uid
     */
    public UUID getUid() {
        return uid;
    }

    /**
     * @return the query
     */
    public ResultSet getResult() {
        return result;
    }

    /**
     * @return the query
     */
    public String getQuery() {
        return query;
    }

    /**
     * @return the priority
     */
    public int getPriority() {
        return priority;
    }

    /**
     * @return the isFilmRequest
     */
    public boolean isIsFilmRequest() {
        return isFilmRequest;
    }

    /**
     * @return the isInsertRequest
     */
    public boolean isIsInsertRequest() {
        return isInsertRequest;
    }
    
}
