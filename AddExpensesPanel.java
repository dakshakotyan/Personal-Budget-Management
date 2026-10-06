import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import javax.swing.*;

public class AddExpensesPanel extends JPanel {

    public AddExpensesPanel(int userId, JFrame frame, DashboardPanel dashboard) {
        setLayout(new GridBagLayout());
        setBackground(new Color(245, 247, 251));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title
        JLabel title = new JLabel("Add Expense");
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(new Color(220, 53, 69));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        add(title, gbc);

        gbc.gridwidth = 1;

        // Expense Title
        gbc.gridy++;
        add(new JLabel("Expense Title:"), gbc);
        JTextField titleField = new JTextField();
        gbc.gridx = 1;
        add(titleField, gbc);

        // Amount
        gbc.gridx = 0;
        gbc.gridy++;
        add(new JLabel("Amount:"), gbc);
        JTextField amountField = new JTextField();
        gbc.gridx = 1;
        add(amountField, gbc);

        // Save Button
        JButton saveBtn = new JButton("💾 Save Expense");
        saveBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        saveBtn.setBackground(new Color(220, 53, 69));
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
            String titleText = titleField.getText().trim();
            String amountText = amountField.getText().trim();

            if (titleText.isEmpty() || amountText.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Please fill all fields");
                return;
            }

            try (Connection con = DBConnection.getConnection()) {
                double amount = Double.parseDouble(amountText);

                PreparedStatement ps = con.prepareStatement(
                        "INSERT INTO expenses (user_id, title, amount) VALUES (?, ?, ?)");
                ps.setInt(1, userId);
                ps.setString(2, titleText);
                ps.setDouble(3, amount);
                ps.executeUpdate();

                JOptionPane.showMessageDialog(frame, "Expense Added Successfully!");
                titleField.setText("");
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
