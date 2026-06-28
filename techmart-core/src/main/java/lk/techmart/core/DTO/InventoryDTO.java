package lk.techmart.core.DTO;

public class InventoryDTO {
    private String productId;
    private String product_name;
    private int stockQuantity;
    private double unitPrice;

    public InventoryDTO() {
    }

    public InventoryDTO(String productId, String product_name, int stockQuantity, double unitPrice) {
        this.productId = productId;
        this.product_name = product_name;
        this.stockQuantity = stockQuantity;
        this.unitPrice = unitPrice;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public int getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(int stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }
}
