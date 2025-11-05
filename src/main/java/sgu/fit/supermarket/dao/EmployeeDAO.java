package sgu.fit.supermarket.dao;

import sgu.fit.supermarket.dto.EmployeeDTO;

public interface EmployeeDAO {
    EmployeeDTO findById(int employeeId);
    EmployeeDTO findByIdWithRole(int employeeId);
}
