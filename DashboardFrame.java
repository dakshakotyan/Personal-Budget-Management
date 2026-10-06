import javax.swing.*;

public class DashboardFrame extends JFrame {

    public DashboardFrame(int userId) {
        setTitle("Dashboard - Budget Manager");
        setSize(800, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Set the content pane to the DashboardPanel
        setContentPane(new DashboardPanel(userId, this));

        setVisible(true);
    }
}
