package ramdan.file.sqlcsv;

import java.sql.SQLException;

public interface QueryProcessing {

    long process(String query , ResultSetCallback callback) throws SQLException;

}
