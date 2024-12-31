package org.zenveus.javaeedemo;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

@WebServlet(urlPatterns = "/latest")
public class Latest extends HttpServlet {
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

            // get latest orderid from the database
            ResultSet resultSet = connection.prepareStatement("SELECT o_id FROM `Order` ORDER BY o_id DESC LIMIT 1;").executeQuery();

            StringBuilder jsonBuilder = new StringBuilder();
            jsonBuilder.append("[");

            boolean first = true;
            while (resultSet.next()) {
                if (!first) {
                    jsonBuilder.append(",");
                }

                String orderJson = String.format(
                        "{\"id\":\"%s\"}",
                        resultSet.getString("o_id")
                );

                jsonBuilder.append(orderJson);
                first = false;
            }

            jsonBuilder.append("]");

            resp.getWriter().write(jsonBuilder.toString());

            // print the returning data
            System.out.println("\n\n\nReturning Data : "+jsonBuilder.toString());
        } catch (SQLException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\":\"Database error occurred.\"}");
            e.printStackTrace();
        }
    }
}
