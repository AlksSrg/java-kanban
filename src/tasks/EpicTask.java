package tasks;

import tools.TaskStatus;
import tools.Type;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class EpicTask extends Task {

    private List<Integer> subTaskId = new ArrayList<>();
    private LocalDateTime endTime;

    public EpicTask(int taskId, String taskName, String tasksInfo, TaskStatus status, Type type,
                    Duration duration, LocalDateTime startTime, LocalDateTime endTime, List<Integer> subTaskId) {
        super(taskId, taskName, tasksInfo, status, type, duration, startTime);
        this.endTime = endTime;
        this.subTaskId = new ArrayList<>(subTaskId);
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

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public String toString() {
        return "Тип задачи - Epic\n"
                + "Идентификатор: " + getTaskId() + "\n"
                + "Название: " + getTaskName() + "\n"
                + "Описание: " + getTasksInfo() + "\n"
                + "Статус: " + getStatus() + "\n"
                + "Включает в себя подзадачи со следующими идентификаторами: " + subTaskId + "\n"
                + "Начало задачи: " + getStartTime() + "\n"
                + "Конец задачи: " + endTime + "\n"
                + "Длительность задачи: " + getDuration().toMinutes() + " минут";
    }

    @Override
    public String toStringFromFile() {
        StringBuilder sb = new StringBuilder();

        // Стандартные поля задачи
        sb.append(getTaskId()).append(",")                           // Идентификатор
                .append(Type.EPIC.name()).append(",")                 // Тип задачи
                .append(getStatus().name()).append(",")               // Статус задачи
                .append(getTaskName()).append(",")                    // Название задачи
                .append(getTasksInfo()).append(",")                   // Описание задачи
                .append(getStartTime().format(dateTimeFormatter)).append(",") // Время старта
                .append(getDuration().toString()).append(",");         // Продолжительность

        // Специальные поля для эпика
        if (endTime != null) {
            sb.append(endTime.format(dateTimeFormatter)).append(",");
        } else {
            sb.append(","); // Пустой  endTime
        }

        // Сабтаски (разделяем идентификаторы двоеточием ":")
        if (!subTaskId.isEmpty()) {
            sb.append(String.join(":", subTaskId.stream().map(Object::toString).toArray(String[]::new)));
        }
        return sb.toString();
    }

}
