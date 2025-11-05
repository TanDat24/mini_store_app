package sgu.fit.supermarket.dao.impl;

import sgu.fit.supermarket.dao.EmployeeDAO;
import sgu.fit.supermarket.dto.EmployeeDTO;
import sgu.fit.supermarket.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

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
}
