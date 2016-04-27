
package mq;

import java.util.Comparator;
import db.SqlRequest;

/**
 *
 * @author benjamin
 */
public class SqlRequestComparator implements Comparator<SqlRequest>{

    @Override
    public int compare(SqlRequest x, SqlRequest y) {
        if (x.getPriority() < y.getPriority())
        {
            return -1;
        }
        if (x.getPriority() > y.getPriority())
        {
            return 1;
        }
        return 0;
    }
    
}
