package src;

import java.util.UUID;

public class UserIDGenerator {
    public static String generateUserID() {
        return "user" + UUID.randomUUID().toString();
    }
}
