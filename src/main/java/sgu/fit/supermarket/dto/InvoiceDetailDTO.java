package sgu.fit.supermarket.dto;

import java.math.BigDecimal;

public class InvoiceDetailDTO {
    private int invoiceId;
    private int productId;
    private int quantity;
    private BigDecimal price;

    public InvoiceDetailDTO() {
    }

    public InvoiceDetailDTO(int invoiceId, int productId, int quantity, BigDecimal price) {
        this.invoiceId = invoiceId;
        this.productId = productId;
        this.quantity = quantity;
        this.price = price;
    }

    public int getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(int invoiceId) {
        this.invoiceId = invoiceId;
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

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "InvoiceDetailDTO{" +
                "invoiceId=" + invoiceId +
                ", productId=" + productId +
                ", quantity=" + quantity +
                ", price=" + price +
                '}';
    }
}
