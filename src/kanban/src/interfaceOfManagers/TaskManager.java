package kanban.src.interfaceOfManagers;

import kanban.src.tasks.EpicTask;
import kanban.src.tasks.SubTask;
import kanban.src.tasks.Task;

import java.util.ArrayList;
import java.util.List;

public interface TaskManager {
    void saveEpicTask(EpicTask epic);

    void deleteEpicTask(int id);

    void updateEpicTask(EpicTask epic);

    EpicTask getEpicById(int id);

    ArrayList<EpicTask> getAllEpics();

    void saveSubTask(SubTask subtask);

    void deleteSubTask(int id);

    ArrayList<SubTask> getSubTasksByEpicId(int epicId);

    SubTask getSubTaskById(int id);

    void updateSubTask(SubTask subtask);

    ArrayList<SubTask> getAllSubTasks();

    void saveTask(Task task);

    void deleteTask(int id);

    ArrayList<Task> getAllTasks();

    void updateTask(Task task);

    Task getTaskById(int id);

    int generateTaskId();

    void updateEpicTaskStatus(EpicTask epic);

    List<Task> getHistory();

}
