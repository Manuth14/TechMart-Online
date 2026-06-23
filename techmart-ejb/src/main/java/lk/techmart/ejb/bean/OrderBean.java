package lk.techmart.ejb.bean;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lk.techmart.ejb.entity.Orders;

import java.util.List;

@Stateless
public class OrderBean {

    @PersistenceContext(unitName = "TechMartPU")
    private EntityManager entityManager;

    public List<Orders> findAllOrders() {
        return entityManager.createQuery("SELECT o FROM Orders o", Orders.class).getResultList();
    }

    public void saveOrders(Orders orders) {
        entityManager.persist(orders);
    }
}
