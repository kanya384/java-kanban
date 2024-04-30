package task;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ManagersTest {

    @Test
    void shouldNotBeNullDefaultTaskManager() {
        TaskManager taskManager = Managers.getDefault();

        Assertions.assertNotNull(taskManager);
    }

    @Test
    void shouldNotBeNullHistoryManager() {
        InMemoryHistoryManager historyManager = Managers.getDefaultHistory();

        Assertions.assertNotNull(historyManager);
    }
}