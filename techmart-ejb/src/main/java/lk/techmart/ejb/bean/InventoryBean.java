package lk.techmart.ejb.bean;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lk.techmart.ejb.entity.Inventory;

import java.util.List;

@Stateless
public class InventoryBean {

    @PersistenceContext(unitName = "TechMartPU")
    private EntityManager entityManager;

    public List<Inventory> getAllInventory() {
        return entityManager.createQuery("SELECT i FROM Inventory i", Inventory.class).getResultList();
    }

    public Inventory getInventoryById(String pId) {
        return entityManager.find(Inventory.class, pId);
    }

    public void updateInventory(Inventory inventory) {
        entityManager.merge(inventory);
    }
}
