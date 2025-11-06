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
    
    /**
     * Cộng điểm cho khách hàng
     * @param customerId ID của khách hàng
     * @param points Số điểm cần cộng
     * @return true nếu thành công, false nếu thất bại
     */
    boolean addPoints(int customerId, int points);
}
