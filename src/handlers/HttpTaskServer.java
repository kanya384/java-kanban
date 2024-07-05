package handlers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.sun.net.httpserver.HttpServer;
import task.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class HttpTaskServer {
    private int port = 8080;
    private static HttpServer httpServer;
    private final TaskManager taskManager;
    private final Gson gson;

    public HttpTaskServer(int port, TaskManager taskManager) {
        this.port = port;
        this.taskManager = taskManager;
        this.gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .create();
    }

    public void start() {
        try {
            httpServer = HttpServer.create(new InetSocketAddress(port), 0);
            initRoutes();
            httpServer.start();
        } catch (IOException e) {
            System.out.println(e);
            throw new RuntimeException(e);
        }

    }

    public static void stop() {
        httpServer.stop(0);
    }

    public Gson getGson() {
        return this.gson;
    }

    private void initRoutes() {
        httpServer.createContext("/tasks", new TaskHandlers(gson, taskManager, "tasks"));
        httpServer.createContext("/subtasks", new SubTaskHandlers(gson, taskManager, "subtasks"));
        httpServer.createContext("/epics", new EpicHandlers(gson, taskManager, "epics"));
        UserHandler userHandlers = new UserHandler(gson, taskManager);
        httpServer.createContext("/history", userHandlers);
        httpServer.createContext("/prioritized", userHandlers);
    }

    class LocalDateAdapter extends TypeAdapter<LocalDateTime> {
        private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

        @Override
        public void write(final JsonWriter jsonWriter, final LocalDateTime localDate) throws IOException {
            if (localDate == null) {
                jsonWriter.nullValue();
                return;
            }
            jsonWriter.value(localDate.format(dtf));
        }

        @Override
        public LocalDateTime read(final JsonReader jsonReader) throws IOException {
            String dateString = jsonReader.nextString();
            if (dateString.isBlank()) {
                return null;
            }

            return LocalDateTime.parse(dateString, dtf);
        }
    }

    class DurationAdapter extends TypeAdapter<Duration> {
        @Override
        public void write(final JsonWriter jsonWriter, final Duration duration) throws IOException {
            if (duration == null) {
                jsonWriter.nullValue();
                return;
            }

            jsonWriter.value(duration.toMinutes());
        }

        @Override
        public Duration read(final JsonReader jsonReader) throws IOException {
            String durationString = jsonReader.nextString();
            if (durationString.isBlank()) {
                return null;
            }

            return Duration.ofMinutes(Integer.parseInt(durationString));
        }
    }
}
