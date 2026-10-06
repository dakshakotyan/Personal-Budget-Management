import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class DashboardPanel extends JPanel {

    private DefaultTableModel tableModel;
    private int userId;
    private JFrame frame;

    public DashboardPanel(int userId, JFrame frame) {
        this.userId = userId;
        this.frame = frame;

        setLayout(new BorderLayout());
        setBackground(new Color(245, 247, 251));

        // --- Top title ---
        JLabel title = new JLabel("Welcome to Budget Manager", JLabel.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(new Color(44, 123, 229));
        title.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        add(title, BorderLayout.NORTH);

        // --- Table ---
        tableModel = new DefaultTableModel();
        tableModel.addColumn("ID");
        tableModel.addColumn("Type");
        tableModel.addColumn("Title");
        tableModel.addColumn("Amount");

        JTable transactionTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(transactionTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Transactions"));
        add(scrollPane, BorderLayout.CENTER);

        // Load transactions
        loadTransactions();

        // --- Bottom buttons ---
        JPanel btnPanel = new JPanel();
        btnPanel.setBackground(new Color(245, 247, 251));
        btnPanel.setLayout(new GridLayout(1, 3, 20, 0));
        btnPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        JButton addIncomeBtn = new JButton("➕ Add Income");
        JButton addExpensesBtn = new JButton("💸 Add Expenses");
        JButton viewReportBtn = new JButton("📊 View Report");

        styleButton(addIncomeBtn, new Color(44, 123, 229));
        styleButton(addExpensesBtn, new Color(220, 53, 69));
        styleButton(viewReportBtn, new Color(44, 123, 229));

        btnPanel.add(addIncomeBtn);
        btnPanel.add(addExpensesBtn);
        btnPanel.add(viewReportBtn);

        add(btnPanel, BorderLayout.SOUTH);

        // --- Button actions ---
        addIncomeBtn.addActionListener(e -> {
            frame.setContentPane(new AddIncomePanel(userId, frame, this));
            frame.revalidate();
            frame.repaint();
        });

        addExpensesBtn.addActionListener(e -> {
            frame.setContentPane(new AddExpensesPanel(userId, frame, this));
            frame.revalidate();
            frame.repaint();
        });

        viewReportBtn.addActionListener(e -> {
            frame.setContentPane(new ReportPanel(userId, frame));
            frame.revalidate();
            frame.repaint();
        });
    }

    // Style buttons
    private void styleButton(JButton btn, Color bgColor) {
        btn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btn.setBackground(bgColor);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    }

    // Load transactions
    public void loadTransactions() {
        tableModel.setRowCount(0);

        try (Connection con = DBConnection.getConnection()) {
            // Income
            PreparedStatement psIncome = con.prepareStatement(
                    "SELECT id, title, amount FROM income WHERE user_id=?");
            psIncome.setInt(1, userId);
            ResultSet rsIncome = psIncome.executeQuery();
            while (rsIncome.next()) {
                tableModel.addRow(new Object[]{
                        rsIncome.getInt("id"),
                        "Income",
                        rsIncome.getString("title"),
                        rsIncome.getDouble("amount")
                });
            }

            // Expenses
            PreparedStatement psExpense = con.prepareStatement(
                    "SELECT id, title, amount FROM expenses WHERE user_id=?");
            psExpense.setInt(1, userId);
            ResultSet rsExpense = psExpense.executeQuery();
            while (rsExpense.next()) {
                tableModel.addRow(new Object[]{
                        rsExpense.getInt("id"),
                        "Expense",
                        rsExpense.getString("title"),
                        rsExpense.getDouble("amount")
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error loading transactions: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
