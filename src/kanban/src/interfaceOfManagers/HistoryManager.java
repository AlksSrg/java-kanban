package kanban.src.interfaceOfManagers;

import kanban.src.tasks.Task;

import java.util.List;

public interface HistoryManager {

    void add(Task task);

    List<Task> getHistory();

}
