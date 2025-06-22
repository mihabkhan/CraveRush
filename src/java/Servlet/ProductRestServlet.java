package Servlet;

import Util.DBUtil;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.*;
import java.sql.*;

@WebServlet("/api/products")
public class ProductRestServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        Connection conn = null;

        try {
            conn = DBUtil.getConnection();
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM products");
            ResultSet rs = stmt.executeQuery();

            PrintWriter out = response.getWriter();
            out.print("[");
            boolean first = true;

            while (rs.next()) {
                if (!first) out.print(",");
                out.print("{");
                out.print("\"id\":" + rs.getInt("id") + ",");
                out.print("\"name\":\"" + rs.getString("name") + "\",");
                out.print("\"price\":" + rs.getDouble("price") + ",");
                out.print("\"description\":\"" + rs.getString("description") + "\",");
                out.print("\"image_path\":\"" + rs.getString("image_path") + "\"");
                out.print("}");
                first = false;
            }
            out.print("]");
        } catch (Exception e) {
            response.setStatus(500);
            response.getWriter().println("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // Expecting JSON input
        BufferedReader reader = request.getReader();
        StringBuilder sb = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) sb.append(line);

        try (Connection conn = DBUtil.getConnection()) {
            String json = sb.toString();

            // Simple JSON parsing (no external libs)
            String name = json.split("\"name\":\"")[1].split("\"")[0];
            String description = json.split("\"description\":\"")[1].split("\"")[0];
            double price = Double.parseDouble(json.split("\"price\":")[1].split(",|}")[0]);

            PreparedStatement stmt = conn.prepareStatement(
                    "INSERT INTO products (name, price, description, image_path) VALUES (?, ?, ?, ?)"
            );
            stmt.setString(1, name);
            stmt.setDouble(2, price);
            stmt.setString(3, description);
            stmt.setString(4, "images/default.png");
            stmt.executeUpdate();

            response.setStatus(201);
            response.getWriter().println("{\"message\":\"Product added\"}");
        } catch (Exception e) {
            response.setStatus(500);
            response.getWriter().println("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        BufferedReader reader = request.getReader();
        StringBuilder sb = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) sb.append(line);

        try (Connection conn = DBUtil.getConnection()) {
            String json = sb.toString();

            int id = Integer.parseInt(json.split("\"id\":")[1].split(",")[0]);
            String name = json.split("\"name\":\"")[1].split("\"")[0];
            String description = json.split("\"description\":\"")[1].split("\"")[0];
            double price = Double.parseDouble(json.split("\"price\":")[1].split(",|}")[0]);

            PreparedStatement stmt = conn.prepareStatement(
                    "UPDATE products SET name=?, price=?, description=? WHERE id=?"
            );
            stmt.setString(1, name);
            stmt.setDouble(2, price);
            stmt.setString(3, description);
            stmt.setInt(4, id);
            int rows = stmt.executeUpdate();

            if (rows > 0) {
                response.getWriter().println("{\"message\":\"Product updated\"}");
            } else {
                response.setStatus(404);
                response.getWriter().println("{\"error\":\"Product not found\"}");
            }
        } catch (Exception e) {
            response.setStatus(500);
            response.getWriter().println("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        BufferedReader reader = request.getReader();
        StringBuilder sb = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) sb.append(line);

        try (Connection conn = DBUtil.getConnection()) {
            String json = sb.toString();
            int id = Integer.parseInt(json.split("\"id\":")[1].split("}")[0]);

            PreparedStatement stmt = conn.prepareStatement("DELETE FROM products WHERE id=?");
            stmt.setInt(1, id);
            int rows = stmt.executeUpdate();

            if (rows > 0) {
                response.getWriter().println("{\"message\":\"Product deleted\"}");
            } else {
                response.setStatus(404);
                response.getWriter().println("{\"error\":\"Product not found\"}");
            }
        } catch (Exception e) {
            response.setStatus(500);
            response.getWriter().println("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }
}
