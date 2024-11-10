package src;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.Locale;
import java.util.ResourceBundle;
import javax.swing.AbstractCellEditor;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableCellEditor;

public class RatingCellEditor extends AbstractCellEditor implements TableCellEditor {

    private JTextField textField;
    private JButton saveButton;
    private boolean isNewValue;
    private PersonalDatabase personalDatabase;
    private ResourceBundle databases;
    private ResourceBundle messagesDatabases;

    public RatingCellEditor(PersonalDatabase personalDatabase,Locale locale) {
        databases = ResourceBundleLoader.loadResourceBundle("src.databases", locale);
        messagesDatabases = ResourceBundleLoader.loadResourceBundle("src.messagesDatabases", locale);
        this.personalDatabase = personalDatabase;

        textField = new JTextField();
        textField.setHorizontalAlignment(JTextField.CENTER);
        textField.setPreferredSize(new Dimension(90, 15)); // Adjust size as needed

        saveButton = new JButton(databases.getString("SAVE"));
        saveButton.addActionListener(e -> {

            fireEditingStopped();

        }); // Stop editing on button click
    }

    @Override
    public Object getCellEditorValue() {
        try {
            if (isNewValue) {
                return "Add rating"; // Returning this if the user cancels without input
            }

            int rating = Integer.parseInt(textField.getText());
            if (rating < 1 || rating > 5) {
                throw new NumberFormatException("Rating must be between 1 and 5");
            }

            return rating;
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, messagesDatabases.getString("RATING_RANGE"));
            return "Add rating"; // Reverting to the default value in case of invalid input
        }
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        // Clear the text field every time the cell is clicked
        textField.setText(""); // Clear the text to start fresh


        // If it's a new value or "Add Rating", prompt the user for input
        String ratingString = JOptionPane.showInputDialog(null, messagesDatabases.getString("ENTER_RATING"));
        if (ratingString != null && !ratingString.isEmpty()) {
            try {
                int rating = Integer.parseInt(ratingString);

                if (rating >= 1 && rating <= 5) {
                    textField.setText(ratingString); // Set valid input

                    personalDatabase.saveOneField(DatabaseOptionsWindow.getUserID(), row, column, ratingString);


                } else {
                    JOptionPane.showMessageDialog(null, messagesDatabases.getString("INVALID_RANGE"));
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, messagesDatabases.getString("INVALID_RATING"));
            }
        } else {
            ratingString = String.valueOf(-1);
            personalDatabase.saveOneField(DatabaseOptionsWindow.getUserID(), row, column, ratingString);
        }


        // Create a panel with centered layout and proper spacing
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        panel.add(textField);
        panel.add(saveButton);

        return panel; // Return the panel as the cell editor component
    }
}