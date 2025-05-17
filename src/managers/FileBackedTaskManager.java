package managers;

import exceptions.ManagerSaveException;
import tasks.EpicTask;
import tasks.SubTask;
import tasks.Task;
import tools.TaskStatus;
import tools.Type;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static tasks.Task.dateTimeFormatter;

//логика работы и сохранения/воспроизведения из файла с ПК с задачами
public class FileBackedTaskManager extends InMemoryTaskManager {

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

    // загрузка внесенных тасок из файла в программу
    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager fm = new FileBackedTaskManager(file);
        try (Stream<String> stream = Files.lines(file.toPath())) {
            stream.skip(1) // Пропускаем первую строку (заголовок)
                    .map(FileBackedTaskManager::fromString)
                    .forEach(task -> {
                        if (task instanceof EpicTask) {
                            fm.saveEpicTask((EpicTask) task);
                        } else if (task instanceof SubTask) {
                            fm.saveSubTask((SubTask) task);
                        } else {
                            fm.saveTask(task);
                        }
                    });
        } catch (IOException ex) {
            throw new ManagerSaveException("Ошибка при чтении файла.");
        }
        return fm;
    }

    //метод для сохранения тасок в файл на пк
    private void save() {
        try (Writer writer = new FileWriter(file)) {
            writer.write("id,type,status,name,tasksInfo,start_time,duration,end_time,subtask_ids\n");
            tasks.values().stream()
                    .map(Task::toStringFromFile)
                    .forEachOrdered(line -> {
                        try {
                            writer.write(line + "\n");
                        } catch (IOException e) {
                            throw new ManagerSaveException("Ошибка при сохранении задач.");
                        }
                    });
        } catch (IOException ex) {
            throw new ManagerSaveException("Ошибка при сохранении задач.");
        }
    }

    private static Task fromString(String content) {
        String[] fields = content.split(",", -1); // Оставляет пустые элементы, если есть лишние запятые
        if (fields.length < 6) return null;       // Если что-то из пунктов не заполнено

        int taskId = Integer.parseInt(fields[0]);          // ID задачи
        Type type = Type.valueOf(fields[1]);               // Тип задачи
        TaskStatus status = TaskStatus.valueOf(fields[2]); // Статус задачи
        String taskName = fields[3];                       // Имя задачи
        String tasksInfo = fields[4];                      // Дополнительная информация
        LocalDateTime startTime = LocalDateTime.parse(fields[5], dateTimeFormatter); // Дата начала
        Duration duration = Duration.parse(fields[6]);     // Длительность задачи

        // Создаем конкретные объекты задач в зависимости от типа
        switch (type) {
            case TASK:
                return new Task(taskId, taskName, tasksInfo, status, type, duration, startTime);

            case EPIC:
                LocalDateTime endTime = fields.length >= 8 && !fields[7].isBlank() ?
                        LocalDateTime.parse(fields[7], dateTimeFormatter) :
                        null;

                List<Integer> subtaskIds = new ArrayList<>();
                if (fields.length >= 9 && !fields[8].isBlank()) {
                    String[] subtaskFields = fields[8].split(":");
                    for (String subtaskField : subtaskFields) {
                        if (!subtaskField.isBlank()) {
                            subtaskIds.add(Integer.parseInt(subtaskField.trim()));
                        }
                    }
                }

                return new EpicTask(taskId, taskName, tasksInfo, status, type, duration, startTime, endTime, subtaskIds);

            case SUBTASK:
                int epicTaskId = fields.length >= 8 ? Integer.parseInt(fields[7]) : -1;
                return new SubTask(taskId, taskName, tasksInfo, status, type, duration, startTime, epicTaskId);

            default:
                return null;
        }
    }
}

