package sgu.fit.supermarket.dao.impl;

import sgu.fit.supermarket.dao.SupplierDAO;
import sgu.fit.supermarket.dto.SupplierDTO;
import sgu.fit.supermarket.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class SupplierDAOImpl implements SupplierDAO {
    @Override
    public List<SupplierDTO> findAll() {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<SupplierDTO> list = new ArrayList<>();
        try {
            conn = DBConnection.getConnection();
            if (conn == null) return list;
            String sql = "SELECT supplier_id, supplier_name, phone, address FROM Supplier ORDER BY supplier_name";
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                SupplierDTO s = new SupplierDTO();
                s.setSupplierId(rs.getInt("supplier_id"));
                s.setSupplierName(rs.getString("supplier_name"));
                s.setPhone(rs.getString("phone"));
                s.setAddress(rs.getString("address"));
                list.add(s);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { if (rs != null) rs.close(); } catch (Exception ignored) {}
            try { if (ps != null) ps.close(); } catch (Exception ignored) {}
            try { if (conn != null) conn.close(); } catch (Exception ignored) {}
        }
        return list;
    }

    @Override
    public SupplierDTO findById(int supplierId) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBConnection.getConnection();
            if (conn == null) return null;
            String sql = "SELECT supplier_id, supplier_name, phone, address FROM Supplier WHERE supplier_id = ?";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, supplierId);
            rs = ps.executeQuery();
            if (rs.next()) {
                SupplierDTO s = new SupplierDTO();
                s.setSupplierId(rs.getInt("supplier_id"));
                s.setSupplierName(rs.getString("supplier_name"));
                s.setPhone(rs.getString("phone"));
                s.setAddress(rs.getString("address"));
                return s;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { if (rs != null) rs.close(); } catch (Exception ignored) {}
            try { if (ps != null) ps.close(); } catch (Exception ignored) {}
            try { if (conn != null) conn.close(); } catch (Exception ignored) {}
        }
        return null;
    }

    @Override
    public List<SupplierDTO> findByName(String name) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<SupplierDTO> list = new ArrayList<>();
        try {
            conn = DBConnection.getConnection();
            if (conn == null) return list;
            String sql = "SELECT supplier_id, supplier_name, phone, address FROM Supplier WHERE supplier_name LIKE ? ORDER BY supplier_name";
            ps = conn.prepareStatement(sql);
            ps.setString(1, "%" + name + "%");
            rs = ps.executeQuery();
            while (rs.next()) {
                SupplierDTO s = new SupplierDTO();
                s.setSupplierId(rs.getInt("supplier_id"));
                s.setSupplierName(rs.getString("supplier_name"));
                s.setPhone(rs.getString("phone"));
                s.setAddress(rs.getString("address"));
                list.add(s);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { if (rs != null) rs.close(); } catch (Exception ignored) {}
            try { if (ps != null) ps.close(); } catch (Exception ignored) {}
            try { if (conn != null) conn.close(); } catch (Exception ignored) {}
        }
        return list;
    }

    @Override
    public boolean insert(SupplierDTO supplier) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBConnection.getConnection();
            if (conn == null) return false;
            String sql = "INSERT INTO Supplier (supplier_name, phone, address) VALUES (?, ?, ?)";
            ps = conn.prepareStatement(sql);
            ps.setString(1, supplier.getSupplierName());
            ps.setString(2, supplier.getPhone());
            ps.setString(3, supplier.getAddress());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            try { if (ps != null) ps.close(); } catch (Exception ignored) {}
            try { if (conn != null) conn.close(); } catch (Exception ignored) {}
        }
    }

    @Override
    public boolean update(SupplierDTO supplier) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBConnection.getConnection();
            if (conn == null) return false;
            String sql = "UPDATE Supplier SET supplier_name = ?, phone = ?, address = ? WHERE supplier_id = ?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, supplier.getSupplierName());
            ps.setString(2, supplier.getPhone());
            ps.setString(3, supplier.getAddress());
            ps.setInt(4, supplier.getSupplierId());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            try { if (ps != null) ps.close(); } catch (Exception ignored) {}
            try { if (conn != null) conn.close(); } catch (Exception ignored) {}
        }
    }

    @Override
    public boolean delete(int supplierId) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBConnection.getConnection();
            if (conn == null) return false;
            String sql = "DELETE FROM Supplier WHERE supplier_id = ?";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, supplierId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            try { if (ps != null) ps.close(); } catch (Exception ignored) {}
            try { if (conn != null) conn.close(); } catch (Exception ignored) {}
        }
    }
}
