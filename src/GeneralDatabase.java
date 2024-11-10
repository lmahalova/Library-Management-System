package src;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class GeneralDatabase {
    private List<Book> books;
    private boolean isAdmin;
    private static final String CSV_DELIMITER = ","; // Delimiter used in CSV

    public GeneralDatabase(boolean isAdmin) {
        this.books = new ArrayList<>();
        this.isAdmin = isAdmin;
    }

    public Object[][] getBooksAsArray() throws IOException {
        Object[][] data = new Object[books.size()][4];
        for (int i = 0; i < books.size(); i++) {
            Book book = books.get(i);

            List<Double> ratings = collectBookRatings("data/personal_database.csv", book.getTitle(), book.getAuthor());
            List<String> reviewers = collectBookReviewers("data/personal_database.csv", book.getTitle(), book.getAuthor());
            Optional<Double> optionalAverageRating = book.calculateAverageRating(ratings); // Get average rating

            int ratingCount = ratings.size();
            String formattedRating;

            if (ratingCount > 0) {
                formattedRating = optionalAverageRating
                        .map(r -> String.format("%.2f (%d)", r, ratingCount)) // Format average rating
                        .orElse("No Rating"); // Handle no ratings
            } else {
                formattedRating = "No Rating";
            }

            String reviewersList = reviewers.isEmpty() ? "No Review" : String.join(", ", reviewers);

            data[i][0] = book.getTitle();
            data[i][1] = book.getAuthor();
            data[i][2] = formattedRating;
            data[i][3] = reviewersList;
        }
        return data;
    }

    public void loadFromCSV(String filename) throws IOException {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(
                new FileInputStream(filename), StandardCharsets.UTF_8))) {
            String line;
            
            while ((line = br.readLine()) != null) {
                System.out.println("Processing line: " + line);

                // Use regex to split on comma, ensuring commas within quotes are ignored
                String[] parts = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);

                // Handling cases where either title or author may be missing
                String titlePart = (parts.length > 0 && !parts[0].trim().isEmpty()) ? parts[0].trim().replace("\"", "")
                        : "Unknown Title";
                String authorPart = (parts.length > 1 && !parts[1].trim().isEmpty()) ? parts[1].trim().replace("\"", "")
                        : "Unknown Author";

                // If there are multiple titles separated by a delimiter (like a comma), split
                // them
                String[] titles = titlePart.split(","); // Assuming the delimiter is a comma
                String author = authorPart.trim();

                // Loop through each title, creating a separate Book for each
                for (String title : titles) {
                    title = title.trim();
                    if (!title.isEmpty() && !author.isEmpty()) {
                        books.add(new Book(title, author));
                        System.out.println("Added book: " + title + " by " + author);
                    } else {
                        System.out.println("Skipping due to empty title or author: " + line);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Failed to load data.");
        }
    }

    // Method to collect all ratings for a given book (by title and author) from the
    // CSV
    public List<Double> collectBookRatings(String filename, String bookTitle, String bookAuthor) throws IOException {
        List<Double> ratings = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;

            while ((line = reader.readLine()) != null) {
                List<String> parts = CsvUtils.parseCsvLine(line);

                if (parts.size() > 9 && parts.get(1).equals(bookTitle) && parts.get(2).equals(bookAuthor)) {
                    double userRating = Double.parseDouble(parts.get(9)); // Example index for user ratings

                    if (userRating > 0) { // Valid ratings are greater than zero
                        ratings.add(userRating);
                    }
                }
            }
        }

        return ratings;
    }

    public List<String> collectBookReviewers(String filename, String bookTitle, String bookAuthor) throws IOException {
        List<String> reviewers = new ArrayList<>();
        UserMapping.loadUserMapping();

        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                List<String> parts = CsvUtils.parseCsvLine(line);

                if (parts.size() >= 10 && parts.get(1).equals(bookTitle) && parts.get(2).equals(bookAuthor)) {
                    String userID = parts.get(0);
                    String userName = UserMapping.getUsername(userID);
                    String userReview = parts.get(10);

                    if (!userReview.isEmpty() && !userReview.equals("Add review") && !userReview.equals("No Review") && !reviewers.contains(userName)) {
                            reviewers.add(userName);
                        }
                }

            }

        }

        return reviewers;
    }

    public void updateAverageRatingInCSV(String filename, String bookTitle, String bookAuthor) throws IOException {
        GeneralDatabase generalDatabase = new GeneralDatabase(false);
        List<Double> ratings = generalDatabase.collectBookRatings(filename, bookTitle, bookAuthor); // Get all ratings
                                                                                                    // for the book

        // Calculate the average rating
        Book tempBook = new Book(bookTitle, bookAuthor);
        Optional<Double> optionalAverage = tempBook.calculateAverageRating(ratings);
        String averageRating = optionalAverage
                .map(r -> String.format("Average Rating: %.2f", r))
                .orElse("No Rating");

        // Update the CSV with the calculated average rating
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                List<String> parts = CsvUtils.parseCsvLine(line);

                if (parts.size() > 2 && parts.get(1).equals(bookTitle) && parts.get(2).equals(bookAuthor)) {
                    parts.set(3, averageRating); // Update the average rating column (example index)
                }

                lines.add(String.join(CSV_DELIMITER, parts)); // Reconstruct the updated CSV line
            }
        }

        // Rewrite the updated lines to the CSV file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            for (String updatedLine : lines) {
                writer.write(updatedLine);
                writer.newLine(); // New line for each entry
            }
        }
    }


}
