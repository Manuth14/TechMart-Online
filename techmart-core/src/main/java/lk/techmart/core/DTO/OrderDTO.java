package lk.techmart.core.DTO;

public class OrderDTO {
    private String orderId;
    private String email;
    private String productId;
    private int quantity;

    public OrderDTO(String orderId, String email, String productId, int quantity) {
        this.orderId = orderId;
        this.email = email;
        this.productId = productId;
        this.quantity = quantity;
    }

    public OrderDTO() {
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
