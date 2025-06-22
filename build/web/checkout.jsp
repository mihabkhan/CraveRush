<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <title>Checkout - CraveRush</title>
  <link rel="stylesheet" href="checkout.css">
  
</head>
<body>
  <header>
    <nav>
      <a href="index.html">Home</a>
      <a href="menu.jsp">Menu</a>
      <a href="cart.html">Cart</a>
      <a href="login.jsp">Login</a>
    </nav>
  </header>

  <div class="container">
    <h1>Checkout</h1>

    <% String successMsg = (String) request.getAttribute("success"); %>
    <% if (successMsg != null) { %>
      <div style="padding: 15px; background-color: #dff0d8; color: #3c763d; border-radius: 10px; margin-bottom: 20px;">
        <%= successMsg %>
      </div>
    <% } %>

    <form class="checkout-form" action="PlaceOrderServlet" method="post" onsubmit="return attachCartItems()">
      <label for="name">Username</label>
      <input type="text" id="name" name="customer_name" placeholder="John Doe" required>

      <label for="contact">Contact Number</label>
<input type="tel" id="contact" name="contact" placeholder="123-456-7890" 

      <label for="address">Shipping Address</label>
      <textarea id="address" name="address" rows="4" placeholder="123 Main St, City, Country" required></textarea>

      <label for="payment">Payment Method</label>
      <select id="payment" name="payment_method" required>
        <option value="">Select</option>
        <option value="card">Credit/Debit Card</option>
        <option value="cod">Cash on Delivery</option>
      </select>

      <input type="hidden" id="cart-items" name="items">

      <button type="submit">Place Order</button>
    </form>
  </div>

  <script>
    function attachCartItems() {
      const cart = JSON.parse(localStorage.getItem("cart") || "[]");
      if (cart.length === 0) {
        alert("Your cart is empty!");
        return false;
      }

      document.getElementById("cart-items").value = JSON.stringify(cart);
      return true;
    }
  </script>
</body>
</html>
