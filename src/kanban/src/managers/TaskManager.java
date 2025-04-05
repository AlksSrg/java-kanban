package kanban.src.managers;

import kanban.src.tasks.EpicTask;
import kanban.src.tasks.SubTask;
import kanban.src.tasks.Task;

import java.util.List;

public interface TaskManager {//общий вид методов класса TaskManager

    void saveEpicTask(EpicTask epic);//сохранение задачи типа Epic

    void deleteEpicTask(int id);//удаление задачи типа Epic

    void updateEpicTask(EpicTask epic);//обновление задачи типа Epic

    EpicTask getEpicById(int id);//получение задачи типа Epic по ее Id

    List<EpicTask> getAllEpics();//получение всех задач типа Epic

    void saveSubTask(SubTask subtask);//сохранение задачи типа SubTask

    void deleteSubTask(int id);//удаление задачи типа Epic

    List<SubTask> getSubTasksByEpicId(int epicId);//получение задачи типа SubTasks по Id ее Epic

    SubTask getSubTaskById(int id);//получение задачи типа SubTasks по ее Id

    void updateSubTask(SubTask subtask);//обновление задачи типа SubTasks

    List<SubTask> getAllSubTasks();//получение всех задач типа SubTask

    void saveTask(Task task);// сохранение задачи типа Task

    void deleteTask(int id);// удаление задачи типа Task

    List<Task> getAllTasks();//получение всех задач типа Task

    void updateTask(Task task);//обновление задачи типа Task

    Task getTaskById(int id);//получение задачи типа Tasks по ее Id

    List<Task> getHistory();//получение истории просмотров

}
