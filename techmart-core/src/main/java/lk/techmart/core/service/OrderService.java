package lk.techmart.core.service;

import jakarta.ejb.Remote;
import lk.techmart.core.DTO.OrderDTO;
import lk.techmart.core.DTO.CartItemDTO; // 🎯 මේක ඉම්පෝර්ට් කරගන්න

import java.util.List;

@Remote
public interface OrderService {
    OrderDTO getOrderById(String orderId);
    void saveOrder(OrderDTO orderDTO);
    List<OrderDTO> getAllOrders();
    void processOrderAsync(List<CartItemDTO> items, String userEmail);
}