package sgu.fit.supermarket.dao;

import sgu.fit.supermarket.dto.RoleDTO;

public interface RoleDAO {
    RoleDTO findById(int roleId);
    RoleDTO findByName(String roleName);
}

