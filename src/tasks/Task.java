package tasks;

import tools.TaskStatus;
import tools.Type;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Task {

    private String tasksInfo;
    private String taskName;
    private int taskId;
    private TaskStatus status;
    private Type type;
    private Duration duration;
    private LocalDateTime startTime;
    public static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm dd.MM.yyyy");

    public Task(int taskId, String taskName, String tasksInfo, TaskStatus status, Type type, Duration duration, LocalDateTime startTime) {
        this.taskId = taskId;
        this.taskName = taskName;
        this.tasksInfo = tasksInfo;
        this.status = status;
        this.type = type;
        this.duration = duration;
        this.startTime = startTime;
    }

    public Type getType() {
        return type;
    }

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getTasksInfo() {
        return tasksInfo;
    }

    public void setTasksInfo(String tasksInfo) {
        this.tasksInfo = tasksInfo;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public Duration getDuration() {
        return duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return startTime.plus(duration);
    }

    @Override
    public String toString() {
        String breakLine = "\n";
        return "Тип задачи - Task" + breakLine
                + "Идентификатор: " + taskId + breakLine
                + "Задача: " + taskName + breakLine
                + "Описание: " + tasksInfo + breakLine
                + "Статус: " + status + breakLine
                + "Старт задачи: " + startTime + breakLine
                + "Продолжительность задачи:" + duration.toMinutes() + breakLine;

    }

    @Override
    public int hashCode() {
        return taskId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return taskId == task.taskId;
    }

    public String toStringFromFile() {
        return String.format("%d,%s,%s,%s,%s,%s,%s",
                taskId,                   // Идентификатор
                type.name(),              // Тип задачи
                status.name(),            // Статус задачи
                taskName,                 // Название задачи
                tasksInfo,                // Описание задачи
                startTime.format(dateTimeFormatter), // Время старта
                duration.toString());      // Продолжительность
    }
}
