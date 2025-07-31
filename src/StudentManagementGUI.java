import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.SwingUtilities;

public class StudentManagementGUI {

    public static final String URL = "jdbc:mysql://localhost:3306/my_app_db";
    public static final String USER = "root";
    public static final String PASSWORD = "kartikey@1506";

    public static void main(String[] args) {
        try {
            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Connected to MySQL database!");

            // Starting of the Graphical User Interface (GUI)
            SwingUtilities.invokeLater(() -> {
                LoginGUI loginGUI = new LoginGUI(conn);
                loginGUI.setVisible(true);
            });

        } catch (SQLException e) {
        }
    }
}
