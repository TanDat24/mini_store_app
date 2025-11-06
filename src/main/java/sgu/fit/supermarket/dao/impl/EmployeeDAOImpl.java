package sgu.fit.supermarket.dao.impl;

import sgu.fit.supermarket.dao.EmployeeDAO;
import sgu.fit.supermarket.dto.EmployeeDTO;
import sgu.fit.supermarket.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class EmployeeDAOImpl implements EmployeeDAO {

    @Override
    public EmployeeDTO findById(int employeeId) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnection.getConnection();
            if (conn == null) {
                return null;
            }
            
            String sql = "SELECT * FROM Employee WHERE employee_id = ?";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, employeeId);
            
            rs = ps.executeQuery();
            
            if (rs.next()) {
                EmployeeDTO employee = new EmployeeDTO();
                employee.setEmployeeId(rs.getInt("employee_id"));
                employee.setFullName(rs.getString("full_name"));
                employee.setPhone(rs.getString("phone"));
                employee.setAddress(rs.getString("address"));
                employee.setDateOfBirth(rs.getDate("date_of_birth"));
                employee.setGender(rs.getString("gender"));
                employee.setRoleId(rs.getInt("role_id"));
                return employee;
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        return null;
    }

    @Override
    public EmployeeDTO findByIdWithRole(int employeeId) {
        // Method này trả về employee với role đã được join
        // Nhưng để đơn giản, chúng ta sẽ chỉ trả về employee và lấy role riêng
        return findById(employeeId);
    }

    @Override
    public List<EmployeeDTO> findAll() {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<EmployeeDTO> list = new ArrayList<>();
        try {
            conn = DBConnection.getConnection();
            if (conn == null) return list;
            String sql = "SELECT * FROM Employee ORDER BY full_name";
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                EmployeeDTO employee = new EmployeeDTO();
                employee.setEmployeeId(rs.getInt("employee_id"));
                employee.setFullName(rs.getString("full_name"));
                employee.setPhone(rs.getString("phone"));
                employee.setAddress(rs.getString("address"));
                employee.setDateOfBirth(rs.getDate("date_of_birth"));
                employee.setGender(rs.getString("gender"));
                employee.setRoleId(rs.getInt("role_id"));
                list.add(employee);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return list;
    }

    @Override
    public List<EmployeeDTO> findByNameOrPhone(String keyword) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<EmployeeDTO> list = new ArrayList<>();
        try {
            conn = DBConnection.getConnection();
            if (conn == null) return list;
            String sql = "SELECT * FROM Employee WHERE full_name LIKE ? OR phone LIKE ? ORDER BY full_name";
            ps = conn.prepareStatement(sql);
            String k = "%" + keyword + "%";
            ps.setString(1, k);
            ps.setString(2, k);
            rs = ps.executeQuery();
            while (rs.next()) {
                EmployeeDTO employee = new EmployeeDTO();
                employee.setEmployeeId(rs.getInt("employee_id"));
                employee.setFullName(rs.getString("full_name"));
                employee.setPhone(rs.getString("phone"));
                employee.setAddress(rs.getString("address"));
                employee.setDateOfBirth(rs.getDate("date_of_birth"));
                employee.setGender(rs.getString("gender"));
                employee.setRoleId(rs.getInt("role_id"));
                list.add(employee);
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
    public boolean insert(EmployeeDTO employee) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBConnection.getConnection();
            if (conn == null) return false;
            String sql = "INSERT INTO Employee (full_name, phone, address, date_of_birth, gender, role_id) VALUES (?,?,?,?,?,?)";
            ps = conn.prepareStatement(sql);
            ps.setString(1, employee.getFullName());
            ps.setString(2, employee.getPhone());
            ps.setString(3, employee.getAddress());
            ps.setDate(4, employee.getDateOfBirth());
            ps.setString(5, employee.getGender());
            ps.setInt(6, employee.getRoleId());
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
    public boolean update(EmployeeDTO employee) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBConnection.getConnection();
            if (conn == null) return false;
            String sql = "UPDATE Employee SET full_name=?, phone=?, address=?, date_of_birth=?, gender=?, role_id=? WHERE employee_id=?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, employee.getFullName());
            ps.setString(2, employee.getPhone());
            ps.setString(3, employee.getAddress());
            ps.setDate(4, employee.getDateOfBirth());
            ps.setString(5, employee.getGender());
            ps.setInt(6, employee.getRoleId());
            ps.setInt(7, employee.getEmployeeId());
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
    public boolean delete(int employeeId) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBConnection.getConnection();
            if (conn == null) return false;
            String sql = "DELETE FROM Employee WHERE employee_id = ?";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, employeeId);
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
    public int countInvoicesByEmployeeId(int employeeId) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBConnection.getConnection();
            if (conn == null) return 0;
            String sql = "SELECT COUNT(*) as count FROM Invoice WHERE employee_id = ?";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, employeeId);
            rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("count");
            }
            return 0;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        } finally {
            try { if (rs != null) rs.close(); } catch (Exception ignored) {}
            try { if (ps != null) ps.close(); } catch (Exception ignored) {}
            try { if (conn != null) conn.close(); } catch (Exception ignored) {}
        }
    }
}
