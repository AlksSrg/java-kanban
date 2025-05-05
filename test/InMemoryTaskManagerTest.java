import managers.InMemoryTaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.EpicTask;
import tasks.SubTask;
import tasks.Task;
import tools.TaskStatus;
import tools.TaskTimeException;
import tools.Type;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


// Реализуем тесты для InMemoryTaskManager
public class InMemoryTaskManagerTest extends ManagerTest<InMemoryTaskManager> {

    @Override
    @BeforeEach
    public void createManagerForTest() throws IOException {
        taskManager = new InMemoryTaskManager();
        taskManager.clearData();
        createTestTasks();
    }

    // Проверяем создание задач разных типов
    @Test
    void testCreateTasks() {
        assertNotNull(taskManager.getTaskById(testTask.getTaskId()), "Ошибка при создании простой задачи");
        assertNotNull(taskManager.getEpicById(testEpicTask.getTaskId()), "Ошибка при создании эпика");
        assertNotNull(taskManager.getSubTaskById(testSubTask1.getTaskId()), "Ошибка при создании подзадачи");
        assertNotNull(taskManager.getSubTaskById(testSubTask2.getTaskId()), "Ошибка при создании подзадачи");
    }

    //Тестируем удаление задач.
    @Test
    void testRemoveTasks() {

        // Удаляем каждую задачу
        taskManager.deleteTask(testTask.getTaskId());
        taskManager.deleteEpicTask(testEpicTask.getTaskId());
        taskManager.deleteSubTask(testSubTask1.getTaskId());
        taskManager.deleteSubTask(testSubTask2.getTaskId());

        // Проверяем, что удалённых задач больше нет
        assertNull(taskManager.getTaskById(testTask.getTaskId()), "Простая задача не была удалена");
        assertNull(taskManager.getEpicById(testEpicTask.getTaskId()), "Эпик не был удалён");
        assertNull(taskManager.getSubTaskById(testSubTask1.getTaskId()), "Подзадача не была удалена");
        assertNull(taskManager.getSubTaskById(testSubTask2.getTaskId()), "Подзадача не была удалена");
    }

    //Тестируем обновление задач.
    @Test
    void testUpdateTasks() {

        // Меняем название
        testTask.setTaskName("Новая простая задача");
        testEpicTask.setTaskName("Новый эпик");
        testSubTask1.setTaskName("Новое имя подзадачи");

        // Обновляем задачи
        taskManager.updateTask(testTask);
        taskManager.updateEpicTask(testEpicTask);
        taskManager.updateSubTask(testSubTask1);

        // Проверяем изменения
        assertEquals("Новая простая задача", taskManager.getTaskById(testTask.getTaskId()).getTaskName(),
                "Название простой задачи не было обновлено");
        assertEquals("Новый эпик", taskManager.getEpicById(testEpicTask.getTaskId()).getTaskName(),
                "Название эпика не было обновлено");
        assertEquals("Новое имя подзадачи", taskManager.getSubTaskById(testSubTask1.getTaskId()).getTaskName(),
                "Название подзадачи не было обновлено");
    }

    //Тестируем получение всех Tasks.
    @Test
    void testGetAllTasks() {
        List<Task> allTasks = taskManager.getAllTasks();
        assertFalse(allTasks.isEmpty(), "Список всех задач должен быть непустым");
    }

    //Тестируем получение всех Epics.
    @Test
    void testGetAllEpics() {
        List<EpicTask> allEpics = taskManager.getAllEpics();
        assertFalse(allEpics.isEmpty(), "Список всех эпиков должен быть непустым");
    }

    //Тестируем получение всех SubTasks.
    @Test
    void testGetAllSubTasks() {
        List<SubTask> allSubTasks = taskManager.getAllSubTasks();
        assertFalse(allSubTasks.isEmpty(), "Список всех подзадач должен быть непустым");
    }

    //Тестируем получение задач определенного типа по идентификатору.
    @Test
    void testGetSpecificTaskTypes() {
        assertNotNull(taskManager.getTaskById(testTask.getTaskId()));
        assertNotNull(taskManager.getEpicById(testEpicTask.getTaskId()));
        assertNotNull(taskManager.getSubTaskById(testSubTask1.getTaskId()));
        assertNotNull(taskManager.getSubTaskById(testSubTask2.getTaskId()));
    }

    //Тестируем получение SubTask по Epic ID
    @Test
    void testGetSubTasksByEpicId() {

        // Получаем подзадачи для нашего эпика
        List<SubTask> subTasksForEpic = taskManager.getSubTasksByEpicId(testEpicTask.getTaskId());

        // Проверяем, что подзадача вернулась
        assertFalse(subTasksForEpic.isEmpty(), "Должна вернуться одна подзадача");
        assertEquals(2, subTasksForEpic.size(), "Количество подзадач должно быть равно одному");
        assertEquals(testSubTask1, subTasksForEpic.getFirst(), "Возвращаемая подзадача должна соответствовать сохранённой");
    }

    //Тестируем очистку хранилища задач.
    @Test
    void testClearData() {
        taskManager.clearData();

        // Проверяем, что хранилище пустое
        assertTrue(taskManager.getAllTasks().isEmpty());
        assertTrue(taskManager.getAllEpics().isEmpty());
        assertTrue(taskManager.getAllSubTasks().isEmpty());
    }

    //Проверка на добавление пересекающихся по времени задач
    @Test
    void testAddTaskToSortedList() throws Exception {

        // Штатные задачи из TaskManagerTest добавляются, кроме Epic
        assertEquals(3, taskManager.getPrioritizedTasks().size(), "Три задачи должны быть добавлены");

        // Попытка добавить пересекающуюся задачу должна привести к ошибке
        // Задача частично пересекается с первой задачей
        LocalDateTime overlappingTaskTimeStart = LocalDateTime.of(2025, 5, 4, 10, 30); // Начинается внутри первой задачи
        Duration overlappingTaskDuration = Duration.ofHours(1); // Продолжительность задачи

        Task overlappingTask = new Task(
                5, "Пересекающаяся задача", "Описание задачи",
                TaskStatus.NEW, Type.TASK,
                overlappingTaskDuration, overlappingTaskTimeStart
        );
        try {
            taskManager.addTaskToSortedList(overlappingTask);
            fail("Исключение не произошло при пересечении задач");
        } catch (TaskTimeException e) {
            assertEquals("Пересечение задач по времени", e.getMessage(), "Сообщение исключения верное");
        }

        // Добавление задачи без пересечения
        LocalDateTime nonOverlappingTaskTimeStart = LocalDateTime.of(2025, 5, 4, 12, 0); // Начинается после первой задачи
        Duration nonOverlappingTaskDuration = Duration.ofHours(1); // Продолжительность задачи

        Task nonOverlappingTask = new Task(
                6, "Не пересекающаяся задача", "Описание задачи",
                TaskStatus.NEW, Type.TASK,
                nonOverlappingTaskDuration, nonOverlappingTaskTimeStart
        );
        taskManager.addTaskToSortedList(nonOverlappingTask);

        // Проверяем, что задача добавилась нормально
        assertEquals(4, taskManager.getPrioritizedTasks().size(), "Всего должно быть 4 задачи");
    }

    //Тестируем получение приоритизированного списка задач.
    @Test
    void testGetPrioritizedTasks() {
        List<Task> prioritizedTasks = taskManager.getPrioritizedTasks();
        assertFalse(prioritizedTasks.isEmpty(), "Список приоритетных задач должен быть непустым");
    }

    //Тестируем логику автоматического обновления статуса эпика.
    @Test
    void testAutoUpdatingEpicStatus() {
        //меняем статус у SubTask
        testSubTask1.setStatus(TaskStatus.DONE);
        testSubTask2.setStatus(TaskStatus.DONE);
        taskManager.updateSubTask(testSubTask1);
        taskManager.updateSubTask(testSubTask2);

        //Статус эпика должен стать IN_PROGRESS
        assertEquals(TaskStatus.DONE, taskManager.getEpicById(testEpicTask.getTaskId()).getStatus(),
                "Статус эпика должен быть DONE при выполнении подзадачи");
    }

    //Тестируем историю просмотров задач.
    @Test
    void testHistoryManager() {
        // Просматриваем задачи
        taskManager.getTaskById(testTask.getTaskId());
        taskManager.getEpicById(testEpicTask.getTaskId());
        taskManager.getSubTaskById(testSubTask1.getTaskId());

        // Проверяем, что история записана верно
        List<Task> history = taskManager.getHistory();
        assertFalse(history.isEmpty(), "История просмотров должна быть непустой");
    }

    //Расчёт статуса Epic при граничных условия:
    @Test
    void testEpicStatusUpdateScenarios() {

        // Случай: Все подзадачи NEW - создаются из TaskManagerTest со статусом NEW
        assertEquals(TaskStatus.NEW, testEpicTask.getStatus(), "Эпик должен быть NEW, если все подзадачи NEW");

        // Случай: Все подзадачи DONE
        testSubTask1.setStatus(TaskStatus.DONE);
        testSubTask2.setStatus(TaskStatus.DONE);
        taskManager.updateSubTask(testSubTask1);
        taskManager.updateSubTask(testSubTask2);

        assertEquals(TaskStatus.DONE, testEpicTask.getStatus(), "Эпик должен быть DONE, если все подзадачи DONE");

        // Случай: Смесь NEW и DONE -
        testSubTask1.setStatus(TaskStatus.DONE);
        testSubTask2.setStatus(TaskStatus.NEW);
        taskManager.updateSubTask(testSubTask1);
        taskManager.updateSubTask(testSubTask2);

        assertEquals(TaskStatus.IN_PROGRESS, testEpicTask.getStatus(), "Эпик должен быть IN_PROGRESS, если есть подзадачи NEW и DONE");

        // Случай: Одна из подзадач IN_PROGRESS
        testSubTask1.setStatus(TaskStatus.IN_PROGRESS);
        testSubTask2.setStatus(TaskStatus.NEW);
        taskManager.updateSubTask(testSubTask1);
        taskManager.updateSubTask(testSubTask2);

        assertEquals(TaskStatus.IN_PROGRESS, testEpicTask.getStatus(), "Эпос должен быть IN_PROGRESS, если есть подзадача IN_PROGRESS");
    }

    //Завершаем тестирование, очищая среду.
    @AfterEach
    void teardown() {
        taskManager.clearData();
    }
}