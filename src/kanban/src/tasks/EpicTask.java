package kanban.src.tasks;

import kanban.src.tools.TaskStatus;

import java.util.ArrayList;

public class EpicTask extends Task {

    private ArrayList<Integer> subTaskId;

    public EpicTask(String taskName, String tasksInfo, TaskStatus status) {
        super(taskName, tasksInfo, status);
        this.subTaskId = new ArrayList<>();
    }

    public ArrayList<Integer> getSubTaskId() {
        return subTaskId;
    }

    public ArrayList<Integer> setSubTaskId(ArrayList<Integer> subTaskId) {
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
}
