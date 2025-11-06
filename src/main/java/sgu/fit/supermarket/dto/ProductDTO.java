package sgu.fit.supermarket.dto;

import java.math.BigDecimal;

public class ProductDTO {
    private int productId;
    private String productName;
    private String imagePath;
    private String unit;
    private BigDecimal price;
    private int stock;
    private boolean isDeleted;
    private int categoryId;
    private int supplierId;

    public ProductDTO() {
    }

    public ProductDTO(int productId, String productName, String imagePath, String unit, BigDecimal price, int stock, int categoryId, int supplierId) {
        this.productId = productId;
        this.productName = productName;
        this.imagePath = imagePath;
        this.unit = unit;
        this.price = price;
        this.stock = stock;
        this.isDeleted = false;
        this.categoryId = categoryId;
        this.supplierId = supplierId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public int getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(int supplierId) {
        this.supplierId = supplierId;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    @Override
    public String toString() {
        return "ProductDTO{" +
                "productId=" + productId +
                ", productName='" + productName + '\'' +
                ", unit='" + unit + '\'' +
                ", price=" + price +
                ", stock=" + stock +
                ", categoryId=" + categoryId +
                ", supplierId=" + supplierId +
                '}';
    }
}
