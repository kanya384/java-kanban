package task;

import java.io.File;

public class Managers {
    static final File file = new File("./tasks.csv");

    public static TaskManager getDefault() {
        return FileBackedTaskManager.loadFromFile(file);
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
