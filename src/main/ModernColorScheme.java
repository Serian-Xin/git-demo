package main;

import java.awt.Color;

/**
 * 现代化配色方案 - 清新校园风格
 */
public class ModernColorScheme {
    // 主色调
    public static final Color PRIMARY = new Color(64, 158, 255);
    public static final Color PRIMARY_LIGHT = new Color(144, 202, 249);
    public static final Color PRIMARY_DARK = new Color(25, 118, 210);
    
    // 辅助色
    public static final Color ACCENT = new Color(255, 167, 38);
    public static final Color ACCENT_LIGHT = new Color(255, 204, 128);
    
    // 功能色
    public static final Color SUCCESS = new Color(102, 187, 106);
    public static final Color SUCCESS_LIGHT = new Color(200, 230, 201);
    public static final Color WARNING = new Color(255, 193, 7);
    public static final Color DANGER = new Color(239, 83, 80);
    public static final Color INFO = new Color(41, 182, 246);
    
    // 背景色
    public static final Color BG_PRIMARY = new Color(250, 252, 255);
    public static final Color BG_SECONDARY = new Color(255, 255, 255);
    public static final Color DIALOG_BG = new Color(200, 226, 234); // 弹窗背景色 #C8E2EA
    
    // 课表专用色
    public static final Color HEADER_BG = new Color(227, 242, 253);
    public static final Color HEADER_TEXT = new Color(33, 33, 33);
    public static final Color TIME_CELL_BG = new Color(224, 247, 250);
    public static final Color TIME_CELL_TEXT = new Color(66, 66, 66);
    public static final Color COURSE_CELL_BG = new Color(245, 250, 255);
    public static final Color COURSE_CELL_HOVER = new Color(255, 243, 224);
    public static final Color EMPTY_CELL_BG = new Color(255, 255, 255);
    
    // 文字色
    public static final Color TEXT_PRIMARY = new Color(33, 33, 33);
    public static final Color TEXT_SECONDARY = new Color(117, 117, 117);
    public static final Color TEXT_WHITE = new Color(255, 255, 255);
    
    // 边框和分割线
    public static final Color BORDER_LIGHT = new Color(224, 224, 224);
    public static final Color BORDER_MEDIUM = new Color(189, 189, 189);
    public static final Color DIVIDER = new Color(238, 238, 238);
    
    // 阴影色
    public static final Color SHADOW_LIGHT = new Color(0, 0, 0, 10);
    public static final Color SHADOW_MEDIUM = new Color(0, 0, 0, 20);
    
    // 渐变色
    public static final Color GRADIENT_START = new Color(240, 248, 255);
    public static final Color GRADIENT_END = new Color(230, 244, 255);
    
    /**
     * 颜色变亮
     */
    public static Color brighten(Color color, float factor) {
        int r = Math.min(255, (int)(color.getRed() * (1 + factor)));
        int g = Math.min(255, (int)(color.getGreen() * (1 + factor)));
        int b = Math.min(255, (int)(color.getBlue() * (1 + factor)));
        return new Color(r, g, b);
    }
    
    /**
     * 颜色变暗
     */
    public static Color darken(Color color, float factor) {
        int r = Math.max(0, (int)(color.getRed() * (1 - factor)));
        int g = Math.max(0, (int)(color.getGreen() * (1 - factor)));
        int b = Math.max(0, (int)(color.getBlue() * (1 - factor)));
        return new Color(r, g, b);
    }
}
