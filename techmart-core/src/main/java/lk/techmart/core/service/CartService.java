package lk.techmart.core.service;

import jakarta.ejb.Remote;
import lk.techmart.core.DTO.CartItemDTO;

import java.util.List;
import java.util.Map;

@Remote
public interface CartService {
    void setUserEmail(String email);
    boolean addCartItem(String productId, int quantity, double unitPrice);
    void updateCartItem(CartItemDTO cartItemDTO);
    void deleteCartItem(String productId);
    List<CartItemDTO> getAllCartItems();
    void clearCart();
}
