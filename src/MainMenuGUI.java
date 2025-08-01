import javax.swing.*;              // Import Swing GUI components
import java.awt.*;                 // Import AWT package for layout managers and other GUI tools
import java.sql.Connection;        // Import for database connection handling
import java.sql.PreparedStatement; // Import for preparing SQL queries safely
import java.sql.ResultSet;          // Import to hold results from SQL queries

public class MainMenuGUI extends JFrame {  // Class representing the main menu window, extending JFrame

    private final Connection conn;   // Variable to store the database connection

    // Constructor that takes a Connection object for database operations
    public MainMenuGUI(Connection conn) {
        this.conn = conn;            // Assign the passed Connection to the class variable

        setTitle("Student Management System - GUI"); // Set the window title
        setSize(800, 700);          
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
        setLocationRelativeTo(null); // Center the window on the screen

        // Create a JPanel with GridLayout:
        JPanel panel = new JPanel(new GridLayout(0, 1, 10, 10));

        // Create buttons for different student management actions:
        JButton addBtn = new JButton("Add Student");
        JButton viewBtn = new JButton("View Students");
        JButton updateBtn = new JButton("Update Student");
        JButton deleteBtn = new JButton("Delete Student");
        JButton searchBtn = new JButton("Search Students");
        JButton exitBtn = new JButton("Exit");


        // Add Student button action listener
    addBtn.addActionListener(e -> {
        // Create input fields for name, email, and age
        JTextField nameField = new JTextField();
        JTextField emailField = new JTextField();
        JTextField ageField = new JTextField();

    // Create an array to hold labels and corresponding input fields for the dialog box
    Object[] message = {
        "Name:", nameField,
        "Email:", emailField,
        "Age:", ageField
    };

    // Show a dialog box to get student details with OK and Cancel options
    int option = JOptionPane.showConfirmDialog(this, message, "Add Student", JOptionPane.OK_CANCEL_OPTION);

    // If user clicked OK
    if (option == JOptionPane.OK_OPTION) {
        // Read and trim input values to remove extra spaces
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String ageText = ageField.getText().trim();

        // Check if any field is empty; if yes, show error message and return early
        if (name.isEmpty() || email.isEmpty() || ageText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required.");
            return;
        }

        int age;
        try {
            // Convert age input from String to integer using the parseInt funtion
            age = Integer.parseInt(ageText);

            // Validate age is within reasonable limits
            if (age < 5 || age > 120) {
                JOptionPane.showMessageDialog(this, "Age must be between 5 and 120.");
                return;  
            }
        } catch (NumberFormatException ex) {
            // Show error if age is not a valid number
            JOptionPane.showMessageDialog(this, "Age must be a valid number.");
            return; 
        }

        try {
            // Prepare SQL INSERT statement with placeholders for data
            String sql = "INSERT INTO students (name, email, age) VALUES (?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);

            // Set the parameters in the SQL query safely to avoid SQL injection
            stmt.setString(1, name);
            stmt.setString(2, email);
            stmt.setInt(3, age);

            // Execute the update query and get number of affected rows
            int rows = stmt.executeUpdate();

            // Check if insertion was successful and show appropriate message
            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "Student added successfully!");
            } else {
                JOptionPane.showMessageDialog(this, "Failed to add student.");
            }
        } catch (Exception ex) {
            // Show error message if database operation fails
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
        }
    }
});


        // View Students button action listener
    viewBtn.addActionListener(e -> {
    try {
        // Prepare and execute SQL query to get all students
        String sql = "SELECT * FROM students ORDER BY id";
        PreparedStatement stmt = conn.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery();

        // Build a string with all student info
        StringBuilder sb = new StringBuilder();

        while (rs.next()) {
            sb.append("ID: ").append(rs.getInt("id"))
              .append(", Name: ").append(rs.getString("name"))
              .append(", Email: ").append(rs.getString("email"))
              .append(", Age: ").append(rs.getInt("age"))
              .append("\n");
        }

        // Display student list in a scrollable text area
        JTextArea textArea = new JTextArea(sb.toString());
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(350, 200));

        JOptionPane.showMessageDialog(this, scrollPane, "All Students", JOptionPane.INFORMATION_MESSAGE);

    } catch (Exception ex) {
        // Show error message if query fails
        JOptionPane.showMessageDialog(this, "Error fetching students: " + ex.getMessage());
    }
});

    
        // Update Student button action listener
    updateBtn.addActionListener(e -> {
        // Prompt user to enter the Student ID they want to update
        String idText = JOptionPane.showInputDialog(this, "Enter Student ID to update:");
        if (idText == null) return; // If user cancels input dialog, exit the action

        int id;
        try {
            // Convert input string to integer ID
            id = Integer.parseInt(idText);
        } catch (NumberFormatException ex) {
            // Show error if entered ID is not a valid number
            JOptionPane.showMessageDialog(this, "Invalid ID entered.");
            return;
        }

        try {
            // Prepare SQL to select student with the specified ID
            String selectSql = "SELECT * FROM students WHERE id = ?";
            PreparedStatement selectStmt = conn.prepareStatement(selectSql);
            selectStmt.setInt(1, id);
            ResultSet rs = selectStmt.executeQuery();

            // Check if student with given ID exists
            if (!rs.next()) {
                JOptionPane.showMessageDialog(this, "No student found with that ID.");
                return;
            }

            // Create text fields pre-filled with existing student data for editing
            JTextField nameField = new JTextField(rs.getString("name"));
            JTextField emailField = new JTextField(rs.getString("email"));
            JTextField ageField = new JTextField(String.valueOf(rs.getInt("age")));

            // Create dialog message with labels and fields
            Object[] message = {
                "Name:", nameField,
                "Email:", emailField,
                "Age:", ageField
            };

            // Show confirm dialog to update student info
            int option = JOptionPane.showConfirmDialog(this, message, "Update Student", JOptionPane.OK_CANCEL_OPTION);
            if (option == JOptionPane.OK_OPTION) {
                // Get updated values and trim whitespace
                String name = nameField.getText().trim();
                String email = emailField.getText().trim();
                String ageStr = ageField.getText().trim();

                // Validate that none of the fields are empty
                if (name.isEmpty() || email.isEmpty() || ageStr.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "All fields are required.");
                    return;
                }

                int age;
                try {
                    // Convert age input to integer and validate range
                    age = Integer.parseInt(ageStr);
                    if (age < 5 || age > 120) {
                        JOptionPane.showMessageDialog(this, "Age must be between 5 and 120.");
                     return;
                }
                } catch (NumberFormatException ex2) {
                    // Show error if age is not a valid number
                    JOptionPane.showMessageDialog(this, "Age must be a valid number.");
                    return;
                }

                // Prepare SQL UPDATE statement to save changes to database
                String updateSql = "UPDATE students SET name = ?, email = ?, age = ? WHERE id = ?";
                PreparedStatement updateStmt = conn.prepareStatement(updateSql);
                updateStmt.setString(1, name);
                updateStmt.setString(2, email);
                updateStmt.setInt(3, age);
                updateStmt.setInt(4, id);

                // Execute update and check if at least one row was affected
                int rows = updateStmt.executeUpdate();
                if (rows > 0) {
                    JOptionPane.showMessageDialog(this, "Student updated successfully!");
                } else {
                 JOptionPane.showMessageDialog(this, "Failed to update student.");
                }
        }
        } catch (Exception ex) {
            // Show error message if something goes wrong with the database operation
            JOptionPane.showMessageDialog(this, "Error updating student: " + ex.getMessage());
        }
    });

        // Delete Student button action
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
            // Confirm deletion with the user
            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete student with ID " + id + "?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
            if (confirm != JOptionPane.YES_OPTION) return;

            try {
                // Prepare and execute DELETE statement
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

        // Search Students button action
        searchBtn.addActionListener(e -> {
            String[] options = {"Name", "Email", "Age"};
            String criteria = (String) JOptionPane.showInputDialog(this, "Search by:", "Search Students", JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
            if (criteria == null) return;  // Cancel pressed

            String searchTerm = JOptionPane.showInputDialog(this, "Enter search term:");
            if (searchTerm == null || searchTerm.trim().isEmpty()) return;

            try {
                String sql;
                PreparedStatement stmt;
                
                // Prepare SQL based on chosen criteria
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
                
                // Build search results string
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
        
        // Add buttons to panel and add panel to frame
        panel.add(addBtn);
        panel.add(viewBtn);
        panel.add(updateBtn);
        panel.add(deleteBtn);
        panel.add(searchBtn);
        panel.add(exitBtn);

        add(panel);
    }
}
