package lk.techmart.ejb.bean;

import jakarta.annotation.Resource;
import jakarta.ejb.Stateless;
import javax.sql.DataSource;
import lk.techmart.core.DTO.InventoryDTO;
import lk.techmart.core.service.InventoryService;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Stateless
public class InventoryBean implements InventoryService {

    @Resource(lookup = "jdbc/TechMart")
    private DataSource dataSource;

    @Override
    public List<InventoryDTO> getAllProducts() {
        List<InventoryDTO> productList = new ArrayList<>();
        String query = "SELECT productId, product_name, stockQuantity, unitPrice FROM inventory";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                InventoryDTO product = new InventoryDTO(
                        rs.getString("productId"),
                        rs.getString("product_name"),
                        rs.getInt("stockQuantity"),
                        rs.getDouble("unitPrice")
                );
                productList.add(product);
            }
            System.out.println("📦 [InventoryBean] Loaded " + productList.size() + " products from DB.");

        } catch (SQLException e) {
            System.err.println("❌ [InventoryBean] Error in getAllProducts: " + e.getMessage());
            e.printStackTrace();
        }
        return productList;
    }

    @Override
    public InventoryDTO getProductById(String productId) {
        String query = "SELECT productId, product_name, stockQuantity, unitPrice FROM inventory WHERE productId = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, productId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new InventoryDTO(
                            rs.getString("productId"),
                            rs.getString("product_name"),
                            rs.getInt("stockQuantity"),
                            rs.getDouble("unitPrice")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ [InventoryBean] Error in getProductById: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void updateInventory(InventoryDTO inventoryDTO) {
        String query = "UPDATE inventory SET product_name = ?, stockQuantity = ?, unitPrice = ? WHERE productId = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, inventoryDTO.getProduct_name());
            ps.setInt(2, inventoryDTO.getStockQuantity());
            ps.setDouble(3, inventoryDTO.getUnitPrice());
            ps.setString(4, inventoryDTO.getProductId());

            int rows = ps.executeUpdate();
            System.out.println("🔄 [InventoryBean] Updated inventory for: " + inventoryDTO.getProductId() + " | Rows affected: " + rows);

        } catch (SQLException e) {
            System.err.println("❌ [InventoryBean] Error in updateInventory: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public boolean reduceStock(String productId, int quantity) {

        String query = "UPDATE inventory SET stockQuantity = stockQuantity - ? WHERE productId = ? AND stockQuantity >= ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setInt(1, quantity);
            ps.setString(2, productId);
            ps.setInt(3, quantity);

            int rowsUpdated = ps.executeUpdate();

            return rowsUpdated > 0;

        } catch (SQLException e) {
            System.err.println("❌ [InventoryBean] SQL Error: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}