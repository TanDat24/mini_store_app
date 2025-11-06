package sgu.fit.supermarket.bus.impl;

import sgu.fit.supermarket.bus.ImportReceiptService;
import sgu.fit.supermarket.bus.ProductService;
import sgu.fit.supermarket.dao.ImportReceiptDAO;
import sgu.fit.supermarket.dao.impl.ImportReceiptDAOImpl;
import sgu.fit.supermarket.dto.ImportReceiptDTO;
import sgu.fit.supermarket.dto.ImportReceiptDetailDTO;

import java.sql.Timestamp;
import java.util.List;

public class ImportReceiptServiceImpl implements ImportReceiptService {
    private final ImportReceiptDAO importReceiptDAO;
    private final ProductService productService;

    public ImportReceiptServiceImpl() {
        this.importReceiptDAO = new ImportReceiptDAOImpl();
        this.productService = new sgu.fit.supermarket.bus.impl.ProductServiceImpl();
    }

    @Override
    public List<ImportReceiptDTO> getAllImportReceipts() {
        try {
            return importReceiptDAO.findAll();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public ImportReceiptDTO getImportReceiptById(int receiptId) {
        try {
            if (receiptId <= 0) return null;
            return importReceiptDAO.findById(receiptId);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean createImportReceipt(ImportReceiptDTO receipt, List<ImportReceiptDetailDTO> details) {
        if (receipt == null || details == null || details.isEmpty()) {
            return false;
        }
        
        // Validate receipt
        if (receipt.getEmployeeId() <= 0 || receipt.getSupplierId() <= 0) {
            return false;
        }
        
        // Validate details
        for (ImportReceiptDetailDTO detail : details) {
            if (detail.getProductId() <= 0 || detail.getQuantity() <= 0 || 
                detail.getCost() == null || detail.getCost().compareTo(java.math.BigDecimal.ZERO) <= 0) {
                return false;
            }
        }
        
        // Set created date if not set
        if (receipt.getCreatedAt() == null) {
            receipt.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        }
        
        // Tạo phiếu nhập hàng và cập nhật tồn kho trong transaction
        try {
            // Insert receipt
            int receiptId = importReceiptDAO.insert(receipt);
            if (receiptId <= 0) {
                return false;
            }
            
            // Insert details and update stock
            for (ImportReceiptDetailDTO detail : details) {
                detail.setReceiptId(receiptId);
                
                // Insert detail
                if (!importReceiptDAO.insertDetail(detail)) {
                    // Rollback: delete receipt and details already inserted
                    importReceiptDAO.delete(receiptId);
                    return false;
                }
                
                // Update product stock
                if (!productService.updateProductStock(detail.getProductId(), detail.getQuantity())) {
                    // Rollback: delete receipt and details
                    importReceiptDAO.delete(receiptId);
                    return false;
                }
            }
            
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<ImportReceiptDetailDTO> getImportReceiptDetails(int receiptId) {
        try {
            if (receiptId <= 0) return null;
            return importReceiptDAO.findDetailsByReceiptId(receiptId);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean deleteImportReceipt(int receiptId) {
        try {
            if (receiptId <= 0) return false;
            
            // Check if receipt exists
            ImportReceiptDTO existing = importReceiptDAO.findById(receiptId);
            if (existing == null) return false;
            
            // Note: In a real system, we might want to decrease stock when deleting
            // For now, we just delete the receipt
            return importReceiptDAO.delete(receiptId);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}

