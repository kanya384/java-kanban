package task;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class InMemoryHistoryManagerTest {

    private static InMemoryHistoryManager inMemoryHistoryManager;

    @BeforeEach
    public void beforeEach() {
        inMemoryHistoryManager = new InMemoryHistoryManager();
    }

    @Test
    void shouldReplacePreviousVersionsOfTask() {

        Task task = new Task("test", "test description", Status.NEW, LocalDateTime.of(2024, Month.JUNE, 19, 10, 20), Duration.ofMinutes(1));
        task.setId(1);

        inMemoryHistoryManager.add(task);


        task.setName("test updated");
        task.setDescription("description updated");

        inMemoryHistoryManager.add(task);

        List<Task> history = inMemoryHistoryManager.getHistory();

        assertEquals(1, history.size());

        assertEquals(history.getFirst(), task);
    }

    @Test
    void shouldReplacePreviousVersionsOfEpic() {

        Epic epic = new Epic("test", "test description");
        epic.setId(1);

        inMemoryHistoryManager.add(epic);

        epic.setName("test updated");
        epic.setDescription("description updated");

        inMemoryHistoryManager.add(epic);

        List<Task> history = inMemoryHistoryManager.getHistory();

        Assertions.assertEquals(1, history.size());

        assertEquals(history.getFirst(), epic);
    }

    @Test
    void shouldReplacePreviousVersionsOfSubTask() {

        SubTask subTask = new SubTask("test", "test description", Status.NEW, LocalDateTime.of(2024, Month.JUNE, 19, 10, 20), Duration.ofMinutes(1), 1);
        subTask.setId(1);

        inMemoryHistoryManager.add(subTask);

        subTask.setName("new name");
        subTask.setDescription("new description");

        inMemoryHistoryManager.add(subTask);

        List<Task> history = inMemoryHistoryManager.getHistory();

        assertEquals(1, history.size());

        assertEquals(history.getFirst(), subTask);
    }

    @Test
    void shouldAddTasksInRightOrder() {
        Task task = new Task("test", "test description", Status.NEW, LocalDateTime.of(2024, Month.JUNE, 19, 10, 20), Duration.ofMinutes(1));
        task.setId(1);

        inMemoryHistoryManager.add(task);

        Task task2 = new Task("test", "test description", Status.NEW, LocalDateTime.of(2024, Month.JUNE, 19, 10, 20), Duration.ofMinutes(1));
        task2.setId(2);

        inMemoryHistoryManager.add(task2);

        Task task3 = new Task("test", "test description", Status.NEW, LocalDateTime.of(2024, Month.JUNE, 19, 10, 20), Duration.ofMinutes(1));
        task3.setId(3);

        inMemoryHistoryManager.add(task3);

        List<Task> history = inMemoryHistoryManager.getHistory();

        for (int i = 0; i < history.size(); i++) {
            assertEquals(i + 1, history.get(i).getId());
        }
    }

    @Test
    void shouldRemoveTaskById() {

        Task task = new Task("test", "test description", Status.NEW, LocalDateTime.of(2024, Month.JUNE, 19, 10, 20), Duration.ofMinutes(1));
        task.setId(1);

        inMemoryHistoryManager.add(task);

        Task task2 = new Task("test", "test description", Status.NEW, LocalDateTime.of(2024, Month.JUNE, 19, 10, 20), Duration.ofMinutes(1));
        task2.setId(2);

        inMemoryHistoryManager.add(task2);

        Task task3 = new Task("test", "test description", Status.NEW, LocalDateTime.of(2024, Month.JUNE, 19, 10, 20), Duration.ofMinutes(1));
        task3.setId(3);

        inMemoryHistoryManager.add(task3);

        List<Task> history = inMemoryHistoryManager.getHistory();

        assertEquals(3, history.size());

        inMemoryHistoryManager.remove(task2.getId());

        history = inMemoryHistoryManager.getHistory();

        assertEquals(2, history.size());

        for (Task historyItem : history) {
            assertNotEquals(task2, historyItem);
        }
        ;
    }

    @Test
    void shouldBeEmptyAfterRemoveIfSingleTask() {

        Task task = new Task("test", "test description", Status.NEW, LocalDateTime.of(2024, Month.JUNE, 19, 10, 20), Duration.ofMinutes(1));
        task.setId(1);

        inMemoryHistoryManager.add(task);

        inMemoryHistoryManager.remove(task.getId());

        List<Task> history = inMemoryHistoryManager.getHistory();

        assertEquals(0, history.size());
    }

}