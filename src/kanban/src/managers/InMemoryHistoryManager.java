package kanban.src.managers;

import kanban.src.tasks.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {//реализация методов HistoryManager
    private final List<Task> history = new ArrayList<>();


    @Override
    public void add(Task task) {//добавление в историю просмотров
        if (task != null) {
            if (history.size() > 9) {
                history.removeFirst();
            }
            history.add(task);
        }
    }

    @Override
    public List<Task> getHistory() {//Просмотр содержимого истории просмотров
        return history;
    }
}