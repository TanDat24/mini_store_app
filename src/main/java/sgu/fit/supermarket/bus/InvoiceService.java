package sgu.fit.supermarket.bus;

import sgu.fit.supermarket.dto.InvoiceDTO;
import sgu.fit.supermarket.dto.InvoiceDetailDTO;

import java.util.List;

public interface InvoiceService {
    List<InvoiceDTO> getAllInvoices();
    InvoiceDTO getInvoiceById(int invoiceId);
    List<InvoiceDetailDTO> getInvoiceDetails(int invoiceId);

    /** Create invoice with details, and decrease stock */
    boolean createInvoice(InvoiceDTO invoice, List<InvoiceDetailDTO> details);
}
