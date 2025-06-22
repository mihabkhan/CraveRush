<%@ page import="java.sql.*, Util.DBUtil" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <title>CraveRush Menu</title>
    <link rel="stylesheet" href="menu.css">
    <script src="menu.js" defer></script>
</head>
<body>

<header>
  <nav>
    <div class="nav-center">
      <a href="index.html">Home</a>
      <a href="menu.jsp">Menu</a>
      <a href="cart.html">Cart</a>
      <a href="login.jsp">Login</a>
    </div>
    <form action="LogoutServlet" method="post" class="logout-form">
      <button type="submit">Logout</button>
    </form>
  </nav>
</header>


<h1 style="text-align:center; margin-top: 20px;">Our Delicious Menu</h1>

<div class="menu-section">
<%
    try {
        Connection conn = DBUtil.getConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM products");

        while (rs.next()) {
            String name = rs.getString("name");
            double price = rs.getDouble("price");
            String description = rs.getString("description");
            String imagePath = rs.getString("image_path");
%>
    <div class="product-card">
        <div class="image-frame">
            <img src="<%= imagePath %>" alt="<%= name %>">
        </div>
        <h3><%= name %></h3>
        <p><%= description %></p>
        <p class="price"><strong>Rs. <%= String.format("%.2f", price) %></strong></p>

        <div class="quantity-controls">
            <button onclick="decrease(this)">-</button>
            <span>0</span>
            <button onclick="increase(this)">+</button>
        </div>

        <button class="add-to-cart">Add to Cart 🛒</button>
    </div>
<%
        }
        rs.close();
        stmt.close();
        conn.close();
    } catch (Exception e) {
%>
    <p style="color:red; text-align:center;">❌ Error loading menu: <%= e.getMessage() %></p>
<%
    }
%>
</div>

</body>
</html>
