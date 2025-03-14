package kanban.src.tools;

import kanban.src.interfaceOfManagers.HistoryManager;
import kanban.src.interfaceOfManagers.TaskManager;

public class Managers {

    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

}
