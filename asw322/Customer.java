import java.util.*;
import java.sql.*;

public class Customer {
    private static String q;

    //Returns the year of the customer
    //Used an online algorithm for age calculator: https://howtodoinjava.com/java/calculate-age-from-date-of-birth/
    private static int getAge(String DOB) {
        String[] CURRENT = ProjectInterface.getCurrentDate();
        String[] BIRTH;
        BIRTH = DOB.split("-");


        //Convert from string into integer
        int[] birthDay, currDay;
        birthDay = new int[BIRTH.length];
        currDay = new int[CURRENT.length];

        for(int i = 0; i < BIRTH.length; i++) {
            birthDay[i] = Integer.parseInt(BIRTH[i]);
            currDay[i] = Integer.parseInt(CURRENT[i]);
        }

        int years = 0;
        int months = 0;
        int days = 0;

        //Algorithm to get age
        years = currDay[0] - birthDay[0];
        currDay[1]++;
        birthDay[1]++;

        months = currDay[1] - birthDay[1];

        if(months < 0) {
            years--;
            months = 12 - birthDay[1] + currDay[1];
            if(currDay[2] < birthDay[2]) {
                months--;
            } else if(months == 0 && currDay[2] < birthDay[2]) {
                years--;
                months = 11;
            }
        }

        if(currDay[2] > birthDay[2]) {
            days = currDay[2] - birthDay[2];
        } else if(currDay[2] < birthDay[2]) {
            int today = currDay[2];
            days = currDay[2] - birthDay[2] + today;
        } else {
            days = 0;
            if(months == 12) {
                years++;
                months = 0;
            }
        }

        return years;
    }

    //Returns true if the user enters the correct SSN -> they get 3 tries
    private static boolean checkSSN(String[] userLogin, Scanner console, Statement b) {
        ResultSet result = null;
        int counter = 0;
        try {
            while(counter < 3) {
                if(counter == 2) {
                    System.out.println("\nWarning, last trial before system locking..\n");
                }

                System.out.println("Security question:\nEnter SSN: ");
                String value = console.nextLine();
                
                //Formatting logistics
                if(value.charAt(3) == '-') {
                    q = "select * from CUSTOMER where SSN = '" + value + "'";
                    result = b.executeQuery(q);
                
                    //Return true if the ResultSet is not empty
                    if(result.next()) {
                        return true;
                    } else {
                        counter++;
                    }
                } else {
                    System.out.println("\nFormat input into xxx-xx-xxxx");
                    counter++;
                }
            }
            result.close();
            System.out.println("\nMaximum number of trails reached..Locking system");
            System.exit(0);
        } catch(Exception e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

    //View personal information
    protected static void viewPersonalInformation(String[] userLogin, Scanner console, Statement b) {
        try {
             //Fetching and print info
            System.out.println("------------------------------");
            System.out.println("Showing customer personal information");
            q = "select * from CUSTOMER where CUSTOMER_ID = '" + userLogin[0] + "'";
            ResultSet result = b.executeQuery(q);
            if(result.next()) {
                System.out.println("Customer ID: " + result.getString("CUSTOMER_ID") + "\nCustomer name: " + result.getString("CUSTOMER_NAME") + "\nHome address: " + result.getString("HOME_ADDRESS") + "\nPhone number: " + result.getString("PHONE") + "\nDate of Birth: " + result.getString("DOB"));    
            }

            //Making an update
            System.out.println("\nWould you like to make an update? Y / N\nInput: ");
            if(console.nextLine().equals("Y") || console.nextLine().equals("y")) {                        
                if(checkSSN(userLogin, console, b)) {
                    System.out.println("\nSecurity verified\n");
                    System.out.println("\nWhat would you like to update?\n1. Customer name\n2. Home address\n3. Phone number\n");
                    boolean rs = false;
                    switch(console.nextInt()) {
                        case 1: 
                            rs = updateName(userLogin, console, b);
                            break;
                        case 2:
                            rs = updateHomeAddress(userLogin, console, b);
                            break;
                        case 3:
                            rs = updatePhoneNumber(userLogin, console, b);
                            break;
                        default: 
                            System.out.println("Invalid option..\n");
                    }
                    if(rs) {
                        System.out.println("\nUpdate successful..\n");
                    }
                } 
            }
        } catch(Exception e) {
            System.out.println(e.getMessage());
        }
    }

    //View bill activities
    //NOTE: Prints all card payments and identifies if the statment has been paid or unpaid based on a fixed date
    //The date is: 2017-01-01. This is to purely demonstrate a feature that would normally exist inside a bank
    private static void viewBillActivities(String[] userLogin, Scanner console, Statement b) {
        //This function should print all the card_payments that are tied to account
        try {
             //Fetching and print info
             System.out.println("------------------------------");
             System.out.println("       Bill Activities        ");
             System.out.println("A");
             q = "select * from TRANSACTION natural join (select TRANSACTION_ID from CARD_PAYMENT natural join (select ACCOUNT_ID from ACCOUNT where CUSTOMER_ID = '" + userLogin[0] + "'))";
             ResultSet result = b.executeQuery(q);
             System.out.println("STATUS based on 2017-01-01");
             System.out.printf("%15s %20s %10s\n", "PAYMENT DATE", "AMOUNT DUE", "STATUS");
            while(result.next()) {
                if(getAge(result.getString("TRANSACTION_DATE")) < 3) {
                    System.out.printf("%15s %20s %10s\n", result.getString("TRANSACTION_DATE"), result.getString("TRANSACTION_AMOUNT"), "UNPAID");
                } 
                else {
                    System.out.printf("%15s %20s %10s\n", result.getString("TRANSACTION_DATE"), result.getString("TRANSACTION_AMOUNT"), "PAID");
                }
            }
            System.out.println();

        } catch(Exception e) {
            System.out.println(e.getMessage());
        }
    }

    //View all branches and ATM
    private static void viewBranchATM(String[] userLogin, Scanner console, Statement b) {
        try {
             //Fetching and print info
             System.out.println("Showing all branch info");
             System.out.println("------------------------------");
            
             q = "select * from BRANCH";
             ResultSet result = b.executeQuery(q);
             System.out.printf("%30s %20s\n", "BRANCH LOCATION", "HOURS");
             while(result.next()) {
                System.out.printf("%30s %20s\n", result.getString("BRANCH_ADDRESS"), result.getString("OPERATION_HOUR"));
             }
             System.out.println();
        } catch(Exception e) {
            System.out.println(e.getMessage());
        }
    }

    //View account information 
    protected static void viewAccountInformation(String[] userLogin, Scanner console, Statement b, int type) {
        try {
            q = "select * from ACCOUNT where customer_id = '" + userLogin[0] + "'";
            ResultSet result = b.executeQuery(q);

            System.out.println("Printing all account informations: \n");
            System.out.printf("%20s %15s %15s %15s\n", "ACCOUNT ID", "AMOUNT", "MIN BAL", "PENALTY");
            if(type == 1) {
                while(result.next()) {
                    System.out.printf("%20s %15s %15s %15s\n", "xxxxxxxx" + result.getString("ACCOUNT_ID").substring(8), result.getString("AMOUNT"), result.getString("MIN_BAL"), result.getString("PENALTY_AMOUNT"));
                }
            } else if(type == 0){
                while(result.next()) {
                    System.out.printf("%20s %15s %15s %15s\n", result.getString("ACCOUNT_ID"), result.getString("AMOUNT"), result.getString("MIN_BAL"), result.getString("PENALTY_AMOUNT"));
                }
            }
            
            if(type == 1) {
                outer: while(true) {
                    System.out.println("\nWhat would you like to do? \n1. Add a new account\n2. Delete an account (Incomplete)\n3. Go back\n");
                    boolean rs = false;
                    switch(console.nextInt()) {
                        case 1: 
                            rs = createNewAccount(userLogin, console, b);
                            break;
                        case 2:
                            System.out.println("Sorry this has not been implemented yet..\n");
                            break;
                        case 3:
                            break outer;
                    }
                    if(rs) {
                        System.out.println("Action successful..\n");
                    }
                }
            }            

        } catch(Exception e) {
            System.out.println(e.getMessage());
        }
    }

    //Create a new account 
    private static boolean createNewAccount(String[] userLogin, Scanner console, Statement b) {
        try {
            //Check if user is old enough to open account
            q = "select DOB from CUSTOMER where CUSTOMER_ID = '" + userLogin[0] + "'";
            ResultSet result = b.executeQuery(q);

            //Check age
            if(result.next() && getAge(result.getString("DOB")) >= 18) {
                System.out.println("Would you like to create a checking or savings account? C / S");
                console.nextLine();
                String newAcc = console.nextLine();
                
                //Generate new account ID
                String new_account_id = ""; 
                while(true) {
                    new_account_id = RandomGenerator.generateRandomAccountID(12);

                    //Verify if ID is not in database already
                    q = "select * from ACCOUNT where ACCOUNT_ID = '" + new_account_id + "'";
                    ResultSet result2 = b.executeQuery(q);

                    if(!result2.next()) {
                        break;
                    }
                }       

                String checkingSavingID = "";
                ResultSet result2 = null;
                ResultSet result3 = null;
                
                //Create a new checking account
                if(newAcc.equals("C") || newAcc.equals("c")) {
                    System.out.println("Generating new checking account number..");
                    //Insert into account
                    q = "insert into ACCOUNT (ACCOUNT_ID, MIN_BAL, PENALTY_AMOUNT, AMOUNT, CUSTOMER_ID) values (" + new_account_id + ", 1000, 5000, 0, '" + userLogin[0] + "')";
                    result2 = b.executeQuery(q);

                    if(result2.next()) {
                        System.out.println("New account information: ");
                        System.out.printf("%20s %15s %15s %15s\n", "xxxxxxxx" + new_account_id.substring(8), 0, 1000, 5000);
                        //Insert into checking
                        while(true) {
                            checkingSavingID = RandomGenerator.generateRandomAccountID(12);

                            q = "select * from CHECKING where CHECKING_ID = '" + checkingSavingID + "'";
                            ResultSet result4 = b.executeQuery(q);

                            if(!result4.next()) {
                                break;
                            }
                        }
                        q = "insert into CHECKING (ACCOUNT_ID, CHECKING_ID) values (" + new_account_id + ", " + checkingSavingID + ")";
                        result3 = b.executeQuery(q);
                    }
                }


                //Create new saving account
                if(newAcc.equals("S") || newAcc.equals("s")) {
                    System.out.println("Generating new saving account number..");
                    //Insert into account
                    q = "insert into ACCOUNT (ACCOUNT_ID, MIN_BAL, PENALTY_AMOUNT, AMOUNT, CUSTOMER_ID) values (" + new_account_id + ", 500, 0, 0, '" + userLogin[0] + "')";
                    result2 = b.executeQuery(q);

                    if(result2.next()) {
                        System.out.println("New account information: ");
                        System.out.printf("%20s %15s %15s %15s\n", "xxxxxxxx" + new_account_id.substring(8), 0, 500, 0);
                        //Insert into saving
                        while(true) {
                            checkingSavingID = RandomGenerator.generateRandomAccountID(12);

                            q = "select * from SAVING where SAVING_ID = '" + checkingSavingID + "'";
                            ResultSet result4 = b.executeQuery(q);

                            if(!result4.next()) {
                                break;
                            }
                        }
                        q = "insert into SAVING (ACCOUNT_ID, SAVING_ID) values (" + new_account_id + ", " + checkingSavingID + ")";
                        result3 = b.executeQuery(q);
                    }                    
                }

                //Insert new account into database
                if(result3.next()) {
                    return true;
                }
                else {
                    //Roll back new account in ACCOUNT table
                    q = "delete from ACCOUNT where ACCOUNT_ID = '" + new_account_id + "'";
                    ResultSet result5 = b.executeQuery(q);
                    if(result5.next()) {
                        return false;
                    } else {
                        System.out.println("Something went wrong");
                    }    
                }
            }   
            else {
                System.out.println("You are not old enough to open an account..\n");
                return false;
            }    
        } catch(Exception e) {
            System.out.println(e.getMessage());
        }
        
        return false;
    }

    //Make a purchase using a customer account
    private static boolean makePurchase(String[] userLogin, Scanner console, Statement b) {
        ResultSet result = null;
        ResultSet result2 = null;
        ResultSet result3 = null;

        try {
            String vendor_name = "";
            long vendor_id = 0;
            double amount = 0;
            String card_used ="";

            console.nextLine();
            //Getting information
            System.out.println("Make a purchase: \nLet's start off with the vendor info");
            System.out.println("------------------------------");
            System.out.println("What was the name of the vendor:");
            vendor_name = console.nextLine();
            System.out.println("Good, next what was the vendor_id: ");
            vendor_id = 0;

            while(true) {
                vendor_id = console.nextLong();

                if(vendor_id >= 1000 && vendor_id <= 9999) {
                    break;
                } else {
                    System.out.println("\nInput a valid 4 digit vendor ID: ");
                }
            }

            System.out.println("Good, next what was the card number that you used: ");
            console.nextLine();
            card_used = console.nextLine();
            System.out.println("Finally, how much was the transaction: ");
            amount = console.nextDouble();

            q ="select * from DEBIT_CARD where CARD_NUMBER = '" + card_used + "' and CUSTOMER_ID = '" + userLogin[0] + "'";
            result = b.executeQuery(q);

            //If the card is actually owned by the customer
            if(result.next()) {
                result.close();
                //Query information 
                q ="select * from ACCOUNT natural join (select * from DEBIT_CARD natural join CREDIT_CARD) where CARD_NUMBER = '" + card_used + "' and CUSTOMER_ID = '" + userLogin[0] + "'";
                result2 = b.executeQuery(q);
                
                //Is a credit card
                if(result2.next()) {
                    System.out.println("CREDIT CARD");
                    String cvv = result2.getString("CVV");
                    String expiration_date = result2.getString("EXPIRATION_DATE");
                    String account_id = result2.getString("ACCOUNT_ID");
                    double running_balance = result2.getDouble("RUNNING_BALANCE");
                    double credit_limit = result2.getDouble("CREDIT_LIMIT");

                    System.out.println("Running balance = " + running_balance);
                    System.out.println("Credit limit = " + credit_limit);
                    
                    result2.close();

                    //Check if credit limit will be exceeded
                    if(running_balance + amount <= credit_limit) {
                        String transaction_date = ProjectInterface.getCurrentDateString();
                        String transaction_id = ProjectInterface.getTransactionID(b);
                        String purchase_id = ProjectInterface.getPurchaseID(b);
                        //Insert info into transaction and purchases
                        q = "insert into TRANSACTION (TRANSACTION_ID, TRANSACTION_DATE, TRANSACTION_AMOUNT, CUSTOMER_ID, ACCOUNT_ID) values ('" + transaction_id + "', '" + transaction_date + "', '$" + amount + "', " + userLogin[0] + ", '" + account_id + "')";
                        b.executeQuery(q);
                        q = "insert into PURCHASES (TRANSACTION_ID, PURCHASES_ID, CARD_NUMBER, CVV, EXPIRATION_DATE, VENDOR_ID, VENDOR_NAME) values ('" + transaction_id + "', '" + purchase_id + "', '" + card_used + "', '" + cvv + "', '" + expiration_date + "', '" + vendor_id + "', '" + vendor_name + "')";
                        b.executeQuery(q);
                        //Update the credit card's running balance 
                        q = "update CREDIT_CARD set RUNNING_BALANCE = '" + (running_balance + amount) + "' where CARD_NUMBER = '" + card_used + "'";
                        b.executeQuery(q);
                        System.out.println("\nTransaction logged!");
                        return true;
                    } else {
                        System.out.println("Purchase exceeds credit limit");
                        return false;
                    }

                } 
                //Is a debit card
                else {
                    System.out.println("DEBIT CARD");
                    q = "select * from ACCOUNT natural join (select * from DEBIT_CARD where CARD_NUMBER = '" + card_used + "') where CUSTOMER_ID = '" + userLogin[0] + "'";
                    result3 = b.executeQuery(q);

                    if(result3.next()) {
                        String cvv = result3.getString("CVV");
                        String expiration_date = result3.getString("EXPIRATION_DATE");
                        String account_id = result3.getString("ACCOUNT_ID");
                        //Check if debit card account has enough money (if the checking account has enough money)
                        double account_amount = result3.getDouble("AMOUNT");
                        double minimum_balance = result3.getDouble("MIN_BAL");
                        
                        if(account_amount - amount >= minimum_balance) {
                            String transaction_date = ProjectInterface.getCurrentDateString();
                            String transaction_id = ProjectInterface.getTransactionID(b);
                            String purchase_id = ProjectInterface.getPurchaseID(b);                            
                            //Insert info into transaction and purchases
                            q = "insert into TRANSACTION (TRANSACTION_ID, TRANSACTION_DATE, TRANSACTION_AMOUNT, CUSTOMER_ID, ACCOUNT_ID) values ('" + transaction_id + "', '" + transaction_date + "', '$" + amount + "', " + userLogin[0] + ", '" + account_id + "')";
                            b.executeQuery(q);
                            q = "insert into PURCHASES (TRANSACTION_ID, PURCHASES_ID, CARD_NUMBER, CVV, EXPIRATION_DATE, VENDOR_ID, VENDOR_NAME) values ('" + transaction_id + "', '" + purchase_id + "', '" + card_used + "', '" + cvv + "', '" + expiration_date + "', '" + vendor_id + "', '" + vendor_name + "')";
                            b.executeQuery(q);
                            //Reduce the account amount 
                            q = "update ACCOUNT set AMOUNT = '" + (account_amount - amount) + "' where ACCOUNT_ID = '" + account_id + "'";
                            b.executeQuery(q);
                            System.out.println("\nTransaction logged!");
                            return true;
                        } else {
                            System.out.println("\nPurchase causes account to drop below minimum balance..Cannot continue");
                            return false;
                        }
                    } else {
                        System.out.println("\nNo checking accounts tied to this debit card");
                        return false;
                    }  
                }
            } else {
                System.out.println("\nThe card number does not belong to you!");
                return false;
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

    //Take out a new loan 
    private static boolean takeOutNewLoan(String[] userLogin, Scanner console, Statement b) {
        ResultSet result = null;
        ResultSet result2 = null;
        try {
            //Get customer inputs
            System.out.println("Let's take out a new loan. ");
            int loanType = 0;
            while(true) {
                System.out.println("Which type of loan would you like to take out?\n1. Mortgage\n2. Unsecured loan\n: ");
                loanType = console.nextInt();

                if(loanType == 1 || loanType == 2) {
                    break;
                } else {
                    System.out.println("Sorry, you input is invalid");
                }
            }
            double amount = 0;
            while(true) {
                System.out.println("First, enter the amount you want to take out: ");
                amount = console.nextDouble();

                if(amount > 100000 || amount <= 0) {
                    System.out.println("Sorry, we only process loans up to $100,000");
                } else {
                    break;
                }
            }
            double income = 0;
            while(true) {
                System.out.println("Next, enter your current income: ");
                income = console.nextDouble();

                if(income <= 0) {
                    System.out.println("Invalid income input");
                } else {
                    break;
                }
            }   
            String account_id = "";
            viewAccountInformation(userLogin, console, b, 0);
            console.nextLine();
            while(true) {
                System.out.println("Enter which account you want this loan to be associated with: ");
                account_id = console.nextLine();
                q = "select * from ACCOUNT where CUSTOMER_ID = '" + userLogin[0] + "' and ACCOUNT_ID = '" + account_id + "'";
                result2 = b.executeQuery(q);

                if(result2.next()) {
                    break;
                } else {
                    System.out.println("\nAccount information is incorrect..Please try again");
                }
            }

            //Process the loan
            String loan_id = ProjectInterface.getLoanID(b);
            String transaction_id = ProjectInterface.getTransactionID(b);
            String loan_payment_id = ProjectInterface.getLoanPaymentID(b);
            String transaction_date = ProjectInterface.getCurrentDateString();
            double loan_interest_rate = 0;
            double monthly_pay = 0;
            double conversion = amount / income;

            if(income > 0 && income <= 50000) {
                loan_interest_rate = 20;
            } else if(income > 50000 && income <= 150000) {
                loan_interest_rate = 40;
            } else if(income > 150000 && income <= 300000) {
                loan_interest_rate = 60;
            } else if(income > 300000 && income <= 500000) {
                loan_interest_rate = 80;
            } else if(income > 500000) {
                loan_interest_rate = 100;
            }

            if(conversion >= 0.40) {
                //Higher monthly payment 
                monthly_pay = 9000;
            } else if(conversion > 0 && conversion < 0.40) {
                //Lower monthly payment 
                monthly_pay = 4500;
            }
            
            //Mortgage
            String property_address = "";
            if(loanType == 1) {
                System.out.println("Finally, we need a property address to secure the loan: ");
                property_address = console.nextLine();

                //Confirm with customer
                System.out.println("Loan Summary:\nLoan Amount: $" + amount + "\nMonthly Payment: $" + monthly_pay + "\nInterest Rate: $" + loan_interest_rate + " per month" + "\nLoan payment date: " + transaction_date.substring(5) + "\nSecurity address: " + property_address);
                System.out.println("\nEnter Y to accept or any other to decline: ");

            } 
            //Unsecured loan
            else if(loanType == 2) {
                //Confirm with customer
                System.out.println("Loan Summary:\nLoan Amount: $" + amount + "\nMonthly Payment: $" + monthly_pay + "\nInterest Rate: $" + loan_interest_rate + " per month" + "\nLoan payment date: " + transaction_date.substring(5));
                System.out.println("\nEnter Y to accept or any other to decline: ");
            } 
            
            String action = console.nextLine();
            if(action.equals("Y") || action.equals("y")) {
                System.out.println("\nOne moment..Processing your loan..\n");
                //Insert into LOAN, LOAN_PAYMENT, TRANSACTION
                q = "insert into TRANSACTION (TRANSACTION_ID, TRANSACTION_DATE, TRANSACTION_AMOUNT, CUSTOMER_ID, ACCOUNT_ID) values ('" + transaction_id + "', '" + transaction_date + "', '$" + amount + "', " + userLogin[0] + ", '" + account_id + "')";
                b.executeQuery(q);
                q = "insert into LOAN (LOAN_ID, LOAN_INTEREST_RATE, LOAN_AMOUNT, MONTHLY_PAY, CUSTOMER_ID) values ('" + loan_id + "', '" + (int)loan_interest_rate + "', " + amount + ", '" + monthly_pay + "', '" + userLogin[0] + "')";
                b.executeQuery(q);
                q = "insert into LOAN_PAYMENT (TRANSACTION_ID, LOAN_PAYMENT_ID, LOAN_ID) values ('" + transaction_id + "', '" + loan_payment_id + "', '" + loan_id + "')";
                b.executeQuery(q);

                if(loanType == 1) {
                    q = "insert into MORTGAGE (LOAN_ID, HOME_INFO) values ('" + loan_id + "', '" + property_address + "')";
                    b.executeQuery(q);
                }

                System.out.println("------------------------------");
                System.out.printf("%25s %15s\n", "ACCOUNT ID:", account_id);
                System.out.printf("%25s %15s\n", "TRANSACTION ID:", transaction_id);
                System.out.printf("%25s %15s\n", "DATE:", transaction_date);
                System.out.printf("%25s %15s\n", "LOAN AMOUNT:", amount);
                System.out.printf("%25s %15s\n", "MONTHLY PAYMENT:", monthly_pay);
                System.out.printf("%25s %15s\n", "INTEREST RATE:", loan_interest_rate);

                return true;                
            } else {
                System.out.println("\nRejecting offer..Returning you home");
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

    //Returns true if the user updates their phone number 
    private static boolean updatePhoneNumber(String[] userLogin, Scanner console, Statement b) {
        ResultSet result = null;
        try {
            while(true) {
                console.nextLine();
                System.out.println("Please enter your new phone number: \n");
                String value = console.nextLine();
                if(value.charAt(3) == '-') {
                    q = "update CUSTOMER set PHONE ='" + value + "' where CUSTOMER_ID = '" + userLogin[0] + "'";
                    result = b.executeQuery(q);
                    
                    if(result.next()) {
                        result.close();
                        return true;
                    } else {
                        result.close();
                        System.out.println("Update failed..");
                    }
                }
            }
        } catch(Exception e) {
            System.out.println(e.getMessage());
        } finally {
            try { if(result != null) result.close(); } catch(Exception e) {};
        }
        return false;
    }

    private static boolean updateHomeAddress(String[] userLogin, Scanner console, Statement b) {
        ResultSet result = null;
        try {
            while(true) {
                console.nextLine();
                System.out.println("Please enter your new residential address: \n");
                String value = console.nextLine();
                q = "update CUSTOMER set HOME_ADDRESS = '" + value + "' where CUSTOMER_ID = '" + userLogin[0] + "'";
                result = b.executeQuery(q);

                if(result.next()) {
                    result.close();
                    return true;
                } else {
                    result.close();
                    System.out.println("Update failed..");
                }
            }
        } catch(Exception e) {
            System.out.println(e.getMessage());
        } finally {
            try { if(result != null) result.close(); } catch(Exception e) {};
        }
        return false;
    }

    private static boolean updateName(String[] userLogin, Scanner console, Statement b) {
        ResultSet result = null;
        try {
            console.nextLine();
            System.out.println("Please enter your new full name: \n"); 
            String value = console.nextLine();
            q = "update CUSTOMER set CUSTOMER_NAME = '" + value + "' where CUSTOMER_ID = '" + userLogin[0] + "'";
            result = b.executeQuery(q);

            if(result.next()) {
                result.close();
                return true;
            } else {
                result.close();
                System.out.println("Update failed..");
            }
        } catch(Exception e) {
            System.out.println(e.getMessage());
        } finally {
            try { if(result != null) result.close(); } catch(Exception e) {};
        }
        return false;
    }

    protected static void init(String[] userLogin, Scanner console, Statement b) {
        System.out.println("\nHELLO THERE, WELCOME TO NICKLE SAVINGS BANK CUSTOMER PAGE");
        ProjectInterface.printCurrentDate(ProjectInterface.getCurrentDate());

        while(true) {
            try {
                //Main Menu
                System.out.println("----------Main Menu-----------");
                System.out.println("Select an option: \n");
                System.out.println("1. View personal information\n2. View bill activites\n3. Find ATM & BRANCH\n4. See account informations\n5. Make a purchase\n6. Take out a new loan\n7. Exit\n");
                int option = console.nextInt();

                if(!(option >= 1 && option <= 7)) {
                    System.out.println("\nInvalid option..");
                }

                //Check exit condition
                if(option == 7) {
                    System.out.print("Are you sure? Y / N\nInput: ");
                    if(console.nextLine().equals("Y") || console.nextLine().equals("y")) {
                        break;
                    }
                }
                //View personal information
                else if(option == 1) {
                    viewPersonalInformation(userLogin, console, b);
                }
                else if(option == 2) {
                    viewBillActivities(userLogin, console, b);
                }
                else if(option == 3) {
                    viewBranchATM(userLogin, console, b);
                }
                else if(option == 4) {
                    viewAccountInformation(userLogin, console, b, 1);
                } 
                else if(option == 5) {
                    makePurchase(userLogin, console, b);
                } 
                else if(option == 6) {
                    takeOutNewLoan(userLogin, console, b);
                }
            } catch(Exception e) {
                System.out.println("\nInvalid input");
                console.nextLine();
            }
        }
    }
}

