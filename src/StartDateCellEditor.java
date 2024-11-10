package src;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.ResourceBundle;

public class StartDateCellEditor extends AbstractCellEditor implements TableCellEditor {

    private SimpleDateFormat dateFormat = new SimpleDateFormat("mm/dd/yy");
    private PersonalDatabase personalDatabase;
    private JTextField textField;
    private boolean isNewValue;
    private ResourceBundle messagesDatabases;

    public StartDateCellEditor(PersonalDatabase personalDatabase, Locale locale) {
        messagesDatabases = ResourceBundleLoader.loadResourceBundle("src.messagesDatabases", locale);
        this.personalDatabase = personalDatabase;
        textField = new JTextField();
        textField.setHorizontalAlignment(JTextField.CENTER);
        textField.setPreferredSize(new Dimension(90, 15)); // Adjust size as needed
    }

    @Override
    public Object getCellEditorValue() {
        try {
            if (isNewValue) {
                return "Add date";
            }

            Date date = dateFormat.parse(textField.getText());
            return dateFormat.format(date);
        } catch (ParseException ex) {
            JOptionPane.showMessageDialog(null, messagesDatabases.getString("INVALID_DATE"));
            return "Add date";
        }
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {

        textField.setText("");

        // If it's a new value or "Add date", prompt the user for input
        String dateString = JOptionPane.showInputDialog(null, messagesDatabases.getString("ENTER_START_DATE"));
        if (dateString != null && !dateString.isEmpty()) {
            try {
                Date date = dateFormat.parse(dateString);
                if (this instanceof StartDateCellEditor) {
                    // Check if start date is after end date
                    String endDate = personalDatabase.getTheDate(DatabaseOptionsWindow.getUserID(), row, column + 2);
                    if (!endDate.isEmpty()) {
                        Date end = dateFormat.parse(endDate);
                        if (date.after(end)) {
                            JOptionPane.showMessageDialog(null, messagesDatabases.getString("DATE_ORDER"));
                            return null;
                        }
                    }
                }
                textField.setText(dateFormat.format(date)); // Set valid input

                personalDatabase.saveOneField(DatabaseOptionsWindow.getUserID(), row, column, dateString);

            } catch (ParseException ex) {
                JOptionPane.showMessageDialog(null, messagesDatabases.getString("INVALID_DATE"));
            }
        }

        return textField;
    }

}
