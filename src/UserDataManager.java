package src;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UserDataManager {

    private static final String userDataFile = "data/user_data.csv";

    public void addUser(String username, char[] password) throws IOException {
        List<UserData> users = getUsers();

        for (UserData user : users) {
            if (user.getUsername().equals(username)) {
                if (Arrays.equals(user.getPassword(), password)) {
                    throw new IllegalArgumentException("Username and password already exists. Try to Log In");
                } else {
                    throw new IllegalArgumentException(
                            "Invalid credentials. Username already exists with a different password.");
                }
            }
        }

        UserData newUser = new UserData(username, password);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(userDataFile, true))) {
            writer.write(username);
            writer.write(",");
            writer.write(new String(password));
            writer.write(",");
            writer.write(newUser.getUserID());
            writer.write("\n");
        }
    }

    public boolean userExists(String username) throws IOException {
        List<UserData> users = getUsers();
        for (UserData user : users) {
            if (user.getUsername().equals(username)) {
                return true;
            }
        }
        return false;
    }

    public boolean isValidUser(String username, char[] password) throws IOException {
        List<UserData> users = getUsers();
        for (UserData user : users) {
            char[] storedPassword = user.getPassword();
            if (user.getUsername().equals(username) && Arrays.equals(storedPassword, password)) {
                return true;
            }
        }
        return false;
    }
    public boolean isUsernameTaken(String username) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(userDataFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length > 0 && parts[0].equals(username)) {
                    return true; 
                }
            }
        }
        return false; 
    }

    private List<UserData> getUsers() throws IOException {
        List<UserData> users = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(userDataFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 3) {
                    users.add(new UserData(parts[0], parts[1].toCharArray()));
                }
            }
        }
        return users;
    }


    public boolean isAdminChecker(String username, char[] password) throws IOException {
        return username.equals("admin") && Arrays.equals(password, "admin".toCharArray());
    }

}
