import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class LoginGUI extends JFrame {


    public LoginGUI(Connection conn) {

        setTitle("Login");
        setSize(400, 180);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        JLabel userLabel = new JLabel("Username:");
        JTextField userField = new JTextField();
        JLabel passLabel = new JLabel("Password:");
        JPasswordField passField = new JPasswordField();
        JButton loginButton = new JButton("Login");

        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        panel.add(userLabel);
        panel.add(userField);
        panel.add(passLabel);
        panel.add(passField);
        panel.add(new JLabel()); 
        panel.add(loginButton);

        add(panel);

        loginButton.addActionListener(e -> {
            String username = userField.getText();
            String password = new String(passField.getPassword());

            try {
                String sql = "SELECT role FROM users WHERE username = ? AND password = ?";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, username);
                stmt.setString(2, password);

                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    String role = rs.getString("role");
                    JOptionPane.showMessageDialog(this, "Login successful! Role: " + role);
                    dispose(); 
                    new MainMenuGUI(conn).setVisible(true); // launch main menu
                } else {
                    JOptionPane.showMessageDialog(this, "Invalid credentials.", "Login Failed", JOptionPane.ERROR_MESSAGE);
                }

            } catch (SQLException ex) {
            }
        });
    }
}
