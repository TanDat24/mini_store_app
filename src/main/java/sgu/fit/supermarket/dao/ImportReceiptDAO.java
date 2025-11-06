package sgu.fit.supermarket.dao;

import sgu.fit.supermarket.dto.ImportReceiptDTO;
import sgu.fit.supermarket.dto.ImportReceiptDetailDTO;

import java.util.List;

public interface ImportReceiptDAO {
    /**
     * Tìm tất cả phiếu nhập hàng
     * @return Danh sách tất cả phiếu nhập hàng
     */
    List<ImportReceiptDTO> findAll();
    
    /**
     * Tìm phiếu nhập hàng theo ID
     * @param receiptId ID của phiếu nhập hàng
     * @return ImportReceiptDTO nếu tìm thấy, null nếu không
     */
    ImportReceiptDTO findById(int receiptId);
    
    /**
     * Thêm phiếu nhập hàng mới
     * @param receipt ImportReceiptDTO chứa thông tin phiếu nhập hàng
     * @return ID của phiếu nhập hàng mới được tạo, -1 nếu thất bại
     */
    int insert(ImportReceiptDTO receipt);
    
    /**
     * Thêm chi tiết phiếu nhập hàng
     * @param detail ImportReceiptDetailDTO chứa thông tin chi tiết
     * @return true nếu thêm thành công, false nếu thất bại
     */
    boolean insertDetail(ImportReceiptDetailDTO detail);
    
    /**
     * Lấy tất cả chi tiết của một phiếu nhập hàng
     * @param receiptId ID của phiếu nhập hàng
     * @return Danh sách chi tiết phiếu nhập hàng
     */
    List<ImportReceiptDetailDTO> findDetailsByReceiptId(int receiptId);
    
    /**
     * Xóa phiếu nhập hàng và tất cả chi tiết
     * @param receiptId ID của phiếu nhập hàng
     * @return true nếu xóa thành công, false nếu thất bại
     */
    boolean delete(int receiptId);
}

