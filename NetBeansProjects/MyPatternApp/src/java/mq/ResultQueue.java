
package mq;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 *
 * @author benjamin
 */
public class ResultQueue {
    private static final Map<UUID, ResultSet> queue = new HashMap<>();
    
    private ResultQueue(){}
    
    public static Map<UUID, ResultSet> getQueue() {
        return queue;
    }
}
