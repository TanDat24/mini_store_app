package sgu.fit.supermarket.gui.swing;

import sgu.fit.supermarket.bus.StatisticsService;
import sgu.fit.supermarket.bus.impl.StatisticsServiceImpl;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.*;

public class StatisticsFrame extends JPanel {
    private final StatisticsService statisticsService;

    public StatisticsFrame() {
        this.statisticsService = new StatisticsServiceImpl();
        setLayout(new BorderLayout());
        setOpaque(true);
        setBackground(Color.WHITE);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Doanh thu", createRevenueTab());
        tabs.addTab("Sản phẩm bán chạy", createTopProductsTab());
        tabs.addTab("Doanh thu theo nhân viên", createRevenueByEmployeeTab());

        add(tabs, BorderLayout.CENTER);
    }

    private JPanel createRevenueTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel top = new JPanel(new BorderLayout());
        JPanel filter = createDateFilter((from, to) -> applyRevenue(from, to));
        JPanel modePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
        modePanel.setOpaque(false);
        modePanel.add(new JLabel("Theo:"));
        cboMode = new JComboBox<>(new String[]{"Ngày", "Tháng", "Năm"});
        modePanel.add(cboMode);
        JButton btnApply = new JButton("Áp dụng");
        btnApply.addActionListener(e -> { if (lastFrom != null && lastTo != null) applyRevenue(lastFrom, lastTo); });
        modePanel.add(btnApply);
        top.add(filter, BorderLayout.CENTER);
        top.add(modePanel, BorderLayout.EAST);

        // Table + line chart
        revenueModel = new DefaultTableModel(new Object[]{"Mốc", "Doanh thu"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable revenueTable = new JTable(revenueModel);
        SimpleLineChart lineChart = new SimpleLineChart();
        this.revenueChart = lineChart;
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new JScrollPane(revenueTable), lineChart);
        split.setResizeWeight(0.45);

        lblRevenue = new JLabel("Tổng doanh thu: 0");
        lblRevenue.setFont(new Font("Segoe UI", Font.BOLD, 16));
        panel.add(top, BorderLayout.NORTH);
        panel.add(split, BorderLayout.CENTER);
        panel.add(lblRevenue, BorderLayout.SOUTH);
        
        // Initialize default range (last 30 days)
        java.time.LocalDate today = java.time.LocalDate.now();
        lastFrom = Date.valueOf(today.minusDays(30));
        lastTo = Date.valueOf(today);
        applyRevenue(lastFrom, lastTo);
        return panel;
    }

    private JPanel createTopProductsTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        DefaultTableModel model = new DefaultTableModel(new Object[]{"Mã SP", "Sản phẩm", "Số lượng"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(model);

        // Simple bar chart on the right
        SimpleBarChart chart = new SimpleBarChart("Số lượng bán");

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new JScrollPane(table), chart);
        // Chia 3|7: bên trái (bảng) chiếm 30%, bên phải (biểu đồ) 70%
        split.setResizeWeight(0.30);
        split.setDividerLocation(0.30);

        JPanel filter = createDateFilter((from, to) -> {
            model.setRowCount(0);
            List<Object[]> rows = statisticsService.getTopSellingProducts(from, to, 20);
            java.util.List<SimpleBarChart.Item> items = new ArrayList<>();
            for (Object[] r : rows) {
                model.addRow(r);
                String label = String.valueOf(r[1]);
                Number value = (Number) r[2];
                items.add(new SimpleBarChart.Item(label, value.doubleValue()));
            }
            chart.setItems(items);
        });

        panel.add(filter, BorderLayout.NORTH);
        panel.add(split, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createRevenueByEmployeeTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        DefaultTableModel model = new DefaultTableModel(new Object[]{"Mã NV", "Nhân viên", "Doanh thu"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(model);

        SimpleBarChart chart = new SimpleBarChart("Doanh thu");
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new JScrollPane(table), chart);
        split.setResizeWeight(0.55);

        JPanel filter = createDateFilter((from, to) -> {
            model.setRowCount(0);
            List<Object[]> rows = statisticsService.getRevenueByEmployee(from, to);
            java.util.List<SimpleBarChart.Item> items = new ArrayList<>();
            for (Object[] r : rows) {
                model.addRow(r);
                String label = String.valueOf(r[1]);
                Number value = (Number) r[2];
                items.add(new SimpleBarChart.Item(label, value.doubleValue()));
            }
            chart.setItems(items);
        });

        panel.add(filter, BorderLayout.NORTH);
        panel.add(split, BorderLayout.CENTER);
        return panel;
    }

    private interface DateRangeAction { void apply(Date from, Date to); }

    private JLabel lblRevenue;
    private JComboBox<String> cboMode;
    private DefaultTableModel revenueModel;
    private Date lastFrom, lastTo;
    private SimpleLineChart revenueChart;

    private JPanel createDateFilter(DateRangeAction action) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        p.setOpaque(false);
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        JTextField txtFrom = new JTextField(10);
        JTextField txtTo = new JTextField(10);
        LocalDate today = LocalDate.now();
        txtFrom.setText(today.minusDays(30).format(fmt));
        txtTo.setText(today.format(fmt));

        JButton btnApply = new JButton("Áp dụng");
        btnApply.addActionListener(e -> {
            try {
                Date from = Date.valueOf(LocalDate.parse(txtFrom.getText().trim(), fmt));
                Date to = Date.valueOf(LocalDate.parse(txtTo.getText().trim(), fmt));
                lastFrom = from; lastTo = to;
                action.apply(from, to);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Ngày không hợp lệ. Định dạng yyyy-MM-dd", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });
        p.add(new JLabel("Từ ngày:")); p.add(txtFrom);
        p.add(new JLabel("Đến ngày:")); p.add(txtTo);
        p.add(btnApply);
        return p;
    }

    private void applyRevenue(Date from, Date to) {
        // total
        BigDecimal total = statisticsService.getRevenue(from, to);
        lblRevenue.setText("Tổng doanh thu: " + total);
        // grouped
        java.util.List<Object[]> rows;
        String mode = (String) cboMode.getSelectedItem();
        if ("Tháng".equals(mode)) rows = statisticsService.getMonthlyRevenue(from, to);
        else if ("Năm".equals(mode)) rows = statisticsService.getYearlyRevenue(from, to);
        else rows = statisticsService.getDailyRevenue(from, to);
        revenueModel.setRowCount(0);
        java.util.List<SimpleLineChart.Point> pts = new java.util.ArrayList<>();
        for (Object[] r : rows) {
            revenueModel.addRow(r);
            String label = String.valueOf(r[0]);
            Number val = (Number) r[1];
            pts.add(new SimpleLineChart.Point(label, val.doubleValue()));
        }
        revenueChart.setPoints(pts);
    }

    /**
     * Very lightweight bar chart component without external libraries.
     */
    static class SimpleBarChart extends JPanel {
        private java.util.List<Item> items = new ArrayList<>();
        private String title;

        SimpleBarChart(String title) {
            this.title = title;
            setPreferredSize(new Dimension(480, 360));
            setBackground(Color.WHITE);
        }

        void setItems(java.util.List<Item> items) {
            this.items = items != null ? items : new ArrayList<>();
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int width = getWidth();
            int height = getHeight();
            int padding = 40;
            int labelPadding = 40;
            int topPadding = 40;
            int left = padding + labelPadding;
            int top = padding + topPadding;
            int chartWidth = width - left - padding;
            int chartHeight = height - top - padding;

            // Draw axes
            g2.setColor(Color.DARK_GRAY);
            g2.drawLine(left, top + chartHeight, left + chartWidth, top + chartHeight); // X axis
            g2.drawLine(left, top, left, top + chartHeight); // Y axis

            // Draw title
            g2.setFont(getFont().deriveFont(Font.BOLD, 14f));
            FontMetrics fm = g2.getFontMetrics();
            int titleW = fm.stringWidth(title);
            g2.drawString(title, left + (chartWidth - titleW) / 2, padding);
            g2.setFont(getFont());

            if (items.isEmpty()) {
                g2.drawString("Không có dữ liệu", left + 10, top + 20);
                g2.dispose();
                return;
            }

            double maxVal = items.stream().mapToDouble(i -> i.value).max().orElse(1);
            int barGap = 8;
            int barWidth = Math.max(10, (chartWidth - barGap * (items.size() + 1)) / Math.max(1, items.size()));
            int x = left + barGap;
            for (Item item : items) {
                int barH = (int) Math.round((item.value / maxVal) * (chartHeight - 20));
                int y = top + chartHeight - barH;
                g2.setColor(new Color(33, 150, 243));
                g2.fillRect(x, y, barWidth, barH);
                g2.setColor(new Color(25, 118, 210));
                g2.drawRect(x, y, barWidth, barH);

                // Label
                g2.setColor(Color.DARK_GRAY);
                String lbl = item.label.length() > 12 ? item.label.substring(0, 12) + "…" : item.label;
                int strW = g2.getFontMetrics().stringWidth(lbl);
                g2.drawString(lbl, x + Math.max(0, (barWidth - strW) / 2), top + chartHeight + 15);
                String v = String.valueOf((long) item.value);
                int vW = g2.getFontMetrics().stringWidth(v);
                g2.drawString(v, x + Math.max(0, (barWidth - vW) / 2), y - 4);

                x += barWidth + barGap;
            }
            g2.dispose();
        }

        static class Item { final String label; final double value; Item(String l, double v) { label = l; value = v; } }
    }

    /** Simple line chart */
    static class SimpleLineChart extends JPanel {
        private java.util.List<Point> points = new java.util.ArrayList<>();
        SimpleLineChart() { setPreferredSize(new Dimension(500, 300)); setBackground(Color.WHITE); }
        void setPoints(java.util.List<Point> pts) { this.points = pts != null ? pts : new java.util.ArrayList<>(); repaint(); }

        @Override protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth(), h = getHeight();
            int pad = 40; int left = 60; int top = 20; int cw = w - left - pad; int ch = h - top - pad;
            g2.setColor(Color.DARK_GRAY);
            g2.drawLine(left, top + ch, left + cw, top + ch);
            g2.drawLine(left, top, left, top + ch);

            if (points.isEmpty()) { g2.drawString("Không có dữ liệu", left + 10, top + 20); g2.dispose(); return; }
            double maxVal = points.stream().mapToDouble(p -> p.value).max().orElse(1);
            int n = points.size();
            double stepX = n > 1 ? (double) cw / (n - 1) : cw;
            int prevX = -1, prevY = -1;
            for (int i = 0; i < n; i++) {
                Point p = points.get(i);
                int x = (int) Math.round(left + i * stepX);
                int y = (int) Math.round(top + ch - (p.value / maxVal) * (ch - 10));
                g2.setColor(new Color(33, 150, 243));
                g2.fillOval(x - 3, y - 3, 6, 6);
                if (i > 0) {
                    g2.setColor(new Color(25, 118, 210));
                    g2.drawLine(prevX, prevY, x, y);
                }
                g2.setColor(Color.DARK_GRAY);
                String lbl = p.label.length() > 10 ? p.label.substring(0, 10) + "…" : p.label;
                int sw = g2.getFontMetrics().stringWidth(lbl);
                g2.drawString(lbl, x - sw / 2, top + ch + 15);
                prevX = x; prevY = y;
            }
            g2.dispose();
        }

        static class Point { final String label; final double value; Point(String l, double v) { label = l; value = v; } }
    }
}
