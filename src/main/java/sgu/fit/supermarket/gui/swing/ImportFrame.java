package sgu.fit.supermarket.gui.swing;

import sgu.fit.supermarket.bus.EmployeeService;
import sgu.fit.supermarket.bus.ImportReceiptService;
import sgu.fit.supermarket.bus.ProductService;
import sgu.fit.supermarket.bus.impl.EmployeeServiceImpl;
import sgu.fit.supermarket.bus.impl.ImportReceiptServiceImpl;
import sgu.fit.supermarket.bus.impl.ProductServiceImpl;
import sgu.fit.supermarket.dao.SupplierDAO;
import sgu.fit.supermarket.dao.impl.SupplierDAOImpl;
import sgu.fit.supermarket.dto.EmployeeDTO;
import sgu.fit.supermarket.dto.ImportReceiptDTO;
import sgu.fit.supermarket.dto.ImportReceiptDetailDTO;
import sgu.fit.supermarket.dto.ProductDTO;
import sgu.fit.supermarket.dto.SupplierDTO;
import sgu.fit.supermarket.util.UserSession;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class ImportFrame extends JPanel {
    private final ImportReceiptService importReceiptService;
    private final ProductService productService;
    private final SupplierDAO supplierDAO;
    private final EmployeeService employeeService;
    
    // Reference to MainFrame for navigation
    private MainFrame mainFrame;
    
    // Main receipt table
    private JTable receiptTable;
    private DefaultTableModel receiptTableModel;
    
    // Detail table for adding products to import
    private JTable detailTable;
    private DefaultTableModel detailTableModel;
    
    // Form fields
    private JComboBox<SupplierDTO> cboSupplier;
    private JTextField txtProductSearch;
    private JComboBox<ProductDTO> cboProduct;
    private JTextField txtQuantity;
    private JTextField txtCost;
    
    public ImportFrame() {
        this.importReceiptService = new ImportReceiptServiceImpl();
        this.productService = new ProductServiceImpl();
        this.supplierDAO = new SupplierDAOImpl();
        this.employeeService = new EmployeeServiceImpl();
        
        setLayout(new BorderLayout());
        setOpaque(true);
        setBackground(Color.WHITE);
        
        add(createHeader(), BorderLayout.NORTH);
        add(createCenter(), BorderLayout.CENTER);
        add(createFooter(), BorderLayout.SOUTH);
        
        loadSupplierOptions();
        loadProductOptions();
        loadReceiptTableData();
    }
    
    private JComponent createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));
        
        JLabel title = new JLabel("📥 Quản lý Nhập hàng");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        
        JButton btnRefresh = new JButton("Tải lại");
        btnRefresh.addActionListener(e -> {
            loadReceiptTableData();
            clearDetailTable();
        });
        
        header.add(title, BorderLayout.WEST);
        header.add(btnRefresh, BorderLayout.EAST);
        return header;
    }
    
    private JComponent createCenter() {
        JPanel center = new JPanel(new BorderLayout(10, 10));
        center.setOpaque(false);
        center.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));
        
        // Receipt table
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setResizeWeight(0.5);
        
        // Top: Receipt list
        String[] receiptColumns = {"ID", "Nhân viên", "Nhà cung cấp", "Ngày tạo"};
        receiptTableModel = new DefaultTableModel(receiptColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        receiptTable = new JTable(receiptTableModel);
        receiptTable.setRowHeight(24);
        receiptTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        receiptTable.getSelectionModel().addListSelectionListener(e -> onReceiptSelectionChanged());
        JScrollPane receiptScrollPane = new JScrollPane(receiptTable);
        
        JPanel receiptPanel = new JPanel(new BorderLayout());
        receiptPanel.setBorder(BorderFactory.createTitledBorder("Danh sách phiếu nhập hàng"));
        receiptPanel.add(receiptScrollPane, BorderLayout.CENTER);
        
        // Bottom: Form and detail table
        JPanel bottomPanel = new JPanel(new BorderLayout(10, 10));
        
        // Form panel
        JPanel formPanel = new JPanel();
        formPanel.setBorder(BorderFactory.createTitledBorder("Thông tin phiếu nhập hàng"));
        formPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        cboSupplier = new JComboBox<>();
        cboSupplier.setPrototypeDisplayValue(new SupplierDTO(0, "Nhà cung cấp", "", ""));
        
        txtProductSearch = new JTextField(15);
        txtProductSearch.addActionListener(e -> searchProducts());
        cboProduct = new JComboBox<>();
        cboProduct.setPrototypeDisplayValue(new ProductDTO(0, "Tên sản phẩm", "", "", 
            BigDecimal.ZERO, 0, 0, 0));
        
        txtQuantity = new JTextField(10);
        txtCost = new JTextField(10);
        
        int r = 0;
        addFormRow(formPanel, gbc, r++, "Nhà cung cấp:", cboSupplier);
        
        JPanel productSearchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        productSearchPanel.setOpaque(false);
        productSearchPanel.add(new JLabel("Tìm sản phẩm:"));
        productSearchPanel.add(txtProductSearch);
        JButton btnSearch = new JButton("Tìm");
        btnSearch.addActionListener(e -> searchProducts());
        productSearchPanel.add(btnSearch);
        gbc.gridx = 0; gbc.gridy = r; gbc.gridwidth = 2;
        formPanel.add(productSearchPanel, gbc);
        r++;
        
        addFormRow(formPanel, gbc, r++, "Sản phẩm:", cboProduct);
        addFormRow(formPanel, gbc, r++, "Số lượng:", txtQuantity);
        addFormRow(formPanel, gbc, r++, "Giá nhập:", txtCost);
        
        JButton btnAddProduct = new JButton("Thêm vào danh sách");
        btnAddProduct.addActionListener(e -> addProductToDetail());
        gbc.gridx = 0; gbc.gridy = r; gbc.gridwidth = 2;
        formPanel.add(btnAddProduct, gbc);
        
        // Detail table
        String[] detailColumns = {"Sản phẩm", "Số lượng", "Giá nhập", "Thành tiền", "Xóa"};
        detailTableModel = new DefaultTableModel(detailColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 4; // Only delete button is editable
            }
        };
        detailTable = new JTable(detailTableModel);
        detailTable.setRowHeight(24);
        detailTable.getColumn("Xóa").setCellRenderer(new ButtonRenderer());
        detailTable.getColumn("Xóa").setCellEditor(new ButtonEditor(new JCheckBox()));
        JScrollPane detailScrollPane = new JScrollPane(detailTable);
        
        JPanel detailPanel = new JPanel(new BorderLayout());
        detailPanel.setBorder(BorderFactory.createTitledBorder("Chi tiết phiếu nhập hàng"));
        detailPanel.add(detailScrollPane, BorderLayout.CENTER);
        
        JSplitPane bottomSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, formPanel, detailPanel);
        bottomSplit.setResizeWeight(0.4);
        
        bottomPanel.add(bottomSplit, BorderLayout.CENTER);
        
        splitPane.setTopComponent(receiptPanel);
        splitPane.setBottomComponent(bottomPanel);
        
        center.add(splitPane, BorderLayout.CENTER);
        return center;
    }
    
    private void addFormRow(JPanel form, GridBagConstraints gbc, int row, String label, JComponent field) {
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 1; gbc.weightx = 0;
        form.add(new JLabel(label), gbc);
        gbc.gridx = 1; gbc.gridy = row; gbc.weightx = 1;
        form.add(field, gbc);
    }
    
    private JComponent createFooter() {
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        footer.setOpaque(false);
        
        JButton btnClear = new JButton("Làm mới");
        JButton btnCreate = new JButton("Tạo phiếu nhập");
//        JButton btnViewDetails = new JButton("Xem chi tiết");
//        JButton btnDelete = new JButton("Xóa phiếu");
        
        btnClear.addActionListener(e -> clearForm());
        btnCreate.addActionListener(e -> createImportReceipt());
//        btnViewDetails.addActionListener(e -> viewReceiptDetails());
//        btnDelete.addActionListener(e -> deleteReceipt());
        
        footer.add(btnClear);
        footer.add(btnCreate);
//        footer.add(btnViewDetails);
//        footer.add(btnDelete);
        return footer;
    }
    
    private void loadSupplierOptions() {
        List<SupplierDTO> suppliers = supplierDAO.findAll();
        cboSupplier.removeAllItems();
        for (SupplierDTO s : suppliers) {
            cboSupplier.addItem(s);
        }
        cboSupplier.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, 
                    boolean isSelected, boolean cellHasFocus) {
                Component comp = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof SupplierDTO) {
                    setText(((SupplierDTO) value).getSupplierName());
                }
                return comp;
            }
        });
        cboSupplier.setSelectedIndex(-1);
    }
    
    private void loadProductOptions() {
        List<ProductDTO> products = productService.getAllProducts();
        cboProduct.removeAllItems();
        if (products != null) {
            for (ProductDTO p : products) {
                cboProduct.addItem(p);
            }
        }
        cboProduct.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, 
                    boolean isSelected, boolean cellHasFocus) {
                Component comp = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof ProductDTO) {
                    setText(((ProductDTO) value).getProductName());
                }
                return comp;
            }
        });
        cboProduct.setSelectedIndex(-1);
    }
    
    /**
     * Public method to refresh product options (called from MainFrame)
     */
    public void refreshProductOptions() {
        // Lưu lại sản phẩm đang được chọn (nếu có)
        ProductDTO selectedProduct = (ProductDTO) cboProduct.getSelectedItem();
        
        // Reload danh sách sản phẩm
        loadProductOptions();
        
        // Khôi phục lại selection nếu sản phẩm vẫn còn trong danh sách
        // Nếu sản phẩm đã bị xóa, selection sẽ bị clear
        if (selectedProduct != null) {
            boolean found = false;
            for (int i = 0; i < cboProduct.getItemCount(); i++) {
                if (cboProduct.getItemAt(i).getProductId() == selectedProduct.getProductId()) {
                    cboProduct.setSelectedIndex(i);
                    found = true;
                    break;
                }
            }
            // Nếu không tìm thấy (sản phẩm đã bị xóa), clear selection
            if (!found) {
                cboProduct.setSelectedIndex(-1);
            }
        }
    }
    
    private void searchProducts() {
        String keyword = txtProductSearch.getText().trim();
        List<ProductDTO> products;
        
        if (keyword.isEmpty()) {
            products = productService.getAllProducts();
        } else {
            products = productService.searchProductsByName(keyword);
        }
        
        // Lưu lại sản phẩm đang được chọn (nếu có)
        ProductDTO selectedProduct = (ProductDTO) cboProduct.getSelectedItem();
        
        cboProduct.removeAllItems();
        if (products != null) {
            for (ProductDTO p : products) {
                cboProduct.addItem(p);
            }
        }
        
        // Khôi phục lại selection nếu sản phẩm vẫn còn trong danh sách
        if (selectedProduct != null) {
            for (int i = 0; i < cboProduct.getItemCount(); i++) {
                if (cboProduct.getItemAt(i).getProductId() == selectedProduct.getProductId()) {
                    cboProduct.setSelectedIndex(i);
                    break;
                }
            }
        }
    }
    
    private void loadReceiptTableData() {
        receiptTableModel.setRowCount(0);
        List<ImportReceiptDTO> receipts = importReceiptService.getAllImportReceipts();
        if (receipts == null) return;
        
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        for (ImportReceiptDTO receipt : receipts) {
            // Get employee name
            String employeeName = "Nhân viên #" + receipt.getEmployeeId();
            EmployeeDTO emp = employeeService.findById(receipt.getEmployeeId());
            if (emp != null) {
                employeeName = emp.getFullName();
            }
            
            // Get supplier name
            SupplierDTO supplier = supplierDAO.findById(receipt.getSupplierId());
            String supplierName = supplier != null ? supplier.getSupplierName() : "N/A";
            
            String dateStr = receipt.getCreatedAt() != null ? 
                sdf.format(receipt.getCreatedAt()) : "N/A";
            
            receiptTableModel.addRow(new Object[]{
                receipt.getReceiptId(),
                employeeName,
                supplierName,
                dateStr
            });
        }
    }
    
    private void onReceiptSelectionChanged() {
        int row = receiptTable.getSelectedRow();
        if (row < 0) {
            clearDetailTable();
            return;
        }
        
        int receiptId = (Integer) receiptTableModel.getValueAt(row, 0);
        loadReceiptDetails(receiptId);
    }
    
    private void loadReceiptDetails(int receiptId) {
        clearDetailTable();
        List<ImportReceiptDetailDTO> details = importReceiptService.getImportReceiptDetails(receiptId);
        if (details == null) return;
        
        for (ImportReceiptDetailDTO detail : details) {
            ProductDTO product = productService.getProductById(detail.getProductId());
            String productName = product != null ? product.getProductName() : "N/A";
            BigDecimal total = detail.getCost().multiply(new BigDecimal(detail.getQuantity()));
            
            detailTableModel.addRow(new Object[]{
                productName,
                detail.getQuantity(),
                detail.getCost(),
                total,
                "Xóa"
            });
        }
    }
    
    private void addProductToDetail() {
        try {
            ProductDTO product = (ProductDTO) cboProduct.getSelectedItem();
            if (product == null) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn sản phẩm!");
                return;
            }
            
            String quantityStr = txtQuantity.getText().trim();
            String costStr = txtCost.getText().trim();
            
            if (quantityStr.isEmpty() || costStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin!");
                return;
            }
            
            int quantity = Integer.parseInt(quantityStr);
            BigDecimal cost = new BigDecimal(costStr);
            
            if (quantity <= 0 || cost.compareTo(BigDecimal.ZERO) <= 0) {
                JOptionPane.showMessageDialog(this, "Số lượng và giá phải lớn hơn 0!");
                return;
            }
            
            // Validate: giá nhập < giá bán
            if (product.getPrice() != null && cost.compareTo(product.getPrice()) >= 0) {
                JOptionPane.showMessageDialog(this, 
                    "Giá nhập (" + cost + ") phải nhỏ hơn giá bán (" + product.getPrice() + ")!", 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            BigDecimal total = cost.multiply(new BigDecimal(quantity));
            detailTableModel.addRow(new Object[]{
                product.getProductName(),
                quantity,
                cost,
                total,
                "Xóa"
            });
            
            // Clear form
            cboProduct.setSelectedIndex(-1);
            txtQuantity.setText("");
            txtCost.setText("");
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Dữ liệu không hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void createImportReceipt() {
        try {
            SupplierDTO supplier = (SupplierDTO) cboSupplier.getSelectedItem();
            if (supplier == null) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn nhà cung cấp!");
                return;
            }
            
            if (detailTableModel.getRowCount() == 0) {
                JOptionPane.showMessageDialog(this, "Vui lòng thêm ít nhất một sản phẩm!");
                return;
            }
            
            // Get current employee from session
            EmployeeDTO employee = UserSession.getInstance().getEmployee();
            if (employee == null) {
                JOptionPane.showMessageDialog(this, "Không thể xác định nhân viên!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Create receipt
            ImportReceiptDTO receipt = new ImportReceiptDTO();
            receipt.setEmployeeId(employee.getEmployeeId());
            receipt.setSupplierId(supplier.getSupplierId());
            receipt.setCreatedAt(new Timestamp(System.currentTimeMillis()));
            
            // Create details và validate giá nhập < giá bán
            List<ImportReceiptDetailDTO> details = new ArrayList<>();
            for (int i = 0; i < detailTableModel.getRowCount(); i++) {
                String productName = (String) detailTableModel.getValueAt(i, 0);
                int quantity = (Integer) detailTableModel.getValueAt(i, 1);
                BigDecimal cost = (BigDecimal) detailTableModel.getValueAt(i, 2);
                
                // Find product by name
                ProductDTO product = findProductByName(productName);
                if (product == null) {
                    JOptionPane.showMessageDialog(this, "Không tìm thấy sản phẩm: " + productName, 
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // Validate: giá nhập < giá bán
                if (product.getPrice() != null && cost.compareTo(product.getPrice()) >= 0) {
                    JOptionPane.showMessageDialog(this, 
                        "Sản phẩm \"" + productName + "\": Giá nhập (" + cost + 
                        ") phải nhỏ hơn giá bán (" + product.getPrice() + ")!", 
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                ImportReceiptDetailDTO detail = new ImportReceiptDetailDTO();
                detail.setProductId(product.getProductId());
                detail.setQuantity(quantity);
                detail.setCost(cost);
                details.add(detail);
            }
            
            boolean success = importReceiptService.createImportReceipt(receipt, details);
            if (success) {
                JOptionPane.showMessageDialog(this, "Tạo phiếu nhập hàng thành công!");
                loadReceiptTableData();
                clearForm();
                
                // Chuyển sang trang sản phẩm và refresh
                if (mainFrame != null) {
                    mainFrame.showCard("PRODUCT");
                    refreshProductFrame();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Tạo phiếu nhập hàng thất bại!", 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage(), 
                "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    private ProductDTO findProductByName(String productName) {
        List<ProductDTO> products = productService.getAllProducts();
        if (products == null) return null;
        for (ProductDTO p : products) {
            if (p.getProductName().equals(productName)) {
                return p;
            }
        }
        return null;
    }
    
    private void viewReceiptDetails() {
        int row = receiptTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn phiếu nhập hàng!");
            return;
        }
        
        int receiptId = (Integer) receiptTableModel.getValueAt(row, 0);
        loadReceiptDetails(receiptId);
    }
    
    private void deleteReceipt() {
        int row = receiptTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn phiếu nhập hàng để xóa!");
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Bạn có chắc muốn xóa phiếu nhập hàng này?", 
            "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;
        
        int receiptId = (Integer) receiptTableModel.getValueAt(row, 0);
        boolean success = importReceiptService.deleteImportReceipt(receiptId);
        if (success) {
            JOptionPane.showMessageDialog(this, "Xóa phiếu nhập hàng thành công!");
            loadReceiptTableData();
            clearDetailTable();
        } else {
            JOptionPane.showMessageDialog(this, "Xóa phiếu nhập hàng thất bại!", 
                "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void clearForm() {
        cboSupplier.setSelectedIndex(-1);
        txtProductSearch.setText("");
        cboProduct.setSelectedIndex(-1);
        txtQuantity.setText("");
        txtCost.setText("");
        clearDetailTable();
        receiptTable.clearSelection();
    }
    
    private void clearDetailTable() {
        detailTableModel.setRowCount(0);
    }
    
    /**
     * Set reference to MainFrame for navigation
     */
    public void setMainFrame(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
    }
    
    /**
     * Refresh ProductFrame if it exists
     */
    private void refreshProductFrame() {
        if (mainFrame != null) {
            mainFrame.refreshProductFrame();
        }
    }
    
    // Button renderer and editor for delete button
    private class ButtonRenderer extends JButton implements javax.swing.table.TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
        }
        
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            setText("Xóa");
            return this;
        }
    }
    
    private class ButtonEditor extends DefaultCellEditor {
        protected JButton button;
        private JTable table;
        
        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(e -> {
                fireEditingStopped();
                int selectedRow = table.getEditingRow();
                if (selectedRow >= 0) {
                    detailTableModel.removeRow(selectedRow);
                }
            });
        }
        
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            this.table = table;
            button.setText("Xóa");
            return button;
        }
        
        public Object getCellEditorValue() {
            return "Xóa";
        }
    }
}

