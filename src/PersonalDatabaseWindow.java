package src;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

public class PersonalDatabaseWindow extends JFrame {
    private PersonalDatabase personalDatabase;
    private JTable table;
    private static String userID;
    private static final String DATA_FILE = "data/personal_database.csv";
    private Map<Integer, Integer> clickCounts = new HashMap<>();
    private TableRowSorter<DefaultTableModel> sorter;
    private List<Integer> sortSequence;
    private Map<Integer, SortOrder> sortOrderMap;

    private ResourceBundle databases;

    public PersonalDatabaseWindow(PersonalDatabase personalDatabase, String userID, Locale locale) {
        databases = ResourceBundleLoader.loadResourceBundle("src.databases", locale);
        this.userID = userID;
        this.personalDatabase = personalDatabase;

        setTitle("Personal Database");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel panel = new JPanel(new BorderLayout());
        DefaultTableModel model = new DefaultTableModel(getData(), getColumnNames());
        for (int i = 0; i < getColumnNames().length; i++) {
            clickCounts.put(i, 0);
        }
        table = new JTable(model) {
            @Override
            public boolean isCellEditable(int row, int column) {

                return column >= 4;

            }
        };

        this.sorter = new TableRowSorter<>(model);
        this.sortOrderMap = new HashMap<>();
        this.sortSequence = new ArrayList<>();
        this.clickCounts = new HashMap<>();
        table.setRowSorter(sorter);

        setupTableSorting();

        table.setPreferredScrollableViewportSize(new Dimension(800, 500));
        table.setFillsViewportHeight(true);

        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        table.getColumnModel().getColumn(6).setCellEditor(new StartDateCellEditor(personalDatabase,locale));
        table.getColumnModel().getColumn(7).setCellEditor(new EndDateCellEditor(personalDatabase,locale));
        table.getColumnModel().getColumn(8).setCellEditor(new RatingCellEditor(personalDatabase, locale));
        table.getColumnModel().getColumn(9).setCellEditor(new ReviewCellEditor(personalDatabase,locale));
        table.getColumnModel().getColumn(4).setCellEditor(new StatusCellEditor(personalDatabase, table,userID));

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                int column = table.columnAtPoint(e.getPoint());
                if (column == 3) {
                    String reviewersList = (String) table.getValueAt(row, column);
                    String[] reviewers = reviewersList.split(", "); // Split the reviewers by comma and space
                    TableClickHandler tableClickHandler = new TableClickHandler(table);
                    tableClickHandler.handleTableClick(e, row, column, reviewers); // Handle clicks on reviewers
                }
            }
        });

        JButton newBookButton = new JButton(databases.getString("NEW_BOOK"));
        newBookButton.addActionListener(e -> {
            String title = JOptionPane.showInputDialog("Enter book title:");
            String author = JOptionPane.showInputDialog("Enter book author:");
            if (title != null && author != null && !title.isEmpty() && !author.isEmpty()) {
                Book newBook = new Book(title, author);
                personalDatabase.getLoadedBooks().add(newBook);
                model.addRow(new Object[] { title, author, "", "", "", "", "", "", "Add Rating", "Add Review" });
                personalDatabase.saveData(userID,newBook);


            } else {
                JOptionPane.showMessageDialog(null, "Title and author must not be empty!");
            }
        });

        JButton removeBookButton = new JButton(databases.getString("REMOVE_BOOK"));
        removeBookButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {
                String title = (String) table.getValueAt(selectedRow, 0);
                String author = (String) table.getValueAt(selectedRow, 1);
                for (int i = 0; i < personalDatabase.getLoadedBooks().size(); i++) {
                    Book book = personalDatabase.getLoadedBooks().get(i);
                    if (book.getTitle().equals(title) && book.getAuthor().equals(author)) {
                        personalDatabase.getLoadedBooks().remove(i);
                        UserFunctionalities.removeBookAsUserFromYourDB(userID, book);
                        break;
                    }
                }
                model.removeRow(selectedRow);
            } else {
                JOptionPane.showMessageDialog(null, "Please select a book to remove.");
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(newBookButton);
        buttonPanel.add(removeBookButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        add(panel);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);

            }
        });
    }

    private void setupTableSorting() {
        JTableHeader tableHeader = table.getTableHeader();
        tableHeader.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int column = table.columnAtPoint(e.getPoint());
                toggleSortOrder(column); // Toggle sorting based on clicks
            }
        });
    }

    private void toggleSortOrder(int column) {
        int clickCount = clickCounts.getOrDefault(column, 0);

        switch (clickCount) {
            case 0:
                // First click: ascending order
                sortOrderMap.put(column, SortOrder.ASCENDING);
                clickCounts.put(column, 1);
                sortSequence.add(column);
                break;
            case 1:
                // Second click: descending order
                sortOrderMap.put(column, SortOrder.DESCENDING);
                clickCounts.put(column, 2);
                break;
            case 2:
                // Third click: reset sorting
                sortOrderMap.remove(column);
                clickCounts.put(column, 0);
                sortSequence.remove((Integer) column);
                break;
        }

        // Build sort keys based on current sort sequence
        List<RowSorter.SortKey> sortKeys = new ArrayList<>();
        for (int colIndex : sortSequence) {
            if (sortOrderMap.containsKey(colIndex)) {
                sortKeys.add(new RowSorter.SortKey(colIndex, sortOrderMap.get(colIndex)));
            }
        }

        sorter.setSortKeys(sortKeys);
    }

    // Modify the getData() method in PersonalDatabaseWindow
    public Object[][] getData() {
        List<Book> loadedBooks = personalDatabase.getLoadedBooks();
        Object[][] data = new Object[loadedBooks.size()][10]; // Default to an empty array in case of exceptions
        try {

            for (int i = 0; i < loadedBooks.size(); i++) {
                Book book = loadedBooks.get(i);

                // Collect ratings for the book
                List<Double> ratings = personalDatabase.collectBookRatings(DATA_FILE, book.getTitle(),
                        book.getAuthor());
                Optional<Double> optionalAverageRating = book.calculateAverageRating(ratings);

                int ratingCount = ratings.size();
                String formattedRating;

                if (ratingCount > 0) {
                    formattedRating = optionalAverageRating
                            .map(r -> String.format("%.2f (%d)", r, ratingCount)) // Format the rating
                            .orElse("No Rating");
                } else {
                    formattedRating = "No Rating";
                }

                List<String> reviewers = personalDatabase.collectBookReviewers(DATA_FILE, book.getTitle(),
                        book.getAuthor());
                String reviewersList = reviewers.isEmpty() ? "No Review" : String.join(", ", reviewers);

                // Set the User Ratings
                Double userRating = personalDatabase.getUserRatings(userID, book.getTitle(), book.getAuthor());
                String userRatingStr = (userRating == -1) ? "Add Rating" : userRating.toString();

                // User reviews
                String userReviews = personalDatabase.getUserReviews(userID, book.getTitle(), book.getAuthor());
                userReviews = userReviews.isEmpty() ? "Add Review" : userReviews;

                // Populate the data array
                data[i] = new Object[] {
                        book.getTitle(), // Title
                        book.getAuthor(), // Author
                        formattedRating, // Average rating
                        reviewersList, // User reviews (update later)
                        personalDatabase.getStatus(userID, book.getTitle(), book.getAuthor()), // Status
                        personalDatabase.getTimeSpent(userID, book.getTitle(), book.getAuthor()), // Time spent
                        personalDatabase.getStartDate(userID, book.getTitle(), book.getAuthor()), // Start date
                        personalDatabase.getEndDate(userID, book.getTitle(), book.getAuthor()), // End date
                        userRatingStr, // User rating (with "Add Rating" for -1)
                        userReviews // User reviews
                };
            }
        } catch (Exception e) {
            System.out.println("Error in getData method: " + e.getMessage());
            e.printStackTrace();
        }

        return data;
    }

    private String[] getColumnNames() {
        return new String[] {
                "Title",
                "Author",
                "Rating",
                "Reviews",
                "Status",
                "Time Spent",
                "Start Date",
                "End Date",
                "User Rating",
                "User Review"
        };
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Book book = new Book("Sample Title", "Sample Author");
            PersonalDatabase personalDatabase = new PersonalDatabase(book);

            personalDatabase.setStatus("In progress");
            personalDatabase.setTimeSpent(2); // hours
            personalDatabase.setStartDate("01/01/24");
            personalDatabase.setEndDate("02/01/24");
            personalDatabase.addUserReview("Great book!");

        });
    }

}
