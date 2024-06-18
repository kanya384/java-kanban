package task;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FileBackedTaskManagerTest {

    @Test
    void shouldStoreOnlyTitleIfNoTasksAdded() {
        try {
            File file = File.createTempFile("test", "csv");
            FileBackedTaskManager taskManager = new FileBackedTaskManager(file);

            String[] lines = Files.readString(file.toPath()).split("\n");
            assertEquals(lines.length, 1);
            assertEquals(lines[0], "id,type,name,status,description,epic");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void shouldStoreTasksToFile() {
        try {
            File file = File.createTempFile("test", "csv");
            FileBackedTaskManager taskManager = new FileBackedTaskManager(file);

            Task task = new Task("Подстричь газон", "Тщательно подстричь газон", Status.NEW, LocalDateTime.of(2024, Month.JUNE, 19, 10, 20), Duration.ofMinutes(1));
            taskManager.createTask(task);

            Epic epic = new Epic("Помыть машину", "Помыть машину перед праздниками");
            taskManager.createEpic(epic);

            SubTask subTask = new SubTask("Нанести пену", "Нанести пену на машину", Status.NEW, LocalDateTime.of(2024, Month.JUNE, 19, 10, 20), Duration.ofMinutes(1), epic.getId());
            taskManager.createSubTask(subTask);

            String[] lines = Files.readString(file.toPath()).split("\n");

            assertEquals(lines.length, 4);

            assertEquals(lines[0], "id,type,name,status,description,epic,start_time,duration");
            assertEquals(lines[1], "1,TASK,Подстричь газон,NEW,Тщательно подстричь газон,,");
            assertEquals(lines[2], "2,EPIC,Помыть машину,NEW,Помыть машину перед праздниками,,");
            assertEquals(lines[3], "3,SUBTASK,Нанести пену,NEW,Нанести пену на машину,2,");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void shouldLoadNoTasksFromEmptyFile() {
        try {
            File file = File.createTempFile("test", "csv");
            FileBackedTaskManager taskManager = FileBackedTaskManager.loadFromFile(file);

            assertEquals(taskManager.getSubTasks().size(), 0);
            assertEquals(taskManager.getTasks().size(), 0);
            assertEquals(taskManager.getEpics().size(), 0);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void shouldLoadTasksFromFile() {
        try {
            File file = File.createTempFile("test", "csv");

            try (FileWriter writer = new FileWriter(file)) {

                writer.write("""
                        id,type,name,status,description,epic,start_time,duration
                        1,TASK,Подстричь газон,NEW,Тщательно подстричь газон,,,
                        2,EPIC,Помыть машину,NEW,Помыть машину перед праздниками,,,
                        3,SUBTASK,Нанести пену,NEW,Нанести пену на машину,2,,""");
            }


            FileBackedTaskManager taskManager = FileBackedTaskManager.loadFromFile(file);


            assertEquals(1, taskManager.getTasks().size());
            assertEquals(1, taskManager.getEpics().size());
            assertEquals(1, taskManager.getSubTasks().size());

            Task expectedTask = new Task("Подстричь газон", "Тщательно подстричь газон", Status.NEW, LocalDateTime.of(2024, Month.JUNE, 19, 10, 20), Duration.ofMinutes(1));
            expectedTask.setId(1);

            Epic expectedEpic = new Epic("Помыть машину", "Помыть машину перед праздниками");
            expectedEpic.setId(2);

            SubTask expectedSubTask = new SubTask("Нанести пену", "Нанести пену на машину", Status.NEW, LocalDateTime.of(2024, Month.JUNE, 19, 10, 20), Duration.ofMinutes(1), expectedEpic.getId());
            expectedSubTask.setId(3);

            assertEquals(expectedTask, taskManager.getTasks().getFirst());
            assertEquals(expectedEpic, taskManager.getEpics().getFirst());
            assertEquals(expectedSubTask, taskManager.getSubTasks().getFirst());

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
