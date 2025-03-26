package service;

import java.nio.file.Paths;

public class Managers {
    private static final String defaultFileStorage = "resources/tasks.csv";

    public static TaskManager getDefault() {
        return new FileBackedTaskManager(Paths.get(defaultFileStorage));
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}