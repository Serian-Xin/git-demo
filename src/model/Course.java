package model;

/**
 * 课程实体类
 * 表示一个课程的基本信息
 */
public class Course {
    private int id;             // 课程ID
    private String name;        // 课程名
    private String teacher;     // 授课教师
    private String dayOfWeek;   // 星期几（如 "星期一"）
    private String timeSlot;    // 节次（如 "第1-2节"）
    private String location;    // 上课地点
    private int week;           // 教学周，默认1

    // 构造方法
    public Course(String name, String teacher, String dayOfWeek, String timeSlot, String location) {
        this.name = name;
        this.teacher = teacher;
        this.dayOfWeek = dayOfWeek;
        this.timeSlot = timeSlot;
        this.location = location;
        this.week = 1; // 默认第1周
    }
    
    // 无参构造方法，用于从数据库查询时使用
    public Course() {
        this.week = 1;
    }

    // Getter 和 Setter 方法
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTeacher() {
        return teacher;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public String getTimeSlot() {
        return timeSlot;
    }

    public void setTimeSlot(String timeSlot) {
        this.timeSlot = timeSlot;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public int getWeek() {
        return week;
    }
    
    public void setWeek(int week) {
        this.week = week;
    }

//通过 toString() 方法便于日志打印和调试。
    @Override
    public String toString() {
        return "Course{" +
                "name='" + name + '\'' +
                ", teacher='" + teacher + '\'' +
                ", dayOfWeek='" + dayOfWeek + '\'' +
                ", timeSlot='" + timeSlot + '\'' +
                ", location='" + location + '\'' +
                '}';
    }
}


