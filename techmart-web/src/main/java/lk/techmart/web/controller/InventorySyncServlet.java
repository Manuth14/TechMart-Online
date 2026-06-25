package lk.techmart.web.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lk.techmart.web.websocket.InventorySyncWebSocket;
import java.io.IOException;

@WebServlet("/sync-stock-trigger")
public class InventorySyncServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String notification = request.getParameter("notification");
        if (notification != null) {
            // 🔥 අපේ WebSocket එක හරහා හැමෝටම බ්‍රෝඩ්කාස්ට් කරනවා
            lk.techmart.web.websocket.InventorySyncWebSocket.broadcastNotification(notification);
            response.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        String productId = request.getParameter("pid");
        String newStockStr = request.getParameter("stock");

        if (productId != null && newStockStr != null) {
            int newStock = Integer.parseInt(newStockStr);

            // වෙබ් මොඩියුලය ඇතුළෙම නිසා දැන් WebSocket එක කෙලින්ම කෝල් කරන්න පුළුවන්!
            InventorySyncWebSocket.broadcastStockUpdate(productId, newStock);
        }
        response.setStatus(HttpServletResponse.SC_OK);
    }
}