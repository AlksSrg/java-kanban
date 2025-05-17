package server;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import exceptions.TaskNotFoundException;
import managers.TaskManager;
import tasks.EpicTask;
import tasks.SubTask;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

public class SubtasksHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager taskManager;

    public SubtasksHandler(TaskManager taskManager, Gson gson) {
        super(gson);
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            switch (exchange.getRequestMethod()) {
                case "GET":
                    if ("/subtasks".equals(exchange.getRequestURI().toString())) {
                        processGetAllSubtasks(exchange); // Получить все подзадачи
                    } else {
                        processGetSingleSubtask(exchange); // Получить отдельную подзадачу по ID
                    }
                    break;
                case "POST":
                    processCreateSubtask(exchange); // Создать новую подзадачу
                    break;
                case "PUT":
                    processUpdateSubtask(exchange); // Обновить подзадачу
                    break;
                case "DELETE":
                    processDeleteSubtask(exchange); // Удалить подзадачу
                    break;
                default:
                    exchange.sendResponseHeaders(405, -1); // Метод не поддерживается
                    break;
            }
        } catch (TaskNotFoundException e) {
            sendNotFound(exchange, e.getMessage()); // Подзадача не найдена
        } catch (IOException ioEx) {
            sendInternalError(exchange, "Ошибка ввода-вывода."); // Внутренняя ошибка
        } catch (RuntimeException ex) {
            sendInternalError(exchange, "Внутренняя ошибка сервера."); // Любые другие непредвиденные ошибки
        }
    }

    // Получение всех подзадач
    private void processGetAllSubtasks(HttpExchange exchange) throws IOException {
        List<SubTask> allSubtasks = taskManager.getAllSubTasks();
        sendOk(exchange, allSubtasks);
    }

    // Получение отдельной подзадачи по ID
    private void processGetSingleSubtask(HttpExchange exchange) throws IOException, TaskNotFoundException {
        String path = exchange.getRequestURI().getPath();
        int subtaskId = Integer.parseInt(path.substring(path.lastIndexOf('/') + 1));
        SubTask subtask = taskManager.getSubTaskById(subtaskId);
        if (subtask == null) throw new TaskNotFoundException("Подзадача с указанным ID не найдена.");
        sendOk(exchange, subtask);
    }

    // Создание новой подзадачи
    private void processCreateSubtask(HttpExchange exchange) throws IOException {
        String requestBody = readBodyFromExchange(exchange);
        Type subtaskType = new TypeToken<SubTask>() {
        }.getType();
        SubTask subtask = gson.fromJson(requestBody, subtaskType);

        // Проверяем, существует ли родительская задача
        EpicTask parentTask = taskManager.getEpicById(subtask.getEpicTaskId());
        if (parentTask == null) {
            sendConflict(exchange, "Родительская задача не найдена или недопустима.");
            return;
        }

        // Сохраняем подзадачу
        taskManager.saveSubTask(subtask);
        sendOk(exchange, subtask);
    }

    // Обновление подзадачи
    private void processUpdateSubtask(HttpExchange exchange) throws IOException {
        String requestBody = readBodyFromExchange(exchange);
        Type subtaskType = new TypeToken<SubTask>() {
        }.getType();
        SubTask updatedSubtask = gson.fromJson(requestBody, subtaskType);
        taskManager.updateSubTask(updatedSubtask);
        sendOk(exchange, updatedSubtask);
    }

    // Удаление подзадачи
    private void processDeleteSubtask(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        int subtaskId = Integer.parseInt(path.substring(path.lastIndexOf('/') + 1));

        // Проверяем, существует ли такая подзадача
        SubTask subtask = taskManager.getSubTaskById(subtaskId);
        if (subtask == null) {
            sendNotFound(exchange, "Подзадача с указанным ID не найдена.");
            return;
        }

        // Если подзадача существует, удаляем её
        taskManager.deleteSubTask(subtaskId);
        exchange.sendResponseHeaders(204, -1); // Нет содержания
    }
}