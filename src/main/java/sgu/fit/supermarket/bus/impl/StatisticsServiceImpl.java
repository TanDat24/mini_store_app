package sgu.fit.supermarket.bus.impl;

import sgu.fit.supermarket.bus.StatisticsService;
import sgu.fit.supermarket.dao.InvoiceDAO;
import sgu.fit.supermarket.dao.impl.InvoiceDAOImpl;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;

public class StatisticsServiceImpl implements StatisticsService {
    private final InvoiceDAO invoiceDAO;

    public StatisticsServiceImpl() {
        this.invoiceDAO = new InvoiceDAOImpl();
    }

    @Override
    public BigDecimal getRevenue(Date from, Date to) {
        try { return invoiceDAO.getRevenue(from, to); } catch (Exception e) { e.printStackTrace(); return BigDecimal.ZERO; }
    }

    @Override
    public List<Object[]> getTopSellingProducts(Date from, Date to, int limit) {
        try { return invoiceDAO.getTopSellingProducts(from, to, limit); } catch (Exception e) { e.printStackTrace(); return java.util.Collections.emptyList(); }
    }

    @Override
    public List<Object[]> getRevenueByEmployee(Date from, Date to) {
        try { return invoiceDAO.getRevenueByEmployee(from, to); } catch (Exception e) { e.printStackTrace(); return java.util.Collections.emptyList(); }
    }

    @Override
    public List<Object[]> getDailyRevenue(Date from, Date to) {
        try { return invoiceDAO.getDailyRevenue(from, to); } catch (Exception e) { e.printStackTrace(); return java.util.Collections.emptyList(); }
    }

    @Override
    public List<Object[]> getMonthlyRevenue(Date from, Date to) {
        try { return invoiceDAO.getMonthlyRevenue(from, to); } catch (Exception e) { e.printStackTrace(); return java.util.Collections.emptyList(); }
    }

    @Override
    public List<Object[]> getYearlyRevenue(Date from, Date to) {
        try { return invoiceDAO.getYearlyRevenue(from, to); } catch (Exception e) { e.printStackTrace(); return java.util.Collections.emptyList(); }
    }
}
