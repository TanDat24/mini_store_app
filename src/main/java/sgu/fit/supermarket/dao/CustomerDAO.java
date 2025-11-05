package sgu.fit.supermarket.dao;

import sgu.fit.supermarket.dto.CustomerDTO;
import java.util.List;

public interface CustomerDAO {
    List<CustomerDTO> findAll();
    CustomerDTO findById(int customerId);
    List<CustomerDTO> findByNameOrPhone(String keyword);
    boolean insert(CustomerDTO customer);
    boolean update(CustomerDTO customer);
    boolean delete(int customerId);
}
