package sgu.fit.supermarket.dao;

import sgu.fit.supermarket.dto.ProductDTO;

import java.util.List;

public interface ProductDAO {
    /**
     * Tìm tất cả sản phẩm
     * @return Danh sách tất cả sản phẩm
     */
    List<ProductDTO> findAll();
    
    /**
     * Tìm sản phẩm theo ID
     * @param productId ID của sản phẩm
     * @return ProductDTO nếu tìm thấy, null nếu không
     */
    ProductDTO findById(int productId);
    
    /**
     * Tìm sản phẩm theo tên (tìm kiếm gần đúng)
     * @param productName Tên sản phẩm cần tìm
     * @return Danh sách sản phẩm khớp với tên
     */
    List<ProductDTO> findByName(String productName);
    
    /**
     * Thêm sản phẩm mới
     * @param product ProductDTO chứa thông tin sản phẩm
     * @return true nếu thêm thành công, false nếu thất bại
     */
    boolean insert(ProductDTO product);
    
    /**
     * Cập nhật thông tin sản phẩm
     * @param product ProductDTO chứa thông tin sản phẩm cần cập nhật
     * @return true nếu cập nhật thành công, false nếu thất bại
     */
    boolean update(ProductDTO product);
    
    /**
     * Xóa sản phẩm theo ID
     * @param productId ID của sản phẩm cần xóa
     * @return true nếu xóa thành công, false nếu thất bại
     */
    boolean delete(int productId);
    
    /**
     * Cập nhật số lượng tồn kho của sản phẩm (tăng thêm)
     * @param productId ID của sản phẩm
     * @param quantity Số lượng cần tăng thêm
     * @return true nếu cập nhật thành công, false nếu thất bại
     */
    boolean updateStock(int productId, int quantity);
}
