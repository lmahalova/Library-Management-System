package src;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class SignupWindow extends JFrame {

    private final UserDataManager userDataManager = new UserDataManager();

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JPasswordField reEnterPasswordField; 
    private JButton signupButton;
    private JCheckBox showPasswordCheckbox;
    private ResourceBundle loginSignupBundle;
    private ResourceBundle messages;

    public SignupWindow(Locale locale) {
        loginSignupBundle = ResourceBundleLoader.loadResourceBundle("src.LoginSignup",locale);
        messages = ResourceBundleLoader.loadResourceBundle("src.messagesLoginSignup",locale);
        setTitle("Sign Up");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 450); 
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        setResizable(false);

        
        JLabel createAccountLabel = new JLabel(loginSignupBundle.getString("SIGNUP_TITLE"));
        createAccountLabel.setHorizontalAlignment(SwingConstants.CENTER);
        createAccountLabel.setFont(new Font("Arial", Font.BOLD, 16));

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        JLabel usernameLabel = new JLabel(loginSignupBundle.getString("USERNAME"));
        JLabel passwordLabel = new JLabel(loginSignupBundle.getString("PASSWORD"));
        JLabel reEnterPasswordLabel = new JLabel(loginSignupBundle.getString("REENTER"));
        usernameField = new JTextField(15);
        passwordField = new JPasswordField(15);
        reEnterPasswordField = new JPasswordField(15);
        signupButton = new JButton(loginSignupBundle.getString("SIGN_UP"));
        showPasswordCheckbox = new JCheckBox(loginSignupBundle.getString("PASSWORD_VISIBILITY"));
        signupButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                char[] password = passwordField.getPassword();
                char[] reEnteredPassword = reEnterPasswordField.getPassword(); 

                if (username.isEmpty() || password.length == 0 || reEnteredPassword.length == 0) {
                    JOptionPane.showMessageDialog(SignupWindow.this, messages.getString("USERNAME_AND_PASSWORD"));
                    return;
                }

                List<String> usernameErrors = UserChecker.validateUsername(username,locale);
                if (!usernameErrors.isEmpty()) {
                    StringBuilder errorMessageBuilder = new StringBuilder();
                    for (String errorMessage : usernameErrors) {
                        errorMessageBuilder.append(errorMessage).append("\n");
                    }
                    JOptionPane.showMessageDialog(SignupWindow.this, errorMessageBuilder.toString());
                    return;
                }

               
                if (!Arrays.equals(password, reEnteredPassword)) {
                    JOptionPane.showMessageDialog(SignupWindow.this, messages.getString("REENTER_PASSWORD"));
                    passwordField.setText(""); 
                    reEnterPasswordField.setText(""); 
                    return;
                }

                try {
                    String passwordString = new String(password);
                    List<String> errorMessages = PasswordChecker.validate(username, passwordString, locale);
                    if (!errorMessages.isEmpty()) {
                        StringBuilder errorMessageBuilder = new StringBuilder();
                        for (String errorMessage : errorMessages) {
                            errorMessageBuilder.append(errorMessage).append("\n");
                        }
                        JOptionPane.showMessageDialog(SignupWindow.this, errorMessageBuilder.toString());
                        return;
                    }

                    if (userDataManager.isUsernameTaken(username)) {
                        JOptionPane.showMessageDialog(SignupWindow.this, messages.getString("USERNAME_DUPLICATE"));
                        return;
                    }

                    userDataManager.addUser(username, password);
                    JOptionPane.showMessageDialog(SignupWindow.this, messages.getString("USER_SIGNUP"));

                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(SignupWindow.this, messages.getString("ACCESS_ERROR"));
                    ex.printStackTrace();
                } finally {
                    Arrays.fill(password, ' ');
                    Arrays.fill(reEnteredPassword, ' '); 
                }

                
                dispose();
                SwingUtilities.invokeLater(() -> new LoginWindow(locale));
            }
        });
        showPasswordCheckbox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (showPasswordCheckbox.isSelected()) {
                    passwordField.setEchoChar((char) 0);
                    reEnterPasswordField.setEchoChar((char) 0);
                } else {
                    passwordField.setEchoChar('\u2022');
                    reEnterPasswordField.setEchoChar('\u2022');
                }
            }
        });

        gbc.gridx = 0;
        gbc.gridy = 0; 
        gbc.gridwidth = 3;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(createAccountLabel, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1; 
        gbc.gridwidth = 1;
        panel.add(usernameLabel, gbc);
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        panel.add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2; 
        gbc.gridwidth = 1;
        panel.add(passwordLabel, gbc);
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        panel.add(passwordField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3; 
        gbc.gridwidth = 1;
        panel.add(reEnterPasswordLabel, gbc); 
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        panel.add(reEnterPasswordField, gbc); 


        gbc.gridx = 0;
        gbc.gridy = 4; 
        gbc.gridwidth = 1;
        panel.add(showPasswordCheckbox, gbc);
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 3;
        gbc.anchor = GridBagConstraints.CENTER;
        JLabel haveAccountLabel = new JLabel(loginSignupBundle.getString("LOG_IN_LABEL"));
        haveAccountLabel.setForeground(Color.BLUE);
        haveAccountLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        haveAccountLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                dispose();
                SwingUtilities.invokeLater(() -> new LoginWindow(locale));
            }
        });
        panel.add(haveAccountLabel, gbc);

        
        gbc.gridx = 0;
        gbc.gridy = 6; 
        gbc.gridwidth = 3;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(signupButton, gbc);

        add(panel);
        setVisible(true);
    }

}
