import task.*;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();

        Task task1 = new Task("Подстричь газон", "Тщательно подстричь газон", Status.NEW);
        taskManager.createTask(task1);

        Task task2 = new Task("Выгулять собаку", "Выгулять собаку на районе", Status.NEW);
        taskManager.createTask(task2);


        Epic epic1 = new Epic("Перезд", "Собрать вещи для переезда");
        taskManager.createEpic(epic1);
        SubTask epic1SubTask1 = new SubTask("Собрать коробки", "Собрать коробки", Status.NEW, epic1.getId());
        taskManager.createSubTask(epic1SubTask1);
        SubTask epic1SubTask2 = new SubTask("Упаковать кошку", "Упаковать кошку", Status.NEW, epic1.getId());
        taskManager.createSubTask(epic1SubTask2);

        Epic epic2 = new Epic("Помыть машину", "Помыть машину");
        taskManager.createEpic(epic2);
        SubTask epic2SubTask1 = new SubTask("Нанести пену и смыть водой", "Нанести пену и смыть водой", Status.NEW, epic2.getId());
        taskManager.createSubTask(epic2SubTask1);

        System.out.println(taskManager.getEpics());
        System.out.println(taskManager.getSubTasks());
        System.out.println(taskManager.getTasks());

        SubTask doneEpic1SubTask1 = new SubTask(epic1SubTask1.getName(), epic1SubTask1.getDescription(), Status.DONE, epic1SubTask1.getEpicId());
        doneEpic1SubTask1.setId(epic1SubTask1.getId());
        taskManager.updateSubTask(doneEpic1SubTask1);
        taskManager.deleteTaskById(task2.getId());
        taskManager.deleteEpicById(epic2.getId());

        System.out.println(taskManager.getEpics());
        System.out.println(taskManager.getSubTasks());
        System.out.println(taskManager.getTasks());

    }
}
