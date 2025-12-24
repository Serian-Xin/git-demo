package ui;

import model.User;
import data.UserRepository;
import util.PasswordUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

public class RegisterDialog extends JDialog {
    private boolean success = false;
    
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JTextField nameField;

    public RegisterDialog(JFrame parent) {
        super(parent, "用户注册", true);
        setSize(350, 300);
        setLocationRelativeTo(parent);
        setLayout(null);
        
        // 创建半透明背景面板
        JPanel contentPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // 绘制半透明背景
                g2d.setColor(new Color(240, 240, 240, 240)); // RGBA: 透明度 240
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);

                // 绘制阴影
                g2d.setColor(new Color(0, 0, 0, 30));
                g2d.fillRoundRect(5, 5, getWidth() - 10, getHeight() - 10, 10, 10);
                g2d.dispose();
            }
        };
        contentPanel.setBounds(0, 0, getWidth(), getHeight());
        contentPanel.setOpaque(false);
        contentPanel.setLayout(null);
        
        // 添加标题
        JLabel titleLabel = new JLabel("用户注册", SwingConstants.CENTER);
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 18));
        titleLabel.setForeground(new Color(50, 50, 50));
        titleLabel.setBounds(20, 20, 310, 30);
        contentPanel.add(titleLabel);

        // 账号标签
        JLabel usernameLabel = new JLabel("账号：");
        usernameLabel.setBounds(50, 60, 60, 25);
        usernameLabel.setForeground(new Color(70, 70, 70));
        contentPanel.add(usernameLabel);
        
        // 账号输入框
        usernameField = new JTextField("请输入账号");
        usernameField.setBounds(110, 60, 190, 28);
        usernameField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY),
            BorderFactory.createEmptyBorder(2, 5, 2, 5)
        ));
        usernameField.setForeground(Color.GRAY);
        
        // 设置输入框占位符效果
        usernameField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (usernameField.getText().equals("请输入账号")) {
                    usernameField.setText("");
                    usernameField.setForeground(Color.BLACK);
                }
            }
            
            @Override
            public void focusLost(FocusEvent e) {
                if (usernameField.getText().isEmpty()) {
                    usernameField.setText("请输入账号");
                    usernameField.setForeground(Color.GRAY);
                }
            }
        });
        contentPanel.add(usernameField);

        // 密码标签
        JLabel passwordLabel = new JLabel("密码：");
        passwordLabel.setBounds(50, 100, 60, 25);
        passwordLabel.setForeground(new Color(70, 70, 70));
        contentPanel.add(passwordLabel);
        
        // 密码输入框
        passwordField = new JPasswordField("请输入密码");
        passwordField.setBounds(110, 100, 190, 28);
        passwordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY),
            BorderFactory.createEmptyBorder(2, 5, 2, 5)
        ));
        passwordField.setEchoChar((char)0); // 初始显示明文
        passwordField.setForeground(Color.GRAY);
        
        // 设置密码框占位符效果
        passwordField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (new String(passwordField.getPassword()).equals("请输入密码")) {
                    passwordField.setText("");
                    passwordField.setEchoChar('•');
                    passwordField.setForeground(Color.BLACK);
                }
            }
            
            @Override
            public void focusLost(FocusEvent e) {
                if (new String(passwordField.getPassword()).isEmpty()) {
                    passwordField.setText("请输入密码");
                    passwordField.setEchoChar((char)0);
                    passwordField.setForeground(Color.GRAY);
                }
            }
        });
        contentPanel.add(passwordField);

        // 姓名标签
        JLabel nameLabel = new JLabel("姓名：");
        nameLabel.setBounds(50, 140, 60, 25);
        nameLabel.setForeground(new Color(70, 70, 70));
        contentPanel.add(nameLabel);
        
        // 姓名输入框
        nameField = new JTextField("请输入姓名");
        nameField.setBounds(110, 140, 190, 28);
        nameField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY),
            BorderFactory.createEmptyBorder(2, 5, 2, 5)
        ));
        nameField.setForeground(Color.GRAY);
        
        // 设置输入框占位符效果
        nameField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (nameField.getText().equals("请输入姓名")) {
                    nameField.setText("");
                    nameField.setForeground(Color.BLACK);
                }
            }
            
            @Override
            public void focusLost(FocusEvent e) {
                if (nameField.getText().isEmpty()) {
                    nameField.setText("请输入姓名");
                    nameField.setForeground(Color.GRAY);
                }
            }
        });
        contentPanel.add(nameField);
        
        // 注册按钮
        JButton registerBtn = createStyledButton("注册", new Color(39, 174, 96), Color.WHITE);
        registerBtn.setBounds(70, 190, 100, 35);
        contentPanel.add(registerBtn);
        
        // 取消按钮
        JButton cancelBtn = createStyledButton("取消", new Color(149, 165, 166), Color.WHITE);
        cancelBtn.setBounds(180, 190, 100, 35);
        contentPanel.add(cancelBtn);
        
        // 设置内容面板
        setContentPane(contentPanel);

        registerBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText().trim();
                String password = new String(passwordField.getPassword());
                String name = nameField.getText().trim();

                // 检查是否为占位符
                if (username.equals("请输入账号") || password.equals("请输入密码") || name.equals("请输入姓名")) {
                    JOptionPane.showMessageDialog(RegisterDialog.this, "请填写所有字段！", "提示", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                // 检查输入是否为空
                if (username.isEmpty() || password.isEmpty() || name.isEmpty()) {
                    JOptionPane.showMessageDialog(RegisterDialog.this, "请填写所有字段！", "提示", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                // 密码长度检查
                if (password.length() < 6) {
                    JOptionPane.showMessageDialog(RegisterDialog.this, "密码长度不能少于6位！", "提示", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                User user = new User(username, PasswordUtil.hashPassword(password), name);
                UserRepository repo = new UserRepository();

                if (repo.findByUsername(username) != null) {
                    JOptionPane.showMessageDialog(RegisterDialog.this, "该账号已存在！", "注册失败", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                try {
                    if (repo.register(user)) {
                        success = true;
                        dispose();
                    } else {
                        JOptionPane.showMessageDialog(RegisterDialog.this, "注册失败，请重试。", "错误", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(RegisterDialog.this, "注册失败：" + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        cancelBtn.addActionListener(e -> dispose());
    }
    
    // 创建美化按钮
    private JButton createStyledButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // 绘制圆角按钮背景
                g2d.setColor(bg);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                
                // 绘制渐变效果
                GradientPaint gp = new GradientPaint(0, 0, bg.brighter(), 0, getHeight(), bg);
                g2d.setPaint(gp);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                
                // 绘制文字
                g2d.setColor(fg);
                g2d.setFont(getFont());
                FontMetrics fm = g2d.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                g2d.drawString(getText(), x, y);
            }
        };
        btn.setForeground(fg);
        btn.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    public boolean isSuccessful() {
        return success;
    }
}
