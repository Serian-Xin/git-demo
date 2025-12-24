import javax.swing.*;
import ui.LoginFrame;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    // 使用Java默认的Metal外观，确保显示Java风格而不是系统风格
                    UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
                    // 设置Java经典主题
                    javax.swing.plaf.metal.MetalLookAndFeel.setCurrentTheme(new javax.swing.plaf.metal.DefaultMetalTheme());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                new LoginFrame().setVisible(true);
            }
        });
    }
}