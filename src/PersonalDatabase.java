package src;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class PersonalDatabase extends GeneralDatabase {

    private static final String DATA_FILE = "data/personal_database.csv";
    private SimpleDateFormat dateFormat = new SimpleDateFormat("mm/dd/yy");

    private Book book; // Use composition to encapsulate book information
    private String status;
    private int timeSpent; // in minutes or hours (specify unit)
    private String startDate; // Format: dd/mm/yy
    private String endDate; // Format: dd/mm/yy
    private double userRatings; // Now encapsulated
    private String userReviews; // Now encapsulated
    private List<String> reviews;
    private RatingData ratingData;

    private List<Book> loadedBooks;

    public PersonalDatabase(Book book) {
        super(false);
        this.book = book;
        this.status = "Not started";
        this.timeSpent = 0;
        this.startDate = "";
        this.endDate = "";
        this.userRatings = -1;
        this.userReviews = "Add review";
        this.loadedBooks = new ArrayList<>();
        this.ratingData = new RatingData(0.0, 0);
        this.reviews = new LinkedList<>();
    }

    public void saveData(String userID,Book book) {
        try (BufferedReader reader = new BufferedReader(new FileReader(DATA_FILE))) {
            String line;
            boolean alreadyExists = false;

            // Check if the entry already exists
            while ((line = reader.readLine()) != null) {
                List<String> parts = CsvUtils.parseCsvLine(line);

                if (parts.size() > 0 && parts.get(0).equals(userID) && parts.size() >= 3
                        && parts.get(1).equals(book.getTitle()) && parts.get(2).equals(book.getAuthor())) {
                    alreadyExists = true;
                    break;
                }
            }

            if (!alreadyExists) {
                try (PrintWriter writer = new PrintWriter(new FileWriter(DATA_FILE, true))) {
                    String title = CsvUtils.escapeCsv(book.getTitle());
                    String author = CsvUtils.escapeCsv(book.getAuthor());

                    // Write book information
                    writer.print(userID + "," + title + "," + author + "," + ratingData + "," + reviews + "," + status
                            + "," + timeSpent + "," + startDate + "," + endDate);
                    System.err.println("From saveData:  " + ratingData);
                    // Write user rating and review
                    writer.print("," + userRatings + "," + CsvUtils.escapeCsv(userReviews));

                    writer.println(); // Move to the next line
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                System.err.println("Duplicate entry. Skipping saving data.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void loadData(String userID) {
        try (BufferedReader reader = new BufferedReader(new FileReader(DATA_FILE))) {
            this.loadedBooks.clear();
            String line;

            while ((line = reader.readLine()) != null) {
                // Correctly parse CSV with possible quoted values
                List<String> parts = CsvUtils.parseCsvLine(line);

                if (parts.size() > 0 && parts.get(0).equals(userID)) {
                    String title = parts.size() >= 2 ? parts.get(1) : "Unknown Title";
                    String author = parts.size() >= 3 ? parts.get(2) : "Unknown Author";
                    String status = parts.size() >= 5 ? parts.get(5) : "Not Started";
                    int timeSpent = parts.size() >= 6 ? Integer.parseInt(parts.get(6)) : 0;
                    String startDate = parts.size() >= 7 ? parts.get(7) : "";
                    String endDate = parts.size() >= 8 ? parts.get(8) : "";

                    // Parse average rating and number of ratings
                    Double averageRating = parts.size() >= 12 ? Double.parseDouble(parts.get(11)) : 0.0;
                    int numberOfRatings = parts.size() >= 13 ? Integer.parseInt(parts.get(12)) : 0;
                    ratingData = new RatingData(averageRating, numberOfRatings);

                    // Load user ratings (starting from index 11)
                    userRatings = parts.size() >= 10 ? Double.parseDouble(parts.get(9)) : -1;

                    // Load user reviews (starting from index 12)
                    userReviews = parts.size() >= 11 ? parts.get(10) : "Add review";

                    Book loadedBook = new Book(title, author);
                    this.book = loadedBook;
                    this.status = status;
                    this.timeSpent = timeSpent;
                    this.startDate = startDate;
                    this.endDate = endDate;
                    // System.err.println("From loadData: " + book + this.status);

                    System.out.println("Loaded book: " + loadedBook + this.status);
                    loadedBooks.add(loadedBook);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Error loading data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    //Generic Method to store both Rating and Review
    public <T> void saveOneField(String userID, int row, int column, T data) {
        List<String> lines = new ArrayList<>();
        column += 1;
        try (BufferedReader reader = new BufferedReader(new FileReader(DATA_FILE))) {

            String line;
            int currentRow = 0;
            while ((line = reader.readLine()) != null) {
                List<String> parts = CsvUtils.parseCsvLine(line);

                if (parts.size() > 0 && currentRow == row && parts.get(0).equals(userID)) {

                    if (column >= 0 && column < parts.size()) {
                        parts.set(column, String.valueOf(data)); // Update the specified column
                    }
                    line = String.join(",", parts); // Reconstruct the line with updated data

                }
                lines.add(line);
                if (parts.size() > 0 && parts.get(0).equals(userID))
                    currentRow++;

            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Write the updated lines back to the file
        try (PrintWriter writer = new PrintWriter(DATA_FILE)) {
            for (String line : lines) {
                writer.println(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method to collect ratings for a specific book from the CSV
    @Override
    public List<Double> collectBookRatings(String filename, String bookTitle, String bookAuthor) {
        List<Double> ratings = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;

            while ((line = reader.readLine()) != null) {
                List<String> parts = CsvUtils.parseCsvLine(line); // Parse CSV line into parts

                // Check if the book title and author match the given parameters
                if (parts.size() > 9 && parts.get(1).equals(bookTitle) && parts.get(2).equals(bookAuthor)) {
                    double userRating = Double.parseDouble(parts.get(9)); // Assuming index 9 for user ratings
                    if (userRating > 0) { // Only add positive ratings
                        ratings.add(userRating);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace(); // Handle exceptions appropriately
        }

        return ratings;
    }

    @Override
    public List<String> collectBookReviewers(String filename, String bookTitle, String bookAuthor) throws IOException {
        List<String> reviewers = new ArrayList<>();
        UserMapping.loadUserMapping();

        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                List<String> parts = CsvUtils.parseCsvLine(line);

                try {

                    if (parts.size() >= 10 && parts.get(1).equals(bookTitle) && parts.get(2).equals(bookAuthor)) {
                        String userID = parts.get(0);
                        String userName = UserMapping.getUsername(userID);
                        String userReview = parts.get(10).trim();

                        if (!userReview.isEmpty() && !userReview.equals("Add review") && !userReview.equals("No Review") && !reviewers.contains(userName)) {
                            reviewers.add(userName);
                        }

                    }

                } catch (IndexOutOfBoundsException e) {
                    e.printStackTrace();
                    System.out.println("Error Processing the line: " + line);
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error reading CSV file: " + e.getMessage());
        }

        return reviewers;
    }


    public Book getBook() {
        return book;
    }

    public String getStatus(String userID, String title, String author) {
        try (BufferedReader reader = new BufferedReader(new FileReader(DATA_FILE))) {

            String line;
            while ((line = reader.readLine()) != null) {
                List<String> parts = CsvUtils.parseCsvLine(line);
                if (parts.size() > 0 && parts.get(0).equals(userID) && parts.size() >= 6 && parts.get(1).equals(title)
                        && parts.get(2).equals(author)) {

                    return parts.get(5);
                }

            }

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return status;
    }

public String getTimeSpent(String userID, String title, String author) {
    SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
    try (BufferedReader reader = new BufferedReader(new FileReader(DATA_FILE))) {
        String line;
        while ((line = reader.readLine()) != null) {
            List<String> parts = CsvUtils.parseCsvLine(line);
            if (parts.size() > 0 && parts.get(0).equals(userID) && parts.size() >= 9 &&
                    parts.get(1).equals(title) && parts.get(2).equals(author)) {
                try {
                    // Parse start and end dates from parts
                    String startDateStr = parts.get(7);
                    String endDateStr = parts.get(8);

                    if (!startDateStr.isEmpty() && !endDateStr.isEmpty()) {
                        Date startDate = dateFormat.parse(startDateStr);
                        Date endDate = dateFormat.parse(endDateStr);

                        // Logging for troubleshooting
                        System.out.println("Start Date: " + startDate);
                        System.out.println("End Date: " + endDate);

                        // Calculate the time difference in milliseconds
                        long timeDifferenceMillis = endDate.getTime() - startDate.getTime();

                        // Logging for troubleshooting
                        System.out.println("Time Difference (ms): " + timeDifferenceMillis);

                        // Convert milliseconds to minutes and return
                        return String.valueOf((int) (timeDifferenceMillis / (1000 * 60)));
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                    return "";
                }
            }
        }
    } catch (FileNotFoundException e) {
        throw new RuntimeException(e);
    } catch (IOException e) {
        throw new RuntimeException(e);
    }
    return ""; // Return 0 if no time spent is found
}

    public String getStartDate(String userID, String title, String author) {
        try (BufferedReader reader = new BufferedReader(new FileReader(DATA_FILE))) {

            String line;
            while ((line = reader.readLine()) != null) {
                List<String> parts = CsvUtils.parseCsvLine(line);
                if (parts.size() > 0 && parts.get(0).equals(userID) && parts.size() >= 8 && parts.get(1).equals(title)
                        && parts.get(2).equals(author)) {

                    return parts.get(7);
                }

            }

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return startDate;
    }

    public String getEndDate(String userID, String title, String author) {
        try (BufferedReader reader = new BufferedReader(new FileReader(DATA_FILE))) {

            String line;
            while ((line = reader.readLine()) != null) {
                List<String> parts = CsvUtils.parseCsvLine(line);
                if (parts.size() > 0 && parts.get(0).equals(userID) && parts.size() >= 9 && parts.get(1).equals(title)
                        && parts.get(2).equals(author)) {
                    return parts.get(8);
                }

            }

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return endDate;
    }
    public String getTheDate(String userID, int row, int column) {

        try (BufferedReader reader = new BufferedReader(new FileReader(DATA_FILE))) {
            String line;
            int currentRow = 0;
            while ((line = reader.readLine()) != null) {
                List<String> parts = CsvUtils.parseCsvLine(line);

                if (parts.size() > 0 && currentRow == row && parts.get(0).equals(userID)) {
                    if (column >= 0 && column < parts.size()) {
                        return parts.get(column);
                    }
                }
                if (parts.size() > 0 && parts.get(0).equals(userID)) {
                    currentRow++;
                }
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return ""; // Return empty string if end date not found
    }

    public Double getUserRatings(String userID, String title, String author) {
        try (BufferedReader reader = new BufferedReader(new FileReader(DATA_FILE))) {

            String line;
            while ((line = reader.readLine()) != null) {
                List<String> parts = CsvUtils.parseCsvLine(line);
                if (parts.size() > 0 && parts.get(0).equals(userID) && parts.size() >= 10 && parts.get(1).equals(title)
                        && parts.get(2).equals(author)) {
                    return Double.valueOf(parts.get(9));
                }

            }

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return this.userRatings; // Read-only list
    }

    public double getAverageRating(String userID, String title, String author) {
        try (BufferedReader reader = new BufferedReader(new FileReader(DATA_FILE))) {

            String line;
            while ((line = reader.readLine()) != null) {
                List<String> parts = CsvUtils.parseCsvLine(line);
                if (parts.size() > 0 && parts.get(0).equals(userID) && parts.size() >= 12 && parts.get(1).equals(title)
                        && parts.get(2).equals(author)) {

                    return Double.parseDouble(parts.get(11));
                }

            }

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return 0.0;
    }

    public int getNumberOfRatings(String userID, String title, String author) {
        try (BufferedReader reader = new BufferedReader(new FileReader(DATA_FILE))) {

            String line;
            while ((line = reader.readLine()) != null) {
                List<String> parts = CsvUtils.parseCsvLine(line);
                if (parts.size() > 0 && parts.get(0).equals(userID) && parts.size() >= 13 && parts.get(1).equals(title)
                        && parts.get(2).equals(author)) {

                    return Integer.parseInt(parts.get(12));
                }

            }

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return 0;
    }

    public String getUserReviews(String userID, String title, String author) {
        try (BufferedReader reader = new BufferedReader(new FileReader(DATA_FILE))) {

            String line;
            while ((line = reader.readLine()) != null) {
                List<String> parts = CsvUtils.parseCsvLine(line);
                if (parts.size() > 0 && parts.get(0).equals(userID) && parts.size() >= 11 && parts.get(1).equals(title)
                        && parts.get(2).equals(author)) {

                    return parts.get(10);
                }

            }

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return "Add review";
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setTimeSpent(int timeSpent) {
        if (timeSpent < 0) {
            throw new IllegalArgumentException("Time spent cannot be less than zero.");
        }
        this.timeSpent = timeSpent;
    }

    public void setStartDate(String startDate) {
        if (!isValidDate(startDate)) {
            throw new IllegalArgumentException("Invalid date format.");
        }
        if (!endDate.isEmpty() && startDate.compareTo(endDate) > 0) {
            throw new IllegalArgumentException("Start date cannot be after the end date.");
        }
        this.startDate = startDate;
    }

    public void setEndDate(String endDate) {
        if (!isValidDate(endDate)) {
            throw new IllegalArgumentException("Invalid date format.");
        }
        if (!startDate.isEmpty() && endDate.compareTo(startDate) < 0) {
            throw new IllegalArgumentException("End date cannot be before the start date.");
        }
        this.endDate = endDate;
    }

    public void addUserReview(String userReview) {
        userReviews = userReview;
    }

    private boolean isValidDate(String date) {
        return date.matches("\\d{2}/\\d{2}/\\d{2}"); // Simple date format check
    }

    public List<Book> getLoadedBooks() {
        return loadedBooks;
    }

    public void setLoadedBooks(List<Book> loadedBooks) {
        this.loadedBooks = loadedBooks;
    }

    public RatingData getRatingData() {
        return ratingData;
    }

    public void setRatingData(RatingData ratingData) {
        this.ratingData = ratingData;
    }

    public List<String> getReviews() {
        return reviews;
    }

    public void setReviews(List<String> reviews) {
        this.reviews = reviews;
    }

    @Override
    public String toString() {
        return "PersonalDatabase [book=" + book + ", status=" + status + ", timeSpent=" + timeSpent + ", startDate="
                + startDate + ", endDate=" + endDate + ", userRatings=" + userRatings + ", userReviews=" + userReviews
                + ", reviews=" + reviews + ", ratingData=" + ratingData + "]";
    }

}
