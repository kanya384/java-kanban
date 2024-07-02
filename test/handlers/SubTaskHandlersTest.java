package handlers;

import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.Status;
import task.SubTask;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SubTaskHandlersTest extends BaseHandlerTest {
    String basePath;

    public SubTaskHandlersTest() throws IOException {
        this.basePath = "subtasks";
    }

    @Test
    public void shouldAdd() throws IOException, InterruptedException {
        Epic epic = new Epic("test", "test");
        assertDoesNotThrow(() -> {
            manager.createEpic(epic);
        });
        SubTask subTask = new SubTask("Test 2", "Testing subTask 2",
                Status.NEW, LocalDateTime.now(), Duration.ofMinutes(5), epic.getId());

        String subTaskJson = gson.toJson(subTask);

        URI url = URI.create(String.format("http://localhost:8080/%s", basePath));
        HttpResponse<String> response = sendHttpRequest(url, "POST", subTaskJson);

        assertEquals(201, response.statusCode());

        List<SubTask> subTasksFromManager = manager.getSubTasks();

        assertNotNull(subTasksFromManager, "Подзадачи не возвращаются");
        assertEquals(1, subTasksFromManager.size(), "Некорректное количество подзадач");
        assertEquals("Test 2", subTasksFromManager.getFirst().getName(), "Некорректное имя подзадачи");
    }

    @Test
    public void shouldUpdate() throws IOException, InterruptedException {
        Epic epic = new Epic("test", "test");
        assertDoesNotThrow(() -> {
            manager.createEpic(epic);
        });
        SubTask subTask = new SubTask("Test 2", "Testing subTask 2",
                Status.NEW, LocalDateTime.now(), Duration.ofMinutes(5), epic.getId());

        assertDoesNotThrow(() -> {
            manager.createSubTask(subTask);
        });

        SubTask updatedSubTask = new SubTask(subTask);
        String newSubTaskName = "new name";
        updatedSubTask.setName(newSubTaskName);

        String updateSubTaskJson = gson.toJson(updatedSubTask);

        URI url = URI.create(String.format("http://localhost:8080/%s", basePath));
        HttpResponse<String> response = sendHttpRequest(url, "POST", updateSubTaskJson);

        assertEquals(200, response.statusCode());

        assertDoesNotThrow(() -> {
            SubTask subTaskFromManagerAfterUpdateRequest = manager.getSubTaskById(subTask.getId());
            assertNotNull(subTaskFromManagerAfterUpdateRequest, "Подзадачи не возвращаются");
            assertEquals(newSubTaskName, subTaskFromManagerAfterUpdateRequest.getName(), "Подзадача не была обновленна");
        });
    }

    @Test
    public void shouldNotAddIfIntersectsWithExisting() throws IOException, InterruptedException {
        Epic epic = new Epic("test", "test");
        assertDoesNotThrow(() -> {
            manager.createEpic(epic);
        });
        SubTask subTask = new SubTask("Test 2", "Testing subTask 2",
                Status.NEW, LocalDateTime.now(), Duration.ofMinutes(5), epic.getId());

        assertDoesNotThrow(() -> {
            SubTask subTaskClone = new SubTask(subTask);
            manager.createSubTask(subTaskClone);
        });

        String subTaskJson = gson.toJson(subTask);

        URI url = URI.create(String.format("http://localhost:8080/%s", basePath));
        HttpResponse<String> response = sendHttpRequest(url, "POST", subTaskJson);

        assertEquals(406, response.statusCode());
    }

    @Test
    public void shouldReturnListFromManager() throws IOException, InterruptedException {
        Epic epic = new Epic("test", "test");
        assertDoesNotThrow(() -> {
            manager.createEpic(epic);
        });
        SubTask subTask = new SubTask("Test 1", "Testing subTask 1",
                Status.NEW, LocalDateTime.now(), Duration.ofMinutes(5), epic.getId());
        SubTask subTask2 = new SubTask("Test 1", "Testing subTask 1",
                Status.NEW, LocalDateTime.now().plus(Duration.ofMinutes(10)), Duration.ofMinutes(5), epic.getId());

        assertDoesNotThrow(() -> {
            manager.createSubTask(subTask);
            manager.createSubTask(subTask2);
        });


        URI url = URI.create(String.format("http://localhost:8080/%s", basePath));
        HttpResponse<String> response = sendHttpRequest(url, "GET", "");

        assertEquals(200, response.statusCode());

        List<SubTask> subTasksFromRequest = gson.fromJson(response.body(), new SubTaskListTypeToken().getType());

        assertArrayEquals(manager.getSubTasks().toArray(), subTasksFromRequest.toArray(), "Списки подзадач не совпадают");
    }

    @Test
    public void shouldGetById() throws IOException, InterruptedException {
        Epic epic = new Epic("test", "test");
        assertDoesNotThrow(() -> {
            manager.createEpic(epic);
        });
        SubTask subTask = new SubTask("Test 2", "Testing subTask 2",
                Status.NEW, LocalDateTime.now(), Duration.ofMinutes(5), epic.getId());

        assertDoesNotThrow(() -> {
            manager.createSubTask(subTask);
        });

        URI url = URI.create(String.format("http://localhost:8080/%s/%d", basePath, subTask.getId()));
        HttpResponse<String> response = sendHttpRequest(url, "GET", "");

        assertEquals(200, response.statusCode());

        SubTask subTaskFromRequest = gson.fromJson(response.body(), SubTask.class);

        assertEquals(subTask, subTaskFromRequest, "Подзадачи не совпадают");
    }

    @Test
    public void shouldBe404IfRequestedNotExisting() throws IOException, InterruptedException {
        URI url = URI.create(String.format("http://localhost:8080/%s/%d", basePath, 1));
        HttpResponse<String> response = sendHttpRequest(url, "GET", "");

        assertEquals(404, response.statusCode());
    }


    @Test
    public void shouldDeleteById() throws IOException, InterruptedException {
        Epic epic = new Epic("test", "test");
        assertDoesNotThrow(() -> {
            manager.createEpic(epic);
        });
        SubTask subTask = new SubTask("Test 2", "Testing subTask 2",
                Status.NEW, LocalDateTime.now(), Duration.ofMinutes(5), epic.getId());

        assertDoesNotThrow(() -> {
            manager.createSubTask(subTask);
        });

        URI url = URI.create(String.format("http://localhost:8080/%s/%d", basePath, subTask.getId()));
        HttpResponse<String> response = sendHttpRequest(url, "DELETE", "");

        assertEquals(200, response.statusCode());

        List<SubTask> subTasks = manager.getSubTasks();

        assertEquals(subTasks.size(), 0, "Подзадача не удалена");
    }

    class SubTaskListTypeToken extends TypeToken<List<SubTask>> {
        // здесь ничего не нужно реализовывать
    }
}
