import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import javax.swing.*;

public class AddIncomePanel extends JPanel {

    public AddIncomePanel(int userId, JFrame frame, DashboardPanel dashboard) {
        setLayout(new GridBagLayout());
        setBackground(new Color(245, 247, 251));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title
        JLabel title = new JLabel("Add Income");
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(new Color(44, 123, 229));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        add(title, gbc);

        gbc.gridwidth = 1;

        // Income Source
        gbc.gridy++;
        add(new JLabel("Income Source:"), gbc);
        JTextField sourceField = new JTextField();
        gbc.gridx = 1;
        add(sourceField, gbc);

        // Amount
        gbc.gridx = 0;
        gbc.gridy++;
        add(new JLabel("Amount:"), gbc);
        JTextField amountField = new JTextField();
        gbc.gridx = 1;
        add(amountField, gbc);

        // Save Button
        JButton saveBtn = new JButton("💾 Save Income");
        saveBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        saveBtn.setBackground(new Color(44, 123, 229));
        saveBtn.setForeground(Color.WHITE);
        saveBtn.setFocusPainted(false);
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        add(saveBtn, gbc);

        // Back Button
        JButton backBtn = new JButton("⬅ Back to Dashboard");
        backBtn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridy++;
        add(backBtn, gbc);

        // Actions
        saveBtn.addActionListener(e -> {
            String source = sourceField.getText().trim();
            String amountText = amountField.getText().trim();

            if (source.isEmpty() || amountText.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Please fill all fields");
                return;
            }

            try (Connection con = DBConnection.getConnection()) {
                double amount = Double.parseDouble(amountText);

                PreparedStatement ps = con.prepareStatement(
                        "INSERT INTO income (user_id, title, amount) VALUES (?, ?, ?)");
                ps.setInt(1, userId);
                ps.setString(2, source);
                ps.setDouble(3, amount);
                ps.executeUpdate();

                JOptionPane.showMessageDialog(frame, "Income Added Successfully!");
                sourceField.setText("");
                amountField.setText("");

                // Refresh dashboard table
                dashboard.loadTransactions();

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Amount must be a number");
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(frame, "Database Error: " + ex.getMessage());
            }
        });

        backBtn.addActionListener(e -> {
            frame.setContentPane(dashboard);
            frame.revalidate();
            frame.repaint();
        });
    }
}
