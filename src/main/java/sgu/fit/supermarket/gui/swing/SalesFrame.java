package sgu.fit.supermarket.gui.swing;

import sgu.fit.supermarket.bus.CustomerService;
import sgu.fit.supermarket.bus.InvoiceService;
import sgu.fit.supermarket.bus.ProductService;
import sgu.fit.supermarket.bus.impl.CustomerServiceImpl;
import sgu.fit.supermarket.bus.impl.InvoiceServiceImpl;
import sgu.fit.supermarket.bus.impl.ProductServiceImpl;
import sgu.fit.supermarket.dto.CustomerDTO;
import sgu.fit.supermarket.dto.InvoiceDTO;
import sgu.fit.supermarket.dto.InvoiceDetailDTO;
import sgu.fit.supermarket.dto.ProductDTO;
import sgu.fit.supermarket.util.UserSession;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class SalesFrame extends JPanel {
    private final ProductService productService;
    private final InvoiceService invoiceService;
    private final CustomerService customerService;
    private final DefaultTableModel cartModel;
    private final JTable cartTable;
    private final JTextField txtSearch;
    private final JPanel productsPanel;
    private final JLabel lblTotal;
    private final JComboBox<CustomerDTO> cboCustomer;
    
    // Redeem controls
    private JSpinner spnUsePoints;
    private JLabel lblDiscountValue;
    private JLabel lblPayableValue;

    public SalesFrame() {
        this.productService = new ProductServiceImpl();
        this.invoiceService = new InvoiceServiceImpl();
        this.customerService = new CustomerServiceImpl();

        setLayout(new BorderLayout());
        setOpaque(true);
        setBackground(Color.WHITE);

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));
        JLabel title = new JLabel("🛒 Bán hàng");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        header.add(title, BorderLayout.WEST);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
        searchPanel.setOpaque(false);
        txtSearch = new JTextField(24);
        txtSearch.addActionListener(e -> reloadProducts()); // Enter key to search
        JButton btnSearch = new JButton("Tìm kiếm");
        btnSearch.addActionListener(e -> reloadProducts());
        JButton btnRefresh = new JButton("Làm mới");
        btnRefresh.addActionListener(e -> {
            txtSearch.setText("");
            reloadProducts();
        });
        searchPanel.add(new JLabel("Từ khóa:"));
        searchPanel.add(txtSearch);
        searchPanel.add(btnSearch);
        searchPanel.add(btnRefresh);
        header.add(searchPanel, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        // Center: products list + cart
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        split.setResizeWeight(0.65); // 65% cho sản phẩm, 35% cho giỏ hàng
        split.setDividerSize(8); // Kích thước divider
        split.setOneTouchExpandable(false); // Không cho phép collapse hoàn toàn
        split.setContinuousLayout(true); // Cập nhật layout liên tục khi drag

        // Products panel (grid with images)
        productsPanel = new JPanel();
        productsPanel.setOpaque(true);
        productsPanel.setBackground(Color.WHITE);
        productsPanel.setLayout(new WrapLayout(FlowLayout.LEFT, 12, 12));
        productsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JScrollPane productsScroll = new JScrollPane(productsPanel);
        productsScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        productsScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        productsScroll.getVerticalScrollBar().setUnitIncrement(16);
        productsScroll.getVerticalScrollBar().setBlockIncrement(64);
        productsScroll.setBorder(BorderFactory.createEmptyBorder());
        productsScroll.setViewportBorder(BorderFactory.createEmptyBorder());
        // Set minimum width cho phần sản phẩm (ít nhất 400px)
        productsScroll.setMinimumSize(new Dimension(400, 0));
        productsScroll.setPreferredSize(new Dimension(800, 0));

        // Cart panel
        JPanel cartPanel = new JPanel(new BorderLayout());
        cartPanel.setOpaque(false);
        cartPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        // Set minimum width cho phần giỏ hàng (ít nhất 350px)
        cartPanel.setMinimumSize(new Dimension(350, 0));
        cartPanel.setPreferredSize(new Dimension(400, 0));
        
        String[] cartCols = {"ID", "Sản phẩm", "SL", "Giá bán", "Thành tiền"};
        cartModel = new DefaultTableModel(cartCols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 2; // allow editing quantity
            }
        };
        cartTable = new JTable(cartModel);
        cartTable.setRowHeight(24);
        cartTable.getModel().addTableModelListener(e -> updateTotal());

        JPanel cartHeader = new JPanel(new BorderLayout());
        cartHeader.setOpaque(false);
        
        // Customer selection panel
        JPanel customerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        customerPanel.setOpaque(false);
        customerPanel.add(new JLabel("Khách hàng:"));
        cboCustomer = new JComboBox<>();
        cboCustomer.setPrototypeDisplayValue(new CustomerDTO(0, "Chọn khách hàng (tùy chọn)", "", 0));
        cboCustomer.addItem(null); // Allow null selection
        loadCustomers();
        cboCustomer.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, 
                    boolean isSelected, boolean cellHasFocus) {
                Component comp = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value == null) {
                    setText("Không chọn (Bán lẻ)");
                } else if (value instanceof CustomerDTO) {
                    CustomerDTO c = (CustomerDTO) value;
                    setText(c.getFullName() + " - " + c.getPhone() + " (Điểm: " + c.getPoints() + ")");
                }
                return comp;
            }
        });
        customerPanel.add(cboCustomer);
        
        // Total and buttons panel
        JPanel totalPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        totalPanel.setOpaque(false);
        lblTotal = new JLabel("Tổng: 0");
        lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 16));
        JButton btnClearCart = new JButton("Xóa giỏ");
        btnClearCart.addActionListener(e -> clearCart());
        totalPanel.add(lblTotal);
        totalPanel.add(Box.createHorizontalStrut(16));
        totalPanel.add(btnClearCart);
        
        cartHeader.add(customerPanel, BorderLayout.NORTH);
        cartHeader.add(totalPanel, BorderLayout.SOUTH);

        cartPanel.add(cartHeader, BorderLayout.NORTH);
        cartPanel.add(new JScrollPane(cartTable), BorderLayout.CENTER);
        
        // Footer: redeem points and checkout
        JPanel cartFooter = new JPanel(new BorderLayout());
        cartFooter.setOpaque(false);
        cartFooter.setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));
        cartFooter.setMinimumSize(new Dimension(0, 44));
        cartFooter.setPreferredSize(new Dimension(0, 44));
        // Left controls
        JPanel footerLeft = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        footerLeft.setOpaque(false);
        JLabel lblUse = new JLabel("Dùng điểm:");
        javax.swing.SpinnerNumberModel model = new javax.swing.SpinnerNumberModel(0, 0, 0, 1); // max sẽ set động
        this.spnUsePoints = new JSpinner(model);
        ((JSpinner.DefaultEditor) spnUsePoints.getEditor()).getTextField().setColumns(3);
        this.lblDiscountValue = new JLabel("Giảm: 0");
        this.lblPayableValue = new JLabel("Phải trả: 0");
        footerLeft.add(lblUse);
        footerLeft.add(spnUsePoints);
        footerLeft.add(Box.createHorizontalStrut(6));
        footerLeft.add(this.lblDiscountValue);
        footerLeft.add(Box.createHorizontalStrut(6));
        footerLeft.add(this.lblPayableValue);
        cartFooter.add(footerLeft, BorderLayout.CENTER);
        // Right fixed checkout button
        JButton btnCheckout = new JButton("Thanh toán");
        btnCheckout.setFocusable(false);
        btnCheckout.setPreferredSize(new Dimension(110, 28));
        btnCheckout.addActionListener(e -> checkout());
        JPanel footerRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        footerRight.setOpaque(false);
        footerRight.add(btnCheckout);
        cartFooter.add(footerRight, BorderLayout.EAST);
        cartPanel.add(cartFooter, BorderLayout.SOUTH);

        // Recalculate totals and bounds when customer or cart changes
        cboCustomer.addActionListener(e -> updateTotalsWithPoints());
        cartTable.getModel().addTableModelListener(e -> updateTotalsWithPoints());
        ((JSpinner.DefaultEditor) spnUsePoints.getEditor()).getTextField().setColumns(4);
        spnUsePoints.addChangeListener(e -> updateTotalsWithPoints());

        split.setLeftComponent(productsScroll);
        split.setRightComponent(cartPanel);
        // Không cho phép divider di chuyển quá xa
        split.setDividerLocation(800); // Set vị trí divider ban đầu
        
        add(split, BorderLayout.CENTER);

        reloadProducts();
        // Initialize footer totals once at start
        updateTotalsWithPoints();
    }

    private void reloadProducts() {
        productsPanel.removeAll();
        String keyword = txtSearch.getText().trim();
        List<ProductDTO> products = keyword.isEmpty() ? productService.getAllProducts() : productService.searchProductsByName(keyword);
        if (products != null) {
            for (ProductDTO p : products) {
                productsPanel.add(createProductCard(p));
            }
        }
        productsPanel.revalidate();
        productsPanel.repaint();
    }

    private JPanel createProductCard(ProductDTO product) {
        JPanel card = new JPanel();
        card.setPreferredSize(new Dimension(180, 230));
        card.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        card.setLayout(new BorderLayout());
        card.setBackground(Color.WHITE);

        JLabel img = new JLabel();
        img.setHorizontalAlignment(SwingConstants.CENTER);
        img.setPreferredSize(new Dimension(180, 140));
        ImageIcon icon = loadProductImage(product.getImagePath(), 160, 120);
        if (icon != null) img.setIcon(icon);
        card.add(img, BorderLayout.NORTH);

        JPanel info = new JPanel(new GridLayout(0, 1));
        info.setOpaque(false);
        info.setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));
        info.add(new JLabel(product.getProductName()));
        info.add(new JLabel("Giá: " + product.getPrice()));
        info.add(new JLabel("Kho: " + product.getStock()));
        card.add(info, BorderLayout.CENTER);

        JButton btnAdd = new JButton("Thêm");
        btnAdd.addActionListener(e -> addToCart(product));
        card.add(btnAdd, BorderLayout.SOUTH);

        return card;
    }

    private ImageIcon loadProductImage(String imagePath, int w, int h) {
        try {
            BufferedImage image = null;
            // 1) Nếu đường dẫn là classpath (bắt đầu bằng '/assets/') thì ưu tiên đọc từ resources
            if (imagePath != null && imagePath.trim().startsWith("/assets/")) {
                InputStream is = getClass().getResourceAsStream(imagePath.trim());
                if (is != null) {
                    image = ImageIO.read(is);
                }
            }
            // 2) Nếu chưa có, thử đọc theo file hệ thống (đường dẫn tuyệt đối/relative)
            if (image == null && imagePath != null && !imagePath.trim().isEmpty()) {
                File f = new File(imagePath.trim());
                if (f.exists()) {
                    image = ImageIO.read(f);
                }
            }
            // 3) Fallback ảnh mặc định trong resources
            if (image == null) {
                String resourcePath = "/assets/images/product.png"; // cập nhật theo thư mục mới
                InputStream is = getClass().getResourceAsStream(resourcePath);
                if (is == null) {
                    // fallback cũ nếu dự án đang dùng product_images
                    is = getClass().getResourceAsStream("/assets/product_images/product.png");
                }
                if (is != null) image = ImageIO.read(is);
            }
            if (image != null) {
                Image scaled = image.getScaledInstance(w, h, Image.SCALE_SMOOTH);
                return new ImageIcon(scaled);
            }
        } catch (Exception ignored) {}
        return null;
    }

    private void addToCart(ProductDTO product) {
        // If exists, increase quantity
        for (int i = 0; i < cartModel.getRowCount(); i++) {
            int id = Integer.parseInt(String.valueOf(cartModel.getValueAt(i, 0)));
            if (id == product.getProductId()) {
                int qty = Integer.parseInt(String.valueOf(cartModel.getValueAt(i, 2)));
                cartModel.setValueAt(qty + 1, i, 2);
                updateRowTotal(i);
                return;
            }
        }
        cartModel.addRow(new Object[]{
            product.getProductId(),
            product.getProductName(),
            1,
            product.getPrice(),
            product.getPrice()
        });
        updateTotal();
    }

    private void updateRowTotal(int row) {
        int qty = Integer.parseInt(String.valueOf(cartModel.getValueAt(row, 2)));
        BigDecimal price = new BigDecimal(String.valueOf(cartModel.getValueAt(row, 3)));
        cartModel.setValueAt(price.multiply(BigDecimal.valueOf(qty)), row, 4);
        updateTotal();
    }

    private void updateTotal() {
        BigDecimal total = BigDecimal.ZERO;
        for (int i = 0; i < cartModel.getRowCount(); i++) {
            BigDecimal line = new BigDecimal(String.valueOf(cartModel.getValueAt(i, 4)));
            total = total.add(line);
        }
        lblTotal.setText("Tổng: " + total);
    }
    
    // Update totals and allowed redeem points, and show discount & payable labels
    private void updateTotalsWithPoints() {
        // Subtotal
        BigDecimal subtotal = BigDecimal.ZERO;
        for (int i = 0; i < cartModel.getRowCount(); i++) {
            BigDecimal line = new BigDecimal(String.valueOf(cartModel.getValueAt(i, 4)));
            subtotal = subtotal.add(line);
        }
        lblTotal.setText("Tổng: " + subtotal);
        
        // Determine max usable points = min(customer points, floor(subtotal/1000))
        int customerPoints = 0;
        Object sel = cboCustomer.getSelectedItem();
        if (sel instanceof CustomerDTO) {
            customerPoints = ((CustomerDTO) sel).getPoints();
        }
        int maxByMoney = subtotal.divide(new BigDecimal(1000), 0, java.math.RoundingMode.DOWN).intValue();
        int maxUsable = Math.max(0, Math.min(customerPoints, maxByMoney));
        javax.swing.SpinnerNumberModel model = (javax.swing.SpinnerNumberModel) spnUsePoints.getModel();
        model.setMaximum(maxUsable);
        if ((Integer) model.getNumber() > maxUsable) {
            model.setValue(maxUsable);
        }
        int usePoints = (Integer) model.getNumber();
        BigDecimal discount = new BigDecimal(usePoints).multiply(new BigDecimal(1000));
        if (discount.compareTo(subtotal) > 0) discount = subtotal;
        BigDecimal payable = subtotal.subtract(discount);
        lblDiscountValue.setText("Giảm: " + discount);
        lblPayableValue.setText("Phải trả: " + payable);
    }

    private void clearCart() {
        cartModel.setRowCount(0);
        updateTotal();
    }

    private void loadCustomers() {
        List<CustomerDTO> customers = customerService.getAllCustomers();
        if (customers != null) {
            for (CustomerDTO c : customers) {
                cboCustomer.addItem(c);
            }
        }
    }

    private void checkout() {
        if (cartModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Giỏ hàng trống!");
            return;
        }
        try {
            // Build invoice
            InvoiceDTO invoice = new InvoiceDTO();
            if (!UserSession.getInstance().isLoggedIn() || UserSession.getInstance().getEmployee() == null) {
                JOptionPane.showMessageDialog(this, "Không xác định được nhân viên!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            invoice.setEmployeeId(UserSession.getInstance().getEmployee().getEmployeeId());
            
            // Set customer (can be null)
            CustomerDTO selectedCustomer = (CustomerDTO) cboCustomer.getSelectedItem();
            if (selectedCustomer != null) {
                invoice.setCustomerId(selectedCustomer.getCustomerId());
            } else {
                invoice.setCustomerId(0); // 0 means no customer (retail sale)
            }
            
            invoice.setCreatedAt(new Timestamp(System.currentTimeMillis()));
            BigDecimal total = BigDecimal.ZERO;
            List<InvoiceDetailDTO> details = new ArrayList<>();
            for (int i = 0; i < cartModel.getRowCount(); i++) {
                int productId = Integer.parseInt(String.valueOf(cartModel.getValueAt(i, 0)));
                int qty = Integer.parseInt(String.valueOf(cartModel.getValueAt(i, 2)));
                BigDecimal price = new BigDecimal(String.valueOf(cartModel.getValueAt(i, 3)));
                total = total.add(price.multiply(BigDecimal.valueOf(qty)));
                InvoiceDetailDTO d = new InvoiceDetailDTO();
                d.setProductId(productId);
                d.setQuantity(qty);
                d.setPrice(price);
                details.add(d);
            }
            // Apply discount by used points
            int pointsToUseAtCheckout = (Integer) spnUsePoints.getValue();
            BigDecimal discountAtCheckout = new BigDecimal(pointsToUseAtCheckout).multiply(new BigDecimal(1000));
            if (discountAtCheckout.compareTo(total) > 0) discountAtCheckout = total;
            BigDecimal payableAtCheckout = total.subtract(discountAtCheckout);
            invoice.setTotalAmount(payableAtCheckout);

            boolean ok = invoiceService.createInvoice(invoice, details);
            if (ok) {
                // Cộng điểm cho khách hàng nếu có
                if (selectedCustomer != null) {
                    // Tính điểm: mỗi sản phẩm = 1 điểm (tính theo số lượng sản phẩm)
                    int pointsToAdd = 0;
                    for (InvoiceDetailDTO detail : details) {
                        pointsToAdd += detail.getQuantity(); // Mỗi sản phẩm cộng 1 điểm
                    }
                    
                    // Trừ điểm nếu có dùng (đã giới hạn ở UI)
                    int pointsToUse = (Integer) spnUsePoints.getValue();
                    if (pointsToUse > 0) {
                        customerService.addPoints(selectedCustomer.getCustomerId(), -pointsToUse);
                    }
                    
                    if (pointsToAdd > 0) {
                        customerService.addPoints(selectedCustomer.getCustomerId(), pointsToAdd);
                    }
                    JOptionPane.showMessageDialog(this, 
                        "Thanh toán thành công!\nĐã trừ " + pointsToUse + " điểm và cộng " + pointsToAdd + " điểm cho khách hàng " + selectedCustomer.getFullName() + ".");
                    
                    // Reset spinner sau khi thanh toán
                    spnUsePoints.setValue(0);
                    
                    // Refresh customer list
                    cboCustomer.removeAllItems();
                    cboCustomer.addItem(null);
                    loadCustomers();
                } else {
                    JOptionPane.showMessageDialog(this, "Thanh toán thành công!");
                }
                clearCart();
            } else {
                JOptionPane.showMessageDialog(this, "Thanh toán thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    // Simple wrap layout for grid-like flow
    static class WrapLayout extends FlowLayout {
        public WrapLayout(int align, int hgap, int vgap) {
            super(align, hgap, vgap);
        }
        @Override
        public Dimension preferredLayoutSize(Container target) {
            return layoutSize(target, true);
        }
        private Dimension layoutSize(Container target, boolean preferred) {
            synchronized (target.getTreeLock()) {
                int targetWidth = target.getWidth() == 0 ? 800 : target.getWidth();
                int hgap = getHgap();
                int vgap = getVgap();
                Insets insets = target.getInsets();
                int maxWidth = targetWidth - (insets.left + insets.right + hgap * 2);
                int x = 0, y = insets.top + vgap;
                int rowHeight = 0;
                for (Component m : target.getComponents()) {
                    if (!m.isVisible()) continue;
                    Dimension d = preferred ? m.getPreferredSize() : m.getMinimumSize();
                    if (x == 0 || x + d.width <= maxWidth) {
                        if (x > 0) x += hgap;
                        x += d.width;
                        rowHeight = Math.max(rowHeight, d.height);
                    } else {
                        x = d.width;
                        y += vgap + rowHeight;
                        rowHeight = d.height;
                    }
                }
                y += rowHeight + insets.bottom + vgap;
                return new Dimension(targetWidth, y);
            }
        }
    }
}
