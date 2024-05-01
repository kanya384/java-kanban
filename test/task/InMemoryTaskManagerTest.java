package task;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class InMemoryTaskManagerTest {
    private static TaskManager taskManager;

    @BeforeEach
    public void beforeEach() {
        taskManager = new InMemoryTaskManager();
    }

    @Test
    void addNewTask() {
        Task task = new Task("Test addNewTask", "Test addNewTask description", Status.NEW);
        int taskId = taskManager.createTask(task);

        final Task savedTask = taskManager.getTaskById(taskId);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");

        final ArrayList<Task> tasks = taskManager.getTasks();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.getFirst(), "Задачи не совпадают.");
    }

    @Test
    void addNewEpic() {
        Epic epic = new Epic("Test addNewEpic", "Test addNewEpic description");
        int epicId = taskManager.createEpic(epic);

        final Epic savedTask = taskManager.getEpicById(epicId);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(epic, savedTask, "Задачи не совпадают.");

        final ArrayList<Epic> epics = taskManager.getEpics();

        assertNotNull(epics, "Задачи не возвращаются.");
        assertEquals(1, epics.size(), "Неверное количество задач.");
        assertEquals(epic, epics.getFirst(), "Задачи не совпадают.");
    }

    @Test
    void addNewSubTask() {
        Epic epic = new Epic("Test addNewEpic", "Test addNewEpic description");
        int epicId = taskManager.createEpic(epic);

        SubTask subTask = new SubTask("Test addNewSubTAsk", "Test addNewSubTAsk description", Status.NEW, epicId);
        int subTaskId = taskManager.createSubTask(subTask);

        final SubTask savedTask = taskManager.getSubTaskById(subTaskId);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(subTask, savedTask, "Задачи не совпадают.");

        final ArrayList<SubTask> subTasks = taskManager.getSubTasks();

        assertNotNull(subTasks, "Задачи не возвращаются.");
        assertEquals(1, subTasks.size(), "Неверное количество задач.");
        assertEquals(subTask, subTasks.getFirst(), "Задачи не совпадают.");
    }

    @Test
    void shouldReturnEpicById() {
        Epic epic = new Epic("test", "test");
        taskManager.createEpic(epic);

        Epic epic2 = taskManager.getEpicById(epic.getId());

        Assertions.assertEquals(epic, epic2);
    }

    @Test
    void shouldReturnTaskById() {
        Task task = new Task("test", "test", Status.NEW);
        taskManager.createTask(task);

        Task task2 = taskManager.getTaskById(task.getId());

        Assertions.assertEquals(task, task2);
    }

    @Test
    void shouldReturnSubTaskById() {
        Epic epic = new Epic("test", "test");
        taskManager.createEpic(epic);

        SubTask subTask = new SubTask("test", "test", Status.NEW, 1);
        taskManager.createSubTask(subTask);

        SubTask subTask2 = taskManager.getSubTaskById(subTask.getId());

        Assertions.assertEquals(subTask, subTask2);
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
        Task oldTask = new Task("test", "test", Status.NEW);
        taskManager.createTask(oldTask);

        Task newTask =taskManager.getTaskById(oldTask.getId());


        Assertions.assertEquals(oldTask.getName(), newTask.getName());
        Assertions.assertEquals(oldTask.getDescription(), newTask.getDescription());
        Assertions.assertEquals(oldTask.getStatus(), newTask.getStatus());
        Assertions.assertEquals(oldTask.getClass(), newTask.getClass());
    }


    @Test
    void shouldNotSubtasksIdEqualToEpicId() {
        Epic epic = new Epic("test", "test");
        taskManager.createEpic(epic);

        SubTask subTask = new SubTask("test", "test", Status.NEW, 1);
        taskManager.createSubTask(subTask);

        ArrayList<SubTask> subTasksBeforeUpdate = taskManager.getSubTasks();

        subTask.setId(1);
        taskManager.updateSubTask(subTask);

        ArrayList<SubTask> subTasksAfterUpdate = taskManager.getSubTasks();

        Assertions.assertArrayEquals(subTasksBeforeUpdate.toArray(), subTasksAfterUpdate.toArray());
    }
}