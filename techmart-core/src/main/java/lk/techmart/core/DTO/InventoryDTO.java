package lk.techmart.core.DTO;

public class InventoryDTO {
    private String productId;
    private int stockQuantity;

    public InventoryDTO() {
    }

    public InventoryDTO(String productId, int stockQuantity) {
        this.productId = productId;
        this.stockQuantity = stockQuantity;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public int getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(int stockQuantity) {
        this.stockQuantity = stockQuantity;
    }
}
