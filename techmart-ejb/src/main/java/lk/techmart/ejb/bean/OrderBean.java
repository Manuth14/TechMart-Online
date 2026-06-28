package lk.techmart.ejb.bean;

import jakarta.ejb.*;
import jakarta.jms.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lk.techmart.core.DTO.CartItemDTO;
import lk.techmart.core.DTO.InventoryDTO;
import lk.techmart.core.DTO.OrderDTO;
import lk.techmart.core.service.InventoryService;
import lk.techmart.core.service.InvoiceService;
import lk.techmart.core.service.OrderService;
import lk.techmart.ejb.entity.Orders;
import org.apache.activemq.ActiveMQConnectionFactory;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

@Stateless
public class OrderBean implements OrderService {

    @PersistenceContext(unitName = "TechMartPU")
    private EntityManager entityManager;

    @EJB
    private InventoryService inventoryBean;

    @EJB
    private InvoiceService invoiceService;

    // ===================================================================
    // 🔥 1. AUTOMATED ASYNCHRONOUS CART-BASED ORDER PROCESSING PIPELINE
    // ===================================================================
    @Override
    @Asynchronous // 🎯 සර්වර් එකේ background thread pool එකෙන් දුවන්න සෙට් කළා
    @TransactionAttribute(TransactionAttributeType.REQUIRED) // 🛡️ එකක් හරි ෆේල් වුණොත් මුළු Parent/Child ටේබල් දෙකම Rollback වෙයි
    public void processOrderAsync(List<CartItemDTO> items, String userEmail) {
        System.out.println("⏳ [Async Cart Order] Pipeline started in background for: " + userEmail);

        try {
            // A. 🆔 මුළු කාර්ට් එකටම පොදු Unique Master Order ID එකක් සාදා ගැනීම
            String mainOrderId = "ORD-" + System.currentTimeMillis() + "-" + (int)(Math.random() * 1000);

            // B. 📑 Parent Record: 'orders' ටේබල් එකට මුලින්ම එක Row එකක් සේව් කරනවා
            Orders entityOrder = new Orders();
            entityOrder.setOrderId(mainOrderId);
            entityOrder.setUserEmail(userEmail);
            // entityOrder.setStatus("PROCESSED"); // ඔයාගේ Entity එකේ status/date තිබේ නම් සෙට් කරන්න මල්ලි

            entityManager.persist(entityOrder);
            System.out.println("💾 [Async Order] Master Order record saved. ID: " + mainOrderId);

            // C. 🛍️ Child Records: කාර්ට් එකේ තියෙන හැම අයිටම් එකක්ම ලූප් කරලා 'order_items' එකට දානවා
            for (CartItemDTO item : items) {

                // ⚡ i. DB Atomic Stock Update (Race Condition එක ඩේටාබේස් ලෙවල් එකෙන්ම බ්ලොක් කරයි)
                boolean isReduced = inventoryBean.reduceStock(item.getProductId(), item.getQuantity());

                if (!isReduced) {
                    // ස්ටොක් මදි නම් RuntimeException එකක් ගහනවා.
                    // එතකොට උඩ දාපු Parent Order එකත් එක්කම මුළු ට්‍රාන්සැක්ෂන් එකම ඩේටාබේස් එකෙන් Rollback වෙනවා! 🛡️
                    throw new RuntimeException("Insufficient stock for Product ID: " + item.getProductId() + ". Transaction Rollbacked!");
                }

                // 💾 ii. OrderItems Entity එකක් හදලා DB එකට ලියනවා
                OrderDTO entityItem = new OrderDTO();
                entityItem.setOrderId(mainOrderId); // 🔗 Parent Order එකට ලින්ක් කළා
                entityItem.setProductId(item.getProductId());
                entityItem.setQuantity(item.getQuantity());
                // entityItem.setUnitPrice(item.getPrice()); // ඔයාගේ Entity එකේ මිල සේව් කරන්න ෆීල්ඩ් එකක් ඇති නම් දාන්න

                entityManager.persist(entityItem);

                // 📨 iii. JMS Message Production to ActiveMQ Queue
                triggerJMSNotification(mainOrderId, userEmail);

                // 🔄 iv. Live Stock Sync to Frontend
                triggerLiveStockSync(item.getProductId());
            }

            System.out.println("🚀 Triggering Asynchronous Invoice & Email Process...");

            // ⚡ D. Invoice Generator Thread එක රන් කිරීම
            java.util.concurrent.Future<Boolean> asyncResult = invoiceService.generateInvoiceAndEmail(mainOrderId, userEmail);

            System.out.println("✅ [Async Cart Order] Complete batch processing finished successfully for: " + userEmail);

        } catch (Exception e) {
            System.err.println("🚨 [Async Order FAILED] Transaction Rollback triggered: " + e.getMessage());
            throw new RuntimeException(e); // 🛑 Container-Managed Transaction එක නිසා ටේබල් දෙකම ඔටෝම Rollback වෙනවා
        }
    }

    // ===================================================================
    // 💾 2. SINGLE ORDER SAVE (පරණ මෙතඩ් එක - BACKWARD COMPATIBILITY)
    // ===================================================================
    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void saveOrder(OrderDTO orderDTO) {
        // තනි අයිටම් එකක් ආවොත්, ඒකත් අපේ ප්‍රධාන පයිප්ලයින් එකටම ලිස්ට් එකක් විදිහට හරවලා යවනවා මල්ලි
        List<CartItemDTO> singleItemList = new ArrayList<>();
        CartItemDTO item = new CartItemDTO();
        item.setProductId(orderDTO.getProductId());
        item.setQuantity(orderDTO.getQuantity());
        singleItemList.add(item);

        this.processOrderAsync(singleItemList, orderDTO.getEmail());
    }

    // ===================================================================
    // 🔍 HELPER METHODS FOR JMS & STOCK SYNC
    // ===================================================================
    private void triggerJMSNotification(String orderId, String email) {
        try {
            ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:61616");
            Connection connection = connectionFactory.createConnection();
            connection.start();

            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Queue queue = session.createQueue("OrderNotificationQueue");

            MessageProducer producer = session.createProducer(queue);
            TextMessage textMessage = session.createTextMessage("Notification Request for Order: " + orderId + " | Customer: " + email);

            producer.send(textMessage);
            System.out.println("📨 JMS Message sent to ActiveMQ Queue!");
            connection.close();
        } catch (JMSException e) {
            System.err.println("❌ JMS Error: " + e.getMessage());
        }
    }

    private void triggerLiveStockSync(String productId) {
        try {
            InventoryDTO updatedInventory = inventoryBean.getProductById(productId);
            if (updatedInventory != null) {
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest httpRequest = HttpRequest.newBuilder()
                        .uri(java.net.URI.create("http://localhost:8080/techmart-web/sync-stock-trigger?pid="
                                + updatedInventory.getProductId() + "&stock=" + updatedInventory.getStockQuantity()))
                        .GET()
                        .build();

                client.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofString());
            }
        } catch (Exception ex) {
            System.err.println("❌ Sync Stock Trigger Failed: " + ex.getMessage());
        }
    }

    // ===================================================================
    // 🔍 FETCH LOOKUP METHODS
    // ===================================================================
    @Override
    public OrderDTO getOrderById(String orderId) {
        Orders entityOrder = entityManager.find(Orders.class, orderId);
        if (entityOrder == null) return null;

        OrderDTO dto = new OrderDTO();
        dto.setOrderId(entityOrder.getOrderId());
        dto.setEmail(entityOrder.getUserEmail());
        // සටහන: ටේබල් දෙකක් නිසා Product/Qty විස්තර එන්නේ order_items එකෙන්.
        // දැනට පරණ ඩීටීඕ එක ක්‍රෑෂ් නොවී දුවන්න මෙතන මෙහෙම තිබ්බාම ඇති මල්ලි.
        return dto;
    }

    @Override
    public List<OrderDTO> getAllOrders() {
        List<Orders> entityList = entityManager.createQuery("SELECT o FROM Orders o", Orders.class).getResultList();
        List<OrderDTO> dtoList = new ArrayList<>();

        for (Orders entityOrder : entityList) {
            OrderDTO dto = new OrderDTO();
            dto.setOrderId(entityOrder.getOrderId());
            dto.setEmail(entityOrder.getUserEmail());
            dtoList.add(dto);
        }
        return dtoList;
    }
}