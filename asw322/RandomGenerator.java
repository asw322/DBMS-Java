import java.util.*;
import java.lang.Math;

public class RandomGenerator {
    //Global objects
    private static final Random rand = new Random();

    //Returns a string for ACCOUNT_ID
    //Got algorithm from: https://www.geeksforgeeks.org/generate-random-string-of-given-size-in-java/
    public static String generateRandomAccountID(int size) {
        String alphaNumericString = "0123456789";
        StringBuilder sb = new StringBuilder(size);

        for(int i = 0; i < size; i++) {
            int index = (int)(alphaNumericString.length() * Math.random());
            sb.append(alphaNumericString.charAt(index));
        }
        return sb.toString();
    }

    //Reutrn a random string 
    //Got algorithm from: https://www.geeksforgeeks.org/generate-random-string-of-given-size-in-java/
    public static String generateRandomString(int size) {
        String alphaNumericString = "0123456789abcdefghijklmnopqrstuvwxyz";
        StringBuilder sb = new StringBuilder(size);
        
        for(int i = 0; i < size; i++) {
            //random index from 0 to size of alphaNumeric
            int index = (int)(alphaNumericString.length() * Math.random());
            sb.append(alphaNumericString.charAt(index));
        }
        return sb.toString();
    }


    public static char[] generateRandomChar(int size) {
        char[] arr = new char[size];
        for(int i = 0; i < size; i++) {
            arr[i] = (char) (rand.nextInt((126-33)+1) + 33);
        }
        return arr;
    }
    public static int[] generateRandomInt(int size, int length) {
        int[] arr;
        //Range from 1 - 9
        if(length < 10 && length > 0) {
            arr = new int[size];
            for(int i = 0; i < arr.length; i++) {
                arr[i] = rand.nextInt((999999999 - 100000000) + 1) + 100000000;
            }
            return arr;
        }

        //Range from 10 - infinity
        if(length >= 10) {
            arr = new int[size * 4];
            for(int i = 0; i < arr.length; i++) {
                arr[i] = rand.nextInt((9999-1000) + 1) + 1000;
            }
            return arr;
        }
        return null;
    }
}