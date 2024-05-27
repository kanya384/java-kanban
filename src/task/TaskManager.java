package task;

import java.util.ArrayList;
import java.util.List;

public interface TaskManager {
    ArrayList<Task> getTasks();

    ArrayList<SubTask> getSubTasks();

    ArrayList<Epic> getEpics();

    ArrayList<SubTask> getEpicSubtasks(int epicId);


    void clearTasks();

    void clearSubTasks();

    void clearEpics();

    Task getTaskById(int id);

    SubTask getSubTaskById(int id);

    Epic getEpicById(int id);

    int createTask(Task task);

    int createSubTask(SubTask subTask);

    int createEpic(Epic epic);

    void updateTask(Task task);

    void updateSubTask(SubTask subTask);

    void updateEpic(Epic epic);

    void deleteTaskById(int id);

    void deleteSubTaskById(int id);

    void deleteEpicById(int epicId);

    List<Task> getHistory();
}
