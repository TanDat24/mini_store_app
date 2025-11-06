package sgu.fit.supermarket.bus;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;

public interface StatisticsService {
    BigDecimal getRevenue(Date from, Date to);
    List<Object[]> getTopSellingProducts(Date from, Date to, int limit);
    List<Object[]> getRevenueByEmployee(Date from, Date to);

    List<Object[]> getDailyRevenue(Date from, Date to);
    List<Object[]> getMonthlyRevenue(Date from, Date to);
    List<Object[]> getYearlyRevenue(Date from, Date to);
}
