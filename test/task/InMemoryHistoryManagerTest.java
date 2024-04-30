package task;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    private static InMemoryHistoryManager inMemoryHistoryManager;

    @BeforeEach
    public void beforeEach() {
        inMemoryHistoryManager = new InMemoryHistoryManager();
    }

    @Test
    void shouldStorePreviousVersionsOfTask() {
        int originalId = 1;
        String originalName = "test";
        String originalDescription = "test description";
        Status originalStatus = Status.NEW;

        Task task = new Task(originalName, originalDescription, originalStatus);
        task.setId(originalId);

        inMemoryHistoryManager.add(task);

        task.setName("new");
        task.setDescription("new");

        inMemoryHistoryManager.add(task);

        ArrayList<Task> history = inMemoryHistoryManager.getHistory();

        Assertions.assertEquals(2, history.size());

        Assertions.assertEquals(history.getFirst().getId(), originalId);
        Assertions.assertEquals(history.getFirst().getName(), originalName);
        Assertions.assertEquals(history.getFirst().getDescription(), originalDescription);
        Assertions.assertEquals(history.getFirst().getStatus(), originalStatus);
    }

    @Test
    void shouldStorePreviousVersionsOfEpic() {

        int originalId = 1;
        String originalName = "test";
        String originalDescription = "test description";

        Epic epic = new Epic(originalName, originalDescription);
        epic.setId(originalId);

        inMemoryHistoryManager.add(epic);

        epic.setName("new");
        epic.setDescription("new");

        inMemoryHistoryManager.add(epic);

        ArrayList<Task> history = inMemoryHistoryManager.getHistory();

        Assertions.assertEquals(2, history.size());

        Assertions.assertEquals(history.getFirst().getId(), originalId);
        Assertions.assertEquals(history.getFirst().getName(), originalName);
        Assertions.assertEquals(history.getFirst().getDescription(), originalDescription);
    }

    @Test
    void shouldStorePreviousVersionsOfSubTask() {

        int originalId = 1;
        String originalName = "test";
        String originalDescription = "test description";

        SubTask subTask = new SubTask(originalName, originalDescription, Status.NEW, 1);
        subTask.setId(originalId);

        inMemoryHistoryManager.add(subTask);

        subTask.setName("new");
        subTask.setDescription("new");

        inMemoryHistoryManager.add(subTask);

        ArrayList<Task> history = inMemoryHistoryManager.getHistory();

        Assertions.assertEquals(2, history.size());

        Assertions.assertEquals(history.getFirst().getId(), originalId);
        Assertions.assertEquals(history.getFirst().getName(), originalName);
        Assertions.assertEquals(history.getFirst().getDescription(), originalDescription);
    }
}