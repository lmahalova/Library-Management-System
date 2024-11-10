package src;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class UserDetailsWindow extends JFrame {
    private JTextArea userDetailsTextArea;

    public UserDetailsWindow(String reviewer, String title, String author) throws IOException {
        setTitle("User Details");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new BorderLayout());

        userDetailsTextArea = new JTextArea();
        userDetailsTextArea.setEditable(false);

        JScrollPane scrollPane = new JScrollPane(userDetailsTextArea);
        panel.add(scrollPane, BorderLayout.CENTER);

        displayUserDetails(reviewer, title, author);

        add(panel);
    }

    private void displayUserDetails(String reviewer, String title, String author) throws IOException {
        String userID = UserDataReader.getUserID(reviewer);
        Book book = new Book(title, author);

        PersonalDatabase personalDatabase = new PersonalDatabase(book);
        GeneralDatabase generalDatabase = new GeneralDatabase(false);
        List<Double> ratings = generalDatabase.collectBookRatings("data/personal_database.csv", title, author);
        Optional<Double> optionalAverageRating = book.calculateAverageRating(ratings);
        StringBuilder userDetails = new StringBuilder();

        userDetails.append("Title: ").append(title).append("\n");
        userDetails.append("Author: ").append(author).append("\n");

        String formattedRating = optionalAverageRating.map(rating -> String.format("%.2f", rating)).orElse("No Rating");
        userDetails.append("Rating: ").append(formattedRating).append("\n");
        userDetails.append("Username: ").append(reviewer).append("\n");
        System.err.println("From displayUserDetails " + DatabaseOptionsWindow.getUserID());

        if (personalDatabase.getUserRatings(userID, title, author) == -1)
            userDetails.append("User Rating: ").append("No Rating").append("\n");
        else
            userDetails.append("User Rating: ").append(personalDatabase.getUserRatings(userID, title, author))
                    .append("\n");
        userDetails.append("User Review: ").append(personalDatabase.getUserReviews(userID, title, author)).append("\n");

        userDetailsTextArea.setText(userDetails.toString());
    }

}
