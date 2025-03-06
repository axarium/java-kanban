package service;

import model.Task;

import java.util.List;
import java.util.ArrayList;

public class InMemoryHistoryManager implements HistoryManager {
    private static final int MAX_HISTORY_SIZE = 10;
    private final List<Task> history;

    public InMemoryHistoryManager() {
        this.history = new ArrayList<>(MAX_HISTORY_SIZE);
    }

    @Override
    public void add(Task task) {
        if (history.size() == MAX_HISTORY_SIZE) {
            history.removeFirst();
        }
        history.add(new Task(task));
    }

    @Override
    public List<Task> getHistory() {
        List<Task> resultList = new ArrayList<>(history.size());

        for (Task task : history) {
            resultList.add(new Task(task));
        }

        return resultList;
    }
}