package sgu.fit.supermarket.dto;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class InvoiceDTO {
    private int invoiceId;
    private int employeeId;
    private int customerId;
    private BigDecimal totalAmount;
    private Timestamp createdAt;

    public InvoiceDTO() {
    }

    public InvoiceDTO(int invoiceId, int employeeId, int customerId, BigDecimal totalAmount, Timestamp createdAt) {
        this.invoiceId = invoiceId;
        this.employeeId = employeeId;
        this.customerId = customerId;
        this.totalAmount = totalAmount;
        this.createdAt = createdAt;
    }

    public int getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(int invoiceId) {
        this.invoiceId = invoiceId;
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "InvoiceDTO{" +
                "invoiceId=" + invoiceId +
                ", employeeId=" + employeeId +
                ", customerId=" + customerId +
                ", totalAmount=" + totalAmount +
                ", createdAt=" + createdAt +
                '}';
    }
}
