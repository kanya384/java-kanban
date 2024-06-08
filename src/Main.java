import task.*;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefault();

        Task task = new Task("Подстричь газон", "Тщательно подстричь газон", Status.NEW);
        taskManager.createTask(task);

        Task task2 = new Task("Купить колу", "Купить колу в магните", Status.NEW);
        taskManager.createTask(task2);

        Epic epic = new Epic("Перезд", "Собрать вещи для переезда");
        taskManager.createEpic(epic);

        SubTask subTask = new SubTask("Нанести пену", "Нанести пену на машину", Status.NEW, epic.getId());
        taskManager.createSubTask(subTask);

        SubTask subTask2 = new SubTask("Подождать пять минут", "Подождать пять минут", Status.NEW, epic.getId());
        taskManager.createSubTask(subTask2);

        SubTask subTask3 = new SubTask("Смыть пену", "Смыть пену", Status.NEW, epic.getId());
        taskManager.createSubTask(subTask3);


        taskManager.getEpicById(epic.getId());
        taskManager.getSubTaskById(subTask.getId());
        taskManager.getTaskById(task.getId());

        System.out.println(taskManager.getHistory());




        /*
        //load from file
        TaskManager taskManager = FileBackedTaskManager.loadFromFile(new File("./tasks.csv"));

        System.out.println(taskManager.getTasks());
        System.out.println(taskManager.getEpics());
        System.out.println(taskManager.getSubTasks());
         */


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
