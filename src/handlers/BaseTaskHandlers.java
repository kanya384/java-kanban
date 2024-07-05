package handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import task.TaskManager;

import java.io.IOException;

abstract class BaseTaskHandlers extends BaseHttpHandler implements HttpHandler {
    protected TaskManager taskManager;
    protected String basePath;

    BaseTaskHandlers(Gson gson, TaskManager taskManager, String basePath) {
        super(gson);
        this.taskManager = taskManager;
        this.basePath = basePath;
    }

    public void handle(HttpExchange exchange) throws IOException {
        TaskHandlers.Endpoint endpoint = getEndpoint(exchange.getRequestURI().getPath(), exchange.getRequestMethod());

        switch (endpoint) {
            case GET_LIST -> handleList(exchange);

            case GET_BY_ID -> handleById(exchange);

            case CREATE_OR_UPDATE -> handleCreateOrUpdate(exchange);

            case DELETE -> handleDelete(exchange);

            case GET_SUBTASKS_OF_EPIC -> handleEpicSubtasks(exchange);

            case UNKNOWN -> {
                sendStatusOnly(exchange, 404);
            }
        }
    }

    protected TaskHandlers.Endpoint getEndpoint(String requestPath, String requestMethod) {
        String[] pathParts = requestPath.split("/");
        String lastPathPart = pathParts[pathParts.length - 1];
        switch (requestMethod) {
            case "GET" -> {
                if (lastPathPart.equals(basePath)) {
                    return TaskHandlers.Endpoint.GET_LIST;
                } else if (lastPathPart.equals("subtasks") && pathParts[1].equals(basePath)) {
                    return TaskHandlers.Endpoint.GET_SUBTASKS_OF_EPIC;
                } else {
                    return TaskHandlers.Endpoint.GET_BY_ID;
                }
            }

            case "POST" -> {
                return TaskHandlers.Endpoint.CREATE_OR_UPDATE;
            }

            case "DELETE" -> {
                return TaskHandlers.Endpoint.DELETE;
            }
        }

        return TaskHandlers.Endpoint.UNKNOWN;
    }

    abstract void handleList(HttpExchange exchange) throws IOException;

    abstract void handleById(HttpExchange exchange) throws IOException;

    abstract void handleCreateOrUpdate(HttpExchange exchange) throws IOException;

    abstract void handleDelete(HttpExchange exchange) throws IOException;

    protected void handleEpicSubtasks(HttpExchange exchange) throws IOException {
        sendStatusOnly(exchange, 404);
    }

    protected enum Endpoint {
        GET_LIST,
        GET_BY_ID,
        GET_SUBTASKS_OF_EPIC,
        CREATE_OR_UPDATE,
        DELETE,

        UNKNOWN
    }
}
