package lk.techmart.core.DTO;

import java.util.List;

public class CartDTO {
    private String userEmail;
    private List<CartItemDTO> items;
    private double grandTotal;

    public CartDTO(String userEmail, List<CartItemDTO> items, double grandTotal) {
        this.userEmail = userEmail;
        this.items = items;
        this.grandTotal = grandTotal;
    }

    public CartDTO() {
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public List<CartItemDTO> getItems() {
        return items;
    }

    public void setItems(List<CartItemDTO> items) {
        this.items = items;
    }

    public double getGrandTotal() {
        return grandTotal;
    }

    public void setGrandTotal(double grandTotal) {
        this.grandTotal = grandTotal;
    }
}
