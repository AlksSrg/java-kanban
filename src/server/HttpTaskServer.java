package server;

import adapters.DurationAdapter;
import adapters.LocalDateTimeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpServer;
import managers.Managers;
import managers.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.LocalDateTime;

public class HttpTaskServer {
    private static final int PORT = 8080;
    private final TaskManager taskManager;
    private final Gson gson;
    private HttpServer server;

    // Предоставляет экземпляр Gson с нужным адаптером
    public static Gson provideGson() {
        return new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter()) // Регистрируем адаптер
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .create();
    }

    public HttpTaskServer(TaskManager taskManager) {
        this.taskManager = taskManager;
        this.gson = provideGson(); // Используем настроенный экземпляр Gson
    }

    // Старт сервера
    public synchronized void start() throws IOException {
        this.server = HttpServer.create(new InetSocketAddress(PORT), 0);
        registerEndpoints();
        this.server.setExecutor(null); // Используем стандартный пул потоков
        this.server.start();
        System.out.println("HTTP Сервер запущен на порте:" + PORT);
    }

    // Остановка сервера
    public synchronized void stop() {
        this.server.stop(0); // Немедленно прекращаем работу сервера
        System.out.println("HTTP Сервер был остановлен.");
    }

    // Зарегистрируем обработчики для конечных точек
    private void registerEndpoints() {
        server.createContext("/tasks", new TasksHandler(taskManager, gson));
        server.createContext("/subtasks", new SubtasksHandler(taskManager, gson));
        server.createContext("/epics", new EpicsHandler(taskManager, gson));
        server.createContext("/history", new HistoryHandler(taskManager, gson));
        server.createContext("/prioritized", new PrioritizedHandler(taskManager, gson));
    }

    // Главный метод запуска сервера
    public static void main(String[] args) throws IOException {
        TaskManager taskManager = Managers.getDefault();
        HttpTaskServer serverInstance = new HttpTaskServer(taskManager);
        serverInstance.start();
    }
}