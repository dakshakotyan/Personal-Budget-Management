import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.*;

public class ReportPanel extends JPanel {

    private double totalIncome = 0;
    private double totalExpenses = 0;

    public ReportPanel(int userId, JFrame frame) {

        setLayout(new BorderLayout());
        setBackground(new Color(245, 247, 251));
        setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        // Title
        JLabel title = new JLabel("Financial Report", JLabel.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(new Color(44, 123, 229));
        add(title, BorderLayout.NORTH);

        // Fetch totals from DB
        try (Connection con = DBConnection.getConnection()) {

            PreparedStatement psIncome =
                con.prepareStatement("SELECT COALESCE(SUM(amount),0) FROM income WHERE user_id=?");
            psIncome.setInt(1, userId);
            ResultSet rsIncome = psIncome.executeQuery();
            if (rsIncome.next()) totalIncome = rsIncome.getDouble(1);

            PreparedStatement psExpense =
                con.prepareStatement("SELECT COALESCE(SUM(amount),0) FROM expenses WHERE user_id=?");
            psExpense.setInt(1, userId);
            ResultSet rsExpense = psExpense.executeQuery();
            if (rsExpense.next()) totalExpenses = rsExpense.getDouble(1);

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading report");
        }

        // Chart
        PieChartPanel chartPanel = new PieChartPanel(totalIncome, totalExpenses);
        add(chartPanel, BorderLayout.CENTER);

        // Back button
        JButton backBtn = new JButton("⬅ Back");
        backBtn.setFont(new Font("Segoe UI", Font.BOLD, 18));
        backBtn.setBackground(new Color(100, 100, 100));
        backBtn.setForeground(Color.WHITE);
        backBtn.setFocusPainted(false);

        backBtn.addActionListener(e -> {
            frame.setContentPane(new DashboardPanel(userId, frame));
            frame.revalidate();
            frame.repaint();
        });

        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(new Color(245, 247, 251));
        bottomPanel.add(backBtn);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    // ================= PIE CHART =================
    static class PieChartPanel extends JPanel {

        private final double income;
        private final double expenses;

        public PieChartPanel(double income, double expenses) {
            this.income = income;
            this.expenses = expenses;
            setPreferredSize(new Dimension(420, 320));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                RenderingHints.VALUE_ANTIALIAS_ON);

            int width = getWidth();
            int height = getHeight();
            int size = Math.min(width, height) - 80;
            int x = (width - size) / 2;
            int y = (height - size) / 2;

            double total = income + expenses;

            // No data case
            if (total <= 0) {
                g2.setFont(new Font("Segoe UI", Font.BOLD, 18));
                g2.setColor(Color.GRAY);
                g2.drawString("No financial data available", x + 30, y + size / 2);
                return;
            }

            double incomeAngle = (income / total) * 360;
            double expenseAngle = (expenses / total) * 360;

            // Income slice
            g2.setColor(new Color(44, 123, 229));
            g2.fillArc(x, y, size, size, 0, (int) incomeAngle);

            // Expense slice
            g2.setColor(new Color(220, 53, 69));
            g2.fillArc(x, y, size, size, (int) incomeAngle, (int) expenseAngle);

            // Labels
            g2.setFont(new Font("Segoe UI", Font.BOLD, 16));
            g2.setColor(Color.BLACK);
            g2.drawString("Income: ₹" + income, 20, 30);
            g2.drawString("Expenses: ₹" + expenses, 20, 55);
        }
    }
}
