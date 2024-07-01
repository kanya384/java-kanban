import com.sun.net.httpserver.HttpServer;
import handlers.EpicHandlers;
import handlers.SubTaskHandlers;
import handlers.TaskHandlers;
import handlers.UserHandler;
import task.Managers;
import task.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServer {
    final static int PORT = 8080;
    private static HttpServer httpServer;

    public static void main(String[] args) {
        start();
    }

    public static void start() {
        try {
            httpServer = HttpServer.create(new InetSocketAddress(PORT), 0);
            TaskManager taskManager = Managers.getDefault();
            httpServer.createContext("/tasks", new TaskHandlers(taskManager, "tasks"));
            httpServer.createContext("/subtasks", new SubTaskHandlers(taskManager, "subtasks"));
            httpServer.createContext("/epics", new EpicHandlers(taskManager, "epics"));
            UserHandler userHandlers = new UserHandler(taskManager);
            httpServer.createContext("/history", userHandlers);
            httpServer.createContext("/prioritized", userHandlers);
            httpServer.start();
            System.out.println(String.format("HTTP-сервер запущен на %d порту!", PORT));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void stop() {
        httpServer.stop(0);
    }
}
