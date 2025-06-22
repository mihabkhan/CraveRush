package Servlet;

import java.io.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.*;

import Util.DBUtil;
import javax.servlet.annotation.WebServlet;

@WebServlet("/ProductServlet")
@MultipartConfig
public class ProductServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        Connection conn = null;

        try {
            conn = DBUtil.getConnection();

            if ("Add".equals(action)) {
                String name = request.getParameter("name");
                double price = Double.parseDouble(request.getParameter("price"));
                String description = request.getParameter("description");
                Part imagePart = request.getPart("image");

                String fileName = System.currentTimeMillis() + "_" + imagePart.getSubmittedFileName();
                String uploadDir = getServletContext().getRealPath("/images");
                File dir = new File(uploadDir);
                if (!dir.exists()) dir.mkdir(); 

                String uploadPath = uploadDir + File.separator + fileName;
                imagePart.write(uploadPath);

                String imagePath = "images/" + fileName;

                PreparedStatement stmt = conn.prepareStatement("INSERT INTO products (name, price, description, image_path) VALUES (?, ?, ?, ?)");
                stmt.setString(1, name);
                stmt.setDouble(2, price);
                stmt.setString(3, description);
                stmt.setString(4, imagePath);
                stmt.executeUpdate();
            }

            else if ("Update".equals(action)) {
                int id = Integer.parseInt(request.getParameter("id"));
                String name = request.getParameter("name");
                double price = Double.parseDouble(request.getParameter("price"));
                String description = request.getParameter("description");

                Part imagePart = request.getPart("image");
                PreparedStatement stmt;
                String query;

                if (imagePart != null && imagePart.getSize() > 0) {
                    String fileName = System.currentTimeMillis() + "_" + imagePart.getSubmittedFileName();
                    String uploadDir = getServletContext().getRealPath("/images");
                    File dir = new File(uploadDir);
                    if (!dir.exists()) dir.mkdir();

                    String uploadPath = uploadDir + File.separator + fileName;
                    imagePart.write(uploadPath);

                    String imagePath = "images/" + fileName;

                    query = "UPDATE products SET name=?, price=?, description=?, image_path=? WHERE id=?";
                    stmt = conn.prepareStatement(query);
                    stmt.setString(1, name);
                    stmt.setDouble(2, price);
                    stmt.setString(3, description);
                    stmt.setString(4, imagePath);
                    stmt.setInt(5, id);
                } else {
                    query = "UPDATE products SET name=?, price=?, description=? WHERE id=?";
                    stmt = conn.prepareStatement(query);
                    stmt.setString(1, name);
                    stmt.setDouble(2, price);
                    stmt.setString(3, description);
                    stmt.setInt(4, id);
                }

                stmt.executeUpdate();
            }

            else if ("Delete".equals(action)) {
                int id = Integer.parseInt(request.getParameter("id"));
                PreparedStatement stmt = conn.prepareStatement("DELETE FROM products WHERE id=?");
                stmt.setInt(1, id);
                stmt.executeUpdate();
            }

            response.sendRedirect("updatemenu.jsp");

        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().println("❌ Error: " + e.getMessage());
        }
    }
}
