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

public class EpicHandlersTest extends BaseHandlerTest {
    String basePath;
    
    public EpicHandlersTest() throws IOException {
        this.basePath = "epics";
    }

    @Test
    public void shouldAdd() throws IOException, InterruptedException {
        Epic epic = new Epic("Test 2", "Testing epic 2");

        String epicJson = gson.toJson(epic);

        URI url = URI.create(String.format("http://localhost:8080/%s", basePath));
        HttpResponse<String> response = sendHttpRequest(url, "POST", epicJson);

        assertEquals(201, response.statusCode());

        List<Epic> epicsFromManager = manager.getEpics();

        assertNotNull(epicsFromManager, "Эпики не возвращаются");
        assertEquals(1, epicsFromManager.size(), "Некорректное количество эпиков");
        assertEquals("Test 2", epicsFromManager.getFirst().getName(), "Некорректное имя эпика");
    }

    @Test
    public void shouldUpdate() throws IOException, InterruptedException {
        Epic epic = new Epic("Test 2", "Testing epic 2");

        assertDoesNotThrow(() -> {
            manager.createEpic(epic);
        });

        Epic updatedEpic = new Epic(epic);
        String newEpicName = "new name";
        updatedEpic.setName(newEpicName);

        String updateEpicJson = gson.toJson(updatedEpic);

        URI url = URI.create(String.format("http://localhost:8080/%s", basePath));
        HttpResponse<String> response = sendHttpRequest(url, "POST", updateEpicJson);

        assertEquals(200, response.statusCode());

        assertDoesNotThrow(() -> {
            Epic epicFromManagerAfterUpdateRequest = manager.getEpicById(epic.getId());
            assertNotNull(epicFromManagerAfterUpdateRequest, "Эпики не возвращаются");
            assertEquals(newEpicName, epicFromManagerAfterUpdateRequest.getName(), "Эпик не был обновленн");
        });
    }

    @Test
    public void shouldReturnListFromManager() throws IOException, InterruptedException {
        Epic epic = new Epic("Test 1", "Testing epic 1");
        Epic epic2 = new Epic("Test 1", "Testing epic 1");

        assertDoesNotThrow(() -> {
            manager.createEpic(epic);
            manager.createEpic(epic2);
        });


        URI url = URI.create(String.format("http://localhost:8080/%s", basePath));
        HttpResponse<String> response = sendHttpRequest(url, "GET", "");

        assertEquals(200, response.statusCode());

        List<Epic> epicsFromRequest = gson.fromJson(response.body(), new EpicListTypeToken().getType());

        assertArrayEquals(manager.getEpics().toArray(), epicsFromRequest.toArray(), "Списки эпиков не совпадают");
    }

    @Test
    public void shouldGetById() throws IOException, InterruptedException {
        Epic epic = new Epic("Test 2", "Testing epic 2");

        assertDoesNotThrow(() -> {
            manager.createEpic(epic);
        });


        URI url = URI.create(String.format("http://localhost:8080/%s/%d", basePath, epic.getId()));
        HttpResponse<String> response = sendHttpRequest(url, "GET", "");

        assertEquals(200, response.statusCode());

        Epic epicFromRequest = gson.fromJson(response.body(), Epic.class);

        assertEquals(epic, epicFromRequest, "Эпики не совпадают");
    }

    @Test
    public void shouldBe404IfRequestedNotExisting() throws IOException, InterruptedException {
        URI url = URI.create(String.format("http://localhost:8080/%s/%d", basePath, 1));
        HttpResponse<String> response = sendHttpRequest(url, "GET", "");

        assertEquals(404, response.statusCode());
    }


    @Test
    public void shouldDeleteById() throws IOException, InterruptedException {
        Epic epic = new Epic("Test 2", "Testing epic 2");

        assertDoesNotThrow(() -> {
            manager.createEpic(epic);
        });

        URI url = URI.create(String.format("http://localhost:8080/%s/%d", basePath, 1));
        HttpResponse<String> response = sendHttpRequest(url, "DELETE", "");

        assertEquals(200, response.statusCode());

        List<Epic> epics = manager.getEpics();

        assertEquals(epics.size(), 0, "Задачи не удалена");
    }

    @Test
    void shouldReturnSubTasksListOfEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Test 1", "Testing epic 1");
        assertDoesNotThrow(() -> {
            manager.createEpic(epic);

            SubTask subTask = new SubTask("Test 1", "Testing subTask 1",
                    Status.NEW, LocalDateTime.now(), Duration.ofMinutes(5), epic.getId());
            SubTask subTask2 = new SubTask("Test 1", "Testing subTask 1",
                    Status.NEW, LocalDateTime.now().plus(Duration.ofMinutes(10)), Duration.ofMinutes(5), epic.getId());

            manager.createSubTask(subTask);
            manager.createSubTask(subTask2);
        });


        URI url = URI.create(String.format("http://localhost:8080/%s/%d/subtasks", basePath, epic.getId()));
        HttpResponse<String> response = sendHttpRequest(url, "GET", "");

        assertEquals(200, response.statusCode());

        List<Epic> subTasksFromRequest = gson.fromJson(response.body(), new SubTaskListTypeToken().getType());

        assertArrayEquals(manager.getSubTasks().toArray(), subTasksFromRequest.toArray(), "Списки подзадача не совпадают");
    }

    class EpicListTypeToken extends TypeToken<List<Epic>> {
        // здесь ничего не нужно реализовывать
    }

    class SubTaskListTypeToken extends TypeToken<List<SubTask>> {
        // здесь ничего не нужно реализовывать
    }
}
