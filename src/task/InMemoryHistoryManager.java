package task;

import java.util.ArrayList;

public class InMemoryHistoryManager implements HistoryManager {
    private final ArrayList<Task> showedTasksHistory = new ArrayList<>();

    @Override
    public void add(Task task) {
        if (showedTasksHistory.size() == 10) {
            showedTasksHistory.remove(1);
        }

        if (task instanceof Epic) {
            Epic epicToInsert = new Epic((Epic) task);
            showedTasksHistory.add(epicToInsert);
            return;
        } else if (task instanceof SubTask) {
            SubTask subTaskToInsert = new SubTask((SubTask) task);
            showedTasksHistory.add(subTaskToInsert);
            return;
        }

        Task taskToInsert = new Task(task);
        showedTasksHistory.add(taskToInsert);
    }

    @Override
    public ArrayList<Task> getHistory() {
        return showedTasksHistory;
    }
}
