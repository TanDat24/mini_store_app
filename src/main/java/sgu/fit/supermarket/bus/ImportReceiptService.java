package sgu.fit.supermarket.bus;

import sgu.fit.supermarket.dto.ImportReceiptDTO;
import sgu.fit.supermarket.dto.ImportReceiptDetailDTO;

import java.util.List;

public interface ImportReceiptService {
    /**
     * Lấy tất cả phiếu nhập hàng
     * @return Danh sách tất cả phiếu nhập hàng
     */
    List<ImportReceiptDTO> getAllImportReceipts();
    
    /**
     * Tìm phiếu nhập hàng theo ID
     * @param receiptId ID của phiếu nhập hàng
     * @return ImportReceiptDTO nếu tìm thấy, null nếu không
     */
    ImportReceiptDTO getImportReceiptById(int receiptId);
    
    /**
     * Tạo phiếu nhập hàng mới với các chi tiết
     * @param receipt Phiếu nhập hàng
     * @param details Danh sách chi tiết phiếu nhập hàng
     * @return true nếu tạo thành công, false nếu thất bại
     */
    boolean createImportReceipt(ImportReceiptDTO receipt, List<ImportReceiptDetailDTO> details);
    
    /**
     * Lấy tất cả chi tiết của một phiếu nhập hàng
     * @param receiptId ID của phiếu nhập hàng
     * @return Danh sách chi tiết phiếu nhập hàng
     */
    List<ImportReceiptDetailDTO> getImportReceiptDetails(int receiptId);
    
    /**
     * Xóa phiếu nhập hàng
     * @param receiptId ID của phiếu nhập hàng
     * @return true nếu xóa thành công, false nếu thất bại
     */
    boolean deleteImportReceipt(int receiptId);
}

