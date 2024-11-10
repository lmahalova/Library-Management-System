package src;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.io.IOException;

public class TableClickHandler {
    private final JTable table;

    public TableClickHandler(JTable table) {
        this.table = table;
    }

    public void handleTableClick(MouseEvent e, int row, int column, String[] reviewers) {
        Rectangle cellRect = table.getCellRect(row, column, true);
        int clickX = e.getX() - cellRect.x;

        int currentPosition = 0;
        for (String reviewer : reviewers) {
            int reviewerWidth = table.getFontMetrics(table.getFont()).stringWidth(reviewer);
            if (clickX >= currentPosition && clickX <= currentPosition + reviewerWidth) {
                String title = (String) table.getValueAt(row, 0);
                String author = (String) table.getValueAt(row, 1);
                try {
                    openUserDetailsWindow(reviewer, title, author);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                break;
            }
            currentPosition += reviewerWidth + 2; // Account for the comma and space
        }
    }

    private void openUserDetailsWindow(String reviewer, String title, String author) throws IOException {
        // Assuming you have a UserDetailsWindow class to display user details
        UserDetailsWindow userDetailsWindow = new UserDetailsWindow(reviewer, title, author);
        userDetailsWindow.setVisible(true);
    }
}
