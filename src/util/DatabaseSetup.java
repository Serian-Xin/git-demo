package util;

import data.DatabaseConnection;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * 数据库初始化工具
 * 创建数据库、表结构和测试数据
 */
public class DatabaseSetup {
    
    public static void main(String[] args) {
        System.out.println("=== 数据库初始化 ===");
        
        try {
            // 1. 创建数据库
            createDatabase();
            
            // 2. 创建表结构
            createTables();
            
            // 3. 插入测试数据
            insertTestData();
            
            System.out.println("✅ 数据库初始化完成！");
            
        } catch (Exception e) {
            System.out.println("❌ 初始化失败：" + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 创建数据库
     */
    private static void createDatabase() throws SQLException {
        System.out.println("\n1. 创建数据库...");
        
        // 先连接到 MySQL 服务器（不指定数据库）
        String url = "jdbc:mysql://localhost:3306/?useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true";
        try (Connection conn = java.sql.DriverManager.getConnection(url, "root", "123456");
             Statement stmt = conn.createStatement()) {
            
            // 创建数据库
            stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS course_manager CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci");
            System.out.println("✅ 数据库 course_manager 创建成功");
            
        }
    }
    
    /**
     * 创建表结构
     */
    private static void createTables() throws SQLException {
        System.out.println("\n2. 创建表结构...");
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            
            // 创建用户表
            String createUsersTable = """
                CREATE TABLE IF NOT EXISTS users (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    student_id VARCHAR(20) UNIQUE NOT NULL,
                    password VARCHAR(64) NOT NULL,
                    name VARCHAR(50) DEFAULT '学生',
                    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
                )
                """;
            stmt.executeUpdate(createUsersTable);
            System.out.println("✅ 用户表 users 创建成功");
            
            // 创建课程表
            String createCoursesTable = """
                CREATE TABLE IF NOT EXISTS courses (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    user_id INT NOT NULL,
                    name VARCHAR(100) NOT NULL,
                    teacher VARCHAR(50),
                    day_of_week VARCHAR(10) NOT NULL,
                    time_slot VARCHAR(20) NOT NULL,
                    location VARCHAR(100),
                    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                    UNIQUE KEY unique_course (user_id, day_of_week, time_slot)
                )
                """;
            stmt.executeUpdate(createCoursesTable);
            System.out.println("✅ 课程表 courses 创建成功");
            
        }
    }
    
    /**
     * 插入测试数据
     */
    private static void insertTestData() throws SQLException {
        System.out.println("\n3. 插入测试数据...");
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            
            // 插入测试用户（密码 123456 的 SHA-256）
            String insertUser = """
                INSERT IGNORE INTO users (student_id, password, name) VALUES 
                ('20250001', 'e10adc3949ba59abbe56e057f20f883e', '张三'),
                ('20250002', 'e10adc3949ba59abbe56e057f20f883e', '李四')
                """;
            int userCount = stmt.executeUpdate(insertUser);
            System.out.println("✅ 插入了 " + userCount + " 个测试用户");
            
            // 插入测试课程（假设用户ID为1）
            String insertCourses = """
                INSERT IGNORE INTO courses (user_id, name, teacher, day_of_week, time_slot, location) VALUES 
                (1, '高等数学', '张教授', '星期一', '第1-2节', 'A栋101'),
                (1, '大学英语', '李老师', '星期二', '第3-4节', 'B栋205'),
                (1, 'Java程序设计', '王老师', '星期三', '第5-6节', '实验楼301'),
                (1, '体育', '刘教练', '星期五', '第7-8节', '体育馆')
                """;
            int courseCount = stmt.executeUpdate(insertCourses);
            System.out.println("插入了 " + courseCount + " 门测试课程"); // 移除emoji图标
            
            System.out.println("\n测试账号信息："); // 移除emoji图标
            System.out.println("   学号: 20250001, 密码: 123456, 姓名: 张三");
            System.out.println("   学号: 20250002, 密码: 123456, 姓名: 李四");
            
        }
    }
}
