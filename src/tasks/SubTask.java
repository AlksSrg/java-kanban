package tasks;

import tools.TaskStatus;
import tools.Type;

import java.time.Duration;
import java.time.LocalDateTime;

public class SubTask extends Task {

    private int epicTaskId;
    private Type type;


    public SubTask(int taskId, String taskName, String tasksInfo, TaskStatus status, Type type, Duration duration, LocalDateTime startTime, int epicTaskId) {
        super(taskId, taskName, tasksInfo, status, type, duration, startTime);
        this.type = type;
        this.epicTaskId = epicTaskId;
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
        String breakLine = "\n";
        return "Тип задачи - Subtask" + breakLine
                + "Идентификатор: " + getTaskId() + breakLine
                + "Задача: " + getTaskName() + breakLine
                + "Описание: " + getTasksInfo() + breakLine
                + "Статус: " + getStatus() + breakLine
                + "Старт задачи: " + getStartTime() + breakLine
                + "Продолжительность задачи:" + getDuration().toMinutes() + breakLine
                + "Является подзадачей для задачи с идентификатором: " + getEpicTaskId() + breakLine;
    }

    @Override
    public String toStringFromFile() {
        return String.format("%d,%s,%s,%s,%s,%s,%s,%d",
                getTaskId(),                              // Идентификатор
                Type.SUBTASK.name(),                      // Тип задачи
                getStatus().name(),                       // Статус задачи
                getTaskName(),                            // Название задачи
                getTasksInfo(),                           // Описание задачи
                getStartTime().format(dateTimeFormatter), // Время старта
                getDuration().toString(),                 // Продолжительность
                epicTaskId);                              // Идентификатор эпика
    }
}
