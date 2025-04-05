import kanban.src.managers.InMemoryTaskManager;
import kanban.src.tasks.EpicTask;
import kanban.src.tasks.SubTask;
import kanban.src.tasks.Task;
import kanban.src.tools.TaskStatus;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryTaskManagerTest { //тесты класса TaskManager
    protected InMemoryTaskManager manager = new InMemoryTaskManager();

    @Test
    void saveTask_shouldCreateATask() { //проверка на создание Task и совпадение исходного таска и полученного по Id
        final Task task = new Task("Task 1", "Task 1 info", TaskStatus.NEW);
        manager.saveTask(task);
        final List<Task> savedTask = manager.getAllTasks();
        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(manager.getAllTasks(), savedTask, "Задачи не совпадают.");
        assertNotNull(savedTask, "Задачи не возвращаются.");
        assertEquals(1, savedTask.size(), "Неверное количество задач.");
        assertEquals(task, savedTask.getFirst(), "Задачи не совпадают.");
    }

    @Test
    void saveEpicTask_shouldCreateATask() { //проверка на создание EpicTask и совпадение исходного таска и полученного по Id
        final EpicTask epic = new EpicTask("Epic 1", "Epic 1 info", TaskStatus.NEW);
        manager.saveEpicTask(epic);
        final List<EpicTask> savedTask = manager.getAllEpics();
        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(manager.getAllEpics(), savedTask, "Задачи не совпадают.");
        assertNotNull(savedTask, "Задачи не возвращаются.");
        assertEquals(1, savedTask.size(), "Неверное количество задач.");
        assertEquals(epic, savedTask.getFirst(), "Задачи не совпадают.");
    }

    @Test
    void saveSubTask_shouldCreateATask() { //проверка на создание SubTask и совпадение исходного таска и полученного по Id
        final EpicTask epic = new EpicTask("Epic 1", "Epic 1 info", TaskStatus.NEW);
        manager.saveEpicTask(epic);
        final SubTask subTask = new SubTask(epic.getTaskId(), "Subtask 1", "SubTask 1 info",
                TaskStatus.NEW);
        manager.saveSubTask(subTask);
        final List<SubTask> savedTask = manager.getAllSubTasks();
        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(manager.getAllSubTasks(), savedTask, "Задачи не совпадают.");
        assertNotNull(savedTask, "Задачи не возвращаются.");
        assertEquals(1, savedTask.size(), "Неверное количество задач.");
        assertEquals(subTask, savedTask.getFirst(), "Задачи не совпадают.");
    }

    @Test
    void deleteEpicTask_shouldDeleteEpicTaskDeleteAndHisSubTask() { //Проверка на удаление Epic'а и принадлежащих ему SubTask'ов
        final EpicTask epic = new EpicTask("Epic 1", "Epic 1 info", TaskStatus.NEW);
        final SubTask subTask = new SubTask(epic.getTaskId(), "Subtask 1", "SubTask 1 info",
                TaskStatus.NEW);
        manager.saveEpicTask(epic);
        manager.saveSubTask(subTask);
        assertNotNull(manager.getAllEpics(), "Задача не найдена.");
        assertNotNull(manager.getAllSubTasks(), "Задача не найдена.");
        manager.deleteEpicTask(epic.getTaskId());
        System.out.println(manager.getAllEpics());
        assertTrue(manager.getAllEpics().isEmpty());
        assertTrue(manager.getSubTasksByEpicId(epic.getTaskId()).isEmpty());
    }

    @Test
    void updateEpicTaskStatus_shouldUpdateStatusEpicShiftsStatusSubTask() { //Проверка на смену статуса у Epic после смены статуса у его SubTask
        final EpicTask epic = new EpicTask("Epic 1", "Epic 1 info", TaskStatus.NEW);
        manager.saveEpicTask(epic);
        final SubTask subTask = new SubTask(epic.getTaskId(), "Subtask 1", "SubTask 1 info", TaskStatus.NEW);
        manager.saveSubTask(subTask);
        assertEquals(TaskStatus.NEW, epic.getStatus(), "Неверный статус.");

        subTask.setStatus(TaskStatus.IN_PROGRESS);
        manager.updateSubTask(subTask);
        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus(), "Неверный статус.");

    }

}