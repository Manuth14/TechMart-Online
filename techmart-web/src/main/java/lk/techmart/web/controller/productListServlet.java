package lk.techmart.web.controller;

import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lk.techmart.core.DTO.InventoryDTO;
import lk.techmart.core.service.InventoryService;

import java.io.IOException;
import java.util.List;

@WebServlet("/products")
public class productListServlet extends HttpServlet {
    @EJB(lookup = "java:global/techmart-ear-1.0/techmart-ejb/InventoryBean!lk.techmart.core.service.InventoryService")
    private InventoryService inventoryService;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 1. DB එකෙන් pure DTO ලිස්ට් එක ගන්නවා
        List<InventoryDTO> allProducts = inventoryService.getAllProducts();

        // 2. ඒක JSP එකට පාස් කරනවා
        request.setAttribute("products", allProducts);
        request.getRequestDispatcher("all-products.jsp").forward(request, response);
    }
}
