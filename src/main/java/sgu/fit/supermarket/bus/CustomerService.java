package sgu.fit.supermarket.bus;

import sgu.fit.supermarket.dto.CustomerDTO;

import java.util.List;

public interface CustomerService {
    List<CustomerDTO> getAllCustomers();
    CustomerDTO getCustomerById(int customerId);
    List<CustomerDTO> searchCustomers(String keyword);
    boolean addCustomer(CustomerDTO customer);
    boolean updateCustomer(CustomerDTO customer);
    boolean deleteCustomer(int customerId);
}
