package handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import task.TaskManager;

import java.io.IOException;

public class UserHandler extends BaseHttpHandler implements HttpHandler {
    protected TaskManager taskManager;

    public UserHandler(Gson gson, TaskManager taskManager) {
        super(gson);
        this.taskManager = taskManager;
    }

    public void handle(HttpExchange exchange) throws IOException {
        Endpoint endpoint = getEndpoint(exchange.getRequestURI().getPath(), exchange.getRequestMethod());

        switch (endpoint) {
            case GET_HISTORY -> handleHistory(exchange);

            case GET_PRIORITIZED -> handlePrioritized(exchange);

            case UNKNOWN -> {
                sendStatusOnly(exchange, 404);
            }
        }
    }

    protected Endpoint getEndpoint(String requestPath, String requestMethod) {
        String[] pathParts = requestPath.split("/");
        String lastPathPart = pathParts[pathParts.length - 1];

        if (!requestMethod.equals("GET")) {
            return Endpoint.UNKNOWN;
        }

        if (lastPathPart.equals("history")) {
            return Endpoint.GET_HISTORY;
        }


        return Endpoint.GET_PRIORITIZED;
    }

    void handleHistory(HttpExchange exchange) throws IOException {
        sendText(exchange, 200, gson.toJson(taskManager.getHistory()));
    }

    void handlePrioritized(HttpExchange exchange) throws IOException {
        sendText(exchange, 200, gson.toJson(taskManager.getPrioritizedTasks()));
    }

    protected enum Endpoint {
        GET_HISTORY,
        GET_PRIORITIZED,

        UNKNOWN
    }
}
