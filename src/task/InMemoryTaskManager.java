package task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;

public class InMemoryTaskManager implements TaskManager {
    private int counter = 0;
    protected final HashMap<Integer, Task> tasks = new HashMap<>();
    protected final HashMap<Integer, Epic> epics = new HashMap<>();
    protected final HashMap<Integer, SubTask> subTasks = new HashMap<>();
    private final HistoryManager historyManager = Managers.getDefaultHistory();
    private final TreeSet<Task> prioritizedTasks = new TreeSet<>();

    @Override
    public ArrayList<Task> getTasks() {
        return new ArrayList<>(tasks.values());
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
        for (SubTask subtask : subTasks.values()) {
            if (subtask.getEpicId() == epicId) {
                result.add(subtask);
            }
        }
        return result;
    }

    @Override
    public void clearTasks() {
        tasks.clear();
        updatePrioritizedTasks();
    }

    @Override
    public void clearSubTasks() {
        subTasks.clear();
        for (Integer id : epics.keySet()) {
            Epic epic = epics.get(id);
            epic.setStatus(Status.NEW);
            epics.put(epic.getId(), epic);
        }

        updatePrioritizedTasks();
    }

    @Override
    public void clearEpics() {
        epics.clear();
        subTasks.clear();

        updatePrioritizedTasks();
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

        updatePrioritizedTasks();
        return id;
    }

    @Override
    public int createSubTask(SubTask subTask) {
        int id = ++counter;
        subTask.setId(id);
        subTasks.put(subTask.getId(), subTask);

        Epic epic = epics.get(subTask.getEpicId());
        epic.addSubTaskId(subTask.getId());

        if (epic.getStartTime() == null || epic.getStartTime().isAfter(subTask.getStartTime())) {
            epic.setStartTime(subTask.getStartTime());
        }

        if (epic.getEndTime() == null || epic.getEndTime().isBefore(subTask.getEndTime())) {
            epic.setEndTime(subTask.getEndTime());
        }

        epic.setDuration(epic.getDuration().plus(subTask.getDuration()));

        epics.put(epic.getId(), epic);
        updateEpicsStatus(subTask.getEpicId());

        updatePrioritizedTasks();
        return id;
    }

    @Override
    public int createEpic(Epic epic) {
        int id = ++counter;
        epic.setId(id);
        epics.put(epic.getId(), epic);

        updatePrioritizedTasks();
        return id;
    }

    @Override
    public void updateTask(Task task) {
        tasks.put(task.getId(), task);

        updatePrioritizedTasks();
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        if (subTask.getId() == subTask.getEpicId()) {
            return;
        }
        subTasks.put(subTask.getId(), subTask);
        updateEpicsStatus(subTask.getEpicId());

        updatePrioritizedTasks();
    }

    @Override
    public void updateEpic(Epic epic) {
        epics.put(epic.getId(), epic);

        updatePrioritizedTasks();
    }

    @Override
    public void deleteTaskById(int id) {
        tasks.remove(id);
        historyManager.remove(id);

        updatePrioritizedTasks();
    }

    @Override
    public void deleteSubTaskById(int id) {
        SubTask subtask = subTasks.get(id);
        Epic epic = epics.get(subtask.getEpicId());
        epic.deleteSubTaskId(id);
        epics.put(epic.getId(), epic);
        updateEpicsStatus(epic.getId());
        subTasks.remove(id);
        historyManager.remove(id);

        updatePrioritizedTasks();
    }

    @Override
    public void deleteEpicById(int epicId) {
        Epic epic = epics.get(epicId);
        for (Integer subTaskId : epic.getSubTaskIds()) {
            subTasks.remove(subTaskId);
            historyManager.remove(subTaskId);
        }

        epics.remove(epicId);
        historyManager.remove(epicId);

        updatePrioritizedTasks();
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return prioritizedTasks.stream().toList();
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

    private void updatePrioritizedTasks() {
        prioritizedTasks.clear();
        prioritizedTasks.addAll(tasks.values());
        prioritizedTasks.addAll(subTasks.values());
        prioritizedTasks.addAll(epics.values());
    }

}
