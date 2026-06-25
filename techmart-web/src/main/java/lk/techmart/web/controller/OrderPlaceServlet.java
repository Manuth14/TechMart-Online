package lk.techmart.web.controller;

import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lk.techmart.core.DTO.OrderDTO;
import lk.techmart.core.service.OrderService;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.UUID;

@WebServlet("/place-order")
public class OrderPlaceServlet extends HttpServlet {

    @EJB(lookup = "java:global/techmart-ear-1.0/techmart-ejb/OrderBean!lk.techmart.core.service.OrderService")
    private OrderService orderService;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        // Response එක plain text එකක් විදිහට සෙට් කරනවා (AJAX එකට ලේසි වෙන්න)
        response.setContentType("text/plain");
        PrintWriter out = response.getWriter();

        String email = request.getParameter("email");
        String productId = request.getParameter("pid");
        String qtyParam = request.getParameter("qty");

        if (email == null || productId == null || qtyParam == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST); // HTTP 400
            out.print("ERROR: Missing Parameters");
            return;
        }

        try {
            int qty = Integer.parseInt(qtyParam);
            String orderId = "ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

            OrderDTO orderDTO = new OrderDTO(orderId, email, productId, qty);

            // 🎯 1. Core Service එක හරහා ඩේටාබේස් එකට සේව් කරනවා
            // (මේක ඇතුළෙන්ම ස්ටොක් අඩු වෙලා WebSocket Broadcast එක සිද්ධ වෙනවා)
            orderService.saveOrder(orderDTO);

            // 🎯 2. හැමදේම හරි නම් බ්‍රවුසර් එකේ fetch එකට "SUCCESS" කියලා යවනවා
            response.setStatus(HttpServletResponse.SC_OK); // HTTP 200
            out.print("SUCCESS");

        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); // HTTP 500
            out.print("ERROR: " + e.getMessage());
        }
    }
}