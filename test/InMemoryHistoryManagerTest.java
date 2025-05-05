import managers.InMemoryHistoryManager;
import org.junit.jupiter.api.Test;
import tasks.Task;
import tools.TaskStatus;
import tools.Type;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    protected InMemoryHistoryManager historyManager = new InMemoryHistoryManager();

    //Проверка метода add(): добавляется новая задача в пустую историю и проверка работы метода getHistory.
    @Test
    public void testAddToEmptyHistory() {
        Task task = new Task(1, "Тестовая задача", "", TaskStatus.NEW, Type.TASK, Duration.ZERO, LocalDateTime.now());

        historyManager.add(task);

        assertEquals(1, historyManager.getHistory().size()); // История должна содержать одну задачу
        assertTrue(historyManager.getHistory().contains(task)); // Задача должна присутствовать в истории
    }


    // Проверка дублирования задач при добавлении методом add().
    @Test
    public void testAddDuplicateTask() {
        Task task = new Task(1, "Дублирующаяся задача", "", TaskStatus.NEW, Type.TASK, Duration.ZERO, LocalDateTime.now());
        historyManager.add(task);

        // Повторное добавление той же задачи
        historyManager.add(task);

        assertEquals(1, historyManager.getHistory().size()); // Должна остаться одна задача
    }

    //Проверка поведения метода getHistory() на пустой истории.
    @Test
    public void testGetHistoryOnEmptyHistory() {
        // Изначально чистая история
        assertTrue(historyManager.getHistory().isEmpty()); // Истории пустая
    }

    //Проверка упорядоченности истории по идентификатору задачи.
    @Test
    public void testOrderedHistory() {
        // Создание трёх задач с разными ID
        Task task1 = new Task(3, "Задача №3", "", TaskStatus.NEW, Type.TASK, Duration.ZERO, LocalDateTime.now());
        Task task2 = new Task(1, "Задача №1", "", TaskStatus.NEW, Type.TASK, Duration.ZERO, LocalDateTime.now());
        Task task3 = new Task(2, "Задача №2", "", TaskStatus.NEW, Type.TASK, Duration.ZERO, LocalDateTime.now());

        // Добавляем задачи в произвольном порядке
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);

        // Проверяем порядок сортировки
        List<Task> orderedHistory = historyManager.getHistory();
        assertEquals(orderedHistory.get(0).getTaskId(), 1); // Первая задача — с наименьшим ID
        assertEquals(orderedHistory.get(1).getTaskId(), 2); // Вторая задача — следующая по порядку
        assertEquals(orderedHistory.get(2).getTaskId(), 3); // Третья задача — самая большая по ID
    }

    @Test
    public void deletingAnTaskByIdentifier() {

        Task task1 = new Task(1, "Задача №1", "", TaskStatus.NEW, Type.TASK, Duration.ZERO, LocalDateTime.now());
        Task task2 = new Task(2, "Задача №2", "", TaskStatus.NEW, Type.TASK, Duration.ZERO, LocalDateTime.now());
        Task task3 = new Task(3, "Задача №3", "", TaskStatus.NEW, Type.TASK, Duration.ZERO, LocalDateTime.now());

        // Добавляем их в историю
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);

        // Удаляем Задачу с идентификатором 2
        historyManager.remove(2);

        // Проверяем
        assertFalse(historyManager.getHistory().contains(task2)); // Вторая задача удалена

    }

    //============================== Проверка граничных значений ==============================

    //Проверка удаления одной задачи из середины истории.
    @Test
    public void testRemoveFromMiddleOfHistory() {

        Task task1 = new Task(1, "Задача №1", "", TaskStatus.NEW, Type.TASK, Duration.ZERO, LocalDateTime.now());
        Task task2 = new Task(2, "Задача №2", "", TaskStatus.NEW, Type.TASK, Duration.ZERO, LocalDateTime.now());
        Task task3 = new Task(3, "Задача №3", "", TaskStatus.NEW, Type.TASK, Duration.ZERO, LocalDateTime.now());

        // Добавляем их в историю
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);

        // Удаляем вторую задачу
        historyManager.remove(task2.getTaskId());

        // Проверяем
        assertFalse(historyManager.getHistory().contains(task2)); // Вторая задача удалена
        assertEquals(2, historyManager.getHistory().size()); // Всего две оставшиеся задачи
    }

    //Проверка удаления первой задачи из истории.
    @Test
    public void testRemoveFirstTask() {
        // Готовим список задач
        Task task1 = new Task(1, "Первая задача", "", TaskStatus.NEW, Type.TASK, Duration.ZERO, LocalDateTime.now());
        Task task2 = new Task(2, "Вторая задача", "", TaskStatus.NEW, Type.TASK, Duration.ZERO, LocalDateTime.now());
        historyManager.add(task1);
        historyManager.add(task2);

        // Удаляем первую задачу
        historyManager.remove(task1.getTaskId());

        // Проверяем, что первая задача исчезла
        assertFalse(historyManager.getHistory().contains(task1));
        assertEquals(1, historyManager.getHistory().size()); // Осталась всего одна задача
    }

    //Проверка удаления последней задачи из истории.
    @Test
    public void testRemoveLastTask() {
        // Подготовим задачи
        Task task1 = new Task(1, "Первая задача", "", TaskStatus.NEW, Type.TASK, Duration.ZERO, LocalDateTime.now());
        Task task2 = new Task(2, "Последняя задача", "", TaskStatus.NEW, Type.TASK, Duration.ZERO, LocalDateTime.now());

        historyManager.add(task1);
        historyManager.add(task2);

        // Удаляем последнюю задачу
        historyManager.remove(task2.getTaskId());

        // Проверяем, что последняя задача удалена
        assertFalse(historyManager.getHistory().contains(task2));
        assertEquals(1, historyManager.getHistory().size()); // Одна оставшаяся задача
    }
}