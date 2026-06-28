package lk.techmart.web.controller;

import jakarta.ejb.EJB; // 🎯 මේක අමතක කරන්න එපා
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lk.techmart.core.service.UserService; // 🔗 අලුත් සර්විස් එක ඉම්පෝර්ට් කරගන්න

import java.io.IOException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    // 🔗 දැන් කෙලින්ම යූසර් බීන් එකට කතා කරනවා 🎯
    @EJB
    private UserService userService;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String email = req.getParameter("email");
        String password = req.getParameter("password");

        // 🔍 අලුත් බීන් එක හරහා වැලිඩේට් කරනවා
        boolean isValidUser = userService.validateUser(email, password);

        if (isValidUser) {
            HttpSession session = req.getSession(true);

            // 👤 සෙෂන් එකට ඩේටා දානවා (CartServlet එක පාවිච්චි කරන්නේ මේකයි)
            session.setAttribute("userEmail", email);
            session.setAttribute("loggedInUserName", email.split("@")[0]);

            System.out.println("🔓 User session initialized via UserBean: " + email);
            resp.sendRedirect(req.getContextPath() + "/products");
        } else {
            req.setAttribute("errorMessage", "Invalid Email or Password! Please try again.");
            req.getRequestDispatcher("/index.jsp").forward(req, resp);
        }
    }
}