package server;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import exceptions.TaskNotFoundException;
import managers.TaskManager;
import tasks.Task;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

public class TasksHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager taskManager;

    public TasksHandler(TaskManager taskManager, Gson gson) {
        super(gson);
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            switch (exchange.getRequestMethod()) {
                case "GET":
                    if ("/tasks".equals(exchange.getRequestURI().toString())) {
                        processGetAllTasks(exchange); // Получить все задачи
                    } else {
                        processGetSingleTask(exchange); // Получить отдельную задачу по ID
                    }
                    break;
                case "POST":
                    processCreateTask(exchange); // Создать новую задачу
                    break;
                case "PUT":
                    processUpdateTask(exchange); // Обновить задачу
                    break;
                case "DELETE":
                    processDeleteTask(exchange); // Удалить задачу
                    break;
                default:
                    exchange.sendResponseHeaders(405, -1); // Метод не поддерживается
                    break;
            }
        } catch (TaskNotFoundException e) {
            sendNotFound(exchange, e.getMessage()); // Задача не найдена
        } catch (IOException ioEx) {
            sendInternalError(exchange, "Ошибка ввода-вывода."); // Внутренняя ошибка
        } catch (RuntimeException ex) {
            sendInternalError(exchange, "Внутренняя ошибка сервера."); // Любые другие непредвиденные ошибки
        }
    }

    // Получение всех задач
    private void processGetAllTasks(HttpExchange exchange) throws IOException {
        List<Task> allTasks = taskManager.getAllTasks();
        sendOk(exchange, allTasks);
    }

    // Получение отдельной задачи по ID
    private void processGetSingleTask(HttpExchange exchange) throws IOException, TaskNotFoundException {
        String path = exchange.getRequestURI().getPath();
        int taskId = Integer.parseInt(path.substring(path.lastIndexOf('/') + 1));
        Task task = taskManager.getTaskById(taskId);
        if (task == null) throw new TaskNotFoundException("Задача с указанным ID не найдена.");
        sendOk(exchange, task);
    }

    // Создание новой задачи
    private void processCreateTask(HttpExchange exchange) throws IOException {
        String requestBody = readBodyFromExchange(exchange);
        Type taskType = new TypeToken<Task>() {
        }.getType();
        Task task = gson.fromJson(requestBody, taskType);
        taskManager.saveTask(task);
        sendOk(exchange, task);
    }

    // Обновление задачи
    private void processUpdateTask(HttpExchange exchange) throws IOException {
        String requestBody = readBodyFromExchange(exchange);
        Type taskType = new TypeToken<Task>() {
        }.getType();
        Task updatedTask = gson.fromJson(requestBody, taskType);
        taskManager.updateTask(updatedTask);
        sendOk(exchange, updatedTask);
    }

    // Удаление задачи по ID
    private void processDeleteTask(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        int taskId = Integer.parseInt(path.substring(path.lastIndexOf('/') + 1));

        // Проверяем, существует ли задача с указанным ID
        Task taskToDelete = taskManager.getTaskById(taskId);
        if (taskToDelete == null) {
            sendNotFound(exchange, "Задача с указанным ID не найдена.");
            return;
        }

        // Если задача существует, удаляем её
        taskManager.deleteTask(taskId);
        exchange.sendResponseHeaders(204, -1); // Нет содержания
    }
}