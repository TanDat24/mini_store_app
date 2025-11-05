package sgu.fit.supermarket.gui.swing;

import sgu.fit.supermarket.bus.SupplierService;
import sgu.fit.supermarket.bus.impl.SupplierServiceImpl;
import sgu.fit.supermarket.dto.SupplierDTO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class SupplierFrame extends JPanel {
    private final SupplierService supplierService;

    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtId;
    private JTextField txtName;
    private JTextField txtPhone;
    private JTextField txtAddress;
    private JTextField txtSearch;

    public SupplierFrame() {
        this.supplierService = new SupplierServiceImpl();
        setLayout(new BorderLayout());
        setOpaque(true);
        setBackground(Color.WHITE);

        add(createHeader(), BorderLayout.NORTH);
        add(createCenter(), BorderLayout.CENTER);
        add(createFooter(), BorderLayout.SOUTH);

        loadTableData();
    }

    private JComponent createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JLabel title = new JLabel("🏭 Quản lý Nhà cung cấp");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
        searchPanel.setOpaque(false);
        txtSearch = new JTextField(24);
        JButton btnSearch = new JButton("Tìm kiếm");
        JButton btnRefresh = new JButton("Tải lại");
        btnSearch.addActionListener(e -> search());
        btnRefresh.addActionListener(e -> { txtSearch.setText(""); loadTableData(); });

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

        tableModel = new DefaultTableModel(new String[]{"ID", "Tên NCC", "SĐT", "Địa chỉ"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        table.setRowHeight(24);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getSelectionModel().addListSelectionListener(e -> onRowSelected());
        JScrollPane scroll = new JScrollPane(table);

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        txtId = new JTextField();
        txtId.setEditable(false);
        txtName = new JTextField();
        txtPhone = new JTextField();
        txtAddress = new JTextField();

        addFormRow(form, gbc, 0, "ID", txtId);
        addFormRow(form, gbc, 1, "Tên NCC", txtName);
        addFormRow(form, gbc, 2, "Số điện thoại", txtPhone);
        addFormRow(form, gbc, 3, "Địa chỉ", txtAddress);

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, scroll, form);
        split.setResizeWeight(0.7);
        center.add(split, BorderLayout.CENTER);
        return center;
    }

    private void addFormRow(JPanel form, GridBagConstraints gbc, int row, String label, JComponent field) {
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        form.add(new JLabel(label), gbc);
        gbc.gridx = 1; gbc.gridy = row; gbc.weightx = 1;
        form.add(field, gbc);
    }

    private JComponent createFooter() {
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        footer.setOpaque(false);
        JButton btnClear = new JButton("Làm mới");
        JButton btnAdd = new JButton("Thêm");
        JButton btnUpdate = new JButton("Cập nhật");
        JButton btnDelete = new JButton("Xóa");
        btnClear.addActionListener(e -> clearForm());
        btnAdd.addActionListener(e -> add());
        btnUpdate.addActionListener(e -> update());
        btnDelete.addActionListener(e -> deleteItem());
        footer.add(btnClear); footer.add(btnAdd); footer.add(btnUpdate); footer.add(btnDelete);
        return footer;
    }

    private void loadTableData() {
        tableModel.setRowCount(0);
        List<SupplierDTO> list = supplierService.getAllSuppliers();
        if (list == null) return;
        for (SupplierDTO s : list) {
            tableModel.addRow(new Object[]{ s.getSupplierId(), s.getSupplierName(), s.getPhone(), s.getAddress() });
        }
    }

    private void search() {
        List<SupplierDTO> list = supplierService.searchSuppliersByName(txtSearch.getText());
        tableModel.setRowCount(0);
        if (list == null) return;
        for (SupplierDTO s : list) {
            tableModel.addRow(new Object[]{ s.getSupplierId(), s.getSupplierName(), s.getPhone(), s.getAddress() });
        }
    }

    private void onRowSelected() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        txtId.setText(String.valueOf(table.getValueAt(row, 0)));
        txtName.setText(String.valueOf(table.getValueAt(row, 1)));
        txtPhone.setText(String.valueOf(table.getValueAt(row, 2)));
        txtAddress.setText(String.valueOf(table.getValueAt(row, 3)));
    }

    private void clearForm() {
        txtId.setText("");
        txtName.setText("");
        txtPhone.setText("");
        txtAddress.setText("");
        table.clearSelection();
    }

    private void add() {
        String name = txtName.getText().trim();
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tên NCC không được trống");
            return;
        }
        SupplierDTO s = new SupplierDTO();
        s.setSupplierName(name);
        s.setPhone(txtPhone.getText().trim());
        s.setAddress(txtAddress.getText().trim());
        boolean ok = supplierService.addSupplier(s);
        if (ok) {
            JOptionPane.showMessageDialog(this, "Thêm NCC thành công!");
            loadTableData();
            clearForm();
        } else {
            JOptionPane.showMessageDialog(this, "Thêm NCC thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void update() {
        if (txtId.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn NCC để cập nhật!");
            return;
        }
        String name = txtName.getText().trim();
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tên NCC không được trống");
            return;
        }
        SupplierDTO s = new SupplierDTO();
        s.setSupplierId(Integer.parseInt(txtId.getText().trim()));
        s.setSupplierName(name);
        s.setPhone(txtPhone.getText().trim());
        s.setAddress(txtAddress.getText().trim());
        boolean ok = supplierService.updateSupplier(s);
        if (ok) {
            JOptionPane.showMessageDialog(this, "Cập nhật NCC thành công!");
            loadTableData();
        } else {
            JOptionPane.showMessageDialog(this, "Cập nhật NCC thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteItem() {
        if (txtId.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn NCC để xóa!");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn xóa nhà cung cấp này?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;
        int id = Integer.parseInt(txtId.getText().trim());
        boolean ok = supplierService.deleteSupplier(id);
        if (ok) {
            JOptionPane.showMessageDialog(this, "Xóa NCC thành công!");
            loadTableData();
            clearForm();
        } else {
            JOptionPane.showMessageDialog(this, "Xóa NCC thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}
