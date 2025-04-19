package tasks;

import tools.TaskStatus;
import tools.Type;

import java.util.ArrayList;
import java.util.List;

public class EpicTask extends Task {

    private List<Integer> subTaskId = new ArrayList<>();
    private final Type type;

    public EpicTask(String taskName, String tasksInfo, TaskStatus status) {
        super(taskName, tasksInfo, status);
        this.subTaskId = new ArrayList<>();
        this.type = Type.EPIC;
    }

    public EpicTask(List<Integer> subTaskId,
                    int id, String taskName,
                    String tasksInfo, TaskStatus status) {
        super(id, taskName, tasksInfo, status);
        this.subTaskId = subTaskId;
        this.type = Type.EPIC;
    }

    public Type getType() {
        return type;
    }

    public List<Integer> getSubTaskId() {
        return subTaskId;
    }

    public List<Integer> setSubTaskId(List<Integer> subTaskId) {
        return this.subTaskId = subTaskId;
    }

    public void addSubTaskId(int subtaskId) {
        subTaskId.add(subtaskId);
    }

    public void removeSubtaskId(int subtaskId) {
        subTaskId.remove(Integer.valueOf(subtaskId));
    }

    @Override
    public String toString() {
        return "Epic { Идентификатор: " + getTaskId()
                + " Задача: " + getTaskName()
                + " Описание: " + getTasksInfo()
                + " Статус: " + getStatus()
                + " SubTasks = " + subTaskId + "}";
    }

    @Override
    public String toStringFromFile() {
        return String.format("%s,%s,%s,%s,%s,%s", getTaskId(), getType(), getTaskName(),
                getStatus(), getTasksInfo(), subTaskId);
    }
}
