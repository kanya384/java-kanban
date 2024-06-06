package task;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;

public class FileBackedTaskManager extends InMemoryTaskManager implements TaskManager {
    private final String outputFile;

    FileBackedTaskManager(String outputFile) {
        this.outputFile = outputFile;
    }

    private void save() {
        try (FileWriter writer = new FileWriter(outputFile)) {
            writer.write("id,type,name,status,description,epic");

            for (Task task : tasks.values()) {
                writer.write(toString(task));
            }

            for (Epic epic : epics.values()) {
                writer.write(toString(epic));
            }

            for (SubTask subTask : subTasks.values()) {
                writer.write(toString(subTask));
            }

        } catch (IOException e) {
            throw new ManagerSaveException(e);
        }
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        try {
            FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(file.getPath());
            String[] lines = Files.readString(file.toPath()).split("\n");

            for (String line : lines) {
                Task task = fromString(line);
                TaskType taskType = TaskType.valueOf(task.getClass().getName().toUpperCase());
                switch (taskType) {
                    case TASK -> fileBackedTaskManager.createTask(task);

                    case EPIC -> fileBackedTaskManager.createEpic((Epic) task);

                    case SUBTASK -> fileBackedTaskManager.createSubTask((SubTask) task);

                    default -> throw new IllegalStateException("Unexpected value: " + taskType);
                }
            }

            return fileBackedTaskManager;
        } catch (IOException e) {
            throw new ManagerSaveException(e);
        }
    }

    private String toString(Task task) {
        TaskType taskType = TaskType.valueOf(task.getClass().getName().toUpperCase());
        switch (taskType) {

            case TaskType.EPIC, TaskType.TASK -> {
                return String.format("%d,%s,%s,%s,%s,", task.getId(), taskType, task.getName(), task.getStatus(), task.getDescription());
            }

            case TaskType.SUBTASK -> {
                SubTask subTask = (SubTask) task;
                return String.format("%d,%s,%s,%s,%s,%d", subTask.getId(), taskType, subTask.getName(), subTask.getStatus(), subTask.getDescription(), subTask.getEpicId());
            }

            default -> {
                throw new IllegalStateException("Unexpected value: " + taskType);
            }
        }
    }

    static Task fromString(String value) {
        String[] items = value.split(",");

        TaskType taskType = TaskType.valueOf(items[1]);
        int id = Integer.parseInt(items[0]);
        String name = items[2];
        String description = items[4];
        Status status = stringToStatus(items[3]);

        switch (taskType) {

            case TaskType.TASK -> {
                Task task = new Task(name, description, status);
                task.setId(id);
                return task;
            }

            case TaskType.EPIC -> {
                Epic epic = new Epic(name, description);
                epic.setStatus(status);
                epic.setId(id);
                return epic;
            }

            case TaskType.SUBTASK -> {
                int epicId = Integer.parseInt(items[5]);
                SubTask subTask = new SubTask(name, description, status, epicId);
                subTask.setId(id);
                return subTask;
            }

            default -> throw new IllegalStateException("Unexpected value: " + taskType);
        }
    }

    static private Status stringToStatus(String input) {
        Status status = Status.valueOf(input);

        switch (status) {
            case Status.NEW, Status.DONE, Status.IN_PROGRESS -> {
                return status;
            }

            default -> {
                throw new IllegalStateException("Unexpected value: " + status);
            }
        }
    }

}
