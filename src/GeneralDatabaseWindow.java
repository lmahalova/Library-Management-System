package src;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableRowSorter;
import java.awt.event.MouseEvent;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.io.*;
import java.util.*;
import java.util.List;

public class GeneralDatabaseWindow extends JFrame {

    private JPanel panel;
    private JTable table;
    private JTextField searchField; // Search text field
    private DefaultTableModel tableModel; // The table model for the JTable
    private TableRowSorter<DefaultTableModel> sorter; // Table row sorter for sorting and filtering
    private boolean isAdmin; // Flag to check if user is admin
    private AdminFunctionalities adminFunctionalities;
    private Map<Integer, SortOrder> sortOrderMap = new HashMap<>();
    private List<Integer> sortSequence = new ArrayList<>();

    private ResourceBundle databases;
    private ResourceBundle messagesDatabases;

    public GeneralDatabaseWindow(boolean isAdmin,Locale locale) {
        databases = ResourceBundleLoader.loadResourceBundle("src.databases", locale);
        messagesDatabases = ResourceBundleLoader.loadResourceBundle("src.messagesDatabases", locale);
        this.isAdmin = isAdmin;
        setTitle("General Database");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        setResizable(false);

        panel = new JPanel(new BorderLayout());

        tableModel = new DefaultTableModel(getData(), getColumnNames());
        adminFunctionalities = new AdminFunctionalities(tableModel);
        setupTable();
        setupTableSorting(); // Setup sorting for table columns
        setupSearchBar(); // Setup search bar for filtering
        setupScrollPane();

        setupAddButton(locale);

        if (!isAdmin) {
            setupAddButton(locale);
        }

        else {
            setupAdminButtons();
        }

        add(panel);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            saveDataToCSV();
        }));
    }

    
    private void setupScrollPane() {
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER); // Add the scroll pane to the main panel
    }

    private void setupTable() {
        table = new JTable(tableModel) {
            @Override
            public boolean isCellEditable(int row, int column) {

                return isAdmin;
            }
        };
        sorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(sorter);

        // Set up sorting on table header clicks
        JTableHeader tableHeader = table.getTableHeader();
        tableHeader.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int column = table.columnAtPoint(e.getPoint()); // Determine which column was clicked
                toggleSortOrder(column); // Toggle sorting order based on clicks
            }
        });

        // Set up additional table interactions
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                int column = table.columnAtPoint(e.getPoint());

                if (column == 3) { // Assuming "Reviews" is the fourth column
                    String reviewersList = (String) table.getValueAt(row, column);
                    String[] reviewers = reviewersList.split(", "); // Split the reviewers by comma and space
                    TableClickHandler tableClickHandler = new TableClickHandler(table);
                    tableClickHandler.handleTableClick(e, row, column, reviewers); // Handle clicks on reviewers
                }
            }
        });
    }

    private void setupTableSorting() {
        JTableHeader tableHeader = table.getTableHeader();
        tableHeader.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int column = table.columnAtPoint(e.getPoint());
                toggleSortOrder(column);
            }
        });
    }

    private void toggleSortOrder(int column) {
        SortOrder currentOrder = sortOrderMap.getOrDefault(column, SortOrder.UNSORTED);

        // Toggle sorting order
        switch (currentOrder) {
            case UNSORTED:
                sortOrderMap.put(column, SortOrder.DESCENDING);
                sortSequence.add(column); // Keep track of order of clicks
                break;
            case DESCENDING:
                sortOrderMap.put(column, SortOrder.ASCENDING);
                break;
            case ASCENDING:
                sortOrderMap.remove(column);
                sortSequence.remove((Integer) column); // Remove from sequence if removed from sorting
                break;
        }

        // Build the list of sort keys in the correct order based on click sequence
        List<RowSorter.SortKey> sortKeys = new ArrayList<>();
        for (int colIndex : sortSequence) { // Use the sequence list to maintain precedence
            if (sortOrderMap.containsKey(colIndex)) { // Ensure column is still in sortOrderMap
                sortKeys.add(new RowSorter.SortKey(colIndex, sortOrderMap.get(colIndex)));
            }
        }

        sorter.setSortKeys(sortKeys); // Apply the updated list of sort keys
    }


    private void setupSearchBar() {
        JPanel searchPanel = new JPanel(new FlowLayout()); // Panel for the search bar
        searchField = new JTextField(20); // Search text field

        // Trigger search when a key is released
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                performSearch(); // Perform search on key release
            }
        });

        searchPanel.add(new JLabel(databases.getString("SEARCH"))); // Label for search bar
        searchPanel.add(searchField); // Add the search field to the panel
        panel.add(searchPanel, BorderLayout.NORTH); // Add search panel to the main panel
    }

    private void performSearch() {
        String query = searchField.getText().toLowerCase(); // Get the search query in lowercase

        RowFilter<DefaultTableModel, Integer> rowFilter = new RowFilter<DefaultTableModel, Integer>() {
            @Override
            public boolean include(RowFilter.Entry<? extends DefaultTableModel, ? extends Integer> entry) {
                for (int i = 0; i < entry.getModel().getColumnCount(); i++) {
                    String cellValue = entry.getStringValue(i).toLowerCase(); // Convert cell value to lowercase
                    if (cellValue.contains(query)) {
                        return true; // Include if any column matches the query
                    }
                }
                return false; // Exclude otherwise
            }
        };

        sorter.setRowFilter(rowFilter); // Apply row filter to the sorter
    }

    private Object[][] getData() {
        GeneralDatabase generalDatabase = new GeneralDatabase(false);
        try {
            generalDatabase.loadFromCSV("data/general_database.csv");
            Object[][] data = generalDatabase.getBooksAsArray();
            if (data.length > 0) {
                System.out.println("Data loaded successfully.");
            } else {
                System.out.println("No data found.");
            }
            return data;
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Failed to load data.");
        }
        return new Object[0][];
    }

    private String[] getColumnNames() {
        Font font = new Font("Arial Unicode MS", Font.PLAIN, 12);
        UIManager.put("TableHeader.font", font);
        return new String[] { "Title", "Author", "Rating", "Reviews" };
    }

    private void setupAddButton(Locale locale) {
        JButton addButton = new JButton(databases.getString("ADD_TO_PERSONAL_DATABASE"));
        addButton.addActionListener(e -> addBookToPersonalDatabase(locale));
        panel.add(addButton, BorderLayout.SOUTH);
    }

    private void setupAdminButtons() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));     

        buttonPanel.add(createButton(databases.getString("ADD_BOOK"), e -> adminFunctionalities.add(new Book(getTitle(), getName()))));
        buttonPanel.add(createButton(databases.getString("UPDATE_BOOK"), e -> updateBook()));
        buttonPanel.add(createButton(databases.getString("DELETE_BOOK"), e -> deleteBook()));
        buttonPanel.add(createButton(databases.getString("REMOVE_REVIEW"), e -> removeReview()));
        buttonPanel.add(createButton(databases.getString("DELETE_USER"),  createDeleteUserActionListener()));

        panel.add(buttonPanel, BorderLayout.SOUTH);
    }

    private JButton createButton(String label, ActionListener actionListener) {
        JButton button = new JButton(label);
        button.addActionListener(actionListener);
        return button;
    }

    private void updateBook() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            String currentTitle = (String) tableModel.getValueAt(selectedRow, 0);
            String currentAuthor = (String) tableModel.getValueAt(selectedRow, 1);

            Book updatedBook = adminFunctionalities.showUpdateBookDialog(currentTitle, currentAuthor);
            if (updatedBook != null) {
                adminFunctionalities.update(selectedRow, updatedBook);
            }
        } else {
            JOptionPane.showMessageDialog(
                this,
                    messagesDatabases.getString("BOOK_UPDATE"),
                "No Book Selected",
                JOptionPane.WARNING_MESSAGE
            );
        }
    }

    private void deleteBook() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            adminFunctionalities.delete(selectedRow);
        } else {
            JOptionPane.showMessageDialog(
                this,
                    messagesDatabases.getString("BOOK_DELETE"),
                "No Book Selected",
                JOptionPane.WARNING_MESSAGE
            );
        }
    }

    private void removeReview() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            String reviews = (String) tableModel.getValueAt(selectedRow, 3);
            if (reviews != null && !reviews.isEmpty()) {
                String[] reviewList = reviews.split(", ");
                String reviewToRemove = (String) JOptionPane.showInputDialog(
                    this,
                        messagesDatabases.getString("REVIEW_REMOVE"),
                    "Remove Review",
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    reviewList,
                    reviewList[0]
                );

                if (reviewToRemove != null) {
                    adminFunctionalities.removeReview(selectedRow, reviewToRemove);
                }
            } else {
                JOptionPane.showMessageDialog(
                    this,
                    messagesDatabases.getString("NO_REVIEW"),
                    "No Reviews",
                    JOptionPane.WARNING_MESSAGE
                );
            }
        }
    }

    private ActionListener createDeleteUserActionListener() {
        return e -> {
            List<String> usernames = getAllUsernames();
            String usernameToDelete = (String) JOptionPane.showInputDialog(
                this,
                messagesDatabases.getString("USER_DELETE"),
                "Delete User",
                JOptionPane.QUESTION_MESSAGE,
                null,
                usernames.toArray(),
                usernames.get(0)
            );

            if (usernameToDelete != null) {
                adminFunctionalities.deleteUser(usernameToDelete);
            }
        };
    }

    private void addBookToPersonalDatabase(Locale locale) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow != -1) {
            String title = (String) table.getValueAt(selectedRow, 0);
            String author = (String) table.getValueAt(selectedRow, 1);
            Book book = new Book(title, author);

            // Create an instance of PersonalDatabase
            PersonalDatabase personalDatabase = new PersonalDatabase(book);

            // Pass personalDatabase to PersonalDatabaseWindow constructor
            personalDatabase.saveData(DatabaseOptionsWindow.getUserID(),book);
            personalDatabase.loadData(DatabaseOptionsWindow.getUserID());
            PersonalDatabaseWindow personalDatabaseWindow = new PersonalDatabaseWindow(personalDatabase,
                    DatabaseOptionsWindow.getUserID(),locale);

            // Show the window
            personalDatabaseWindow.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, messagesDatabases.getString("SELECT_BOOK"));
        }
    }

    private void saveDataToCSV() {
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
            JOptionPane.showMessageDialog(this, messagesDatabases.getString("FAILED_CSV_SAVE"));
        }
    }

    // Helper method to get all usernames from user_data.csv
    private List<String> getAllUsernames() {
        List<String> usernames = new ArrayList<>();
        String userDataPath = "data/user_data.csv"; // Path to user_data.csv

        try (BufferedReader reader = new BufferedReader(new FileReader(userDataPath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(","); // Split CSV into fields
                if (data.length > 2) {
                    usernames.add(data[0]); // Assuming 1st column is the username
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            System.out.println("Error reading user data."); // Log error
        }

        return usernames; // Return the list of usernames
    }

    public static void main(String[] args) {

    }

}
