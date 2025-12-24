package main;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class MultiSelectComboBox extends JPanel {
    private JButton button;
    private JPopupMenu popup;
    private List<JCheckBoxMenuItem> checkBoxItems;
    private List<String> selectedItems;

    public MultiSelectComboBox(String[] items) {
        setLayout(new BorderLayout());
        button = new JButton("请选择");
        button.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setBackground(Color.WHITE); // 设置白色背景
        button.setFocusPainted(false); // 去除焦点边框

        popup = new JPopupMenu();
        checkBoxItems = new ArrayList<>();
        selectedItems = new ArrayList<>();

        for (String item : items) {
            JCheckBoxMenuItem cb = new JCheckBoxMenuItem(item);
            cb.addActionListener(e -> updateButtonText());
            popup.add(cb);
            checkBoxItems.add(cb);
        }

        button.addActionListener(e -> {
            popup.show(button, 0, button.getHeight());
        });

        add(button, BorderLayout.CENTER);
    }

    private void updateButtonText() {
        selectedItems.clear();
        StringBuilder sb = new StringBuilder();
        for (JCheckBoxMenuItem cb : checkBoxItems) {
            if (cb.isSelected()) {
                selectedItems.add(cb.getText());
                if (sb.length() > 0) sb.append(", ");
                sb.append(cb.getText());
            }
        }
        
        // 限制显示长度：如果选择太多，显示"已选X项"而不是全部列出
        if (selectedItems.size() > 3) {
            button.setText("已选" + selectedItems.size() + "项");
        } else {
            button.setText(sb.length() == 0 ? "请选择" : sb.toString());
        }
        
        // 取消颜色提示，保持默认样式
        // button.setBackground(selectedItems.isEmpty() ? null : new Color(230, 240, 255));
    }

    public List<String> getSelectedItems() {
        return new ArrayList<>(selectedItems);
    }

    public void clearSelection() {
        for (JCheckBoxMenuItem cb : checkBoxItems) {
            cb.setSelected(false);
        }
        updateButtonText();
    }

    public void setSelectedItems(List<String> items) {
        clearSelection();
        for (JCheckBoxMenuItem cb : checkBoxItems) {
            if (items.contains(cb.getText())) {
                cb.setSelected(true);
            }
        }
        updateButtonText();
    }
}