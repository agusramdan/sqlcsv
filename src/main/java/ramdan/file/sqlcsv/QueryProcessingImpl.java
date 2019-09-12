package ramdan.file.sqlcsv;

import java.sql.*;

public class QueryProcessingImpl implements QueryProcessing {
    private String jdbc;
    private String user;
    private String password;
    private boolean closeAfterQuery =true;

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
        Connection con=getConnection();
        try(
                Statement stmt=con.createStatement(ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_READ_ONLY);
                ResultSet rs = stmt.executeQuery(query)
        ){
            count = callback.call(rs);
        }finally {
            if(closeAfterQuery){
                con.close();
            }
        }
        return count;
    }
}
