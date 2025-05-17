package handlerTest;

import com.google.gson.Gson;
import managers.InMemoryTaskManager;
import managers.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.HttpTaskServer;
import tasks.EpicTask;
import tools.TaskStatus;
import tools.Type;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class EpicsHandlerTest {

    private TaskManager taskManager;
    private HttpTaskServer taskServer;
    private Gson gson;
    protected LocalDateTime epicTime = LocalDateTime.of(2025, 6, 4, 11, 0);

    // Метод для запуска сервера перед каждым тестом
    @BeforeEach
    public void startServer() throws IOException {
        taskManager = new InMemoryTaskManager();
        taskServer = new HttpTaskServer(taskManager);
        taskServer.start();
        gson = HttpTaskServer.provideGson();
    }

    // Метод для остановки сервера после каждого тестирования
    @AfterEach
    public void stopServer() {
        taskServer.stop();
    }


    // Тест на успешное создание эпика
    @Test
    public void testAddEpicSuccess() throws Exception {
        EpicTask epic = new EpicTask(2, "Эпик-задача", "Информация о задаче", TaskStatus.NEW, Type.EPIC, Duration.ofHours(2), epicTime, epicTime.plusDays(1), List.of());
        String epicJson = gson.toJson(epic);

        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(epicJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertFalse(taskManager.getAllEpics().isEmpty());
    }

    // Тест на успешное удаление эпика.
    @Test
    public void testDeleteEpicSuccess() throws Exception {
        EpicTask epic = new EpicTask(2, "Эпик-задача", "Информация о задаче", TaskStatus.NEW, Type.EPIC, Duration.ofHours(2), epicTime, epicTime.plusDays(1), List.of());
        taskManager.saveEpicTask(epic);

        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/epics/" + epic.getTaskId());
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(204, response.statusCode());
        assertTrue(taskManager.getAllEpics().isEmpty());
    }

    // Тест на попытку удалить несуществующий эпик
    @Test
    public void testDeleteNonExistingEpic() throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/epics/9999");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode()); // Должна возвращаться ошибка 404
    }
}