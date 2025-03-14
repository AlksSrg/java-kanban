package kanban.src.tasks;

import kanban.src.tools.TaskStatus;

public class SubTask extends Task {

    private int epicTaskId ;

    public SubTask(int epicTaskId, String taskName, String tasksInfo, TaskStatus status) {
        super(taskName, tasksInfo, status);
        this.epicTaskId = epicTaskId;
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
}
