package src;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UserFunctionalities {

    private static final String DATA_FILE = "data/personal_database.csv";

    // Method to remove a book from the CSV file without a temp file
    public static void removeBookAsUserFromYourDB(String userID, Book book) {
        List<String> lines = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(DATA_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                List<String> parts = Arrays.asList(line.split(","));
                if (parts.size() > 2 && parts.get(0).equals(userID) && parts.get(1).equals(book.getTitle()) && parts.get(2).equals(book.getAuthor())) {
                    continue; // skip this line to remove the book
                }
                lines.add(line); // retain lines that are not to be removed
            }

            try (PrintWriter writer = new PrintWriter(new FileWriter(DATA_FILE, false))) { // 'false' to overwrite
                for (String retainedLine : lines) {
                    writer.println(retainedLine);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
