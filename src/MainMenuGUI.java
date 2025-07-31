import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class MainMenuGUI extends JFrame {

    private final Connection conn;

    public MainMenuGUI(Connection conn) {
        this.conn = conn;

        setTitle("Student Management System - GUI");
        setSize(800, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center the window

        JPanel panel = new JPanel(new GridLayout(0, 1, 10, 10));

        JButton addBtn = new JButton("Add Student");
        JButton viewBtn = new JButton("View Students");
        JButton updateBtn = new JButton("Update Student");
        JButton deleteBtn = new JButton("Delete Student");
        JButton searchBtn = new JButton("Search Students");
        JButton exitBtn = new JButton("Exit");

        // Add Student
        addBtn.addActionListener(e -> {
            JTextField nameField = new JTextField();
            JTextField emailField = new JTextField();
            JTextField ageField = new JTextField();

            Object[] message = {
                "Name:", nameField,
                "Email:", emailField,
                "Age:", ageField
            };

            int option = JOptionPane.showConfirmDialog(this, message, "Add Student", JOptionPane.OK_CANCEL_OPTION);
            if (option == JOptionPane.OK_OPTION) {
                String name = nameField.getText().trim();
                String email = emailField.getText().trim();
                String ageText = ageField.getText().trim();

                if (name.isEmpty() || email.isEmpty() || ageText.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "All fields are required.");
                    return;
                }

                int age;
                try {
                    age = Integer.parseInt(ageText);
                    if (age < 5 || age > 120) {
                        JOptionPane.showMessageDialog(this, "Age must be between 5 and 120.");
                        return;
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Age must be a valid number.");
                    return;
                }

                try {
                    String sql = "INSERT INTO students (name, email, age) VALUES (?, ?, ?)";
                    PreparedStatement stmt = conn.prepareStatement(sql);
                    stmt.setString(1, name);
                    stmt.setString(2, email);
                    stmt.setInt(3, age);

                    int rows = stmt.executeUpdate();
                    if (rows > 0) {
                        JOptionPane.showMessageDialog(this, "Student added successfully!");
                    } else {
                        JOptionPane.showMessageDialog(this, "Failed to add student.");
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
                }
            }
        });

        // View Students
        viewBtn.addActionListener(e -> {
            try {
                String sql = "SELECT * FROM students ORDER BY id";
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery();

                StringBuilder sb = new StringBuilder();
                while (rs.next()) {
                    sb.append("ID: ").append(rs.getInt("id"))
                      .append(", Name: ").append(rs.getString("name"))
                      .append(", Email: ").append(rs.getString("email"))
                      .append(", Age: ").append(rs.getInt("age"))
                      .append("\n");
                }

                JTextArea textArea = new JTextArea(sb.toString());
                textArea.setEditable(false);
                JScrollPane scrollPane = new JScrollPane(textArea);
                scrollPane.setPreferredSize(new Dimension(350, 200));

                JOptionPane.showMessageDialog(this, scrollPane, "All Students", JOptionPane.INFORMATION_MESSAGE);

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error fetching students: " + ex.getMessage());
            }
        });

        // Update Student
        updateBtn.addActionListener(e -> {
            String idText = JOptionPane.showInputDialog(this, "Enter Student ID to update:");
            if (idText == null) return; 
            int id;
            try {
                id = Integer.parseInt(idText);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid ID entered.");
                return;
            }

            try {
                String selectSql = "SELECT * FROM students WHERE id = ?";
                PreparedStatement selectStmt = conn.prepareStatement(selectSql);
                selectStmt.setInt(1, id);
                ResultSet rs = selectStmt.executeQuery();

                if (!rs.next()) {
                    JOptionPane.showMessageDialog(this, "No student found with that ID.");
                    return;
                }

                JTextField nameField = new JTextField(rs.getString("name"));
                JTextField emailField = new JTextField(rs.getString("email"));
                JTextField ageField = new JTextField(String.valueOf(rs.getInt("age")));

                Object[] message = {
                    "Name:", nameField,
                    "Email:", emailField,
                    "Age:", ageField
                };

                int option = JOptionPane.showConfirmDialog(this, message, "Update Student", JOptionPane.OK_CANCEL_OPTION);
                if (option == JOptionPane.OK_OPTION) {
                    String name = nameField.getText().trim();
                    String email = emailField.getText().trim();
                    String ageStr = ageField.getText().trim();

                    if (name.isEmpty() || email.isEmpty() || ageStr.isEmpty()) {
                        JOptionPane.showMessageDialog(this, "All fields are required.");
                        return;
                    }

                    int age;
                    try {
                        age = Integer.parseInt(ageStr);
                        if (age < 5 || age > 120) {
                            JOptionPane.showMessageDialog(this, "Age must be between 5 and 120.");
                            return;
                        }
                    } catch (NumberFormatException ex2) {
                        JOptionPane.showMessageDialog(this, "Age must be a valid number.");
                        return;
                    }

                    String updateSql = "UPDATE students SET name = ?, email = ?, age = ? WHERE id = ?";
                    PreparedStatement updateStmt = conn.prepareStatement(updateSql);
                    updateStmt.setString(1, name);
                    updateStmt.setString(2, email);
                    updateStmt.setInt(3, age);
                    updateStmt.setInt(4, id);

                    int rows = updateStmt.executeUpdate();
                    if (rows > 0) {
                        JOptionPane.showMessageDialog(this, "Student updated successfully!");
                    } else {
                        JOptionPane.showMessageDialog(this, "Failed to update student.");
                    }
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error updating student: " + ex.getMessage());
            }
        });

        // Delete Student
        deleteBtn.addActionListener(e -> {
            String idText = JOptionPane.showInputDialog(this, "Enter Student ID to delete:");
            if (idText == null) return;
            int id;
            try {
                id = Integer.parseInt(idText);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid ID entered.");
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete student with ID " + id + "?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
            if (confirm != JOptionPane.YES_OPTION) return;

            try {
                String deleteSql = "DELETE FROM students WHERE id = ?";
                PreparedStatement deleteStmt = conn.prepareStatement(deleteSql);
                deleteStmt.setInt(1, id);

                int rows = deleteStmt.executeUpdate();
                if (rows > 0) {
                    JOptionPane.showMessageDialog(this, "Student deleted successfully!");
                } else {
                    JOptionPane.showMessageDialog(this, "No student found with that ID.");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error deleting student: " + ex.getMessage());
            }
        });

        // Search Students
        searchBtn.addActionListener(e -> {
            String[] options = {"Name", "Email", "Age"};
            String criteria = (String) JOptionPane.showInputDialog(this, "Search by:", "Search Students", JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
            if (criteria == null) return; 

            String searchTerm = JOptionPane.showInputDialog(this, "Enter search term:");
            if (searchTerm == null || searchTerm.trim().isEmpty()) return;

            try {
                String sql;
                PreparedStatement stmt;

                switch (criteria) {
                    case "Name":
                        sql = "SELECT * FROM students WHERE name LIKE ?";
                        stmt = conn.prepareStatement(sql);
                        stmt.setString(1, "%" + searchTerm.trim() + "%");
                        break;
                    case "Email":
                        sql = "SELECT * FROM students WHERE email LIKE ?";
                        stmt = conn.prepareStatement(sql);
                        stmt.setString(1, "%" + searchTerm.trim() + "%");
                        break;
                    case "Age":
                        int age;
                        try {
                            age = Integer.parseInt(searchTerm.trim());
                            if (age < 5 || age > 120) {
                                JOptionPane.showMessageDialog(this, "Age must be between 5 and 120.");
                                return;
                            }
                        } catch (NumberFormatException ex) {
                            JOptionPane.showMessageDialog(this, "Invalid age entered.");
                            return;
                        }
                        sql = "SELECT * FROM students WHERE age = ?";
                        stmt = conn.prepareStatement(sql);
                        stmt.setInt(1, age);
                        break;
                    default:
                        JOptionPane.showMessageDialog(this, "Invalid search criteria.");
                        return;
                }

                ResultSet rs = stmt.executeQuery();

                StringBuilder sb = new StringBuilder();
                while (rs.next()) {
                    sb.append("ID: ").append(rs.getInt("id"))
                      .append(", Name: ").append(rs.getString("name"))
                      .append(", Email: ").append(rs.getString("email"))
                      .append(", Age: ").append(rs.getInt("age"))
                      .append("\n");
                }

                if (sb.length() == 0) {
                    JOptionPane.showMessageDialog(this, "No students found matching criteria.");
                } else {
                    JTextArea textArea = new JTextArea(sb.toString());
                    textArea.setEditable(false);
                    JScrollPane scrollPane = new JScrollPane(textArea);
                    scrollPane.setPreferredSize(new Dimension(350, 200));
                    JOptionPane.showMessageDialog(this, scrollPane, "Search Results", JOptionPane.INFORMATION_MESSAGE);
                }

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error during search: " + ex.getMessage());
            }
        });

        // Exit from program
        exitBtn.addActionListener(e -> System.exit(0));

        panel.add(addBtn);
        panel.add(viewBtn);
        panel.add(updateBtn);
        panel.add(deleteBtn);
        panel.add(searchBtn);
        panel.add(exitBtn);

        add(panel);
    }
}
