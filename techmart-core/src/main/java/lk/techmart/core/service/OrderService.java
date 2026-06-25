package lk.techmart.core.service;

import jakarta.ejb.Remote;
import lk.techmart.core.DTO.OrderDTO;

import java.util.List;

@Remote
public interface OrderService {
    OrderDTO getOrderById(String orderId);
    void saveOrder(OrderDTO orderDTO);
    List<OrderDTO> getAllOrders();
}
