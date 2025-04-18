package service;

import model.Epic;
import model.Subtask;
import model.Task;

import java.util.List;

public interface TaskManager {

    List<Task> getPrioritizedTasks();

    List<Task> getAllTasks();

    List<Epic> getAllEpics();

    List<Subtask> getAllSubtasks();

    void removeAllTasks();

    void removeAllEpics();

    void removeAllSubtasks();

    Task getTaskById(int id);

    Epic getEpicById(int id);

    Subtask getSubtaskById(int id);

    Task createTask(Task task);

    void createEpic(Epic epic);

    Subtask createSubtask(Subtask subtask);

    Task updateTask(Task task);

    Epic updateEpic(Epic epic);

    Subtask updateSubtask(Subtask subtask);

    Task removeTaskById(int id);

    Epic removeEpicById(int id);

    Subtask removeSubtaskById(int id);

    List<Subtask> getSubtasksByEpicId(int epicId);

    List<Task> getHistory();
}