package server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public abstract class BaseHttpHandler implements HttpHandler {
    protected final Gson gson;

    public BaseHttpHandler(Gson gson) {
        this.gson = gson;
    }

    // Сообщение о статусе выполнения "OK" (200)
    protected void sendOk(HttpExchange exchange, Object responseObject) throws IOException {
        String jsonString = gson.toJson(responseObject);
        byte[] bytes = jsonString.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json; charset=UTF-8");
        exchange.sendResponseHeaders(200, bytes.length);
        OutputStream outputStream = exchange.getResponseBody();
        outputStream.write(bytes);
        outputStream.flush();
        outputStream.close();
        exchange.close();
    }

    // Сообщение об ошибке "Ресурс не найден" (404)
    protected void sendNotFound(HttpExchange exchange, String message) throws IOException {
        String errorMessage = "{\"error 404\": \"" + message + "\"}";
        byte[] bytes = errorMessage.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json; charset=UTF-8");
        exchange.sendResponseHeaders(404, bytes.length);
        OutputStream outputStream = exchange.getResponseBody();
        outputStream.write(bytes);
        outputStream.flush();
        outputStream.close();
        exchange.close();
    }

    // Сообщение об ошибке "Конфликт при добавлении" (406)
    protected void sendConflict(HttpExchange exchange, String message) throws IOException {
        String errorMessage = "{\"error 406\": \"" + message + "\"}";
        byte[] bytes = errorMessage.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json; charset=UTF-8");
        exchange.sendResponseHeaders(406, bytes.length);
        OutputStream outputStream = exchange.getResponseBody();
        outputStream.write(bytes);
        outputStream.flush();
        outputStream.close();
        exchange.close();
    }

    // Сообщение об ошибке "Внутренняя ошибка сервера" (400)
    protected void sendInternalError(HttpExchange exchange, String message) throws IOException {
        String errorMessage = "{\"error 400\": \"" + message + "\"}";
        byte[] bytes = errorMessage.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json; charset=UTF-8");
        exchange.sendResponseHeaders(400, bytes.length);
        OutputStream outputStream = exchange.getResponseBody();
        outputStream.write(bytes);
        outputStream.flush();
        outputStream.close();
        exchange.close();
    }

    // Общее чтение тела запроса
    protected String readBodyFromExchange(HttpExchange exchange) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        byte[] chunk = new byte[1024];
        int count;
        while ((count = exchange.getRequestBody().read(chunk)) >= 0) {
            buffer.write(chunk, 0, count);
        }
        return buffer.toString(StandardCharsets.UTF_8.name());
    }
}