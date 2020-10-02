import java.sql.*;
import java.util.*;


public class Query {
    //Query processor
    public static ArrayList<String> parseQuery(ResultSet result) {
        ArrayList<String> lst = new ArrayList<String>();;
        try {
            ResultSetMetaData rsmd = result.getMetaData();
            for(int i = 1; i <= rsmd.getColumnCount(); i++) {
                lst.add(result.getString(i));
            }
        } catch(Exception e) {
            System.out.println(e.getMessage());
        }
        return lst;
    }
}    