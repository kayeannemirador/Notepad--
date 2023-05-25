package my.texteditor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class LoginFrame extends JFrame implements ActionListener {
    private JLabel usernameLabel, passwordLabel;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    
    public LoginFrame() {
        setTitle("Login Page");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setResizable(false);
        
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                // Do nothing to prevent closing the login frame
            }
        });
        
        usernameLabel = new JLabel("Username:");
        passwordLabel = new JLabel("Password:");
        
        usernameField = new JTextField(20);
        passwordField = new JPasswordField(20);
        
        loginButton = new JButton("Login");
        loginButton.addActionListener(this);
        
        JPanel panel = new JPanel(new GridLayout(3, 2));
        panel.add(usernameLabel);
        panel.add(usernameField);
        panel.add(passwordLabel);
        panel.add(passwordField);
        panel.add(loginButton);
        
        add(panel);
        pack();
        setLocationRelativeTo(null);
    }
    
    public void actionPerformed(ActionEvent e) {
        String username = usernameField.getText();
        char[] password = passwordField.getPassword();
        
        if (username.equals("admin") && String.valueOf(password).equals("admin")) {
            JOptionPane.showMessageDialog(this, "Login successful!");
            dispose(); 
             new TextEditorUI().setVisible(true);

        } else {
            JOptionPane.showMessageDialog(this, "Invalid username or password. Please try again.");
        }
    }
    
}
