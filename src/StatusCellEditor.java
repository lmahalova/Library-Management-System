package src;

import javax.swing.*;
import java.awt.event.ItemEvent;

class StatusCellEditor extends DefaultCellEditor {
    private JComboBox<String> comboBox;

    public StatusCellEditor(PersonalDatabase personalDatabase, JTable table,String userID) {
        super(new JComboBox<String>(new String[] { "Not Started", "Ongoing", "Completed" }));
        comboBox = (JComboBox<String>) getComponent();
        comboBox.setEditable(false);
        comboBox.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                // Get the row and column of the changed status
                int row = table.getSelectedRow();
                int column = table.getSelectedColumn();

                // Ensure the row and column are valid
                if (row != -1 && column != -1) {
                    String status = (String) comboBox.getSelectedItem();
                    System.err.println(userID);
                    personalDatabase.saveOneField(userID, row, column, status);
                    System.out.println("Changed status to: " + status + " in row: " + row + ", column: " + column);

                    // Now you can call the saveOneField method to update the status in the CSV file
                    personalDatabase.saveOneField(userID, row, column, status);
                }
            }
        });
    }
}