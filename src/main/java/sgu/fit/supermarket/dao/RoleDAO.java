package sgu.fit.supermarket.dao;

import sgu.fit.supermarket.dto.RoleDTO;
import java.util.List;

public interface RoleDAO {
    RoleDTO findById(int roleId);
    RoleDTO findByName(String roleName);
    List<RoleDTO> findAll();
}

