package org.zenveus.javaeedemo;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

@WebServlet(urlPatterns = "/customer")
public class CustomerServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        System.out.println("");
        try {
            // Load the JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Establish a connection to the database
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/ZenVeusBags", "root", "Shen@1234");

            // Execute the query to fetch customers
            ResultSet resultSet = connection.prepareStatement("SELECT * FROM Customer").executeQuery();

            // Build the JSON response using StringBuilder
            StringBuilder jsonBuilder = new StringBuilder();
            jsonBuilder.append("[");

            boolean first = true;
            while (resultSet.next()) {
                if (!first) {
                    jsonBuilder.append(",");
                }

                // Format each customer record as a JSON object
                String customerJson = String.format(
                        "{\"id\":\"%s\",\"name\":\"%s\",\"email\":\"%s\"}",
                        resultSet.getString("cust_id"),
                        resultSet.getString("name"),
                        resultSet.getString("email")
                );

                jsonBuilder.append(customerJson);
                first = false;
            }

            jsonBuilder.append("]");

            // Close the resources
            resultSet.close();
            connection.close();

            // Write the JSON response
            resp.getWriter().write(jsonBuilder.toString());
        } catch (ClassNotFoundException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\":\"JDBC Driver not found.\"}");
            e.printStackTrace();
        } catch (SQLException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\":\"Database error occurred.\"}");
            e.printStackTrace();
        }
    }


    // doPut
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        System.out.println("Request : "+req.getReader().readLine());

        // Read the updated customer data from the request body
        StringBuilder requestBody = new StringBuilder();
        String line;
        while ((line = req.getReader().readLine()) != null) {
            requestBody.append(line);
            System.out.println("Line: " + line);
        }

        // Manually parse the JSON data
        String jsonString = requestBody.toString();
        String customerId = jsonString.split("\"id\":\"")[1].split("\"")[0];
        String name = jsonString.split("\"name\":\"")[1].split("\"")[0];
        String email = jsonString.split("\"email\":\"")[1].split("\"")[0];

        System.out.println("Customer ID: " + customerId);
        System.out.println("Name: " + name);
        System.out.println("Email: " + email);


        try {
            // Load the JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Establish a connection to the database
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/ZenVeusBags", "root", "Shen@1234");

            // Prepare the SQL UPDATE statement
            String updateSQL = "UPDATE Customer SET name = ?, email = ? WHERE cust_id = ?";
            var preparedStatement = connection.prepareStatement(updateSQL);
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, email);
            preparedStatement.setString(3, customerId);

            // Execute the update query
            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                // Send a success JSON response
                resp.setStatus(HttpServletResponse.SC_OK);
                resp.getWriter().write("{\"message\":\"Customer updated successfully!\"}");
            } else {
                // Send an error JSON response
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.getWriter().write("{\"message\":\"Customer not found.\"}");
            }

            // Close resources
            preparedStatement.close();
            connection.close();
        } catch (ClassNotFoundException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\":\"JDBC Driver not found.\"}");
            e.printStackTrace();
        } catch (SQLException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\":\"Database error occurred.\"}");
            e.printStackTrace();
        }
    }


    // doDelete

    // doPost
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        // Read the customer data from the request body
        StringBuilder requestBody = new StringBuilder();
        String line;
        while ((line = req.getReader().readLine()) != null) {
            requestBody.append(line);
        }

        // Assuming the data is in JSON format, parse the data
        String[] customerData = requestBody.toString().split(",");
        String id = customerData[0].split(":")[1].replace("\"", "");
        String name = customerData[1].split(":")[1].replace("\"", "");
        String email = customerData[2].split(":")[1].replace("\"", "");

        try {


            // Establish a connection to the database
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/ZenVeusBags", "root", "Shen@1234");

            // Prepare the SQL INSERT statement
            String insertSQL = "INSERT INTO Customer (cust_id, name, email) VALUES (?, ?, ?)";
            var preparedStatement = connection.prepareStatement(insertSQL);
            preparedStatement.setString(1, id);
            preparedStatement.setString(2, name);
            preparedStatement.setString(3, email);

            // Execute the query
            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                // Send a success JSON response
                resp.setStatus(HttpServletResponse.SC_CREATED);
                resp.getWriter().write("{\"message\":\"Customer added successfully!\"}");
            } else {
                // Send an error JSON response
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                resp.getWriter().write("{\"message\":\"Failed to add customer.\"}");
            }

            // Close resources
            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\":\"Database error occurred.\"}");
            e.printStackTrace();
        }
    }

    // doDelete
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        // Get the customer ID from the URL query string
        String customerId = req.getParameter("id");
        System.out.println("Customer ID: " + customerId);

        if (customerId == null || customerId.isEmpty()) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\":\"Customer ID is required.\"}");
            return;
        }

        try {
            // Load the JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Establish a connection to the database
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/ZenVeusBags", "root", "Shen@1234");

            // Prepare the SQL DELETE statement
            String deleteSQL = "DELETE FROM Customer WHERE cust_id = ?";
            var preparedStatement = connection.prepareStatement(deleteSQL);
            preparedStatement.setString(1, customerId);


            // Execute the query
            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                // Send a success JSON response
                resp.setStatus(HttpServletResponse.SC_OK);
                resp.getWriter().write("{\"message\":\"Customer deleted successfully!\"}");
            } else {
                // Send an error JSON response
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.getWriter().write("{\"message\":\"Customer not found.\"}");
            }

            // Close resources
            preparedStatement.close();
            connection.close();
        } catch (ClassNotFoundException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\":\"JDBC Driver not found.\"}");
            e.printStackTrace();
        } catch (SQLException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\":\"Database error occurred.\"}");
            e.printStackTrace();
        }
    }
}
