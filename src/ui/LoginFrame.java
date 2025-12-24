package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.MediaTracker;

public class LoginFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;

    public LoginFrame() {
        setTitle("课程表管理系统");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        initBackground();
        initLoginPanel();
    }

    private void initBackground() {
        // 加载背景图片
        ImageIcon backgroundImage = new ImageIcon("background.png");
        
        // 创建一个可以绘制背景图片的面板
        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                
                if (backgroundImage.getImageLoadStatus() == MediaTracker.COMPLETE) {
                    // 图片加载成功，绘制背景图片
                    Image img = backgroundImage.getImage();
                    g.drawImage(img, 0, 0, getWidth(), getHeight(), this);
                } else {
                    // 图片加载失败，使用渐变背景作为后备
                    Graphics2D g2d = (Graphics2D) g;
                    GradientPaint gradient = new GradientPaint(
                        0, 0, new Color(240, 245, 250),
                        getWidth(), getHeight(), new Color(230, 240, 250)
                    );
                    g2d.setPaint(gradient);
                    g2d.fillRect(0, 0, getWidth(), getHeight());
                }
            }
        };
        
        backgroundPanel.setLayout(null);
        setContentPane(backgroundPanel);
        
        // 添加窗口大小改变监听器
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                backgroundPanel.repaint();
            }
        });
    }

    private void initLoginPanel() {
        // 创建毛玻璃效果登录面板
        JPanel loginPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

                // 绘制毛玻璃效果（半透明白色背景，带模糊感）
                // 使用更低的透明度以增强玻璃效果
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.85f));
                g2d.setColor(new Color(255, 255, 255, 220));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);

                // 绘制边框增强玻璃感
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
                g2d.setStroke(new BasicStroke(1));
                g2d.setColor(new Color(255, 255, 255, 80));
                g2d.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 20, 20);

                g2d.dispose();
            }
        };
        
        loginPanel.setBounds(280, 80, 340, 480);
        loginPanel.setOpaque(false);
        loginPanel.setLayout(null);
        
        // 添加标题
        JLabel titleLabel = new JLabel("欢迎登录", SwingConstants.CENTER);
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 32));
        titleLabel.setForeground(new Color(30, 30, 30));
        titleLabel.setBounds(20, 40, 300, 50);
        loginPanel.add(titleLabel);
        
        // 副标题
        JLabel subtitleLabel = new JLabel("登录以访问您的课程表", SwingConstants.CENTER);
        subtitleLabel.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        subtitleLabel.setForeground(new Color(120, 120, 120));
        subtitleLabel.setBounds(20, 85, 300, 25);
        loginPanel.add(subtitleLabel);

        // 用户名标签
        JLabel usernameLabel = new JLabel("账号");
        usernameLabel.setFont(new Font("微软雅黑", Font.BOLD, 12));
        usernameLabel.setBounds(60, 130, 220, 25);
        usernameLabel.setForeground(new Color(80, 80, 80));
        loginPanel.add(usernameLabel);

        // 用户名输入框
        usernameField = new JTextField();
        usernameField.setBounds(60, 155, 220, 45);
        usernameField.setFont(new Font("微软雅黑", Font.PLAIN, 15));
        usernameField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 2),
            BorderFactory.createEmptyBorder(12, 15, 12, 15)
        ));
        usernameField.setBackground(new Color(250, 250, 250));
        usernameField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                usernameField.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(47, 111, 237), 2),
                        BorderFactory.createEmptyBorder(12, 15, 12, 15)
                ));
            }
            @Override
            public void focusLost(FocusEvent e) {
                usernameField.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(220, 220, 220), 2),
                        BorderFactory.createEmptyBorder(12, 15, 12, 15)
                ));
            }
        });
        loginPanel.add(usernameField);

        // 密码标签
        JLabel passwordLabel = new JLabel("密码");
        passwordLabel.setFont(new Font("微软雅黑", Font.BOLD, 12));
        passwordLabel.setBounds(60, 215, 220, 25);
        passwordLabel.setForeground(new Color(80, 80, 80));
        loginPanel.add(passwordLabel);

        // 密码输入框
        passwordField = new JPasswordField();
        passwordField.setBounds(60, 240, 220, 45);
        passwordField.setFont(new Font("微软雅黑", Font.PLAIN, 15));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 2),
            BorderFactory.createEmptyBorder(12, 15, 12, 15)
        ));
        passwordField.setBackground(new Color(250, 250, 250));
        passwordField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                passwordField.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(47, 111, 237), 2),
                        BorderFactory.createEmptyBorder(12, 15, 12, 15)
                ));
            }
            @Override
            public void focusLost(FocusEvent e) {
                passwordField.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(220, 220, 220), 2),
                        BorderFactory.createEmptyBorder(12, 15, 12, 15)
                ));
            }
        });
        
        // 回车登录
        passwordField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    handleLogin();
                }
            }
        });
        loginPanel.add(passwordField);

        // 添加一些间距
        loginPanel.add(new JLabel());  // 占位
        
        // 登录按钮（半透明效果）
        JButton loginBtn = new JButton("登录") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                Color btnColor;
                int alpha;
                if (getModel().isPressed()) {
                    btnColor = new Color(47, 111, 237);
                    alpha = 200; // 按下时更不透明
                } else if (getModel().isRollover()) {
                    btnColor = new Color(47, 111, 237);
                    alpha = 180; // 悬停时稍微不透明
                } else {
                    btnColor = new Color(47, 111, 237);
                    alpha = 150; // 正常状态半透明
                }
                
                // 绘制半透明按钮背景
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha / 255.0f));
                g2d.setColor(btnColor);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                
                // 绘制边框
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
                g2d.setStroke(new BasicStroke(1.5f));
                g2d.setColor(new Color(47, 111, 237, 200));
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
                
                // 绘制文字
                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("微软雅黑", Font.BOLD, 16));
                FontMetrics fm = g2d.getFontMetrics();
                int textX = (getWidth() - fm.stringWidth(getText())) / 2;
                int textY = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                g2d.drawString(getText(), textX, textY);
            }
        };
        loginBtn.setBounds(60, 310, 220, 52);
        loginBtn.setFocusPainted(false);
        loginBtn.setBorderPainted(false);
        loginBtn.setContentAreaFilled(false);
        loginBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginBtn.addActionListener(e -> handleLogin());
        loginPanel.add(loginBtn);
        
        // 注册按钮（半透明效果，与登录按钮统一）
        JButton registerBtn = new JButton("注册新账号") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                Color btnColor;
                int alpha;
                if (getModel().isPressed()) {
                    btnColor = new Color(47, 111, 237);
                    alpha = 200; // 按下时更不透明
                } else if (getModel().isRollover()) {
                    btnColor = new Color(47, 111, 237);
                    alpha = 180; // 悬停时稍微不透明
                } else {
                    btnColor = new Color(47, 111, 237);
                    alpha = 150; // 正常状态半透明
                }
                
                // 绘制半透明按钮背景
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha / 255.0f));
                g2d.setColor(btnColor);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                
                // 绘制边框
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
                g2d.setStroke(new BasicStroke(1.5f));
                g2d.setColor(new Color(47, 111, 237, 200));
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);

                // 绘制文字
                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("微软雅黑", Font.BOLD, 15));
                FontMetrics fm = g2d.getFontMetrics();
                int textX = (getWidth() - fm.stringWidth(getText())) / 2;
                int textY = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                g2d.drawString(getText(), textX, textY);
            }
        };
        registerBtn.setBounds(60, 372, 220, 52);
        registerBtn.setFocusPainted(false);
        registerBtn.setBorderPainted(false);
        registerBtn.setContentAreaFilled(false);
        registerBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        registerBtn.addActionListener(e -> {
            RegisterDialog dialog = new RegisterDialog(this);
            dialog.setVisible(true);
            if (dialog.isSuccessful()) {
                showCustomMessage("注册成功，请登录！", "消息");
                usernameField.setText("");
                passwordField.setText("");
            }
        });
        loginPanel.add(registerBtn);

        // 将登录面板添加到背景面板
        ((JPanel)getContentPane()).add(loginPanel);
    }

    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            showCustomMessage("请输入账号和密码！", "提示");
            return;
        }

        data.UserRepository repo = new data.UserRepository();
        model.User user = repo.findByUsername(username);

        if (user != null && user.getPassword().equals(util.PasswordUtil.hashPassword(password))) {
            showCustomMessage("登录成功！", "消息");
            new main.MainFrame(user).setVisible(true);
            dispose();
        } else {
            showCustomMessage("账号或密码错误！", "登录失败");
        }
    }
    
    /**
     * 显示自定义消息对话框（居中、大字体、无图标）
     */
    private void showCustomMessage(String message, String title) {
        // 创建自定义消息面板
        JPanel messagePanel = new JPanel(new BorderLayout());
        messagePanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        
        // 创建标签，设置大字体和居中对齐
        JLabel messageLabel = new JLabel(message);
        messageLabel.setFont(new Font("微软雅黑", Font.PLAIN, 16));
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        messagePanel.add(messageLabel, BorderLayout.CENTER);
        
        // 使用 PLAIN_MESSAGE 类型，不显示图标
        JOptionPane.showMessageDialog(
            this,
            messagePanel,
            title,
            JOptionPane.PLAIN_MESSAGE,
            null  // 明确设置为 null，不显示图标
        );
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}
