package src;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class UserDataReader {

    private static final String CSV_FILE_PATH = "data/user_data.csv";

    public static String getUserID(String username, char[] password) throws IOException {
        String providedPassword = new String(password); // Convert char[] to String
        try (BufferedReader reader = new BufferedReader(new FileReader(CSV_FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 3) {
                    String storedUsername = parts[0];
                    String storedPassword = parts[1];
                    String userID = parts[2];
                    if (storedUsername.equals(username) && storedPassword.equals(providedPassword)) {
                        return userID;
                    }
                }
            }
        }
        return null;
    }
    public static String getUserID(String username) throws IOException {

        try (BufferedReader reader = new BufferedReader(new FileReader(CSV_FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 3) {
                    String storedUsername = parts[0];
                    String userID = parts[2];
                    if (storedUsername.equals(username) ) {
                        return userID;
                    }
                }
            }
        }
        return null;
    }


    public static void main(String[] args) {
        try {
            String username = "Leyla123";
            String password = "Leyla12#";
            String userID = getUserID(username, password.toCharArray());
            if (userID != null) {
                System.out.println("UserID for user " + username + ": " + userID);
            } else {
                System.out.println("User not found or invalid credentials.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
