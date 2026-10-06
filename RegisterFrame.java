import java.awt.*;
import java.sql.*;
import javax.swing.*;

public class RegisterFrame extends JFrame {

    JTextField userField;
    JPasswordField passField;

    public RegisterFrame() {
        setTitle("Budget Manager - Register");
        setSize(400, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setBackground(Color.WHITE);
        panel.setLayout(new GridLayout(5, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        JLabel title = new JLabel("Register", JLabel.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));

        userField = new JTextField();
        passField = new JPasswordField();

        JButton registerBtn = new JButton("Register");
        registerBtn.setBackground(new Color(44, 123, 229));
        registerBtn.setForeground(Color.WHITE);
        registerBtn.setFocusPainted(false);
        registerBtn.addActionListener(e -> register());

        panel.add(title);
        panel.add(userField);
        panel.add(passField);
        panel.add(registerBtn);

        add(panel);
        setVisible(true);
    }

    void register() {
        String username = userField.getText().trim();
        String password = new String(passField.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter all fields", "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try (Connection con = DBConnection.getConnection()) {

            if (con == null) {
                JOptionPane.showMessageDialog(this, "Cannot connect to database", "Database Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Check if username already exists
            PreparedStatement check = con.prepareStatement("SELECT * FROM users WHERE username=?");
            check.setString(1, username);
            ResultSet rs = check.executeQuery();

            if (rs.next()) {
                JOptionPane.showMessageDialog(this, "Username already taken", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                PreparedStatement ps = con.prepareStatement(
                        "INSERT INTO users (username, password) VALUES (?, ?)"
                );
                ps.setString(1, username);
                ps.setString(2, password);
                ps.executeUpdate();

                JOptionPane.showMessageDialog(this, "Registration Successful! You can now login.", "Success", JOptionPane.INFORMATION_MESSAGE);
                dispose(); // close registration window
            }

        } catch (SQLException ex) {
            // Show exact error for debugging
            JOptionPane.showMessageDialog(this, "Database error:\n" + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Unexpected error:\n" + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
}
