package sgu.fit.supermarket.bus.impl;

import sgu.fit.supermarket.bus.ProductService;
import sgu.fit.supermarket.dao.ProductDAO;
import sgu.fit.supermarket.dao.impl.ProductDAOImpl;
import sgu.fit.supermarket.dto.ProductDTO;

import java.util.List;

public class ProductServiceImpl implements ProductService {
    private ProductDAO productDAO;
    
    public ProductServiceImpl() {
        this.productDAO = new ProductDAOImpl();
    }
    
    @Override
    public List<ProductDTO> getAllProducts() {
        try {
            return productDAO.findAll();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    @Override
    public ProductDTO getProductById(int productId) {
        try {
            if (productId <= 0) {
                return null;
            }
            return productDAO.findById(productId);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    @Override
    public List<ProductDTO> searchProductsByName(String productName) {
        try {
            if (productName == null || productName.trim().isEmpty()) {
                return getAllProducts();
            }
            return productDAO.findByName(productName.trim());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    @Override
    public boolean addProduct(ProductDTO product) {
        try {
            if (product == null) {
                return false;
            }
            
            // Validate dữ liệu
            if (product.getProductName() == null || product.getProductName().trim().isEmpty()) {
                return false;
            }
            if (product.getPrice() == null || product.getPrice().compareTo(java.math.BigDecimal.ZERO) < 0) {
                return false;
            }
            if (product.getStock() < 0) {
                return false;
            }
            
            return productDAO.insert(product);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    public boolean updateProduct(ProductDTO product) {
        try {
            if (product == null || product.getProductId() <= 0) {
                return false;
            }
            
            // Validate dữ liệu
            if (product.getProductName() == null || product.getProductName().trim().isEmpty()) {
                return false;
            }
            if (product.getPrice() == null || product.getPrice().compareTo(java.math.BigDecimal.ZERO) < 0) {
                return false;
            }
            if (product.getStock() < 0) {
                return false;
            }
            
            // Kiểm tra sản phẩm có tồn tại không
            ProductDTO existingProduct = productDAO.findById(product.getProductId());
            if (existingProduct == null) {
                return false;
            }
            
            return productDAO.update(product);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    public boolean deleteProduct(int productId) {
        try {
            if (productId <= 0) {
                return false;
            }
            
            // Kiểm tra sản phẩm có tồn tại không
            ProductDTO existingProduct = productDAO.findById(productId);
            if (existingProduct == null) {
                return false;
            }
            
            return productDAO.delete(productId);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    public boolean updateProductStock(int productId, int quantity) {
        try {
            if (productId <= 0 || quantity <= 0) {
                return false;
            }
            
            // Kiểm tra sản phẩm có tồn tại không
            ProductDTO existingProduct = productDAO.findById(productId);
            if (existingProduct == null) {
                return false;
            }
            
            return productDAO.updateStock(productId, quantity);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
