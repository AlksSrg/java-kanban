package managers;

import tasks.EpicTask;
import tasks.SubTask;
import tasks.Task;
import tools.TaskStatus;
import tools.TaskTimeException;

import java.time.Instant;
import java.time.ZoneId;
import java.util.*;

//реализация методов класса TaskManager
public class InMemoryTaskManager implements TaskManager {
    protected Map<Integer, Task> tasks;
    protected Map<Integer, EpicTask> epicTasks;
    protected Map<Integer, SubTask> subTasks;
    protected final Comparator<Task> comparator = Comparator.comparing(Task::getStartTime);
    protected final Set<Task> sortedTasks = new TreeSet<>(comparator);
    protected HistoryManager historyManager;
    protected int tasksId = 1;

    public InMemoryTaskManager() {
        this.historyManager = Managers.getDefaultHistory();
        this.tasks = new HashMap<>();
        this.epicTasks = new HashMap<>();
        this.subTasks = new HashMap<>();
    }

    //сохранение задачи типа Epic
    @Override
    public void saveEpicTask(EpicTask epic) { //сохранение задачи типа Epic
        epic.setTaskId(generateTaskId());
        epicTasks.put(epic.getTaskId(), epic);
    }

    //удаление задачи типа Epic
    @Override
    public void deleteEpicTask(int id) {
        EpicTask epic = epicTasks.remove(id);
        if (epic != null) {
            for (int subtaskId : epic.getSubTaskId()) {
                subTasks.remove(subtaskId);
            }
            sortedTasks.remove(epic);
        }
    }

    //обновление задачи типа Epic
    @Override
    public void updateEpicTask(EpicTask epic) {
        if (epicTasks.containsKey(epic.getSubTaskId())) {
            epicTasks.put(epic.getTaskId(), epic);
            updateEpicTaskStatus(epic);
        }
    }

    //получение задачи типа Epic по ее Id
    @Override
    public EpicTask getEpicById(int id) {
        EpicTask epic = epicTasks.get(id);
        historyManager.add(epic);
        return epic;
    }

    //получение всех задач типа Epic
    @Override
    public List<EpicTask> getAllEpics() {
        return new ArrayList<>(epicTasks.values());
    }

    //сохранение задачи типа SubTask
    @Override
    public void saveSubTask(SubTask subtask) {
        subtask.setTaskId(generateTaskId());
        subTasks.put(subtask.getTaskId(), subtask);
        EpicTask epic = epicTasks.get(subtask.getEpicTaskId());
        if (epic != null) {
            epic.addSubTaskId(subtask.getTaskId());
            updateEpicTaskStatus(epic);
        }
        addTaskToSortedList(subtask);
    }

    //удаление задачи типа Epic
    @Override
    public void deleteSubTask(int id) { //удаление задачи типа Epic
        SubTask subtask = subTasks.remove(id);
        if (subtask != null) {
            EpicTask epic = epicTasks.get(subtask.getEpicTaskId());
            if (epic != null) {
                epic.removeSubtaskId(id);
                updateEpicTaskStatus(epic);
            }
            sortedTasks.remove(subtask);
        }
    }

    //получение задачи типа SubTasks по Id ее Epic
    @Override
    public List<SubTask> getSubTasksByEpicId(int epicId) { //получение задачи типа SubTasks по Id ее Epic
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

    //получение задачи типа SubTasks по ее Id
    @Override
    public SubTask getSubTaskById(int id) { //получение задачи типа SubTasks по ее Id
        SubTask subtask = subTasks.get(id);
        historyManager.add(subtask);
        return subtask;
    }

    //обновление задачи типа SubTasks
    @Override
    public void updateSubTask(SubTask subtask) { //обновление задачи типа SubTasks
        if (subTasks.containsKey(subtask.getTaskId())) {
            subTasks.put(subtask.getTaskId(), subtask);
            EpicTask epic = epicTasks.get(subtask.getEpicTaskId());
            if (epic != null) {
                updateEpicTaskStatus(epic);
            }
            addTaskToSortedList(subtask);
        }
    }

    //получение всех задач типа SubTask
    @Override
    public List<SubTask> getAllSubTasks() {
        return new ArrayList<>(subTasks.values());
    }

    // сохранение задачи типа Task
    @Override
    public void saveTask(Task task) { // сохранение задачи типа Task
        task.setTaskId(generateTaskId());
        tasks.put(task.getTaskId(), task);
        addTaskToSortedList(task);
    }

    // удаление задачи типа Task
    @Override
    public void deleteTask(int id) {
        sortedTasks.remove(getTaskById(id));
        tasks.remove(id);
    }

    //получение всех задач типа Task
    @Override
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    //обновление задачи типа Task
    @Override
    public void updateTask(Task task) {
        tasks.put(task.getTaskId(), task);
        addTaskToSortedList(task);
    }

    //получение задачи типа Tasks по ее Id
    @Override
    public Task getTaskById(int id) {
        Task task = tasks.get(id);
        historyManager.add(task);
        return task;
    }

    //получение истории просмотров
    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    //Добавление задачи в лист приоритета
    public void addTaskToSortedList(Task task) {
        boolean isCheck = checking(task);
        if (!isCheck) {
            sortedTasks.add(task);
        } else {
            throw new TaskTimeException("Пересечение задач по времени");
        }
    }

    //Очистка всех списков задач
    @Override
    public void clearData() {
        tasks.clear();      // Удаляем все таски
        epicTasks.clear();      // Удаляем все эпики
        subTasks.clear();   // Удаляем все сабтаски
    }

    //Получение отсортированного списка задач
    public List<Task> getPrioritizedTasks() {
        List<Task> prioritizedTasks = new ArrayList<>(sortedTasks.size());
        prioritizedTasks.addAll(sortedTasks);
        return prioritizedTasks;
    }


    // генерация уникально Id для всех типов задач
    private int generateTaskId() {
        return tasksId++;
    }

    //обновление статуса задачи типа Epic при обновлении/изменении статуса ее задач типа SubTask
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

    // метод для проверки пересечения задач по времени
    private boolean checking(Task task) {
        // Определяем часы и минуты текущего региона ("Europe/Moscow")
        ZoneId moscowZone = ZoneId.of("Europe/Moscow");

        // Получаем начало и конец заданной задачи
        Instant startOfTask = task.getStartTime().atZone(moscowZone).toInstant();
        Instant endOfTask = task.getEndTime().atZone(moscowZone).toInstant();

        if (startOfTask == null || endOfTask == null) {
            return false; // Нет временных ограничений, значит нет конфликтов
        }

        for (Task existingTask : sortedTasks) {
            if (existingTask.getStartTime() == null || existingTask.getEndTime() == null) {
                continue; // Пропускаем задачи без временных рамок
            }
            // Получаем начало и конец существующей задачи
            Instant startExisting = existingTask.getStartTime().atZone(moscowZone).toInstant();
            Instant endExisting = existingTask.getEndTime().atZone(moscowZone).toInstant();

            // Проверяем случаи пересечения:
            boolean isCovering = startExisting.isBefore(startOfTask) && endExisting.isAfter(endOfTask);
            boolean isOverlappingByEnd = startExisting.isBefore(startOfTask) && endExisting.isAfter(startOfTask);
            boolean isOverlappingByStart = startExisting.isBefore(endOfTask) && endExisting.isAfter(endOfTask);
            boolean isWithin = startExisting.isAfter(startOfTask) && endExisting.isBefore(endOfTask);

            if (isCovering || isOverlappingByEnd || isOverlappingByStart || isWithin) {
                return true; // Нашли пересечение, прекращаем дальнейшую проверку
            }
        }
        return false; // Пересечения не найдены
    }
}