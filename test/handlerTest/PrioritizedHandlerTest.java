package handlerTest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import exceptions.TaskTimeException;
import managers.InMemoryTaskManager;
import managers.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.HttpTaskServer;
import tasks.Task;
import tools.TaskStatus;
import tools.Type;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PrioritizedHandlerTest {

    private TaskManager taskManager;
    private HttpTaskServer taskServer;
    private Gson gson;
    protected LocalDateTime taskTime = LocalDateTime.of(2025, 5, 4, 10, 0); // Замороженное время для Task

    // Метод для запуска сервера перед каждым тестом
    @BeforeEach
    public void setup() throws IOException {
        taskManager = new InMemoryTaskManager();
        taskServer = new HttpTaskServer(taskManager);
        taskServer.start();

        // Настройка Gson с нашими адаптерами
        gson = new GsonBuilder()
                .registerTypeAdapter(Duration.class, new adapters.DurationAdapter())
                .registerTypeAdapter(LocalDateTime.class, new adapters.LocalDateTimeAdapter())
                .create();
    }

    // Метод для остановки сервера после каждого тестирования
    @AfterEach
    public void tearDown() throws InterruptedException {
        Thread.sleep(1000); // Ждем секунду, чтобы дать серверу шанс закончить обработку
        taskServer.stop();
    }

    // Тест на успешное добавление задач без пересечения по времени
    @Test
    public void testSuccessfulAddingTasksWithoutOverlap() throws Exception {

        // Первая задача: начинается и длится один час
        Task earlyTask = new Task(0, "Ранняя задача", "Описание ранней задачи",
                TaskStatus.NEW, Type.TASK, Duration.ofHours(1), taskTime);

        // Вторая задача: начинается через час и длится восемь часов
        Task laterTask = new Task(1, "Позднее задача", "Описание позднее задачи",
                TaskStatus.NEW, Type.TASK, Duration.ofHours(8), taskTime.plusHours(1));

        taskManager.saveTask(earlyTask);
        taskManager.saveTask(laterTask);

        // Увеличим таймаут ожидания ответа
        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();

        URI uri = URI.create("http://localhost:8080/prioritized");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        List<Task> sortedTasks = Arrays.asList(gson.fromJson(response.body(), Task[].class));
        assertEquals(2, sortedTasks.size());
        assertEquals("Ранняя задача", sortedTasks.get(0).getTaskName()); // Ранняя задача идёт первой
        assertEquals("Позднее задача", sortedTasks.get(1).getTaskName()); // Позднее задача идёт следующей
    }


    // Тест на отказ при пересечении по времени
    @Test
    public void testFailOnTimeIntersection() throws Exception {
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("Europe/Moscow"));

        // Первая задача: началась через час и продолжалась один час
        Task firstTask = new Task(1, "Первая задача", "Описание первой задачи",
                TaskStatus.NEW, Type.TASK, Duration.ofHours(1), now.plusHours(1).toLocalDateTime());

        // Вторая задача: началась через полтора часа и длилась полчаса
        // Таким образом, её начало приходится на середину первого интервала
        Task overlappingTask = new Task(2, "Вторая задача", "Описание второй задачи",
                TaskStatus.NEW, Type.TASK, Duration.ofMinutes(30), now.plusHours(1).plusMinutes(30).toLocalDateTime());

        taskManager.saveTask(firstTask);

        // Попытка сохранить вторую задачу приведет к конфликту
        Throwable exception = assertThrows(
                TaskTimeException.class,
                () -> taskManager.saveTask(overlappingTask),
                "Задачи пересекаются!"
        );

        assertEquals("Пересечение задач по времени", exception.getMessage());
    }

    // Тест на возврат пустого списка, если нет задач
    @Test
    public void testReturnEmptyListWhenNoTasks() throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/prioritized");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        List<Task> emptyList = Arrays.asList(gson.fromJson(response.body(), Task[].class));
        assertTrue(emptyList.isEmpty());
    }
}