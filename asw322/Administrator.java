/**
 * Let the database administrator do the querying completely on their own. 
 */

import java.util.*;
import java.sql.*;

public class Administrator {
    public static void init(String[] userLogin, Scanner console, Statement b) {
        while(true) {
            try {
                System.out.println("\nHELLO THERE, WELCOME TO NICKLE SAVINGS BANK ADMINISTRATOR PAGE");
                ProjectInterface.printCurrentDate(ProjectInterface.getCurrentDate());
                System.out.println("----------Main Menu-----------");
                System.out.println("We let the database administrator control the querying manually: ");
                System.out.println("Please enter a query: ");
                String q = console.nextLine();

                if(q.equals("exit") || q.equals("EXIT")) {
                    System.exit(1);
                }
    
                ResultSet result = b.executeQuery(q);
    
                if(!result.next()) {
                    JDBC.printEmptyQuery();
                }
                else {
                    do {
                        //At this point, each result iteration can be seen as a string
                        List<String> lst = new ArrayList<String>();
                        lst = Query.parseQuery(result);
    
                        for(int i = 0; i < lst.size(); i++) {
                            System.out.printf("%30s", lst.get(i));
                        }
                        System.out.println();                   
                    } while(result.next());
                }
            } catch(Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }
}