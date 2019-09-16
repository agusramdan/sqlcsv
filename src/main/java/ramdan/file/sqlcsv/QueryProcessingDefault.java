package ramdan.file.sqlcsv;

import java.sql.*;

/**
 * "Engineering" to support default function interface java 8
 *
 */
public abstract class QueryProcessingDefault implements QueryProcessing {

    /*
     * @param callback
     * @return
     * @throws SQLException
     */
    public long process(Connection con ,String query, ResultSetCallback callback) throws SQLException{
        long count;
        try(
                Statement stmt=con.createStatement(ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_READ_ONLY);
                ResultSet rs = stmt.executeQuery(query)
        ){
            count = callback.call(rs);
        }
        return count;
    }
}
