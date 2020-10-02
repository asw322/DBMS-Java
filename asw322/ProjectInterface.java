import java.util.*;
import java.sql.*;

public class ProjectInterface {
    //Get the current date in a string array
    public static String[] getCurrentDate() {
        String[] CURRENT;
        String currDate;

        long millis=System.currentTimeMillis();  
        currDate = new java.sql.Date(millis).toString();
        CURRENT = currDate.split("-");

        return CURRENT;
    }
    
    //Get the current date in a string
    public static String getCurrentDateString() {
        long millis=System.currentTimeMillis();
        return new java.sql.Date(millis).toString();
    }

    //Used an online decimal rounding implementation: https://stackoverflow.com/questions/2808535/round-a-double-to-2-decimal-places
    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();
    
        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

    //Gets a new loan ID and ensures that it is valid
    public static String getLoanID(Statement b) {
        String q = "";
        String loan_id = "";
        try {
            while(true) {
                loan_id = RandomGenerator.generateRandomAccountID(2) + "-" + RandomGenerator.generateRandomAccountID(3) + "-" + RandomGenerator.generateRandomAccountID(4);
                q = "select * from LOAN where LOAN_ID = '" + loan_id + "'";
                ResultSet result = b.executeQuery(q);

                if(!result.next()) {
                    result.close();
                    return loan_id;
                }
            }
        } catch(Exception e) {
            System.out.println(e.getMessage());
        }
        return loan_id;
    }

    //Gets a new transaction ID and ensures that it is valid
    public static String getTransactionID(Statement b) {
        //need to make sure transaction_id is unique
        String q = "";
        String transaction_id = "";
        try {
            while(true) {
                transaction_id = RandomGenerator.generateRandomAccountID(5) + "-" + RandomGenerator.generateRandomAccountID(3);
                q = "select * from TRANSACTION where TRANSACTION_ID = '" + transaction_id + "'";
                ResultSet result = b.executeQuery(q);
    
                if(!result.next()) {
                    result.close();
                    return transaction_id;
                }
            }
        } catch(Exception e) {
            System.out.println(e.getMessage());
        }
        return transaction_id;
    }   

    //Get a new loan payment ID and ensures that it is valid
    public static String getLoanPaymentID(Statement b) {
        String q = "";
        String loan_payment_id = "";
        try {
            while(true) {
                loan_payment_id = RandomGenerator.generateRandomString(8) + "-" + RandomGenerator.generateRandomString(4) + "-" + RandomGenerator.generateRandomString(4) + "-" + RandomGenerator.generateRandomString(4) + "-" + RandomGenerator.generateRandomString(12);
                q = "select * from LOAN_PAYMENt where LOAN_PAYMENT_ID = '" + loan_payment_id+ "'";
                ResultSet result = b.executeQuery(q);

                if(!result.next()) {
                    result.close();
                    return loan_payment_id;
                }
            }
        } catch(Exception e) {
            System.out.println(e.getMessage());
        }
        return loan_payment_id;
    }

    //Get a new purchase ID and ensures that it is valid 
    public static String getPurchaseID(Statement b) {
        String q = "";
        String purchase_id = "";
        try {
            while(true) {
                purchase_id = RandomGenerator.generateRandomString(8) + "-" + RandomGenerator.generateRandomString(4) + "-" + RandomGenerator.generateRandomString(4) + "-" + RandomGenerator.generateRandomString(4) + "-" + RandomGenerator.generateRandomString(12);
                q = "select * from PURCHASES where PURCHASES_ID = '" + purchase_id + "'";
                ResultSet result = b.executeQuery(q);

                if(!result.next()) {
                    result.close();
                    return purchase_id;
                }
            }
        } catch(Exception e) {
            System.out.println(e.getMessage());
        }
        return purchase_id;
    }

    //Print the current date
    public static void printCurrentDate(String[] CURRENT) {
        System.out.println("Today is: " + CURRENT[1] + "/" + CURRENT[2] + "/" + CURRENT[0] + "\n");
    }

    //Print the header 
    public static void generateHeader() {
        System.out.println("\t\t\tWELCOME TO\t\t\t");
        System.out.println("---------------------------------------------------------------");
        System.out.println("##    # ###  ###  #  ## #    ####    ####    ##   ##    # #  ##");
        System.out.println("# #   #  #  #   # # #   #    #       #   #  #  #  # #   # # #  ");
        System.out.println("#  #  #  #  #     ##    #    ####    ####   ####  #  #  # ##   ");
        System.out.println("#   # #  #  #   # # #   #    #       #   # #    # #   # # # #  ");
        System.out.println("#    ## ###  ###  #  ## #### ####    ####  #    # #    ## #  ##");
        System.out.println("---------------------------------------------------------------");
    }
}