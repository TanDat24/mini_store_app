package sgu.fit.supermarket.bus;

import sgu.fit.supermarket.dto.RoleDTO;
import java.util.List;

public interface RoleService {
    /**
     * Tìm role theo ID
     * @param roleId ID của role
     * @return RoleDTO nếu tìm thấy, null nếu không
     */
    RoleDTO findById(int roleId);
    
    /**
     * Tìm role theo tên
     * @param roleName Tên của role
     * @return RoleDTO nếu tìm thấy, null nếu không
     */
    RoleDTO findByName(String roleName);

    /**
     * Lấy tất cả role
     */
    List<RoleDTO> findAll();
}

