package sgu.fit.supermarket.bus.impl;

import sgu.fit.supermarket.bus.InvoiceService;
import sgu.fit.supermarket.bus.ProductService;
import sgu.fit.supermarket.dao.InvoiceDAO;
import sgu.fit.supermarket.dao.impl.InvoiceDAOImpl;
import sgu.fit.supermarket.dto.InvoiceDTO;
import sgu.fit.supermarket.dto.InvoiceDetailDTO;

import java.sql.Timestamp;
import java.util.List;

public class InvoiceServiceImpl implements InvoiceService {
    private final InvoiceDAO invoiceDAO;
    private final ProductService productService;

    public InvoiceServiceImpl() {
        this.invoiceDAO = new InvoiceDAOImpl();
        this.productService = new ProductServiceImpl();
    }

    @Override
    public List<InvoiceDTO> getAllInvoices() {
        try {
            return invoiceDAO.findAll();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public InvoiceDTO getInvoiceById(int invoiceId) {
        try {
            if (invoiceId <= 0) return null;
            return invoiceDAO.findById(invoiceId);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<InvoiceDetailDTO> getInvoiceDetails(int invoiceId) {
        try {
            if (invoiceId <= 0) return null;
            return invoiceDAO.findDetailsByInvoiceId(invoiceId);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean createInvoice(InvoiceDTO invoice, List<InvoiceDetailDTO> details) {
        if (invoice == null || details == null || details.isEmpty()) {
            return false;
        }
        if (invoice.getEmployeeId() <= 0) return false;
        if (invoice.getCreatedAt() == null) invoice.setCreatedAt(new Timestamp(System.currentTimeMillis()));

        try {
            int invoiceId = invoiceDAO.insert(invoice);
            if (invoiceId <= 0) return false;

            for (InvoiceDetailDTO d : details) {
                d.setInvoiceId(invoiceId);
                // Insert detail
                if (!invoiceDAO.insertDetail(d)) {
                    // rollback by design would be here; for now we stop and return false
                    return false;
                }
                // Decrease stock
                if (!productService.decreaseProductStock(d.getProductId(), d.getQuantity())) {
                    return false;
                }
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
