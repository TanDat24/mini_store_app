package sgu.fit.supermarket.gui.swing;

import sgu.fit.supermarket.dao.AccountDAO;
import sgu.fit.supermarket.dao.EmployeeDAO;
import sgu.fit.supermarket.dao.RoleDAO;
import sgu.fit.supermarket.dao.impl.AccountDAOImpl;
import sgu.fit.supermarket.dao.impl.EmployeeDAOImpl;
import sgu.fit.supermarket.dao.impl.RoleDAOImpl;
import sgu.fit.supermarket.dto.AccountDTO;
import sgu.fit.supermarket.dto.EmployeeDTO;
import sgu.fit.supermarket.dto.RoleDTO;
import sgu.fit.supermarket.util.UserSession;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.net.URL;

public class LoginFrame extends JFrame {
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private JButton btnExit;
    private JLabel lblMessage;
    private AccountDAO accountDAO;
    private EmployeeDAO employeeDAO;
    private RoleDAO roleDAO;

    // --- Custom Panel for Round Corners and Shadow (Simplified) ---
    // Để có hiệu ứng bóng đổ chuẩn, cần thư viện bên ngoài hoặc code phức tạp hơn.
    // Em sẽ dùng RoundedPanel để tạo bo góc cho form container.
    class RoundedPanel extends JPanel {
        private int cornerRadius = 20;

        public RoundedPanel(LayoutManager layout, int radius) {
            super(layout);
            this.cornerRadius = radius;
            setOpaque(false); // Để vẽ hình nền bo góc
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Dimension arcs = new Dimension(cornerRadius, cornerRadius);
            int width = getWidth();
            int height = getHeight();
            Graphics2D graphics = (Graphics2D) g;
            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Draws the rounded opaque panel with a solid color
            graphics.setColor(getBackground());
            graphics.fillRoundRect(0, 0, width-1, height-1, arcs.width, arcs.height);
        }
    }
    // ----------------------------------------------------------------

    public LoginFrame() {
        accountDAO = new AccountDAOImpl();
        employeeDAO = new EmployeeDAOImpl();
        roleDAO = new RoleDAOImpl();
        initializeComponents();
        setupLayout();
        setupEvents();
    }

    private void initializeComponents() {
        setTitle("Hệ Thống Quản Lý Cửa Hàng Tiện Lợi");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        // Tăng kích thước để bố cục trông thoáng hơn
        setSize(600, 700);
        setLocationRelativeTo(null);

        txtUsername = new JTextField(20);
        txtPassword = new JPasswordField(20);
        btnLogin = new JButton("Đăng Nhập");
        btnExit = new JButton("Thoát");
        lblMessage = new JLabel(" ");
        lblMessage.setForeground(new Color(220, 0, 0)); // Màu đỏ đậm hơn
        lblMessage.setHorizontalAlignment(SwingConstants.CENTER);
    }

    private void setupLayout() {
        // Main panel với gradient background
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                int w = getWidth();
                int h = getHeight();
                // Gradient từ xanh lá đậm đến xanh lá nhạt hơn (tông màu tươi mới, hiện đại)
                Color color1 = new Color(50, 150, 50); // Xanh lá đậm
                Color color2 = new Color(139, 195, 74); // Xanh lá nhạt
                GradientPaint gp = new GradientPaint(0, 0, color1, w, h, color2); // Chuyển hướng gradient
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, w, h);
            }
        };
        mainPanel.setLayout(new BorderLayout());

        // Panel header với logo/title
        JPanel headerPanel = new JPanel();
        headerPanel.setOpaque(false);
        headerPanel.setBorder(new EmptyBorder(60, 20, 40, 20)); // Tăng padding

        // 1. Tải Logo bằng Class Loader
        JLabel lblTitle = new JLabel("SIÊU THỊ MINI 24/7");

        // Đường dẫn tài nguyên (resource path)
        String logoResourcePath = "/assets/product_images/logo.png";
        URL imageUrl = getClass().getResource(logoResourcePath);

        try {
            if (imageUrl != null) {
                ImageIcon originalIcon = new ImageIcon(imageUrl);

                // Thay đổi kích thước (ví dụ: 40x40 pixels)
                Image scaledImage = originalIcon.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH);
                ImageIcon logoIcon = new ImageIcon(scaledImage);

                // 2. Thiết lập Icon và Text cho JLabel
                lblTitle.setIcon(logoIcon);
                lblTitle.setText(" SIÊU THỊ MINI 24/7"); // Thêm khoảng trắng
                lblTitle.setHorizontalTextPosition(SwingConstants.RIGHT);
                lblTitle.setVerticalTextPosition(SwingConstants.CENTER);
                lblTitle.setIconTextGap(10);

            } else {
                // Nếu không tìm thấy resource, dùng icon mặc định
                lblTitle.setText("🛒 SIÊU THỊ MINI 24/7");
                System.err.println("Lỗi: Không tìm thấy tài nguyên tại đường dẫn: " + logoResourcePath);
            }

        } catch (Exception e) {
            lblTitle.setText("🛒 SIÊU THỊ MINI 24/7");
            System.err.println("Lỗi tải logo: " + e.getMessage());
        }


        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 36));
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel lblSubtitle = new JLabel("Hệ Thống Quản Lý Thông Minh");
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        lblSubtitle.setForeground(new Color(255, 255, 255, 220));
        lblSubtitle.setHorizontalAlignment(SwingConstants.CENTER);

        headerPanel.setLayout(new BorderLayout(0, 5));
        headerPanel.add(lblTitle, BorderLayout.CENTER);
        headerPanel.add(lblSubtitle, BorderLayout.SOUTH);

        // Panel form ĐÃ ĐƯỢC BO GÓC
        RoundedPanel formContainer = new RoundedPanel(new BorderLayout(), 20); // Bo góc 20
        formContainer.setBackground(Color.WHITE); // Nền trắng để nổi bật
        formContainer.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        // Em bỏ Shadow vì cần Custom Border phức tạp hơn. Form trắng bo góc đã tạo hiệu ứng tốt.

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false); // Quan trọng: FormPanel phải trong suốt để thấy nền bo góc của Container

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 0, 10, 0); // Giảm insets ngang
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL; // Các field chiếm hết chiều ngang

        // Username
        JLabel lblUsername = new JLabel("Tên đăng nhập:");
        lblUsername.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblUsername.setForeground(new Color(51, 51, 51)); // Màu chữ đen
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(lblUsername, gbc);

        txtUsername.setFont(new Font("Segoe UI", Font.PLAIN, 16)); // Font to hơn
        txtUsername.setPreferredSize(new Dimension(300, 48)); // Chiều cao lớn hơn
        txtUsername.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        txtUsername.setBackground(new Color(245, 245, 245)); // Nền field hơi xám
        gbc.gridy = 1;
        formPanel.add(txtUsername, gbc);

        // Password
        JLabel lblPassword = new JLabel("Mật khẩu:");
        lblPassword.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblPassword.setForeground(new Color(51, 51, 51));
        gbc.gridy = 2;
        formPanel.add(lblPassword, gbc);

        txtPassword.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        txtPassword.setPreferredSize(new Dimension(300, 48));
        txtPassword.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        txtPassword.setBackground(new Color(245, 245, 245));
        gbc.gridy = 3;
        formPanel.add(txtPassword, gbc);

        // Message label
        lblMessage.setFont(new Font("Segoe UI", Font.BOLD, 13));
        gbc.insets = new Insets(5, 0, 5, 0); // Giảm insets
        gbc.gridy = 4;
        formPanel.add(lblMessage, gbc);
        gbc.insets = new Insets(10, 0, 10, 0); // Khôi phục

        // Button panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 15)); // Khoảng cách rộng hơn
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        // Login button
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnLogin.setPreferredSize(new Dimension(180, 50)); // Nút lớn hơn
        btnLogin.setBackground(new Color(76, 175, 80)); // Xanh lá
        // Giữ lại custom MouseAdapter để có hiệu ứng hover

        // Exit button
        btnExit.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnExit.setPreferredSize(new Dimension(180, 50));
        btnExit.setBackground(new Color(158, 158, 158)); // Xám trung tính
        // Thay màu thoát bằng màu trung tính
        btnExit.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnExit.setBackground(new Color(130, 130, 130));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnExit.setBackground(new Color(158, 158, 158));
            }
        });

        buttonPanel.add(btnLogin);
        buttonPanel.add(btnExit);

        // Add buttonPanel
        gbc.gridy = 5;
        formPanel.add(buttonPanel, gbc);

        // Add formPanel to the rounded formContainer
        formContainer.add(formPanel, BorderLayout.CENTER);

        // Center panel để chứa formContainer
        JPanel centerPanelContainer = new JPanel(new GridBagLayout()); // Dùng GridBag để căn giữa tuyệt đối
        centerPanelContainer.setOpaque(false);
        centerPanelContainer.add(formContainer);

        // Add to main panel
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(centerPanelContainer, BorderLayout.CENTER); // Thay centerPanel bằng centerPanelContainer

        // Footer
        JPanel footerPanel = new JPanel();
        footerPanel.setOpaque(false);
        footerPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        JLabel lblFooter = new JLabel("© 2024 Hệ Thống Quản Lý Cửa Hàng Tiện Lợi | Version 1.0");
        lblFooter.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblFooter.setForeground(new Color(255, 255, 255, 200));
        footerPanel.add(lblFooter);
        mainPanel.add(footerPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    // ... (Giữ nguyên setupEvents và performLogin) ...
    private void setupEvents() {
        // Login button action
        btnLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performLogin();
            }
        });

        // Exit button action
        btnExit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int option = JOptionPane.showConfirmDialog(
                        LoginFrame.this,
                        "Bạn có chắc chắn muốn thoát?",
                        "Xác nhận",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE
                );
                if (option == JOptionPane.YES_OPTION) {
                    System.exit(0);
                }
            }
        });

        // Enter key on password field
        txtPassword.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    performLogin();
                }
            }
        });

        // Enter key on username field
        txtUsername.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    txtPassword.requestFocus();
                }
            }
        });
    }

    private void performLogin() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());

        // Validation
        if (username.isEmpty()) {
            lblMessage.setText("Vui lòng nhập tên đăng nhập!");
            txtUsername.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            lblMessage.setText("Vui lòng nhập mật khẩu!");
            txtPassword.requestFocus();
            return;
        }

        // Clear previous message
        lblMessage.setText(" ");

        // Disable buttons during login
        btnLogin.setEnabled(false);
        btnExit.setEnabled(false);
        btnLogin.setText("Đang xử lý...");

        // Perform login in background
        // Em sử dụng Thread.State.NEW để tránh lỗi khi cố gắng chạy lại một thread đã hoàn thành
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    AccountDTO account = accountDAO.login(username, password);

                    if (account != null) {
                        // Lấy thông tin Employee và Role
                        EmployeeDTO employee = employeeDAO.findById(account.getEmployeeId());
                        RoleDTO role = null;
                        
                        if (employee != null) {
                            role = roleDAO.findById(employee.getRoleId());
                        }
                        
                        if (employee != null && role != null) {
                            // Lưu vào session
                            final EmployeeDTO finalEmployee = employee;
                            final RoleDTO finalRole = role;
                            UserSession.getInstance().setUser(account, finalEmployee, finalRole);
                            
                            // Login successful
                            lblMessage.setText("Đăng nhập thành công!");
                            lblMessage.setForeground(new Color(0, 150, 0)); // Xanh lá đậm

                            // Đóng màn hình đăng nhập và mở MainFrame
                            SwingUtilities.invokeLater(new Runnable() {
                                @Override
                                public void run() {
                                    dispose();
                                    MainFrame mainFrame = new MainFrame(account, finalEmployee, finalRole);
                                    mainFrame.setVisible(true);
                                }
                            });
                        } else {
                            lblMessage.setText("Không thể lấy thông tin nhân viên!");
                            lblMessage.setForeground(new Color(220, 0, 0));
                        }
                    } else {
                        // Login failed
                        lblMessage.setText("Tên đăng nhập hoặc mật khẩu không đúng!");
                        lblMessage.setForeground(new Color(220, 0, 0));
                        txtPassword.setText("");
                        txtPassword.requestFocus();
                    }
                } catch (Exception e) {
                    lblMessage.setText("Lỗi kết nối database: " + e.getMessage());
                    lblMessage.setForeground(new Color(220, 0, 0));
                    e.printStackTrace();
                } finally {
                    // Re-enable buttons
                    btnLogin.setEnabled(true);
                    btnExit.setEnabled(true);
                    btnLogin.setText("Đăng Nhập");
                }
            }
        });
    }

    public static void main(String[] args) {
        // Set look and feel
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel"); // Thử dùng Nimbus để có giao diện hiện đại hơn
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                LoginFrame loginFrame = new LoginFrame();
                loginFrame.setVisible(true);
            }
        });
    }
}