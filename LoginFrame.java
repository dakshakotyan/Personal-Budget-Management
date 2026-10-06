import java.awt.*;
import java.sql.*;
import javax.swing.*;

public class LoginFrame extends JFrame {

    private JTextField userField;
    private JPasswordField passField;

    public LoginFrame() {
        setTitle("Budget Manager - Login");
        setSize(400, 350);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE); // Exit app if login closed

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(245, 247, 251));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 20, 10, 20);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel title = new JLabel("Budget Manager", JLabel.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(new Color(44, 123, 229));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panel.add(title, gbc);

        gbc.gridwidth = 1;
        gbc.gridy++;
        panel.add(new JLabel("Username:"), gbc);

        userField = new JTextField();
        gbc.gridx = 1;
        panel.add(userField, gbc);

        gbc.gridx = 0; gbc.gridy++;
        panel.add(new JLabel("Password:"), gbc);

        passField = new JPasswordField();
        gbc.gridx = 1;
        panel.add(passField, gbc);

        // Login button
        JButton loginBtn = new JButton("Login");
        styleButton(loginBtn, new Color(44, 123, 229));
        gbc.gridx = 0; gbc.gridy++; gbc.gridwidth = 2;
        panel.add(loginBtn, gbc);

        // Register button
        JButton registerBtn = new JButton("Register");
        styleButton(registerBtn, new Color(100, 100, 100));
        gbc.gridy++;
        panel.add(registerBtn, gbc);

        // Action listeners
        loginBtn.addActionListener(e -> login());
        registerBtn.addActionListener(e -> {
            new RegisterFrame().setVisible(true); // Opens RegisterFrame
        });

        add(panel);
        setVisible(true);
    }

    private void styleButton(JButton btn, Color bgColor) {
        btn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btn.setBackground(bgColor);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
    }

    private void login() {
        String username = userField.getText().trim();
        String password = new String(passField.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter username and password");
            return;
        }

        try (Connection con = DBConnection.getConnection()) {
            PreparedStatement ps = con.prepareStatement(
                "SELECT id FROM users WHERE username=? AND password=?"
            );
            ps.setString(1, username);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                int userId = rs.getInt("id");
                JOptionPane.showMessageDialog(this, "Login successful!");

                // Open dashboard and pass userId
                new DashboardFrame(userId);

                // Close login frame
                dispose();

            } else {
                JOptionPane.showMessageDialog(this, "Invalid username or password");
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(LoginFrame::new);
    }
}
