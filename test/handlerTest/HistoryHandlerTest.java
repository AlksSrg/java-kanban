package handlerTest;

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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class HistoryHandlerTest {

    private TaskManager taskManager;
    private HttpTaskServer taskServer;

    // Метод для запуска сервера перед каждым тестом
    @BeforeEach
    public void startServer() throws IOException {
        taskManager = new InMemoryTaskManager();
        taskServer = new HttpTaskServer(taskManager);
        taskServer.start();
    }

    // Метод для остановки сервера после каждого тестирования
    @AfterEach
    public void stopServer() {
        taskServer.stop();
    }

    // Тест на проверку получения истории задач
    @Test
    public void testGetHistory() throws Exception {
        Task task1 = new Task(1, "Тестовая задача 1", "Информация о тестовой задаче 1",
                TaskStatus.NEW, Type.TASK, Duration.ofHours(1), LocalDateTime.now());
        taskManager.saveTask(task1);

        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/history");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertTrue(!response.body().isBlank()); // Ответ не пустой
    }
}