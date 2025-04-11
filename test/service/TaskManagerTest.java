package service;

import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public abstract class TaskManagerTest {
    protected static final LocalDateTime currentDate = LocalDateTime.now();
    protected TaskManager taskManager;

    @Test
    void createTask() {
        Task task = new Task(
                "Title",
                "Description",
                TaskStatus.NEW,
                currentDate,
                Duration.ofMinutes(60)
        );
        Task taskWithoutStartTime = new Task(
                "Title",
                "Description",
                TaskStatus.NEW,
                null,
                null
        );
        Task taskWithOverlapStartTime = new Task(
                "Title",
                "Description",
                TaskStatus.NEW,
                currentDate,
                Duration.ofMinutes(30)
        );
        taskManager.createTask(task);
        Task taskInManager = taskManager.getTaskById(task.getId());

        assertEquals("Title", taskInManager.getTitle());
        assertEquals("Description", taskInManager.getDescription());
        assertEquals(TaskStatus.NEW, taskInManager.getStatus());
        assertEquals(currentDate, taskInManager.getStartTime());
        assertEquals(currentDate.plusMinutes(60), taskInManager.getEndTime());
        assertEquals(60, taskInManager.getDuration().toMinutes());
        assertTrue(taskInManager.getId() > 0);
        assertEquals(task, taskInManager);

        List<Task> tasksInManager = taskManager.getAllTasks();
        List<Task> prioritizedTasks = taskManager.getPrioritizedTasks();

        assertNotNull(tasksInManager);
        assertEquals(1, tasksInManager.size());
        assertEquals(taskInManager, tasksInManager.getFirst());
        assertNotNull(prioritizedTasks);
        assertEquals(1, prioritizedTasks.size());

        taskManager.createTask(taskWithoutStartTime);
        tasksInManager = taskManager.getAllTasks();
        prioritizedTasks = taskManager.getPrioritizedTasks();

        assertEquals(2, tasksInManager.size());
        assertEquals(taskWithoutStartTime, tasksInManager.get(1));
        assertEquals(1, prioritizedTasks.size());

        Task notCreatedTask = taskManager.createTask(taskWithOverlapStartTime);
        tasksInManager = taskManager.getAllTasks();
        prioritizedTasks = taskManager.getPrioritizedTasks();

        assertEquals(2, tasksInManager.size());
        assertEquals(1, prioritizedTasks.size());
        assertNull(notCreatedTask);

        task.setStartTime(currentDate.plusDays(1));
        task.setDuration(Duration.ofMinutes(120));
        taskManager.createTask(task);

        assertEquals(3, taskManager.getAllTasks().size());
        assertNotEquals(task, taskInManager);
    }

    @Test
    void createEpic() {
        Epic epic = new Epic("Title", "Description");
        taskManager.createEpic(epic);
        Epic epicInManager = taskManager.getEpicById(epic.getId());

        assertEquals("Title", epicInManager.getTitle());
        assertEquals("Description", epicInManager.getDescription());
        assertEquals(TaskStatus.NEW, epicInManager.getStatus());
        assertEquals(0, epic.getDuration().toMinutes());
        assertNull(epic.getStartTime());
        assertNull(epic.getEndTime());
        assertTrue(epicInManager.getId() > 0);
        assertEquals(epic, epicInManager);

        List<Epic> epicsInManager = taskManager.getAllEpics();
        List<Task> prioritizedTasks = taskManager.getPrioritizedTasks();

        assertNotNull(epicsInManager);
        assertNotNull(prioritizedTasks);
        assertEquals(1, epicsInManager.size());
        assertEquals(epicInManager, epicsInManager.getFirst());
        assertTrue(prioritizedTasks.isEmpty());

        taskManager.createEpic(epic);

        assertEquals(2, taskManager.getAllEpics().size());
        assertNotEquals(epic, epicInManager);
    }

    @Test
    void createSubtask() {
        Epic epic = new Epic("T", "D");
        taskManager.createEpic(epic);
        Subtask subtask = new Subtask(
                "T",
                "D",
                TaskStatus.NEW,
                currentDate,
                Duration.ofMinutes(60),
                epic.getId()
        );
        Subtask subtaskWithoutStartTime = new Subtask(
                "T",
                "D",
                TaskStatus.NEW,
                null,
                null,
                epic.getId()
        );
        Subtask subtaskWithOverlapStartTime = new Subtask(
                "T",
                "D",
                TaskStatus.NEW,
                currentDate,
                Duration.ofMinutes(30),
                epic.getId()
        );
        taskManager.createSubtask(subtask);
        Subtask subtaskInManager = taskManager.getSubtaskById(subtask.getId());
        Epic epicInManager = taskManager.getEpicById(epic.getId());
        Subtask subtaskWithoutEpicId = new Subtask(
                "T",
                "D",
                TaskStatus.NEW,
                null,
                null,
                subtaskInManager.getId()
        );
        Subtask firstNotCreatedSubtask = taskManager.createSubtask(subtaskWithoutEpicId);
        Subtask secondNotCreatedSubtask = taskManager.createSubtask(subtaskWithOverlapStartTime);

        assertEquals("T", subtaskInManager.getTitle());
        assertEquals("D", subtaskInManager.getDescription());
        assertEquals(TaskStatus.NEW, subtaskInManager.getStatus());
        assertEquals(currentDate, subtaskInManager.getStartTime());
        assertEquals(currentDate.plusMinutes(60), subtaskInManager.getEndTime());
        assertEquals(60, subtaskInManager.getDuration().toMinutes());
        assertTrue(subtaskInManager.getId() > 0);
        assertEquals(subtask, subtaskInManager);
        assertEquals(1, epicInManager.getSubtasksIds().size());
        assertEquals(subtaskInManager.getId(), epicInManager.getSubtasksIds().getFirst());
        assertNull(firstNotCreatedSubtask);
        assertNull(secondNotCreatedSubtask);

        List<Subtask> subtasksInManager = taskManager.getAllSubtasks();
        List<Task> prioritizedTasks = taskManager.getPrioritizedTasks();

        assertNotNull(subtasksInManager);
        assertEquals(1, subtasksInManager.size());
        assertEquals(subtaskInManager, subtasksInManager.getFirst());
        assertNotNull(prioritizedTasks);
        assertEquals(1, prioritizedTasks.size());

        taskManager.createSubtask(subtaskWithoutStartTime);
        subtasksInManager = taskManager.getAllSubtasks();
        prioritizedTasks = taskManager.getPrioritizedTasks();

        assertEquals(2, subtasksInManager.size());
        assertEquals(subtaskWithoutStartTime, subtasksInManager.get(1));
        assertEquals(1, prioritizedTasks.size());

        taskManager.createSubtask(subtask);

        assertEquals(2, taskManager.getAllSubtasks().size());
        assertNotEquals(subtask, subtaskInManager);
    }

    @Test
    void updateTask() {
        Task firstTask = new Task(
                "Title",
                "Description",
                TaskStatus.NEW,
                currentDate,
                Duration.ofMinutes(60)
        );
        Task secondTask = new Task(
                "NewTitle",
                "NewDescription",
                TaskStatus.IN_PROGRESS,
                currentDate.plusDays(1),
                Duration.ofMinutes(120)
        );
        Task thirdTask = new Task(
                "Title",
                "Description",
                TaskStatus.NEW,
                currentDate,
                Duration.ofMinutes(60)
        );

        taskManager.createTask(firstTask);
        secondTask.setId(firstTask.getId());
        taskManager.updateTask(secondTask);
        Task updatedTaskInManager = taskManager.getTaskById(secondTask.getId());
        List<Task> prioritizedTasks = taskManager.getPrioritizedTasks();

        assertEquals("NewTitle", updatedTaskInManager.getTitle());
        assertEquals("NewDescription", updatedTaskInManager.getDescription());
        assertEquals(TaskStatus.IN_PROGRESS, updatedTaskInManager.getStatus());
        assertEquals(currentDate.plusDays(1), updatedTaskInManager.getStartTime());
        assertEquals(currentDate.plusDays(1).plusMinutes(120), updatedTaskInManager.getEndTime());
        assertEquals(120, updatedTaskInManager.getDuration().toMinutes());
        assertEquals(firstTask, updatedTaskInManager);
        assertEquals(1, prioritizedTasks.size());

        thirdTask.setId(-1);
        Task notUpdatedTask = taskManager.updateTask(thirdTask);

        assertNull(notUpdatedTask);
    }

    @Test
    void updateEpic() {
        Epic firstEpic = new Epic("Title", "Description");
        Epic secondEpic = new Epic("NewTitle", "NewDescription");
        Epic thirdEpic = new Epic("Title", "Description");

        taskManager.createEpic(firstEpic);
        secondEpic.setId(firstEpic.getId());
        taskManager.updateEpic(secondEpic);
        Epic updatedEpicInManager = taskManager.getEpicById(secondEpic.getId());
        List<Task> prioritizedTasks = taskManager.getPrioritizedTasks();

        assertEquals("NewTitle", updatedEpicInManager.getTitle());
        assertEquals("NewDescription", updatedEpicInManager.getDescription());
        assertEquals(TaskStatus.NEW, updatedEpicInManager.getStatus());
        assertEquals(0, updatedEpicInManager.getDuration().toMinutes());
        assertEquals(firstEpic, updatedEpicInManager);
        assertTrue(prioritizedTasks.isEmpty());
        assertNull(updatedEpicInManager.getStartTime());
        assertNull(updatedEpicInManager.getEndTime());

        thirdEpic.setId(-1);
        Epic notUpdatedEpic = taskManager.updateEpic(thirdEpic);

        assertNull(notUpdatedEpic);
    }

    @Test
    void updateSubtask() {
        Epic epic = new Epic("Title", "Description");
        taskManager.createEpic(epic);

        Subtask firstSubtask = new Subtask(
                "T",
                "D",
                TaskStatus.NEW,
                currentDate,
                Duration.ofMinutes(60),
                epic.getId()
        );
        Subtask secondSubtask = new Subtask(
                "NT",
                "ND",
                TaskStatus.IN_PROGRESS,
                currentDate.plusDays(1),
                Duration.ofMinutes(120),
                epic.getId()
        );
        Subtask thirdSubtask = new Subtask(
                "T",
                "D",
                TaskStatus.NEW,
                currentDate,
                Duration.ofMinutes(60),
                epic.getId()
        );

        taskManager.createSubtask(firstSubtask);
        secondSubtask.setId(firstSubtask.getId());
        taskManager.updateSubtask(secondSubtask);
        Subtask updatedSubtaskInManager = taskManager.getSubtaskById(secondSubtask.getId());
        List<Task> prioritizedTasks = taskManager.getPrioritizedTasks();

        assertEquals("NT", updatedSubtaskInManager.getTitle());
        assertEquals("ND", updatedSubtaskInManager.getDescription());
        assertEquals(TaskStatus.IN_PROGRESS, updatedSubtaskInManager.getStatus());
        assertEquals(currentDate.plusDays(1), updatedSubtaskInManager.getStartTime());
        assertEquals(currentDate.plusDays(1).plusMinutes(120), updatedSubtaskInManager.getEndTime());
        assertEquals(120, updatedSubtaskInManager.getDuration().toMinutes());
        assertEquals(firstSubtask.getEpicId(), updatedSubtaskInManager.getEpicId());
        assertEquals(firstSubtask, updatedSubtaskInManager);
        assertEquals(1, prioritizedTasks.size());

        thirdSubtask.setId(-1);
        Subtask notUpdatedSubtask = taskManager.updateSubtask(thirdSubtask);

        assertNull(notUpdatedSubtask);
    }

    @Test
    void removeTaskById() {
        Task task = new Task(
                "Title",
                "Description",
                TaskStatus.NEW,
                currentDate,
                Duration.ofMinutes(60)
        );
        taskManager.createTask(task);
        Task deletedTask = taskManager.removeTaskById(task.getId());
        List<Task> prioritizedTasks = taskManager.getPrioritizedTasks();

        assertEquals(task, deletedTask);
        assertTrue(prioritizedTasks.isEmpty());
        assertNull(taskManager.getTaskById(deletedTask.getId()));
        assertNull(taskManager.removeTaskById(-1));
    }

    @Test
    void removeEpicById() {
        Epic epic = new Epic("Title", "Description");
        taskManager.createEpic(epic);
        Subtask subtask = new Subtask(
                "Title",
                "Description",
                TaskStatus.NEW,
                currentDate,
                Duration.ofMinutes(60),
                epic.getId()
        );
        taskManager.createSubtask(subtask);
        Epic deletedEpic = taskManager.removeEpicById(epic.getId());
        List<Task> prioritizedTasks = taskManager.getPrioritizedTasks();

        assertEquals(epic, deletedEpic);
        assertTrue(prioritizedTasks.isEmpty());
        assertNull(taskManager.getEpicById(deletedEpic.getId()));
        assertNull(taskManager.getSubtaskById(subtask.getId()));
        assertNull(taskManager.removeEpicById(-1));
    }

    @Test
    void removeSubtaskById() {
        Epic epic = new Epic("Title", "Description");
        taskManager.createEpic(epic);
        Subtask subtask = new Subtask(
                "Title",
                "Description",
                TaskStatus.NEW,
                currentDate,
                Duration.ofMinutes(60),
                epic.getId()
        );
        taskManager.createSubtask(subtask);
        Subtask deletedSubtask = taskManager.removeSubtaskById(subtask.getId());
        List<Task> prioritizedTasks = taskManager.getPrioritizedTasks();

        assertEquals(subtask, deletedSubtask);
        assertTrue(prioritizedTasks.isEmpty());
        assertNull(taskManager.getSubtaskById(subtask.getId()));
        assertTrue(taskManager.getEpicById(epic.getId()).getSubtasksIds().isEmpty());
        assertNull(taskManager.removeSubtaskById(-1));
    }

    @Test
    void removeAllTasks() {
        Task firstTask = new Task(
                "Title",
                "Description",
                TaskStatus.NEW,
                currentDate,
                Duration.ofMinutes(60)
        );
        Task secondTask = new Task(
                "Title",
                "Description",
                TaskStatus.NEW,
                currentDate.plusDays(1),
                Duration.ofMinutes(120)
        );
        taskManager.removeAllTasks();
        List<Task> prioritizedTasks = taskManager.getPrioritizedTasks();

        assertNotNull(taskManager.getAllTasks());
        assertTrue(taskManager.getAllTasks().isEmpty());
        assertTrue(prioritizedTasks.isEmpty());

        taskManager.createTask(firstTask);
        taskManager.createTask(secondTask);
        prioritizedTasks = taskManager.getPrioritizedTasks();

        assertEquals(2, taskManager.getAllTasks().size());
        assertEquals(2, prioritizedTasks.size());

        taskManager.removeAllTasks();
        prioritizedTasks = taskManager.getPrioritizedTasks();

        assertTrue(taskManager.getAllTasks().isEmpty());
        assertTrue(prioritizedTasks.isEmpty());
    }

    @Test
    void removeAllEpics() {
        Epic firstEpic = new Epic("Title", "Description");
        Epic secondEpic = new Epic("Title", "Description");
        taskManager.removeAllEpics();

        assertNotNull(taskManager.getAllEpics());
        assertTrue(taskManager.getAllEpics().isEmpty());

        taskManager.createEpic(firstEpic);
        taskManager.createEpic(secondEpic);
        Subtask firstSubtask = new Subtask(
                "Title",
                "Description",
                TaskStatus.NEW,
                currentDate,
                Duration.ofMinutes(60),
                firstEpic.getId()
        );
        Subtask secondSubtask = new Subtask(
                "Title",
                "Description",
                TaskStatus.NEW,
                currentDate.plusDays(1),
                Duration.ofMinutes(120),
                secondEpic.getId()
        );
        taskManager.createSubtask(firstSubtask);
        taskManager.createSubtask(secondSubtask);
        List<Task> prioritizedTasks = taskManager.getPrioritizedTasks();

        assertEquals(2, taskManager.getAllEpics().size());
        assertEquals(2, taskManager.getAllSubtasks().size());
        assertEquals(2, prioritizedTasks.size());

        taskManager.removeAllEpics();
        prioritizedTasks = taskManager.getPrioritizedTasks();

        assertTrue(taskManager.getAllEpics().isEmpty());
        assertTrue(taskManager.getAllSubtasks().isEmpty());
        assertTrue(prioritizedTasks.isEmpty());
    }

    @Test
    void removeAllSubtasks() {
        Epic firstEpic = new Epic("Title", "Description");
        Epic secondEpic = new Epic("Title", "Description");
        taskManager.createEpic(firstEpic);
        taskManager.createEpic(secondEpic);
        Subtask firstSubtask = new Subtask(
                "Title",
                "Description",
                TaskStatus.NEW,
                currentDate,
                Duration.ofMinutes(60),
                firstEpic.getId()
        );
        Subtask secondSubtask = new Subtask(
                "Title",
                "Description",
                TaskStatus.NEW,
                currentDate.plusDays(1),
                Duration.ofMinutes(120),
                secondEpic.getId()
        );
        List<Task> prioritizedTasks = taskManager.getPrioritizedTasks();

        assertNotNull(taskManager.getAllSubtasks());
        assertTrue(taskManager.getAllSubtasks().isEmpty());
        assertTrue(prioritizedTasks.isEmpty());

        taskManager.createSubtask(firstSubtask);
        taskManager.createSubtask(secondSubtask);
        prioritizedTasks = taskManager.getPrioritizedTasks();

        assertEquals(2, taskManager.getAllSubtasks().size());
        assertEquals(2, taskManager.getAllEpics().size());
        assertEquals(2, prioritizedTasks.size());

        taskManager.removeAllSubtasks();
        prioritizedTasks = taskManager.getPrioritizedTasks();

        assertTrue(taskManager.getAllSubtasks().isEmpty());
        assertTrue(prioritizedTasks.isEmpty());
        assertTrue(taskManager.getEpicById(firstEpic.getId()).getSubtasksIds().isEmpty());
        assertTrue(taskManager.getEpicById(secondEpic.getId()).getSubtasksIds().isEmpty());
    }

    @Test
    void taskInManagerCannotBeChangedWithoutUpdateMethod() {
        Task task = new Task(
                "Title",
                "Description",
                TaskStatus.NEW,
                currentDate,
                Duration.ofMinutes(60)
        );
        taskManager.createTask(task);
        task.setId(-1);
        task.setTitle("NewTitle");
        task.setDescription("NewDescription");
        task.setStatus(TaskStatus.IN_PROGRESS);
        task.setStartTime(currentDate.plusDays(1));
        task.setDuration(Duration.ofMinutes(120));
        Task taskInManager = taskManager.getAllTasks().getFirst();

        assertNotEquals(task.getId(), taskInManager.getId());
        assertNotEquals(task.getStatus(), taskInManager.getStatus());
        assertNotEquals(task.getDescription(), taskInManager.getDescription());
        assertNotEquals(task.getTitle(), taskInManager.getTitle());
        assertNotEquals(task.getStartTime(), taskInManager.getStartTime());
        assertNotEquals(task.getEndTime(), taskInManager.getEndTime());
        assertNotEquals(task.getDuration(), taskInManager.getDuration());
    }

    @Test
    void epicInManagerCannotBeChangedWithoutUpdateMethod() {
        Epic epic = new Epic("Title", "Description");
        taskManager.createEpic(epic);
        epic.setId(-1);
        epic.setTitle("NewTitle");
        epic.setDescription("NewDescription");
        epic.setStatus(TaskStatus.IN_PROGRESS);
        epic.setStartTime(currentDate.plusDays(1));
        epic.setDuration(Duration.ofMinutes(120));
        epic.setEndTime(currentDate.plusDays(1).plusMinutes(120));
        Epic epicInManager = taskManager.getAllEpics().getFirst();

        assertNotEquals(epic.getId(), epicInManager.getId());
        assertNotEquals(epic.getStatus(), epicInManager.getStatus());
        assertNotEquals(epic.getDescription(), epicInManager.getDescription());
        assertNotEquals(epic.getTitle(), epicInManager.getTitle());
        assertNotEquals(epic.getStartTime(), epicInManager.getStartTime());
        assertNotEquals(epic.getEndTime(), epicInManager.getEndTime());
        assertNotEquals(epic.getDuration(), epicInManager.getDuration());
    }

    @Test
    void subtaskInManagerCannotBeChangedWithoutUpdateMethod() {
        Epic epic = new Epic("Title", "Description");
        taskManager.createEpic(epic);
        Subtask subtask = new Subtask(
                "Title",
                "Description",
                TaskStatus.NEW,
                currentDate,
                Duration.ofMinutes(60),
                epic.getId()
        );
        taskManager.createSubtask(subtask);
        subtask.setId(-1);
        subtask.setTitle("NewTitle");
        subtask.setDescription("NewDescription");
        subtask.setStatus(TaskStatus.IN_PROGRESS);
        subtask.setStartTime(currentDate.plusDays(1));
        subtask.setDuration(Duration.ofMinutes(120));
        Subtask subtaskInManager = taskManager.getAllSubtasks().getFirst();

        assertNotEquals(subtask.getId(), subtaskInManager.getId());
        assertNotEquals(subtask.getStatus(), subtaskInManager.getStatus());
        assertNotEquals(subtask.getDescription(), subtaskInManager.getDescription());
        assertNotEquals(subtask.getTitle(), subtaskInManager.getTitle());
        assertNotEquals(subtask.getStartTime(), subtaskInManager.getStartTime());
        assertNotEquals(subtask.getEndTime(), subtaskInManager.getEndTime());
        assertNotEquals(subtask.getDuration(), subtaskInManager.getDuration());
    }

    @Test
    void getTaskById() {
        Task task = new Task(
                "Title",
                "Description",
                TaskStatus.NEW,
                currentDate,
                Duration.ofMinutes(60)
        );
        taskManager.createTask(task);

        assertEquals(task, taskManager.getTaskById(task.getId()));
        assertNull(taskManager.getTaskById(-1));
    }

    @Test
    void getEpicById() {
        Epic epic = new Epic("Title", "Description");
        taskManager.createEpic(epic);

        assertEquals(epic, taskManager.getEpicById(epic.getId()));
        assertNull(taskManager.getEpicById(-1));
    }

    @Test
    void getSubtaskById() {
        Epic epic = new Epic("Title", "Description");
        taskManager.createEpic(epic);
        Subtask subtask = new Subtask(
                "Title",
                "Description",
                TaskStatus.NEW,
                currentDate,
                Duration.ofMinutes(60),
                epic.getId()
        );
        taskManager.createSubtask(subtask);

        assertEquals(subtask, taskManager.getSubtaskById(subtask.getId()));
        assertNull(taskManager.getSubtaskById(-1));
    }

    @Test
    void taskInManagerCannotBeChangedAfterGetById() {
        Task task = new Task(
                "Title",
                "Description",
                TaskStatus.NEW,
                currentDate,
                Duration.ofMinutes(60)
        );
        taskManager.createTask(task);
        Task taskInManagerAfterCreate = taskManager.getTaskById(task.getId());
        taskInManagerAfterCreate.setTitle("NewTitle");
        taskInManagerAfterCreate.setDescription("NewDescription");
        taskInManagerAfterCreate.setStatus(TaskStatus.IN_PROGRESS);
        taskInManagerAfterCreate.setStartTime(currentDate.plusDays(1));
        taskInManagerAfterCreate.setDuration(Duration.ofMinutes(120));
        taskInManagerAfterCreate.setId(-1);
        Task taskInManagerAfterChanges = taskManager.getTaskById(task.getId());

        assertNotEquals(taskInManagerAfterCreate.getId(), taskInManagerAfterChanges.getId());
        assertNotEquals(taskInManagerAfterCreate.getTitle(), taskInManagerAfterChanges.getTitle());
        assertNotEquals(taskInManagerAfterCreate.getDescription(), taskInManagerAfterChanges.getDescription());
        assertNotEquals(taskInManagerAfterCreate.getStatus(), taskInManagerAfterChanges.getStatus());
        assertNotEquals(taskInManagerAfterCreate.getStartTime(), taskInManagerAfterChanges.getStartTime());
        assertNotEquals(taskInManagerAfterCreate.getDuration(), taskInManagerAfterChanges.getDuration());
        assertNotEquals(taskInManagerAfterCreate.getEndTime(), taskInManagerAfterChanges.getEndTime());
    }

    @Test
    void epicInManagerCannotBeChangedAfterGetById() {
        Epic epic = new Epic("Title", "Description");
        taskManager.createEpic(epic);
        Epic epicInManagerAfterCreate = taskManager.getEpicById(epic.getId());
        epicInManagerAfterCreate.setTitle("NewTitle");
        epicInManagerAfterCreate.setDescription("NewDescription");
        epicInManagerAfterCreate.setStatus(TaskStatus.IN_PROGRESS);
        epicInManagerAfterCreate.setStartTime(currentDate.plusDays(1));
        epicInManagerAfterCreate.setEndTime(currentDate.plusDays(1).plusMinutes(120));
        epicInManagerAfterCreate.setDuration(Duration.ofMinutes(120));
        epicInManagerAfterCreate.setId(-1);
        Epic epicInManagerAfterChanges = taskManager.getEpicById(epic.getId());

        assertNotEquals(epicInManagerAfterCreate.getId(), epicInManagerAfterChanges.getId());
        assertNotEquals(epicInManagerAfterCreate.getTitle(), epicInManagerAfterChanges.getTitle());
        assertNotEquals(epicInManagerAfterCreate.getDescription(), epicInManagerAfterChanges.getDescription());
        assertNotEquals(epicInManagerAfterCreate.getStatus(), epicInManagerAfterChanges.getStatus());
        assertNotEquals(epicInManagerAfterCreate.getStartTime(), epicInManagerAfterChanges.getStartTime());
        assertNotEquals(epicInManagerAfterCreate.getDuration(), epicInManagerAfterChanges.getDuration());
        assertNotEquals(epicInManagerAfterCreate.getEndTime(), epicInManagerAfterChanges.getEndTime());
    }

    @Test
    void subtaskInManagerCannotBeChangedAfterGetById() {
        Epic epic = new Epic("Title", "Description");
        taskManager.createEpic(epic);
        Subtask subtask = new Subtask(
                "Title",
                "Description",
                TaskStatus.NEW,
                currentDate,
                Duration.ofMinutes(60),
                epic.getId()
        );
        taskManager.createSubtask(subtask);
        Subtask subtaskInManagerAfterCreate = taskManager.getSubtaskById(subtask.getId());
        subtaskInManagerAfterCreate.setTitle("NewTitle");
        subtaskInManagerAfterCreate.setDescription("NewDescription");
        subtaskInManagerAfterCreate.setStatus(TaskStatus.IN_PROGRESS);
        subtaskInManagerAfterCreate.setStartTime(currentDate.plusDays(1));
        subtaskInManagerAfterCreate.setDuration(Duration.ofMinutes(120));
        subtaskInManagerAfterCreate.setId(-1);
        Subtask subtaskInManagerAfterChanges = taskManager.getSubtaskById(subtask.getId());

        assertNotEquals(subtaskInManagerAfterCreate.getId(), subtaskInManagerAfterChanges.getId());
        assertNotEquals(subtaskInManagerAfterCreate.getTitle(), subtaskInManagerAfterChanges.getTitle());
        assertNotEquals(subtaskInManagerAfterCreate.getDescription(), subtaskInManagerAfterChanges.getDescription());
        assertNotEquals(subtaskInManagerAfterCreate.getStatus(), subtaskInManagerAfterChanges.getStatus());
        assertNotEquals(subtaskInManagerAfterCreate.getStartTime(), subtaskInManagerAfterChanges.getStartTime());
        assertNotEquals(subtaskInManagerAfterCreate.getDuration(), subtaskInManagerAfterChanges.getDuration());
        assertNotEquals(subtaskInManagerAfterCreate.getEndTime(), subtaskInManagerAfterChanges.getEndTime());
    }

    @Test
    void tasksInManagerCannotBeChangedAfterGetAllTasks() {
        Task task = new Task(
                "Title",
                "Description",
                TaskStatus.NEW,
                currentDate,
                Duration.ofMinutes(60)
        );
        taskManager.createTask(task);
        Task taskInManagerAfterCreate = taskManager.getAllTasks().getFirst();
        taskInManagerAfterCreate.setTitle("NewTitle");
        taskInManagerAfterCreate.setDescription("NewDescription");
        taskInManagerAfterCreate.setStatus(TaskStatus.IN_PROGRESS);
        taskInManagerAfterCreate.setStartTime(currentDate.plusDays(1));
        taskInManagerAfterCreate.setDuration(Duration.ofMinutes(120));
        taskInManagerAfterCreate.setId(-1);
        Task taskInManagerAfterChanges = taskManager.getTaskById(task.getId());

        assertNotEquals(taskInManagerAfterCreate.getId(), taskInManagerAfterChanges.getId());
        assertNotEquals(taskInManagerAfterCreate.getTitle(), taskInManagerAfterChanges.getTitle());
        assertNotEquals(taskInManagerAfterCreate.getDescription(), taskInManagerAfterChanges.getDescription());
        assertNotEquals(taskInManagerAfterCreate.getStatus(), taskInManagerAfterChanges.getStatus());
        assertNotEquals(taskInManagerAfterCreate.getStartTime(), taskInManagerAfterChanges.getStartTime());
        assertNotEquals(taskInManagerAfterCreate.getEndTime(), taskInManagerAfterChanges.getEndTime());
        assertNotEquals(taskInManagerAfterCreate.getDuration(), taskInManagerAfterChanges.getDuration());

        List<Task> tasks = taskManager.getAllTasks();
        tasks.clear();

        assertEquals(1, taskManager.getAllTasks().size());
    }

    @Test
    void epicsInManagerCannotBeChangedAfterGetAllEpics() {
        Epic epic = new Epic("Title", "Description");
        taskManager.createEpic(epic);
        Epic epicInManagerAfterCreate = taskManager.getAllEpics().getFirst();
        epicInManagerAfterCreate.setTitle("NewTitle");
        epicInManagerAfterCreate.setDescription("NewDescription");
        epicInManagerAfterCreate.setStatus(TaskStatus.IN_PROGRESS);
        epicInManagerAfterCreate.setStartTime(currentDate.plusDays(1));
        epicInManagerAfterCreate.setDuration(Duration.ofMinutes(120));
        epicInManagerAfterCreate.setEndTime(currentDate.plusDays(1).plusMinutes(120));
        Epic epicInManagerAfterChanges = taskManager.getEpicById(epic.getId());

        assertNotEquals(epicInManagerAfterCreate.getTitle(), epicInManagerAfterChanges.getTitle());
        assertNotEquals(epicInManagerAfterCreate.getDescription(), epicInManagerAfterChanges.getDescription());
        assertNotEquals(epicInManagerAfterCreate.getStatus(), epicInManagerAfterChanges.getStatus());
        assertNotEquals(epicInManagerAfterCreate.getStartTime(), epicInManagerAfterChanges.getStartTime());
        assertNotEquals(epicInManagerAfterCreate.getEndTime(), epicInManagerAfterChanges.getEndTime());
        assertNotEquals(epicInManagerAfterCreate.getDuration(), epicInManagerAfterChanges.getDuration());

        epicInManagerAfterCreate.setId(-1);
        epicInManagerAfterChanges = taskManager.getEpicById(epic.getId());

        assertNotEquals(epicInManagerAfterCreate, epicInManagerAfterChanges);

        List<Epic> epics = taskManager.getAllEpics();
        epics.clear();

        assertEquals(1, taskManager.getAllEpics().size());
    }

    @Test
    void subtasksInManagerCannotBeChangedAfterGetAllSubtasks() {
        Epic epic = new Epic("Title", "Description");
        taskManager.createEpic(epic);
        Subtask subtask = new Subtask(
                "Title",
                "Description",
                TaskStatus.NEW,
                currentDate,
                Duration.ofMinutes(60),
                epic.getId()
        );
        taskManager.createSubtask(subtask);
        Subtask subtaskInManagerAfterCreate = taskManager.getAllSubtasks().getFirst();
        subtaskInManagerAfterCreate.setTitle("NewTitle");
        subtaskInManagerAfterCreate.setDescription("NewDescription");
        subtaskInManagerAfterCreate.setStatus(TaskStatus.IN_PROGRESS);
        subtaskInManagerAfterCreate.setStartTime(currentDate.plusDays(1));
        subtaskInManagerAfterCreate.setDuration(Duration.ofMinutes(120));
        subtaskInManagerAfterCreate.setId(-1);
        Subtask subtaskInManagerAfterChanges = taskManager.getSubtaskById(subtask.getId());

        assertNotEquals(subtaskInManagerAfterCreate.getId(), subtaskInManagerAfterChanges.getId());
        assertNotEquals(subtaskInManagerAfterCreate.getTitle(), subtaskInManagerAfterChanges.getTitle());
        assertNotEquals(subtaskInManagerAfterCreate.getDescription(), subtaskInManagerAfterChanges.getDescription());
        assertNotEquals(subtaskInManagerAfterCreate.getStatus(), subtaskInManagerAfterChanges.getStatus());
        assertNotEquals(subtaskInManagerAfterCreate.getStartTime(), subtaskInManagerAfterChanges.getStartTime());
        assertNotEquals(subtaskInManagerAfterCreate.getEndTime(), subtaskInManagerAfterChanges.getEndTime());
        assertNotEquals(subtaskInManagerAfterCreate.getDuration(), subtaskInManagerAfterChanges.getDuration());

        List<Subtask> subtasks = taskManager.getAllSubtasks();
        subtasks.clear();

        assertEquals(1, taskManager.getAllSubtasks().size());
    }

    @Test
    void getSubtasksByEpicId() {
        Epic epic = new Epic("Title", "Description");
        taskManager.createEpic(epic);
        Subtask subtask = new Subtask(
                "Title",
                "Description",
                TaskStatus.NEW,
                currentDate,
                Duration.ofMinutes(60),
                epic.getId()
        );
        taskManager.createSubtask(subtask);

        assertNotNull(taskManager.getSubtasksByEpicId(-1));
        assertTrue(taskManager.getSubtasksByEpicId(-1).isEmpty());

        List<Subtask> subtasks = taskManager.getSubtasksByEpicId(epic.getId());

        assertEquals(1, subtasks.size());
        assertEquals(subtask, subtasks.getFirst());
    }

    @Test
    void calculateEpicStatus() {
        Epic epic = new Epic("Title", "Description");
        taskManager.createEpic(epic);

        assertEquals(TaskStatus.NEW, taskManager.getEpicById(epic.getId()).getStatus());

        Subtask firstSubtask = new Subtask(
                "Title",
                "Description",
                TaskStatus.DONE,
                currentDate,
                Duration.ofMinutes(60),
                epic.getId()
        );
        Subtask secondSubtask = new Subtask(
                "Title",
                "Description",
                TaskStatus.DONE,
                currentDate.plusDays(1),
                Duration.ofMinutes(120),
                epic.getId()
        );
        Subtask thirdSubtask = new Subtask(
                "Title",
                "Description",
                TaskStatus.DONE,
                currentDate.plusDays(2),
                Duration.ofMinutes(180),
                epic.getId()
        );
        taskManager.createSubtask(firstSubtask);
        taskManager.createSubtask(secondSubtask);
        taskManager.createSubtask(thirdSubtask);

        assertEquals(TaskStatus.DONE, taskManager.getEpicById(epic.getId()).getStatus());

        firstSubtask.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.updateSubtask(firstSubtask);

        assertEquals(TaskStatus.IN_PROGRESS, taskManager.getEpicById(epic.getId()).getStatus());

        firstSubtask.setStatus(TaskStatus.NEW);
        taskManager.updateSubtask(firstSubtask);

        assertEquals(TaskStatus.IN_PROGRESS, taskManager.getEpicById(epic.getId()).getStatus());

        secondSubtask.setStatus(TaskStatus.NEW);
        thirdSubtask.setStatus(TaskStatus.NEW);
        taskManager.updateSubtask(secondSubtask);
        taskManager.updateSubtask(thirdSubtask);

        assertEquals(TaskStatus.NEW, taskManager.getEpicById(epic.getId()).getStatus());
    }

    @Test
    void getHistory() {
        Task task = new Task(
                "Title",
                "Description",
                TaskStatus.NEW,
                currentDate,
                Duration.ofMinutes(60)
        );
        Epic firstEpic = new Epic("Title", "Description");
        Epic secondEpic = new Epic("Title", "Description");
        taskManager.createTask(task);
        taskManager.createEpic(firstEpic);
        taskManager.createEpic(secondEpic);
        Subtask firstSubtask = new Subtask(
                "Title",
                "Description",
                TaskStatus.NEW,
                currentDate.plusDays(1),
                Duration.ofMinutes(120),
                firstEpic.getId()
        );
        Subtask secondSubtask = new Subtask(
                "Title",
                "Description",
                TaskStatus.NEW,
                currentDate.plusDays(2),
                Duration.ofMinutes(180),
                secondEpic.getId()
        );
        taskManager.createSubtask(firstSubtask);
        taskManager.createSubtask(secondSubtask);

        assertNotNull(taskManager.getHistory());
        assertTrue(taskManager.getHistory().isEmpty());

        taskManager.getTaskById(task.getId());
        taskManager.getEpicById(firstEpic.getId());
        taskManager.getSubtaskById(firstSubtask.getId());
        taskManager.getEpicById(secondEpic.getId());
        taskManager.getSubtaskById(secondSubtask.getId());

        List<Task> history = taskManager.getHistory();

        assertEquals(5, history.size());
        assertEquals(task.getId(), history.getFirst().getId());
        assertEquals(firstEpic.getId(), history.get(1).getId());
        assertEquals(firstSubtask.getId(), history.get(2).getId());
        assertEquals(secondEpic.getId(), history.get(3).getId());
        assertEquals(secondSubtask.getId(), history.get(4).getId());

        taskManager.removeTaskById(task.getId());
        history = taskManager.getHistory();

        assertEquals(4, history.size());
        assertEquals(firstEpic.getId(), history.getFirst().getId());
        assertEquals(firstSubtask.getId(), history.get(1).getId());
        assertEquals(secondEpic.getId(), history.get(2).getId());
        assertEquals(secondSubtask.getId(), history.get(3).getId());

        taskManager.removeEpicById(firstEpic.getId());
        history = taskManager.getHistory();

        assertEquals(2, history.size());
        assertEquals(secondEpic.getId(), history.getFirst().getId());
        assertEquals(secondSubtask.getId(), history.get(1).getId());

        taskManager.removeSubtaskById(secondSubtask.getId());
        history = taskManager.getHistory();

        assertEquals(1, history.size());
        assertEquals(secondEpic.getId(), history.getFirst().getId());

        taskManager.createTask(task);
        taskManager.createSubtask(secondSubtask);
        taskManager.getTaskById(task.getId());
        taskManager.getSubtaskById(secondSubtask.getId());
        taskManager.removeAllTasks();
        taskManager.removeAllSubtasks();
        history = taskManager.getHistory();

        assertEquals(1, history.size());
        assertEquals(secondEpic.getId(), history.getFirst().getId());

        taskManager.createSubtask(secondSubtask);
        taskManager.getSubtaskById(secondSubtask.getId());
        taskManager.removeAllEpics();
        history = taskManager.getHistory();

        assertTrue(history.isEmpty());
    }

    @Test
    void twoTasksNotOverlapIfFirstTaskEndsEarlierThenSecondTaskStarts() {
        Task firstTask = new Task(
                "Title",
                "Description",
                TaskStatus.NEW,
                currentDate,
                Duration.ofMinutes(60)
        );
        Task secondTask = new Task(
                "Title",
                "Description",
                TaskStatus.NEW,
                currentDate.plusDays(1),
                Duration.ofMinutes(60)
        );

        taskManager.createTask(firstTask);
        taskManager.createTask(secondTask);

        assertEquals(2, taskManager.getPrioritizedTasks().size());
    }

    @Test
    void twoTasksNotOverlapIfFirstTaskStartsLaterThenSecondTaskEnds() {
        Task firstTask = new Task(
                "Title",
                "Description",
                TaskStatus.NEW,
                currentDate,
                Duration.ofMinutes(60)
        );
        Task secondTask = new Task(
                "Title",
                "Description",
                TaskStatus.NEW,
                currentDate.minusDays(1),
                Duration.ofMinutes(60)
        );

        taskManager.createTask(firstTask);
        taskManager.createTask(secondTask);

        assertEquals(2, taskManager.getPrioritizedTasks().size());
    }

    @Test
    void twoTasksOverlapIfFirstTaskDurationContainsSecondTaskDuration() {
        Task firstTask = new Task(
                "Title",
                "Description",
                TaskStatus.NEW,
                currentDate,
                Duration.ofMinutes(120)
        );
        Task secondTask = new Task(
                "Title",
                "Description",
                TaskStatus.NEW,
                currentDate.plusMinutes(30),
                Duration.ofMinutes(60)
        );

        taskManager.createTask(firstTask);
        taskManager.createTask(secondTask);

        assertEquals(1, taskManager.getPrioritizedTasks().size());
    }

    @Test
    void twoTasksOverlapIfSecondTaskDurationContainsFirstTaskDuration() {
        Task firstTask = new Task(
                "Title",
                "Description",
                TaskStatus.NEW,
                currentDate,
                Duration.ofMinutes(60)
        );
        Task secondTask = new Task(
                "Title",
                "Description",
                TaskStatus.NEW,
                currentDate.minusMinutes(30),
                Duration.ofMinutes(120)
        );

        taskManager.createTask(firstTask);
        taskManager.createTask(secondTask);

        assertEquals(1, taskManager.getPrioritizedTasks().size());
    }

    @Test
    void twoTasksOverlapIfFirstTaskEndTimeInSecondTaskDuration() {
        Task firstTask = new Task(
                "Title",
                "Description",
                TaskStatus.NEW,
                currentDate,
                Duration.ofMinutes(60)
        );
        Task secondTask = new Task(
                "Title",
                "Description",
                TaskStatus.NEW,
                currentDate.plusMinutes(50),
                Duration.ofMinutes(60)
        );

        taskManager.createTask(firstTask);
        taskManager.createTask(secondTask);

        assertEquals(1, taskManager.getPrioritizedTasks().size());
    }

    @Test
    void twoTasksOverlapIfFirstTaskStartTimeInSecondTaskDuration() {
        Task firstTask = new Task(
                "Title",
                "Description",
                TaskStatus.NEW,
                currentDate,
                Duration.ofMinutes(60)
        );
        Task secondTask = new Task(
                "Title",
                "Description",
                TaskStatus.NEW,
                currentDate.minusMinutes(50),
                Duration.ofMinutes(60)
        );

        taskManager.createTask(firstTask);
        taskManager.createTask(secondTask);

        assertEquals(1, taskManager.getPrioritizedTasks().size());
    }

    @Test
    void twoTasksOverlapIfFirstTaskStartTimeEqualsSecondTaskEndTime() {
        Task firstTask = new Task(
                "Title",
                "Description",
                TaskStatus.NEW,
                currentDate,
                Duration.ofMinutes(60)
        );
        Task secondTask = new Task(
                "Title",
                "Description",
                TaskStatus.NEW,
                currentDate.minusMinutes(30),
                Duration.ofMinutes(30)
        );

        taskManager.createTask(firstTask);
        taskManager.createTask(secondTask);

        assertEquals(1, taskManager.getPrioritizedTasks().size());
    }

    @Test
    void twoTasksOverlapIfFirstTaskEndTimeEqualsSecondTaskStartTime() {
        Task firstTask = new Task(
                "Title",
                "Description",
                TaskStatus.NEW,
                currentDate,
                Duration.ofMinutes(60)
        );
        Task secondTask = new Task(
                "Title",
                "Description",
                TaskStatus.NEW,
                currentDate.plusMinutes(60),
                Duration.ofMinutes(60)
        );

        taskManager.createTask(firstTask);
        taskManager.createTask(secondTask);

        assertEquals(1, taskManager.getPrioritizedTasks().size());
    }

    @Test
    void sortTasksByStartTime() {
        Task firstTask = new Task(
                "Title",
                "Description",
                TaskStatus.NEW,
                currentDate,
                Duration.ofMinutes(60)
        );
        Task secondTask = new Task(
                "Title",
                "Description",
                TaskStatus.NEW,
                currentDate.minusDays(1),
                Duration.ofMinutes(60)
        );
        Task thirdTask = new Task(
                "Title",
                "Description",
                TaskStatus.NEW,
                currentDate.plusDays(1),
                Duration.ofMinutes(60)
        );
        taskManager.createTask(firstTask);
        taskManager.createTask(secondTask);
        taskManager.createTask(thirdTask);

        List<Task> prioritizedTasks = taskManager.getPrioritizedTasks();

        assertEquals(secondTask, prioritizedTasks.getFirst());
        assertEquals(firstTask, prioritizedTasks.get(1));
        assertEquals(thirdTask, prioritizedTasks.getLast());
    }
}