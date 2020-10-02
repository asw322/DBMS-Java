import java.io.*;
import java.sql.*;
import java.util.*;

public class JDBC {
    //Data fields stored in String arrays
    private static String[] oracleLogin;
    private static String[] userLogin;
    private static String[] tellerLogin;

    private static int userType = 0;

    private static int getUserType(Scanner console) {
        while (true) {
            try {
                System.out.println("\nInput your role:\n1.Customer\n2.Bank Teller\n3.Bank Administrator\n");
                userType = console.nextInt();
                
                if (userType == 1 || userType == 2 || userType == 3) {
                    return userType;
                }
                else {
                    System.out.println("Input value incorrect");
                }
            }
            catch(InputMismatchException e) {
                System.err.println("Wrong input type!");
                console.nextLine();
            }    
        }
    }

    public static void printEmptyQuery() {
        System.out.println("Query is empty");
    }

    protected static void getLogin(String[] userLogin, Scanner console, Statement b, int type) {
        String q = "";
        int INVALID_LOGIN_COUNTER = 0;
        boolean a = false;
        while(true) {
            if(INVALID_LOGIN_COUNTER == 3){
                System.out.println("\nLast login chance");
            }
            if(INVALID_LOGIN_COUNTER == 4) {
                System.out.println("You have made too many invalid attempts..Locking account..");
                System.exit(0);
            }

            try {
                if(type == 1) {
                    System.out.println("enter CUSTOMER ID: ");
                    userLogin[0] = console.nextLine();
                    System.out.println("enter CUSTOMER NAME: ");
                    userLogin[1] = console.nextLine();
        
                    //Check if user exists
                    q = "select CUSTOMER_NAME from CUSTOMER where CUSTOMER_ID = '" + userLogin[0] + "'";
                    ResultSet result = b.executeQuery(q);

                    //The user does not exist
                    if(!result.next()) {
                        System.out.println("Invalid login credentials..");
                        INVALID_LOGIN_COUNTER++;
                    }
                    else {
                        break;
                    }
                }
                else if(type == 2) {
                    System.out.println("enter TELLER ID: ");
                    tellerLogin[0] = console.nextLine();
                    System.out.println("enter TELLER NAME: ");
                    tellerLogin[1] = console.nextLine();
        
                    //Check if user exists
                    q = "select TELLER_NAME from TELLER where TELLER_ID = '" + userLogin[0] + "'";
                    ResultSet result = b.executeQuery(q);

                    //The user does not exist
                    if(!result.next()) {
                        System.out.println("Invalid login credentials..");
                        INVALID_LOGIN_COUNTER++;
                    }
                    else {
                        break;
                    }
                }
                else if(type == 3) {
                    a = true;
                    System.out.println("enter ADMINISTRATOR ID: ");
                    userLogin[0] = console.nextLine();
                    System.out.println("enter ADMINISTRATOR KEYWORD: ");
                    userLogin[1] = console.nextLine();
        
                    if(userLogin[0].trim().toUpperCase().equals("ZOMBIE") && userLogin[1].trim().toUpperCase().equals("ATTACK")) {
                        break;          
                    } else {
                        INVALID_LOGIN_COUNTER++;
                    }
                } 
            } catch(Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public static void main(final String[] args) throws SQLException, IOException, java.lang.ClassNotFoundException {
        //Connection, Statement, and Scanner objcets
        Connection a = null;
        Statement b = null;
        final Scanner console = new Scanner(System.in);
        oracleLogin = new String[2];        

        boolean login = false;

        //continuously get userID and password until the correct input is entered (robust)
        do {

            System.out.print("\nenter Oracle user id: ");
            oracleLogin[0] = console.nextLine();
            System.out.print("enter Oracle password for database: ");
            oracleLogin[1] = console.nextLine();

            //attempting to take userId and password to create a connection 
            try {
                a = DriverManager.getConnection("jdbc:oracle:thin:@edgar1.cse.lehigh.edu:1521:cse241", oracleLogin[0], oracleLogin[1]);
                b = a.createStatement();

                login = true;

                userLogin = new String[2];

                //Generate headers through ProjectInterface
                ProjectInterface.generateHeader();

                //Get which type of user (customer, bank teller, bank administrator)
                getUserType(console);
                console.nextLine();

                //Execute separate files depending on user type
                if(userType == 1) {
                    //Call Customer Class
                    getLogin(userLogin, console, b, 1);
                    Customer.init(userLogin, console, b);

                } else if(userType == 2) {
                    tellerLogin = new String[2];
                    //Call Bank Teller Class
                    getLogin(tellerLogin, console, b, 2);
                    Teller.init(tellerLogin, userLogin, console, b);
                } else if(userType == 3) {
                    //Call Bank Administrator Class
                    getLogin(userLogin, console, b, 3);
                    Administrator.init(userLogin, console, b);
                } else {
                    System.out.println("Your user type is invalid..");
                }
            } 
            //Catch log in errors when user input is invalid 
            catch(final SQLException e) {
                System.out.println("Login failed.. Try again");
                login = false;
            } 
            //Catch all other errors 
            catch(final Exception e) {
                System.out.println("General error..");
                System.out.println(e.getMessage());
            } 
            //Close all connections and statements
            finally {
                try { if(a != null) a.close(); } catch(Exception e) {};
                try { if(b != null) b.close(); } catch(Exception e) {};
            }
        } while(login == false); 
    }
}