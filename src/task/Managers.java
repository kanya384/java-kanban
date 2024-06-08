package task;

import java.io.File;

public class Managers {
    final static File file = new File("./tasks.csv");

    public static TaskManager getDefault() {
        return new FileBackedTaskManager(file);
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
