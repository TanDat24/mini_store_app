package sgu.fit.supermarket.dao.impl;

import sgu.fit.supermarket.dao.RoleDAO;
import sgu.fit.supermarket.dto.RoleDTO;
import sgu.fit.supermarket.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class RoleDAOImpl implements RoleDAO {

    @Override
    public RoleDTO findById(int roleId) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnection.getConnection();
            if (conn == null) {
                return null;
            }
            
            String sql = "SELECT * FROM Role WHERE role_id = ?";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, roleId);
            
            rs = ps.executeQuery();
            
            if (rs.next()) {
                RoleDTO role = new RoleDTO();
                role.setRoleId(rs.getInt("role_id"));
                role.setRoleName(rs.getString("role_name"));
                return role;
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
    public RoleDTO findByName(String roleName) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnection.getConnection();
            if (conn == null) {
                return null;
            }
            
            String sql = "SELECT * FROM Role WHERE role_name = ?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, roleName);
            
            rs = ps.executeQuery();
            
            if (rs.next()) {
                RoleDTO role = new RoleDTO();
                role.setRoleId(rs.getInt("role_id"));
                role.setRoleName(rs.getString("role_name"));
                return role;
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
}

