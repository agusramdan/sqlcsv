package ramdan.file.sqlcsv;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {

    private static String DEFAULT_JDBC = "jdbc:oracle:thin:@localhost:1521:xe";
    private static String DEFAULT_USER  = "oracle";
    private static String DEFAULT_PASSWORD = "oracle";
    private static String DEFAULT_QUERY = "select * from dual";
    private static String DEFAULT_JDBC_DRIVER = "oracle.jdbc.driver.OracleDriver";
    private static String DEFAULT_DELIMITER = ",";

    private  Map <String,String> parameters = new HashMap<>();
    /**
     * Populate Argument
     * -j     : jdbc connection
     * -u     : username
     * -p     : password
     * -q     : query
     * -d     : delimiter
     * -quote : using quote
     * -o     : file output optional default std IO.
     *
     * @param args
     */
    public void args(String ... args){
        String param = null;
        int count = 0;
        for (String arg : args) {

            if(param!=null){
                parameters.put(param,arg);
                param = null;

            }else
            if(arg.startsWith("-")){
                param = arg;
                if("-quote".equals(param)){
                    parameters.put(param,"true");
                    param = null;
                }
                if("-nopassword".equals(param)){
                    parameters.put("-p","");
                    parameters.put(param,"true");
                    param = null;
                }
            }
        }
    }
    private PrintWriter getOutput() throws IOException {
        String fileOutput = parameters.get("-o");
        PrintWriter printWriter;
        if(fileOutput != null && !fileOutput.isEmpty() ){
            File output = new File(fileOutput);
            File parentOutput = output.getParentFile();
            if (parentOutput != null && ( !parentOutput.exists() || !parentOutput.isDirectory())) {
                System.err.println("File ["+fileOutput+"],"+output);
                System.err.println("parent directory not exists");
                System.exit(1);
            }
            printWriter = new PrintWriter(output);
        }else {
            printWriter = new PrintWriter(System.out);
        }
        return printWriter;
    }

    private QueryProcessing getQueryProcessing() {
        QueryProcessingImpl queryProcessing = new QueryProcessingImpl();
        String cls = parameters.containsKey("-driver")?parameters.get("-driver"):DEFAULT_JDBC_DRIVER;
        try {
            //Register JDBC driver
            Class.forName(cls);
        } catch (ClassNotFoundException e) {
            System.err.println("Register JDBC driver");
            e.printStackTrace();
        }
        if(parameters.containsKey("-j")){
           queryProcessing.setJdbc(parameters.get("-j"));
        }else{
           queryProcessing.setJdbc(DEFAULT_JDBC);
        }
        if(parameters.containsKey("-u")){
            queryProcessing.setUser(parameters.get("-u"));
        }else{
            queryProcessing.setUser(DEFAULT_USER);
        }
        if(parameters.containsKey("-p")){
            queryProcessing.setPassword(parameters.get("-p"));
        }else{
            queryProcessing.setPassword(DEFAULT_PASSWORD);
        }

        return queryProcessing;
    }
    private String getQuery(){
        if(parameters.containsKey("-qf")){
            try {
                return new String(Files.readAllBytes(
                        Paths.get(parameters.get("-qf")))
                        , StandardCharsets.UTF_8);
            } catch (Exception e) {
                System.err.printf("Error load query file ");//e.printStackTrace();
                System.exit(1);
            }
        }
        if(parameters.containsKey("-q")){
            return  parameters.get("-q");
        }else{
            return DEFAULT_QUERY;
        }
    }
    private String getDelimiter(){
        if(parameters.containsKey("-d")){
            return parameters.get("-d");
        }else{
            return DEFAULT_DELIMITER;
        }
    }
    private boolean isUsingQuote() {
        return Boolean.parseBoolean(parameters.get("-quote"));
    }

    private void println(PrintWriter printWriter, String delimiter,boolean usingQuote, List<String> row){
        if(row.isEmpty()) return;
        int size = row.size();
        for (int i = 0; i < size; i++) {
            if(i>0) printWriter.print(delimiter);
            String str = row.get(i);
            if(usingQuote ){
                if(str !=null){
                    str = str.replaceAll("\"","\"\"");
                    printWriter.print("\"");
                    printWriter.print(str);
                    printWriter.print("\"");
                }
            }else{
                printWriter.print(str);
            }
        }
        printWriter.println();
    }

    public void run() throws SQLException, IOException {
        QueryProcessing queryProcessing = getQueryProcessing();
        String query = getQuery();
        final String delimiter = getDelimiter();
        final boolean usingQuote =isUsingQuote();
        final PrintWriter printWriter = getOutput();
        // using ResultSetCallback
        queryProcessing.process(query, new ResultSetCallback() {
            public long call(ResultSet rs) throws SQLException {

                // get meta data for header and column count
                ResultSetMetaData rsmd = rs.getMetaData();
                int columnCount = rsmd.getColumnCount();

                List<String> row;

                // print header
                row = new ArrayList<>();
                for (int i = 1; i <= columnCount; i++ ) {
                    row.add(rsmd.getColumnName(i));
                }
                println(printWriter,delimiter,usingQuote,row);

                // print data
                long count=0;
                while (rs.next()){
                    count++;
                    row = new ArrayList<>();
                    for (int i = 1; i <= columnCount; i++ ) {
                        // convert to string
                        row.add(rs.getString(i));
                    }
                    println(printWriter,delimiter,usingQuote,row);
                }
                printWriter.flush();

                return count;
            }
        });
    }

    public static void main(String ... args) throws SQLException, IOException {
        Main main = new Main();
        main.args(args);
        main.run();
    }
}
