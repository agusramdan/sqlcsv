package ramdan.file.sqlcsv;

import java.sql.Connection;
import java.sql.SQLException;

public interface QueryProcessing {
    long process(Connection con , String query, ResultSetCallback callback) throws SQLException;
    long process(String query , ResultSetCallback callback) throws SQLException;
}
