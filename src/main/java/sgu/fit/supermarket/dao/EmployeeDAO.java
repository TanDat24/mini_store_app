package sgu.fit.supermarket.dao;

import sgu.fit.supermarket.dto.EmployeeDTO;
import java.util.List;

public interface EmployeeDAO {
    EmployeeDTO findById(int employeeId);
    EmployeeDTO findByIdWithRole(int employeeId);
    List<EmployeeDTO> findAll();
    List<EmployeeDTO> findByNameOrPhone(String keyword);
    boolean insert(EmployeeDTO employee);
    boolean update(EmployeeDTO employee);
    boolean delete(int employeeId);
}
