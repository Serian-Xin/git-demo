package util;

import data.DatabaseConnection;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * 更新数据库表结构为多账号系统
 */
public class UpdateDatabaseSchema {
    
    public static void main(String[] args) {
        System.out.println("=== 更新数据库表结构 ===");
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            
            // 1. 备份现有数据（可选）
            System.out.println("1. 备份现有用户数据...");
            
            // 2. 删除旧表
            System.out.println("2. 删除旧表结构...");
            stmt.executeUpdate("DROP TABLE IF EXISTS courses");
            stmt.executeUpdate("DROP TABLE IF EXISTS users");
            
            // 3. 创建新的用户表
            System.out.println("3. 创建新的用户表...");
            String createUsersTable = """
                CREATE TABLE users (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    username VARCHAR(50) UNIQUE NOT NULL,
                    password VARCHAR(64) NOT NULL,
                    name VARCHAR(50) DEFAULT '学生',
                    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
                )
                """;
            stmt.executeUpdate(createUsersTable);
            System.out.println("✅ 用户表创建成功");
            
            // 4. 创建新的课程表
            System.out.println("4. 创建新的课程表...");
            String createCoursesTable = """
                CREATE TABLE courses (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    user_id INT NOT NULL,
                    name VARCHAR(100) NOT NULL,
                    teacher VARCHAR(50),
                    day_of_week VARCHAR(10),
                    time_slot VARCHAR(20),
                    location VARCHAR(100),
                    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                    UNIQUE KEY unique_course (user_id, day_of_week, time_slot)
                )
                """;
            stmt.executeUpdate(createCoursesTable);
            System.out.println("✅ 课程表创建成功");
            
            // 5. 插入测试数据
            System.out.println("5. 插入测试数据...");
            String insertTestUsers = """
                INSERT INTO users (username, password, name) VALUES 
                ('admin', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', '管理员'),
                ('student1', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', '张三'),
                ('student2', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', '李四')
                """;
            int userCount = stmt.executeUpdate(insertTestUsers);
            System.out.println("✅ 插入了 " + userCount + " 个测试用户");
            
            // 6. 插入测试课程（分配给用户ID=2，即student1）
            String insertTestCourses = """
                INSERT INTO courses (user_id, name, teacher, day_of_week, time_slot, location) VALUES 
                (2, '高等数学', '张教授', '星期一', '第1-2节', 'A栋101'),
                (2, '大学英语', '李老师', '星期二', '第3-4节', 'B栋205'),
                (2, 'Java程序设计', '王老师', '星期三', '第5-6节', '实验楼301'),
                (2, '体育', '刘教练', '星期五', '第7-8节', '体育馆')
                """;
            int courseCount = stmt.executeUpdate(insertTestCourses);
            System.out.println("插入了 " + courseCount + " 门测试课程");
            
            System.out.println("\n测试账号信息：");
            System.out.println("   账号: admin, 密码: 123456, 姓名: 管理员");
            System.out.println("   账号: student1, 密码: 123456, 姓名: 张三");
            System.out.println("   账号: student2, 密码: 123456, 姓名: 李四");
            
        } catch (Exception e) {
            System.out.println("❌ 更新失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
