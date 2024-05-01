package task;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EpicTest {
    @Test
    void shouldBeEqualEpicsWithSameId() {
        Epic epic1 = new Epic("test", "test");
        epic1.setId(1);

        Epic epic2 = new Epic("test", "test");
        epic2.setId(1);

        assertEquals(epic1, epic2);
    }

    @Test
    void shouldNotEpicIdEqualToSubtaskId() {
        Epic epic = new Epic("test", "test");
        epic.setId(1);

        epic.addSubTaskId(2);

        ArrayList<Integer> subTasksIdsBeforeAdd = epic.getSubTaskIds();

        epic.addSubTaskId(1);

        ArrayList<Integer> subTasksIdsAfterAdd = epic.getSubTaskIds();

        Assertions.assertArrayEquals(subTasksIdsAfterAdd.toArray(), subTasksIdsBeforeAdd.toArray());
    }
}