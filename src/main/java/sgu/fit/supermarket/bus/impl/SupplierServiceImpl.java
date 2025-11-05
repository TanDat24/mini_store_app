package sgu.fit.supermarket.bus.impl;

import sgu.fit.supermarket.bus.SupplierService;
import sgu.fit.supermarket.dao.SupplierDAO;
import sgu.fit.supermarket.dao.impl.SupplierDAOImpl;
import sgu.fit.supermarket.dto.SupplierDTO;

import java.util.List;

public class SupplierServiceImpl implements SupplierService {
    private final SupplierDAO supplierDAO;

    public SupplierServiceImpl() {
        this.supplierDAO = new SupplierDAOImpl();
    }

    @Override
    public List<SupplierDTO> getAllSuppliers() {
        try {
            return supplierDAO.findAll();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public SupplierDTO getSupplierById(int supplierId) {
        try {
            if (supplierId <= 0) return null;
            return supplierDAO.findById(supplierId);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<SupplierDTO> searchSuppliersByName(String name) {
        try {
            if (name == null || name.trim().isEmpty()) {
                return getAllSuppliers();
            }
            return supplierDAO.findByName(name.trim());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean addSupplier(SupplierDTO supplier) {
        try {
            if (supplier == null) return false;
            if (supplier.getSupplierName() == null || supplier.getSupplierName().trim().isEmpty()) return false;
            return supplierDAO.insert(supplier);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean updateSupplier(SupplierDTO supplier) {
        try {
            if (supplier == null || supplier.getSupplierId() <= 0) return false;
            if (supplier.getSupplierName() == null || supplier.getSupplierName().trim().isEmpty()) return false;
            SupplierDTO existing = supplierDAO.findById(supplier.getSupplierId());
            if (existing == null) return false;
            return supplierDAO.update(supplier);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean deleteSupplier(int supplierId) {
        try {
            if (supplierId <= 0) return false;
            SupplierDTO existing = supplierDAO.findById(supplierId);
            if (existing == null) return false;
            return supplierDAO.delete(supplierId);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
