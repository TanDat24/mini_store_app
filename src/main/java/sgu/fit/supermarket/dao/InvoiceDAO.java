package sgu.fit.supermarket.dao;

import sgu.fit.supermarket.dto.InvoiceDTO;
import sgu.fit.supermarket.dto.InvoiceDetailDTO;

import java.sql.Date;
import java.util.List;
import java.math.BigDecimal;

public interface InvoiceDAO {
    /** List all invoices (newest first) */
    List<InvoiceDTO> findAll();

    /** Find an invoice by id */
    InvoiceDTO findById(int invoiceId);

    /** Insert an invoice, return generated id or -1 */
    int insert(InvoiceDTO invoice);

    /** Insert an invoice detail */
    boolean insertDetail(InvoiceDetailDTO detail);

    /** List details by invoice id */
    List<InvoiceDetailDTO> findDetailsByInvoiceId(int invoiceId);

    // Statistics
    BigDecimal getRevenue(Date from, Date to);
    List<Object[]> getTopSellingProducts(Date from, Date to, int limit);
    List<Object[]> getRevenueByEmployee(Date from, Date to);

    // Grouped revenue (label, amount)
    List<Object[]> getDailyRevenue(Date from, Date to);
    List<Object[]> getMonthlyRevenue(Date from, Date to);
    List<Object[]> getYearlyRevenue(Date from, Date to);
}
