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

    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    public ArrayList<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    public ArrayList<Subtask> getAllSubtasks() {
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
        for (Epic epic : getAllEpics()) {
            epic.getSubtasksIds().clear();
            calculateEpicStatus(epic);
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
        task.setId(generateId());
        tasks.put(task.getId(), task);
    }

    public void createEpic(Epic epic) {
        epic.setId(generateId());
        epics.put(epic.getId(), epic);
    }

    public Subtask createSubtask(Subtask subtask) {
        Epic epic = getEpicById(subtask.getEpicId());

        if (epic == null) {
            return null;
        }

        subtask.setId(generateId());
        subtasks.put(subtask.getId(), subtask);
        epic.getSubtasksIds().add(subtask.getId());
        calculateEpicStatus(epic);

        return subtask;
    }

    public Task updateTask(Task task) {
        Task currentTask = getTaskById(task.getId());

        if (currentTask == null) {
            return null;
        }

        currentTask.setTitle(task.getTitle());
        currentTask.setDescription(task.getDescription());
        currentTask.setStatus(task.getStatus());

        return currentTask;
    }

    public Epic updateEpic(Epic epic) {
        Epic currentEpic = getEpicById(epic.getId());

        if (currentEpic == null) {
            return null;
        }

        currentEpic.setTitle(epic.getTitle());
        currentEpic.setDescription(epic.getDescription());

        return currentEpic;
    }

    public Subtask updateSubtask(Subtask subtask) {
        Subtask currentSubtask = getSubtaskById(subtask.getId());

        if (currentSubtask == null) {
            return null;
        }

        currentSubtask.setTitle(subtask.getTitle());
        currentSubtask.setDescription(subtask.getDescription());
        currentSubtask.setStatus(subtask.getStatus());

        Epic epic = getEpicById(subtask.getEpicId());
        calculateEpicStatus(epic);

        return currentSubtask;
    }

    public Task removeTaskById(int id) {
        return tasks.remove(id);
    }

    public Epic removeEpicById(int id) {
        Epic epic = epics.remove(id);

        if (epic == null) {
            return null;
        }

        for (Integer subtaskId : epic.getSubtasksIds()) {
            subtasks.remove(subtaskId);
        }

        return epic;
    }

    public Subtask removeSubtaskById(int id) {
        Subtask subtask = subtasks.remove(id);

        if (subtask == null) {
            return null;
        }

        Epic epic = getEpicById(subtask.getEpicId());
        epic.getSubtasksIds().remove((Integer) subtask.getId());
        calculateEpicStatus(epic);

        return subtask;
    }

    public ArrayList<Subtask> getSubtasksByEpic(Epic epic) {
        ArrayList<Subtask> epicSubtasks = new ArrayList<>();

        for (Integer subtaskId : epic.getSubtasksIds()) {
            epicSubtasks.add(getSubtaskById(subtaskId));
        }

        return epicSubtasks;
    }

    private void calculateEpicStatus(Epic epic) {
        if (epic.getSubtasksIds().isEmpty()) {
            epic.setStatus(TaskStatus.NEW);
            return;
        }

        boolean isAllSubtasksDone = true;
        boolean isAllSubtasksNew = true;

        for (Integer subtaskId : epic.getSubtasksIds()) {
            TaskStatus subtaskStatus = subtasks.get(subtaskId).getStatus();
            if (subtaskStatus == TaskStatus.IN_PROGRESS) {
                epic.setStatus(TaskStatus.IN_PROGRESS);
                return;
            } else if (subtaskStatus != TaskStatus.DONE) {
                isAllSubtasksDone = false;
            } else {
                isAllSubtasksNew = false;
            }
        }

        if (isAllSubtasksDone) {
            epic.setStatus(TaskStatus.DONE);
            return;
        }

        if (isAllSubtasksNew) {
            epic.setStatus(TaskStatus.NEW);
            return;
        }

        epic.setStatus(TaskStatus.IN_PROGRESS);
    }

    private static int generateId() {
        return tasksCount++;
    }
}