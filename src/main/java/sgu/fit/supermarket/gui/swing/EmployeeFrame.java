package sgu.fit.supermarket.gui.swing;

import sgu.fit.supermarket.bus.EmployeeService;
import sgu.fit.supermarket.bus.impl.EmployeeServiceImpl;
import sgu.fit.supermarket.dto.EmployeeDTO;
import sgu.fit.supermarket.bus.RoleService;
import sgu.fit.supermarket.bus.impl.RoleServiceImpl;
import sgu.fit.supermarket.dto.RoleDTO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Date;
import java.util.List;

public class EmployeeFrame extends JPanel {
    private final EmployeeService employeeService;

    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtId;
    private JTextField txtName;
    private JTextField txtPhone;
    private JTextField txtAddress;
    private JTextField txtDob; // yyyy-mm-dd
    private JComboBox<String> cboGender;
    private JComboBox<RoleDTO> cboRole;
    private JTextField txtSearch;

    public EmployeeFrame() {
        this.employeeService = new EmployeeServiceImpl();
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
        JLabel title = new JLabel("👨‍💼 Quản lý Nhân viên");
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

        tableModel = new DefaultTableModel(new String[]{"ID", "Họ tên", "SĐT", "Địa chỉ", "Ngày sinh", "Giới tính", "Role ID"}, 0) {
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

        txtId = new JTextField(); txtId.setEditable(false);
        txtName = new JTextField();
        txtPhone = new JTextField();
        txtAddress = new JTextField();
        txtDob = new JTextField();
        cboGender = new JComboBox<>(new String[]{"Male", "Female"});
        cboGender.setSelectedIndex(-1);
        cboRole = new JComboBox<>();

        int r = 0;
        addFormRow(form, gbc, r++, "ID", txtId);
        addFormRow(form, gbc, r++, "Họ tên", txtName);
        addFormRow(form, gbc, r++, "Số điện thoại", txtPhone);
        addFormRow(form, gbc, r++, "Địa chỉ", txtAddress);
        addFormRow(form, gbc, r++, "Ngày sinh (yyyy-mm-dd)", txtDob);
        addFormRow(form, gbc, r++, "Giới tính", cboGender);
        addFormRow(form, gbc, r++, "Vai trò", cboRole);

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, scroll, form);
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

    private JComponent createFooter() {
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        footer.setOpaque(false);
        JButton btnClear = new JButton("Làm mới");
        JButton btnAdd = new JButton("Thêm");
        JButton btnUpdate = new JButton("Cập nhật");
//        JButton btnDelete = new JButton("Xóa");
        btnClear.addActionListener(e -> clearForm());
        btnAdd.addActionListener(e -> add());
        btnUpdate.addActionListener(e -> update());
//        btnDelete.addActionListener(e -> deleteItem());
//        footer.add(btnClear); footer.add(btnAdd); footer.add(btnUpdate); footer.add(btnDelete);
        return footer;
    }

    private void loadTableData() {
        tableModel.setRowCount(0);
        List<EmployeeDTO> list = employeeService.findAll();
        if (list == null) return;
        for (EmployeeDTO e : list) {
            tableModel.addRow(new Object[]{ e.getEmployeeId(), e.getFullName(), e.getPhone(), e.getAddress(), e.getDateOfBirth(), e.getGender(), e.getRoleId() });
        }
    }

    private void loadRoles() {
        RoleService roleService = new RoleServiceImpl();
        List<RoleDTO> roles = roleService.findAll();
        cboRole.removeAllItems();
        if (roles != null) {
            for (RoleDTO r : roles) {
                cboRole.addItem(r);
            }
        }
        cboRole.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                Component comp = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof RoleDTO) {
                    setText(((RoleDTO) value).getRoleName() + " (ID: " + ((RoleDTO) value).getRoleId() + ")");
                }
                return comp;
            }
        });
        cboRole.setSelectedIndex(-1);
    }

    private void search() {
        List<EmployeeDTO> list = employeeService.search(txtSearch.getText());
        tableModel.setRowCount(0);
        if (list == null) return;
        for (EmployeeDTO e : list) {
            tableModel.addRow(new Object[]{ e.getEmployeeId(), e.getFullName(), e.getPhone(), e.getAddress(), e.getDateOfBirth(), e.getGender(), e.getRoleId() });
        }
    }

    private void onRowSelected() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        txtId.setText(String.valueOf(table.getValueAt(row, 0)));
        txtName.setText(String.valueOf(table.getValueAt(row, 1)));
        txtPhone.setText(String.valueOf(table.getValueAt(row, 2)));
        txtAddress.setText(String.valueOf(table.getValueAt(row, 3)));
        txtDob.setText(String.valueOf(table.getValueAt(row, 4)));
        cboGender.setSelectedItem(String.valueOf(table.getValueAt(row, 5)));
        selectRoleById(Integer.parseInt(String.valueOf(table.getValueAt(row, 6))));
    }

    private void clearForm() {
        txtId.setText("");
        txtName.setText("");
        txtPhone.setText("");
        txtAddress.setText("");
        txtDob.setText("");
        cboGender.setSelectedIndex(-1);
        cboRole.setSelectedIndex(-1);
        table.clearSelection();
    }

    private void add() {
        try {
            EmployeeDTO e = readForm(false);
            boolean ok = employeeService.add(e);
            if (ok) { JOptionPane.showMessageDialog(this, "Thêm nhân viên thành công!"); loadTableData(); clearForm(); }
            else { JOptionPane.showMessageDialog(this, "Thêm nhân viên thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE); }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Dữ liệu không hợp lệ", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void update() {
        if (txtId.getText().trim().isEmpty()) { JOptionPane.showMessageDialog(this, "Vui lòng chọn nhân viên để cập nhật!"); return; }
        try {
            EmployeeDTO e = readForm(true);
            boolean ok = employeeService.update(e);
            if (ok) { JOptionPane.showMessageDialog(this, "Cập nhật nhân viên thành công!"); loadTableData(); }
            else { JOptionPane.showMessageDialog(this, "Cập nhật nhân viên thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE); }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Dữ liệu không hợp lệ", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void deleteItem() {
        if (txtId.getText().trim().isEmpty()) { JOptionPane.showMessageDialog(this, "Vui lòng chọn nhân viên để xóa!"); return; }
        int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn xóa nhân viên này?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;
        int id = Integer.parseInt(txtId.getText().trim());
        try {
            boolean ok = employeeService.delete(id);
            if (ok) { 
                JOptionPane.showMessageDialog(this, "Xóa nhân viên thành công!"); 
                loadTableData(); 
                clearForm(); 
            } else { 
                JOptionPane.showMessageDialog(this, "Xóa nhân viên thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE); 
            }
        } catch (RuntimeException e) {
            // Display the error message from the service layer
            String errorMessage = e.getMessage();
            if (errorMessage != null && errorMessage.contains("Không thể xóa")) {
                JOptionPane.showMessageDialog(this, errorMessage, "Không thể xóa", JOptionPane.WARNING_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Xóa nhân viên thất bại: " + (errorMessage != null ? errorMessage : "Lỗi không xác định"), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            // Handle other exceptions
            String errorMessage = e.getMessage();
            if (errorMessage != null && errorMessage.contains("Không thể xóa")) {
                JOptionPane.showMessageDialog(this, errorMessage, "Không thể xóa", JOptionPane.WARNING_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Xóa nhân viên thất bại: " + (errorMessage != null ? errorMessage : "Lỗi không xác định"), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private EmployeeDTO readForm(boolean requireId) throws Exception {
        String name = txtName.getText().trim();
        if (name.isEmpty()) throw new Exception("Họ tên không được trống");
        String phone = txtPhone.getText().trim();
        String address = txtAddress.getText().trim();
        String dobStr = txtDob.getText().trim();
        String gender = cboGender.getSelectedItem() == null ? "" : String.valueOf(cboGender.getSelectedItem());
        RoleDTO selectedRole = (RoleDTO) cboRole.getSelectedItem();
        if (gender.isEmpty()) throw new Exception("Vui lòng chọn giới tính");
        if (selectedRole == null) throw new Exception("Vui lòng chọn vai trò");
        Date dob = null;
        if (!dobStr.isEmpty()) {
            try { dob = Date.valueOf(dobStr); } catch (Exception e) { throw new Exception("Ngày sinh không hợp lệ (yyyy-mm-dd)"); }
        }

        EmployeeDTO emp = new EmployeeDTO();
        if (requireId) emp.setEmployeeId(Integer.parseInt(txtId.getText().trim()));
        emp.setFullName(name);
        emp.setPhone(phone);
        emp.setAddress(address);
        emp.setDateOfBirth(dob);
        emp.setGender(gender);
        emp.setRoleId(selectedRole.getRoleId());
        return emp;
    }

    @Override
    public void addNotify() {
        super.addNotify();
        // load roles when component is added to UI to ensure DB ready
        loadRoles();
    }

    private void selectRoleById(int roleId) {
        for (int i = 0; i < cboRole.getItemCount(); i++) {
            RoleDTO r = cboRole.getItemAt(i);
            if (r.getRoleId() == roleId) {
                cboRole.setSelectedIndex(i);
                break;
            }
        }
    }
}
