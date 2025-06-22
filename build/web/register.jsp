<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <title>Register - CraveRush</title>
  <link rel="stylesheet" href="register.css">
</head>
<body>

  <header>
    <h1>Register at CraveRush</h1>
    <nav>
      <a href="index.html">Home</a>
      <a href="menu.jsp">Menu</a>
      <a href="order.html">Order</a>
      <a href="login.jsp">Login</a>
      <a href="checkout.html">CheckOut</a>
    </nav>
  </header>

  <section class="register-section">

      <%-- Show error message if registration failed --%>
      <%
        String error = request.getParameter("error");
        String registered = request.getParameter("registered");
        if (error != null) {
      %>
        <p style="color: red;">Registration failed. Please try again.</p>
      <% } else if (registered != null) { %>
        <p style="color: green;">Registration successful! You can now log in.</p>
      <% } %>
    <form action="RegisterServlet" method="post">

      <div class="form-group">
        <label for="username">Username:</label>
        <input type="text" id="username" name="username" required>
      </div>

      <div class="form-group">
        <label for="password">Password:</label>
        <input type="password" id="password" name="password" required>
      </div>

      <div class="form-group">
        <label for="email">Email:</label>
        <input type="email" id="email" name="email" required>
      </div>

      <button type="submit" class="register-btn">Register</button>

      <div class="login-link">
        <p>Already have an account? <a href="login.jsp">Login here</a></p>
      </div>
    </form>
  </section>

</body>
</html>
