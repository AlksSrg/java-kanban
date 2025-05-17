package handlerTest;

import com.google.gson.Gson;
import managers.InMemoryTaskManager;
import managers.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.HttpTaskServer;
import tasks.EpicTask;
import tasks.SubTask;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SubtasksHandlerTest {

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

    // Тест на успешное создание подзадачи
    @Test
    public void testAddSubtaskSuccess() throws Exception {

        EpicTask epic = new EpicTask(
                1, "Эпик-задача", "Информация о задаче", TaskStatus.NEW, Type.EPIC, Duration.ofHours(2), epicTime, epicTime.plusDays(1), List.of(2)
        );
        taskManager.saveEpicTask(epic); // Сохраняем эпик

        SubTask subtask = new SubTask(
                2, "Subtask Name", "Subtask Description", TaskStatus.NEW, Type.SUBTASK, Duration.ofMinutes(30), LocalDateTime.now(), 1); // Передаем ID эпика как родительской задачи
        String subtaskJson = gson.toJson(subtask);

        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(subtaskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode()); // Проверяем успешный статус
        assertTrue(!taskManager.getAllSubTasks().isEmpty()); // Проверяем, что подзадача создана
    }

    // Тест на неудачную попытку создания подзадачи без эпика
    @Test
    public void testAddSubtaskWithoutEpicFailure() throws Exception {

        SubTask invalidSubtask = new SubTask(1, "Subtask Name", "Subtask Description",
                TaskStatus.NEW, Type.SUBTASK, Duration.ofMinutes(30),
                LocalDateTime.now(), -1); // Некорректный ID эпика
        String subtaskJson = gson.toJson(invalidSubtask);

        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(subtaskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(406, response.statusCode()); // Ожидается конфликт
    }

    // Тест на успешное удаление подзадачи
    @Test
    public void testDeleteSubtaskSuccess() throws Exception {
        // Цель: проверка успешного удаления подзадачи
        EpicTask epic = new EpicTask(2, "Эпик-задача", "Информация о задаче", TaskStatus.NEW, Type.EPIC, Duration.ofHours(2), epicTime, epicTime.plusDays(1), List.of());
        taskManager.saveTask(epic);

        SubTask subtask = new SubTask(1, "Subtask Name", "Subtask Description",
                TaskStatus.NEW, Type.SUBTASK, Duration.ofMinutes(30),
                LocalDateTime.now(), epic.getTaskId());
        ;
        taskManager.saveSubTask(subtask);

        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/subtasks/" + subtask.getTaskId());
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(204, response.statusCode());
        assertTrue(taskManager.getAllSubTasks().isEmpty());
    }

    // Тест на попытку удалить несуществующую подзадачу
    @Test
    public void testDeleteNonExistingSubtask() throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/subtasks/9999");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
    }
}