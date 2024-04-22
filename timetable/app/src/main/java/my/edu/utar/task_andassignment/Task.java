package my.edu.utar.task_andassignment;
public class Task {
    private int id;
    private String taskName;
    private String date;
    private String time;
    private String description;
    private String completed;
    private int subjectId;

    public Task() {
    }

    public Task(int id, String taskName, String date, String time, String description, String completed, int subjectId) {
        this.id = id;
        this.taskName = taskName;
        this.date = date;
        this.time = time;
        this.description = description;
        this.completed = completed;
        this.subjectId = subjectId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCompleted() {
        return completed;
    }

    public void setCompleted(String completed) {
        this.completed = completed;
    }

    public int getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(int subjectId) {
        this.subjectId = subjectId;
    }
}
