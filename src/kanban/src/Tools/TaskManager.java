package kanban.src.Tools;

import kanban.src.Tasks.EpicTask;
import kanban.src.Tasks.SubTask;
import kanban.src.Tasks.Task;


import java.util.HashMap;
import java.util.ArrayList;

public class TaskManager {
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, EpicTask> epicTasks = new HashMap<>();
    private final HashMap<Integer, SubTask> subTasks = new HashMap<>();
    private int tasksId = 1;


    private int generateTaskId() {
        return tasksId++;
    }

    public EpicTask saveEpicTask(EpicTask epic) {
        epic.setTaskId(generateTaskId());
        epicTasks.put(epic.getTaskId(), epic);
        return epic;
    }

    public void deleteEpicTask(int id) {
        EpicTask epic = epicTasks.remove(id);
        if (epic != null) {
            for (int subtaskId : epic.getSubTaskId()) {
                subTasks.remove(subtaskId);
            }
        }
    }

    public void updateEpicTask(EpicTask epic) {
        if (epicTasks.containsKey(epic.getSubTaskId())) {
            epicTasks.put(epic.getTaskId(), epic);
            updateEpicTaskStatus(epic);
        }
    }

    private void updateEpicTaskStatus(EpicTask epic) {
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

    public EpicTask getEpicById(int id) {
        return epicTasks.get(id);
    }

    public ArrayList<EpicTask> getAllEpics() {
        return new ArrayList<>(epicTasks.values());
    }

    public SubTask saveSubTask(SubTask subtask) {
        subtask.setTaskId(generateTaskId());
        subTasks.put(subtask.getTaskId(), subtask);
        EpicTask epic = epicTasks.get(subtask.getEpicTaskId());
        if (epic != null) {
            epic.addSubTaskId(subtask.getTaskId());
            updateEpicTaskStatus(epic);
        }
        return subtask;
    }

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

    public SubTask getSubTaskById(int id) {
        return subTasks.get(id);
    }

    public void updateSubTask(SubTask subtask) {
        if (subTasks.containsKey(subtask.getTaskId())) {
            subTasks.put(subtask.getTaskId(), subtask);
            EpicTask epic = epicTasks.get(subtask.getEpicTaskId());
            if (epic != null) {
                updateEpicTaskStatus(epic);
            }
        }
    }

    public ArrayList<SubTask> getAllSubtasks() {
        return new ArrayList<>(subTasks.values());
    }

    public Task saveTask(Task task) {
        task.setTaskId(generateTaskId());
        tasks.put(task.getTaskId(), task);
        return task;
    }

    public void deleteTask(int id) {
        if (tasks.containsKey(id)) {
            tasks.remove(id);
        }
    }

    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    public void updateTask(Task task) {
        tasks.put(task.getTaskId(), task);
    }

    public Task getTask(int id) {
        return tasks.get(id);
    }

}
