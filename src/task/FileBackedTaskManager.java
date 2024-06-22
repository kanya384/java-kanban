package task;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class FileBackedTaskManager extends InMemoryTaskManager implements TaskManager {
    private final File outputFile;
    private static final String title = "id,type,name,status,description,epic,start_time,duration";
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yy HH:mm");

    FileBackedTaskManager(File outputFile) {
        this.outputFile = outputFile;
        save();
    }

    public static void main(String[] args) {
        try {
            File tmpFile = new File("tasks.csv");

            FileBackedTaskManager taskManager1 = new FileBackedTaskManager(tmpFile);
            Task task = new Task("Подстричь газон", "Тщательно подстричь газон", Status.NEW, LocalDateTime.of(2024, Month.JUNE, 21, 0, 0), Duration.ofMinutes(10));
            taskManager1.createTask(task);

            Task task2 = new Task("Купить колу", "Купить колу в магните", Status.NEW, LocalDateTime.of(2024, Month.JUNE, 21, 0, 0), Duration.ofMinutes(10));
            System.out.println(taskManager1.createTask(task2));

            Epic epic = new Epic("Помыть машину", "Помыть машину перед выходными");
            taskManager1.createEpic(epic);

            SubTask subTask = new SubTask("Нанести пену", "Нанести пену на машину", Status.NEW, LocalDateTime.of(2024, Month.JUNE, 19, 10, 20), Duration.ofMinutes(1), epic.getId());
            taskManager1.createSubTask(subTask);

            SubTask subTask2 = new SubTask("Подождать пять минут", "Подождать пять минут", Status.NEW, LocalDateTime.of(2024, Month.JUNE, 19, 10, 30), Duration.ofMinutes(1), epic.getId());
            taskManager1.createSubTask(subTask2);

            FileBackedTaskManager taskManager2 = FileBackedTaskManager.loadFromFile(tmpFile);

            if (taskManager1.getTasks().size() != taskManager2.getTasks().size()) {
                System.out.println("tasks sizes are not equal");
            }

            if (taskManager1.getSubTasks().size() != taskManager2.getSubTasks().size()) {
                System.out.println("subtasks sizes are not equal");
            }

            if (taskManager1.getEpics().size() != taskManager2.getEpics().size()) {
                System.out.println("epics sizes are not equal");
            }

            System.out.println(taskManager1.getPrioritizedTasks());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }

    @Override
    public void clearTasks() {
        super.clearTasks();
        save();
    }

    @Override
    public void clearSubTasks() {
        super.clearSubTasks();
        save();
    }

    @Override
    public void clearEpics() {
        super.clearEpics();
        save();
    }

    @Override
    public int createTask(Task task) throws IntersectsExistingTaskException {
        int taskId = super.createTask(task);
        save();
        return taskId;
    }

    @Override
    public int createSubTask(SubTask subTask) throws IntersectsExistingTaskException, NoEpicException {
        int subTaskId = super.createSubTask(subTask);
        save();
        return subTaskId;
    }

    @Override
    public int createEpic(Epic epic) {
        int epicId = super.createEpic(epic);
        save();
        return epicId;
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        super.updateSubTask(subTask);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void deleteTaskById(int id) {
        super.deleteTaskById(id);
        save();
    }

    @Override
    public void deleteSubTaskById(int id) {
        super.deleteSubTaskById(id);
        save();
    }

    @Override
    public void deleteEpicById(int epicId) {
        super.deleteEpicById(epicId);
        save();
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        try {
            String[] lines = Files.readString(file.toPath()).split("\n");
            FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(file);

            List<Task> tasks = Arrays.stream(lines)
                    .skip(1)
                    .map(line -> Optional.ofNullable(convertStringToTask(line)))
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .toList();

            for (Task task : tasks) {
                TaskType taskType = toEnum(task);
                switch (taskType) {
                    case TASK -> fileBackedTaskManager.createTask(task);

                    case EPIC -> fileBackedTaskManager.createEpic((Epic) task);

                    case SUBTASK -> fileBackedTaskManager.createSubTask((SubTask) task);

                    default -> throw new IllegalStateException("Unexpected value: " + taskType);
                }
            }


            return fileBackedTaskManager;
        } catch (IOException | NoEpicException | IntersectsExistingTaskException e) {
            throw new ManagerSaveException(e);
        }
    }

    private void save() {
        try (FileWriter writer = new FileWriter(outputFile)) {
            writer.write(title);

            for (Task task : tasks.values()) {
                writer.write(String.format("\n%s", convertTaskToString(task)));
            }

            for (Epic epic : epics.values()) {
                writer.write(String.format("\n%s", convertTaskToString(epic)));
            }

            for (SubTask subTask : subTasks.values()) {
                writer.write(String.format("\n%s", convertTaskToString(subTask)));
            }

        } catch (IOException e) {
            throw new ManagerSaveException(e);
        }
    }

    private static Task convertStringToTask(String value) {
        String[] items = value.split(",");

        TaskType taskType = TaskType.valueOf(items[1]);
        int id = Integer.parseInt(items[0]);
        String name = items[2];
        String description = items[4];
        Status status = stringToStatus(items[3]);

        LocalDateTime startDateTime = null;
        if (!items[6].isBlank()) {
            startDateTime = LocalDateTime.parse(items[6], DATE_TIME_FORMATTER);
        }

        Duration duration = Duration.ofMinutes(Integer.parseInt(items[7]));

        switch (taskType) {

            case TaskType.TASK -> {
                Task task = new Task(name, description, status, startDateTime, duration);
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
                SubTask subTask = new SubTask(name, description, status, startDateTime, duration, epicId);
                subTask.setId(id);
                return subTask;
            }

            default -> {
                return null;
            }
        }
    }

    private String convertTaskToString(Task task) {
        TaskType taskType = toEnum(task);
        switch (taskType) {

            case TaskType.EPIC, TaskType.TASK -> {
                return String.format("%d,%s,%s,%s,%s,,%s,%d", task.getId(), taskType, task.getName(), task.getStatus(), task.getDescription(), task.getStartTime() != null ? task.getStartTime().format(DATE_TIME_FORMATTER) : "", task.getDuration().toMinutes());
            }

            case TaskType.SUBTASK -> {
                SubTask subTask = (SubTask) task;
                return String.format("%d,%s,%s,%s,%s,%d,%s,%d", subTask.getId(), taskType, subTask.getName(), subTask.getStatus(), subTask.getDescription(), subTask.getEpicId(), subTask.getStartTime().format(DATE_TIME_FORMATTER), subTask.getDuration().toMinutes());
            }
        }

        throw new IllegalStateException("Unexpected value: " + taskType);
    }

    private static TaskType toEnum(Task task) {
        if (task instanceof SubTask) {
            return TaskType.SUBTASK;
        } else if (task instanceof Epic) {
            return TaskType.EPIC;
        }

        return TaskType.TASK;
    }

    private static Status stringToStatus(String input) {
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
