package kanban.src.test;

import kanban.src.managers.InMemoryTaskManager;
import kanban.src.tasks.Task;
import kanban.src.managers.InMemoryHistoryManager;
import org.junit.jupiter.api.Test;

import java.util.List;

import static kanban.src.tools.TaskStatus.NEW;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


class InMemoryHistoryManagerTest {//тесты класса HistoryManager

    protected InMemoryHistoryManager historyManager = new InMemoryHistoryManager();
    protected InMemoryTaskManager manager = new InMemoryTaskManager();

    private final Task task1 = new Task("Task 1", "Task 1 info", NEW);
    private final Task task2 = new Task("Task 2", "Task 2 info", NEW);
    private final Task task3 = new Task("Task 3", "Task 3 info", NEW);

    @Test
    void addHistory() { // проверка на добавление в историю просмотров
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
        final List<Task> history = historyManager.getHistory();
        assertNotNull(history, "История пустая.");
        assertEquals(1, history.size(), "История не пустая.");
    }

    @Test
    void getHistory() {// Проверка на получение истории просмотров
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
        final List<Task> history = historyManager.getHistory();
        assertNotNull(history, "История пустая");
    }

    @Test
    void remove() {//Удаление истории по ID задачи
        manager.saveTask(task1);
        manager.saveTask(task2);
        manager.saveTask(task3);
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
        historyManager.remove(task1.getTaskId());
        final List<Task> history = historyManager.getHistory();
        assertEquals(3, history.size(), "Просмотр удален");

    }

}