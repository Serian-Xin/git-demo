package data;

import model.Course;
import model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CourseRepository {
    public List<Course> getCoursesByUser(User user) {
        List<Course> courses = new ArrayList<>();
        String sql = "SELECT name, teacher, day_of_week, time_slot, location FROM courses WHERE user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, user.getId());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                courses.add(new Course(
                        rs.getString("name"),
                        rs.getString("teacher"),
                        rs.getString("day_of_week"),
                        rs.getString("time_slot"),
                        rs.getString("location")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return courses;
    }

    public boolean addCourse(Course course, User user) {
        String sql = "INSERT INTO courses (user_id, name, teacher, day_of_week, time_slot, location, week) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, user.getId());
            stmt.setString(2, course.getName());
            stmt.setString(3, course.getTeacher());
            stmt.setString(4, course.getDayOfWeek());
            stmt.setString(5, course.getTimeSlot());
            stmt.setString(6, course.getLocation());
            stmt.setInt(7, course.getWeek());
            
            System.out.println("执行SQL插入: user_id=" + user.getId() + ", name=" + course.getName() + 
                             ", day=" + course.getDayOfWeek() + ", time=" + course.getTimeSlot() + 
                             ", week=" + course.getWeek());
            
            int result = stmt.executeUpdate();
            System.out.println("插入结果: " + result + " 行受影响");
            return result > 0;
        } catch (SQLException e) {
            System.err.println("添加课程失败: " + e.getMessage());
            System.err.println("课程信息: name=" + course.getName() + ", week=" + course.getWeek() + 
                             ", day=" + course.getDayOfWeek() + ", time=" + course.getTimeSlot());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * 根据用户ID和教学周获取课程列表
     */
    public List<Course> getCoursesByWeek(int userId, int week) {
        List<Course> courses = new ArrayList<>();
        String sql = "SELECT * FROM courses WHERE user_id = ? AND week = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, week);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Course course = new Course();
                course.setId(rs.getInt("id"));
                course.setName(rs.getString("name"));
                course.setTeacher(rs.getString("teacher"));
                course.setDayOfWeek(rs.getString("day_of_week"));
                course.setTimeSlot(rs.getString("time_slot"));
                course.setLocation(rs.getString("location"));
                course.setWeek(rs.getInt("week"));
                courses.add(course);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return courses;
    }

    public boolean removeCourse(String name, String dayOfWeek, String timeSlot, User user, int week) {
        String sql = "DELETE FROM courses WHERE user_id = ? AND name = ? AND day_of_week = ? AND time_slot = ? AND week = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            // 添加调试信息
            System.out.println("执行删除SQL: " + sql);
            System.out.println("参数: user_id=" + user.getId() + ", name='" + name + "', day_of_week='" + dayOfWeek + "', time_slot='" + timeSlot + "', week=" + week);

            //通过这 5 个参数的精确匹配，SQL 语句能唯一锁定需要删除的课程记录
            stmt.setInt(1, user.getId());//为第 1个占位符设置参数：当前登录用户的 id（整数类型）
            stmt.setString(2, name);
            stmt.setString(3, dayOfWeek);
            stmt.setString(4, timeSlot);
            stmt.setInt(5, week);
            
            int affectedRows = stmt.executeUpdate();
            System.out.println("删除操作影响的行数: " + affectedRows);
            
            return affectedRows > 0;
        } catch (SQLException e) {
            System.out.println("删除课程时发生SQL异常: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean removeCourse(String name, String dayOfWeek, String timeSlot, User user) {
        // 默认删除第1周的课程（兼容旧方法）
        return removeCourse(name, dayOfWeek, timeSlot, user, 1);
    }
}


