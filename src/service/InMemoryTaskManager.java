package service;

import exception.NotFoundException;
import exception.OverlapException;
import model.Task;
import model.Epic;
import model.Subtask;
import model.TaskStatus;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {
    protected int tasksCount = 0;
    protected final HistoryManager historyManager;
    protected final Map<Integer, Task> tasks;
    protected final Map<Integer, Epic> epics;
    protected final Map<Integer, Subtask> subtasks;
    protected final Set<Task> prioritizedTasks;

    public InMemoryTaskManager() {
        this.historyManager = Managers.getDefaultHistory();
        this.tasks = new HashMap<>();
        this.epics = new HashMap<>();
        this.subtasks = new HashMap<>();
        this.prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime));
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return prioritizedTasks
                .stream()
                .map(Task::new)
                .collect(Collectors.toList());
    }

    @Override
    public List<Task> getAllTasks() {
        return tasks.values()
                .stream()
                .map(Task::new)
                .collect(Collectors.toList());
    }

    @Override
    public List<Epic> getAllEpics() {
        return epics.values()
                .stream()
                .map(Epic::new)
                .collect(Collectors.toList());
    }

    @Override
    public List<Subtask> getAllSubtasks() {
        return subtasks.values()
                .stream()
                .map(Subtask::new)
                .collect(Collectors.toList());
    }

    @Override
    public void removeAllTasks() {
        for (Map.Entry<Integer, Task> entry : tasks.entrySet()) {
            historyManager.remove(entry.getKey());
            prioritizedTasks.remove(entry.getValue());
        }

        tasks.clear();
    }

    @Override
    public void removeAllEpics() {
        for (Map.Entry<Integer, Epic> entry : epics.entrySet()) {
            historyManager.remove(entry.getKey());
            prioritizedTasks.remove(entry.getValue());
        }
        epics.clear();

        for (Map.Entry<Integer, Subtask> entry : subtasks.entrySet()) {
            historyManager.remove(entry.getKey());
            prioritizedTasks.remove(entry.getValue());
        }
        subtasks.clear();
    }

    @Override
    public void removeAllSubtasks() {
        for (Map.Entry<Integer, Subtask> entry : subtasks.entrySet()) {
            historyManager.remove(entry.getKey());
            prioritizedTasks.remove(entry.getValue());
        }
        subtasks.clear();

        for (Epic epic : epics.values()) {
            prioritizedTasks.remove(epic);
            epic.getSubtasksIds().clear();
            epic.setStatus(TaskStatus.NEW);
            epic.setStartTime(null);
            epic.setEndTime(null);
            epic.setDuration(Duration.ZERO);
        }
    }

    @Override
    public Task getTaskById(int id) throws NotFoundException {
        Task task = tasks.get(id);

        if (task != null) {
            historyManager.add(task);
            return new Task(task);
        }

        throw new NotFoundException("Задача не найдена");
    }

    @Override
    public Epic getEpicById(int id) throws NotFoundException {
        Epic epic = epics.get(id);

        if (epic != null) {
            historyManager.add(epic);
            return new Epic(epic);
        }

        throw new NotFoundException("Эпик не найден");
    }

    @Override
    public Subtask getSubtaskById(int id) {
        Subtask subtask = subtasks.get(id);

        if (subtask != null) {
            historyManager.add(subtask);
            return new Subtask(subtask);
        }

        throw new NotFoundException("Подзадача не найдена");
    }

    @Override
    public Task createTask(Task task) throws OverlapException {
        task.setId(generateId());
        Task newTask = new Task(task);

        if (newTask.getStartTime() != null && newTask.getEndTime() != null) {
            if (isAnyTaskInManagerOverlap(newTask)) {
                throw new OverlapException("Задача пересекается по времени выполнения");
            }
            prioritizedTasks.add(newTask);
        }

        tasks.put(task.getId(), newTask);

        return task;
    }

    @Override
    public void createEpic(Epic epic) {
        epic.setId(generateId());
        epic.setStatus(TaskStatus.NEW);
        epic.setStartTime(null);
        epic.setEndTime(null);
        epic.setDuration(Duration.ZERO);
        epic.setSubtasksIds(new ArrayList<>());
        epics.put(epic.getId(), new Epic(epic));
    }

    @Override
    public Subtask createSubtask(Subtask subtask) throws NotFoundException, OverlapException {
        Epic epic = epics.get(subtask.getEpicId());

        if (epic == null) {
            throw new NotFoundException("Эпик подзадачи не найден");
        }

        subtask.setId(generateId());
        Subtask newSubtask = new Subtask(subtask);

        if (newSubtask.getStartTime() != null && newSubtask.getEndTime() != null) {
            if (isAnyTaskInManagerOverlap(subtask)) {
                throw new OverlapException("Подзадача пересекается по времени выполнения");
            }
            prioritizedTasks.add(newSubtask);
        }

        subtasks.put(subtask.getId(), newSubtask);
        epic.getSubtasksIds().add(subtask.getId());
        calculateEpicStatus(epic);
        calculateEpicDatesAndDuration(epic);

        return subtask;
    }

    @Override
    public Task updateTask(Task task) throws NotFoundException, OverlapException {
        Task currentTask = tasks.get(task.getId());

        if (currentTask == null) {
            throw new NotFoundException("Задача не найдена");
        }

        prioritizedTasks.remove(currentTask);

        if (currentTask.getStartTime() != null && currentTask.getEndTime() != null) {
            if (isAnyTaskInManagerOverlap(task)) {
                throw new OverlapException("Задача пересекается по времени выполнения");
            }
            prioritizedTasks.add(currentTask);
        }

        updateAnyTaskData(currentTask, task);

        return task;
    }

    @Override
    public Epic updateEpic(Epic epic) throws NotFoundException {
        Epic currentEpic = epics.get(epic.getId());

        if (currentEpic == null) {
            throw new NotFoundException("Эпик не найден");
        }

        updateAnyTaskData(currentEpic, epic);
        calculateEpicStatus(currentEpic);
        calculateEpicDatesAndDuration(currentEpic);

        return epic;
    }

    @Override
    public Subtask updateSubtask(Subtask subtask) throws NotFoundException, OverlapException {
        Subtask currentSubtask = subtasks.get(subtask.getId());

        if (currentSubtask == null) {
            throw new NotFoundException("Подзадача не найдена");
        }

        prioritizedTasks.remove(currentSubtask);

        if (currentSubtask.getStartTime() != null && currentSubtask.getEndTime() != null) {
            if (isAnyTaskInManagerOverlap(subtask)) {
                throw new OverlapException("Подзадача пересекается по времени выполнения");
            }
            prioritizedTasks.add(currentSubtask);
        }

        updateAnyTaskData(currentSubtask, subtask);
        Epic epic = epics.get(subtask.getEpicId());
        calculateEpicStatus(epic);
        calculateEpicDatesAndDuration(epic);

        return subtask;
    }

    @Override
    public Task removeTaskById(int id) throws NotFoundException {
        Task task = tasks.remove(id);

        if (task == null) {
            throw new NotFoundException("Задача не найдена");
        }

        historyManager.remove(id);
        prioritizedTasks.remove(task);
        return task;
    }

    @Override
    public Epic removeEpicById(int id) throws NotFoundException {
        Epic epic = epics.remove(id);

        if (epic == null) {
            throw new NotFoundException("Эпик не найден");
        }

        historyManager.remove(id);

        for (Integer subtaskId : epic.getSubtasksIds()) {
            historyManager.remove(subtaskId);
            prioritizedTasks.remove(subtasks.get(subtaskId));
            subtasks.remove(subtaskId);
        }

        return epic;
    }

    @Override
    public Subtask removeSubtaskById(int id) throws NotFoundException {
        Subtask subtask = subtasks.remove(id);

        if (subtask == null) {
            throw new NotFoundException("Подзадача не найдена");
        }

        historyManager.remove(id);
        prioritizedTasks.remove(subtask);

        Epic epic = epics.get(subtask.getEpicId());
        epic.getSubtasksIds().remove((Integer) subtask.getId());
        calculateEpicStatus(epic);
        calculateEpicDatesAndDuration(epic);

        return subtask;
    }

    @Override
    public List<Subtask> getSubtasksByEpicId(int epicId) {
        Epic epic = epics.get(epicId);

        if (epic == null) {
            return new ArrayList<>();
        }

        return epic.getSubtasksIds()
                .stream()
                .map(subtasks::get)
                .map(Subtask::new)
                .collect(Collectors.toList());
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    private void updateAnyTaskData(Task oldTask, Task newTask) {
        oldTask.setTitle(newTask.getTitle());
        oldTask.setDescription(newTask.getDescription());
        oldTask.setStatus(newTask.getStatus());
        oldTask.setStartTime(newTask.getStartTime());
        oldTask.setDuration(newTask.getDuration());
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

    private void calculateEpicDatesAndDuration(Epic epic) {
        List<Subtask> sortedSubtasks = epic.getSubtasksIds()
                .stream()
                .map(subtasks::get)
                .filter(subtask -> subtask.getStartTime() != null && subtask.getEndTime() != null)
                .sorted(Comparator.comparing(Subtask::getStartTime))
                .toList();

        if (sortedSubtasks.isEmpty()) {
            epic.setStartTime(null);
            epic.setEndTime(null);
            epic.setDuration(Duration.ZERO);
            return;
        }

        epic.setStartTime(sortedSubtasks.getFirst().getStartTime());
        epic.setEndTime(sortedSubtasks.getLast().getEndTime());
        epic.setDuration(Duration.ofMinutes(sortedSubtasks
                .stream()
                .map(Subtask::getDuration)
                .mapToLong(Duration::toMinutes)
                .sum())
        );
    }

    private boolean isAnyTaskInManagerOverlap(Task checkTask) {
        return getPrioritizedTasks()
                .stream()
                .anyMatch(task -> isTwoTasksOverlap(checkTask, task));
    }

    private boolean isTwoTasksOverlap(Task firstTask, Task secondTask) {
        if (firstTask.getStartTime().isEqual(secondTask.getEndTime())
                || firstTask.getEndTime().isEqual(secondTask.getStartTime())
        ) {
            return true;
        }

        return firstTask.getEndTime().isAfter(secondTask.getStartTime())
                && firstTask.getStartTime().isBefore(secondTask.getEndTime());
    }

    private int generateId() {
        return ++tasksCount;
    }
}