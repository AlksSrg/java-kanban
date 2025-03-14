package kanban.src.tools;

import kanban.src.tasks.EpicTask;
import kanban.src.tasks.SubTask;
import kanban.src.tasks.Task;
import kanban.src.interfaceOfManagers.HistoryManager;
import kanban.src.interfaceOfManagers.TaskManager;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;


public class InMemoryTaskManager implements TaskManager {
    protected HashMap<Integer, Task> tasks = new HashMap<>();
    protected HashMap<Integer, EpicTask> epicTasks = new HashMap<>();
    protected HashMap<Integer, SubTask> subTasks = new HashMap<>();
    protected HistoryManager historyManager;
    protected int tasksId = 1;

    public InMemoryTaskManager() {
        this.historyManager = Managers.getDefaultHistory();
        this.tasks = new HashMap<>();
        this.epicTasks = new HashMap<>();
        this.subTasks = new HashMap<>();
    }


    @Override
    public void saveEpicTask(EpicTask epic) {
        epic.setTaskId(generateTaskId());
        epicTasks.put(epic.getTaskId(), epic);
    }

    @Override
    public void deleteEpicTask(int id) {
        EpicTask epic = epicTasks.remove(id);
        if (epic != null) {
            for (int subtaskId : epic.getSubTaskId()) {
                subTasks.remove(subtaskId);
            }
        }
    }

    @Override
    public void updateEpicTask(EpicTask epic) {
        if (epicTasks.containsKey(epic.getSubTaskId())) {
            epicTasks.put(epic.getTaskId(), epic);
            updateEpicTaskStatus(epic);
        }
    }

    @Override
    public EpicTask getEpicById(int id) {
        EpicTask epic = epicTasks.get(id);
        historyManager.add(epic);
        return epic;
    }


    @Override
    public ArrayList<EpicTask> getAllEpics() {
        return new ArrayList<>(epicTasks.values());
    }

    @Override
    public void saveSubTask(SubTask subtask) {
        subtask.setTaskId(generateTaskId());
        subTasks.put(subtask.getTaskId(), subtask);
        EpicTask epic = epicTasks.get(subtask.getEpicTaskId());
        if (epic != null) {
            epic.addSubTaskId(subtask.getTaskId());
            updateEpicTaskStatus(epic);
        }
    }

    @Override
    public void deleteSubTask(int id) {
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
    public ArrayList<SubTask> getSubTasksByEpicId(int epicId) {
        ArrayList<SubTask> result = new ArrayList<>();
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
    public SubTask getSubTaskById(int id) {
        SubTask subtask = subTasks.get(id);
        historyManager.add(subtask);
        return subtask;
    }

    @Override
    public void updateSubTask(SubTask subtask) {
        if (subTasks.containsKey(subtask.getTaskId())) {
            subTasks.put(subtask.getTaskId(), subtask);
            EpicTask epic = epicTasks.get(subtask.getEpicTaskId());
            if (epic != null) {
                updateEpicTaskStatus(epic);
            }
        }
    }

    @Override
    public ArrayList<SubTask> getAllSubTasks() {
        return new ArrayList<>(subTasks.values());
    }

    @Override
    public void saveTask(Task task) {
        task.setTaskId(generateTaskId());
        tasks.put(task.getTaskId(), task);
    }

    @Override
    public void deleteTask(int id) {
        if (tasks.containsKey(id)) {
            tasks.remove(id);
        }
    }

    @Override
    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public void updateTask(Task task) {
        tasks.put(task.getTaskId(), task);
    }

    @Override
    public Task getTaskById(int id) {
        Task task = tasks.get(id);
        historyManager.add(task);
        return task;
    }

    @Override
    public int generateTaskId() {
        return tasksId++;

    }

    @Override
    public void updateEpicTaskStatus(EpicTask epic) {
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

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }
}
