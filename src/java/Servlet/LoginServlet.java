package Servlet;

import java.io.*;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.sql.*;
import Util.DBUtil;

@WebServlet(name = "LoginServlet", urlPatterns = {"/LoginServlet"})
public class LoginServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String username = request.getParameter("username");
        String password = request.getParameter("password");

        String role = authenticate(username, password); // returns "admin", "user", or null

        if (role != null) {
            HttpSession session = request.getSession();
            session.setAttribute("username", username);
            session.setAttribute("role", role);

            if (role.equals("admin")) {
                response.sendRedirect("new.html"); // Admin page
            } else {
                response.sendRedirect("checkout.jsp"); // Regular user page
            }

        } else {
            response.sendRedirect("login.jsp?error=1"); // Login failed
        }
    }

    private String authenticate(String username, String password) {
        String role = null;

        try (Connection conn = DBUtil.getConnection()) {

            // First check in admins table
            String sqlAdmin = "SELECT * FROM admins WHERE username = ? AND password = ?";
            PreparedStatement stmtAdmin = conn.prepareStatement(sqlAdmin);
            stmtAdmin.setString(1, username);
            stmtAdmin.setString(2, password);
            ResultSet rsAdmin = stmtAdmin.executeQuery();

            if (rsAdmin.next()) {
                role = "admin";
            }

            rsAdmin.close();
            stmtAdmin.close();

            // If not found in admins, check users
            if (role == null) {
                String sqlUser = "SELECT * FROM users WHERE username = ? AND password = ?";
                PreparedStatement stmtUser = conn.prepareStatement(sqlUser);
                stmtUser.setString(1, username);
                stmtUser.setString(2, password);
                ResultSet rsUser = stmtUser.executeQuery();

                if (rsUser.next()) {
                    role = "user";
                }

                rsUser.close();
                stmtUser.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return role; // returns "admin", "user", or null
    }
}
