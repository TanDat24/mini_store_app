package sgu.fit.supermarket.dao.impl;

import sgu.fit.supermarket.dao.CustomerDAO;
import sgu.fit.supermarket.dto.CustomerDTO;
import sgu.fit.supermarket.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class CustomerDAOImpl implements CustomerDAO {
    @Override
    public List<CustomerDTO> findAll() {
        Connection conn = null; PreparedStatement ps = null; ResultSet rs = null;
        List<CustomerDTO> list = new ArrayList<>();
        try {
            conn = DBConnection.getConnection();
            if (conn == null) return list;
            String sql = "SELECT customer_id, full_name, phone, points FROM Customer ORDER BY customer_id";
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) list.add(map(rs));
        } catch (Exception e) {
            e.printStackTrace();
        } finally { close(rs, ps, conn); }
        return list;
    }

    @Override
    public CustomerDTO findById(int customerId) {
        Connection conn = null; PreparedStatement ps = null; ResultSet rs = null;
        try {
            conn = DBConnection.getConnection();
            if (conn == null) return null;
            String sql = "SELECT customer_id, full_name, phone, points FROM Customer WHERE customer_id = ?";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, customerId);
            rs = ps.executeQuery();
            if (rs.next()) return map(rs);
        } catch (Exception e) {
            e.printStackTrace();
        } finally { close(rs, ps, conn); }
        return null;
    }

    @Override
    public List<CustomerDTO> findByNameOrPhone(String keyword) {
        Connection conn = null; PreparedStatement ps = null; ResultSet rs = null;
        List<CustomerDTO> list = new ArrayList<>();
        try {
            conn = DBConnection.getConnection();
            if (conn == null) return list;
            String sql = "SELECT customer_id, full_name, phone, points FROM Customer " +
                         "WHERE full_name LIKE ? OR phone LIKE ? ORDER BY customer_id";
            ps = conn.prepareStatement(sql);
            String k = "%" + keyword + "%";
            ps.setString(1, k); ps.setString(2, k);
            rs = ps.executeQuery();
            while (rs.next()) list.add(map(rs));
        } catch (Exception e) {
            e.printStackTrace();
        } finally { close(rs, ps, conn); }
        return list;
    }

    @Override
    public boolean insert(CustomerDTO customer) {
        Connection conn = null; PreparedStatement ps = null;
        try {
            conn = DBConnection.getConnection();
            if (conn == null) return false;
            String sql = "INSERT INTO Customer (full_name, phone, points) VALUES (?, ?, ?)";
            ps = conn.prepareStatement(sql);
            ps.setString(1, customer.getFullName());
            ps.setString(2, customer.getPhone());
            ps.setInt(3, customer.getPoints());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally { close(null, ps, conn); }
    }

    @Override
    public boolean update(CustomerDTO customer) {
        Connection conn = null; PreparedStatement ps = null;
        try {
            conn = DBConnection.getConnection();
            if (conn == null) return false;
            String sql = "UPDATE Customer SET full_name = ?, phone = ?, points = ? WHERE customer_id = ?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, customer.getFullName());
            ps.setString(2, customer.getPhone());
            ps.setInt(3, customer.getPoints());
            ps.setInt(4, customer.getCustomerId());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally { close(null, ps, conn); }
    }

    @Override
    public boolean delete(int customerId) {
        Connection conn = null; PreparedStatement ps = null;
        try {
            conn = DBConnection.getConnection();
            if (conn == null) return false;
            String sql = "DELETE FROM Customer WHERE customer_id = ?";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, customerId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally { close(null, ps, conn); }
    }

    @Override
    public boolean addPoints(int customerId, int points) {
        Connection conn = null; PreparedStatement ps = null;
        try {
            conn = DBConnection.getConnection();
            if (conn == null) return false;
            String sql = "UPDATE Customer SET points = points + ? WHERE customer_id = ?";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, points);
            ps.setInt(2, customerId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally { close(null, ps, conn); }
    }

    private CustomerDTO map(ResultSet rs) throws Exception {
        CustomerDTO c = new CustomerDTO();
        c.setCustomerId(rs.getInt("customer_id"));
        c.setFullName(rs.getString("full_name"));
        c.setPhone(rs.getString("phone"));
        c.setPoints(rs.getInt("points"));
        return c;
    }

    private void close(ResultSet rs, PreparedStatement ps, Connection conn) {
        try { if (rs != null) rs.close(); } catch (Exception ignored) {}
        try { if (ps != null) ps.close(); } catch (Exception ignored) {}
        try { if (conn != null) conn.close(); } catch (Exception ignored) {}
    }
}
