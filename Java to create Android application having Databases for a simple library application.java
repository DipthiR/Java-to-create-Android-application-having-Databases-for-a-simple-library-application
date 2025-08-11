import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;
public class LibraryApp {
    private static final String DB_URL = "jdbc:sqlite:library.db";

    public static void main(String[] args) {
        try {
            // Create or connect to the SQLite database
            Connection connection = DriverManager.getConnection(DB_URL);

            // Create the "books" table if it doesn't exist
            String createTableSQL = "CREATE TABLE IF NOT EXISTS books (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "title TEXT NOT NULL," +
                "author TEXT NOT NULL)";
            connection.createStatement().execute(createTableSQL);

            Scanner scanner = new Scanner(System.in);
            while (true) {
                System.out.println("Library Application");
                System.out.println("1. Add a book");
                System.out.println("2. View all books");
                System.out.println("3. Search for a book by title");
                System.out.println("4. Exit");
                System.out.print("Enter your choice: ");
                int choice = scanner.nextInt();
                scanner.nextLine(); // Consume the newline

                switch (choice) {
                    case 1:
                        addBook(connection, scanner);
                        break;
                    case 2:
                        viewBooks(connection);
                        break;
                    case 3:
                        searchBookByTitle(connection, scanner);
                        break;
                    case 4:
                        System.out.println("Goodbye!");
                        connection.close();
                        System.exit(0);
                    default:
                        System.out.println("Invalid choice. Try again.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void addBook(Connection connection, Scanner scanner) throws SQLException {
        System.out.print("Enter the title of the book: ");
        String title = scanner.nextLine();
        System.out.print("Enter the author of the book: ");
        String author = scanner.nextLine();

        String insertSQL = "INSERT INTO books (title, author) VALUES (?, ?)";
        PreparedStatement preparedStatement = connection.prepareStatement(insertSQL);
        preparedStatement.setString(1, title);
        preparedStatement.setString(2, author);
        preparedStatement.executeUpdate();

        System.out.println("Book added successfully!");
    }

    private static void viewBooks(Connection connection) throws SQLException {
        String selectSQL = "SELECT id, title, author FROM books";
        ResultSet resultSet = connection.createStatement().executeQuery(selectSQL);

        System.out.println("List of Books:");
        while (resultSet.next()) {
            int id = resultSet.getInt("id");
            String title = resultSet.getString("title");
            String author = resultSet.getString("author");
            System.out.println(id + ". " + title + " by " + author);
        }
    }

    private static void searchBookByTitle(Connection connection, Scanner scanner) throws SQLException {
        System.out.print("Enter the title to search for: ");
        String searchTitle = scanner.nextLine();

        String selectSQL = "SELECT id, title, author FROM books WHERE title LIKE ?";
        PreparedStatement preparedStatement = connection.prepareStatement(selectSQL);
        preparedStatement.setString(1, "%" + searchTitle + "%");
        ResultSet resultSet = preparedStatement.executeQuery();

        System.out.println("Search Results:");
        while (resultSet.next()) {
            int id = resultSet.getInt("id");
            String title = resultSet.getString("title");
            String author = resultSet.getString("author");
            System.out.println(id + ". " + title + " by " + author);
        }
    }
}
