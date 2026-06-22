package lk.techmart.ejb.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "orders")
public class Orders implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L; // 💡 අර අපි කතා වුණු ස්ථාවර ID එක

    @Id
    @Column(name = "orderId")
    private String orderId;

    @Column(name = "user_email", nullable = false)
    private String userEmail;

    @Column(name = "productId", nullable = false)
    private String productId;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Column(name = "order_date", insertable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date orderDate;
}
