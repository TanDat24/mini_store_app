package sgu.fit;

import sgu.fit.supermarket.gui.swing.LoginFrame;

import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        // Gọi màn hình LoginFrame khi chạy chương trình
        SwingUtilities.invokeLater(() -> {
            LoginFrame login = new LoginFrame();
            login.setVisible(true);
        });
    }
}
