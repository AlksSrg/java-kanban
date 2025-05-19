package server;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import exceptions.TaskNotFoundException;
import managers.TaskManager;
import tasks.EpicTask;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

public class EpicsHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager taskManager;

    public EpicsHandler(TaskManager taskManager, Gson gson) {
        super(gson);
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            switch (exchange.getRequestMethod()) {
                case "GET":
                    if ("/epics".equals(exchange.getRequestURI().toString())) {
                        processGetAllEpics(exchange); // Получить все эпики
                    } else {
                        processGetSingleEpic(exchange); // Получить отдельный эпик по ID
                    }
                    break;
                case "POST":
                    processCreateEpic(exchange); // Создать новый эпик
                    break;
                case "PUT":
                    processUpdateEpic(exchange); // Обновить эпик
                    break;
                case "DELETE":
                    processDeleteEpic(exchange); // Удалить эпик
                    break;
                default:
                    exchange.sendResponseHeaders(405, -1); // Метод не поддерживается
                    break;
            }
        } catch (TaskNotFoundException e) {
            sendNotFound(exchange, e.getMessage()); // Эпик не найден
        } catch (IOException ioEx) {
            sendInternalError(exchange, "Ошибка ввода-вывода."); // Внутренняя ошибка
        } catch (RuntimeException ex) {
            sendInternalError(exchange, "Внутренняя ошибка сервера."); // Любые другие непредвиденные ошибки
        }
    }

    // Получение всех эпиков
    private void processGetAllEpics(HttpExchange exchange) throws IOException {
        List<EpicTask> allEpics = taskManager.getAllEpics();
        sendOk(exchange, allEpics);
    }

    // Получение отдельного эпика по ID
    private void processGetSingleEpic(HttpExchange exchange) throws IOException, TaskNotFoundException {
        String path = exchange.getRequestURI().getPath();
        int epicId = Integer.parseInt(path.substring(path.lastIndexOf('/') + 1));
        EpicTask epic = taskManager.getEpicById(epicId);
        if (epic == null) throw new TaskNotFoundException("Эпик с указанным ID не найден.");
        sendOk(exchange, epic);
    }

    // Создание нового эпика
    private void processCreateEpic(HttpExchange exchange) throws IOException {
        String requestBody = readBodyFromExchange(exchange);
        Type epicType = new TypeToken<EpicTask>() {
        }.getType();
        EpicTask epic = gson.fromJson(requestBody, epicType);
        taskManager.saveEpicTask(epic);
        sendOk(exchange, epic);
    }

    // Обновление эпика
    private void processUpdateEpic(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String requestBody = readBodyFromExchange(exchange);
        Type epicType = new TypeToken<EpicTask>() {
        }.getType();
        EpicTask updatedEpic = gson.fromJson(requestBody, epicType);
        taskManager.updateEpicTask(updatedEpic);
        sendOk(exchange, updatedEpic);
    }

    // Удаление эпика
    private void processDeleteEpic(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        int epicId = Integer.parseInt(path.substring(path.lastIndexOf('/') + 1));

        // Проверяем, существует ли такой эпик
        EpicTask existingEpic = taskManager.getEpicById(epicId);
        if (existingEpic == null) {
            sendNotFound(exchange, "Эпик с указанным ID не найден.");
            return;
        }

        // Если эпик существует, удаляем его
        taskManager.deleteEpicTask(epicId);
        exchange.sendResponseHeaders(204, -1); // Отправляем 204 "Нет содержимого"
    }
}