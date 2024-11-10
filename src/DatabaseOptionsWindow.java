package src;

import javax.swing.*;


import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

public class DatabaseOptionsWindow extends JFrame {
    
    private static String userID;
    private GeneralDatabaseWindow generalDatabaseWindow = null;
    private PersonalDatabaseWindow personalDatabaseWindow = null;
    private ResourceBundle databases;


    public DatabaseOptionsWindow(String userID, boolean isAdmin, Locale locale) {
        databases = ResourceBundle.getBundle("src.Databases", locale,
                new ResourceBundle.Control() {
                    @Override
                    public ResourceBundle newBundle(String baseName, Locale locale, String format,
                                                    ClassLoader loader, boolean reload)
                            throws IllegalAccessException, InstantiationException, IOException {
                        // The below code is copied from the default implementation
                        String bundleName = toBundleName(baseName, locale);
                        String resourceName = toResourceName(bundleName, "properties");
                        try (InputStream stream = loader.getResourceAsStream(resourceName);
                             InputStreamReader reader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {
                            return new PropertyResourceBundle(reader);
                        }
                    }
                });

        DatabaseOptionsWindow.userID = userID;
        setTitle("Database Options");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 650);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout());
        JLabel logoutLabel = new JLabel(databases.getString("LOG_OUT"));
        logoutLabel.setForeground(Color.BLUE);
        logoutLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        logoutLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                dispose();
                new LoginWindow(locale).setVisible(true);
            }
        });
        Font logoutFont = logoutLabel.getFont().deriveFont(Font.BOLD, 16f);
        logoutLabel.setFont(logoutFont);
        JPanel logoutPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        logoutPanel.add(logoutLabel);
        mainPanel.add(logoutPanel, BorderLayout.NORTH);



        JPanel centerPanel = new JPanel(new BorderLayout());


        JPanel welcomePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel titleLabel = new JLabel(databases.getString("GREETING"));
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        Font titleFont = titleLabel.getFont().deriveFont(Font.BOLD, 28f);
        titleLabel.setFont(titleFont);
        welcomePanel.add(titleLabel); // Add the label to the welcomePanel
        centerPanel.add(welcomePanel, BorderLayout.NORTH); // Add the welcome

        JLabel imageLabel = new JLabel(new ImageIcon("media\\Library-3.png"));
        imageLabel.setHorizontalAlignment(JLabel.CENTER);
        centerPanel.add(imageLabel, BorderLayout.CENTER);

        mainPanel.add(centerPanel, BorderLayout.CENTER); // Add the center panel to the main panel



        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        JButton generalDatabaseButton = new JButton(databases.getString("GENERAL_DATABASE_OPTION"));
        generalDatabaseButton.setPreferredSize(new Dimension(250, 60));
        generalDatabaseButton.addActionListener(e -> {

            if (personalDatabaseWindow != null) {
                personalDatabaseWindow.dispose();
                personalDatabaseWindow = null;
            }

            if (generalDatabaseWindow == null) {
                generalDatabaseWindow = new GeneralDatabaseWindow(isAdmin,locale);
                generalDatabaseWindow.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosing(WindowEvent e) {
                        generalDatabaseWindow = null;
                    }
                });
                generalDatabaseWindow.setVisible(true);
            }

        });

        buttonPanel.add(generalDatabaseButton);

        if (!isAdmin) {

            JButton personalDatabaseButton = new JButton(databases.getString("PERSONAL_DATABASE_OPTION"));
            personalDatabaseButton.setPreferredSize(new Dimension(250, 60));
            personalDatabaseButton.addActionListener(e -> {

                if (generalDatabaseWindow != null) {
                    generalDatabaseWindow.dispose();
                    generalDatabaseWindow = null;
                }

                if (personalDatabaseWindow == null) {
                    try {
                        System.out.println("Opening Personal Database");
                        PersonalDatabase personalDatabase = new PersonalDatabase( new Book("Sample Title", "Sample Author"));
                        personalDatabase.loadData(DatabaseOptionsWindow.getUserID());
                        personalDatabaseWindow = new PersonalDatabaseWindow(personalDatabase, DatabaseOptionsWindow.getUserID(),locale);
                        personalDatabaseWindow.addWindowListener(new WindowAdapter() {
                            @Override
                            public void windowClosing(WindowEvent e) {
                                personalDatabaseWindow = null;
                            }
                        });
                        personalDatabaseWindow.setVisible(true);
                    } catch (Exception ex) {
                        System.err.println("Error opening Personal Database Window:");
                        ex.printStackTrace();
                    }
                }
            });

            buttonPanel.add(personalDatabaseButton);
        }

      
        mainPanel.add(buttonPanel, BorderLayout.SOUTH); // Add the button panel to the bottom

        add(mainPanel); // Add the main panel to the frame
    }

    public static String getUserID() {
        return userID;
    }

    public static void setUserID(String userID) {
        DatabaseOptionsWindow.userID = userID;
    }

    public static void main(String[] args) {
    }
}