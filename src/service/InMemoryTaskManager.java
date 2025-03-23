package service;

import model.Task;
import model.Epic;
import model.Subtask;
import model.TaskStatus;

import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    private static int tasksCount = 0;
    private final HistoryManager historyManager;
    private final Map<Integer, Task> tasks;
    private final Map<Integer, Epic> epics;
    private final Map<Integer, Subtask> subtasks;

    public InMemoryTaskManager() {
        this.historyManager = Managers.getDefaultHistory();
        this.tasks = new HashMap<>();
        this.epics = new HashMap<>();
        this.subtasks = new HashMap<>();
    }

    public InMemoryTaskManager(List<Task> tasks, List<Epic> epics, List<Subtask> subtasks) {
        this.historyManager = Managers.getDefaultHistory();
        this.tasks = new HashMap<>();
        this.epics = new HashMap<>();
        this.subtasks = new HashMap<>();

        for (Task task : tasks) {
            this.createTaskWithoutChangeId(task);
        }
        for (Epic epic : epics) {
            this.createEpicWithoutChangeId(epic);
        }
        for (Subtask subtask : subtasks) {
            Epic epic = this.epics.get(subtask.getEpicId());

            if (epic == null) {
                continue;
            }

            this.createSubtaskWithoutChangeId(subtask);
        }
    }

    @Override
    public List<Task> getAllTasks() {
        List<Task> resultList = new ArrayList<>(tasks.size());

        for (Task task : tasks.values()) {
            resultList.add(new Task(task));
        }

        return resultList;
    }

    @Override
    public List<Epic> getAllEpics() {
        List<Epic> resultList = new ArrayList<>(epics.size());

        for (Epic epic : epics.values()) {
            resultList.add(new Epic(epic));
        }

        return resultList;
    }

    @Override
    public List<Subtask> getAllSubtasks() {
        List<Subtask> resultList = new ArrayList<>(subtasks.size());

        for (Subtask subtask : subtasks.values()) {
            resultList.add(new Subtask(subtask));
        }

        return resultList;
    }

    @Override
    public void removeAllTasks() {
        for (Integer taskId : tasks.keySet()) {
            historyManager.remove(taskId);
        }
        tasks.clear();
    }

    @Override
    public void removeAllEpics() {
        for (Integer epicId : epics.keySet()) {
            historyManager.remove(epicId);
        }
        epics.clear();

        for (Integer subtaskId : subtasks.keySet()) {
            historyManager.remove(subtaskId);
        }
        subtasks.clear();
    }

    @Override
    public void removeAllSubtasks() {
        for (Integer subtaskId : subtasks.keySet()) {
            historyManager.remove(subtaskId);
        }
        subtasks.clear();

        for (Epic epic : epics.values()) {
            epic.getSubtasksIds().clear();
            epic.setStatus(TaskStatus.NEW);
        }
    }

    @Override
    public Task getTaskById(int id) {
        Task task = tasks.get(id);

        if (task != null) {
            historyManager.add(task);
            return new Task(task);
        }

        return null;
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epic = epics.get(id);

        if (epic != null) {
            historyManager.add(epic);
            return new Epic(epic);
        }

        return null;
    }

    @Override
    public Subtask getSubtaskById(int id) {
        Subtask subtask = subtasks.get(id);

        if (subtask != null) {
            historyManager.add(subtask);
            return new Subtask(subtask);
        }

        return null;
    }

    @Override
    public void createTask(Task task) {
        task.setId(generateId());
        createTaskWithoutChangeId(task);
    }

    @Override
    public void createEpic(Epic epic) {
        epic.setId(generateId());
        createEpicWithoutChangeId(epic);
    }

    @Override
    public Subtask createSubtask(Subtask subtask) {
        Epic epic = epics.get(subtask.getEpicId());

        if (epic == null) {
            return null;
        }

        subtask.setId(generateId());
        createSubtaskWithoutChangeId(subtask);

        return subtask;
    }

    @Override
    public Task updateTask(Task task) {
        Task currentTask = tasks.get(task.getId());

        if (currentTask == null) {
            return null;
        }

        updateAnyTaskData(currentTask, task);

        return task;
    }

    @Override
    public Epic updateEpic(Epic epic) {
        Epic currentEpic = epics.get(epic.getId());

        if (currentEpic == null) {
            return null;
        }

        updateAnyTaskData(currentEpic, epic);
        calculateEpicStatus(currentEpic);

        return epic;
    }

    @Override
    public Subtask updateSubtask(Subtask subtask) {
        Subtask currentSubtask = subtasks.get(subtask.getId());

        if (currentSubtask == null) {
            return null;
        }

        updateAnyTaskData(currentSubtask, subtask);
        Epic epic = epics.get(subtask.getEpicId());
        calculateEpicStatus(epic);

        return subtask;
    }

    @Override
    public Task removeTaskById(int id) {
        historyManager.remove(id);

        return tasks.remove(id);
    }

    @Override
    public Epic removeEpicById(int id) {
        historyManager.remove(id);
        Epic epic = epics.remove(id);

        if (epic == null) {
            return null;
        }

        for (Integer subtaskId : epic.getSubtasksIds()) {
            historyManager.remove(subtaskId);
            subtasks.remove(subtaskId);
        }

        return epic;
    }

    @Override
    public Subtask removeSubtaskById(int id) {
        historyManager.remove(id);
        Subtask subtask = subtasks.remove(id);

        if (subtask == null) {
            return null;
        }

        Epic epic = epics.get(subtask.getEpicId());
        epic.getSubtasksIds().remove((Integer) subtask.getId());
        calculateEpicStatus(epic);

        return subtask;
    }

    @Override
    public List<Subtask> getSubtasksByEpicId(int epicId) {
        Epic epic = epics.get(epicId);
        ArrayList<Subtask> epicSubtasks = new ArrayList<>();

        if (epic == null) {
            return epicSubtasks;
        }

        for (Integer subtaskId : epic.getSubtasksIds()) {
            epicSubtasks.add(new Subtask(subtasks.get(subtaskId)));
        }

        return epicSubtasks;
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    private void updateAnyTaskData(Task oldTask, Task newTask) {
        oldTask.setTitle(newTask.getTitle());
        oldTask.setDescription(newTask.getDescription());
        oldTask.setStatus(newTask.getStatus());
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

    private void createTaskWithoutChangeId(Task task) {
        tasks.put(task.getId(), new Task(task));
    }

    private void createEpicWithoutChangeId(Epic epic) {
        epics.put(epic.getId(), new Epic(epic));
    }

    private void createSubtaskWithoutChangeId(Subtask subtask) {
        Epic epic = epics.get(subtask.getEpicId());
        subtasks.put(subtask.getId(), new Subtask(subtask));
        epic.getSubtasksIds().add(subtask.getId());
        calculateEpicStatus(epic);
    }

    private static int generateId() {
        return ++tasksCount;
    }
}