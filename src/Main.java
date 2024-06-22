import task.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;

public class Main {

    public static void main(String[] args) {
        try {
            TaskManager taskManager = Managers.getDefault();

            Task task = new Task("Подстричь газон", "Тщательно подстричь газон", Status.NEW, LocalDateTime.of(2024, Month.JUNE, 21, 0, 0), Duration.ofMinutes(10));
            taskManager.createTask(task);

            Task task2 = new Task("Купить колу", "Купить колу в магните", Status.NEW, LocalDateTime.of(2024, Month.JUNE, 19, 10, 20), Duration.ofMinutes(1));
            taskManager.createTask(task2);

            Epic epic = new Epic("Перезд", "Собрать вещи для переезда");
            taskManager.createEpic(epic);

            SubTask subTask = new SubTask("Нанести пену", "Нанести пену на машину", Status.NEW, LocalDateTime.of(2024, Month.JUNE, 19, 10, 20), Duration.ofMinutes(1), epic.getId());
            taskManager.createSubTask(subTask);

            SubTask subTask2 = new SubTask("Подождать пять минут", "Подождать пять минут", Status.NEW, LocalDateTime.of(2024, Month.JUNE, 19, 10, 30), Duration.ofMinutes(1), epic.getId());
            taskManager.createSubTask(subTask2);

            SubTask subTask3 = new SubTask("Смыть пену", "Смыть пену", Status.NEW, LocalDateTime.of(2024, Month.JUNE, 19, 10, 32), Duration.ofMinutes(1), epic.getId());
            taskManager.createSubTask(subTask3);


            taskManager.getEpicById(epic.getId());
            taskManager.getSubTaskById(subTask.getId());
            taskManager.getTaskById(task.getId());

            System.out.println(taskManager.getHistory());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private static void printAllTasks(TaskManager manager) {
        System.out.println("Задачи:");
        for (Task task : manager.getTasks()) {
            System.out.println(task);
        }
        System.out.println("Эпики:");
        for (Task epic : manager.getEpics()) {
            System.out.println(epic);

            for (Task task : manager.getEpicSubtasks(epic.getId())) {
                System.out.println("--> " + task);
            }
        }
        System.out.println("Подзадачи:");
        for (Task subtask : manager.getSubTasks()) {
            System.out.println(subtask);
        }

        System.out.println("История:");
        for (Task task : manager.getHistory()) {
            System.out.println(task);
        }
    }
}
