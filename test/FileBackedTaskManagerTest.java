import managers.FileBackedTaskManager;
import managers.InMemoryTaskManager;
import org.junit.jupiter.api.Test;
import tasks.EpicTask;
import tasks.SubTask;
import tasks.Task;
import tools.TaskStatus;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


import static org.junit.jupiter.api.Assertions.*;

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

    @Test
    void saveEpicTask() {
        final EpicTask epic = new EpicTask("Epic 1", "Epic 1 info", TaskStatus.NEW);
        fileManager.saveEpicTask(epic);
        assertNotNull(tempFile, "Файл не создан."); //Проверка на работу метода save() при вызове метода saveEpicTask() в классе FileBackedTaskManager
        manager.saveEpicTask(epic);
        assertEquals(manager.getAllEpics(), fileManager.getAllEpics(), "Задачи не совпадают"); //Проверка на совпадение сохраняемых задач в обоих менеджерах

    }

    @Test
    void saveSubTask() {
        final EpicTask epic = new EpicTask("Epic 1", "Epic 1 info", TaskStatus.NEW);
        fileManager.saveEpicTask(epic);
        final SubTask subTask = new SubTask(epic.getTaskId(), "Subtask 1", "SubTask 1 info",
                TaskStatus.NEW);
        fileManager.saveSubTask(subTask);
        assertNotNull(tempFile, "Файл не создан."); //Проверка на работу метода save() при вызове метода saveSubTask() в классе FileBackedTaskManager
        manager.saveEpicTask(epic);
        manager.saveSubTask(subTask);
        assertEquals(manager.getAllEpics(), fileManager.getAllEpics(), "Задачи не совпадают"); //Проверка на совпадение сохраняемых задач в обоих менеджерах
        assertEquals(manager.getAllSubTasks(), fileManager.getAllSubTasks(), "Задачи не совпадают"); //Проверка на совпадение сохраняемых задач в обоих менеджерах
    }

    @Test
    void saveTask() {
        final Task task = new Task("Task 1", "Task 1 info", TaskStatus.NEW);
        fileManager.saveTask(task);
        assertNotNull(tempFile, "Файл не создан."); //Проверка на работу метода save() при вызове метода saveTask() в классе FileBackedTaskManager
        manager.saveTask(task);
        assertEquals(manager.getAllTasks(), fileManager.getAllTasks(), "Задачи не совпадают"); //Проверка на совпадение сохраняемых задач в обоих менеджерах
    }

    @Test
    void save() {
        final EpicTask epic = new EpicTask("Epic 1", "Epic 1 info", TaskStatus.NEW);
        fileManager.saveEpicTask(epic);
        final SubTask subTask = new SubTask(epic.getTaskId(), "Subtask 1", "SubTask 1 info",
                TaskStatus.NEW);
        fileManager.saveSubTask(subTask);
        final Task task = new Task("Task 1", "Task 1 info", TaskStatus.NEW);
        fileManager.saveTask(task);
        assertNotNull(tempFile, "Файл не создан.");
    }

    @Test
    void loadFromFile() throws IOException { //Проверка на сериализацию/десериализацию данных
        final Task task = new Task("Task 1", "Task 1 info", TaskStatus.NEW);
        final EpicTask epic = new EpicTask("Epic 1", "Epic 1 info", TaskStatus.NEW);
        fileManager.saveTask(task);
        fileManager.saveEpicTask(epic);
        fileManager.loadFromFile(tempFile);
        HashMap<Integer, Task> mapOfTasks = fileManager.getTasks();
        List<Task> listOfTasks = new ArrayList<>(mapOfTasks.values());
        HashMap<Integer, EpicTask> mapOfEpics = fileManager.getEpicTask();
        List<EpicTask> listOfEpics = new ArrayList<>(mapOfEpics.values());
        assertEquals(List.of(task), listOfTasks);
        assertEquals(List.of(epic), listOfEpics);
    }
}
