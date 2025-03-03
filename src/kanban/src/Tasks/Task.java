package kanban.src.Tasks;

import kanban.src.Tools.TaskStatus;

public class Task {

    private String tasksInfo;
    private String taskName;
    private int taskId;
    private TaskStatus status;

    public Task(String taskName, String tasksInfo, TaskStatus status) {
        this.taskName = taskName;
        this.tasksInfo = tasksInfo;
        this.status = status;
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

    @Override
    public String toString() {
        return "Task { Идентификатор: " + taskId
                + " Задача: " + taskName
                + " Описание: " + tasksInfo
                + " Статус: " + status + "}";
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

}
