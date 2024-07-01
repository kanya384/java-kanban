package handlers;

import com.sun.net.httpserver.HttpExchange;
import task.IntersectsExistingTaskException;
import task.NotFoundException;
import task.Task;
import task.TaskManager;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class TaskHandlers extends BaseTaskHandlers {

    public TaskHandlers(TaskManager taskManager, String basePath) {
        super(taskManager, basePath);
    }

    @Override
    void handleList(HttpExchange exchange) throws IOException {
        String responseString = gson.toJson(taskManager.getTasks());
        sendText(exchange, 200, responseString);
    }

    @Override
    void handleById(HttpExchange exchange) throws IOException {
        try {
            Optional<Integer> idOpt = getIdFromPath(exchange);

            if (idOpt.isEmpty()) {
                sendText(exchange, 400, "Некорректный идентификатор задачи");
                return;
            }

            Task task = taskManager.getTaskById(idOpt.get());

            if (task == null) {
                sendText(exchange, 404, String.format("Задача с идентификатором " + idOpt.get() + " не найдена"));
                return;
            }

            String responseString = gson.toJson(task);
            sendText(exchange, 200, responseString);
        } catch (NotFoundException e) {
            sendText(exchange, 404, e.getMessage());
        }
    }

    @Override
    void handleCreateOrUpdate(HttpExchange exchange) throws IOException {
        try (InputStream is = exchange.getRequestBody()) {
            Optional<Task> optionalTask = parseTask(is);
            if (optionalTask.isEmpty()) {
                sendText(exchange, 400, "Поля задачи не могут быть пустыми");
                return;
            }

            Task task = optionalTask.get();
            try {
                if (task.getId() != 0) {
                    taskManager.updateTask(task);
                    sendText(exchange, 200, "Задача успешно обновлена");
                    return;
                }

                taskManager.createTask(task);
                sendText(exchange, 201, "Задача успешно создана");
            } catch (IntersectsExistingTaskException e) {
                sendText(exchange, 406, "Задача не может быть добавлена, так как пересекается с текущей");
            }
        }
    }

    @Override
    void handleDelete(HttpExchange exchange) throws IOException {
        Optional<Integer> idOpt = getIdFromPath(exchange);

        if (idOpt.isEmpty()) {
            sendText(exchange, 400, "Некорректный идентификатор задачи");
            return;
        }

        taskManager.deleteTaskById(idOpt.get());
        sendText(exchange, 200, "Задача успешно удалена!");
    }

    private Optional<Task> parseTask(InputStream bodyInputStream) throws IOException {
        String body = new String(bodyInputStream.readAllBytes(), StandardCharsets.UTF_8);
        if (!body.contains("name") || !body.contains("description") || !body.contains("status") || !body.contains("startTime")) {
            return Optional.empty();
        }
        Task task = gson.fromJson(body, Task.class);

        return Optional.of(task);
    }

}
