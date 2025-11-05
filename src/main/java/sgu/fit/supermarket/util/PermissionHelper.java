package sgu.fit.supermarket.util;

import sgu.fit.supermarket.dto.RoleDTO;

public class PermissionHelper {
    
    // Role names
    public static final String ROLE_MANAGER = "Quản lý";
    public static final String ROLE_SALES_STAFF = "Nhân viên bán hàng";
    
    /**
     * Kiểm tra xem role có quyền quản lý sản phẩm không
     */
    public static boolean canManageProduct(RoleDTO role) {
        if (role == null) return false;
        return ROLE_MANAGER.equals(role.getRoleName());
    }
    
    /**
     * Kiểm tra xem role có quyền quản lý nhân viên không
     */
    public static boolean canManageEmployee(RoleDTO role) {
        if (role == null) return false;
        return ROLE_MANAGER.equals(role.getRoleName());
    }
    
    /**
     * Kiểm tra xem role có quyền tạo hóa đơn không
     */
    public static boolean canCreateInvoice(RoleDTO role) {
        if (role == null) return false;
        String roleName = role.getRoleName();
        return ROLE_MANAGER.equals(roleName) || ROLE_SALES_STAFF.equals(roleName);
    }
    
    /**
     * Kiểm tra xem role có quyền xem thống kê doanh thu không
     */
    public static boolean canViewRevenueStats(RoleDTO role) {
        if (role == null) return false;
        return ROLE_MANAGER.equals(role.getRoleName());
    }
    
    /**
     * Kiểm tra xem role có quyền nhập hàng không
     */
    public static boolean canImportProducts(RoleDTO role) {
        if (role == null) return false;
        return ROLE_MANAGER.equals(role.getRoleName());
    }
    
    /**
     * Kiểm tra xem role có quyền bán hàng không
     */
    public static boolean canSell(RoleDTO role) {
        if (role == null) return false;
        String roleName = role.getRoleName();
        return ROLE_MANAGER.equals(roleName) || ROLE_SALES_STAFF.equals(roleName);
    }
    
    /**
     * Kiểm tra xem có phải quản lý không
     */
    public static boolean isManager(RoleDTO role) {
        if (role == null) return false;
        return ROLE_MANAGER.equals(role.getRoleName());
    }
    
    /**
     * Kiểm tra xem có phải nhân viên bán hàng không
     */
    public static boolean isSalesStaff(RoleDTO role) {
        if (role == null) return false;
        return ROLE_SALES_STAFF.equals(role.getRoleName());
    }
}

