package src;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class UserMapping {
    
    private static final String USER_MAPPING_CSV = "data/user_data.csv";
    private static Map<String, String> userMap = new HashMap<>();

    public static void loadUserMapping(){
        if(userMap.isEmpty()){
            try(BufferedReader reader = new BufferedReader(new FileReader(USER_MAPPING_CSV))){
                String line;
                while((line = reader.readLine()) != null){
                    String[] parts = line.split(",");
                    if(parts.length >= 3){
                        String username = parts[0].trim();
                        String userID = parts[2].trim();
                        userMap.put(userID, username);
                    }
                }
            }catch (IOException e) {
                e.printStackTrace();
                System.out.println("Failed to load user mapping");
            }
        }
    }

    public static String getUsername(String userID){
        loadUserMapping();
        return userMap.getOrDefault(userID, "");
    }
}
