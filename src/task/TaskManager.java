package task;

import java.util.List;

public interface TaskManager {
    List<Task> getTasks();

    List<SubTask> getSubTasks();

    List<Epic> getEpics();

    List<SubTask> getEpicSubtasks(int epicId);


    void clearTasks();

    void clearSubTasks();

    void clearEpics();

    Task getTaskById(int id) throws NotFoundException;

    SubTask getSubTaskById(int id) throws NotFoundException;

    Epic getEpicById(int id) throws NotFoundException;

    int createTask(Task task) throws IntersectsExistingTaskException;

    int createSubTask(SubTask subTask) throws IntersectsExistingTaskException, NoEpicException;

    int createEpic(Epic epic);

    void updateTask(Task task);

    void updateSubTask(SubTask subTask);

    void updateEpic(Epic epic);

    void deleteTaskById(int id);

    void deleteSubTaskById(int id);

    void deleteEpicById(int epicId);

    List<Task> getHistory();

    List<Task> getPrioritizedTasks();

    boolean isIntersectedTasks(Task task1, Task task2);
}
