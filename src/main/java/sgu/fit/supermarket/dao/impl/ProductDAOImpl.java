package sgu.fit.supermarket.dao.impl;

import sgu.fit.supermarket.dao.ProductDAO;
import sgu.fit.supermarket.dto.ProductDTO;
import sgu.fit.supermarket.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ProductDAOImpl implements ProductDAO {

    @Override
    public List<ProductDTO> findAll() {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<ProductDTO> products = new ArrayList<>();
        
        try {
            conn = DBConnection.getConnection();
            if (conn == null) {
                return products;
            }
            
            String sql = "SELECT * FROM Product WHERE is_deleted = 0 ORDER BY product_id";
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            
            while (rs.next()) {
                ProductDTO product = mapResultSetToProductDTO(rs);
                products.add(product);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeResources(rs, ps, conn);
        }
        
        return products;
    }

    @Override
    public ProductDTO findById(int productId) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnection.getConnection();
            if (conn == null) {
                return null;
            }
            
            String sql = "SELECT * FROM Product WHERE product_id = ? AND is_deleted = 0";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, productId);
            
            rs = ps.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToProductDTO(rs);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeResources(rs, ps, conn);
        }
        
        return null;
    }

    @Override
    public List<ProductDTO> findByName(String productName) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<ProductDTO> products = new ArrayList<>();
        
        try {
            conn = DBConnection.getConnection();
            if (conn == null) {
                return products;
            }
            
            String sql = "SELECT * FROM Product WHERE product_name LIKE ? AND is_deleted = 0 ORDER BY product_id";
            ps = conn.prepareStatement(sql);
            ps.setString(1, "%" + productName + "%");
            
            rs = ps.executeQuery();
            
            while (rs.next()) {
                ProductDTO product = mapResultSetToProductDTO(rs);
                products.add(product);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeResources(rs, ps, conn);
        }
        
        return products;
    }

    @Override
    public boolean insert(ProductDTO product) {
        Connection conn = null;
        PreparedStatement ps = null;
        
        try {
            conn = DBConnection.getConnection();
            if (conn == null) {
                return false;
            }
            
            String sql = "INSERT INTO Product (product_name, image_path, unit, price, stock, category_id, supplier_id, is_deleted) " +
                         "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            ps = conn.prepareStatement(sql);
            ps.setString(1, product.getProductName());
            ps.setString(2, product.getImagePath());
            ps.setString(3, product.getUnit());
            ps.setBigDecimal(4, product.getPrice());
            ps.setInt(5, product.getStock());
            ps.setInt(6, product.getCategoryId());
            ps.setInt(7, product.getSupplierId());
            ps.setInt(8, product.isDeleted() ? 1 : 0);
            
            int result = ps.executeUpdate();
            return result > 0;
            
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            closeResources(null, ps, conn);
        }
    }

    @Override
    public boolean update(ProductDTO product) {
        Connection conn = null;
        PreparedStatement ps = null;
        
        try {
            conn = DBConnection.getConnection();
            if (conn == null) {
                return false;
            }
            
            String sql = "UPDATE Product SET product_name = ?, image_path = ?, unit = ?, price = ?, " +
                         "stock = ?, category_id = ?, supplier_id = ?, is_deleted = ? WHERE product_id = ?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, product.getProductName());
            ps.setString(2, product.getImagePath());
            ps.setString(3, product.getUnit());
            ps.setBigDecimal(4, product.getPrice());
            ps.setInt(5, product.getStock());
            ps.setInt(6, product.getCategoryId());
            ps.setInt(7, product.getSupplierId());
            ps.setInt(8, product.isDeleted() ? 1 : 0);
            ps.setInt(9, product.getProductId());
            
            int result = ps.executeUpdate();
            return result > 0;
            
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            closeResources(null, ps, conn);
        }
    }

    @Override
    public boolean delete(int productId) {
        Connection conn = null;
        PreparedStatement ps = null;
        
        try {
            conn = DBConnection.getConnection();
            if (conn == null) {
                return false;
            }
            
            // Soft delete: set is_deleted = 1 instead of DELETE
            String sql = "UPDATE Product SET is_deleted = 1 WHERE product_id = ?";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, productId);
            
            int result = ps.executeUpdate();
            return result > 0;
            
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            closeResources(null, ps, conn);
        }
    }
    
    @Override
    public boolean updateStock(int productId, int quantity) {
        Connection conn = null;
        PreparedStatement ps = null;
        
        try {
            conn = DBConnection.getConnection();
            if (conn == null) {
                return false;
            }
            
            String sql = "UPDATE Product SET stock = stock + ? WHERE product_id = ? AND is_deleted = 0";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, quantity);
            ps.setInt(2, productId);
            
            int result = ps.executeUpdate();
            return result > 0;
            
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            closeResources(null, ps, conn);
        }
    }
    
    /**
     * Map ResultSet thành ProductDTO
     */
    private ProductDTO mapResultSetToProductDTO(ResultSet rs) throws Exception {
        ProductDTO product = new ProductDTO();
        product.setProductId(rs.getInt("product_id"));
        product.setProductName(rs.getString("product_name"));
        product.setImagePath(rs.getString("image_path"));
        product.setUnit(rs.getString("unit"));
        product.setPrice(rs.getBigDecimal("price"));
        product.setStock(rs.getInt("stock"));
        product.setDeleted(rs.getInt("is_deleted") == 1);
        product.setCategoryId(rs.getInt("category_id"));
        product.setSupplierId(rs.getInt("supplier_id"));
        return product;
    }
    
    /**
     * Đóng các tài nguyên database
     */
    private void closeResources(ResultSet rs, PreparedStatement ps, Connection conn) {
        try {
            if (rs != null) rs.close();
            if (ps != null) ps.close();
            if (conn != null) conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
