package service;

import model.Task;
import model.Epic;
import model.Subtask;
import model.TaskStatus;

import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    private static int tasksCount = 0;
    private final HashMap<Integer, Task> tasks;
    private final HashMap<Integer, Epic> epics;
    private final HashMap<Integer, Subtask> subtasks;

    public TaskManager() {
        this.tasks = new HashMap<>();
        this.epics = new HashMap<>();
        this.subtasks = new HashMap<>();
    }

    private TaskStatus calculateEpicStatus(Epic epic) {
        boolean isAllSubtasksDone = true;

        for (Integer subtaskId : epic.getSubtasksIds()) {
            TaskStatus subtaskStatus = subtasks.get(subtaskId).getStatus();
            if (subtaskStatus == TaskStatus.IN_PROGRESS) {
                return TaskStatus.IN_PROGRESS;
            } else if (subtaskStatus != TaskStatus.DONE) {
                isAllSubtasksDone = false;
            }
        }

        if (isAllSubtasksDone) {
            return TaskStatus.DONE;
        }

        return TaskStatus.NEW;
    }

    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    public ArrayList<Task> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    public ArrayList<Task> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    public void removeAllTasks() {
        tasks.clear();
    }

    public void removeAllEpics() {
        epics.clear();
        subtasks.clear();
    }

    public void removeAllSubtasks() {
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.getSubtasksIds().clear();
            epic.setStatus(TaskStatus.NEW);
        }
    }

    public Task getTaskById(int id) {
        return tasks.get(id);
    }

    public Epic getEpicById(int id) {
        return epics.get(id);
    }

    public Subtask getSubtaskById(int id) {
        return subtasks.get(id);
    }

    public void createTask(Task task) {
        task.setId(++tasksCount);
        tasks.put(task.getId(), task);
    }

    public void createEpic(Epic epic) {
        epic.setId(++tasksCount);
        epic.setStatus(TaskStatus.NEW);
        epics.put(epic.getId(), epic);
    }

    public Subtask createSubtask(Subtask subtask) {
        Epic epic = epics.get(subtask.getEpicId());

        if (epic == null) {
            return null;
        }

        subtask.setId(++tasksCount);
        subtasks.put(subtask.getId(), subtask);
        epic.getSubtasksIds().add(subtask.getId());
        epic.setStatus(calculateEpicStatus(epic));
        return subtask;
    }

    public Task updateTask(Task task) {
        Task currentTask = tasks.get(task.getId());

        if (currentTask == null) {
            return null;
        }

        tasks.put(task.getId(), task);
        return currentTask;
    }

    public Epic updateEpic(Epic epic) {
        Epic currentEpic = removeEpicById(epic.getId());

        if (currentEpic == null) {
            return null;
        }

        epic.setStatus(TaskStatus.NEW);
        epics.put(epic.getId(), epic);
        return currentEpic;
    }

    public Subtask updateSubtask(Subtask subtask) {
        Subtask currentSubtask = removeSubtaskById(subtask.getId());

        if (currentSubtask == null) {
            return null;
        }

        Epic epic = epics.get(subtask.getEpicId());

        if (epic == null) {
            return null;
        }

        subtasks.put(subtask.getId(), subtask);
        epic.getSubtasksIds().add(subtask.getId());
        epic.setStatus(calculateEpicStatus(epic));
        return subtask;
    }

    public Task removeTaskById(int id) {
        Task task = tasks.get(id);
        tasks.remove(id);
        return task;
    }

    public Epic removeEpicById(int id) {
        Epic epic = epics.get(id);

        if (epic == null) {
            return null;
        }

        for (Integer subtaskId : epic.getSubtasksIds()) {
            subtasks.remove(subtaskId);
        }

        epics.remove(id);
        return epic;
    }

    public Subtask removeSubtaskById(int id) {
        Subtask subtask = subtasks.get(id);

        if (subtask == null) {
            return null;
        }

        Epic epic = epics.get(subtask.getEpicId());
        epic.getSubtasksIds().remove((Integer) subtask.getId());
        epic.setStatus(calculateEpicStatus(epic));
        subtasks.remove(id);
        return subtask;
    }

    public ArrayList<Subtask> getSubtasksByEpic(Epic epic) {
        ArrayList<Subtask> epicSubtasks = new ArrayList<>();

        for (Integer subtaskId : epic.getSubtasksIds()) {
            epicSubtasks.add(getSubtaskById(subtaskId));
        }

        return epicSubtasks;
    }
}