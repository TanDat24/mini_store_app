package sgu.fit.supermarket.gui.swing;

import sgu.fit.supermarket.bus.AccountService;
import sgu.fit.supermarket.bus.impl.AccountServiceImpl;
import sgu.fit.supermarket.dto.AccountDTO;
import sgu.fit.supermarket.bus.EmployeeService;
import sgu.fit.supermarket.bus.impl.EmployeeServiceImpl;
import sgu.fit.supermarket.dto.EmployeeDTO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class AccountFrame extends JPanel {
    private final AccountService accountService;

    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtId;
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JComboBox<EmployeeDTO> cboEmployee;
    private JTextField txtSearch;

    public AccountFrame() {
        this.accountService = new AccountServiceImpl();
        setLayout(new BorderLayout());
        setOpaque(true);
        setBackground(Color.WHITE);

        add(createHeader(), BorderLayout.NORTH);
        add(createCenter(), BorderLayout.CENTER);
        add(createFooter(), BorderLayout.SOUTH);

        loadEmployees();
        loadTableData();
    }

    private JComponent createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JLabel title = new JLabel("👤 Quản lý Tài khoản");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
        searchPanel.setOpaque(false);
        txtSearch = new JTextField(24);
        JButton btnSearch = new JButton("Tải lại");
        btnSearch.addActionListener(e -> loadTableData());
        searchPanel.add(btnSearch);

        header.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));
        header.add(title, BorderLayout.WEST);
        header.add(searchPanel, BorderLayout.EAST);
        return header;
    }

    private JComponent createCenter() {
        JPanel center = new JPanel(new BorderLayout(10, 10));
        center.setOpaque(false);
        center.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));

        tableModel = new DefaultTableModel(new String[]{"ID", "Username", "Employee ID"}, 0) {
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
        txtUsername = new JTextField();
        txtPassword = new JPasswordField();
        cboEmployee = new JComboBox<>();

        addFormRow(form, gbc, 0, "ID", txtId);
        addFormRow(form, gbc, 1, "Username", txtUsername);
        addFormRow(form, gbc, 2, "Password", txtPassword);
        addFormRow(form, gbc, 3, "Nhân viên", cboEmployee);

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
        List<AccountDTO> list = accountService.getAllAccounts();
        if (list == null) return;
        for (AccountDTO a : list) {
            tableModel.addRow(new Object[]{ a.getAccountId(), a.getUsername(), a.getEmployeeId() });
        }
    }

    private void onRowSelected() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        txtId.setText(String.valueOf(table.getValueAt(row, 0)));
        txtUsername.setText(String.valueOf(table.getValueAt(row, 1)));
        selectEmployeeById(Integer.parseInt(String.valueOf(table.getValueAt(row, 2))));
        // Không hiển thị password từ DB vì lý do bảo mật
        txtPassword.setText("");
    }

    private void clearForm() {
        txtId.setText("");
        txtUsername.setText("");
        txtPassword.setText("");
        cboEmployee.setSelectedIndex(-1);
        table.clearSelection();
    }

    private void add() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());
        if (username.isEmpty() || password.isEmpty()) { JOptionPane.showMessageDialog(this, "Username/Password không được trống"); return; }
        EmployeeDTO selectedEmp = (EmployeeDTO) cboEmployee.getSelectedItem();
        if (selectedEmp == null) { JOptionPane.showMessageDialog(this, "Vui lòng chọn nhân viên"); return; }
        AccountDTO a = new AccountDTO();
        a.setUsername(username); a.setPassword(password); a.setEmployeeId(selectedEmp.getEmployeeId());
        boolean ok = accountService.addAccount(a);
        if (ok) { JOptionPane.showMessageDialog(this, "Thêm tài khoản thành công!"); loadTableData(); clearForm(); }
        else { JOptionPane.showMessageDialog(this, "Thêm tài khoản thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE); }
    }

    private void update() {
        if (txtId.getText().trim().isEmpty()) { JOptionPane.showMessageDialog(this, "Vui lòng chọn tài khoản để cập nhật!"); return; }
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());
        if (username.isEmpty() || password.isEmpty()) { JOptionPane.showMessageDialog(this, "Username/Password không được trống"); return; }
        EmployeeDTO selectedEmp = (EmployeeDTO) cboEmployee.getSelectedItem();
        if (selectedEmp == null) { JOptionPane.showMessageDialog(this, "Vui lòng chọn nhân viên"); return; }
        AccountDTO a = new AccountDTO();
        a.setAccountId(Integer.parseInt(txtId.getText().trim()));
        a.setUsername(username); a.setPassword(password); a.setEmployeeId(selectedEmp.getEmployeeId());
        boolean ok = accountService.updateAccount(a);
        if (ok) { JOptionPane.showMessageDialog(this, "Cập nhật tài khoản thành công!"); loadTableData(); }
        else { JOptionPane.showMessageDialog(this, "Cập nhật tài khoản thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE); }
    }

    private void deleteItem() {
        if (txtId.getText().trim().isEmpty()) { JOptionPane.showMessageDialog(this, "Vui lòng chọn tài khoản để xóa!"); return; }
        int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn xóa tài khoản này?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;
        int id = Integer.parseInt(txtId.getText().trim());
        boolean ok = accountService.deleteAccount(id);
        if (ok) { JOptionPane.showMessageDialog(this, "Xóa tài khoản thành công!"); loadTableData(); clearForm(); }
        else { JOptionPane.showMessageDialog(this, "Xóa tài khoản thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE); }
    }

    private void loadEmployees() {
        EmployeeService employeeService = new EmployeeServiceImpl();
        List<EmployeeDTO> employees = employeeService.findAll();
        cboEmployee.removeAllItems();
        if (employees != null) {
            for (EmployeeDTO e : employees) {
                cboEmployee.addItem(e);
            }
        }
        cboEmployee.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                Component comp = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof EmployeeDTO) {
                    setText(((EmployeeDTO) value).getFullName() + " (ID: " + ((EmployeeDTO) value).getEmployeeId() + ")");
                }
                return comp;
            }
        });
        cboEmployee.setSelectedIndex(-1);
    }

    private void selectEmployeeById(int employeeId) {
        for (int i = 0; i < cboEmployee.getItemCount(); i++) {
            EmployeeDTO e = cboEmployee.getItemAt(i);
            if (e.getEmployeeId() == employeeId) {
                cboEmployee.setSelectedIndex(i);
                break;
            }
        }
    }
}


