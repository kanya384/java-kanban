package task;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;

class SubTaskTest {
    @Test
    void shouldBeEqualSubTasksWithSameId() {
        SubTask subTask1 = new SubTask("test", "test", Status.NEW, LocalDateTime.of(2024, Month.JUNE, 19, 10, 20), Duration.ofMinutes(1), 1);
        subTask1.setId(1);

        SubTask subTask2 = new SubTask("test", "test", Status.NEW, LocalDateTime.of(2024, Month.JUNE, 19, 10, 20), Duration.ofMinutes(1), 1);
        subTask2.setId(1);

        Assertions.assertEquals(subTask1, subTask2);
    }
}