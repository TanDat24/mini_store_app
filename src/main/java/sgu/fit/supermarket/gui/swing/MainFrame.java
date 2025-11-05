package sgu.fit.supermarket.gui.swing;

import sgu.fit.supermarket.dto.AccountDTO;
import sgu.fit.supermarket.dto.EmployeeDTO;
import sgu.fit.supermarket.dto.RoleDTO;
import sgu.fit.supermarket.util.PermissionHelper;
import sgu.fit.supermarket.util.UserSession;

import javax.swing.*;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

public class MainFrame extends JFrame {
    private JPanel contentPanel;
    private CardLayout cardLayout;
    private JLabel lblUserInfo;
    private JLabel lblTime;
    private JLabel lblEmployeeInfo;
    
    // Current user info
    private AccountDTO currentAccount;
    private EmployeeDTO currentEmployee;
    private RoleDTO currentRole;
    private String currentUsername = "Admin";
    private String currentEmployeeName = "Nhân viên";
    private String currentRoleName = "";
    
    // Menu buttons for permission control
    private MenuButton btnProduct;
    private MenuButton btnCategory;
    private MenuButton btnSupplier;
    private MenuButton btnEmployee;
    private MenuButton btnInvoice;
    private MenuButton btnImport;
    private JButton btnNewInvoice;
    private JButton btnNewProduct;
    private JButton btnNewImport;
    
    public MainFrame() {
        this(null, null, null);
    }
    
    public MainFrame(AccountDTO account, EmployeeDTO employee, RoleDTO role) {
        this.currentAccount = account;
        this.currentEmployee = employee;
        this.currentRole = role;
        
        if (account != null) {
            this.currentUsername = account.getUsername();
        }
        if (employee != null) {
            this.currentEmployeeName = employee.getFullName();
        }
        if (role != null) {
            this.currentRoleName = role.getRoleName();
        }
        
        initializeFrame();
        createMenuBar();
        createSidebar();
        createContentArea();
        createDashboard();
        applyPermissions();
        startClock();
    }
    
    private void initializeFrame() {
        setTitle("Hệ Thống Quản Lý Cửa Hàng Tiện Lợi");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1400, 900);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        
        // Set look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(new Color(76, 175, 80));
        menuBar.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        
        // File Menu
        JMenu fileMenu = new JMenu("Hệ thống");
        fileMenu.setForeground(Color.WHITE);
        fileMenu.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        
        JMenuItem logoutItem = new JMenuItem("Đăng xuất");
        logoutItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, InputEvent.CTRL_DOWN_MASK));
        logoutItem.addActionListener(e -> performLogout());
        
        JMenuItem exitItem = new JMenuItem("Thoát");
        exitItem.addActionListener(e -> {
            int option = JOptionPane.showConfirmDialog(
                this,
                "Bạn có chắc chắn muốn thoát ứng dụng?",
                "Xác nhận",
                JOptionPane.YES_NO_OPTION
            );
            if (option == JOptionPane.YES_OPTION) {
                System.exit(0);
            }
        });
        
        fileMenu.add(logoutItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);
        
        // Help Menu
        JMenu helpMenu = new JMenu("Trợ giúp");
        helpMenu.setForeground(Color.WHITE);
        helpMenu.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        
        JMenuItem aboutItem = new JMenuItem("Giới thiệu");
        aboutItem.addActionListener(e -> {
            JOptionPane.showMessageDialog(
                this,
                "Hệ Thống Quản Lý Cửa Hàng Tiện Lợi\n" +
                "Version 1.0\n\n" +
                "Phát triển bởi: Nhóm SV",
                "Giới thiệu",
                JOptionPane.INFORMATION_MESSAGE
            );
        });
        
        helpMenu.add(aboutItem);
        
        // User info label with role
        String userInfoText = "👤 " + currentEmployeeName;
        if (!currentRoleName.isEmpty()) {
            userInfoText += " - " + currentRoleName;
        }
        userInfoText += " (" + currentUsername + ")";
        lblUserInfo = new JLabel(userInfoText);
        lblUserInfo.setForeground(Color.WHITE);
        lblUserInfo.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblUserInfo.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));
        
        // Employee info label (optional - có thể hiển thị thêm thông tin)
        if (currentEmployee != null) {
            String empInfo = "";
            if (currentEmployee.getPhone() != null && !currentEmployee.getPhone().isEmpty()) {
                empInfo = "📞 " + currentEmployee.getPhone();
            }
            lblEmployeeInfo = new JLabel(empInfo);
            lblEmployeeInfo.setForeground(new Color(255, 255, 255, 200));
            lblEmployeeInfo.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            lblEmployeeInfo.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));
        }
        
        // Time label
        lblTime = new JLabel();
        lblTime.setForeground(Color.WHITE);
        lblTime.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblTime.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
        
        menuBar.add(fileMenu);
        menuBar.add(helpMenu);
        menuBar.add(Box.createHorizontalGlue());
        menuBar.add(lblTime);
        menuBar.add(Box.createHorizontalStrut(10));
        if (lblEmployeeInfo != null) {
            menuBar.add(lblEmployeeInfo);
            menuBar.add(Box.createHorizontalStrut(5));
        }
        menuBar.add(lblUserInfo);
        
        setJMenuBar(menuBar);
    }
    
    private void createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setBackground(new Color(33, 150, 243));
        sidebar.setPreferredSize(new Dimension(250, 0));
        sidebar.setLayout(new BorderLayout());
        
        // Header
        JPanel sidebarHeader = new JPanel();
        sidebarHeader.setBackground(new Color(25, 118, 210));
        sidebarHeader.setBorder(BorderFactory.createEmptyBorder(20, 15, 20, 15));
        sidebarHeader.setLayout(new BorderLayout());
        
        JLabel lblTitle = new JLabel("🛒 MENU");
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        
        sidebarHeader.add(lblTitle, BorderLayout.CENTER);
        
        // Menu items
        JPanel menuPanel = new JPanel();
        menuPanel.setBackground(new Color(33, 150, 243));
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        
        // Create menu buttons
        MenuButton btnDashboard = createMenuButton("📊 Dashboard", "DASHBOARD");
        btnProduct = createMenuButton("📦 Sản phẩm", "PRODUCT");
        btnCategory = createMenuButton("📁 Danh mục", "CATEGORY");
        btnSupplier = createMenuButton("🏭 Nhà cung cấp", "SUPPLIER");
        MenuButton btnCustomer = createMenuButton("👥 Khách hàng", "CUSTOMER");
        btnEmployee = createMenuButton("👨‍💼 Nhân viên", "EMPLOYEE");
        btnInvoice = createMenuButton("🧾 Hóa đơn", "INVOICE");
        btnImport = createMenuButton("📥 Nhập hàng", "IMPORT");
        
        menuPanel.add(btnDashboard);
        menuPanel.add(Box.createVerticalStrut(10));
        menuPanel.add(btnProduct);
        menuPanel.add(Box.createVerticalStrut(10));
        menuPanel.add(btnCategory);
        menuPanel.add(Box.createVerticalStrut(10));
        menuPanel.add(btnSupplier);
        menuPanel.add(Box.createVerticalStrut(10));
        menuPanel.add(btnCustomer);
        menuPanel.add(Box.createVerticalStrut(10));
        menuPanel.add(btnEmployee);
        menuPanel.add(Box.createVerticalStrut(10));
        menuPanel.add(btnInvoice);
        menuPanel.add(Box.createVerticalStrut(10));
        menuPanel.add(btnImport);
        menuPanel.add(Box.createVerticalGlue());
        
        // Logout button at bottom
        JPanel logoutPanel = new JPanel();
        logoutPanel.setOpaque(false);
        logoutPanel.setLayout(new BorderLayout());
        logoutPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 20, 10));
        
        MenuButton btnLogout = new MenuButton("🚪 Đăng xuất");
        btnLogout.setBackground(new Color(244, 67, 54)); // Red color for logout
        btnLogout.addActionListener(e -> performLogout());
        btnLogout.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnLogout.setBackground(new Color(229, 57, 53));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnLogout.setBackground(new Color(244, 67, 54));
            }
        });
        
        logoutPanel.add(btnLogout, BorderLayout.CENTER);
        
        sidebar.add(sidebarHeader, BorderLayout.NORTH);
        sidebar.add(menuPanel, BorderLayout.CENTER);
        sidebar.add(logoutPanel, BorderLayout.SOUTH);
        
        add(sidebar, BorderLayout.WEST);
    }
    
    private void performLogout() {
        int option = JOptionPane.showConfirmDialog(
            this,
            "Bạn có chắc chắn muốn đăng xuất?",
            "Xác nhận đăng xuất",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        
        if (option == JOptionPane.YES_OPTION) {
            // Clear session
            UserSession.getInstance().clear();
            
            // Dispose current frame
            dispose();
            
            // Show login frame
            SwingUtilities.invokeLater(() -> {
                LoginFrame loginFrame = new LoginFrame();
                loginFrame.setVisible(true);
            });
        }
    }
    
    private MenuButton createMenuButton(String text, String cardName) {
        MenuButton btn = new MenuButton(text);
        btn.addActionListener(e -> showCard(cardName));
        return btn;
    }
    
    private void createContentArea() {
        contentPanel = new JPanel();
        cardLayout = new CardLayout();
        contentPanel.setLayout(cardLayout);
        contentPanel.setBackground(Color.WHITE);
        
        add(contentPanel, BorderLayout.CENTER);
    }
    
    private void createDashboard() {
        JPanel dashboardPanel = new JPanel();
        dashboardPanel.setBackground(new Color(245, 245, 245));
        dashboardPanel.setLayout(new BorderLayout());
        dashboardPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        
        // Header
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        headerPanel.setOpaque(false);
        JLabel headerLabel = new JLabel("📊 Dashboard - Tổng quan");
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        headerLabel.setForeground(new Color(33, 33, 33));
        headerPanel.add(headerLabel);
        
        // Stats cards
        JPanel statsPanel = new JPanel(new GridLayout(2, 4, 20, 20));
        statsPanel.setOpaque(false);
        statsPanel.setBorder(BorderFactory.createEmptyBorder(30, 0, 30, 0));
        
        // Stat cards
        statsPanel.add(createStatCard("📦 Tổng sản phẩm", "1,234", new Color(76, 175, 80)));
        statsPanel.add(createStatCard("📁 Danh mục", "15", new Color(33, 150, 243)));
        statsPanel.add(createStatCard("👥 Khách hàng", "567", new Color(255, 152, 0)));
        statsPanel.add(createStatCard("👨‍💼 Nhân viên", "12", new Color(156, 39, 176)));
        statsPanel.add(createStatCard("🧾 Hóa đơn hôm nay", "89", new Color(244, 67, 54)));
        statsPanel.add(createStatCard("💰 Doanh thu hôm nay", "15,500,000đ", new Color(0, 150, 136)));
        statsPanel.add(createStatCard("📥 Nhập hàng", "23", new Color(255, 87, 34)));
        statsPanel.add(createStatCard("🏭 Nhà cung cấp", "8", new Color(121, 85, 72)));
        
        // Quick actions
        JPanel quickActionsPanel = new JPanel();
        quickActionsPanel.setOpaque(false);
        quickActionsPanel.setLayout(new GridLayout(1, 3, 20, 0));
        quickActionsPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));
        
        JLabel quickActionsLabel = new JLabel("⚡ Thao tác nhanh:");
        quickActionsLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        quickActionsLabel.setForeground(new Color(33, 33, 33));
        
        btnNewInvoice = new JButton("➕ Tạo hóa đơn mới");
        styleQuickActionButton(btnNewInvoice, new Color(76, 175, 80));
        btnNewInvoice.addActionListener(e -> showCard("INVOICE"));
        
        btnNewProduct = new JButton("➕ Thêm sản phẩm");
        styleQuickActionButton(btnNewProduct, new Color(33, 150, 243));
        btnNewProduct.addActionListener(e -> showCard("PRODUCT"));
        
        btnNewImport = new JButton("➕ Nhập hàng");
        styleQuickActionButton(btnNewImport, new Color(255, 152, 0));
        btnNewImport.addActionListener(e -> showCard("IMPORT"));
        
        quickActionsPanel.add(btnNewInvoice);
        quickActionsPanel.add(btnNewProduct);
        quickActionsPanel.add(btnNewImport);
        
        // Welcome message
        JPanel welcomePanel = new JPanel();
        welcomePanel.setOpaque(false);
        welcomePanel.setLayout(new BorderLayout());
        welcomePanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        
        JLabel welcomeLabel = new JLabel("<html><div style='text-align: center;'>" +
            "<h2>Chào mừng trở lại, " + currentEmployeeName + "!</h2>" +
            "<p style='color: #666; font-size: 14px;'>Hệ thống quản lý cửa hàng tiện lợi của bạn đã sẵn sàng.</p>" +
            "</div></html>");
        welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        welcomePanel.add(welcomeLabel, BorderLayout.CENTER);
        
        // Center panel
        JPanel centerPanel = new JPanel();
        centerPanel.setOpaque(false);
        centerPanel.setLayout(new BorderLayout());
        centerPanel.add(statsPanel, BorderLayout.CENTER);
        centerPanel.add(quickActionsPanel, BorderLayout.SOUTH);
        
        dashboardPanel.add(headerPanel, BorderLayout.NORTH);
        dashboardPanel.add(centerPanel, BorderLayout.CENTER);
        dashboardPanel.add(welcomePanel, BorderLayout.SOUTH);
        
        contentPanel.add(dashboardPanel, "DASHBOARD");
    }
    
    private JPanel createStatCard(String title, String value, Color color) {
        JPanel card = new JPanel();
        card.setBackground(Color.WHITE);
        card.setLayout(new BorderLayout());
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        titleLabel.setForeground(new Color(100, 100, 100));
        
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        valueLabel.setForeground(color);
        
        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BorderLayout());
        content.add(titleLabel, BorderLayout.NORTH);
        content.add(valueLabel, BorderLayout.CENTER);
        
        card.add(content, BorderLayout.CENTER);
        
        return card;
    }
    
    private void styleQuickActionButton(JButton btn, Color color) {
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setPreferredSize(new Dimension(0, 50));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(color.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(color);
            }
        });
    }
    
    private void showCard(String cardName) {
        // Create panel if not exists
        if (cardName.equals("PRODUCT")) {
            createProductPanel();
        } else if (cardName.equals("CATEGORY")) {
            createCategoryPanel();
        } else if (cardName.equals("SUPPLIER")) {
            createSupplierPanel();
        } else if (cardName.equals("CUSTOMER")) {
            createCustomerPanel();
        } else if (cardName.equals("EMPLOYEE")) {
            createEmployeePanel();
        } else if (cardName.equals("INVOICE")) {
            createInvoicePanel();
        } else if (cardName.equals("IMPORT")) {
            createImportPanel();
        }
        
        cardLayout.show(contentPanel, cardName);
    }
    
    private void createProductPanel() {
        if (contentPanel.getComponentCount() > 0 && 
            ((JPanel)contentPanel.getComponent(0)).getName() != null &&
            ((JPanel)contentPanel.getComponent(0)).getName().equals("PRODUCT")) {
            return; // Already created
        }
        
        JPanel panel = new JPanel();
        panel.setName("PRODUCT");
        panel.setLayout(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel label = new JLabel("📦 Quản lý Sản phẩm", SwingConstants.CENTER);
        label.setFont(new Font("Segoe UI", Font.BOLD, 24));
        label.setForeground(new Color(33, 33, 33));
        
        JLabel infoLabel = new JLabel("<html><div style='text-align: center; padding: 50px;'>" +
            "<p style='font-size: 16px; color: #666;'>Chức năng quản lý sản phẩm sẽ được tích hợp vào đây.</p>" +
            "<p style='font-size: 14px; color: #999;'>Sử dụng ProductFrame để hiển thị danh sách và thao tác với sản phẩm.</p>" +
            "</div></html>", SwingConstants.CENTER);
        
        panel.add(label, BorderLayout.NORTH);
        panel.add(infoLabel, BorderLayout.CENTER);
        
        contentPanel.add(panel, "PRODUCT");
    }
    
    private void createCategoryPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel label = new JLabel("📁 Quản lý Danh mục", SwingConstants.CENTER);
        label.setFont(new Font("Segoe UI", Font.BOLD, 24));
        label.setForeground(new Color(33, 33, 33));
        
        JLabel infoLabel = new JLabel("<html><div style='text-align: center; padding: 50px;'>" +
            "<p style='font-size: 16px; color: #666;'>Chức năng quản lý danh mục sẽ được tích hợp vào đây.</p>" +
            "</div></html>", SwingConstants.CENTER);
        
        panel.add(label, BorderLayout.NORTH);
        panel.add(infoLabel, BorderLayout.CENTER);
        
        contentPanel.add(panel, "CATEGORY");
    }
    
    private void createSupplierPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel label = new JLabel("🏭 Quản lý Nhà cung cấp", SwingConstants.CENTER);
        label.setFont(new Font("Segoe UI", Font.BOLD, 24));
        label.setForeground(new Color(33, 33, 33));
        
        JLabel infoLabel = new JLabel("<html><div style='text-align: center; padding: 50px;'>" +
            "<p style='font-size: 16px; color: #666;'>Chức năng quản lý nhà cung cấp sẽ được tích hợp vào đây.</p>" +
            "</div></html>", SwingConstants.CENTER);
        
        panel.add(label, BorderLayout.NORTH);
        panel.add(infoLabel, BorderLayout.CENTER);
        
        contentPanel.add(panel, "SUPPLIER");
    }
    
    private void createCustomerPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel label = new JLabel("👥 Quản lý Khách hàng", SwingConstants.CENTER);
        label.setFont(new Font("Segoe UI", Font.BOLD, 24));
        label.setForeground(new Color(33, 33, 33));
        
        JLabel infoLabel = new JLabel("<html><div style='text-align: center; padding: 50px;'>" +
            "<p style='font-size: 16px; color: #666;'>Chức năng quản lý khách hàng sẽ được tích hợp vào đây.</p>" +
            "</div></html>", SwingConstants.CENTER);
        
        panel.add(label, BorderLayout.NORTH);
        panel.add(infoLabel, BorderLayout.CENTER);
        
        contentPanel.add(panel, "CUSTOMER");
    }
    
    private void createEmployeePanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel label = new JLabel("👨‍💼 Quản lý Nhân viên", SwingConstants.CENTER);
        label.setFont(new Font("Segoe UI", Font.BOLD, 24));
        label.setForeground(new Color(33, 33, 33));
        
        JLabel infoLabel = new JLabel("<html><div style='text-align: center; padding: 50px;'>" +
            "<p style='font-size: 16px; color: #666;'>Chức năng quản lý nhân viên sẽ được tích hợp vào đây.</p>" +
            "</div></html>", SwingConstants.CENTER);
        
        panel.add(label, BorderLayout.NORTH);
        panel.add(infoLabel, BorderLayout.CENTER);
        
        contentPanel.add(panel, "EMPLOYEE");
    }
    
    private void createInvoicePanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel label = new JLabel("🧾 Quản lý Hóa đơn", SwingConstants.CENTER);
        label.setFont(new Font("Segoe UI", Font.BOLD, 24));
        label.setForeground(new Color(33, 33, 33));
        
        JLabel infoLabel = new JLabel("<html><div style='text-align: center; padding: 50px;'>" +
            "<p style='font-size: 16px; color: #666;'>Chức năng quản lý hóa đơn sẽ được tích hợp vào đây.</p>" +
            "</div></html>", SwingConstants.CENTER);
        
        panel.add(label, BorderLayout.NORTH);
        panel.add(infoLabel, BorderLayout.CENTER);
        
        contentPanel.add(panel, "INVOICE");
    }
    
    private void createImportPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel label = new JLabel("📥 Quản lý Nhập hàng", SwingConstants.CENTER);
        label.setFont(new Font("Segoe UI", Font.BOLD, 24));
        label.setForeground(new Color(33, 33, 33));
        
        JLabel infoLabel = new JLabel("<html><div style='text-align: center; padding: 50px;'>" +
            "<p style='font-size: 16px; color: #666;'>Chức năng quản lý nhập hàng sẽ được tích hợp vào đây.</p>" +
            "</div></html>", SwingConstants.CENTER);
        
        panel.add(label, BorderLayout.NORTH);
        panel.add(infoLabel, BorderLayout.CENTER);
        
        contentPanel.add(panel, "IMPORT");
    }
    
    private void applyPermissions() {
        if (currentRole == null) {
            // Nếu không có role, ẩn tất cả các chức năng
            return;
        }
        
        // Ẩn/hiện menu dựa trên quyền
        btnProduct.setVisible(PermissionHelper.canManageProduct(currentRole));
        btnCategory.setVisible(PermissionHelper.canManageProduct(currentRole));
        btnSupplier.setVisible(PermissionHelper.canManageProduct(currentRole));
        btnEmployee.setVisible(PermissionHelper.canManageEmployee(currentRole));
        btnImport.setVisible(PermissionHelper.canImportProducts(currentRole));
        
        // Invoice và Customer luôn hiển thị (cả 2 role đều có quyền)
        // Nhưng sẽ kiểm tra quyền khi truy cập
        
        // Ẩn/hiện quick action buttons trong dashboard
        if (btnNewProduct != null) {
            btnNewProduct.setVisible(PermissionHelper.canManageProduct(currentRole));
        }
        if (btnNewImport != null) {
            btnNewImport.setVisible(PermissionHelper.canImportProducts(currentRole));
        }
        // btnNewInvoice luôn hiển thị vì cả 2 role đều có quyền
        
        // Ẩn thống kê doanh thu nếu không có quyền
        // (Có thể làm sau khi tích hợp dashboard thực tế)
    }
    
    private void startClock() {
        Timer timer = new Timer(1000, e -> {
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("HH:mm:ss | dd/MM/yyyy");
            lblTime.setText(sdf.format(new java.util.Date()));
        });
        timer.start();
    }
    
    // Custom Menu Button Class
    private class MenuButton extends JButton {
        public MenuButton(String text) {
            super(text);
            setFont(new Font("Segoe UI", Font.PLAIN, 14));
            setForeground(Color.WHITE);
            setBackground(new Color(33, 150, 243));
            setBorderPainted(false);
            setFocusPainted(false);
            setContentAreaFilled(false);
            setOpaque(true);
            setAlignmentX(Component.LEFT_ALIGNMENT);
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
            setHorizontalAlignment(SwingConstants.LEFT);
            setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            
            addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    setBackground(new Color(25, 118, 210));
                }
                public void mouseExited(java.awt.event.MouseEvent evt) {
                    setBackground(new Color(33, 150, 243));
                }
            });
        }
    }
}
