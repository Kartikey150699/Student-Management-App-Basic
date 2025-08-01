import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.SwingUtilities;

public class StudentManagementGUI {

    // Database connection details
    public static final String URL = "jdbc:mysql://localhost:3306/my_app_db";
    public static final String USER = "root";
    public static final String PASSWORD = "kartikey@1506";

    public static void main(String[] args) {
        try {
            // Establish connection to MySQL database
            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Connected to MySQL database!");

            // Start GUI on the Event Dispatch Thread
            SwingUtilities.invokeLater(() -> {
                LoginGUI loginGUI = new LoginGUI(conn);
                loginGUI.setVisible(true);
            });

        } catch (SQLException e) {
            // Handle exception silently (you might want to log this)
        }
    }
}
