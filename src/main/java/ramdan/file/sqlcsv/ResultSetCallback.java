package ramdan.file.sqlcsv;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface ResultSetCallback {
    long call(ResultSet resultSet) throws SQLException;
}
