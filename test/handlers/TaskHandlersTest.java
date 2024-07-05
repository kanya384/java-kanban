package handlers;

import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.Test;
import task.Status;
import task.Task;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TaskHandlersTest extends BaseHandlerTest {
    String basePath;

    public TaskHandlersTest() throws IOException {
        this.basePath = "tasks";
    }

    @Test
    public void shouldAdd() throws IOException, InterruptedException {
        Task task = new Task("Test 2", "Testing task 2",
                Status.NEW, LocalDateTime.now(), Duration.ofMinutes(5));

        String taskJson = gson.toJson(task);

        URI url = URI.create(String.format("http://localhost:8080/%s", basePath));
        HttpResponse<String> response = sendHttpRequest(url, "POST", taskJson);

        assertEquals(201, response.statusCode());

        List<Task> tasksFromManager = manager.getTasks();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Test 2", tasksFromManager.getFirst().getName(), "Некорректное имя задачи");
    }

    @Test
    public void shouldUpdate() throws IOException, InterruptedException {
        Task task = new Task("Test 2", "Testing task 2",
                Status.NEW, LocalDateTime.now(), Duration.ofMinutes(5));

        assertDoesNotThrow(() -> {
            manager.createTask(task);
        });

        Task updatedTask = new Task(task);
        String newTaskName = "new name";
        updatedTask.setName(newTaskName);

        String updateTaskJson = gson.toJson(updatedTask);

        URI url = URI.create(String.format("http://localhost:8080/%s", basePath));
        HttpResponse<String> response = sendHttpRequest(url, "POST", updateTaskJson);

        assertEquals(200, response.statusCode());

        assertDoesNotThrow(() -> {
            Task taskFromManagerAfterUpdateRequest = manager.getTaskById(task.getId());
            assertNotNull(taskFromManagerAfterUpdateRequest, "Задачи не возвращаются");
            assertEquals(newTaskName, taskFromManagerAfterUpdateRequest.getName(), "Задача не была обновленна");
        });
    }

    @Test
    void shouldNotAddIfIntersectsWithExisting() throws IOException, InterruptedException {
        Task task = new Task("Test 2", "Testing task 2",
                Status.NEW, LocalDateTime.now(), Duration.ofMinutes(5));

        assertDoesNotThrow(() -> {
            Task taskClone = new Task(task);
            manager.createTask(taskClone);
        });

        String taskJson = gson.toJson(task);

        URI url = URI.create(String.format("http://localhost:8080/%s", basePath));
        HttpResponse<String> response = sendHttpRequest(url, "POST", taskJson);

        assertEquals(406, response.statusCode());
    }

    @Test
    public void shouldReturnListFromManager() throws IOException, InterruptedException {
        Task task = new Task("Test 1", "Testing task 1",
                Status.NEW, LocalDateTime.now(), Duration.ofMinutes(5));
        Task task2 = new Task("Test 1", "Testing task 1",
                Status.NEW, LocalDateTime.now().plus(Duration.ofMinutes(10)), Duration.ofMinutes(5));

        assertDoesNotThrow(() -> {
            manager.createTask(task);
            manager.createTask(task2);
        });


        URI url = URI.create(String.format("http://localhost:8080/%s", basePath));
        HttpResponse<String> response = sendHttpRequest(url, "GET", "");

        assertEquals(200, response.statusCode());

        List<Task> tasksFromRequest = gson.fromJson(response.body(), new TaskListTypeToken().getType());

        assertArrayEquals(manager.getTasks().toArray(), tasksFromRequest.toArray(), "Списки задач не совпадают");
    }

    @Test
    public void shouldGetById() throws IOException, InterruptedException {
        Task task = new Task("Test 2", "Testing task 2",
                Status.NEW, LocalDateTime.now(), Duration.ofMinutes(5));

        assertDoesNotThrow(() -> {
            manager.createTask(task);
        });

        String taskJson = gson.toJson(task);

        URI url = URI.create(String.format("http://localhost:8080/%s/%d", basePath, task.getId()));
        HttpResponse<String> response = sendHttpRequest(url, "GET", "");

        assertEquals(200, response.statusCode());

        Task taskFromRequest = gson.fromJson(response.body(), Task.class);

        assertEquals(task, taskFromRequest, "Задачи не совпадают");
    }

    @Test
    public void shouldBe404IfRequestedNotExisting() throws IOException, InterruptedException {
        URI url = URI.create(String.format("http://localhost:8080/%s/%d", basePath, 1));
        HttpResponse<String> response = sendHttpRequest(url, "GET", "");

        assertEquals(404, response.statusCode());
    }


    @Test
    public void shouldDeleteById() throws IOException, InterruptedException {
        Task task = new Task("Test 2", "Testing task 2",
                Status.NEW, LocalDateTime.now(), Duration.ofMinutes(5));

        assertDoesNotThrow(() -> {
            manager.createTask(task);
        });

        URI url = URI.create(String.format("http://localhost:8080/%s/%d", basePath, 1));
        HttpResponse<String> response = sendHttpRequest(url, "DELETE", "");

        assertEquals(200, response.statusCode());

        List<Task> tasks = manager.getTasks();

        assertEquals(tasks.size(), 0, "Задачи не удалена");
    }

    class TaskListTypeToken extends TypeToken<List<Task>> {
        // здесь ничего не нужно реализовывать
    }
}
