package handlerTest;

import adapters.DurationAdapter;
import adapters.LocalDateTimeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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

public class TasksHandlerTest {

    private TaskManager taskManager;
    private HttpTaskServer taskServer;
    private Gson gson;

    // Метод для запуска сервера перед каждым тестом
    @BeforeEach
    public void serverStart() throws IOException {
        taskManager = new InMemoryTaskManager();
        taskServer = new HttpTaskServer(taskManager);
        taskServer.start();
        gson = new GsonBuilder()
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();
    }

    // Метод для остановки сервера после каждого тестирования
    @AfterEach
    public void serverStop() {
        taskServer.stop();
    }

    // Тест на успешное создание задачи
    @Test
    public void testAddTaskSuccess() throws Exception {
        Task task = new Task(1, "Тестовая задача 1", "Информация о тестовой задаче 1",
                TaskStatus.NEW, Type.TASK, Duration.ofHours(1), LocalDateTime.now());
        String taskJson = gson.toJson(task);

        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertTrue(!taskManager.getAllTasks().isEmpty());
    }

    // Тест на неудачное создание задачи (пустой запрос)
    @Test
    public void testAddTaskEmptyPayloadFailure() throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(400, response.statusCode());
    }

    // Тест на успешное обновление задачи
    @Test
    public void testUpdateTaskSuccess() throws Exception {
        Task originalTask = new Task(1, "Исходная задача со статусом NEW", "Описание задачи",
                TaskStatus.NEW, Type.TASK, Duration.ofHours(1), LocalDateTime.now());

        Task updatedTask = new Task(1, "Обновленная задача, но уже со статусом DONE", "Описание задачи",
                TaskStatus.DONE, Type.TASK, Duration.ofHours(1), LocalDateTime.now());
        updatedTask.setTaskId(originalTask.getTaskId());
        String taskJson = gson.toJson(updatedTask);

        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks/" + updatedTask.getTaskId());
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertEquals("Обновленная задача, но уже со статусом DONE", taskManager.getTaskById(updatedTask.getTaskId()).getTaskName());
    }

    // Тест на успешное удаление задачи
    @Test
    public void testDeleteTaskSuccess() throws Exception {
        Task taskToRemove = new Task(1, "Тестовая задача 1", "Информация о тестовой задаче 1",
                TaskStatus.NEW, Type.TASK, Duration.ofHours(1), LocalDateTime.now());
        taskManager.saveTask(taskToRemove);

        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks/" + taskToRemove.getTaskId());
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(204, response.statusCode());
        assertTrue(taskManager.getAllTasks().isEmpty());
    }

    // Тест на попытку удалить несуществующую задачу
    @Test
    public void testDeleteNonExistingTask() throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks/9999");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
    }
}