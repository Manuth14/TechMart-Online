package lk.techmart.web.controller;

import lk.techmart.ejb.bean.UserBean;
import lk.techmart.ejb.entity.Users;
import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/test-users") // බ්‍රවුසර් එකෙන් යන්න ඕන URL එක
public class TestUserServlet extends HttpServlet {

    @EJB
    private UserBean userBean; // අර හදපු EJB එක Inject කරගන්නවා

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            out.println("<h2>TechMart - User Database Load Test</h2>");

            // Bean එක හරහා ඩේටාබේස් එකෙන් ලිස්ට් එක ගන්නවා
            List<Users> users = userBean.getAllUsers();

            if (users.isEmpty()) {
                out.println("<p style='color:red;'>Oops! No users found in database.</p>");
            } else {
                out.println("<table border='1'><tr><th>Email</th><th>Name</th><th>Role</th></tr>");
                for (Users u : users) {
                    out.println("<tr><td>" + u.getEmail() + "</td><td>" + u.getName() + "</td><td>" + u.getRole() + "</td></tr>");
                }
                out.println("</table>");
                out.println("<p style='color:green;'><b>Success: Data loaded perfectly from MySQL!</b></p>");
            }
        }
    }
}