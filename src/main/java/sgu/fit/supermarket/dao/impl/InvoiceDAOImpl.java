package sgu.fit.supermarket.dao.impl;

import sgu.fit.supermarket.dao.InvoiceDAO;
import sgu.fit.supermarket.dto.InvoiceDTO;
import sgu.fit.supermarket.dto.InvoiceDetailDTO;
import sgu.fit.supermarket.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class InvoiceDAOImpl implements InvoiceDAO {

    @Override
    public List<InvoiceDTO> findAll() {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<InvoiceDTO> invoices = new ArrayList<>();
        try {
            conn = DBConnection.getConnection();
            if (conn == null) return invoices;
            String sql = "SELECT * FROM Invoice ORDER BY created_at DESC";
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                invoices.add(mapInvoice(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeResources(rs, ps, conn);
        }
        return invoices;
    }

    @Override
    public InvoiceDTO findById(int invoiceId) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBConnection.getConnection();
            if (conn == null) return null;
            String sql = "SELECT * FROM Invoice WHERE invoice_id = ?";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, invoiceId);
            rs = ps.executeQuery();
            if (rs.next()) return mapInvoice(rs);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeResources(rs, ps, conn);
        }
        return null;
    }

    @Override
    public int insert(InvoiceDTO invoice) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBConnection.getConnection();
            if (conn == null) return -1;
            String sql = "INSERT INTO Invoice (employee_id, customer_id, created_at, total_amount) VALUES (?, ?, ?, ?)";
            ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, invoice.getEmployeeId());
            ps.setObject(2, invoice.getCustomerId() == 0 ? null : invoice.getCustomerId());
            ps.setTimestamp(3, invoice.getCreatedAt() != null ? invoice.getCreatedAt() : new Timestamp(System.currentTimeMillis()));
            ps.setBigDecimal(4, invoice.getTotalAmount());
            int r = ps.executeUpdate();
            if (r > 0) {
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
    public boolean insertDetail(InvoiceDetailDTO detail) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBConnection.getConnection();
            if (conn == null) return false;
            String sql = "INSERT INTO InvoiceDetail (invoice_id, product_id, quantity, price) VALUES (?, ?, ?, ?)";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, detail.getInvoiceId());
            ps.setInt(2, detail.getProductId());
            ps.setInt(3, detail.getQuantity());
            ps.setBigDecimal(4, detail.getPrice());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            closeResources(null, ps, conn);
        }
    }

    @Override
    public List<InvoiceDetailDTO> findDetailsByInvoiceId(int invoiceId) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<InvoiceDetailDTO> details = new ArrayList<>();
        try {
            conn = DBConnection.getConnection();
            if (conn == null) return details;
            String sql = "SELECT * FROM InvoiceDetail WHERE invoice_id = ?";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, invoiceId);
            rs = ps.executeQuery();
            while (rs.next()) {
                details.add(mapDetail(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeResources(rs, ps, conn);
        }
        return details;
    }

    private InvoiceDTO mapInvoice(ResultSet rs) throws Exception {
        InvoiceDTO i = new InvoiceDTO();
        i.setInvoiceId(rs.getInt("invoice_id"));
        i.setEmployeeId(rs.getInt("employee_id"));
        i.setCustomerId(rs.getInt("customer_id"));
        i.setCreatedAt(rs.getTimestamp("created_at"));
        i.setTotalAmount(rs.getBigDecimal("total_amount"));
        return i;
    }

    private InvoiceDetailDTO mapDetail(ResultSet rs) throws Exception {
        InvoiceDetailDTO d = new InvoiceDetailDTO();
        d.setInvoiceId(rs.getInt("invoice_id"));
        d.setProductId(rs.getInt("product_id"));
        d.setQuantity(rs.getInt("quantity"));
        d.setPrice(rs.getBigDecimal("price"));
        return d;
    }

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
