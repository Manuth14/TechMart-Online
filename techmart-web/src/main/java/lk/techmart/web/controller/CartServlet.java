package lk.techmart.web.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lk.techmart.core.DTO.CartItemDTO;
import lk.techmart.core.service.CartService;
import lk.techmart.core.service.OrderService; // 🎯 අලුතෙන් ඉම්පෝර්ට් කරගන්න

import javax.naming.InitialContext;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

@WebServlet("/cart-action")
public class CartServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(true);

        // 1. 🎯 HTTP Session එකෙන් Stateful ShoppingCart Bean Reference එක ගැනීම
        CartService cart = (CartService) session.getAttribute("userCart");

        if (cart == null) {
            try {
                // 🔥 JNDI Lookup එක හැම සෙෂන් එකකටම වෙන වෙනම අලුත් Stateful Instance එකක් නිපදවීමට
                Properties props = new Properties();
                InitialContext ctx = new InitialContext(props);

                // 🔗 Payara Portable JNDI Name
                cart = (CartService) ctx.lookup("java:global/techmart-ear-1.0/techmart-ejb/CartBean!lk.techmart.core.service.CartService");

                // 2. 🎯 ලොගින් වෙලා ඉන්න යූසර්ගේ ඊමේල් එක අරන් Stateful Bean එකට දෙනවා
                String loggedInUser = (String) session.getAttribute("userEmail");
                if (loggedInUser != null) {
                    cart.setUserEmail(loggedInUser);
                }

                session.setAttribute("userCart", cart);
                System.out.println("📦 [JNDI FIXED] Created a strictly UNIQUE Stateful Cart for Session ID: " + session.getId());
            } catch (Exception e) {
                throw new ServletException("JNDI Lookup Failed for ShoppingCartBean", e);
            }
        }

        // 2. 🎯 Request Parameters කියවීම
        String action = req.getParameter("action");
        String pid = req.getParameter("pid");

        // ==========================================
        // 🛒 CASE 1: ADD ITEM TO CART (AJAX FLOW 🚀)
        // ==========================================
        if ("add".equals(action) && pid != null) {
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");

            try {
                double price = Double.parseDouble(req.getParameter("price"));
                boolean isAdded = cart.addCartItem(pid, 1, price);

                if (isAdded) {
                    resp.setStatus(HttpServletResponse.SC_OK);
                    resp.getWriter().write("{\"status\":\"success\"}");
                } else {
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    resp.getWriter().write("{\"status\":\"error\", \"message\":\"Not enough stock!\"}");
                }

            } catch (Exception e) {
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                resp.getWriter().write("{\"status\":\"error\", \"message\":\"Server error occurred\"}");
            }
            return;
        }

        // ==========================================
        // ❌ CASE 2: REMOVE ITEM FROM CART
        // ==========================================
        else if ("remove".equals(action) && pid != null) {
            cart.deleteCartItem(pid);
            System.out.println("❌ Removed item from cart: " + pid);
            resp.sendRedirect(req.getContextPath() + "/products");
        }

        // ==========================================
        // 🧹 CASE 3: CLEAR ENTIRE CART
        // ==========================================
        else if ("clear".equals(action)) {
            cart.clearCart();
            session.removeAttribute("userCart");
            System.out.println("🧹 Cleared entire cart.");
            resp.sendRedirect(req.getContextPath() + "/products");
        }

        // ==========================================
        // 💳 CASE 4: AUTOMATED ASYNCHRONOUS CHECKOUT pipeline 🚀
        // ==========================================
        else if ("checkout".equals(action)) {
            List<CartItemDTO> itemsInCart = cart.getAllCartItems();
            String loggedInUser = (String) session.getAttribute("userEmail");

            if (itemsInCart == null || itemsInCart.isEmpty()) {
                resp.sendRedirect(req.getContextPath() + "/products");
                return;
            }

            try {
                InitialContext ctx = new InitialContext();
                // 🔍 [FIX] සර්ව්ලට් එකෙන් කෙලින්ම ඉන්වෙන්ටරි බැලීම වෙනුවට, අපේ සම්පූර්ණ ඕඩර් පයිප්ලයින් එක තියෙන OrderBean එක lookup කරනවා
                OrderService orderService = (OrderService) ctx.lookup("java:global/techmart-ear-1.0/techmart-ejb/OrderBean!lk.techmart.core.service.OrderService");

                // 🔥 [💥 ASYNCHRONOUS EXECUTION]
                // මුළු කාර්ට් ලිස්ට් එකම OrderBean එකේ තියෙන `@Asynchronous` මෙතඩ් එකට බාර දෙනවා.
                // ඩේටාබේස් රෝ-ලොක් අප්ඩේට්, JPA Persistence, ActiveMQ මැසේජ්, සහ වෙබ්සොකට් ට්‍රිගර් ඔක්කොම
                // සර්වර් එකේ බැක්ග්‍රවුන්ඩ් Thread එකකින් සිද්ද වෙන්න පටන් ගන්නවා.
                // සර්ව්ලට් එක මේ ලයින් එකෙන් බ්ලොක් වෙන්නේ නැතුව සැනින් ඊළඟ පියවරට පනිනවා!
                orderService.processOrderAsync(itemsInCart, loggedInUser != null ? loggedInUser : "guest@techmart.com");

                // 🧹 වැඩේ බැක්ග්‍රවුන්ඩ් එකට දුන්න ගමන් සෙෂන් එකේ තියෙන කාර්ට් එක ක්ලීන් කරලා දානවා
                cart.clearCart();
                session.removeAttribute("userCart");

                System.out.println("🚀 [Servlet Thread Free] Cart pipeline handed over to OrderBean Async Thread pool successfully!");

                // UI එකට 'async_started' පරාමිතිය යවනවා HTML5 Notification එකක් ට්‍රිගර් කරගන්න 🔔
                resp.sendRedirect(req.getContextPath() + "/products?success=async_started");

            } catch (Exception e) {
                System.err.println("❌ EJB Lookup or Processing Failed during Checkout: " + e.getMessage());
                e.printStackTrace();
                // මොකක් හරි JNDI කේස් එකක් ආවොත් එරර් පේජ් එකට හරවනවා
                resp.sendRedirect(req.getContextPath() + "/products?error=checkout_failed");
            }
        }
    }
}