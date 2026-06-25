package lk.techmart.ejb.bean;

import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.jms.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lk.techmart.core.DTO.InventoryDTO;
import lk.techmart.core.DTO.OrderDTO;
import lk.techmart.core.service.InventoryService;
import lk.techmart.core.service.OrderService;
import lk.techmart.ejb.entity.Orders;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.ScheduledMessage;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

@Stateless
public class OrderBean implements OrderService {

    @PersistenceContext(unitName = "TechMartPU")
    private EntityManager entityManager;

    @EJB // 🎯 Interface එක හරහා Inject කරගන්නවා
    private InventoryService inventoryBean;

    @Override
    public void saveOrder(OrderDTO orderDTO) {
        try {
            // 1. Inventory එකෙන් Product DTO එක ගන්නවා
            InventoryDTO inventoryDTO = inventoryBean.getProductById(orderDTO.getProductId());
            if (inventoryDTO == null) {
                throw new RuntimeException("Product Not Found!");
            }

            // 2. ස්ටොක් ඇතිද බලනවා
            if (inventoryDTO.getStockQuantity() < orderDTO.getQuantity()) {
                throw new RuntimeException("Insufficient stock!");
            }

            // 3. ස්ටොක් අඩු කරලා ඩේටාබේස් එක Update කරනවා
            int currentStock = inventoryDTO.getStockQuantity();
            inventoryDTO.setStockQuantity(currentStock - orderDTO.getQuantity());
            inventoryBean.updateInventory(inventoryDTO);

            // 4. Orders Entity එකක් හදලා DB එකට ලියනවා
            Orders entityOrder = new Orders();
            entityOrder.setOrderId(orderDTO.getOrderId());
            entityOrder.setUserEmail(orderDTO.getEmail());
            entityOrder.setProductId(orderDTO.getProductId());
            entityOrder.setQuantity(orderDTO.getQuantity());
            entityManager.persist(entityOrder);

            ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:61616");

            try{
                Connection connection = connectionFactory.createConnection();
                connection.start();

                Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
                Queue queue = session.createQueue("OrderNotificationQueue");

                MessageProducer producer = session.createProducer(queue);
                TextMessage textMessage = session.createTextMessage("Notification Request for Order: " + orderDTO.getOrderId() + " | Customer: " + orderDTO.getEmail());

                producer.send(textMessage);
                System.out.println("📨 JMS Message sent to ActiveMQ Queue!");

            } catch (JMSException e) {
                throw new RuntimeException(e);
            }

            try {
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest httpRequest = HttpRequest.newBuilder()
                        .uri(java.net.URI.create("http://localhost:8080/techmart-web/sync-stock-trigger?pid="
                                + inventoryDTO.getProductId() + "&stock=" + inventoryDTO.getStockQuantity()))
                        .GET()
                        .build();

                // බැක්ග්‍රවුන්ඩ් එකෙන් සර්ව්ලට් එක කෝල් වෙනවා (User ට දැනෙන්නෙවත් නැහැ)
                client.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofString());

            } catch (Exception ex) {
                ex.printStackTrace();
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e); // Rollback වෙන්න අනිවාර්යයි
        }
    }

    @Override
    public OrderDTO getOrderById(String orderId) {
        Orders entityOrder = entityManager.find(Orders.class, orderId);
        if (entityOrder == null) return null;

        OrderDTO dto = new OrderDTO();
        dto.setOrderId(entityOrder.getOrderId());
        dto.setEmail(entityOrder.getUserEmail());
        dto.setProductId(entityOrder.getProductId());
        dto.setQuantity(entityOrder.getQuantity());
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
            dto.setProductId(entityOrder.getProductId());
            dto.setQuantity(entityOrder.getQuantity());
            dtoList.add(dto);
        }
        return dtoList;
    }
}