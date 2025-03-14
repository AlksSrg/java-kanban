package kanban.src.test;

import kanban.src.tasks.Task;
import kanban.src.tools.InMemoryHistoryManager;
import org.junit.jupiter.api.Test;

import java.util.List;

import static kanban.src.tools.TaskStatus.NEW;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


class InMemoryHistoryManagerTest {

    protected InMemoryHistoryManager historyManager = new InMemoryHistoryManager();

    private final Task task = new Task("Task 1", "Task 1 info", NEW);

    @Test
    void addHistory() { // проверка на добавление в историю просмотров
        historyManager.add(task);
        final List<Task> history = historyManager.getHistory();
        assertNotNull(history, "История пустая.");
        assertEquals(1, history.size(), "История не пустая.");
    }

    @Test
    void addHistory11() {// проверка на добавление в историю одиннадцатого просмотра
        for (int i = 0; i < 10; i++) {
            historyManager.add(task);
        }
        final List<Task> history = historyManager.getHistory();
        assertEquals(10, history.size(), "В истории не 10 значений.");
        historyManager.add(task);
        assertEquals(10, history.size(), "В истории не 10 значений.");
    }

    @Test
    void getHistory() {
        final List<Task> history = historyManager.getHistory();
        historyManager.add(task);
        assertNotNull(history, "История пустая");
    }
}