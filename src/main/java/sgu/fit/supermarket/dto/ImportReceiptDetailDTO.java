package sgu.fit.supermarket.dto;

import java.math.BigDecimal;

public class ImportReceiptDetailDTO {
    private int receiptId;
    private int productId;
    private int quantity;
    private BigDecimal cost;

    public ImportReceiptDetailDTO() {
    }

    public ImportReceiptDetailDTO(int receiptId, int productId, int quantity, BigDecimal cost) {
        this.receiptId = receiptId;
        this.productId = productId;
        this.quantity = quantity;
        this.cost = cost;
    }

    public int getReceiptId() {
        return receiptId;
    }

    public void setReceiptId(int receiptId) {
        this.receiptId = receiptId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getCost() {
        return cost;
    }

    public void setCost(BigDecimal cost) {
        this.cost = cost;
    }

    @Override
    public String toString() {
        return "ImportReceiptDetailDTO{" +
                "receiptId=" + receiptId +
                ", productId=" + productId +
                ", quantity=" + quantity +
                ", cost=" + cost +
                '}';
    }
}
