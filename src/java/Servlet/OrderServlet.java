package Servlet;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.*;
import java.sql.*;
import java.util.*;

import Util.DBUtil;

@WebServlet("/api/orders")
public class OrderServlet extends HttpServlet {
    Gson gson = new Gson();

    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        res.setContentType("application/json");

        try (Connection conn = DBUtil.getConnection()) {
            String idParam = req.getParameter("id");

            PreparedStatement stmt;
            if (idParam != null) {
                stmt = conn.prepareStatement("SELECT * FROM orders WHERE id=? ORDER BY order_date DESC");
                stmt.setInt(1, Integer.parseInt(idParam));
            } else {
                stmt = conn.prepareStatement("SELECT * FROM orders ORDER BY order_date DESC");
            }

            ResultSet rs = stmt.executeQuery();
            List<Map<String, Object>> orders = new ArrayList<>();

            while (rs.next()) {
                int orderId = rs.getInt("id");

                Map<String, Object> order = new HashMap<>();
                order.put("id", orderId);
                order.put("customer_name", rs.getString("customer_name"));
                order.put("contact", rs.getString("contact"));
                order.put("address", rs.getString("address"));
                order.put("payment_method", rs.getString("payment_method"));
                order.put("status", rs.getString("status"));
                order.put("order_date", rs.getTimestamp("order_date").toString());

                PreparedStatement itemStmt = conn.prepareStatement(
                    "SELECT item_name, quantity, price FROM order_items WHERE order_id = ?"
                );
                itemStmt.setInt(1, orderId);
                ResultSet itemRs = itemStmt.executeQuery();

                List<String> itemDescriptions = new ArrayList<>();
                double total = 0;

                while (itemRs.next()) {
                    String name = itemRs.getString("item_name");
                    int quantity = itemRs.getInt("quantity");
                    double price = itemRs.getDouble("price");

                    total += quantity * price;
                    itemDescriptions.add(name + " x" + quantity);
                }

                order.put("items", String.join(", ", itemDescriptions));
                order.put("total", total);

                orders.add(order);
            }

            res.getWriter().write(gson.toJson(orders));

        } catch (Exception e) {
            e.printStackTrace();
            res.setStatus(500);
            res.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        res.setContentType("application/json");

        BufferedReader reader = req.getReader();
        JsonObject body = gson.fromJson(reader, JsonObject.class);

        String name = body.get("customer_name").getAsString();
        String contact = body.get("contact").getAsString();
        String address = body.get("address").getAsString();
        String payment = body.get("payment_method").getAsString();
        JsonArray itemsArray = body.get("items").getAsJsonArray();

        try (Connection conn = DBUtil.getConnection()) {
            conn.setAutoCommit(false); // Start transaction

            // Insert order
            PreparedStatement orderStmt = conn.prepareStatement(
                "INSERT INTO orders (customer_name, contact, address, payment_method) VALUES (?, ?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS
            );
            orderStmt.setString(1, name);
            orderStmt.setString(2, contact);
            orderStmt.setString(3, address);
            orderStmt.setString(4, payment);

            int orderRows = orderStmt.executeUpdate();

            if (orderRows == 0) throw new SQLException("Failed to insert order.");

            ResultSet generatedKeys = orderStmt.getGeneratedKeys();
            if (!generatedKeys.next()) throw new SQLException("No order ID generated.");
            int orderId = generatedKeys.getInt(1);

            // Insert order items
            PreparedStatement itemStmt = conn.prepareStatement(
                "INSERT INTO order_items (order_id, item_name, quantity, price) VALUES (?, ?, ?, ?)"
            );

            for (int i = 0; i < itemsArray.size(); i++) {
                JsonObject item = itemsArray.get(i).getAsJsonObject();
                itemStmt.setInt(1, orderId);
                itemStmt.setString(2, item.get("name").getAsString());
                itemStmt.setInt(3, item.get("quantity").getAsInt());
                itemStmt.setDouble(4, item.get("price").getAsDouble());
                itemStmt.addBatch();
            }

            itemStmt.executeBatch();

            conn.commit(); // Commit transaction

            res.getWriter().write("{\"status\":\"Order placed\",\"order_id\":" + orderId + "}");

        } catch (Exception e) {
            e.printStackTrace();
            res.setStatus(500);
            res.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    protected void doDelete(HttpServletRequest req, HttpServletResponse res) throws IOException {
        String idParam = req.getParameter("id");

        if (idParam == null || idParam.isEmpty()) {
            res.setStatus(400);
            res.getWriter().write("{\"error\":\"Missing order ID\"}");
            return;
        }

        int orderId = Integer.parseInt(idParam);
        res.setContentType("application/json");

        try (Connection conn = DBUtil.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("DELETE FROM orders WHERE id=?");
            stmt.setInt(1, orderId);
            int rows = stmt.executeUpdate();

            res.getWriter().write("{\"status\":\"Deleted\",\"rows\":" + rows + "}");

        } catch (Exception e) {
            e.printStackTrace();
            res.setStatus(500);
            res.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }
}
