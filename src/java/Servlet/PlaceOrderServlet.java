package Servlet;

import Util.DBUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.lang.reflect.Type;
import java.sql.*;
import java.util.List;
import java.util.Map;

@WebServlet(name = "PlaceOrderServlet", urlPatterns = {"/PlaceOrderServlet"})
public class PlaceOrderServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String customerName = request.getParameter("customer_name");
        String contact = request.getParameter("contact");
        if (contact == null || !contact.matches("^\\d{10}$")) {
    request.setAttribute("success", "❌ Invalid contact number. Please enter a valid 10-digit number.");
    RequestDispatcher rd = request.getRequestDispatcher("checkout.jsp");
    rd.forward(request, response);
    return;
}

        String address = request.getParameter("address");
        String paymentMethod = request.getParameter("payment_method");
        String itemsJson = request.getParameter("items");  
        String status = "In Progress";

        try (Connection conn = DBUtil.getConnection()) {
            conn.setAutoCommit(false);  

            String orderSql = "INSERT INTO orders (customer_name, contact, address, payment_method, status) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement orderStmt = conn.prepareStatement(orderSql, Statement.RETURN_GENERATED_KEYS);
            orderStmt.setString(1, customerName);
            orderStmt.setString(2, contact);
            orderStmt.setString(3, address);
            orderStmt.setString(4, paymentMethod);
            orderStmt.setString(5, status);
            orderStmt.executeUpdate();

            ResultSet rs = orderStmt.getGeneratedKeys();
            int orderId = -1;
            if (rs.next()) {
                orderId = rs.getInt(1);
            }
            rs.close();
            orderStmt.close();

            Gson gson = new Gson();
            Type listType = new TypeToken<List<Map<String, Object>>>() {}.getType();
            List<Map<String, Object>> cartItems = gson.fromJson(itemsJson, listType);

            String itemSql = "INSERT INTO order_items (order_id, item_name, quantity, price) VALUES (?, ?, ?, ?)";
            PreparedStatement itemStmt = conn.prepareStatement(itemSql);

            for (Map<String, Object> item : cartItems) {
                String name = (String) item.get("name");
                int qty = ((Double) item.get("quantity")).intValue();
                double price = ((Double) item.get("price")).doubleValue();

                itemStmt.setInt(1, orderId);
                itemStmt.setString(2, name);
                itemStmt.setInt(3, qty);
                itemStmt.setDouble(4, price);
                itemStmt.addBatch();
            }

            itemStmt.executeBatch();
            itemStmt.close();

            conn.commit(); 

            request.setAttribute("success", "✅ Order Confirmed! Thank you, " + customerName + " 🎉");

        } catch (Exception e) {
            request.setAttribute("success", "❌ Error placing order: " + e.getMessage());
            e.printStackTrace();
        }

        RequestDispatcher rd = request.getRequestDispatcher("checkout.jsp");
        rd.forward(request, response);
    }
}
