package sgu.fit.supermarket.dao;

import sgu.fit.supermarket.dto.SupplierDTO;
import java.util.List;

public interface SupplierDAO {
    List<SupplierDTO> findAll();
    SupplierDTO findById(int supplierId);
    List<SupplierDTO> findByName(String name);
    boolean insert(SupplierDTO supplier);
    boolean update(SupplierDTO supplier);
    boolean delete(int supplierId);
}
