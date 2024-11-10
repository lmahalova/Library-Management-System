package src;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.awt.GridLayout;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class AdminFunctionalities extends JFrame implements CRUD<Book>{

    private final DefaultTableModel tableModel;
    private static final String DATA_FILE = "data/personal_database.csv";

    public AdminFunctionalities(DefaultTableModel tableModel){
        this.tableModel = tableModel;
    }

    @Override
    public void add(Book book) {
        String[] bookInfo = showAddBookDialog();
        if (bookInfo != null) {
            String title = bookInfo[0];
            String author = bookInfo[1];

            tableModel.addRow(new Object[] { title, author, "", "" });
        }
        saveChanges();
    }

    @Override
    public void delete(int rowIndex) {
        if (rowIndex >= 0 && rowIndex < tableModel.getRowCount()) {
            String title = (String) tableModel.getValueAt(rowIndex, 0);
            tableModel.removeRow(rowIndex);
            saveChanges();

            deleteFromPersonalDatabase(title);
        } else {
            JOptionPane.showMessageDialog(this,
                    "No row selected or invalid index",
                    "Deletion Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void update(int rowIndex, Book updatedData) {
        if (rowIndex >= 0 && rowIndex < tableModel.getRowCount() && updatedData != null) {
            String currentTitle = (String) tableModel.getValueAt(rowIndex, 0);
            tableModel.setValueAt(updatedData.getTitle(), rowIndex, 0);
            tableModel.setValueAt(updatedData.getAuthor(), rowIndex, 1);

            saveChanges();
            updatePersonalDatabase(currentTitle, updatedData);
        } else {
            throw new IllegalArgumentException("Invalid row index");
        }

    }

    @Override
    public void removeReview(int rowIndex, String reviewToRemove) {
        if (rowIndex >= 0 && rowIndex < tableModel.getRowCount()) {
            // Get book title and current reviews from the table model
            String bookTitle = (String) tableModel.getValueAt(rowIndex, 0); // Book title
            String bookAuthor = (String) tableModel.getValueAt(rowIndex, 1);
            String reviews = (String) tableModel.getValueAt(rowIndex, 3); // Reviews (4th column)

            // Ensure reviews contain the review to remove
            if (reviews != null && reviews.contains(reviewToRemove)) {
                // Remove the specific review
                String updatedReviews = Arrays.stream(reviews.split(", "))
                        .filter(r -> !r.equals(reviewToRemove)) // Exclude the review to remove
                        .collect(Collectors.joining(", ")); // Join remaining reviews

                // Update the table model with the new reviews
                tableModel.setValueAt(updatedReviews, rowIndex, 3);

                // Save changes to ensure persistence
                saveChanges();

                // Call the method to update the personal database CSV

                try {
                    deleteReview(reviewToRemove,bookTitle,bookAuthor);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                System.out.println("Review removed successfully.");

            } else {
                System.out.println("Review not found: " + reviewToRemove); // Log review not found
                throw new IllegalArgumentException("Review not found in the specified book.");
            }
        } else {
            System.out.println("Invalid row index: " + rowIndex); // Log invalid row index
            throw new IllegalArgumentException("Invalid row index.");
        }
    }

    private void saveChanges() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("data/general_database.csv"))) {
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                StringBuilder line = new StringBuilder();
                for (int j = 0; j < tableModel.getColumnCount(); j++) {
                    if (j > 0)
                        line.append(",");
                    line.append(tableModel.getValueAt(i, j));
                }
                writer.write(line.toString());
                writer.newLine();
            }

        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to save changes.");
        }
    }
    
    private String[] showAddBookDialog() {
        JTextField titleField = new JTextField();
        JTextField authorField = new JTextField();

        // Panel for the input fields
        JPanel panel = new JPanel(new GridLayout(2, 2));
        panel.add(new JLabel("Title:"));
        panel.add(titleField);
        panel.add(new JLabel("Author:"));
        panel.add(authorField);

        // Show the dialog and capture the response
        int result = JOptionPane.showConfirmDialog(this, panel, "Add New Book", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            String title = titleField.getText().trim();
            String author = authorField.getText().trim();

            if (title.isEmpty() || author.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Title and author cannot be empty.", "Invalid Input",
                        JOptionPane.WARNING_MESSAGE);
                return null; // Invalid input
            }

            return new String[] { title, author }; // Return the title and author
        }

        return null; // User canceled the operation
    }

    private void deleteFromPersonalDatabase(String title) {
        String personalDbPath = "data/personal_database.csv"; // Path to the personal database CSV
        List<String> updatedContent = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(personalDbPath))) {
            String line;

            while ((line = reader.readLine()) != null) {
                // Assuming CSV has a userID as the first value, followed by book data
                if (!line.contains(title)) { // Keep only lines that do not contain the book title
                    updatedContent.add(line); // Add to the updated list
                }
            }

        } catch (IOException ex) {
            ex.printStackTrace();
            System.out.println("Error reading personal database."); // Log the error
        }

        // Rewrite the personal database with the updated content
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(personalDbPath))) {
            for (String updatedLine : updatedContent) {
                writer.write(updatedLine);
                writer.newLine();
            }

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error updating personal database.");
        }
    }

    public Book showUpdateBookDialog(String currentTitle, String currentAuthor) {
        JTextField titleField = new JTextField(currentTitle);
        JTextField authorField = new JTextField(currentAuthor);

        JPanel panel = new JPanel(new GridLayout(2, 2));
        panel.add(new JLabel("Title:"));
        panel.add(titleField);
        panel.add(new JLabel("Author:"));
        panel.add(authorField);

        int result = JOptionPane.showConfirmDialog(this,
                panel,
                "Update Book",
                JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            String newTitle = titleField.getText().trim();
            String newAuthor = authorField.getText().trim();

            if (newTitle.isEmpty() || newAuthor.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Title and Author cannot be empty",
                        "Invalid Input",
                        JOptionPane.WARNING_MESSAGE);

                return null;
            }

            return new Book(newTitle, newAuthor);

        }
        return null;
    }


    private void updatePersonalDatabase(String currentTitle, Book updatedData) {
        String personalDbPath = "data/personal_database.csv"; // Path to personal database CSV
        List<String> updatedContent = new ArrayList<>();

        // Try-with-resources for BufferedReader
        try (BufferedReader reader = new BufferedReader(new FileReader(personalDbPath))) {
            String line;

            // Read and update the personal database based on the current title
            while ((line = reader.readLine()) != null) {
                if (line.contains(currentTitle)) { // If the line contains the old title, update it
                    String[] data = line.split(","); // Split CSV into fields
                    data[1] = updatedData.getTitle(); // Update the title
                    data[2] = updatedData.getAuthor(); // Update the author
                    String updatedLine = String.join(",", data); // Rejoin fields into CSV line
                    updatedContent.add(updatedLine); // Add updated line to the list
                } else {
                    updatedContent.add(line); // If it doesn't contain the title, keep the original line
                }
            }

        } catch (IOException ex) {
            ex.printStackTrace();
            System.out.println("Error reading personal database."); // Log error
        }

        // Try-with-resources for BufferedWriter to rewrite the personal database
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(personalDbPath))) {
            for (String updatedLine : updatedContent) {
                writer.write(updatedLine); // Write each updated line to the CSV
                writer.newLine(); // Move to the next line
            }

        } catch (IOException ex) {
            ex.printStackTrace();
            System.out.println("Error writing to personal database."); // Log error
        }
    }

    public void deleteReview(String username, String title, String author) throws IOException {
        String userID = UserDataReader.getUserID(username);
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(DATA_FILE))) {

            String line;

            while ((line = reader.readLine()) != null) {
                List<String> parts = CsvUtils.parseCsvLine(line);

                if (parts.size() > 0 && parts.get(0).equals(userID) && parts.get(1).equals(title) && parts.get(2).equals(author)) {
                    parts.set(10, "Add review");
                    line = String.join(",", parts);

                }
                lines.add(line);
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try (PrintWriter writer = new PrintWriter(DATA_FILE)) {
            for (String line : lines) {
                writer.println(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void deleteUser(String username) {
        String userDataPath = "data/user_data.csv"; // Path to user_data.csv
        String personalDbPath = "data/personal_database.csv"; // Path to personal_database.csv

        List<String> updatedUserData = new ArrayList<>();
        String userIDToDelete = null;

        // Read user_data.csv to find the user ID
        try (BufferedReader reader = new BufferedReader(new FileReader(userDataPath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(","); // Split CSV into fields
                if (data.length > 2 && data[0].equals(username)) {
                    userIDToDelete = data[2]; // Assuming 3rd column is the user ID
                } else {
                    updatedUserData.add(line); // Keep users other than the one to delete
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            System.out.println("Error reading user data."); // Log error
            return; // Exit on failure
        }

        // If userID found, proceed with deleting the user and their data
        if (userIDToDelete != null) {
            // Rewrite user_data.csv with updated content
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(userDataPath))) {
                for (String updatedLine : updatedUserData) {
                    writer.write(updatedLine); // Write each updated line
                    writer.newLine(); // Move to the next line
                }
            } catch (IOException ex) {
                ex.printStackTrace();
                System.out.println("Error updating user data."); // Log error
                return; // Exit on failure
            }

            List<String> updatedPersonalData = new ArrayList<>();

            // Read personal_database.csv and remove lines with the userID
            try (BufferedReader reader = new BufferedReader(new FileReader(personalDbPath))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (!line.startsWith(userIDToDelete + ",")) { // Exclude lines with matching userID
                        updatedPersonalData.add(line);
                    }
                }
            } catch (IOException ex) {
                ex.printStackTrace();
                System.out.println("Error reading personal database."); // Log error
                return; // Exit on failure
            }

            // Rewrite personal_database.csv with updated content
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(personalDbPath))) {
                for (String updatedLine : updatedPersonalData) {
                    writer.write(updatedLine); // Write each updated line
                    writer.newLine(); // Move to the next line
                }
            } catch (IOException ex) {
                ex.printStackTrace();
                System.out.println("Error updating personal database."); // Log error
            }

            System.out.println("User " + username + " and their data have been deleted successfully."); // Log success
        } else {
            System.out.println("User " + username + " not found."); // Log user not found
        }
    }
}
