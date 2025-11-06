package sgu.fit.supermarket.dao;

import sgu.fit.supermarket.dto.InvoiceDTO;
import sgu.fit.supermarket.dto.InvoiceDetailDTO;

import java.util.List;

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
}
