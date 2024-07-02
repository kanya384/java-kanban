import handlers.HttpTaskServer;
import task.Managers;
import task.TaskManager;

public class Main {
    public static final int PORT = 8080;

    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefault();
        HttpTaskServer httpTaskServer = new HttpTaskServer(PORT, taskManager);
        httpTaskServer.start();

        System.out.printf("HTTP-сервер запущен на %d порту!\n", PORT);
    }
}
