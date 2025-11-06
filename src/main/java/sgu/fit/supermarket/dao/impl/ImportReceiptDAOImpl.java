package sgu.fit.supermarket.dao.impl;

import sgu.fit.supermarket.dao.ImportReceiptDAO;
import sgu.fit.supermarket.dto.ImportReceiptDTO;
import sgu.fit.supermarket.dto.ImportReceiptDetailDTO;
import sgu.fit.supermarket.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class ImportReceiptDAOImpl implements ImportReceiptDAO {

    @Override
    public List<ImportReceiptDTO> findAll() {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<ImportReceiptDTO> receipts = new ArrayList<>();
        
        try {
            conn = DBConnection.getConnection();
            if (conn == null) {
                return receipts;
            }
            
            String sql = "SELECT * FROM ImportReceipt ORDER BY created_at DESC";
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            
            while (rs.next()) {
                ImportReceiptDTO receipt = mapResultSetToImportReceiptDTO(rs);
                receipts.add(receipt);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeResources(rs, ps, conn);
        }
        
        return receipts;
    }

    @Override
    public ImportReceiptDTO findById(int receiptId) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnection.getConnection();
            if (conn == null) {
                return null;
            }
            
            String sql = "SELECT * FROM ImportReceipt WHERE receipt_id = ?";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, receiptId);
            
            rs = ps.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToImportReceiptDTO(rs);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeResources(rs, ps, conn);
        }
        
        return null;
    }

    @Override
    public int insert(ImportReceiptDTO receipt) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnection.getConnection();
            if (conn == null) {
                return -1;
            }
            
            String sql = "INSERT INTO ImportReceipt (employee_id, supplier_id, created_at) VALUES (?, ?, ?)";
            ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, receipt.getEmployeeId());
            ps.setInt(2, receipt.getSupplierId());
            ps.setTimestamp(3, receipt.getCreatedAt() != null ? receipt.getCreatedAt() : new Timestamp(System.currentTimeMillis()));
            
            int result = ps.executeUpdate();
            
            if (result > 0) {
                rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
            
            return -1;
            
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        } finally {
            closeResources(rs, ps, conn);
        }
    }

    @Override
    public boolean insertDetail(ImportReceiptDetailDTO detail) {
        Connection conn = null;
        PreparedStatement ps = null;
        
        try {
            conn = DBConnection.getConnection();
            if (conn == null) {
                return false;
            }
            
            String sql = "INSERT INTO ImportReceiptDetail (receipt_id, product_id, quantity, cost) VALUES (?, ?, ?, ?)";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, detail.getReceiptId());
            ps.setInt(2, detail.getProductId());
            ps.setInt(3, detail.getQuantity());
            ps.setBigDecimal(4, detail.getCost());
            
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
    public List<ImportReceiptDetailDTO> findDetailsByReceiptId(int receiptId) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<ImportReceiptDetailDTO> details = new ArrayList<>();
        
        try {
            conn = DBConnection.getConnection();
            if (conn == null) {
                return details;
            }
            
            String sql = "SELECT * FROM ImportReceiptDetail WHERE receipt_id = ?";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, receiptId);
            
            rs = ps.executeQuery();
            
            while (rs.next()) {
                ImportReceiptDetailDTO detail = mapResultSetToImportReceiptDetailDTO(rs);
                details.add(detail);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeResources(rs, ps, conn);
        }
        
        return details;
    }

    @Override
    public boolean delete(int receiptId) {
        Connection conn = null;
        PreparedStatement ps = null;
        
        try {
            conn = DBConnection.getConnection();
            if (conn == null) {
                return false;
            }
            
            // Xóa chi tiết trước
            String sqlDetail = "DELETE FROM ImportReceiptDetail WHERE receipt_id = ?";
            ps = conn.prepareStatement(sqlDetail);
            ps.setInt(1, receiptId);
            ps.executeUpdate();
            ps.close();
            
            // Xóa phiếu nhập hàng
            String sql = "DELETE FROM ImportReceipt WHERE receipt_id = ?";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, receiptId);
            
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
     * Map ResultSet thành ImportReceiptDTO
     */
    private ImportReceiptDTO mapResultSetToImportReceiptDTO(ResultSet rs) throws Exception {
        ImportReceiptDTO receipt = new ImportReceiptDTO();
        receipt.setReceiptId(rs.getInt("receipt_id"));
        receipt.setEmployeeId(rs.getInt("employee_id"));
        receipt.setSupplierId(rs.getInt("supplier_id"));
        receipt.setCreatedAt(rs.getTimestamp("created_at"));
        return receipt;
    }
    
    /**
     * Map ResultSet thành ImportReceiptDetailDTO
     */
    private ImportReceiptDetailDTO mapResultSetToImportReceiptDetailDTO(ResultSet rs) throws Exception {
        ImportReceiptDetailDTO detail = new ImportReceiptDetailDTO();
        detail.setReceiptId(rs.getInt("receipt_id"));
        detail.setProductId(rs.getInt("product_id"));
        detail.setQuantity(rs.getInt("quantity"));
        detail.setCost(rs.getBigDecimal("cost"));
        return detail;
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

