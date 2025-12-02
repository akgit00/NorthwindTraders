package com.pluralsight;

import java.awt.*;
import java.util.Scanner;
import java.sql.*;

public class App {

    public static void main(String[] args) {

        //did we pass in a username and password
        //if not, the application must die
        if (args.length != 2) {
            //display a message to the user
            System.out.println("Application needs two args to run: A username and a password for the db");
            //exit the app due to failure because we dont have a usernamne and password from the command line
            System.exit(1);
        }

        //get the username and password from args[]
        String username = args[0];
        String password = args[1];

        //create a scanner to ask the user some questions from our menu
        Scanner myScanner = new Scanner(System.in);

        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/northwind", username, password)) {

            boolean running  = true;

            while (running) {

                System.out.println("""
                What do you want to do?
                1) Display all products
                2) Display all customers
                3) Display all categories
                0) Exit
                Select an option:
                """);


                switch (myScanner.nextInt()) {
                    case 1:
                        displayAllProducts(connection);
                        break;
                    case 2:
                        displayAllCustomers(connection);
                        break;
                    case 3:
                        displayAllCategories(connection, scanner);
                        break;
                    case 0:
                        running = false;
                        System.out.println("Goodbye!");
                        break;
                    default:
                        System.out.println("Invalid option.");
                }
            }
        } catch (SQLException e) {
            System.out.println("Could not connect to DB");
            System.exit(1);
        }
    }
    public static void displayAllProducts(Connection connection) {

        try (
                PreparedStatement preparedStatement = connection.prepareStatement("""
                SELECT
                    ProductID,
                    ProductName,
                    UnitPrice,
                    UnitsInStock
                FROM
                    Products
                ORDER BY
                    ProductID
            """);

                ResultSet results = preparedStatement.executeQuery();
        ) {

            printResults(results);

        } catch (SQLException e) {
            System.out.println("Could not get all the products");
            System.exit(1);
        }
    }

    public static void displayAllCategories(Connection connection, Scanner scanner) {

        // first display all categories
        try (
                PreparedStatement ps = connection.prepareStatement("""
                    SELECT CategoryID, CategoryName
                    FROM Categories
                    ORDER BY CategoryID
                    """);
                ResultSet results = ps.executeQuery()
        ) {
            System.out.println("CATEGORIES:");
            printResults(results);

        } catch (SQLException e) {
            System.out.println("Could not retrieve categories.");
            return;
        }

        // ask user which category they want
        System.out.print("Enter a Category ID to view its products: ");
        int categoryId = scanner.nextInt();

        displayProductsByCategory(connection, categoryId);
    }

    public static void displayProductsByCategory(Connection connection, int categoryId) {

        try (
                PreparedStatement ps = connection.prepareStatement("""
                    SELECT 
                        ProductID,
                        ProductName,
                        UnitPrice,
                        UnitsInStock
                    FROM Products
                    WHERE CategoryID = ?
                    ORDER BY ProductID
                    """


                        }


    public static void displayAllCustomers(Connection connection) {

        try (
                PreparedStatement ps = connection.prepareStatement("""
                        SELECT 
                            ContactName,
                            CompanyName,
                            City,
                            Country,
                            Phone
                        FROM Customers
                        ORDER BY Country
                        """);
                ResultSet results = ps.executeQuery()
        ) {
            printResults(results);

        } catch (SQLException e) {
            System.out.println("Could not retrieve customers.");
        }
    }



    //this method will be used in the displayMethods to actually print the results to the screen
    public static void printResults(ResultSet results) throws SQLException {

        ResultSetMetaData meta = results.getMetaData();
        int columnCount = meta.getColumnCount();

        while (results.next()) {

            for (int i = 1; i <= columnCount; i++) {
                String colName = meta.getColumnName(i);
                String value = results.getString(i);
                System.out.println(colName + ": " + value);
            }

            System.out.println("------------------------");
        }
    }
}
