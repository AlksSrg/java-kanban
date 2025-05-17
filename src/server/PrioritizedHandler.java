package server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import managers.TaskManager;

import java.io.IOException;
import java.util.List;

public class PrioritizedHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager taskManager;

    public PrioritizedHandler(TaskManager taskManager, Gson gson) {
        super(gson);
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!"/prioritized".equals(exchange.getRequestURI().toString())) {
            exchange.sendResponseHeaders(404, -1); // Ресурс не найден
            return;
        }

        if ("GET".equals(exchange.getRequestMethod())) {
            processGetPrioritizedList(exchange); // Показать задачи в порядке приоритета
        } else {
            exchange.sendResponseHeaders(405, -1); // Метод не поддерживается
        }
    }

    // Получение списка задач в порядке приоритета
    private void processGetPrioritizedList(HttpExchange exchange) throws IOException {
        List<?> prioritizedTasks = taskManager.getAllTasks(); // Предполагаем, что возвращаются отсортированные задачи
        sendOk(exchange, prioritizedTasks);
    }
}