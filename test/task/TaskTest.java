package task;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class TaskTest {

    @Test
    void shouldBeEqualTasksWithSameId() {
        Task task1 = new Task("test", "test", Status.NEW);
        task1.setId(1);

        Task task2 = new Task("test", "test", Status.NEW);
        task2.setId(1);

        Assertions.assertEquals(task1, task2);
    }
}