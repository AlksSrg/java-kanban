package server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import managers.TaskManager;

import java.io.IOException;
import java.util.List;

public class HistoryHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager taskManager;

    public HistoryHandler(TaskManager taskManager, Gson gson) {
        super(gson);
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!"/history".equals(exchange.getRequestURI().toString())) {
            exchange.sendResponseHeaders(404, -1); // Ресурс не найден
            return;
        }

        if ("GET".equals(exchange.getRequestMethod())) {
            processGetHistory(exchange); // Посмотреть историю
        } else {
            exchange.sendResponseHeaders(405, -1); // Метод не поддерживается
        }
    }

    // Получение истории задач
    private void processGetHistory(HttpExchange exchange) throws IOException {
        List<?> history = taskManager.getHistory();
        sendOk(exchange, history);
    }
}