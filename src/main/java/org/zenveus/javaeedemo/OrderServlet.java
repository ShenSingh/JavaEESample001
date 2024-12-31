package org.zenveus.javaeedemo;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.sql.*;

@WebServlet(urlPatterns = "/order")
public class OrderServlet extends HttpServlet {
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/ZenVeusBags", "root", "Shen@1234")) {
            ResultSet resultSet = connection.prepareStatement("SELECT * FROM `Order`;").executeQuery();

            StringBuilder jsonBuilder = new StringBuilder();
            jsonBuilder.append("[");

            boolean first = true;
            while (resultSet.next()) {
                if (!first) {
                    jsonBuilder.append(",");
                }

                String orderJson = String.format(
                        "{\"id\":\"%s\",\"cust_id\":\"%s\",\"total\":\"%s\",\"date\":\"%s\"}",
                        resultSet.getString("o_id"),
                        resultSet.getString("cust_id"),
                        resultSet.getString("total"),
                        resultSet.getString("date")
                );

                jsonBuilder.append(orderJson);
                first = false;
            }

            jsonBuilder.append("]");
            resultSet.close();

            resp.getWriter().write(jsonBuilder.toString());

            // print the returning data
            System.out.println("\n\n\nReturning Data : "+jsonBuilder.toString());
        } catch (SQLException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\":\"Database error occurred.\"}");
            e.printStackTrace();
        }
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/ZenVeusBags", "root", "Shen@1234")) {
            connection.setAutoCommit(false);

            BufferedReader reader = request.getReader();
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
            String requestBody = stringBuilder.toString();

            Gson gson = new Gson();
            JsonObject jsonObject = gson.fromJson(requestBody, JsonObject.class);
            JsonObject orderJson = jsonObject.getAsJsonObject("order");
            JsonArray itemsJson = jsonObject.getAsJsonArray("items");

            String custId = orderJson.get("cust_id").getAsString();
            String total = orderJson.get("total").getAsString();
            String date = orderJson.get("date").getAsString();

            if (custId == null || total == null || date == null || itemsJson == null) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("{\"error\":\"Missing required parameters.\"}");
                return;
            }

            String orderQuery = "INSERT INTO `Order` (cust_id, total, date) VALUES (?, ?, ?)";
            try (PreparedStatement orderStatement = connection.prepareStatement(orderQuery)) {
                orderStatement.setString(1, custId);
                orderStatement.setString(2, total);
                orderStatement.setString(3, date);
                orderStatement.executeUpdate();
            }

            // Fetch the last inserted order ID
            String lastId = null;
            String fetchLastOrderQuery = "SELECT o_id FROM `Order` WHERE cust_id = ? ORDER BY o_id DESC LIMIT 1";
            try (PreparedStatement lastIdStatement = connection.prepareStatement(fetchLastOrderQuery)) {
                lastIdStatement.setString(1, custId);
                try (ResultSet resultSet = lastIdStatement.executeQuery()) {
                    if (resultSet.next()) {
                        lastId = resultSet.getString("o_id");
                    }
                }
            }

            if (lastId == null) {
                throw new SQLException("Failed to retrieve last inserted order ID.");
            }

            // Insert order items
            String itemQuery = "INSERT INTO `order_item_detail` (o_id, item_id, qty) VALUES (?, ?, ?)";
            try (PreparedStatement orderItemStatement = connection.prepareStatement(itemQuery)) {
                for (int i = 0; i < itemsJson.size(); i++) {
                    JsonObject itemJson = itemsJson.get(i).getAsJsonObject();
                    String itemId = itemJson.get("item_id").getAsString();
                    String qty = itemJson.get("qty").getAsString();

                    orderItemStatement.setString(1, lastId);
                    orderItemStatement.setString(2, itemId);
                    orderItemStatement.setString(3, qty);
                    orderItemStatement.executeUpdate();
                }
            }

            // deduct the quantity from the stock
            String deductQuery = "UPDATE Item SET qty = qty - ? WHERE item_id = ?";
            try (PreparedStatement deductStatement = connection.prepareStatement(deductQuery)) {
                for (int i = 0; i < itemsJson.size(); i++) {
                    JsonObject itemJson = itemsJson.get(i).getAsJsonObject();
                    String itemId = itemJson.get("item_id").getAsString();
                    String qty = itemJson.get("qty").getAsString();

                    deductStatement.setString(1, qty);
                    deductStatement.setString(2, itemId);
                    deductStatement.executeUpdate();
                }
            }


            connection.commit();
            resp.getWriter().write("{\"status\":\"success\"}");
        } catch (SQLException | JsonSyntaxException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\":\"Error occurred while processing the order.\"}");
            e.printStackTrace();
        }
    }
}
