import java.sql.*;
import java.util.Scanner;
import java.util.regex.Pattern;
import java.io.Console;

public class StudentManagementApp {

    private static final String URL = "jdbc:mysql://localhost:3306/my_app_db";
    private static final String USER = "root";
    private static final String PASSWORD = "kartikey@1506";

    private static Connection conn;

    public static void main(String[] args) {
        try {
            conn = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Connected to MySQL database!");

            try (Scanner scanner = new Scanner(System.in)) {
                boolean running = true;
                
                
                if (!login(scanner)) {
                    conn.close();
                    scanner.close();
                    return;
                }
                
                
                while (running) {
                    System.out.println("\nStudent Management Menu:");
                    System.out.println("1. Add Student");
                    System.out.println("2. View Students");
                    System.out.println("3. Update Student Information");
                    System.out.println("4. Delete Student");
                    System.out.println("5. Search Students");
                    System.out.println("6. Exit");
                    System.out.print("Enter your choice: ");
                    
                    int choice = Integer.parseInt(scanner.nextLine());
                    
                    switch (choice) {
                        case 1 -> addStudent(scanner);
                        case 2 -> viewStudents(scanner);
                        case 3 -> updateStudent(scanner);
                        case 4 -> deleteStudent(scanner);
                        case 5 -> searchStudents(scanner);
                        case 6 -> running = false;
                        default -> System.out.println("Invalid choice. Try again.");
                    }
                }
            }
            conn.close();
            System.out.println("App closed.");

        } catch (SQLException e) {
        }
    }
    
    private static boolean login(Scanner scanner) throws SQLException {
    System.out.println("=== User Login ==="); 
    System.out.print("Username: ");
    String username = scanner.nextLine();
    Console console = System.console();
    String password;

    if (console != null) {
        char[] passwordChars = console.readPassword("Password: ");
        password = new String(passwordChars);
    } else {
        System.out.print("Password: ");
        password = scanner.nextLine();
    }

    String sql = "SELECT role FROM users WHERE username = ? AND password = ?";
    PreparedStatement stmt = conn.prepareStatement(sql);
    stmt.setString(1, username);
    stmt.setString(2, password);

    ResultSet rs = stmt.executeQuery();

    if (rs.next()) {
        String role = rs.getString("role");
        System.out.println("Login successful! Role: " + role);
        return true;
    } else {
        System.out.println("Invalid username or password. Exiting.");
        return false;
    }
}



        private static void addStudent(Scanner scanner) throws SQLException {
    System.out.print("Enter student name: ");
    String name = scanner.nextLine();

    String email;
    while (true) {
        System.out.print("Enter student email: ");
        email = scanner.nextLine();
        if (isValidEmail(email)) break;
        System.out.println("Invalid email format. Please try again.");
    }

    int age;
    while (true) {
        System.out.print("Enter student age: ");
        try {
            age = Integer.parseInt(scanner.nextLine());
            if (age >= 5 && age <= 120) break;
            else System.out.println("Age must be between 5 and 120.");
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid integer for age.");
        }
    }

    String sql = "INSERT INTO students (name, email, age) VALUES (?, ?, ?)";
    PreparedStatement stmt = conn.prepareStatement(sql);
    stmt.setString(1, name);
    stmt.setString(2, email);
    stmt.setInt(3, age);

    int rowsInserted = stmt.executeUpdate();

    if (rowsInserted > 0) {
        System.out.println("Student added successfully!");
    } else {
        System.out.println("Error adding student.");
    }
}


    private static void viewStudents(Scanner scanner) throws SQLException {
        System.out.println("Sort by:");
        System.out.println("1. ID");
        System.out.println("2. Name");
        System.out.println("3. Age");
        System.out.print("Enter choice (default ID): ");
        String input = scanner.nextLine();

        String orderBy;
        switch (input) {
            case "2":
                orderBy = "name";
                break;
            case "3":
                orderBy = "age";
                break;
            case "1":
            default:
                orderBy = "id";
        }

        String sql = "SELECT * FROM students ORDER BY " + orderBy;
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql);

        System.out.println("\nList of students:");
        while (rs.next()) {
            System.out.println("ID: " + rs.getInt("id")
                    + ", Name: " + rs.getString("name")
                    + ", Email: " + rs.getString("email")
                    + ", Age: " + rs.getInt("age"));
        }
    }

    private static void updateStudent(Scanner scanner) throws SQLException {
        System.out.print("Enter student ID to update: ");
        int id = Integer.parseInt(scanner.nextLine());

        System.out.println("What do you want to update?");
        System.out.println("1. Name");
        System.out.println("2. Email");
        System.out.println("3. Age");
        System.out.println("4. All");

        System.out.print("Enter choice: ");
        int choice = Integer.parseInt(scanner.nextLine());

        String newName = null;
        String newEmail = null;
        Integer newAge = null;

        switch (choice) {
            case 1 -> {
                System.out.print("Enter new name: ");
                newName = scanner.nextLine();
            }
            case 2 -> {
                while (true) {
                    System.out.print("Enter new email: ");
                    newEmail = scanner.nextLine();
                    if (isValidEmail(newEmail)) break;
                    System.out.println("Invalid email format. Please try again.");
                }
            }
            case 3 -> {
                while (true) {
                    System.out.print("Enter new age: ");
                    try {
                        newAge = Integer.parseInt(scanner.nextLine());
                        if (newAge >= 5 && newAge <= 120) break;
                        else System.out.println("Age must be between 5 and 120.");
                    } catch (NumberFormatException e) {
                        System.out.println("Please enter a valid integer for age.");
                    }
                }
            }
            case 4 -> {
                System.out.print("Enter new name: ");
                newName = scanner.nextLine();

                while (true) {
                    System.out.print("Enter new email: ");
                    newEmail = scanner.nextLine();
                    if (isValidEmail(newEmail)) break;
                    System.out.println("Invalid email format. Please try again.");
                }

                while (true) {
                    System.out.print("Enter new age: ");
                    try {
                        newAge = Integer.parseInt(scanner.nextLine());
                        if (newAge >= 5 && newAge <= 120) break;
                        else System.out.println("Age must be between 5 and 120.");
                    } catch (NumberFormatException e) {
                        System.out.println("Please enter a valid integer for age.");
                    }
                }
            }
            default -> {
                System.out.println("Invalid choice. Update cancelled.");
                return;
            }
        }

        String sql = null;
        if (choice == 1) {
            sql = "UPDATE students SET name = ? WHERE id = ?";
        } else if (choice == 2) {
            sql = "UPDATE students SET email = ? WHERE id = ?";
        } else if (choice == 3) {
            sql = "UPDATE students SET age = ? WHERE id = ?";
        } else if (choice == 4) {
            sql = "UPDATE students SET name = ?, email = ?, age = ? WHERE id = ?";
        }

        PreparedStatement stmt = conn.prepareStatement(sql);

        if (choice == 1) {
            stmt.setString(1, newName);
            stmt.setInt(2, id);
        } else if (choice == 2) {
            stmt.setString(1, newEmail);
            stmt.setInt(2, id);
        } else if (choice == 3) {
            stmt.setInt(1, newAge);
            stmt.setInt(2, id);
        } else if (choice == 4) {
            stmt.setString(1, newName);
            stmt.setString(2, newEmail);
            stmt.setInt(3, newAge);
            stmt.setInt(4, id);
        }

        int rowsUpdated = stmt.executeUpdate();

        if (rowsUpdated > 0) {
            System.out.println("Student updated successfully!");
        } else {
            System.out.println("No student found with that ID.");
        }
    }

    private static void deleteStudent(Scanner scanner) throws SQLException {
        System.out.print("Enter student ID to delete: ");
        int id = Integer.parseInt(scanner.nextLine());

        String sql = "DELETE FROM students WHERE id = ?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, id);

        int rowsDeleted = stmt.executeUpdate();

        if (rowsDeleted > 0) {
            System.out.println("Student deleted successfully!");
        } else {
            System.out.println("No student found with that ID.");
        }
    }

    private static void searchStudents(Scanner scanner) throws SQLException {
        System.out.println("Search by:");
        System.out.println("1. Name");
        System.out.println("2. Email");
        System.out.println("3. Age");
        System.out.print("Enter choice: ");

        int choice = Integer.parseInt(scanner.nextLine());
        String searchTerm = null;
        int ageTerm = -1;
        String sql = null;
        PreparedStatement stmt;

        switch (choice) {
            case 1:
                System.out.print("Enter name to search (partial allowed): ");
                searchTerm = scanner.nextLine();
                sql = "SELECT * FROM students WHERE name LIKE ?";
                stmt = conn.prepareStatement(sql);
                stmt.setString(1, "%" + searchTerm + "%");
                break;
            case 2:
                System.out.print("Enter email to search (partial allowed): ");
                searchTerm = scanner.nextLine();
                sql = "SELECT * FROM students WHERE email LIKE ?";
                stmt = conn.prepareStatement(sql);
                stmt.setString(1, "%" + searchTerm + "%");
                break;
            case 3:
                while (true) {
                    System.out.print("Enter age to search: ");
                    try {
                        ageTerm = Integer.parseInt(scanner.nextLine());
                        if (ageTerm >= 5 && ageTerm <= 120) break;
                        else System.out.println("Age must be between 5 and 120.");
                    } catch (NumberFormatException e) {
                        System.out.println("Please enter a valid integer for age.");
                    }
                }
                sql = "SELECT * FROM students WHERE age = ?";
                stmt = conn.prepareStatement(sql);
                stmt.setInt(1, ageTerm);
                break;
            default:
                System.out.println("Invalid choice. Returning to menu.");
                return;
        }

        ResultSet rs = stmt.executeQuery();

        System.out.println("\nSearch results:");
        boolean found = false;
        while (rs.next()) {
            found = true;
            System.out.println("ID: " + rs.getInt("id")
                    + ", Name: " + rs.getString("name")
                    + ", Email: " + rs.getString("email")
                    + ", Age: " + rs.getInt("age"));
        }
        if (!found) {
            System.out.println("No matching students found.");
        }
    }

    private static boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        return Pattern.matches(emailRegex, email);
    }
}
