<%@ page import="java.sql.*, Util.DBUtil" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<html>
<head>
    <title>Admin Panel - Update Menu</title>
    <link rel="stylesheet" href="admin.css">
</head>
<body>
    <a href="menu.jsp">Menu</a>

    <h1>Admin Panel - Update Menu</h1>

    <h2>Add New Product</h2>
    <form action="ProductServlet" method="post" enctype="multipart/form-data">
        <input type="hidden" name="action" value="Add" />
        Name: <input type="text" name="name" required /><br>
        Price: <input type="text" name="price" required /><br>
        Description: <input type="text" name="description" required /><br>
        Image: <input type="file" name="image" accept="image/*" required /><br>
        <input type="submit" value="Add Product" />
    </form>

    <h2>Current Products</h2>
    <table border="1" cellpadding="10">
        <tr>
            <th>ID</th>
            <th>Name</th>
            <th>Price</th>
            <th>Description</th>
            <th>Image</th>
            <th>Actions</th>
        </tr>
        <%
            try {
                Connection conn = DBUtil.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT * FROM products");

                while (rs.next()) {
                    int id = rs.getInt("id");
                    String name = rs.getString("name");
                    double price = rs.getDouble("price");
                    String description = rs.getString("description");
                    String imagePath = rs.getString("image_path");
        %>
        <tr>
            <form action="ProductServlet" method="post" enctype="multipart/form-data">
                <td><%= id %></td>
                <td><input type="text" name="name" value="<%= name %>" /></td>
                <td><input type="text" name="price" value="<%= price %>" /></td>
                <td><input type="text" name="description" value="<%= description %>" /></td>
                <td>
                    <img src="<%= imagePath %>" width="80" height="80" alt="Product Image" /><br>
                    <input type="file" name="image" accept="image/*" />
                </td>
                <td>
                    <input type="hidden" name="id" value="<%= id %>" />
                    <input type="submit" name="action" value="Update" /><br><br>
                    <input type="submit" name="action" value="Delete" onclick="return confirm('Are you sure?');" />
                </td>
            </form>
        </tr>
        <%
                }
                rs.close();
                stmt.close();
                conn.close();
            } catch (Exception e) {
                out.println("Error: " + e.getMessage());
            }
        %>
    </table>
</body>
</html>
