package src;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.Locale;
import java.util.ResourceBundle;
import javax.swing.*;
import javax.swing.table.TableCellEditor;

public class ReviewCellEditor extends AbstractCellEditor implements TableCellEditor {

    private final JTextField textField;
    private final JButton addButton;
    private PersonalDatabase personalDatabase;
    private ResourceBundle databases;
    private ResourceBundle messagesDatabases;

    public ReviewCellEditor(PersonalDatabase personalDatabase, Locale locale) {
        databases = ResourceBundleLoader.loadResourceBundle("src.Databases", locale);
        messagesDatabases = ResourceBundleLoader.loadResourceBundle("src.messagesDatabases", locale);
        this.personalDatabase = personalDatabase;
        textField = new JTextField();
        textField.setPreferredSize(new Dimension(90, 15)); // Adjust size as needed


        addButton = new JButton(databases.getString("ADD_REVIEW"));
        addButton.setPreferredSize(new Dimension(100, 20));
        addButton.addActionListener(e -> fireEditingStopped()); // Stop editing on button click
    }


    public Object getCellEditorValue() {
        String reviewText = textField.getText().trim(); // Retrieve the trimmed text from the text field
        if (reviewText.isEmpty()) {
            // Return a default value if the review is empty
            return "Add review";
        }
        return reviewText; // Return the non-empty review text
    }


    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        textField.setText(""); // Clear the text to start fresh


        // If it's a new value or "Add Rating", prompt the user for input
        String reviewString = JOptionPane.showInputDialog(null, messagesDatabases.getString("ENTER_REVIEW"));
        if (reviewString != null && !reviewString.isEmpty()) {
            try {


                textField.setText(reviewString); // Set valid input

                personalDatabase.saveOneField(DatabaseOptionsWindow.getUserID(), row, column, reviewString);


            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, messagesDatabases.getString("INVALID_REVIEW"));
            }
        } else {
            reviewString = "Add review";
            personalDatabase.saveOneField(DatabaseOptionsWindow.getUserID(), row, column, reviewString);
        }


        // Create a panel with centered layout and proper spacing
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        panel.add(textField);
        panel.add(addButton);

        return panel;
    }

}