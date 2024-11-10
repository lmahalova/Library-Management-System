package src;

import javax.swing.*;
import java.awt.*;
import java.util.Locale;

public class LanguageSelectionWindow extends JFrame {

    public LanguageSelectionWindow() {
        setTitle("Language Selection");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 200);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.CENTER;

        JLabel languageLabel = new JLabel("Languages");
        languageLabel.setFont(new Font(languageLabel.getFont().getName(), Font.PLAIN, 20)); 
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 3; 
        gbc.anchor = GridBagConstraints.CENTER; 
        panel.add(languageLabel, gbc);

        JButton azerbaijaniButton = new JButton("Azerbaijani");
        azerbaijaniButton.addActionListener(e -> selectLanguage(new Locale("az", "AZ")));
        azerbaijaniButton.setPreferredSize(new Dimension(150, 60)); 
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1; 
        panel.add(azerbaijaniButton, gbc);

        JButton englishButton = new JButton("English");
        englishButton.addActionListener(e -> selectLanguage(new Locale("en", "US")));
        englishButton.setPreferredSize(new Dimension(150, 60)); 
        gbc.gridx = 2;
        gbc.gridy = 1;
        panel.add(englishButton, gbc);

        add(panel, BorderLayout.CENTER);
        setVisible(true);
    }

    private void selectLanguage(Locale locale) {
        dispose();
        SwingUtilities.invokeLater(() -> new LoginWindow(locale));
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(LanguageSelectionWindow::new);
    }
}
