package kanban.src.managers;

import kanban.src.tasks.EpicTask;
import kanban.src.tasks.SubTask;
import kanban.src.tasks.Task;
import kanban.src.tools.TaskStatus;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class InMemoryTaskManager implements TaskManager {//реализация методов класса TaskManager
    protected Map<Integer, Task> tasks;
    protected Map<Integer, EpicTask> epicTasks;
    protected Map<Integer, SubTask> subTasks;
    protected HistoryManager historyManager;
    protected int tasksId = 1;

    public InMemoryTaskManager() {
        this.historyManager = Managers.getDefaultHistory();
        this.tasks = new HashMap<>();
        this.epicTasks = new HashMap<>();
        this.subTasks = new HashMap<>();
    }


    @Override
    public void saveEpicTask(EpicTask epic) {//сохранение задачи типа Epic
        epic.setTaskId(generateTaskId());
        epicTasks.put(epic.getTaskId(), epic);
    }

    @Override
    public void deleteEpicTask(int id) {//удаление задачи типа Epic
        EpicTask epic = epicTasks.remove(id);
        if (epic != null) {
            for (int subtaskId : epic.getSubTaskId()) {
                subTasks.remove(subtaskId);
            }
        }
    }

    @Override
    public void updateEpicTask(EpicTask epic) {//обновление задачи типа Epic
        if (epicTasks.containsKey(epic.getSubTaskId())) {
            epicTasks.put(epic.getTaskId(), epic);
            updateEpicTaskStatus(epic);
        }
    }

    @Override
    public EpicTask getEpicById(int id) {//получение задачи типа Epic по ее Id
        EpicTask epic = epicTasks.get(id);
        historyManager.add(epic);
        return epic;
    }


    @Override
    public List<EpicTask> getAllEpics() {//получение всех задач типа Epic
        return new ArrayList<>(epicTasks.values());
    }

    @Override
    public void saveSubTask(SubTask subtask) {//сохранение задачи типа SubTask
        subtask.setTaskId(generateTaskId());
        subTasks.put(subtask.getTaskId(), subtask);
        EpicTask epic = epicTasks.get(subtask.getEpicTaskId());
        if (epic != null) {
            epic.addSubTaskId(subtask.getTaskId());
            updateEpicTaskStatus(epic);
        }
    }

    @Override
    public void deleteSubTask(int id) {//удаление задачи типа Epic
        SubTask subtask = subTasks.remove(id);
        if (subtask != null) {
            EpicTask epic = epicTasks.get(subtask.getEpicTaskId());
            if (epic != null) {
                epic.removeSubtaskId(id);
                updateEpicTaskStatus(epic);
            }
        }
    }

    @Override
    public List<SubTask> getSubTasksByEpicId(int epicId) {//получение задачи типа SubTasks по Id ее Epic
        List<SubTask> result = new ArrayList<>();
        EpicTask epic = epicTasks.get(epicId);
        if (epic != null) {
            for (int subtaskId : epic.getSubTaskId()) {
                SubTask subtask = subTasks.get(subtaskId);
                if (subtask != null) {
                    result.add(subtask);
                }
            }
        }
        return result;
    }

    @Override
    public SubTask getSubTaskById(int id) {//получение задачи типа SubTasks по ее Id
        SubTask subtask = subTasks.get(id);
        historyManager.add(subtask);
        return subtask;
    }

    @Override
    public void updateSubTask(SubTask subtask) {//обновление задачи типа SubTasks
        if (subTasks.containsKey(subtask.getTaskId())) {
            subTasks.put(subtask.getTaskId(), subtask);
            EpicTask epic = epicTasks.get(subtask.getEpicTaskId());
            if (epic != null) {
                updateEpicTaskStatus(epic);
            }
        }
    }

    @Override
    public List<SubTask> getAllSubTasks() {//получение всех задач типа SubTask
        return new ArrayList<>(subTasks.values());
    }

    @Override
    public void saveTask(Task task) {// сохранение задачи типа Task
        task.setTaskId(generateTaskId());
        tasks.put(task.getTaskId(), task);
    }

    @Override
    public void deleteTask(int id) {// удаление задачи типа Task
        tasks.remove(id);
    }

    @Override
    public List<Task> getAllTasks() {//получение всех задач типа Task
        return new ArrayList<>(tasks.values());
    }

    @Override
    public void updateTask(Task task) {//обновление задачи типа Task
        tasks.put(task.getTaskId(), task);
    }

    @Override
    public Task getTaskById(int id) {//получение задачи типа Tasks по ее Id
        Task task = tasks.get(id);
        historyManager.add(task);
        return task;
    }


    @Override
    public List<Task> getHistory() {//получение истории просмотров
        return historyManager.getHistory();
    }

    private int generateTaskId() {// генерация уникально Id для всех типов задач
        return tasksId++;

    }

    private void updateEpicTaskStatus(EpicTask epic) {//обновление статуса задачи типа Epic при обновлении/изменении статуса ее задач типа SubTask
        if (epic.getSubTaskId().isEmpty()) {
            epic.setStatus(TaskStatus.NEW);
            return;
        }
        boolean allDone = true;
        boolean allNew = true;

        for (int subtaskId : epic.getSubTaskId()) {
            SubTask subtask = subTasks.get(subtaskId);
            if (subtask != null) {
                if (subtask.getStatus() != TaskStatus.DONE) {
                    allDone = false;
                }
                if (subtask.getStatus() != TaskStatus.NEW) {
                    allNew = false;
                }
            }
        }
        if (allDone) {
            epic.setStatus(TaskStatus.DONE);
        } else if (allNew) {
            epic.setStatus(TaskStatus.NEW);
        } else {
            epic.setStatus(TaskStatus.IN_PROGRESS);
        }
    }
}
