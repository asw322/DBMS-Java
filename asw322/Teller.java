import java.util.*;
import java.sql.*;

/**
 * personal information
 * balance enquiry
 * transfer money
 * deposit money
 * withdraw money
 * bill pay???? 
 */

 /**
 * 2. Account deposit/withdrawal: These happen at a branch(teller or ATM)
 *      savings account cannot go negative
 *      If checking account withdrawal takes account balance < minimum balance -> penalty
  */

public class Teller {
    private static String q;
    private static boolean transfer(String[] userLogin, Scanner console, Statement b) {
        ResultSet result = null;
        ResultSet result2 = null;
        try {
            console.nextLine();
            Customer.viewAccountInformation(userLogin, console, b, 0);
            System.out.println("Enter the ACCOUNT ID you want to transfer funds from: ");
            String account_id_from = console.nextLine();

            //Makes sure account is a checking account and is owned by the user
            q = "select * from ACCOUNT natural join CHECKING where ACCOUNT_ID = '" + account_id_from + "' and CUSTOMER_ID = '" + userLogin[0] + "'";
            result = b.executeQuery(q);

            if(result.next()) {
                double currAmountFrom = result.getDouble("AMOUNT");
                double minimum_balance = result.getDouble("MIN_BAL");
                
                System.out.println("Enter the ACCOUNT ID you want to transfer funds to: ");
                String account_id_to = console.nextLine();
                if(!account_id_from.equals(account_id_to)) {
                    q = "select * from customer natural join (select * from account where account_id = '" + account_id_to +"')";
                    result2 = b.executeQuery(q);
                    
                    if(result2.next()) {
                        String customerIDTo = result2.getString("CUSTOMER_ID");
                        double currAmountTo = result2.getDouble("AMOUNT");
                        System.out.println("How much would you like to transfer to " + result2.getString("CUSTOMER_NAME") + ":");
                        double amount = console.nextDouble();
                        double newAmount = (currAmountFrom - amount);
                        
                        if(newAmount >= minimum_balance) {
                            //update sender account 
                            q = "update ACCOUNT set AMOUNT = " + newAmount  + " where ACCOUNT_ID = '" + account_id_from + "'";
                            b.executeQuery(q);
                            //update receiver account
                            newAmount = (currAmountTo + amount);
                            q = "update ACCOUNT set AMOUNT = " + newAmount  + " where ACCOUNT_ID = '" + account_id_to + "'";
                            b.executeQuery(q);
                            //insert transaction for both account 
                            String transaction_id = ProjectInterface.getTransactionID(b);
                            String transaction_date = ProjectInterface.getCurrentDateString();
                            q = "insert into TRANSACTION (TRANSACTION_ID, TRANSACTION_DATE, TRANSACTION_AMOUNT, CUSTOMER_ID, ACCOUNT_ID) values ('" + transaction_id + "', '" + transaction_date + "', '$(" + amount + ")', " + userLogin[0] + ", '" + account_id_from + "')";
                            b.executeQuery(q);
                            transaction_id = ProjectInterface.getTransactionID(b);
                            transaction_date = ProjectInterface.getCurrentDateString();
                            q = "insert into TRANSACTION (TRANSACTION_ID, TRANSACTION_DATE, TRANSACTION_AMOUNT, CUSTOMER_ID, ACCOUNT_ID) values ('" + transaction_id + "', '" + transaction_date + "', '$" + amount + "', " + customerIDTo + ", '" + account_id_to + "')";
                            b.executeQuery(q);
                            return true;
                        } else {
                            System.out.println("\nAmount will bring account balance below minimum balance..");
                            return false;
                        }
                    } else {
                        System.out.println("\nInvalid receiver ACCOUNT ID..");
                        return false;
                    }
                } else {
                    System.out.println("\nSender and receiver have the same ACCOUNT ID..");
                    return false;
                }
            } else {
                System.out.println("\nYou either do not have access to this account or\nthis account is not a checking account..");
                return false;
            }
        } catch(Exception e) {
            System.out.println(e.getMessage());
        } finally {
            try { if(result != null) result.close(); } catch(Exception e) {};
            try { if(result2 != null) result2.close(); } catch(Exception e) {};
        }
        return false;
    }
    private static boolean depositMoney(String[] userLogin, Scanner console, Statement b) {
        ResultSet result = null;
        ResultSet result2 = null;
        ResultSet result3 = null;

        try {
            console.nextLine(); 
            Customer.viewAccountInformation(userLogin, console, b, 0);
            System.out.println("Enter the ACCOUNT ID you want to make a deposit to: ");
            String account_id = console.nextLine();
            q = "select * from ACCOUNT where ACCOUNT_ID = '" + account_id + "' and CUSTOMER_ID = '" + userLogin[0] + "'";
            result = b.executeQuery(q);

            if(!result.next()) {
                System.out.println("Invalid account ID..");
            } else {
                if(result.getString("CUSTOMER_ID").equals(userLogin[0])) {
                    System.out.println("How much would you like to deposit: ");
                    double amount = console.nextDouble();
                    
                    if(amount <= 0) {
                        System.out.println("Invalid amount..");
                    } else {
                        System.out.println("Are you sure you would like to deposit $" + amount + " Y / N");
                        console.nextLine();
                        String action = console.nextLine();

                        if(action.equals("Y") || action.equals("y")) {
                            //Adds the current account amount to new amount and rounds the double to 2 decimals
                            double currAmount = ProjectInterface.round(Double.parseDouble(result.getString("AMOUNT")), 2);
                            double newAmount = ProjectInterface.round((currAmount + amount), 2);

                            q = "update ACCOUNT set AMOUNT = " + newAmount + " where ACCOUNT_ID = '" + account_id + "'";
                            result2 = b.executeQuery(q);

                            //need to make sure transaction_id is unique
                            String transaction_id = ProjectInterface.getTransactionID(b);
                            String transaction_date = ProjectInterface.getCurrentDateString();
                            String transaction_amount = "" + amount;
                            String customer_id = userLogin[0];

                            q = "insert into TRANSACTION (TRANSACTION_ID, TRANSACTION_DATE, TRANSACTION_AMOUNT, CUSTOMER_ID, ACCOUNT_ID) values ('" + transaction_id + "', '" + transaction_date + "', '$" + transaction_amount + "', " + customer_id + ", '" + account_id + "')";
                            result3 = b.executeQuery(q);

                            // boolean update = true;
                            System.out.println("------------------------------");
                            System.out.printf("%25s %15s\n", "ACCOUNT ID:", account_id);
                            System.out.printf("%25s %15s\n", "TRANSACTION ID:", transaction_id);
                            System.out.printf("%25s %15s\n", "DATE:", transaction_date);
                            System.out.printf("%25s %15s\n", "AMOUNT:", transaction_amount);
                            System.out.printf("%25s %15s\n", "NEW ACCOUNT TOTAL:", newAmount);
                            
                            return true;
                        } 
                    }
                } else{
                    System.out.println("No access into this account");
                    return false;
                } 
            }
        } catch(Exception e) {
            System.out.println(e.getMessage());
        } finally {
            try { if(result != null) result.close(); } catch(Exception e) {};
            try { if(result2 != null) result2.close(); } catch(Exception e) {};
            try { if(result3 != null) result3.close(); } catch(Exception e) {};
        }
        return false;
    }

    //Withdraw function 
    private static boolean withdrawMoney(String[] userLogin, Scanner console, Statement b) {
        ResultSet result = null;
        ResultSet result2 = null;
        try {
            //Get account information - ensure robustness
            console.nextLine(); 
            Customer.viewAccountInformation(userLogin, console, b, 0);
            System.out.println("Enter the ACCOUNT ID you want to make a withdrawal from: ");
            String account_id = console.nextLine();
            q = "select * from ACCOUNT where ACCOUNT_ID = '" + account_id + "'";
            result = b.executeQuery(q);

            if(!result.next()) {
                System.out.println("\nInvalid account ID..");
            } else {
                double minimum_balance = Double.parseDouble(result.getString("MIN_BAL"));
                double penalty_amount = Double.parseDouble(result.getString("PENALTY_AMOUNT"));

                if(result.getString("CUSTOMER_ID").equals(userLogin[0])) {
                    //Get withdraw amount - ensure robustness
                    System.out.println("How much would you like to withdraw: ");
                    double amount = console.nextDouble();

                    if(amount <= 0) {
                        System.out.println("Invalid amount..");
                    } else {
                        System.out.println("Are you sure you would like to withdraw $" + amount + " Y / N");
                        console.nextLine();
                        String action = console.nextLine();

                        if(action.equals("Y") || action.equals("y")) {
                            //calculate new account totals after withdraw
                            double currAmount = ProjectInterface.round(Double.parseDouble(result.getString("AMOUNT")), 2);
                            double newAmount = ProjectInterface.round((currAmount - amount), 2);
                           
                            //check which type of account user has
                            q = "select * from CHECKING where ACCOUNT_ID = " + account_id;
                            result2 = b.executeQuery(q);

                            //The account is a checking account
                            if(result2.next()) {

                                 String transaction_id = ProjectInterface.getTransactionID(b);
                                 String transaction_date = ProjectInterface.getCurrentDateString();
                                 String transaction_amount = "" + penalty_amount;
                                 String customer_id = userLogin[0];

                                //Account balance cannot go below min_ bal unless -> penalty
                                if(newAmount < minimum_balance) {
                                    transaction_amount = "(" + (penalty_amount + amount) + ")"; 
                                    //Incurr a penalty
                                    System.out.println("Balance less than minimum balance..Penalty incurred");
                                    q = "insert into TRANSACTION (TRANSACTION_ID, TRANSACTION_DATE, TRANSACTION_AMOUNT, CUSTOMER_ID, ACCOUNT_ID) values ('" + transaction_id + "', '" + transaction_date + "', '$" + transaction_amount + "', " + customer_id + ", '" + account_id + "')";
                                    b.executeQuery(q);
                                    q = "update ACCOUNT set AMOUNT = " + (newAmount - penalty_amount) + " where ACCOUNT_ID = '" + account_id + "'";
                                    b.executeQuery(q);

                                    System.out.println("------------------------------");
                                    System.out.printf("%25s %15s\n", "ACCOUNT ID:", account_id);
                                    System.out.printf("%25s %15s\n", "TRANSACTION ID:", transaction_id);
                                    System.out.printf("%25s %15s\n", "DATE:", transaction_date);
                                    System.out.printf("%25s %15s\n", "AMOUNT:", transaction_amount);
                                    System.out.printf("%25s %15s\n", "PENALTY:", penalty_amount);
                                    System.out.printf("%25s %15s\n", "NEW ACCOUNT TOTAL:", (newAmount - penalty_amount));

                                    return true;
                                } 
                                //No penalty
                                else {
                                    transaction_amount = "(" + (amount) + ")";
                                    q = "insert into TRANSACTION (TRANSACTION_ID, TRANSACTION_DATE, TRANSACTION_AMOUNT, CUSTOMER_ID, ACCOUNT_ID) values ('" + transaction_id + "', '" + transaction_date + "', '$" + transaction_amount + "', " + customer_id + ", '" + account_id + "')";
                                    b.executeQuery(q);
                                    q = "update ACCOUNT set AMOUNT = " + (newAmount) + " where ACCOUNT_ID = '" + account_id + "'";
                                    b.executeQuery(q);

                                    System.out.println("------------------------------");
                                    System.out.printf("%25s %15s\n", "ACCOUNT ID:", account_id);
                                    System.out.printf("%25s %15s\n", "TRANSACTION ID:", transaction_id);
                                    System.out.printf("%25s %15s\n", "DATE:", transaction_date);
                                    System.out.printf("%25s %15s\n", "AMOUNT:", transaction_amount);
                                    System.out.printf("%25s %15s\n", "NEW ACCOUNT TOTAL:", newAmount);

                                    return true;
                                }
                            } 

                            //The account is a saving account
                            else {

                                String transaction_id = ProjectInterface.getTransactionID(b);
                                String transaction_date = ProjectInterface.getCurrentDateString();
                                String transaction_amount = "" + penalty_amount;
                                String customer_id = userLogin[0];

                               //Account balance cannot go below min_ bal unless -> penalty
                               if(newAmount < 0) {
                                   System.out.println("Invalid withdraw account balance will become negative..");
                                   return false;
                               } else {
                                   transaction_amount = "(" + (amount) + ")";
                                   q = "insert into TRANSACTION (TRANSACTION_ID, TRANSACTION_DATE, TRANSACTION_AMOUNT, CUSTOMER_ID, ACCOUNT_ID) values ('" + transaction_id + "', '" + transaction_date + "', '$" + transaction_amount + "', " + customer_id + ", '" + account_id + "')";
                                   b.executeQuery(q);
                                   q = "update ACCOUNT set AMOUNT = " + (newAmount) + " where ACCOUNT_ID = '" + account_id + "'";
                                   b.executeQuery(q);

                                   System.out.println("------------------------------");
                                   System.out.printf("%25s %15s\n", "ACCOUNT ID:", account_id);
                                   System.out.printf("%25s %15s\n", "TRANSACTION ID:", transaction_id);
                                   System.out.printf("%25s %15s\n", "DATE:", transaction_date);
                                   System.out.printf("%25s %15s\n", "AMOUNT:", transaction_amount);
                                   System.out.printf("%25s %15s\n", "NEW ACCOUNT TOTAL:", newAmount);
                                   
                                   return true;
                               }
                            }
                        }
                    }
                } else {
                    System.out.println("No access into this account");
                }
            } 
        } catch(Exception e) {
            System.out.println(e.getMessage());
        } finally {
            try { if(result != null) result.close(); } catch(Exception e) {};
            try { if(result2 != null) result2.close(); } catch(Exception e) {};
        }
        return false;
    }

    public static void init(String[] tellerLogin, String[] userLogin, Scanner console, Statement b) {
        System.out.println("\nHELLO THERE, WELCOME TO NICKLE SAVINGS BANK TELLER PAGE");
        ProjectInterface.printCurrentDate(ProjectInterface.getCurrentDate());

        //Help a customer
        System.out.println("Enter anything to help a customer: ");
        String input = console.nextLine();
        if(input.length() >= 0) {
            JDBC.getLogin(userLogin, console, b, 1);
        }

        while(true) {
            try {                
                boolean condition = false;
                System.out.println("------------------------------");
                System.out.println("Select an option: \n");
                System.out.println("1. View personal information\n2. Balance Enquiry\n3. Transfer\n4. Deposit Money\n5. Withdraw Money\n6. Exit\n");
                int option = console.nextInt();

                if(!(option >= 1 && option <= 6)) {
                    System.out.println("Invalid option..");
                }

                //Check exit condition
                if(option == 6) {
                    System.out.print("Are you sure? Y / N\nInput: ");
                    if(console.nextLine().equals("Y") || console.nextLine().equals("y")) {
                        break;
                    }
                }

                //View personal information
                else if(option == 1) {
                    Customer.viewPersonalInformation(userLogin, console, b);
                }
                else if(option == 2) {
                    Customer.viewAccountInformation(userLogin, console, b, 0);
                }
                else if(option == 3) {
                    condition = transfer(userLogin, console, b);
                }
                else if(option == 4) {
                    condition = depositMoney(userLogin, console, b);
                }
                else if(option == 5) {
                    condition = withdrawMoney(userLogin, console, b);
                }

                if(condition) {
                    System.out.println("\nAction successful\n");
                }
            } catch(Exception e) {
                System.out.println("Invalid input: " + e.getMessage());
                console.nextLine();
            }
        }
    }
}