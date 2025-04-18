import tasks.Task;
import org.junit.jupiter.api.Test;

import static tools.TaskStatus.DONE;
import static tools.TaskStatus.NEW;
import static org.junit.jupiter.api.Assertions.*;

class TaskTest {

    final Task task = new Task("Task 1", "Task 1 info", NEW);


    @Test
    void setTaskName() { // проверка на изменение имени в Task
        Task testTask = task;
        task.setTaskName("New Task1");
        assertEquals(testTask, task, "Задачи совпадают.");
    }

    @Test
    void setTasksInfo() { // проверка на изменение описания в Task
        Task testTask = task;
        task.setTasksInfo("New Task1 info");
        assertEquals(testTask, task, "Задачи совпадают.");
    }


    @Test
    void setStatus() { // проверка на изменение статуса в Task
        Task testTask = task;
        task.setStatus(DONE);
        assertEquals(testTask, task, "Задачи совпадают.");

    }
}