package lk.techmart.core.service;

import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import lk.techmart.ejb.bean.InventoryBean;
import lk.techmart.ejb.bean.OrderBean;
import lk.techmart.ejb.entity.Inventory;
import lk.techmart.ejb.entity.Orders;

@Stateless
public class OrderService {

    @EJB
    private InventoryBean inventoryBean;

    @EJB
    private OrderBean orderBean;

    public boolean orderProcess(String orderId, String email, String pId, int quantity) {
        try {
            Inventory inventory = inventoryBean.getInventoryById(pId);
            if (inventory == null) {
                System.out.println("Core Error: Product Not Found...");
                return false;
            }

            if (inventory.getStockQuantity() < quantity) {
                System.out.println("Core Error: Insufficient stock available!");
                return false;
            }

            int currentStock = inventory.getStockQuantity();
            inventory.setStockQuantity(currentStock - quantity);
            inventoryBean.updateInventory(inventory);

            Orders newOrder = new Orders();
            newOrder.setOrderId(orderId);
            newOrder.setUserEmail(email);
            newOrder.setProductId(pId);
            newOrder.setQuantity(quantity);

            orderBean.saveOrders(newOrder);
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
