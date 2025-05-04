import managers.HistoryManager;
import managers.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import tasks.EpicTask;
import tasks.SubTask;
import tasks.Task;
import tools.TaskStatus;
import tools.Type;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public abstract class ManagerTest<T extends TaskManager> {

    protected T taskManager;
    protected HistoryManager historyManager;

    protected Task testTask;
    protected EpicTask testEpicTask;
    protected SubTask testSubTask1;
    protected SubTask testSubTask2;


    @BeforeEach
    protected abstract void createManagerForTest() throws IOException;


    protected void createTestTasks() {

        // Определение фиксированной даты и времени для задач
        LocalDateTime taskTime = LocalDateTime.of(2025, 5, 4, 10, 0); // Замороженное время для Task
        LocalDateTime epicTime = LocalDateTime.of(2025, 6, 4, 11, 0); // Замороженное время для Epic
        LocalDateTime subTaskTime1 = LocalDateTime.of(2025, 7, 4, 12, 0); // Замороженное время для SubTask1
        LocalDateTime subTaskTime2 = LocalDateTime.of(2025, 7, 4, 15, 0); // Замороженное время для SubTask2

        // Создаём тестовую задачу
        testTask = new Task(1, "Основная задача", "Детали первой задачи", TaskStatus.NEW, Type.TASK,
                Duration.ofHours(1), taskTime);

        // Создаём тестовый эпик
        testEpicTask = new EpicTask(2, "Эпик-задача", "Информация о задаче", TaskStatus.NEW, Type.EPIC,
                Duration.ofHours(2), epicTime, epicTime.plusDays(1), List.of());

        // Создаём тестовую подзадачу
        testSubTask1 = new SubTask(3, "Подзадача", "Детали подзадачи", TaskStatus.NEW, Type.SUBTASK,
                Duration.ofHours(2), subTaskTime1, 2);
        testSubTask2 = new SubTask(4, "Подзадача", "Детали подзадачи", TaskStatus.NEW, Type.SUBTASK,
                Duration.ofHours(2), subTaskTime2, 2);

        // Сохранение задач
        taskManager.saveTask(testTask);
        taskManager.saveEpicTask(testEpicTask);
        taskManager.saveSubTask(testSubTask1);
        taskManager.saveSubTask(testSubTask2);
    }

}



