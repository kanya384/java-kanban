package task;

import java.util.ArrayList;
import java.util.HashMap;

public class InMemoryTaskManager implements TaskManager {
    private int counter = 0;
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private final HashMap<Integer, SubTask> subTasks = new HashMap<>();
    private final HistoryManager historyManager = Managers.getDefaultHistory();

    @Override
    public ArrayList<Task> getTasks() {
        return  new ArrayList<>(tasks.values());
    }

    @Override
    public ArrayList<SubTask> getSubTasks() {
        return new ArrayList<>(subTasks.values());
    }

    @Override
    public ArrayList<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public ArrayList<SubTask> getEpicSubtasks(int epicId) {
        ArrayList<SubTask> result = new ArrayList<>();
        for (SubTask subtask: subTasks.values()) {
            if (subtask.getEpicId()  == epicId) {
                result.add(subtask);
            }
        }
        return result;
    }

    @Override
    public void clearTasks() {
        tasks.clear();
    }

    @Override
    public void clearSubTasks() {
        subTasks.clear();
        for (Integer id : epics.keySet()) {
            Epic epic = epics.get(id);
            epic.setStatus(Status.NEW);
            epics.put(epic.getId(), epic);
        }
    }

    @Override
    public void clearEpics() {
        epics.clear();
        subTasks.clear();
    }

    @Override
    public Task getTaskById(int id) {
        Task task = tasks.get(id);
        historyManager.add(task);
        return task;
    }

    @Override
    public SubTask getSubTaskById(int id) {
        SubTask subTask = subTasks.get(id);
        historyManager.add(subTask);
        return subTask;
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epic = epics.get(id);
        historyManager.add(epic);
        return epic;
    }

    @Override
    public int createTask(Task task) {
        int id = ++counter;
        task.setId(id);
        tasks.put(task.getId(), task);

        return id;
    }

    @Override
    public int createSubTask(SubTask subTask) {
        int id = ++counter;
        subTask.setId(id);
        subTasks.put(subTask.getId(), subTask);

        Epic epic = epics.get(subTask.getEpicId());
        epic.addSubTaskId(subTask.getId());
        epics.put(epic.getId(), epic);
        updateEpicsStatus(subTask.getEpicId());

        return  id;
    }

    @Override
    public int createEpic(Epic epic) {
        int id = ++counter;
        epic.setId(id);
        epics.put(epic.getId(), epic);

        return id;
    }

    @Override
    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        if (subTask.getId() == subTask.getEpicId()) {
            return;
        }
        subTasks.put(subTask.getId(), subTask);
        updateEpicsStatus(subTask.getEpicId());
    }

    @Override
    public void updateEpic(Epic epic) {
        epics.put(epic.getId(), epic);
    }

    @Override
    public void deleteTaskById(int id) {
        tasks.remove(id);
    }

    @Override
    public void deleteSubTaskById(int id) {
        SubTask subtask = subTasks.get(id);
        Epic epic = epics.get(subtask.getEpicId());
        epic.deleteSubTaskId(id);
        epics.put(epic.getId(), epic);
        updateEpicsStatus(epic.getId());
        subTasks.remove(id);
    }

    @Override
    public void deleteEpicById(int epicId) {
        Epic epic = epics.get(epicId);
        for (Integer subTaskId : epic.getSubTaskIds()) {
            subTasks.remove(subTaskId);
        }

        epics.remove(epicId);
    }

    @Override
    public ArrayList<Task> getHistory() {
        return historyManager.getHistory();
    }

    private void updateEpicsStatus(int epicId) {
        Epic epic = epics.get(epicId);
        ArrayList<Integer> subTaskIds = epic.getSubTaskIds();
        Status status;

        boolean isAllSubTasksNew = true;
        boolean isAllSubTasksDone = true;

        for (Integer subTaskId : subTaskIds) {
            SubTask subTask = subTasks.get(subTaskId);
            if (subTask.getStatus() != Status.NEW) {
                isAllSubTasksNew = false;
            }

            if (subTask.getStatus() != Status.DONE) {
                isAllSubTasksDone = false;
            }
        }

        if (isAllSubTasksNew) {
            status = Status.NEW;
        } else if (isAllSubTasksDone) {
            status = Status.DONE;
        } else {
            status = Status.IN_PROGRESS;
        }

        epic.setStatus(status);
        epics.put(epic.getId(), epic);
    }

}
