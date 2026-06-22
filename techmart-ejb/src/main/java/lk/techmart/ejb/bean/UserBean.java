package lk.techmart.ejb.bean;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lk.techmart.ejb.entity.Users;

import java.util.List;

@Stateless
public class UserBean {

    @PersistenceContext(unitName = "TechMartPU") // persistence.xml එකේ තියෙන නම
    private EntityManager em;

    // ඩේටාබේස් එකේ ඉන්න ඔක්කොම Users ලාව අරන් එන මෙතඩ් එක
    public List<Users> getAllUsers() {
        return em.createQuery("SELECT u FROM Users u", Users.class).getResultList();
    }
}
