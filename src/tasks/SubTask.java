package tasks;

import tools.TaskStatus;
import tools.Type;

public class SubTask extends Task {

    private int epicTaskId;
    private final Type type;

    public SubTask(int epicTaskId, String taskName, String tasksInfo, TaskStatus status) {
        super(taskName, tasksInfo, status);
        this.epicTaskId = epicTaskId;
        this.type = Type.SUBTASK;
    }

    public SubTask(String taskName, String tasksInfo, int taskId, TaskStatus status, int epicTaskId) {
        super(taskName, tasksInfo, taskId, status);
        this.epicTaskId = epicTaskId;
        this.type = Type.SUBTASK;
    }

    public Type getType() {
        return type;
    }

    public int getEpicTaskId() {
        return epicTaskId;
    }

    public void setEpicTaskId(int epicTaskId) {
        this.epicTaskId = epicTaskId;
    }

    @Override
    public String toString() {
        return "Subtask { Идентификатор: " + getTaskId()
                + " Задача: " + getTaskName()
                + " Описание: " + getTasksInfo()
                + " Статус: " + getStatus()
                + " Является подзадачей для задачи с идентификатором: " + getEpicTaskId();
    }

    @Override
    public String toStringFromFile() {
        return String.format("%s,%s,%s,%s,%s,%s", getEpicTaskId(), getTaskId(), getType(), getTaskName(),
                getStatus(), getTasksInfo());
    }
}
