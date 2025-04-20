package managers;

import tasks.EpicTask;
import tasks.SubTask;
import tasks.Task;
import tools.ManagerSaveException;
import tools.TaskStatus;
import tools.Type;

import java.io.FileWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.io.*;
import java.util.List;
import java.util.Map;


import static tools.Type.*;

public class FileBackedTaskManager extends InMemoryTaskManager { //логика работы и сохранения/воспроизведения из файла с ПК с задачами

    private final File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    @Override
    public void saveEpicTask(EpicTask epic) {
        super.saveEpicTask(epic);
        save();
    }

    @Override
    public void deleteEpicTask(int id) {
        super.deleteEpicTask(id);
        save();
    }

    @Override
    public void updateEpicTask(EpicTask epic) {
        super.updateEpicTask(epic);
        save();
    }

    @Override
    public void saveSubTask(SubTask subtask) {
        super.saveSubTask(subtask);
        save();
    }

    @Override
    public void deleteSubTask(int id) {
        super.deleteSubTask(id);
        save();
    }

    @Override
    public void saveTask(Task task) {
        super.saveTask(task);
        save();
    }

    @Override
    public void deleteTask(int id) {
        super.deleteTask(id);
        save();
    }

    public static FileBackedTaskManager loadFromFile(File file) { // загрузка внесенных тасок из файла в программу
        FileBackedTaskManager fm = new FileBackedTaskManager(file);
        try {
            List<String> fileTasks = Files.readAllLines(file.toPath());

            for (int i = 1; i < fileTasks.size(); i++) {
                String line = fileTasks.get(i);

                if (fromString(line).getType() == EPIC) {
                    EpicTask epic = (EpicTask) fromString(line);
                    fm.saveEpicTask(epic);
                }
                if (fromString(line).getType() == SUBTASK) {
                    SubTask subTask = (SubTask) fromString(line);
                    fm.saveSubTask(subTask);
                }
                if (fromString(line).getType() == TASK) {
                    Task task = fromString(line);
                    fm.saveTask(task);
                }
            }
        } catch (IOException exception) {
            throw new ManagerSaveException("Произошла ошибка во время чтения файла.");
        }
        return fm;
    }

    private void save() { //метод для сохранения тасок в файл на пк
        try (Writer writer = new FileWriter(file)) {
            writer.write("id,type,name,status,info\n");
            Map<Integer, String> allTasks = new HashMap<>();

            Map<Integer, Task> tasksFile = tasks;

            for (Integer id : tasksFile.keySet()) {
                allTasks.put(id, tasksFile.get(id).toStringFromFile());
            }

            Map<Integer, EpicTask> epicsFile = epicTasks;
            for (Integer id : epicsFile.keySet()) {
                allTasks.put(id, epicsFile.get(id).toStringFromFile());
            }

            Map<Integer, SubTask> subtasksFile = subTasks;
            for (Integer id : subtasksFile.keySet()) {
                allTasks.put(id, subtasksFile.get(id).toStringFromFile());
            }

            for (String value : allTasks.values()) {
                writer.write(String.format("%s\n", value));
            }

        } catch (IOException exception) {
            throw new ManagerSaveException("Произошла ошибка во время чтения файла.");
        }
    }

    private static Task fromString(String content) { //метод для получения из строки конечной таски
        Task task = new Task();
        List<Integer> listOfSubTasksIds = new ArrayList<>();
        int id = 0;
        Type type = null;
        String taskName = null;
        TaskStatus status = null;
        String taskInfo = null;
        int epicTaskId = 0;
        String[] elements = content.split(",");
        if (elements[1] == null) {
            return null;
        }
        if (elements[1].equals("EPIC")) {
            listOfSubTasksIds = List.of(Integer.valueOf(elements[0]));
        } else {
            id = Integer.parseInt(elements[0]);
            type = Type.valueOf(elements[1]);
            taskName = String.valueOf(elements[2]);
            status = TaskStatus.valueOf(elements[3]);
            taskInfo = elements[4];
            if (elements.length == 6) {
                epicTaskId = Integer.parseInt(elements[5]);
            }
        }

        if (type == EPIC) {
            return new Task(id, taskName, taskInfo, status);
        } else if (type == SUBTASK) {
            return new SubTask(taskName, taskInfo, id, status, epicTaskId);
        } else if (type == TASK) {
            return new EpicTask((ArrayList<Integer>) listOfSubTasksIds, id, taskName, taskInfo, status);
        }
        return task;
    }

}

