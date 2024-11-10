package src;


public class UserData{

    private String username;
    private char[] password;
    private String userID;

    public UserData(String username, char[] password) {
        this.username = username;
        this.password = password;
        this.userID = UserIDGenerator.generateUserID();
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setPassword(char[] password) {
        this.password = password;
    }

    public char[] getPassword() {
        return password;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

}
