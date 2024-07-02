package task;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

abstract class TaskManagerTest<T extends TaskManager> {
    protected T taskManager;

    @Test
    void addNewTask() {
        assertDoesNotThrow(() -> {
            Task task = new Task("Test addNewTask", "Test addNewTask description", Status.NEW, LocalDateTime.of(2024, Month.JUNE, 19, 10, 20), Duration.ofMinutes(1));
            int taskId = taskManager.createTask(task);

            final Task savedTask = taskManager.getTaskById(taskId);

            assertNotNull(savedTask, "Задача не найдена.");
            assertEquals(task, savedTask, "Задачи не совпадают.");

            final List<Task> tasks = taskManager.getTasks();

            assertNotNull(tasks, "Задачи не возвращаются.");
            assertEquals(1, tasks.size(), "Неверное количество задач.");
            assertEquals(task, tasks.getFirst(), "Задачи не совпадают.");
        });
    }

    @Test
    void addNewEpic() {
        assertDoesNotThrow(() -> {
            Epic epic = new Epic("Test addNewEpic", "Test addNewEpic description");
            int epicId = taskManager.createEpic(epic);

            final Epic savedTask = taskManager.getEpicById(epicId);

            assertNotNull(savedTask, "Задача не найдена.");
            assertEquals(epic, savedTask, "Задачи не совпадают.");

            final List<Epic> epics = taskManager.getEpics();

            assertNotNull(epics, "Задачи не возвращаются.");
            assertEquals(1, epics.size(), "Неверное количество задач.");
            assertEquals(epic, epics.getFirst(), "Задачи не совпадают.");
        });
    }

    @Test
    void addNewSubTask() {
        assertDoesNotThrow(() -> {
            Epic epic = new Epic("Test addNewEpic", "Test addNewEpic description");
            int epicId = taskManager.createEpic(epic);

            SubTask subTask = new SubTask("Test addNewSubTAsk", "Test addNewSubTAsk description", Status.NEW, LocalDateTime.of(2024, Month.JUNE, 19, 10, 20), Duration.ofMinutes(1), epicId);
            int subTaskId = taskManager.createSubTask(subTask);

            final SubTask savedTask = taskManager.getSubTaskById(subTaskId);

            assertNotNull(savedTask, "Задача не найдена.");
            assertEquals(subTask, savedTask, "Задачи не совпадают.");

            final List<SubTask> subTasks = taskManager.getSubTasks();

            assertNotNull(subTasks, "Задачи не возвращаются.");
            assertEquals(1, subTasks.size(), "Неверное количество задач.");
            assertEquals(subTask, subTasks.getFirst(), "Задачи не совпадают.");
        });
    }

    @Test
    void shouldReturnEpicById() {
        assertDoesNotThrow(() -> {
            Epic epic = new Epic("test", "test");
            taskManager.createEpic(epic);

            Epic epic2 = taskManager.getEpicById(epic.getId());

            Assertions.assertEquals(epic, epic2);
        });
    }

    @Test
    void shouldReturnTaskById() {
        assertDoesNotThrow(() -> {
            Task task = new Task("test", "test", Status.NEW, LocalDateTime.of(2024, Month.JUNE, 19, 10, 20), Duration.ofMinutes(1));
            taskManager.createTask(task);

            Task task2 = taskManager.getTaskById(task.getId());

            Assertions.assertEquals(task, task2);
        });
    }

    @Test
    void shouldReturnSubTaskById() {
        assertDoesNotThrow(() -> {
            Epic epic = new Epic("test", "test");
            taskManager.createEpic(epic);

            SubTask subTask = new SubTask("test", "test", Status.NEW, LocalDateTime.of(2024, Month.JUNE, 19, 10, 20), Duration.ofMinutes(1), 1);
            taskManager.createSubTask(subTask);

            SubTask subTask2 = taskManager.getSubTaskById(subTask.getId());

            Assertions.assertEquals(subTask, subTask2);
        });
    }

    @Test
    void shouldUpdateIdsOnCreate() {
        Epic epic = new Epic("test", "test");
        epic.setId(0);
        taskManager.createEpic(epic);

        Epic epic2 = new Epic("test", "test");
        epic.setId(0);
        taskManager.createEpic(epic);

        Assertions.assertNotEquals(epic.getId(), epic2.getId());
    }

    @Test
    void shouldEqualTaskFieldsBeforeAndAfterCreate() {
        assertDoesNotThrow(() -> {
            Task oldTask = new Task("test", "test", Status.NEW, LocalDateTime.of(2024, Month.JUNE, 19, 10, 20), Duration.ofMinutes(1));
            taskManager.createTask(oldTask);

            Task newTask = taskManager.getTaskById(oldTask.getId());


            Assertions.assertEquals(oldTask.getName(), newTask.getName());
            Assertions.assertEquals(oldTask.getDescription(), newTask.getDescription());
            Assertions.assertEquals(oldTask.getStatus(), newTask.getStatus());
            Assertions.assertEquals(oldTask.getClass(), newTask.getClass());
        });
    }

    @Test
    void shouldNotSubtasksIdEqualToEpicId() {
        assertDoesNotThrow(() -> {
            Epic epic = new Epic("test", "test");
            taskManager.createEpic(epic);

            SubTask subTask = new SubTask("test", "test", Status.NEW, LocalDateTime.of(2024, Month.JUNE, 19, 10, 20), Duration.ofMinutes(1), 1);
            taskManager.createSubTask(subTask);

            List<SubTask> subTasksBeforeUpdate = taskManager.getSubTasks();

            subTask.setId(1);
            taskManager.updateSubTask(subTask);

            List<SubTask> subTasksAfterUpdate = taskManager.getSubTasks();

            Assertions.assertArrayEquals(subTasksBeforeUpdate.toArray(), subTasksAfterUpdate.toArray());
        });
    }

    @Test
    void shouldNotEpicContainRemovedTaskId() {
        assertDoesNotThrow(() -> {
            Epic epic = new Epic("test", "test");
            taskManager.createEpic(epic);

            SubTask subTask = new SubTask("test", "test", Status.NEW, LocalDateTime.of(2024, Month.JUNE, 19, 10, 20), Duration.ofMinutes(1), 1);
            taskManager.createSubTask(subTask);

            taskManager.deleteSubTaskById(subTask.getId());

            ArrayList<Integer> subTasksList = taskManager.getEpicById(epic.getId()).getSubTaskIds();
            Assertions.assertEquals(subTasksList.size(), 0);
        });
    }

    @Test
    void shouldReturnTrueWhenTaskIncludesAnotherTask() {
        Task task1 = new Task("task1", "task1", Status.NEW, LocalDateTime.of(2024, 06, 19, 15, 00), Duration.ofMinutes(10));

        Task task2 = new Task("task2", "task2", Status.NEW, LocalDateTime.of(2024, 06, 19, 15, 00), Duration.ofMinutes(2));

        boolean isIntersected = taskManager.isIntersectedTasks(task1, task2);

        Assertions.assertTrue(isIntersected);

        isIntersected = taskManager.isIntersectedTasks(task2, task1);

        Assertions.assertTrue(isIntersected);
    }

    @Test
    void shouldReturnTrueWhenTaskInctersectsAnotherTask() {
        Task task1 = new Task("task1", "task1", Status.NEW, LocalDateTime.of(2024, 06, 19, 15, 00), Duration.ofMinutes(10));

        Task task2 = new Task("task2", "task2", Status.NEW, LocalDateTime.of(2024, 06, 19, 15, 05), Duration.ofMinutes(15));

        boolean isIntersected = taskManager.isIntersectedTasks(task1, task2);

        Assertions.assertTrue(isIntersected);

        isIntersected = taskManager.isIntersectedTasks(task2, task1);

        Assertions.assertTrue(isIntersected);
    }

    @Test
    void shouldBeFalseWhenTasksRangesAreNotIntersects() {
        Task task1 = new Task("task1", "task1", Status.NEW, LocalDateTime.of(2024, 06, 19, 15, 00), Duration.ofMinutes(10));

        Task task2 = new Task("task2", "task2", Status.NEW, LocalDateTime.of(2024, 06, 19, 15, 10), Duration.ofMinutes(15));

        boolean isIntersected = taskManager.isIntersectedTasks(task1, task2);

        Assertions.assertFalse(isIntersected);

        isIntersected = taskManager.isIntersectedTasks(task2, task1);

        Assertions.assertFalse(isIntersected);
    }

    @Test
    void shouldReturnNewIfAllSubTasksOfEpicAreNew() {
        assertDoesNotThrow(() -> {
            Epic epic = new Epic("test", "test");
            int epicId = taskManager.createEpic(epic);

            SubTask subTask = new SubTask("test", "test", Status.NEW, LocalDateTime.of(2024, Month.JUNE, 19, 10, 20), Duration.ofMinutes(1), epicId);
            taskManager.createSubTask(subTask);

            SubTask subTask2 = new SubTask("test", "test", Status.NEW, LocalDateTime.of(2024, Month.JUNE, 19, 10, 50), Duration.ofMinutes(1), epicId);
            taskManager.createSubTask(subTask2);

            epic = taskManager.getEpicById(epicId);

            assertEquals(epic.getStatus(), Status.NEW);
        });
    }

    @Test
    void shouldReturnDoneIfAllSubTasksOfEpicAreDone() {
        assertDoesNotThrow(() -> {
            Epic epic = new Epic("test", "test");
            int epicId = taskManager.createEpic(epic);

            SubTask subTask = new SubTask("test", "test", Status.DONE, LocalDateTime.of(2024, Month.JUNE, 19, 10, 20), Duration.ofMinutes(1), epicId);
            taskManager.createSubTask(subTask);

            SubTask subTask2 = new SubTask("test", "test", Status.DONE, LocalDateTime.of(2024, Month.JUNE, 19, 10, 50), Duration.ofMinutes(1), epicId);
            taskManager.createSubTask(subTask2);

            epic = taskManager.getEpicById(epicId);

            assertEquals(epic.getStatus(), Status.DONE);
        });
    }

    @Test
    void shouldReturnInProgressIfSubTasksOfEpicInNewAndDoneStatus() {
        assertDoesNotThrow(() -> {
            Epic epic = new Epic("test", "test");
            int epicId = taskManager.createEpic(epic);

            SubTask subTask = new SubTask("test", "test", Status.NEW, LocalDateTime.of(2024, Month.JUNE, 19, 10, 20), Duration.ofMinutes(1), epicId);
            taskManager.createSubTask(subTask);

            SubTask subTask2 = new SubTask("test", "test", Status.DONE, LocalDateTime.of(2024, Month.JUNE, 19, 10, 50), Duration.ofMinutes(1), epicId);
            taskManager.createSubTask(subTask2);

            epic = taskManager.getEpicById(epicId);

            assertEquals(epic.getStatus(), Status.IN_PROGRESS);
        });
    }

    @Test
    void shouldReturnInProgressIfSubTasksOfEpicInProgressStatus() {
        assertDoesNotThrow(() -> {
            Epic epic = new Epic("test", "test");
            int epicId = taskManager.createEpic(epic);

            SubTask subTask = new SubTask("test", "test", Status.IN_PROGRESS, LocalDateTime.of(2024, Month.JUNE, 19, 10, 20), Duration.ofMinutes(1), epicId);
            taskManager.createSubTask(subTask);

            SubTask subTask2 = new SubTask("test", "test", Status.IN_PROGRESS, LocalDateTime.of(2024, Month.JUNE, 19, 10, 50), Duration.ofMinutes(1), epicId);
            taskManager.createSubTask(subTask2);

            epic = taskManager.getEpicById(epicId);

            assertEquals(epic.getStatus(), Status.IN_PROGRESS);
        });
    }

    @Test
    void shouldThrowExceptionOnSubTaskCreateIfNoParentEpicExists() {
        int epicId = 1;
        assertThrows(NoEpicException.class, () -> {
            SubTask subTask = new SubTask("test", "test", Status.NEW, LocalDateTime.of(2024, Month.JUNE, 19, 10, 20), Duration.ofMinutes(1), epicId);
            taskManager.createSubTask(subTask);
        }, String.format("no epic with id: %d", epicId));
    }

    @Test
    void shouldThrowExceptionOnIntersectTaskTimes() {
        assertThrows(IntersectsExistingTaskException.class, () -> {
            Task task1 = new Task("test", "test", Status.NEW, LocalDateTime.of(2024, Month.JUNE, 19, 10, 20), Duration.ofMinutes(20));
            Task task2 = new Task("test", "test", Status.NEW, LocalDateTime.of(2024, Month.JUNE, 19, 10, 25), Duration.ofMinutes(1));
            taskManager.createTask(task1);
            taskManager.createTask(task2);
        });
    }

    @Test
    void shouldThrowNothingOnNoIntersectedTaskTimes() {
        assertDoesNotThrow(() -> {
            Task task1 = new Task("test", "test", Status.NEW, LocalDateTime.of(2024, Month.JUNE, 19, 10, 20), Duration.ofMinutes(20));
            Task task2 = new Task("test", "test", Status.NEW, LocalDateTime.of(2024, Month.JUNE, 19, 10, 50), Duration.ofMinutes(1));
            taskManager.createTask(task1);
            taskManager.createTask(task2);
        });
    }
}
