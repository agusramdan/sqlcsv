package ramdan.file.sqlcsv;

import java.sql.*;

public class QueryProcessingImpl extends QueryProcessingDefault {
    private String jdbc;
    private String user;
    private String password;

    public void setJdbc(String jdbc) {
        this.jdbc = jdbc;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    protected Connection getConnection() throws SQLException {
        //Class.forName("oracle.jdbc.driver.OracleDriver");
        return DriverManager.getConnection(jdbc,user,password);
    }

    /**
     *
     * @param query
     * @param callback
     * @return
     * @throws SQLException
     */
    public long process(String query , ResultSetCallback callback) throws SQLException{
        long count;
        // connection auto close
        try(Connection con=getConnection()){
            count = process(con,query,callback);
        }
        return count;
    }
}
