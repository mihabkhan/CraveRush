package Servlet;

import Util.DBUtil;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet(name = "RegisterServlet", urlPatterns = {"/RegisterServlet"})
public class RegisterServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String email = request.getParameter("email");

        System.out.println("Received username: " + username);
        System.out.println("Received password: " + password);
        System.out.println("Received email: " + email);

        try (Connection conn = DBUtil.getConnection()) {
            // SQL to insert new user
            String sql = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.setString(3, email);

            int result = stmt.executeUpdate();

            if (result > 0) {
                System.out.println("User inserted successfully");
                response.sendRedirect("login.jsp?registered=1"); 
            } else {
                System.out.println("Failed to insert user");
                response.sendRedirect("register.jsp?error=1"); 
            }

        } catch (SQLException e) {
            e.printStackTrace();
            response.sendRedirect("register.jsp?error=1"); 
        }
    }
}
