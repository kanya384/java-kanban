package task;

import java.io.File;

public class Managers {
    static final File file = new File("./tasks.csv");

    public static TaskManager getDefault() {
        if (file.exists()) {
            return FileBackedTaskManager.loadFromFile(file);
        }
        return new FileBackedTaskManager(file);
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
