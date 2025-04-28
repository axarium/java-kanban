package service;

import exception.NotFoundException;
import exception.OverlapException;
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

    Task getTaskById(int id) throws NotFoundException;

    Epic getEpicById(int id) throws NotFoundException;

    Subtask getSubtaskById(int id) throws NotFoundException;

    Task createTask(Task task) throws OverlapException;

    void createEpic(Epic epic);

    Subtask createSubtask(Subtask subtask) throws NotFoundException, OverlapException;

    Task updateTask(Task task) throws NotFoundException, OverlapException;

    Epic updateEpic(Epic epic) throws NotFoundException;

    Subtask updateSubtask(Subtask subtask) throws NotFoundException, OverlapException;

    Task removeTaskById(int id) throws NotFoundException;

    Epic removeEpicById(int id) throws NotFoundException;

    Subtask removeSubtaskById(int id) throws NotFoundException;

    List<Subtask> getSubtasksByEpicId(int epicId);

    List<Task> getHistory();
}