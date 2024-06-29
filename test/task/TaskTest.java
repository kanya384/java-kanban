package task;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;

class TaskTest {

    @Test
    void shouldBeEqualTasksWithSameId() {
        Task task1 = new Task("test", "test", Status.NEW, LocalDateTime.of(2024, Month.JUNE, 19, 10, 20), Duration.ofMinutes(1));
        task1.setId(1);

        Task task2 = new Task("test", "test", Status.NEW, LocalDateTime.of(2024, Month.JUNE, 19, 10, 20), Duration.ofMinutes(1));
        task2.setId(1);

        Assertions.assertEquals(task1, task2);
    }
}