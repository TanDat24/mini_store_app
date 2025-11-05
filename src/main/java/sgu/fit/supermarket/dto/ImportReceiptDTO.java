package sgu.fit.supermarket.dto;

import java.sql.Timestamp;

public class ImportReceiptDTO {
    private int receiptId;
    private int employeeId;
    private int supplierId;
    private Timestamp createdAt;

    public ImportReceiptDTO() {
    }

    public ImportReceiptDTO(int receiptId, int employeeId, int supplierId, Timestamp createdAt) {
        this.receiptId = receiptId;
        this.employeeId = employeeId;
        this.supplierId = supplierId;
        this.createdAt = createdAt;
    }

    public int getReceiptId() {
        return receiptId;
    }

    public void setReceiptId(int receiptId) {
        this.receiptId = receiptId;
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
    }

    public int getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(int supplierId) {
        this.supplierId = supplierId;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "ImportReceiptDTO{" +
                "receiptId=" + receiptId +
                ", employeeId=" + employeeId +
                ", supplierId=" + supplierId +
                ", createdAt=" + createdAt +
                '}';
    }
}
