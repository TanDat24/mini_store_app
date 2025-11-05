package sgu.fit.supermarket.bus.impl;

import sgu.fit.supermarket.bus.RoleService;
import sgu.fit.supermarket.dao.RoleDAO;
import sgu.fit.supermarket.dao.impl.RoleDAOImpl;
import sgu.fit.supermarket.dto.RoleDTO;
import java.util.List;

public class RoleServiceImpl implements RoleService {
    private RoleDAO roleDAO;
    
    public RoleServiceImpl() {
        this.roleDAO = new RoleDAOImpl();
    }
    
    @Override
    public RoleDTO findById(int roleId) {
        try {
            return roleDAO.findById(roleId);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    @Override
    public RoleDTO findByName(String roleName) {
        try {
            return roleDAO.findByName(roleName);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<RoleDTO> findAll() {
        try {
            return roleDAO.findAll();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}

