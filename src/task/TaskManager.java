package task;

import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    private int counter = 0;
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private final HashMap<Integer, SubTask> subTasks = new HashMap<>();

    public ArrayList<Task> getTasks() {
        ArrayList<Task> tasksList = new ArrayList<>();
        for (Integer id : tasks.keySet()) {
            tasksList.add(tasks.get(id));
        }
        return  tasksList;
    }

    public ArrayList<SubTask> getSubTasks() {
        ArrayList<SubTask> subTasksList = new ArrayList<>();
        for (Integer id : subTasks.keySet()) {
            subTasksList.add(subTasks.get(id));
        }
        return  subTasksList;
    }

    public ArrayList<Epic> getEpics() {
        ArrayList<Epic> epicsList = new ArrayList<>();
        for (Integer id : epics.keySet()) {
            epicsList.add(epics.get(id));
        }
        return  epicsList;
    }

    public void clearTasks() {
        tasks.clear();
    }

    public void clearSubTasks() {
        subTasks.clear();
        for (Integer id : epics.keySet()) {
            Epic epic = epics.get(id);
            epic.setStatus(Status.NEW);
            epics.put(epic.getId(), epic);
        }
    }

    public void clearEpics() {
        epics.clear();
    }

    public Task getTaskById(int id) {
        return tasks.get(id);
    }

    public SubTask getSubTaskById(int id) {
        return subTasks.get(id);
    }

    public Epic getEpicById(int id) {
        return epics.get(id);
    }

    public void createTask(Task task) {
        task.setId(++counter);
        tasks.put(task.getId(), task);
    }

    public void createSubTask(SubTask subTask) {
        subTask.setId(++counter);
        subTasks.put(subTask.getId(), subTask);

        Epic epic = epics.get(subTask.getEpicId());
        epic.addSubTaskId(subTask.getId());
        epics.put(epic.getId(), epic);
        updateEpicsStatus(subTask.getEpicId());
    }

    public void createEpic(Epic epic) {
        epic.setId(++counter);
        epics.put(epic.getId(), epic);
    }

    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }

    public void updateSubTask(SubTask subTask) {
        subTasks.put(subTask.getId(), subTask);
        updateEpicsStatus(subTask.getEpicId());
    }

    public void updateEpic(Epic epic) {
        epics.put(epic.getId(), epic);
    }

    public void deleteTaskById(int id) {
        tasks.remove(id);
    }

    public void deleteSubTaskById(int id) {
        SubTask subtask = subTasks.get(id);
        Epic epic = epics.get(subtask.getEpicId());
        epic.deleteSubTaskId(id);
        epics.put(epic.getId(), epic);
        updateEpicsStatus(epic.getId());
        subTasks.remove(id);
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

    public void deleteEpicById(int epicId) {
        Epic epic = epics.get(epicId);
        for (Integer subTaskId : epic.getSubTaskIds()) {
            subTasks.remove(subTaskId);
        }

        epics.remove(epicId);
    }


}
