package sgu.fit.supermarket.bus.impl;

import sgu.fit.supermarket.bus.CustomerService;
import sgu.fit.supermarket.dao.CustomerDAO;
import sgu.fit.supermarket.dao.impl.CustomerDAOImpl;
import sgu.fit.supermarket.dto.CustomerDTO;

import java.util.List;

public class CustomerServiceImpl implements CustomerService {
    private final CustomerDAO customerDAO;

    public CustomerServiceImpl() {
        this.customerDAO = new CustomerDAOImpl();
    }

    @Override
    public List<CustomerDTO> getAllCustomers() {
        try { return customerDAO.findAll(); } catch (Exception e) { e.printStackTrace(); return null; }
    }

    @Override
    public CustomerDTO getCustomerById(int customerId) {
        try { if (customerId <= 0) return null; return customerDAO.findById(customerId); } catch (Exception e) { e.printStackTrace(); return null; }
    }

    @Override
    public List<CustomerDTO> searchCustomers(String keyword) {
        try { if (keyword == null || keyword.trim().isEmpty()) return getAllCustomers(); return customerDAO.findByNameOrPhone(keyword.trim()); } catch (Exception e) { e.printStackTrace(); return null; }
    }

    @Override
    public boolean addCustomer(CustomerDTO customer) {
        try {
            if (customer == null) return false;
            if (customer.getFullName() == null || customer.getFullName().trim().isEmpty()) return false;
            if (customer.getPoints() < 0) return false;
            return customerDAO.insert(customer);
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    @Override
    public boolean updateCustomer(CustomerDTO customer) {
        try {
            if (customer == null || customer.getCustomerId() <= 0) return false;
            if (customer.getFullName() == null || customer.getFullName().trim().isEmpty()) return false;
            if (customer.getPoints() < 0) return false;
            CustomerDTO existing = customerDAO.findById(customer.getCustomerId());
            if (existing == null) return false;
            return customerDAO.update(customer);
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    @Override
    public boolean deleteCustomer(int customerId) {
        try {
            if (customerId <= 0) return false;
            CustomerDTO existing = customerDAO.findById(customerId);
            if (existing == null) return false;
            return customerDAO.delete(customerId);
        } catch (Exception e) { e.printStackTrace(); return false; }
    }
}
