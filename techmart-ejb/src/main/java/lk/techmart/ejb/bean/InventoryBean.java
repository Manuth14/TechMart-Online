package lk.techmart.ejb.bean;

import jakarta.ejb.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lk.techmart.core.DTO.InventoryDTO;
import lk.techmart.core.service.InventoryService;
import lk.techmart.ejb.entity.Inventory;

import java.util.ArrayList;
import java.util.List;

@Stateless
public class InventoryBean implements InventoryService {

    @PersistenceContext(unitName = "TechMartPU")
    private EntityManager entityManager;

    @Override
    public InventoryDTO getProductById(String productId) {
        Inventory entity = entityManager.find(Inventory.class, productId);
        if (entity == null) return null;

        return new InventoryDTO(entity.getProductId(), entity.getStockQuantity());
    }

    @Override
    public void updateInventory(InventoryDTO inventoryDTO) {
        Inventory entity = entityManager.find(Inventory.class, inventoryDTO.getProductId());
        if (entity != null) {
            // DTO එකෙන් එන අලුත් Qty එක Entity එකට දානවා
            entity.setStockQuantity(inventoryDTO.getStockQuantity());
            entityManager.merge(entity); // DB එකට සේව් වෙනවා
        }
    }

    @Override
    public List<InventoryDTO> getAllProducts() {
        List<Inventory> entityList = entityManager.createQuery("SELECT i FROM Inventory i", Inventory.class).getResultList();
        List<InventoryDTO> dtoList = new ArrayList<>();

        for (Inventory entity : entityList) {
            dtoList.add(new InventoryDTO(entity.getProductId(), entity.getStockQuantity()));
        }
        return dtoList;
    }
}
