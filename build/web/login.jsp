<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <title>Login - CraveRush</title>
  <link rel="stylesheet" href="Login.css">
</head>
<body>

  <header>
    <h1>Login to CraveRush</h1>
    <nav>
      <a href="index.html">Home</a>
      <a href="menu.jsp">Menu</a>
      <a href="login.jsp">Login</a>
     
    </nav>
  </header>

  <section class="login-section">
    <form action="LoginServlet" method="post">
      <h2>User Login</h2>

      <% 
        String error = request.getParameter("error");
        if ("1".equals(error)) {
      %>
        <p style="color: red; font-weight: bold;">Invalid username or password.</p>
      <% } %>

      <div class="form-group">
        <label for="username">Username:</label>
        <input type="text" id="username" name="username" required>
      </div>

      <div class="form-group">
        <label for="password">Password:</label>
        <input type="password" id="password" name="password" required>
      </div>

      <button type="submit" class="login-btn">Login</button>

      <div class="register-link">
        <p>Don't have an account? <a href="register.jsp">Register here</a></p>
      </div>
    </form>
  </section>

  <script src="script.js"></script>
</body>
</html>
