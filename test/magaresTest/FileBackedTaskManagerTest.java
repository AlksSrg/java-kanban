package magaresTest;

import managers.FileBackedTaskManager;
import managers.InMemoryTaskManager;
import org.junit.jupiter.api.Test;
import tasks.EpicTask;
import tasks.SubTask;
import tasks.Task;
import tools.TaskStatus;
import tools.Type;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class FileBackedTaskManagerTest {

    File tempFile; // Файл для работы менеджера FileBackedTaskManager

    {
        try {
            tempFile = File.createTempFile("Test_file", ".csv");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    FileBackedTaskManager fileManager = new FileBackedTaskManager(tempFile);
    InMemoryTaskManager manager = new InMemoryTaskManager(); //создание экземпляра InMemoryTaskManager для проверок корректного сохранения Тасок

    //Проверка на работу метода save() при вызове метода saveEpicTask() в классе FileBackedTaskManager
    @Test
    void saveEpicTask() {
        final EpicTask epic = new EpicTask(1, "Epic 1", "Epic 1 info", TaskStatus.NEW,
                Type.EPIC, Duration.ZERO, LocalDateTime.now(), LocalDateTime.now().plusHours(1), List.of());
        fileManager.saveEpicTask(epic);
        assertNotNull(tempFile, "Файл не создан.");
        manager.saveEpicTask(epic);
        assertEquals(manager.getAllEpics(), fileManager.getAllEpics(), "Задачи не совпадают"); //Проверка на совпадение сохраняемых задач в обоих менеджерах

    }

    //Проверка на работу метода save() при вызове метода saveSubTask() в классе FileBackedTaskManager
    @Test
    void saveSubTask() {
        final EpicTask epic = new EpicTask(1, "Epic 1", "Epic 1 info", TaskStatus.NEW,
                Type.EPIC, Duration.ZERO, LocalDateTime.now(), LocalDateTime.now().plusHours(1), List.of(2));
        fileManager.saveEpicTask(epic);
        final SubTask subTask = new SubTask(2, "Subtask 1", "SubTask 1 info", TaskStatus.NEW,
                Type.SUBTASK, Duration.ZERO, LocalDateTime.now(), 1);
        fileManager.saveSubTask(subTask);
        assertNotNull(tempFile, "Файл не создан.");
        manager.saveEpicTask(epic);
        manager.saveSubTask(subTask);
        assertEquals(manager.getAllEpics(), fileManager.getAllEpics(), "Задачи не совпадают"); //Проверка на совпадение сохраняемых задач в обоих менеджерах
        assertEquals(manager.getAllSubTasks(), fileManager.getAllSubTasks(), "Задачи не совпадают"); //Проверка на совпадение сохраняемых задач в обоих менеджерах
    }

    //Проверка на работу метода save() при вызове метода saveTask() в классе FileBackedTaskManager
    @Test
    void saveTask() {
        final Task task = new Task(1, "Task 1", "Task 1 info", TaskStatus.NEW, Type.TASK,
                Duration.ZERO, LocalDateTime.now());
        fileManager.saveTask(task);
        assertNotNull(tempFile, "Файл не создан.");
        manager.saveTask(task);
        assertEquals(manager.getAllTasks(), fileManager.getAllTasks(), "Задачи не совпадают"); //Проверка на совпадение сохраняемых задач в обоих менеджерах
    }

    //Проверка работы метода save
    @Test
    void save() {
        final EpicTask epic = new EpicTask(1, "Epic 1", "Epic 1 info", TaskStatus.NEW,
                Type.EPIC, Duration.ZERO, LocalDateTime.now(), LocalDateTime.now().plusHours(1), List.of(2));
        final SubTask subTask = new SubTask(2, "Subtask 1", "SubTask 1 info", TaskStatus.NEW,
                Type.SUBTASK, Duration.ZERO, LocalDateTime.now(), epic.getTaskId());
        fileManager.saveSubTask(subTask);
        final Task task = new Task(1, "Task 1", "Task 1 info", TaskStatus.NEW, Type.TASK,
                Duration.ZERO, LocalDateTime.now());
        fileManager.saveTask(task);
        assertNotNull(tempFile, "Файл не создан.");
    }

    //Проверка на сериализацию/десериализацию данных
    @Test
    void loadFromFile() throws IOException {
        final Task task = new Task(1, "Task 1", "Task 1 info", TaskStatus.NEW, Type.TASK,
                Duration.ZERO, LocalDateTime.now());
        final EpicTask epic = new EpicTask(2, "Epic 1", "Epic 1 info", TaskStatus.NEW,
                Type.EPIC, Duration.ZERO, LocalDateTime.now(), LocalDateTime.now().plusHours(1), List.of());
        fileManager.saveTask(task);
        fileManager.saveEpicTask(epic);
        fileManager.loadFromFile(tempFile);
        List<Task> listOfTasks = fileManager.getAllTasks();
        List<EpicTask> listOfEpics = fileManager.getAllEpics();
        assertEquals(List.of(task), listOfTasks);
        assertEquals(List.of(epic), listOfEpics);
    }
}
