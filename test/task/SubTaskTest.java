package task;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SubTaskTest {
    @Test
    void shouldBeEqualSubTasksWithSameId() {
        SubTask subTask1 = new SubTask("test", "test", Status.NEW, 1);
        subTask1.setId(1);

        SubTask subTask2 = new SubTask("test", "test", Status.NEW, 1);
        subTask2.setId(1);

        Assertions.assertEquals(subTask1, subTask2);
    }
}