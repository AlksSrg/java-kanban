package kanban.src.tools;

import kanban.src.tasks.Task;
import kanban.src.interfaceOfManagers.HistoryManager;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {
    private final List<Task> history = new ArrayList<>();


    @Override
    public void add(Task task) {
        if (task != null) {
            if (history.size() > 9) {
                history.removeFirst();
            }
            history.add(task);
        }
    }

    @Override
    public List<Task> getHistory() {
        return history;
    }
}