package lk.techmart.core.service;

import jakarta.ejb.Remote;
import lk.techmart.core.DTO.InventoryDTO;

import java.util.List;

@Remote
public interface InventoryService {
    InventoryDTO getProductById(String productId);
    void updateInventory(InventoryDTO inventoryDTO);
    boolean reduceStock(String productId, int quantity);
    List<InventoryDTO> getAllProducts();
}
