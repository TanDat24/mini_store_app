package sgu.fit.supermarket.gui.swing;

import sgu.fit.supermarket.bus.ProductService;
import sgu.fit.supermarket.bus.impl.ProductServiceImpl;
import sgu.fit.supermarket.dao.CategoryDAO;
import sgu.fit.supermarket.dao.SupplierDAO;
import sgu.fit.supermarket.dao.impl.CategoryDAOImpl;
import sgu.fit.supermarket.dao.impl.SupplierDAOImpl;
import sgu.fit.supermarket.dto.CategoryDTO;
import sgu.fit.supermarket.dto.ProductDTO;
import sgu.fit.supermarket.dto.SupplierDTO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.util.List;

public class ProductFrame extends JPanel {
    private final ProductService productService;

    private JTable table;
    private DefaultTableModel tableModel;

    private JTextField txtId;
    private JTextField txtName;
    private JTextField txtUnit;
    private JTextField txtPrice;
    private JTextField txtStock;
    private JComboBox<CategoryDTO> cboCategory;
    private JComboBox<SupplierDTO> cboSupplier;
    private JTextField txtImagePath;
    private JTextField txtSearch;

    public ProductFrame() {
        this.productService = new ProductServiceImpl();
        setLayout(new BorderLayout());
        setOpaque(true);
        setBackground(Color.WHITE);

        add(createHeader(), BorderLayout.NORTH);
        add(createCenter(), BorderLayout.CENTER);
        add(createFooter(), BorderLayout.SOUTH);

        loadCategoryOptions();
        loadSupplierOptions();
        loadTableData();
    }

    private JComponent createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JLabel title = new JLabel("📦 Quản lý Sản phẩm");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
        searchPanel.setOpaque(false);
        txtSearch = new JTextField(24);
        JButton btnSearch = new JButton("Tìm kiếm");
        JButton btnRefresh = new JButton("Tải lại");

        btnSearch.addActionListener(e -> searchProducts());
        btnRefresh.addActionListener(e -> {
            txtSearch.setText("");
            loadTableData();
        });

        searchPanel.add(new JLabel("Từ khóa:"));
        searchPanel.add(txtSearch);
        searchPanel.add(btnSearch);
        searchPanel.add(btnRefresh);

        header.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));
        header.add(title, BorderLayout.WEST);
        header.add(searchPanel, BorderLayout.EAST);
        return header;
    }

    private JComponent createCenter() {
        JPanel center = new JPanel(new BorderLayout(10, 10));
        center.setOpaque(false);
        center.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));

        // Table
        String[] columns = {"ID", "Tên", "Đơn vị", "Giá", "Tồn kho", "Danh mục", "Nhà CC", "Ảnh"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        table.setRowHeight(24);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getSelectionModel().addListSelectionListener(e -> onTableSelectionChanged());
        JScrollPane scrollPane = new JScrollPane(table);

        // Form
        JPanel form = new JPanel();
        form.setOpaque(false);
        form.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        txtId = new JTextField();
        txtId.setEditable(false);
        txtName = new JTextField();
        txtUnit = new JTextField();
        txtPrice = new JTextField();
        txtStock = new JTextField();
        cboCategory = new JComboBox<>();
        cboSupplier = new JComboBox<>();
        cboCategory.setPrototypeDisplayValue(new CategoryDTO(0, "Danh mục"));
        cboSupplier.setPrototypeDisplayValue(new SupplierDTO(0, "Nhà cung cấp", "", ""));
        txtImagePath = new JTextField();

        int r = 0;
        addFormRow(form, gbc, r++, "ID", txtId);
        addFormRow(form, gbc, r++, "Tên sản phẩm", txtName);
        addFormRow(form, gbc, r++, "Đơn vị", txtUnit);
        addFormRow(form, gbc, r++, "Giá", txtPrice);
        addFormRow(form, gbc, r++, "Tồn kho", txtStock);
        addFormRow(form, gbc, r++, "Danh mục", cboCategory);
        addFormRow(form, gbc, r++, "Nhà cung cấp", cboSupplier);
        addFormRow(form, gbc, r++, "Đường dẫn ảnh", createImagePicker());

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, scrollPane, form);
        split.setResizeWeight(0.65);
        center.add(split, BorderLayout.CENTER);
        return center;
    }

    private void addFormRow(JPanel form, GridBagConstraints gbc, int row, String label, JComponent field) {
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        form.add(new JLabel(label), gbc);
        gbc.gridx = 1; gbc.gridy = row; gbc.weightx = 1;
        form.add(field, gbc);
    }

    private JComponent createImagePicker() {
        JPanel p = new JPanel(new BorderLayout(6, 0));
        p.setOpaque(false);
        JButton btnBrowse = new JButton("Chọn...");
        btnBrowse.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Chọn ảnh sản phẩm");
            chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            chooser.setAcceptAllFileFilterUsed(true);
            int result = chooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION && chooser.getSelectedFile() != null) {
                txtImagePath.setText(chooser.getSelectedFile().getAbsolutePath());
            }
        });
        p.add(txtImagePath, BorderLayout.CENTER);
        p.add(btnBrowse, BorderLayout.EAST);
        return p;
    }

    private JComponent createFooter() {
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        footer.setOpaque(false);

        JButton btnClear = new JButton("Làm mới");
        JButton btnAdd = new JButton("Thêm");
        JButton btnUpdate = new JButton("Cập nhật");
        JButton btnDelete = new JButton("Xóa");

        btnClear.addActionListener(e -> clearForm());
        btnAdd.addActionListener(e -> addProduct());
        btnUpdate.addActionListener(e -> updateProduct());
        btnDelete.addActionListener(e -> deleteProduct());

        footer.add(btnClear);
        footer.add(btnAdd);
        footer.add(btnUpdate);
        footer.add(btnDelete);
        return footer;
    }

    private void loadTableData() {
        tableModel.setRowCount(0);
        List<ProductDTO> products = productService.getAllProducts();
        if (products == null) return;
        for (ProductDTO p : products) {
            tableModel.addRow(new Object[]{
                p.getProductId(),
                p.getProductName(),
                p.getUnit(),
                p.getPrice(),
                p.getStock(),
                p.getCategoryId(),
                p.getSupplierId(),
                p.getImagePath()
            });
        }
    }

    private void searchProducts() {
        String keyword = txtSearch.getText();
        List<ProductDTO> products = productService.searchProductsByName(keyword);
        tableModel.setRowCount(0);
        if (products == null) return;
        for (ProductDTO p : products) {
            tableModel.addRow(new Object[]{
                p.getProductId(), p.getProductName(), p.getUnit(), p.getPrice(), p.getStock(),
                p.getCategoryId(), p.getSupplierId(), p.getImagePath()
            });
        }
    }

    private void onTableSelectionChanged() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        txtId.setText(String.valueOf(table.getValueAt(row, 0)));
        txtName.setText(String.valueOf(table.getValueAt(row, 1)));
        txtUnit.setText(String.valueOf(table.getValueAt(row, 2)));
        txtPrice.setText(String.valueOf(table.getValueAt(row, 3)));
        txtStock.setText(String.valueOf(table.getValueAt(row, 4)));
        selectCategoryById(Integer.parseInt(String.valueOf(table.getValueAt(row, 5))));
        selectSupplierById(Integer.parseInt(String.valueOf(table.getValueAt(row, 6))));
        txtImagePath.setText(String.valueOf(table.getValueAt(row, 7)));
    }

    private void clearForm() {
        txtId.setText("");
        txtName.setText("");
        txtUnit.setText("");
        txtPrice.setText("");
        txtStock.setText("");
        cboCategory.setSelectedIndex(-1);
        cboSupplier.setSelectedIndex(-1);
        txtImagePath.setText("");
        table.clearSelection();
    }

    private void addProduct() {
        try {
            ProductDTO product = readForm(false);
            boolean ok = productService.addProduct(product);
            if (ok) {
                JOptionPane.showMessageDialog(this, "Thêm sản phẩm thành công!");
                loadTableData();
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this, "Thêm sản phẩm thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Dữ liệu không hợp lệ", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void updateProduct() {
        if (txtId.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn sản phẩm để cập nhật!");
            return;
        }
        try {
            ProductDTO product = readForm(true);
            boolean ok = productService.updateProduct(product);
            if (ok) {
                JOptionPane.showMessageDialog(this, "Cập nhật sản phẩm thành công!");
                loadTableData();
            } else {
                JOptionPane.showMessageDialog(this, "Cập nhật sản phẩm thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Dữ liệu không hợp lệ", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void deleteProduct() {
        if (txtId.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn sản phẩm để xóa!");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn xóa sản phẩm này?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        int id = Integer.parseInt(txtId.getText().trim());
        boolean ok = productService.deleteProduct(id);
        if (ok) {
            JOptionPane.showMessageDialog(this, "Xóa sản phẩm thành công!");
            loadTableData();
            clearForm();
        } else {
            JOptionPane.showMessageDialog(this, "Xóa sản phẩm thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private ProductDTO readForm(boolean requireId) throws Exception {
        String name = txtName.getText().trim();
        String unit = txtUnit.getText().trim();
        String priceStr = txtPrice.getText().trim();
        String stockStr = txtStock.getText().trim();
        CategoryDTO selectedCategory = (CategoryDTO) cboCategory.getSelectedItem();
        SupplierDTO selectedSupplier = (SupplierDTO) cboSupplier.getSelectedItem();
        String img = txtImagePath.getText().trim();

        if (name.isEmpty()) throw new Exception("Tên sản phẩm không được trống");
        BigDecimal price;
        try { price = new BigDecimal(priceStr); } catch (Exception e) { throw new Exception("Giá không hợp lệ"); }
        int stock;
        try { stock = Integer.parseInt(stockStr); } catch (Exception e) { throw new Exception("Tồn kho không hợp lệ"); }
        if (selectedCategory == null) throw new Exception("Vui lòng chọn Danh mục");
        if (selectedSupplier == null) throw new Exception("Vui lòng chọn Nhà cung cấp");

        ProductDTO p = new ProductDTO();
        if (requireId) {
            p.setProductId(Integer.parseInt(txtId.getText().trim()));
        }
        p.setProductName(name);
        p.setUnit(unit);
        p.setPrice(price);
        p.setStock(stock);
        p.setCategoryId(selectedCategory.getCategoryId());
        p.setSupplierId(selectedSupplier.getSupplierId());
        p.setImagePath(img);
        return p;
    }

    private void loadCategoryOptions() {
        CategoryDAO categoryDAO = new CategoryDAOImpl();
        List<CategoryDTO> categories = categoryDAO.findAll();
        cboCategory.removeAllItems();
        for (CategoryDTO c : categories) {
            cboCategory.addItem(c);
        }
        cboCategory.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                Component comp = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof CategoryDTO) {
                    setText(((CategoryDTO) value).getCategoryName());
                }
                return comp;
            }
        });
        cboCategory.setSelectedIndex(-1);
    }

    private void loadSupplierOptions() {
        SupplierDAO supplierDAO = new SupplierDAOImpl();
        List<SupplierDTO> suppliers = supplierDAO.findAll();
        cboSupplier.removeAllItems();
        for (SupplierDTO s : suppliers) {
            cboSupplier.addItem(s);
        }
        cboSupplier.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                Component comp = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof SupplierDTO) {
                    setText(((SupplierDTO) value).getSupplierName());
                }
                return comp;
            }
        });
        cboSupplier.setSelectedIndex(-1);
    }

    private void selectCategoryById(int categoryId) {
        for (int i = 0; i < cboCategory.getItemCount(); i++) {
            CategoryDTO c = cboCategory.getItemAt(i);
            if (c.getCategoryId() == categoryId) {
                cboCategory.setSelectedIndex(i);
                break;
            }
        }
    }

    private void selectSupplierById(int supplierId) {
        for (int i = 0; i < cboSupplier.getItemCount(); i++) {
            SupplierDTO s = cboSupplier.getItemAt(i);
            if (s.getSupplierId() == supplierId) {
                cboSupplier.setSelectedIndex(i);
                break;
            }
        }
    }
}

