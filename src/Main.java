import task.*;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefault();

        Task task = new Task("Подстричь газон", "Тщательно подстричь газон", Status.NEW);
        taskManager.createTask(task);

        Epic epic = new Epic("Перезд", "Собрать вещи для переезда");
        taskManager.createEpic(epic);

        SubTask subTask = new SubTask("Нанести пену и смыть водой", "Нанести пену и смыть водой", Status.NEW, epic.getId());
        taskManager.createSubTask(subTask);


        taskManager.getEpicById(epic.getId());
        taskManager.getSubTaskById(subTask.getId());
        taskManager.getTaskById(task.getId());

        printAllTasks(taskManager);
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
