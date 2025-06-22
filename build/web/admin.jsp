<%@ page import="java.sql.*, Util.DBUtil" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Admin Panel - CraveRush</title>
    <link rel="stylesheet" href="admin.css">
</head>
<body>
    <a href="updatemenu.jsp">Update Menu</a>
    <a href="new.html">New</a>

    <h1>🛒 Admin Panel - Manage Orders</h1>

    <table border="1" cellpadding="10">
        <tr>
            <th>Order ID</th>
            <th>Customer</th>
            <th>Items</th>
            <th>Status</th>
            <th>Change Status</th>
        </tr>

        <%
            try {
                Connection conn = DBUtil.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT * FROM orders");

                while (rs.next()) {
                    int orderId = rs.getInt("id");
                    String customer = rs.getString("customer_name");
                    String status = rs.getString("status");

                    PreparedStatement itemsStmt = conn.prepareStatement(
                        "SELECT item_name, quantity FROM order_items WHERE order_id = ?"
                    );
                    itemsStmt.setInt(1, orderId);
                    ResultSet itemsRs = itemsStmt.executeQuery();

                    StringBuilder itemList = new StringBuilder();
                    while (itemsRs.next()) {
                        itemList.append(itemsRs.getString("item_name"))
                                .append(" (x").append(itemsRs.getInt("quantity")).append(")<br>");
                    }

                    itemsRs.close();
                    itemsStmt.close();
        %>
        <tr>
            <td><%= orderId %></td>
            <td><%= customer %></td>
            <td><%= itemList.toString() %></td>
            <td><%= status %></td>
            <td>
                <form action="UpdateOrderStatusServlet" method="post">
                    <input type="hidden" name="order_id" value="<%= orderId %>" />
                    <select name="new_status" required>
                        <option value="Pending" <%= "Pending".equalsIgnoreCase(status) ? "selected" : "" %>>Pending</option>
                        <option value="In Progress" <%= "In Progress".equalsIgnoreCase(status) ? "selected" : "" %>>In Progress</option>
                        <option value="Delivered" <%= "Delivered".equalsIgnoreCase(status) ? "selected" : "" %>>Delivered</option>
                        <option value="Cancelled" <%= "Cancelled".equalsIgnoreCase(status) ? "selected" : "" %>>Cancelled</option>
                    </select>
                    <input type="submit" value="Update" />
                </form>
            </td>
        </tr>
        <%
                }

                rs.close();
                stmt.close();
                conn.close();
            } catch (Exception e) {
                out.println("❌ Error: " + e.getMessage());
            }
        %>
    </table>
</body>
</html>
