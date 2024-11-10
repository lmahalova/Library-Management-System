package src;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class PasswordChecker {
    private static ResourceBundle credentialErrors;
    public static List<String> validate(String username, String password, Locale locale) {
        credentialErrors = ResourceBundleLoader.loadResourceBundle("src.credentialErrors", locale);
        List<String> errorMessage = new ArrayList<>();

        if (username.isEmpty() || password.length() == 0) {
            errorMessage.add(credentialErrors.getString("EMPTY"));
        }

        if (password.length() < 8) {
            errorMessage.add(credentialErrors.getString("PASSWORD_LENGTH"));
        }

        if (!password.matches(".*[A-Z].*")) {
            errorMessage.add(credentialErrors.getString("UPPERCASE_LETTER"));
        }

        if (!password.matches(".*[a-z].*")) {
            errorMessage.add(credentialErrors.getString("LOWERCASE_LETTER"));
        }

        if (!password.matches(".*[!@#$%^&*()].*")) {
            errorMessage.add(credentialErrors.getString("SPECIAL_CHARACTER"));
        }

        if (!password.matches(".*\\d.*")) {
            errorMessage.add(credentialErrors.getString("NUMBER"));
        }

        return errorMessage;
    }
}
