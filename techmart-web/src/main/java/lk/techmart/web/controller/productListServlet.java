package lk.techmart.web.controller;

import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lk.techmart.core.DTO.InventoryDTO;
import lk.techmart.core.DTO.CartItemDTO;
import lk.techmart.core.service.InventoryService;
import lk.techmart.core.service.CartService;

import java.io.IOException;
import java.util.List;

@WebServlet("/products")
public class productListServlet extends HttpServlet {

    @EJB(lookup = "java:global/techmart-ear-1.0/techmart-ejb/InventoryBean!lk.techmart.core.service.InventoryService")
    private InventoryService inventoryService;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 1. DB එකෙන් pure DTO ලිස්ට් එක ගන්නවා (පරණ කෝඩ් එක)
        List<InventoryDTO> allProducts = inventoryService.getAllProducts();
        request.setAttribute("products", allProducts);

        // 2. 🔐 HTTP Session එක හරහා යූසර්ගේ ස්ටේට් එක හැන්ඩ්ල් කිරීම
        HttpSession session = request.getSession(true);

        // UI එකේ "Welcome, Kusal!" කියලා පෙන්වන්න Login එකේදී සෙට් කරපු නම පාස් කරනවා
        request.setAttribute("loggedInUserName", session.getAttribute("userName"));

        // 3. 🛒 Session එක ඇතුළෙන් මේ යූසර්ට අදාළ Stateful Cart Service එක ගන්නවා
        CartService cart = (CartService) session.getAttribute("userCart");

        if (cart != null) {
            // 🎯 1 වෙනි ක්‍රමය: Bean එකෙන් කෙලින්ම එන්නේ List<CartItemDTO> එකක්
            List<CartItemDTO> cartItems = cart.getAllCartItems();
            request.setAttribute("userCartItems", cartItems);
            System.out.println("🛒 [ProductListServlet] Loaded " + cartItems.size() + " items from Stateful Cart.");
        } else {
            request.setAttribute("userCartItems", null);
        }

        // 4. 🔄 ඔක්කොම ඩේටා ටික අරන් JSP එකට forward කරනවා
        request.getRequestDispatcher("products.jsp").forward(request, response);
    }
}