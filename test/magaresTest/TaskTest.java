package magaresTest;

import org.junit.jupiter.api.Test;
import tasks.Task;
import tools.TaskStatus;
import tools.Type;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static tools.TaskStatus.DONE;

class TaskTest {
    LocalDateTime fixedNow = LocalDateTime.of(2025, 5, 4, 12, 0);
    final Task task = new Task(1, "Основная задача", "Детали первой задачи", TaskStatus.NEW, Type.TASK,
            Duration.ofHours(1), fixedNow);

    // проверка на изменение имени в Task
    @Test
    void setTaskName() {
        Task testTask = task;
        task.setTaskName("New Task1");
        assertEquals(testTask, task, "Задачи совпадают.");
    }

    // проверка на изменение описания в Task
    @Test
    void setTasksInfo() {
        Task testTask = task;
        task.setTasksInfo("New Task1 info");
        assertEquals(testTask, task, "Задачи совпадают.");
    }

    // проверка на изменение статуса в Task
    @Test
    void setStatus() {
        Task testTask = task;
        task.setStatus(DONE);
        assertEquals(testTask, task, "Задачи совпадают.");
    }
}