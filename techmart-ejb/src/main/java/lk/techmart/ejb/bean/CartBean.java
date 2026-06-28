package lk.techmart.ejb.bean;

import jakarta.ejb.EJB;
import jakarta.ejb.Stateful;
import lk.techmart.core.DTO.CartItemDTO;
import lk.techmart.core.DTO.InventoryDTO;
import lk.techmart.core.service.CartService;
import lk.techmart.core.service.InventoryService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Stateful
public class CartBean implements CartService {

    private String userEmail = "Guest";
    private final Map<String, CartItemDTO> cartMap = new HashMap<>();

    @EJB
    private InventoryService inventoryService;

    @Override
    public void setUserEmail(String email) {
        this.userEmail = email;
    }

    @Override
    public boolean addCartItem(String productId, int quantity, double unitPrice) {
        // 1. 🔍 DB එකෙන් ලයිව් ස්ටොක් කියවීම
        InventoryDTO product = inventoryService.getProductById(productId);

        if (product == null) {
            System.out.println("❌ [Cart] Product not found in DB: " + productId);
            return false;
        }

        // 2. 🧮 දැනට කාර්ට් එකේ තියෙන ගණන බැලීම
        int currentQtyInCart = 0;
        if (cartMap.containsKey(productId)) {
            currentQtyInCart = cartMap.get(productId).getQuantity();
        }

        // 3. 🚨 [FIXED] Exception throw කරන්නේ නැත! කෙලින්ම false රිටර්න් කරයි (බීන් එක බේරේ!)
        if ((currentQtyInCart + quantity) > product.getStockQuantity()) {
            System.out.println("⚠️ [Cart] Cannot add! Insufficient stock for " + productId
                    + ". DB Stock: " + product.getStockQuantity() + ", Requested total: " + (currentQtyInCart + quantity));
            return false;
        }

        // 4. ✅ ස්ටොක් ඇති නම් පමණක් කාර්ට් එකට දමයි
        if (cartMap.containsKey(productId)) {
            CartItemDTO existingItem = cartMap.get(productId);
            existingItem.setQuantity(existingItem.getQuantity() + quantity);
        } else {
            cartMap.put(productId, new CartItemDTO(productId, quantity, unitPrice));
        }
        System.out.println("🛒 [Stateful Cart] Successfully added: " + productId + " | Total Cart Qty: " + cartMap.get(productId).getQuantity());
        return true;
    }

    @Override
    public void updateCartItem(CartItemDTO cartItemDTO) {
        // Implementation if needed
    }

    @Override
    public void deleteCartItem(String productId) {
        cartMap.remove(productId);
    }

    @Override
    public List<CartItemDTO> getAllCartItems() {
        return new ArrayList<>(cartMap.values());
    }

    @Override
    public void clearCart() {
        cartMap.clear();
    }
}