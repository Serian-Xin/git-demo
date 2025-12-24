package data;

import model.Course;

import javax.swing.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 课程数据文件工具类：读写 CSV 文件
 * 格式：name,teacher,dayOfWeek,timeSlot,location
 */
public class CourseFileUtil {
    private static final String FILE_PATH = "courses.csv";

    /**
     * 保存所有课程到 CSV 文件
     */
    public static void saveToFile(List<Course> courses) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(FILE_PATH))) {
            // 写入表头
            writer.println("课程名,教师,星期,节次,地点");
            // 写入数据
            for (Course course : courses) {
                writer.printf("%s,%s,%s,%s,%s%n",
                        escapeCsv(course.getName()),
                        escapeCsv(course.getTeacher()),
                        escapeCsv(course.getDayOfWeek()),
                        escapeCsv(course.getTimeSlot()),
                        escapeCsv(course.getLocation()));
            }
            System.out.println("课程数据已保存到: " + FILE_PATH);
        } catch (IOException e) {
            System.err.println("保存文件失败: " + e.getMessage());
            JOptionPane.showMessageDialog(null, "保存课程数据失败！", "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * 从 CSV 文件加载课程数据
     */
    public static List<Course> loadFromFile() {
        List<Course> courses = new ArrayList<>();
        File file = new File(FILE_PATH);

        // 如果文件不存在，返回空列表（首次运行）
        if (!file.exists()) {
            System.out.println("未找到 " + FILE_PATH + "，使用默认数据。");
            return courses;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            reader.readLine(); // 跳过表头
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = splitCsvLine(line);
                if (parts.length == 5) {
                    courses.add(new Course(
                            parts[0].trim(),
                            parts[1].trim(),
                            parts[2].trim(),
                            parts[3].trim(),
                            parts[4].trim()
                    ));
                }
            }
            System.out.println("从 " + FILE_PATH + " 加载了 " + courses.size() + " 条课程数据。");
        } catch (IOException e) {
            System.err.println("读取文件失败: " + e.getMessage());
            JOptionPane.showMessageDialog(null, "读取课程数据失败，将使用默认数据。", "警告", JOptionPane.WARNING_MESSAGE);
        }

        return courses;
    }

    /**
     * 简单 CSV 转义（处理含逗号的字段）
     */
    private static String escapeCsv(String field) {
        if (field.contains(",") || field.contains("\"")) {
            return "\"" + field.replace("\"", "\"\"") + "\"";
        }
        return field;
    }

    /**
     * 分割 CSV 行（支持带引号的字段）
     */
    private static String[] splitCsvLine(String line) {
        List<String> result = new ArrayList<>();
        boolean inQuotes = false;
        StringBuilder current = new StringBuilder();

        for (char c : line.toCharArray()) {
            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                result.add(current.toString());
                current.setLength(0);
            } else {
                current.append(c);
            }
        }
        result.add(current.toString());
        return result.toArray(new String[0]);
    }
}


