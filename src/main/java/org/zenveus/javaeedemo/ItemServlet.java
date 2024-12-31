package org.zenveus.javaeedemo;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.sql.*;

@WebServlet(urlPatterns = "/item")
public class ItemServlet extends HttpServlet {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/ZenVeusBags";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "Shen@1234";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            // Register the driver explicitly (optional for newer versions)
            Class.forName("com.mysql.cj.jdbc.Driver");

            try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
                String itemId = req.getParameter("id");

                PreparedStatement statement = connection.prepareStatement(
                        "SELECT * FROM Item" + (itemId == null ? "" : " WHERE item_id=?")
                );

                if (itemId != null) {
                    statement.setString(1, itemId);
                }

                ResultSet resultSet = statement.executeQuery();
                resp.setContentType("application/json");
                resp.getWriter().write("[");
                while (resultSet.next()) {
                    String id = resultSet.getString("item_id");
                    String name = resultSet.getString("description");
                    String price = resultSet.getString("retail_price");
                    String qtyOnHand = resultSet.getString("qty");

                    resp.getWriter().write(
                            "{\"id\":\"" + id + "\",\"name\":\"" + name + "\",\"price\":\"" + price + "\",\"qtyOnHand\":\"" + qtyOnHand + "\"}"
                    );

                    if (!resultSet.isLast()) {
                        resp.getWriter().write(",");
                    }
                    // Print Data retrieved from the database
                    System.out.println("Data retrieved from the database");
                    System.out.println("Item ID: " + id);
                    System.out.println("Item Name: " + name);
                    System.out.println("Item Price: " + price);
                    System.out.println("Item Quantity: " + qtyOnHand);
                }
                resp.getWriter().write("]");


            }
        } catch (ClassNotFoundException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\":\"MySQL JDBC Driver not found.\"}");
            e.printStackTrace();
        } catch (SQLException e) {
            handleException(resp, e, "Failed to retrieve items.");
        }
    }


    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            StringBuilder body = new StringBuilder();
            try (BufferedReader reader = req.getReader()) {
                String line;
                while ((line = reader.readLine()) != null) {
                    body.append(line);
                }
            }

            // Extract values from JSON
            String json = body.toString();
            String id = extractValue(json, "id");
            String name = extractValue(json, "name");
            String price = extractValue(json, "price");
            String qtyOnHand = extractValue(json, "qtyOnHand");

            if (id == null || name == null || price == null) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("{\"error\":\"Missing required fields.\"}");
                return;
            }

            PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO Item (item_id, description, retail_price, qty) VALUES (?, ?, ?,?)"
            );
            statement.setString(1, id);
            statement.setString(2, name);
            statement.setString(3, price);
            statement.setString(4, qtyOnHand);

            int rowsAffected = statement.executeUpdate();
            resp.setStatus(rowsAffected > 0 ? HttpServletResponse.SC_CREATED : HttpServletResponse.SC_BAD_REQUEST);
        } catch (Exception e) {
            handleException(resp, e, "Failed to create item.");
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String id = req.getParameter("id");
            String name = req.getParameter("name");
            String price = req.getParameter("price");
            String qtyOnHand = req.getParameter("qtyOnHand");

            if (id == null || name == null || price == null) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("{\"error\":\"Missing required parameters.\"}");
                return;
            }

            PreparedStatement statement = connection.prepareStatement(
                    "UPDATE Item SET description=?, retail_price=?, qty=? WHERE item_id=?"
            );
            statement.setString(1, name);
            statement.setString(2, price);
            statement.setString(3, id);
            statement.setString(4, qtyOnHand);


            int rowsAffected = statement.executeUpdate();
            resp.setStatus(rowsAffected > 0 ? HttpServletResponse.SC_OK : HttpServletResponse.SC_NOT_FOUND);
        } catch (Exception e) {
            handleException(resp, e, "Failed to update item.");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String itemId = req.getParameter("id");

            if (itemId == null) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("{\"error\":\"Item ID is required.\"}");
                return;
            }

            PreparedStatement statement = connection.prepareStatement(
                    "DELETE FROM Item WHERE item_id=?"
            );
            statement.setString(1, itemId);

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected == 0) {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.getWriter().write("{\"error\":\"Item not found.\"}");
            } else {
                resp.setStatus(HttpServletResponse.SC_OK);
            }
        } catch (Exception e) {
            handleException(resp, e, "Failed to delete item.");
        }
    }

    private String extractValue(String json, String key) {
        try {
            String searchKey = "\"" + key + "\"";
            int keyIndex = json.indexOf(searchKey);
            if (keyIndex == -1) {
                return null; // Key not found
            }

            int colonIndex = json.indexOf(":", keyIndex);
            if (colonIndex == -1) {
                return null; // Malformed JSON
            }

            int startQuote = json.indexOf("\"", colonIndex + 1);
            int endQuote = json.indexOf("\"", startQuote + 1);

            if (startQuote == -1 || endQuote == -1) {
                return null; // Value not properly quoted
            }

            return json.substring(startQuote + 1, endQuote);
        } catch (Exception e) {
            e.printStackTrace();
            return null; // Return null on any error
        }
    }


    private void handleException(HttpServletResponse resp, Exception e, String message) throws IOException {
        resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        resp.setContentType("application/json");
        resp.getWriter().write("{\"error\":\"" + message + "\"}");
        e.printStackTrace();
    }
}
