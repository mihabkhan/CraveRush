package Servlet;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.*;
import java.sql.*;
import Util.DBUtil;

@WebServlet("/api/admin")
public class AdminServlet extends HttpServlet {
    private Gson gson = new Gson();

    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        String idParam = req.getParameter("id");
        res.setContentType("application/json");

        try (Connection conn = DBUtil.getConnection()) {
            if (idParam == null) {
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT id, username, email FROM admins");

                JsonArray admins = new JsonArray();
                while (rs.next()) {
                    JsonObject admin = new JsonObject();
                    admin.addProperty("id", rs.getInt("id"));
                    admin.addProperty("username", rs.getString("username"));
                    admin.addProperty("email", rs.getString("email"));
                    admins.add(admin);
                }

                res.getWriter().write(gson.toJson(admins));
            } else {
                // Fetch single admin
                PreparedStatement stmt = conn.prepareStatement("SELECT * FROM admins WHERE id = ?");
                stmt.setInt(1, Integer.parseInt(idParam));
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    JsonObject admin = new JsonObject();
                    admin.addProperty("id", rs.getInt("id"));
                    admin.addProperty("username", rs.getString("username"));
                    admin.addProperty("email", rs.getString("email"));

                    res.getWriter().write(gson.toJson(admin));
                } else {
                    res.setStatus(404);
                    res.getWriter().write("{\"error\":\"Admin not found\"}");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            res.setStatus(500);
            res.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        JsonObject data = gson.fromJson(req.getReader(), JsonObject.class);

        String username = data.get("username").getAsString();
        String email = data.get("email").getAsString();
        String password = data.get("password").getAsString();  // You should hash passwords in real apps

        try (Connection conn = DBUtil.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("INSERT INTO admins (username, email, password) VALUES (?, ?, ?)");
            stmt.setString(1, username);
            stmt.setString(2, email);
            stmt.setString(3, password);

            int rows = stmt.executeUpdate();
            res.setContentType("application/json");
            res.getWriter().write("{\"status\":\"admin created\",\"rows\":" + rows + "}");

        } catch (SQLIntegrityConstraintViolationException e) {
            res.setStatus(409);
            res.getWriter().write("{\"error\":\"Username or email already exists\"}");
        } catch (Exception e) {
            e.printStackTrace();
            res.setStatus(500);
            res.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    protected void doPut(HttpServletRequest req, HttpServletResponse res) throws IOException {
        JsonObject data = gson.fromJson(req.getReader(), JsonObject.class);
        int id = data.get("id").getAsInt();
        String email = data.get("email").getAsString();

        try (Connection conn = DBUtil.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("UPDATE admins SET email = ? WHERE id = ?");
            stmt.setString(1, email);
            stmt.setInt(2, id);

            int rows = stmt.executeUpdate();
            res.setContentType("application/json");
            res.getWriter().write("{\"status\":\"admin updated\",\"rows\":" + rows + "}");

        } catch (Exception e) {
            e.printStackTrace();
            res.setStatus(500);
            res.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    protected void doDelete(HttpServletRequest req, HttpServletResponse res) throws IOException {
        String idParam = req.getParameter("id");
        res.setContentType("application/json");

        if (idParam == null) {
            res.setStatus(400);
            res.getWriter().write("{\"error\":\"Missing admin ID\"}");
            return;
        }

        try (Connection conn = DBUtil.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("DELETE FROM admins WHERE id = ?");
            stmt.setInt(1, Integer.parseInt(idParam));

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                res.getWriter().write("{\"status\":\"admin deleted\",\"rows\":" + rows + "}");
            } else {
                res.setStatus(404);
                res.getWriter().write("{\"error\":\"Admin not found\"}");
            }

        } catch (Exception e) {
            e.printStackTrace();
            res.setStatus(500);
            res.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }
}
