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

    @Override
    public java.math.BigDecimal getRevenue(java.sql.Date from, java.sql.Date to) {
        Connection conn = null; PreparedStatement ps = null; ResultSet rs = null;
        try {
            conn = DBConnection.getConnection(); if (conn == null) return java.math.BigDecimal.ZERO;
            String sql = "SELECT COALESCE(SUM(total_amount),0) AS revenue FROM Invoice WHERE created_at BETWEEN ? AND ?";
            ps = conn.prepareStatement(sql);
            ps.setDate(1, from); ps.setDate(2, to);
            rs = ps.executeQuery();
            if (rs.next()) return rs.getBigDecimal("revenue");
        } catch (Exception e) { e.printStackTrace(); }
        finally { closeResources(rs, ps, conn); }
        return java.math.BigDecimal.ZERO;
    }

    @Override
    public List<Object[]> getTopSellingProducts(java.sql.Date from, java.sql.Date to, int limit) {
        Connection conn = null; PreparedStatement ps = null; ResultSet rs = null;
        List<Object[]> list = new ArrayList<>();
        try {
            conn = DBConnection.getConnection(); if (conn == null) return list;
            String sql = "SELECT d.product_id, p.product_name, SUM(d.quantity) AS qty " +
                         "FROM Invoice i JOIN InvoiceDetail d ON i.invoice_id = d.invoice_id " +
                         "JOIN Product p ON p.product_id = d.product_id " +
                         "WHERE i.created_at BETWEEN ? AND ? " +
                         "GROUP BY d.product_id, p.product_name ORDER BY qty DESC LIMIT ?";
            ps = conn.prepareStatement(sql);
            ps.setDate(1, from); ps.setDate(2, to); ps.setInt(3, limit);
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Object[]{ rs.getInt("product_id"), rs.getString("product_name"), rs.getInt("qty") });
            }
        } catch (Exception e) { e.printStackTrace(); }
        finally { closeResources(rs, ps, conn); }
        return list;
    }

    @Override
    public List<Object[]> getRevenueByEmployee(java.sql.Date from, java.sql.Date to) {
        Connection conn = null; PreparedStatement ps = null; ResultSet rs = null;
        List<Object[]> list = new ArrayList<>();
        try {
            conn = DBConnection.getConnection(); if (conn == null) return list;
            String sql = "SELECT i.employee_id, e.full_name, COALESCE(SUM(i.total_amount),0) AS revenue " +
                         "FROM Invoice i JOIN Employee e ON e.employee_id = i.employee_id " +
                         "WHERE i.created_at BETWEEN ? AND ? " +
                         "GROUP BY i.employee_id, e.full_name ORDER BY revenue DESC";
            ps = conn.prepareStatement(sql);
            ps.setDate(1, from); ps.setDate(2, to);
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Object[]{ rs.getInt("employee_id"), rs.getString("full_name"), rs.getBigDecimal("revenue") });
            }
        } catch (Exception e) { e.printStackTrace(); }
        finally { closeResources(rs, ps, conn); }
        return list;
    }

    @Override
    public List<Object[]> getDailyRevenue(java.sql.Date from, java.sql.Date to) {
        Connection conn = null; PreparedStatement ps = null; ResultSet rs = null; List<Object[]> list = new ArrayList<>();
        try {
            conn = DBConnection.getConnection(); if (conn == null) return list;
            String sql = "SELECT DATE(created_at) AS d, COALESCE(SUM(total_amount),0) AS rev FROM Invoice " +
                         "WHERE created_at BETWEEN ? AND ? GROUP BY DATE(created_at) ORDER BY d";
            ps = conn.prepareStatement(sql); ps.setDate(1, from); ps.setDate(2, to); rs = ps.executeQuery();
            while (rs.next()) list.add(new Object[]{ rs.getDate("d").toString(), rs.getBigDecimal("rev") });
        } catch (Exception e) { e.printStackTrace(); } finally { closeResources(rs, ps, conn); }
        return list;
    }

    @Override
    public List<Object[]> getMonthlyRevenue(java.sql.Date from, java.sql.Date to) {
        Connection conn = null; PreparedStatement ps = null; ResultSet rs = null; List<Object[]> list = new ArrayList<>();
        try {
            conn = DBConnection.getConnection(); if (conn == null) return list;
            String sql = "SELECT DATE_FORMAT(created_at,'%Y-%m') AS m, COALESCE(SUM(total_amount),0) AS rev FROM Invoice " +
                         "WHERE created_at BETWEEN ? AND ? GROUP BY DATE_FORMAT(created_at,'%Y-%m') ORDER BY m";
            ps = conn.prepareStatement(sql); ps.setDate(1, from); ps.setDate(2, to); rs = ps.executeQuery();
            while (rs.next()) list.add(new Object[]{ rs.getString("m"), rs.getBigDecimal("rev") });
        } catch (Exception e) { e.printStackTrace(); } finally { closeResources(rs, ps, conn); }
        return list;
    }

    @Override
    public List<Object[]> getYearlyRevenue(java.sql.Date from, java.sql.Date to) {
        Connection conn = null; PreparedStatement ps = null; ResultSet rs = null; List<Object[]> list = new ArrayList<>();
        try {
            conn = DBConnection.getConnection(); if (conn == null) return list;
            String sql = "SELECT YEAR(created_at) AS y, COALESCE(SUM(total_amount),0) AS rev FROM Invoice " +
                         "WHERE created_at BETWEEN ? AND ? GROUP BY YEAR(created_at) ORDER BY y";
            ps = conn.prepareStatement(sql); ps.setDate(1, from); ps.setDate(2, to); rs = ps.executeQuery();
            while (rs.next()) list.add(new Object[]{ String.valueOf(rs.getInt("y")), rs.getBigDecimal("rev") });
        } catch (Exception e) { e.printStackTrace(); } finally { closeResources(rs, ps, conn); }
        return list;
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
