package src;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Arrays;
import java.util.Locale;
import java.util.ResourceBundle;

public class LoginWindow extends JFrame {

    private final UserDataManager userDataManager = new UserDataManager();

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JCheckBox showPasswordCheckbox;
    private ResourceBundle loginSignupBundle;
    private ResourceBundle messages;

    public LoginWindow(Locale locale) {
        loginSignupBundle = ResourceBundleLoader.loadResourceBundle("src.LoginSignup", locale);
        messages = ResourceBundleLoader.loadResourceBundle("src.messagesLoginSignup", locale);
        setTitle("Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 450);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        setResizable(false);
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        JLabel usernameLabel = new JLabel(loginSignupBundle.getString("USERNAME"));
        JLabel passwordLabel = new JLabel(loginSignupBundle.getString("PASSWORD"));
        usernameField = new JTextField(15);
        passwordField = new JPasswordField(15);
        loginButton = new JButton(loginSignupBundle.getString("LOG_IN"));
        showPasswordCheckbox = new JCheckBox(loginSignupBundle.getString("PASSWORD_VISIBILITY"));

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                char[] password = passwordField.getPassword();

                if (username.isEmpty() || password.length == 0) {
                    JOptionPane.showMessageDialog(LoginWindow.this, messages.getString("USERNAME_AND_PASSWORD"));
                    return;
                }

                try {

                    if (userDataManager.isAdminChecker(username, password)) {
                        JOptionPane.showMessageDialog(LoginWindow.this,
                                messages.getString("ADMIN_LOGIN") + username);
                        showDatabaseOptions(username, password, locale);
                    } else if (userDataManager.isValidUser(username, password)) {

                        JOptionPane.showMessageDialog(LoginWindow.this, messages.getString("USER_LOGIN") + username);

                        showDatabaseOptions(username, password, locale);

                    } else {
                        JOptionPane.showMessageDialog(LoginWindow.this,
                                messages.getString("INVALID_CREDENTIALS"));
                    }
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(LoginWindow.this, messages.getString("ACCESS_ERROR"));
                    ex.printStackTrace();
                } finally {
                    Arrays.fill(password, ' ');
                }
            }
        });

        showPasswordCheckbox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (showPasswordCheckbox.isSelected()) {
                    passwordField.setEchoChar((char) 0);

                } else {
                    passwordField.setEchoChar('\u2022');
                }
            }
        });
        JLabel signUpLabel = new JLabel(loginSignupBundle.getString("SIGN_UP_LABEL"));
        signUpLabel.setForeground(Color.BLUE);
        signUpLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        signUpLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                dispose();
                SwingUtilities.invokeLater(() -> new SignupWindow(locale).setVisible(true));
            }
        });

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(usernameLabel, gbc);
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        panel.add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        panel.add(passwordLabel, gbc);
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        panel.add(passwordField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        panel.add(showPasswordCheckbox, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        loginButton.setPreferredSize(new Dimension(120, 35));
        buttonPanel.add(loginButton);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 3;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(buttonPanel, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 3;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(signUpLabel, gbc);

        add(panel);
        setVisible(true);
    }

    private void showDatabaseOptions(String username, char[] password, Locale locale) throws IOException {
        setVisible(false);
        boolean isAdmin = userDataManager.isAdminChecker(username, password);

        String userID = UserDataReader.getUserID(username, password);
        if (userDataManager.isAdminChecker(username, password)) {
            DatabaseOptionsWindow databaseOptionsPage = new DatabaseOptionsWindow(userID, isAdmin, locale);
            databaseOptionsPage.setVisible(true);
        } else {
            DatabaseOptionsWindow databaseOptionsPage = new DatabaseOptionsWindow(userID, isAdmin, locale);
            databaseOptionsPage.setVisible(true);
        }

    }

    public static void main(String[] args) {
        Locale english = new Locale("en", "US");
        Locale azerbaijani = new Locale("az", "AZ");


        // Create windows with different locales
        SwingUtilities.invokeLater(() -> new LoginWindow(english));

    }

}
