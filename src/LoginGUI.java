import javax.swing.*;      // Import Swing components for GUI
import java.awt.*;         // Import AWT for layout managers and more
import java.sql.*;         // Import SQL classes for database connection and queries

public class LoginGUI extends JFrame {   // Define LoginGUI class, which is a JFrame (window)

    // Constructor that takes a database Connection object
    public LoginGUI(Connection conn) {

        setTitle("Login");                         // Set window title
        setSize(400, 180);                         // Set window size (width x height)
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  // Close app when window is closed
        setLocationRelativeTo(null);               // Center window on screen

        // Create a JPanel with GridLayout: 3 rows, 2 columns, 10px horizontal and vertical gaps
        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));

        // Create label and text field for username input
        JLabel userLabel = new JLabel("Username:");
        JTextField userField = new JTextField();

        // Create label and password field for password input
        JLabel passLabel = new JLabel("Password:");
        JPasswordField passField = new JPasswordField();

        // Create login button
        JButton loginButton = new JButton("Login");

        // Add padding around the panel content (top, left, bottom, right)
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Add components to the panel in order (GridLayout controls positioning)
        panel.add(userLabel);       // Row 1, Col 1
        panel.add(userField);       // Row 1, Col 2
        panel.add(passLabel);       // Row 2, Col 1
        panel.add(passField);       // Row 2, Col 2

        // Add empty label to fill the GridLayout cell (Row 3, Col 1)
        panel.add(new JLabel());    

        panel.add(loginButton);     // Row 3, Col 2

        add(panel);                 // Add the panel to the JFrame (window)

        // Add action listener to login button to handle clicks
        loginButton.addActionListener(e -> {
            String username = userField.getText();                     // Get entered username
            String password = new String(passField.getPassword());     // Get entered password securely

            try {
                // Prepare SQL query to find user with matching username and password
                String sql = "SELECT role FROM users WHERE username = ? AND password = ?";
                PreparedStatement stmt = conn.prepareStatement(sql);

                // Set parameters safely to prevent SQL injection attacks
                stmt.setString(1, username);
                stmt.setString(2, password);

                // Execute query and get result
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {  // If a matching user is found
                    String role = rs.getString("role");  // Get the role of the user from database

                    // Show success message with the user's role
                    JOptionPane.showMessageDialog(this, "Login successful! Role: " + role);

                    dispose();  // Close the login window

                    // Open main menu window and make it visible, passing the same database connection
                    new MainMenuGUI(conn).setVisible(true);

                } else {
                    // If no user matches, show error message
                    JOptionPane.showMessageDialog(this, "Invalid credentials.", "Login Failed", JOptionPane.ERROR_MESSAGE);
                }

            } catch (SQLException ex) {
                // You can add error handling here if needed (currently empty)
            }
        });
    }
}
