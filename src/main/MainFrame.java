package main;

import data.CourseRepository;
import data.UserRepository;
import model.Course;
import model.User;
import ui.LoginFrame;
import util.PasswordUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.ArrayList;
import java.io.IOException;
import java.io.File;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javax.swing.filechooser.FileNameExtensionFilter;


/**
 * 主界面：课程表管理系统核心窗口（UI升级版）
 * 展示课表、支持添加/删除课程、账户管理
 */
public class MainFrame extends JFrame {
    private CourseRepository repository;
    private User currentUser;
    private JPanel courseGrid; // 动态课程展示面板
    private String[] days = {"星期一", "星期二", "星期三", "星期四", "星期五", "星期六", "星期日"};
    private String[] timeSlots = {"第1-2节", "第3-4节", "第5-6节", "第7-8节", "第9-10节"};
    private String[] timeRanges = {"08:00-09:40", "10:00-11:40", "14:30-16:10", "16:30-18:10", "19:00-20:40"}; // 上课时间范围
    
    // 主面板和相关组件
    private JPanel mainPanel;    // 主内容面板
    private JPanel coursePanel;  // 课表面板
    private JPanel myPanel;      // 我的面板
    private JComboBox<String> weekSelector; // 周选择器
    private JButton myBtn;       // 我的按钮
    private JLabel weekInfoLabel; // 周次信息标签（"第i周课程安排"）
    
    public MainFrame(User user) {
        this.currentUser = user;
        this.repository = new CourseRepository();
        
        // 设置全局字体
        Font font = new Font("微软雅黑", Font.PLAIN, 13);
        UIManager.put("Button.font", font);
        UIManager.put("Label.font", font);
        UIManager.put("TextField.font", font);
        UIManager.put("TextArea.font", font);
        UIManager.put("ComboBox.font", font);
        UIManager.put("Table.font", font);
        
        // 设置JOptionPane对话框背景色
        UIManager.put("OptionPane.background", ModernColorScheme.DIALOG_BG);
        UIManager.put("Panel.background", ModernColorScheme.DIALOG_BG);
        
        // 设置对话框按钮样式（圆角+毛玻璃效果）
        // 注意：UIManager的Button设置对JOptionPane按钮效果有限
        // 需要通过自定义方法来实现毛玻璃效果
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        setTitle("课程表管理系统");
        setSize(1200, 750);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // 设置主背景色
        getContentPane().setBackground(ModernColorScheme.BG_PRIMARY);
        setLayout(new BorderLayout(0, 0));
        
        // 创建顶部导航栏
        initTopPanel();
        
        // 创建主面板
        mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(ModernColorScheme.BG_PRIMARY);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        add(mainPanel, BorderLayout.CENTER);
        
        // 创建底部面板
        initBottomButtonPanel();
        
        // 显示主页面板
        showHomePanel();
        loadCourseSchedule();
    }
    

    private void showChangePasswordDialog() {
        // 创建密码输入面板
        JPanel panel = new JPanel(new GridLayout(3, 2, 8, 8));
        panel.setBackground(ModernColorScheme.DIALOG_BG);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 10, 15));
        
        // 创建标签和输入框（减小输入框宽度和高度）
        JLabel oldPasswordLabel = new JLabel("旧密码:");
        JPasswordField oldPasswordField = new JPasswordField(15);
        oldPasswordField.setPreferredSize(new Dimension(150, 30));
        
        JLabel newPasswordLabel = new JLabel("新密码:");
        JPasswordField newPasswordField = new JPasswordField(15);
        newPasswordField.setPreferredSize(new Dimension(150, 30));
        
        JLabel confirmPasswordLabel = new JLabel("确认新密码:");
        JPasswordField confirmPasswordField = new JPasswordField(15);
        confirmPasswordField.setPreferredSize(new Dimension(150, 30));
        
        // 添加到面板
        panel.add(oldPasswordLabel);
        panel.add(oldPasswordField);
        panel.add(newPasswordLabel);
        panel.add(newPasswordField);
        panel.add(confirmPasswordLabel);
        panel.add(confirmPasswordField);
        
        // 显示对话框前设置焦点
        SwingUtilities.invokeLater(() -> oldPasswordField.requestFocusInWindow());
        
        int result = showGlassConfirmDialog(this, panel, "修改密码");
        
        // 用户点击了确定按钮
        if (result == JOptionPane.OK_OPTION) {
            String oldPassword = new String(oldPasswordField.getPassword());
            String newPassword = new String(newPasswordField.getPassword());
            String confirmPassword = new String(confirmPasswordField.getPassword());
            
            // 验证输入
            if (oldPassword.isEmpty()) {
                JOptionPane.showMessageDialog(this, "请输入旧密码", "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (newPassword.isEmpty()) {
                JOptionPane.showMessageDialog(this, "请输入新密码", "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (!newPassword.equals(confirmPassword)) {
                JOptionPane.showMessageDialog(this, "两次输入的新密码不一致", "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // 调用更新密码方法
            UserRepository userRepository = new UserRepository();
            boolean success = userRepository.updatePassword(currentUser.getId(), oldPassword, newPassword);
            
            if (success) {
                JOptionPane.showMessageDialog(this, "密码修改成功", "成功", JOptionPane.INFORMATION_MESSAGE);
                // 更新当前用户对象中的密码哈希值
                currentUser.setPassword(PasswordUtil.hashPassword(newPassword));
            } else {
                JOptionPane.showMessageDialog(this, "旧密码错误或修改失败", "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void switchAccount() {
        int confirm = showSmallGlassConfirmDialog(this, "确定要切换账号吗？", "切换账号");
        if (confirm == JOptionPane.YES_OPTION) {
            dispose(); // 关闭当前主界面
            new LoginFrame().setVisible(true); // 回到登录界面
        }
    }
    
    private void logout() {
        int confirm = showSmallGlassConfirmDialog(this, "确定退出登录？", "提示");
        if (confirm == JOptionPane.YES_OPTION) {
            dispose(); // 关闭当前主界面
            new LoginFrame().setVisible(true);//回到登录页面
        }
    }
    

    private void initTopPanel() {
        JPanel topPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                
                // 渐变背景（#E1FBFD浅色系渐变）
                GradientPaint gradient = new GradientPaint(
                    0, 0, new Color(241, 253, 255),      // 上：更浅的青色
                    0, getHeight(), new Color(225, 251, 253)  // 下：#E1FBFD
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                
                g2d.dispose();
            }
        };
        topPanel.setLayout(new BorderLayout());
        topPanel.setPreferredSize(new Dimension(0, 120));
        topPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, ModernColorScheme.BORDER_LIGHT),
            BorderFactory.createEmptyBorder(20, 30, 20, 30)
        ));
        
        // 中央标题区域
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setOpaque(false);
        
        // 主标题
        JLabel titleLabel = new JLabel("我的课程表");
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 36));
        titleLabel.setForeground(new Color(112, 146, 190)); // #7092BE
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // 副标题
        weekInfoLabel = new JLabel("第1周课程安排");
        weekInfoLabel.setFont(new Font("微软雅黑", Font.PLAIN, 16));
        weekInfoLabel.setForeground(ModernColorScheme.TEXT_SECONDARY);
        weekInfoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        centerPanel.add(titleLabel);
        centerPanel.add(Box.createVerticalStrut(8));
        centerPanel.add(weekInfoLabel);
        
        topPanel.add(centerPanel, BorderLayout.CENTER);
        
        // 初始化周选择器
        weekSelector = new JComboBox<>();
        for (int i = 1; i <= 18; i++) {
            weekSelector.addItem("第" + i + "周");
        }
        weekSelector.setSelectedIndex(0);
        weekSelector.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        weekSelector.setPreferredSize(new Dimension(120, 32));
        weekSelector.addActionListener(e -> {
            int selectedWeek = weekSelector.getSelectedIndex() + 1;
            loadCoursesForWeek(selectedWeek);
        });
        
        add(topPanel, BorderLayout.NORTH);
    }
    
    /**
     * 创建底部按钮面板，包含导航按钮
     */
    private void initBottomButtonPanel() {
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(ModernColorScheme.BG_SECONDARY);
        bottomPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, ModernColorScheme.BORDER_LIGHT),
            BorderFactory.createEmptyBorder(12, 20, 12, 20)
        ));
        
        // 状态栏
        statusLabel = new JLabel("  欢迎使用课程表管理系统");
        statusLabel.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        statusLabel.setForeground(ModernColorScheme.TEXT_SECONDARY);
        
        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setOpaque(false);
        
        JButton homeBtn = createModernButton("首页", new Color(24, 103, 143), ""); // #18678F
        myBtn = createModernButton("我的", new Color(222, 110, 110), ""); // #DE6E6E
        JButton addBtn = createModernButton("添加课程", new Color(80, 127, 128), ""); // #507F80
        JButton exportBtn = createModernButton("导出图片", new Color(240, 173, 137), ""); // #F0AD89
        
        homeBtn.addActionListener(e -> showHomePanel());
        myBtn.addActionListener(e -> toggleMyPanel());
        addBtn.addActionListener(e -> showAddCourseDialog());
        exportBtn.addActionListener(e -> exportAsImage());
        
        buttonPanel.add(homeBtn);
        buttonPanel.add(myBtn);
        buttonPanel.add(addBtn);
        buttonPanel.add(exportBtn);
        
        bottomPanel.add(statusLabel, BorderLayout.WEST);
        bottomPanel.add(buttonPanel, BorderLayout.EAST);
        
        add(bottomPanel, BorderLayout.SOUTH);
    }
    
    /**
     * 创建现代化按钮
     */
    private JButton createModernButton(String text, Color bgColor, String emoji) {
        String buttonText = emoji.isEmpty() ? text : emoji + " " + text;
        JButton button = new JButton(buttonText) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // 绘制圆角背景
                Color currentColor = bgColor;
                if (getModel().isPressed()) {
                    currentColor = ModernColorScheme.darken(bgColor, 0.1f);
                } else if (getModel().isRollover()) {
                    currentColor = ModernColorScheme.brighten(bgColor, 0.1f);
                }
                
                g2d.setColor(currentColor);
                g2d.fill(new java.awt.geom.RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));
                g2d.dispose();
                
                super.paintComponent(g);
            }
        };
        
        button.setForeground(ModernColorScheme.TEXT_WHITE);
        button.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        button.setPreferredSize(new Dimension(110, 36));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        return button;
    }
    
    /**
     * 导出课程表为图片
     */
    private void exportAsImage() {
        exportSchedule();
    }
    
    private void showHomePanel() {
        if (coursePanel == null) {
            coursePanel = createCoursePanel();
        }
        // 只替换中间的组件，保留底部组件
        Component centerComponent = ((BorderLayout)mainPanel.getLayout()).getLayoutComponent(BorderLayout.CENTER);
        if (centerComponent != null) {
            mainPanel.remove(centerComponent);
        }
        mainPanel.add(coursePanel, BorderLayout.CENTER);
        mainPanel.revalidate();
        mainPanel.repaint();
    }
    
    /**
     * 创建样式按钮（用于用户信息面板）
     */
    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                Color currentColor = bgColor;
                if (getModel().isPressed()) {
                    currentColor = ModernColorScheme.darken(bgColor, 0.1f);
                } else if (getModel().isRollover()) {
                    currentColor = ModernColorScheme.brighten(bgColor, 0.1f);
                }
                
                g2d.setColor(currentColor);
                g2d.fill(new java.awt.geom.RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));
                g2d.dispose();
                
                super.paintComponent(g);
            }
        };
        
        button.setForeground(ModernColorScheme.TEXT_WHITE);
        button.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        button.setPreferredSize(new Dimension(240, 40));
        button.setMaximumSize(new Dimension(240, 40));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        return button;
    }
    
    /**
     * 显示带毛玻璃按钮的确认对话框
     */
    private int showGlassConfirmDialog(Component parent, Object message, String title) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        
        if (message instanceof JPanel) {
            panel.add((JPanel)message, BorderLayout.CENTER);
        } else {
            JLabel label = new JLabel(message.toString());
            label.setFont(new Font("微软雅黑", Font.PLAIN, 14));
            panel.add(label, BorderLayout.CENTER);
        }
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setOpaque(false);
        
        final int[] result = {JOptionPane.CANCEL_OPTION};
        
        JButton okButton = createGlassButton("确定", new Color(47, 111, 237));
        JButton cancelButton = createGlassButton("取消", new Color(150, 150, 150));
        
        JDialog dialog = new JDialog((Frame)parent, title, true);
        dialog.setLayout(new BorderLayout());
        dialog.getContentPane().setBackground(ModernColorScheme.DIALOG_BG);
        
        okButton.addActionListener(e -> {
            result[0] = JOptionPane.OK_OPTION;
            dialog.dispose();
        });
        
        cancelButton.addActionListener(e -> {
            result[0] = JOptionPane.CANCEL_OPTION;
            dialog.dispose();
        });
        
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        
        dialog.add(panel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setSize(400, 250);
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
        
        return result[0];
    }
    
    /**
     * 创建带毛玻璃效果的对话框按钮
     */
    private JButton createGlassButton(String text, Color baseColor) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                int alpha;
                if (getModel().isPressed()) {
                    alpha = 200; // 按下时更不透明
                } else if (getModel().isRollover()) {
                    alpha = 180; // 悬停时稍微不透明
                } else {
                    alpha = 150; // 正常状态半透明
                }
                
                // 绘制半透明按钮背景
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha / 255.0f));
                g2d.setColor(baseColor);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                
                // 绘制边框
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
                g2d.setStroke(new BasicStroke(1.5f));
                g2d.setColor(new Color(baseColor.getRed(), baseColor.getGreen(), baseColor.getBlue(), 200));
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
                
                // 绘制文字
                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("微软雅黑", Font.BOLD, 14));
                FontMetrics fm = g2d.getFontMetrics();
                int textX = (getWidth() - fm.stringWidth(getText())) / 2;
                int textY = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                g2d.drawString(getText(), textX, textY);
            }
        };
        
        button.setPreferredSize(new Dimension(100, 40));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        return button;
    }
    
    /**
     * 创建带毛玻璃效果的小尺寸对话框按钮（用于退出登录和切换账号）
     */
    private JButton createSmallGlassButton(String text, Color baseColor) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                int alpha;
                if (getModel().isPressed()) {
                    alpha = 200; // 按下时更不透明
                } else if (getModel().isRollover()) {
                    alpha = 180; // 悬停时稍微不透明
                } else {
                    alpha = 150; // 正常状态半透明
                }
                
                // 绘制半透明按钮背景
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha / 255.0f));
                g2d.setColor(baseColor);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                
                // 绘制边框
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
                g2d.setStroke(new BasicStroke(1.5f));
                g2d.setColor(new Color(baseColor.getRed(), baseColor.getGreen(), baseColor.getBlue(), 200));
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
                
                // 绘制文字
                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("微软雅黑", Font.BOLD, 13));
                FontMetrics fm = g2d.getFontMetrics();
                int textX = (getWidth() - fm.stringWidth(getText())) / 2;
                int textY = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                g2d.drawString(getText(), textX, textY);
            }
        };
        
        button.setPreferredSize(new Dimension(80, 35));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        return button;
    }
    
    /**
     * 显示带小尺寸毛玻璃按钮的确认对话框（用于退出登录和切换账号）
     */
    private int showSmallGlassConfirmDialog(Component parent, String message, String title) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
        
        JLabel label = new JLabel(message);
        label.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        label.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(label, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setOpaque(false);
        
        final int[] result = {JOptionPane.CANCEL_OPTION};
        
        JButton okButton = createSmallGlassButton("确定", new Color(47, 111, 237));
        JButton cancelButton = createSmallGlassButton("取消", new Color(150, 150, 150));
        
        JDialog dialog = new JDialog((Frame)parent, title, true);
        dialog.setLayout(new BorderLayout());
        dialog.getContentPane().setBackground(ModernColorScheme.DIALOG_BG);
        
        okButton.addActionListener(e -> {
            result[0] = JOptionPane.YES_OPTION;
            dialog.dispose();
        });
        
        cancelButton.addActionListener(e -> {
            result[0] = JOptionPane.NO_OPTION;
            dialog.dispose();
        });
        
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        
        dialog.add(panel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setSize(320, 180);
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
        
        return result[0];
    }
    
    // 移除了未使用的周数范围选择功能
    
    private void toggleMyPanel() {
        if (myPanel == null) {
            myPanel = createMyPanel();
        }
        
        // 简化判断逻辑：检查当前center位置的组件是否为contentPanel类型
        Component centerComponent = ((BorderLayout)mainPanel.getLayout()).getLayoutComponent(BorderLayout.CENTER);
        boolean isMyPanelVisible = false;
        
        // 如果center位置的组件是一个JPanel并且包含两个子组件（课程表和我的面板）
        if (centerComponent instanceof JPanel) {
            JPanel currentPanel = (JPanel)centerComponent;
            if (currentPanel.getComponentCount() == 2 && currentPanel.getLayout() instanceof GridLayout) {
                GridLayout layout = (GridLayout)currentPanel.getLayout();
                if (layout.getRows() == 1 && layout.getColumns() == 2) {
                    isMyPanelVisible = true;
                }
            }
        }
        
        if (isMyPanelVisible) {
            // 如果"我的"面板已打开，则恢复为完整的课程表视图
            showHomePanel();
        } else {
            // 如果"我的"面板未打开，则显示左右分栏布局
            // 只替换中间的组件，保留底部组件
            if (centerComponent != null) {
                mainPanel.remove(centerComponent);
            }
            
            // 创建左右分栏布局的contentPanel
            JPanel contentPanel = new JPanel(new BorderLayout());
            contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            
            // 左侧：课程表（占据剩余空间）
            if (coursePanel == null) {
                coursePanel = createCoursePanel();
            }
            contentPanel.add(coursePanel, BorderLayout.CENTER);
            
            // 右侧：用户信息面板（固定宽度，右对齐）
            contentPanel.add(myPanel, BorderLayout.EAST);
            
            // 将contentPanel添加到mainPanel的CENTER位置
            mainPanel.add(contentPanel, BorderLayout.CENTER);
        }
        
        mainPanel.revalidate();
        mainPanel.repaint();
    }
    
    /**
     * 创建课表面板 - 现代化版
     */
    private JPanel createCoursePanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 15));
        panel.setOpaque(false);
        
        // 顶部工具栏
        JPanel toolBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        toolBar.setOpaque(false);
        
        JLabel weekLabel = new JLabel("教学周:");
        weekLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        weekLabel.setForeground(ModernColorScheme.TEXT_PRIMARY);
        
        toolBar.add(weekLabel);
        toolBar.add(weekSelector);
        
        panel.add(toolBar, BorderLayout.NORTH);
        
        // 课表主体
        JPanel tablePanel = createCourseTable();
        JScrollPane scrollPane = new JScrollPane(tablePanel);
        scrollPane.setBorder(BorderFactory.createLineBorder(ModernColorScheme.BORDER_LIGHT, 1));
        scrollPane.getViewport().setBackground(ModernColorScheme.BG_SECONDARY);
        
        // 提高滚动灵敏度
        scrollPane.getVerticalScrollBar().setUnitIncrement(20);
        scrollPane.getHorizontalScrollBar().setUnitIncrement(20);
        
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    /**
     * 创建课程表网格
     */
    private JPanel createCourseTable() {
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(ModernColorScheme.BG_SECONDARY);
        tablePanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        courseGrid = new JPanel(new GridLayout(6, 8, 1, 1));
        courseGrid.setBackground(ModernColorScheme.BORDER_LIGHT);
        
        tablePanel.add(courseGrid, BorderLayout.CENTER);
        
        return tablePanel;
    }
    
    /**
     * 添加课程内容（支持多节）
     */
    private void addCourse(JPanel panel, int row, String content) {
        // 计算位置：row 从 1 开始，对应第几行
        int index = 1 + row; // 因为第一行是表头
        JLabel courseLabel = new JLabel(content);
        courseLabel.setHorizontalAlignment(SwingConstants.LEFT);
        courseLabel.setVerticalAlignment(SwingConstants.TOP);
        courseLabel.setFont(new Font("微软雅黑", Font.PLAIN, 10));
        courseLabel.setBackground(ModernColorScheme.COURSE_CELL_BG);
        courseLabel.setForeground(Color.BLACK);
        courseLabel.setOpaque(true);
        courseLabel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
        panel.add(courseLabel);
    }
    
    /**
     * 创建我的面板，显示用户信息
     */
    private JPanel createMyPanel() {
        // 创建面板并添加圆角边框和阴影
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // 绘制带阴影的圆角背景
                int width = getWidth();
                int height = getHeight();
                
                // 阴影
                g2.setColor(ModernColorScheme.SHADOW_MEDIUM);
                g2.fillRoundRect(3, 3, width - 6, height - 6, 15, 15);
                
                // 背景
                g2.setColor(ModernColorScheme.BG_SECONDARY);
                g2.fillRoundRect(0, 0, width - 1, height - 1, 15, 15);
                
                // 边框
                g2.setColor(ModernColorScheme.BORDER_LIGHT);
                g2.drawRoundRect(0, 0, width - 1, height - 1, 15, 15);
                
                g2.dispose();
                super.paintComponent(g);
            }
        };
        
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setPreferredSize(new Dimension(230, 600)); // 减小宽度，使其与底部按钮对齐
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // 用户信息标题
        JLabel titleLabel = new JLabel("用户信息");
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 22));
        titleLabel.setForeground(new Color(112, 146, 190)); // #7092BE
        
        // 用户详情标签（直接放在标题下方，无背景面板）
        JLabel nameLabel = new JLabel("姓名：" + currentUser.getName());
        JLabel usernameLabel = new JLabel("账号：" + currentUser.getUsername());
        
        // 增大字体
        nameLabel.setFont(new Font("微软雅黑", Font.PLAIN, 18));
        usernameLabel.setFont(new Font("微软雅黑", Font.PLAIN, 18));
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        usernameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        nameLabel.setForeground(new Color(60, 60, 60));
        usernameLabel.setForeground(new Color(60, 60, 60));
        
        // 功能按钮 - 调整布局以适应230宽度
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        buttonPanel.setOpaque(false); // 透明背景
        
        JButton changePasswordBtn = createStyledButton("修改密码", new Color(105, 119, 158)); // #69779E
        changePasswordBtn.addActionListener(e -> showChangePasswordDialog());
        
        JButton switchAccountBtn = createStyledButton("切换账号", new Color(140, 186, 147)); // #8CBA93
        switchAccountBtn.addActionListener(e -> switchAccount());
        
        JButton logoutBtn = createStyledButton("退出登录", new Color(227, 71, 74)); // #E3474A
        logoutBtn.addActionListener(e -> logout());
        
        buttonPanel.add(changePasswordBtn);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 12))); // 按钮之间的间距
        buttonPanel.add(switchAccountBtn);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 12)));
        buttonPanel.add(logoutBtn);
        
        // 添加组件到面板
        panel.add(titleLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 25)));
        panel.add(nameLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));
        panel.add(usernameLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 50))); // 信息和按钮之间的间距
        buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(buttonPanel);
        panel.add(Box.createVerticalGlue());
        
        return panel;
    }
    
    /**
     * 根据教学周加载课程表
     */
    // 状态栏引用
    private JLabel statusLabel;
    
    private void loadCoursesForWeek(int week) {
        courseGrid.removeAll(); // 清空旧内容
        // 更新周次提示标签 - 直接使用实例变量
        if (weekInfoLabel != null) {
            weekInfoLabel.setText("第" + week + "周课程安排");
        }
        
        // 使用引用更新状态栏，避免通过索引获取
        if (statusLabel != null) {
            statusLabel.setText(" 欢迎使用课程表管理系统 | 当前周: 第" + week + "周");
        }

        // 添加表头（节次/星期，然后是星期一到星期日）
        String[] weekLabels = {"节次\\星期", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六", "星期日"};
        for (int i = 0; i < weekLabels.length; i++) {
            JLabel label = new JLabel(weekLabels[i]);
            label.setHorizontalAlignment(SwingConstants.CENTER);
            label.setFont(new Font("微软雅黑", Font.BOLD, 14));
            label.setForeground(ModernColorScheme.HEADER_TEXT);
            label.setBackground(ModernColorScheme.HEADER_BG);
            label.setOpaque(true);
            label.setBorder(BorderFactory.createLineBorder(ModernColorScheme.BORDER_LIGHT));
            courseGrid.add(label);
        }

        // 先获取该周的所有课程（优化：避免在循环中重复查询）
        List<Course> weeklyCourses = repository.getCoursesByWeek(currentUser.getId(), week);
        System.out.println("加载第" + week + "周的课程，共 " + weeklyCourses.size() + " 门课程");
        for (Course c : weeklyCourses) {
            System.out.println("  课程: " + c.getName() + ", 星期: " + c.getDayOfWeek() + ", 节次: " + c.getTimeSlot() + ", 周次: " + c.getWeek());
        }

        // 添加时间列和课程内容
        // 时间数组 - 节次和时间分开
        String[][] timeData = {
            {"第1-2节", "08:00-09:40"},
            {"第3-4节", "10:00-11:40"},
            {"第5-6节", "14:00-15:40"},
            {"第7-8节", "16:00-17:40"},
            {"第9-10节", "19:00-20:40"}
        };

        for (int i = 0; i < timeData.length; i++) {
            String timeSlot = timeData[i][0];
            String timeRange = timeData[i][1];
            
            // 左侧：时间列标签 - 使用HTML实现更好的布局
            JLabel timeLabel = new JLabel("<html><div style='text-align:center;'>" + 
                "<div style='font-weight:bold; font-size:12px; margin-bottom:4px;'>" + timeSlot + "</div>" +
                "<div style='font-size:11px; color:#666;'>" + timeRange + "</div>" +
                "</div></html>");
            timeLabel.setHorizontalAlignment(SwingConstants.CENTER);
            timeLabel.setVerticalAlignment(SwingConstants.CENTER);
            timeLabel.setBackground(ModernColorScheme.TIME_CELL_BG);
            timeLabel.setForeground(ModernColorScheme.TIME_CELL_TEXT);
            timeLabel.setOpaque(true);
            timeLabel.setBorder(BorderFactory.createLineBorder(ModernColorScheme.BORDER_LIGHT));
            courseGrid.add(timeLabel);

            // 每一天的课程（从星期一开始到星期日）
            String[] daysOrder = {"星期一", "星期二", "星期三", "星期四", "星期五", "星期六", "星期日"};
            for (String day : daysOrder) {
                List<Course> dailyCourses = filterByDay(weeklyCourses, day);
                
                // 查找当前时间段的具体课程对象
                Course currentCourse = null;
                final String courseText; // 声明为final
                String tempText = "";
                for (Course course : dailyCourses) {
                    if (course.getTimeSlot().equals(timeSlot)) {
                        currentCourse = course;
                        tempText = course.getName() + "\n" + course.getTeacher() + "\n" + course.getLocation();
                        break;
                    }
                }
                courseText = tempText; // 赋值后不再改变
                
                // 创建课程单元格 - 使用JLabel和HTML实现居中对齐
                JLabel courseLabel;
                
                if (!courseText.isEmpty() && currentCourse != null) {
                    // 有课程时，使用HTML格式化文本，居中对齐
                    String htmlText = "<html><div style='text-align:center;'>" +
                        "<div style='font-size:12px; margin-bottom:4px; color:#333;'>" + 
                        currentCourse.getName() + "</div>" +
                        "<div style='font-size:12px; color:#555; margin-bottom:3px;'>" + 
                        currentCourse.getTeacher() + "</div>" +
                        "<div style='font-size:11px; color:#777;'>" + 
                        currentCourse.getLocation() + "</div>" +
                        "</div></html>";
                    
                    courseLabel = new JLabel(htmlText);
                    courseLabel.setHorizontalAlignment(SwingConstants.CENTER);
                    courseLabel.setVerticalAlignment(SwingConstants.CENTER);
                    courseLabel.setBackground(ModernColorScheme.COURSE_CELL_BG);
                    courseLabel.setOpaque(true);
                    courseLabel.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));
                    
                    // 添加点击删除功能
                    courseLabel.setToolTipText("点击删除此课程");
                    courseLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                    
                    // 直接使用找到的课程对象，包含完整信息
                    final Course courseToDelete = currentCourse;
                    courseLabel.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseClicked(MouseEvent e) {
                            int result = JOptionPane.showConfirmDialog(
                                    MainFrame.this,
                                    "确定要删除这门课吗？\n" + courseText,
                                    "确认删除",
                                    JOptionPane.YES_NO_OPTION);
                            if (result == JOptionPane.YES_OPTION) {
                                // 使用课程对象的所有属性来删除，确保准确性
                                boolean deleted = repository.removeCourse(
                                        courseToDelete.getName(), 
                                        courseToDelete.getDayOfWeek(), 
                                        courseToDelete.getTimeSlot(), 
                                        currentUser, 
                                        courseToDelete.getWeek());
                                          
                                if (deleted) {
                                    loadCoursesForWeek(week); // 刷新界面
                                    JOptionPane.showMessageDialog(MainFrame.this, "课程删除成功!");
                                } else {
                                    JOptionPane.showMessageDialog(MainFrame.this, "课程删除失败，请检查是否存在该课程", "错误", JOptionPane.ERROR_MESSAGE);
                                }
                            }
                        }
                        
                        // 简单的鼠标悬停效果
                        @Override
                        public void mouseEntered(MouseEvent e) {
                            courseLabel.setBackground(ModernColorScheme.COURSE_CELL_HOVER);
                            // 更新HTML文本颜色为白色
                            String hoverHtml = "<html><div style='text-align:center;'>" +
                                "<div style='font-size:12px; margin-bottom:4px; color:#FFF;'>" + 
                                courseToDelete.getName() + "</div>" +
                                "<div style='font-size:12px; color:#FFF; margin-bottom:3px;'>" + 
                                courseToDelete.getTeacher() + "</div>" +
                                "<div style='font-size:11px; color:#FFF;'>" + 
                                courseToDelete.getLocation() + "</div>" +
                                "</div></html>";
                            courseLabel.setText(hoverHtml);
                        }
                        
                        @Override
                        public void mouseExited(MouseEvent e) {
                            courseLabel.setBackground(ModernColorScheme.COURSE_CELL_BG);
                            // 恢复原始HTML文本颜色
                            String normalHtml = "<html><div style='text-align:center;'>" +
                                "<div style='font-size:12px; margin-bottom:4px; color:#333;'>" + 
                                courseToDelete.getName() + "</div>" +
                                "<div style='font-size:12px; color:#555; margin-bottom:3px;'>" + 
                                courseToDelete.getTeacher() + "</div>" +
                                "<div style='font-size:11px; color:#777;'>" + 
                                courseToDelete.getLocation() + "</div>" +
                                "</div></html>";
                            courseLabel.setText(normalHtml);
                        }
                    });
                } else {
                    // 无课时使用白色背景
                    courseLabel = new JLabel("");
                    courseLabel.setHorizontalAlignment(SwingConstants.CENTER);
                    courseLabel.setVerticalAlignment(SwingConstants.CENTER);
                    courseLabel.setBackground(Color.WHITE);
                    courseLabel.setBackground(ModernColorScheme.EMPTY_CELL_BG);
                    courseLabel.setOpaque(true);
                    courseLabel.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));
                }

                courseGrid.add(courseLabel);
            }
        }

        courseGrid.revalidate();
        courseGrid.repaint();
    }
    
    /**
     * 加载并显示课程表（兼容旧方法）
     */
    private void loadCourseSchedule() {
        loadCoursesForWeek(1); // 默认加载第1周
    }

    /**
     * 根据时间和当天课程列表，查找匹配的课程文本
     */
    private String findCourseInTimeSlot(List<Course> courses, String timeSlot) {
        for (Course course : courses) {
            if (course.getTimeSlot().equals(timeSlot)) {
                return course.getName() + "\\n" + course.getTeacher() + "\\n" + course.getLocation();
            }
        }
        return ""; // 无课
    }

    private List<Course> filterByDay(List<Course> courses, String day) {
        java.util.List<Course> result = new java.util.ArrayList<>();
        for (Course c : courses) {
            if (c.getDayOfWeek().equals(day)) {
                result.add(c);
            }
        }
        return result;
    }

    /**
     * 显示添加课程对话框
     */
    private void showAddCourseDialog() {
        JDialog dialog = new JDialog(this, "添加课程", true);
        dialog.setSize(350, 450); // 增加高度以适应范围选择组件
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new GridBagLayout());
        dialog.getContentPane().setBackground(ModernColorScheme.DIALOG_BG);
        
        // 创建组件和布局
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblName = new JLabel("课程名：");
        JTextField txtName = new JTextField(15);

        JLabel lblTeacher = new JLabel("教师：");
        JTextField txtTeacher = new JTextField(15);

        JLabel lblDay = new JLabel("星期：");
        // 使用MultiSelectComboBox但只允许单选
        MultiSelectComboBox cbDay = new MultiSelectComboBox(days);

        // 节次选择（下拉多选）
        JLabel periodLabel = new JLabel("节次（多选）:");
        String[] periods = {"第1-2节", "第3-4节", "第5-6节", "第7-8节", "第9-10节"};
        MultiSelectComboBox periodCombo = new MultiSelectComboBox(periods);

        JLabel lblLocation = new JLabel("地点：");
        JTextField txtLocation = new JTextField(15);
        
        // 周次选择（下拉多选和范围选择）
        JLabel weekLabel = new JLabel("周次选择:");
        String[] weeks = new String[18];
        for (int i = 0; i < 18; i++) {
            weeks[i] = "第" + (i+1) + "周";
        }
        MultiSelectComboBox weekCombo = new MultiSelectComboBox(weeks);
        
        // 添加范围选择组件
        JLabel rangeLabel = new JLabel("或范围选择:");
        JTextField startWeekField = new JTextField(3);
        startWeekField.setBackground(Color.WHITE);
        JLabel toLabel = new JLabel("至");
        JTextField endWeekField = new JTextField(3);
        endWeekField.setBackground(Color.WHITE);
        JButton applyRangeButton = new JButton("应用");
        applyRangeButton.setBackground(new Color(47, 111, 237));
        applyRangeButton.setForeground(Color.BLACK); // 改为黑色
        applyRangeButton.setFocusPainted(false);
        
        // 范围选择应用按钮事件
        applyRangeButton.addActionListener(e -> {
            try {
                String startText = startWeekField.getText().trim();
                String endText = endWeekField.getText().trim();
                
                if (startText.isEmpty() || endText.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "请输入起始周和结束周", "输入错误", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                int startWeek = Integer.parseInt(startText);
                int endWeek = Integer.parseInt(endText);
                
                if (startWeek < 1 || endWeek > 18 || startWeek > endWeek) {
                    JOptionPane.showMessageDialog(dialog, "请输入有效的周数范围 (1-18)", "输入错误", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // 清除现有选择并添加范围内的周次
                List<String> rangeWeeks = new ArrayList<>();
                for (int i = startWeek; i <= endWeek; i++) {
                    rangeWeeks.add("第" + i + "周");
                }
                weekCombo.setSelectedItems(rangeWeeks);
                JOptionPane.showMessageDialog(dialog, "已选择第" + startWeek + "周至第" + endWeek + "周", "选择成功", JOptionPane.INFORMATION_MESSAGE);
                
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "请输入有效的数字", "输入错误", JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton btnSave = createGlassButton("保存", new Color(47, 111, 237));
        JButton btnCancel = createGlassButton("取消", new Color(150, 150, 150));

        // 布局
        gbc.gridx = 0; gbc.gridy = 0; dialog.add(lblName, gbc);
        gbc.gridx = 1; dialog.add(txtName, gbc);

        gbc.gridx = 0; gbc.gridy = 1; dialog.add(lblTeacher, gbc);
        gbc.gridx = 1; dialog.add(txtTeacher, gbc);

        gbc.gridx = 0; gbc.gridy = 2; dialog.add(lblDay, gbc);
        gbc.gridx = 1; dialog.add(cbDay, gbc);

        gbc.gridx = 0; gbc.gridy = 3; dialog.add(periodLabel, gbc);
        gbc.gridx = 1; dialog.add(periodCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 4; dialog.add(lblLocation, gbc);
        gbc.gridx = 1; dialog.add(txtLocation, gbc);
        
        gbc.gridx = 0; gbc.gridy = 5; dialog.add(weekLabel, gbc);
        gbc.gridx = 1; dialog.add(weekCombo, gbc);
        
        // 添加范围选择组件布局
        gbc.gridx = 0; gbc.gridy = 6; dialog.add(rangeLabel, gbc);
        
        JPanel rangePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        rangePanel.add(startWeekField);
        rangePanel.add(toLabel);
        rangePanel.add(endWeekField);
        rangePanel.add(applyRangeButton);
        
        gbc.gridx = 1; gbc.gridy = 6; dialog.add(rangePanel, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(btnSave);
        buttonPanel.add(btnCancel);

        gbc.gridx = 0; gbc.gridy = 7; gbc.gridwidth = 2;
        dialog.add(buttonPanel, gbc);

        // 保存按钮事件
        btnSave.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 获取表单数据
                String name = txtName.getText().trim();
                String teacher = txtTeacher.getText().trim();
                // 从MultiSelectComboBox获取选中的星期（只取第一个）
                List<String> selectedDays = cbDay.getSelectedItems();
                if (selectedDays.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "请选择星期", "错误", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                String day = selectedDays.get(0);
                String location = txtLocation.getText().trim();

                // 基本验证
                if (name.isEmpty() || teacher.isEmpty() || location.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "请填写所有必填字段", "错误", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // 获取节次
                List<String> selectedPeriodsStr = periodCombo.getSelectedItems();
                List<int[]> selectedPeriods = new ArrayList<>();
                if (selectedPeriodsStr.contains("第1-2节")) selectedPeriods.add(new int[]{1, 2});
                if (selectedPeriodsStr.contains("第3-4节")) selectedPeriods.add(new int[]{3, 4});
                if (selectedPeriodsStr.contains("第5-6节")) selectedPeriods.add(new int[]{5, 6});
                if (selectedPeriodsStr.contains("第7-8节")) selectedPeriods.add(new int[]{7, 8});
                if (selectedPeriodsStr.contains("第9-10节")) selectedPeriods.add(new int[]{9, 10});

                if (selectedPeriods.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "请至少选择一个节次！");
                    return;
                }

                // 获取周次
                List<String> selectedWeeksStr = weekCombo.getSelectedItems();
                List<Integer> selectedWeeks = new ArrayList<>();
                for (String w : selectedWeeksStr) {
                    selectedWeeks.add(Integer.parseInt(w.replace("第", "").replace("周", "")));
                }

                if (selectedWeeks.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "请至少选择一个教学周！");
                    return;
                }

                // 检查每个时间段的课程冲突
                boolean hasConflict = false;
                StringBuilder conflictInfo = new StringBuilder();
                
                for (int week : selectedWeeks) {
                    List<Course> weeklyCourses = repository.getCoursesByWeek(currentUser.getId(), week);
                    for (int[] period : selectedPeriods) {
                        String periodStr = "第" + period[0] + "-" + period[1] + "节";
                        for (Course c : weeklyCourses) {
                            if (c.getDayOfWeek().equals(day) && c.getTimeSlot().equals(periodStr)) {
                                hasConflict = true;
                                conflictInfo.append("第").append(week).append("周 ").append(periodStr).append("\n");
                            }
                        }
                    }
                }
                
                if (hasConflict) {
                    JOptionPane.showMessageDialog(dialog, "以下时间段已有课程，请选择其他时间：\n" + conflictInfo.toString(), "课程冲突", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // 为每个周次和节次组合创建并保存课程
                int successCount = 0;
                int failCount = 0;
                StringBuilder failInfo = new StringBuilder();
                
                System.out.println("开始添加课程，周次: " + selectedWeeks + ", 节次数量: " + selectedPeriods.size());
                
                for (int week : selectedWeeks) {
                    for (int[] period : selectedPeriods) {
                        Course newCourse = new Course();
                        newCourse.setName(name);
                        newCourse.setTeacher(teacher);
                        newCourse.setDayOfWeek(day);
                        newCourse.setTimeSlot("第" + period[0] + "-" + period[1] + "节");
                        newCourse.setLocation(location);
                        newCourse.setWeek(week);
                        
                        System.out.println("添加课程: " + name + ", 第" + week + "周, " + day + ", " + newCourse.getTimeSlot());

                        boolean success = repository.addCourse(newCourse, currentUser);
                        if (success) {
                            successCount++;
                            System.out.println("  成功添加到第" + week + "周");
                        } else {
                            failCount++;
                            failInfo.append("第").append(week).append("周 ").append(newCourse.getTimeSlot()).append("\n");
                            System.out.println("  失败：第" + week + "周 " + newCourse.getTimeSlot());
                        }
                    }
                }
                
                System.out.println("添加完成：成功 " + successCount + " 个，失败 " + failCount + " 个");

                // 显示添加结果
                String message;
                if (failCount == 0) {
                    message = "课程添加成功，共添加 " + successCount + " 个课时\n" +
                             "课程已添加到：" + String.join(", ", selectedWeeksStr);
                    JOptionPane.showMessageDialog(dialog, message, "成功", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    message = "部分课程添加失败！\n" +
                             "成功添加：" + successCount + " 个课时\n" +
                             "失败：" + failCount + " 个课时\n" +
                             "失败详情：\n" + failInfo.toString();
                    JOptionPane.showMessageDialog(dialog, message, "警告", JOptionPane.WARNING_MESSAGE);
                }
                
                dialog.dispose();
                // 刷新界面，默认显示第一周
                loadCoursesForWeek(selectedWeeks.get(0));
                // 设置状态提示，提醒用户可以通过周选择器查看其他周的课程
                if (selectedWeeks.size() > 1) {
                    statusLabel.setText("提示：课程已添加到多个周次，请使用上方的周选择器切换查看");
                } else {
                    statusLabel.setText("课程添加成功");
                }
            }
        });

        // 取消按钮事件
        btnCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
            }
        });

        dialog.setVisible(true);
    }

    /**
     * 获取课程表面板
     */
    private JPanel getCourseTablePanel() {
        return coursePanel;
    }
    
    /**
     * 导出课程表为图片
     */
    private void exportTableAsImage() {
        // 创建自定义对话框选择周次
        JDialog weekDialog = new JDialog(this, "选择周次", true);
        weekDialog.setSize(350, 200);
        weekDialog.setLocationRelativeTo(this);
        weekDialog.setLayout(new BorderLayout());
        weekDialog.getContentPane().setBackground(ModernColorScheme.DIALOG_BG);
        
        // 内容面板
        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.setOpaque(false);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
        
        JLabel label = new JLabel("请选择要导出的周次：");
        label.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        contentPanel.add(label, BorderLayout.NORTH);
        
        // 周次选择（使用MultiSelectComboBox但只允许单选）
        String[] weekOptions = new String[18];
        for (int i = 0; i < 18; i++) {
            weekOptions[i] = "第" + (i + 1) + "周";
        }
        MultiSelectComboBox weekCombo = new MultiSelectComboBox(weekOptions);
        
        // 设置默认选中当前周次
        int currentWeek = weekSelector != null ? weekSelector.getSelectedIndex() + 1 : 1;
        List<String> defaultWeek = new ArrayList<>();
        defaultWeek.add("第" + currentWeek + "周");
        weekCombo.setSelectedItems(defaultWeek);
        
        contentPanel.add(weekCombo, BorderLayout.CENTER);
        
        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setOpaque(false);
        
        final int[] result = {-1};
        
        JButton okButton = createSmallGlassButton("确定", new Color(47, 111, 237));
        JButton cancelButton = createSmallGlassButton("取消", new Color(150, 150, 150));
        
        okButton.addActionListener(e -> {
            List<String> selected = weekCombo.getSelectedItems();
            if (selected.isEmpty()) {
                JOptionPane.showMessageDialog(weekDialog, "请选择一个周次", "提示", JOptionPane.WARNING_MESSAGE);
                return;
            }
            result[0] = Integer.parseInt(selected.get(0).replace("第", "").replace("周", ""));
            weekDialog.dispose();
        });
        
        cancelButton.addActionListener(e -> {
            result[0] = -1;
            weekDialog.dispose();
        });
        
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        
        weekDialog.add(contentPanel, BorderLayout.CENTER);
        weekDialog.add(buttonPanel, BorderLayout.SOUTH);
        weekDialog.setVisible(true);
        
        // 用户取消选择
        if (result[0] == -1) {
            return;
        }
        
        int exportWeek = result[0];
        
        // 加载要导出的周次
        loadCoursesForWeek(exportWeek);
        
        // 更新界面
        courseGrid.revalidate();
        courseGrid.repaint();
        
        // 更新周选择器
        if (weekSelector != null) {
            weekSelector.setSelectedIndex(exportWeek - 1);
        }
        
        // 强制刷新界面
        SwingUtilities.invokeLater(() -> {
            // 获取要导出的组件（课程表面板）
            JPanel tablePanel = getCourseTablePanel();

            if (tablePanel == null) {
                JOptionPane.showMessageDialog(this, "未找到课程表！");
                return;
            }
            
            // 继续导出流程
            performExport(tablePanel, exportWeek);
        });
    }
    
    /**
     * 执行导出操作
     */
    private void performExport(JPanel tablePanel, int exportWeek) {

        // 弹出文件保存对话框
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setSelectedFile(new File("第" + exportWeek + "周课程表.png")); // 默认文件名包含周次
        fileChooser.setDialogTitle("保存课程表为图片");
        fileChooser.setAcceptAllFileFilterUsed(false);

        // 只允许 .png 文件
        FileNameExtensionFilter filter = new FileNameExtensionFilter("PNG Image (*.png)", "png");
        fileChooser.addChoosableFileFilter(filter);
        fileChooser.setFileFilter(filter);

        int result = fileChooser.showSaveDialog(this);
        if (result != JFileChooser.APPROVE_OPTION) {
            // 用户取消，保持当前周次
            return;
        }

        File selectedFile = fileChooser.getSelectedFile();
        String filePath = selectedFile.getAbsolutePath();

        // 如果没有 .png 后缀，自动加上
        if (!filePath.toLowerCase().endsWith(".png")) {
            filePath += ".png";
            selectedFile = new File(filePath);
        }

        // 检查文件是否已存在，提示覆盖
        if (selectedFile.exists()) {
            int overwrite = JOptionPane.showConfirmDialog(
                this,
                "文件已存在，是否覆盖？",
                "文件已存在",
                JOptionPane.YES_NO_OPTION
            );
            if (overwrite != JOptionPane.YES_OPTION) {
                // 用户取消覆盖，保持当前周次
                return;
            }
        }

        // 创建高分辨率图像
        int width = tablePanel.getWidth();
        int height = tablePanel.getHeight();
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        // 使用 Graphics2D 绘制组件到图像
        Graphics2D g2d = image.createGraphics();

        // 开启抗锯齿
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // 填充白色背景
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, width, height);

        // 绘制组件
        tablePanel.paint(g2d);

        g2d.dispose();

        // 保存为 PNG
        try {
            ImageIO.write(image, "png", selectedFile);
            JOptionPane.showMessageDialog(this, "✅课程表已成功导出为图片！\n" + filePath);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "❌导出失败：" + ex.getMessage());
            ex.printStackTrace();
        }
    }
    
    /**
     * 导出课程表（旧方法，现在调用exportTableAsImage）
     */
    private void exportSchedule() {
        exportTableAsImage();
    }
}