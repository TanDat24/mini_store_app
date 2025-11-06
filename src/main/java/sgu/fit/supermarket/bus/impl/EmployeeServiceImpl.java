package sgu.fit.supermarket.bus.impl;

import sgu.fit.supermarket.bus.EmployeeService;
import sgu.fit.supermarket.dao.EmployeeDAO;
import sgu.fit.supermarket.dao.impl.EmployeeDAOImpl;
import sgu.fit.supermarket.dto.EmployeeDTO;
import java.util.List;

public class EmployeeServiceImpl implements EmployeeService {
    private EmployeeDAO employeeDAO;
    
    public EmployeeServiceImpl() {
        this.employeeDAO = new EmployeeDAOImpl();
    }
    
    @Override
    public EmployeeDTO findById(int employeeId) {
        try {
            return employeeDAO.findById(employeeId);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<EmployeeDTO> findAll() {
        try {
            return employeeDAO.findAll();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<EmployeeDTO> search(String keyword) {
        try {
            if (keyword == null || keyword.trim().isEmpty()) return findAll();
            return employeeDAO.findByNameOrPhone(keyword.trim());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean add(EmployeeDTO employee) {
        try {
            if (employee == null) return false;
            if (employee.getFullName() == null || employee.getFullName().trim().isEmpty()) return false;
            if (employee.getRoleId() <= 0) return false;
            return employeeDAO.insert(employee);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean update(EmployeeDTO employee) {
        try {
            if (employee == null || employee.getEmployeeId() <= 0) return false;
            if (employee.getFullName() == null || employee.getFullName().trim().isEmpty()) return false;
            if (employee.getRoleId() <= 0) return false;
            EmployeeDTO existing = employeeDAO.findById(employee.getEmployeeId());
            if (existing == null) return false;
            return employeeDAO.update(employee);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean delete(int employeeId) {
        try {
            if (employeeId <= 0) return false;
            EmployeeDTO existing = employeeDAO.findById(employeeId);
            if (existing == null) return false;
            
            // Check if employee has invoices before deletion
            int invoiceCount = employeeDAO.countInvoicesByEmployeeId(employeeId);
            if (invoiceCount > 0) {
                throw new RuntimeException("Không thể xóa nhân viên này vì đã có " + invoiceCount + " hóa đơn liên quan. Vui lòng xóa các hóa đơn trước.");
            }
            
            return employeeDAO.delete(employeeId);
        } catch (RuntimeException e) {
            // Re-throw RuntimeExceptions (including our custom message)
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}

