package sgu.fit.supermarket.bus;

import sgu.fit.supermarket.dto.SupplierDTO;

import java.util.List;

public interface SupplierService {
    List<SupplierDTO> getAllSuppliers();
    SupplierDTO getSupplierById(int supplierId);
    List<SupplierDTO> searchSuppliersByName(String name);
    boolean addSupplier(SupplierDTO supplier);
    boolean updateSupplier(SupplierDTO supplier);
    boolean deleteSupplier(int supplierId);
}
