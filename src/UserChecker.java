package src;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class UserChecker {
    private static ResourceBundle credentialErrors;

    public static List<String> validateUsername(String username, Locale locale) {
        credentialErrors = ResourceBundleLoader.loadResourceBundle("src.credentialErrors", locale);
        List<String> errors = new ArrayList<>();
        
        if (username.length() < 8) {
            errors.add(credentialErrors.getString("USERNAME_LENGTH"));
        }
        
        return errors;
    }
}

