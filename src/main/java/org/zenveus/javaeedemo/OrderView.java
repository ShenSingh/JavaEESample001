package org.zenveus.javaeedemo;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.*;

@WebServlet(urlPatterns = "/order_view")
public class OrderView extends HttpServlet {

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

            // First query: Fetch all orders
            String orderQuery = "SELECT o.o_id, o.cust_id, o.total, o.date FROM `Order` o";
            PreparedStatement orderStatement = connection.prepareStatement(orderQuery);
            ResultSet orderResultSet = orderStatement.executeQuery();

            StringBuilder jsonBuilder = new StringBuilder();
            jsonBuilder.append("[");

            boolean firstOrder = true;

            while (orderResultSet.next()) {
                if (!firstOrder) {
                    jsonBuilder.append(",");
                }

                String orderId = orderResultSet.getString("o_id");
                String customerId = orderResultSet.getString("cust_id");
                String total = orderResultSet.getString("total");
                String date = orderResultSet.getString("date");

                // Start the order JSON structure
                jsonBuilder.append(String.format(
                        "{\"id\":\"%s\", \"cust_id\":\"%s\", \"total\":\"%s\", \"date\":\"%s\", \"items\":",
                        orderId,
                        customerId,
                        total,
                        date
                ));

                // Second query: Fetch items for the current order
                String itemQuery = "SELECT i.item_id, i.qty FROM `order_item_detail` i WHERE i.o_id = ?";
                PreparedStatement itemStatement = connection.prepareStatement(itemQuery);
                itemStatement.setString(1, orderId);
                ResultSet itemResultSet = itemStatement.executeQuery();

                // Start the items array
                jsonBuilder.append("[");

                boolean firstItem = true;
                while (itemResultSet.next()) {
                    if (!firstItem) {
                        jsonBuilder.append(",");
                    }

                    String itemId = itemResultSet.getString("item_id");
                    String qty = itemResultSet.getString("qty");

                    jsonBuilder.append(String.format(
                            "{\"item_id\":\"%s\", \"qty\":\"%s\"}",
                            itemId,
                            qty
                    ));
                    firstItem = false;
                }

                // Close the items array
                jsonBuilder.append("]}");

                firstOrder = false;
            }

            jsonBuilder.append("]");  // Close the array of orders

            orderResultSet.close();

            // Write the response back to the client
            resp.getWriter().write(jsonBuilder.toString());

            // Print the returning data for debugging
            System.out.println("\n\n\nReturning Data : " + jsonBuilder.toString());

        } catch (SQLException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\":\"Database error occurred.\"}");
            e.printStackTrace();
        }
    }
}
