package service;

import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {
    private InMemoryTaskManager inMemoryTaskManager;

    @BeforeEach
    void createNewInMemoryTaskManager() {
        inMemoryTaskManager = new InMemoryTaskManager();
    }

    @Test
    void createTask() {
        Task task = new Task("Title", "Description", TaskStatus.NEW);
        inMemoryTaskManager.createTask(task);
        Task taskInManager = inMemoryTaskManager.getTaskById(task.getId());

        assertEquals("Title", taskInManager.getTitle());
        assertEquals("Description", taskInManager.getDescription());
        assertEquals(TaskStatus.NEW, taskInManager.getStatus());
        assertTrue(taskInManager.getId() > 0);
        assertEquals(task, taskInManager);

        List<Task> tasksInManager = inMemoryTaskManager.getAllTasks();

        assertNotNull(tasksInManager);
        assertEquals(1, tasksInManager.size());
        assertEquals(taskInManager, tasksInManager.getFirst());

        inMemoryTaskManager.createTask(task);

        assertEquals(2, inMemoryTaskManager.getAllTasks().size());
        assertNotEquals(task, taskInManager);
    }

    @Test
    void taskInManagerCannotBeChangedWithoutUpdateMethod() {
        Task task = new Task("Title", "Description", TaskStatus.NEW);
        inMemoryTaskManager.createTask(task);
        task.setId(-1);
        task.setTitle("NewTitle");
        task.setDescription("NewDescription");
        task.setStatus(TaskStatus.IN_PROGRESS);
        Task taskInManager = inMemoryTaskManager.getAllTasks().getFirst();

        assertNotEquals(task.getId(), taskInManager.getId());
        assertNotEquals(task.getStatus(), taskInManager.getStatus());
        assertNotEquals(task.getDescription(), taskInManager.getDescription());
        assertNotEquals(task.getTitle(), taskInManager.getTitle());
    }

    @Test
    void createEpic() {
        Epic epic = new Epic("Title", "Description");
        inMemoryTaskManager.createEpic(epic);
        Epic epicInManager = inMemoryTaskManager.getEpicById(epic.getId());

        assertEquals("Title", epicInManager.getTitle());
        assertEquals("Description", epicInManager.getDescription());
        assertEquals(TaskStatus.NEW, epicInManager.getStatus());
        assertTrue(epicInManager.getId() > 0);
        assertEquals(epic, epicInManager);

        List<Epic> epicsInManager = inMemoryTaskManager.getAllEpics();

        assertNotNull(epicsInManager);
        assertEquals(1, epicsInManager.size());
        assertEquals(epicInManager, epicsInManager.getFirst());

        inMemoryTaskManager.createEpic(epic);

        assertEquals(2, inMemoryTaskManager.getAllEpics().size());
        assertNotEquals(epic, epicInManager);
    }

    @Test
    void epicInManagerCannotBeChangedWithoutUpdateMethod() {
        Epic epic = new Epic("Title", "Description");
        inMemoryTaskManager.createEpic(epic);
        epic.setId(-1);
        epic.setTitle("NewTitle");
        epic.setDescription("NewDescription");
        epic.setStatus(TaskStatus.IN_PROGRESS);
        Epic epicInManager = inMemoryTaskManager.getAllEpics().getFirst();

        assertNotEquals(epic.getId(), epicInManager.getId());
        assertNotEquals(epic.getStatus(), epicInManager.getStatus());
        assertNotEquals(epic.getDescription(), epicInManager.getDescription());
        assertNotEquals(epic.getTitle(), epicInManager.getTitle());
    }

    @Test
    void createSubtask() {
        Epic epic = new Epic("T", "D");
        inMemoryTaskManager.createEpic(epic);
        Subtask firstSubtask = new Subtask("T", "D", TaskStatus.NEW, epic.getId());
        inMemoryTaskManager.createSubtask(firstSubtask);
        Subtask subtaskInManager = inMemoryTaskManager.getSubtaskById(firstSubtask.getId());
        Epic epicInManager = inMemoryTaskManager.getEpicById(epic.getId());
        Subtask secondSubtask = new Subtask("T", "D", TaskStatus.NEW, subtaskInManager.getId());
        Subtask notCreatedSubtask = inMemoryTaskManager.createSubtask(secondSubtask);

        assertEquals("T", subtaskInManager.getTitle());
        assertEquals("D", subtaskInManager.getDescription());
        assertEquals(TaskStatus.NEW, subtaskInManager.getStatus());
        assertTrue(subtaskInManager.getId() > 0);
        assertEquals(firstSubtask, subtaskInManager);
        assertEquals(1, epicInManager.getSubtasksIds().size());
        assertEquals(subtaskInManager.getId(), epicInManager.getSubtasksIds().getFirst());
        assertNull(notCreatedSubtask);

        List<Subtask> subtasksInManager = inMemoryTaskManager.getAllSubtasks();

        assertNotNull(subtasksInManager);
        assertEquals(1, subtasksInManager.size());
        assertEquals(subtaskInManager, subtasksInManager.getFirst());

        inMemoryTaskManager.createSubtask(firstSubtask);

        assertEquals(2, inMemoryTaskManager.getAllSubtasks().size());
        assertNotEquals(firstSubtask, subtaskInManager);
    }

    @Test
    void subtaskInManagerCannotBeChangedWithoutUpdateMethod() {
        Epic epic = new Epic("Title", "Description");
        inMemoryTaskManager.createEpic(epic);
        Subtask subtask = new Subtask("Title", "Description", TaskStatus.NEW, epic.getId());
        inMemoryTaskManager.createSubtask(subtask);
        subtask.setId(-1);
        subtask.setTitle("NewTitle");
        subtask.setDescription("NewDescription");
        subtask.setStatus(TaskStatus.IN_PROGRESS);
        Subtask subtaskInManager = inMemoryTaskManager.getAllSubtasks().getFirst();

        assertNotEquals(subtask.getId(), subtaskInManager.getId());
        assertNotEquals(subtask.getStatus(), subtaskInManager.getStatus());
        assertNotEquals(subtask.getDescription(), subtaskInManager.getDescription());
        assertNotEquals(subtask.getTitle(), subtaskInManager.getTitle());
    }

    @Test
    void updateTask() {
        Task firstTask = new Task("Title", "Description", TaskStatus.NEW);
        Task secondTask = new Task("NewTitle", "NewDescription", TaskStatus.IN_PROGRESS);
        Task thirdTask = new Task("Title", "Description", TaskStatus.NEW);

        inMemoryTaskManager.createTask(firstTask);
        secondTask.setId(firstTask.getId());
        inMemoryTaskManager.updateTask(secondTask);
        Task updatedTaskInManager = inMemoryTaskManager.getTaskById(secondTask.getId());

        assertEquals("NewTitle", updatedTaskInManager.getTitle());
        assertEquals("NewDescription", updatedTaskInManager.getDescription());
        assertEquals(TaskStatus.IN_PROGRESS, updatedTaskInManager.getStatus());
        assertEquals(firstTask, updatedTaskInManager);

        thirdTask.setId(-1);
        Task notUpdatedTask = inMemoryTaskManager.updateTask(thirdTask);

        assertNull(notUpdatedTask);
    }

    @Test
    void updateEpic() {
        Epic firstEpic = new Epic("Title", "Description");
        Epic secondEpic = new Epic("NewTitle", "NewDescription");
        Epic thirdEpic = new Epic("Title", "Description");

        inMemoryTaskManager.createEpic(firstEpic);
        secondEpic.setId(firstEpic.getId());
        inMemoryTaskManager.updateEpic(secondEpic);
        Epic updatedEpicInManager = inMemoryTaskManager.getEpicById(secondEpic.getId());

        assertEquals("NewTitle", updatedEpicInManager.getTitle());
        assertEquals("NewDescription", updatedEpicInManager.getDescription());
        assertEquals(TaskStatus.NEW, updatedEpicInManager.getStatus());
        assertEquals(firstEpic, updatedEpicInManager);

        thirdEpic.setId(-1);
        Epic notUpdatedEpic = inMemoryTaskManager.updateEpic(thirdEpic);

        assertNull(notUpdatedEpic);
    }

    @Test
    void updateSubtask() {
        Epic epic = new Epic("Title", "Description");
        inMemoryTaskManager.createEpic(epic);

        Subtask firstSubtask = new Subtask("T", "D", TaskStatus.NEW, epic.getId());
        Subtask secondSubtask = new Subtask("NT", "ND", TaskStatus.IN_PROGRESS, epic.getId());
        Subtask thirdSubtask = new Subtask("T", "D", TaskStatus.NEW, epic.getId());

        inMemoryTaskManager.createSubtask(firstSubtask);
        secondSubtask.setId(firstSubtask.getId());
        inMemoryTaskManager.updateSubtask(secondSubtask);
        Subtask updatedSubtaskInManager = inMemoryTaskManager.getSubtaskById(secondSubtask.getId());

        assertEquals("NT", updatedSubtaskInManager.getTitle());
        assertEquals("ND", updatedSubtaskInManager.getDescription());
        assertEquals(TaskStatus.IN_PROGRESS, updatedSubtaskInManager.getStatus());
        assertEquals(firstSubtask.getEpicId(), updatedSubtaskInManager.getEpicId());
        assertEquals(firstSubtask, updatedSubtaskInManager);

        thirdSubtask.setId(-1);
        Subtask notUpdatedSubtask = inMemoryTaskManager.updateSubtask(thirdSubtask);

        assertNull(notUpdatedSubtask);
    }

    @Test
    void getTaskById() {
        Task task = new Task("Title", "Description", TaskStatus.NEW);
        inMemoryTaskManager.createTask(task);

        assertEquals(task, inMemoryTaskManager.getTaskById(task.getId()));
        assertNull(inMemoryTaskManager.getTaskById(-1));
    }

    @Test
    void taskInManagerCannotBeChangedAfterGetById() {
        Task task = new Task("Title", "Description", TaskStatus.NEW);
        inMemoryTaskManager.createTask(task);
        Task taskInManagerAfterCreate = inMemoryTaskManager.getTaskById(task.getId());
        taskInManagerAfterCreate.setTitle("NewTitle");
        taskInManagerAfterCreate.setDescription("NewDescription");
        taskInManagerAfterCreate.setStatus(TaskStatus.IN_PROGRESS);
        taskInManagerAfterCreate.setId(-1);
        Task taskInManagerAfterChanges = inMemoryTaskManager.getTaskById(task.getId());

        assertNotEquals(taskInManagerAfterCreate.getId(), taskInManagerAfterChanges.getId());
        assertNotEquals(taskInManagerAfterCreate.getTitle(), taskInManagerAfterChanges.getTitle());
        assertNotEquals(taskInManagerAfterCreate.getDescription(), taskInManagerAfterChanges.getDescription());
        assertNotEquals(taskInManagerAfterCreate.getStatus(), taskInManagerAfterChanges.getStatus());
    }

    @Test
    void getEpicById() {
        Epic epic = new Epic("Title", "Description");
        inMemoryTaskManager.createEpic(epic);

        assertEquals(epic, inMemoryTaskManager.getEpicById(epic.getId()));
        assertNull(inMemoryTaskManager.getEpicById(-1));
    }

    @Test
    void epicInManagerCannotBeChangedAfterGetById() {
        Epic epic = new Epic("Title", "Description");
        inMemoryTaskManager.createEpic(epic);
        Epic epicInManagerAfterCreate = inMemoryTaskManager.getEpicById(epic.getId());
        epicInManagerAfterCreate.setTitle("NewTitle");
        epicInManagerAfterCreate.setDescription("NewDescription");
        epicInManagerAfterCreate.setStatus(TaskStatus.IN_PROGRESS);
        epicInManagerAfterCreate.setId(-1);
        Epic epicInManagerAfterChanges = inMemoryTaskManager.getEpicById(epic.getId());

        assertNotEquals(epicInManagerAfterCreate.getId(), epicInManagerAfterChanges.getId());
        assertNotEquals(epicInManagerAfterCreate.getTitle(), epicInManagerAfterChanges.getTitle());
        assertNotEquals(epicInManagerAfterCreate.getDescription(), epicInManagerAfterChanges.getDescription());
        assertNotEquals(epicInManagerAfterCreate.getStatus(), epicInManagerAfterChanges.getStatus());
    }

    @Test
    void getSubtaskById() {
        Epic epic = new Epic("Title", "Description");
        inMemoryTaskManager.createEpic(epic);
        Subtask subtask = new Subtask("Title", "Description", TaskStatus.NEW, epic.getId());
        inMemoryTaskManager.createSubtask(subtask);

        assertEquals(subtask, inMemoryTaskManager.getSubtaskById(subtask.getId()));
        assertNull(inMemoryTaskManager.getSubtaskById(-1));
    }

    @Test
    void subtaskInManagerCannotBeChangedAfterGetById() {
        Epic epic = new Epic("Title", "Description");
        inMemoryTaskManager.createEpic(epic);
        Subtask subtask = new Subtask("Title", "Description", TaskStatus.NEW, epic.getId());
        inMemoryTaskManager.createSubtask(subtask);
        Subtask subtaskInManagerAfterCreate = inMemoryTaskManager.getSubtaskById(subtask.getId());
        subtaskInManagerAfterCreate.setTitle("NewTitle");
        subtaskInManagerAfterCreate.setDescription("NewDescription");
        subtaskInManagerAfterCreate.setStatus(TaskStatus.IN_PROGRESS);
        subtaskInManagerAfterCreate.setId(-1);
        Subtask subtaskInManagerAfterChanges = inMemoryTaskManager.getSubtaskById(subtask.getId());

        assertNotEquals(subtaskInManagerAfterCreate.getId(), subtaskInManagerAfterChanges.getId());
        assertNotEquals(subtaskInManagerAfterCreate.getTitle(), subtaskInManagerAfterChanges.getTitle());
        assertNotEquals(subtaskInManagerAfterCreate.getDescription(), subtaskInManagerAfterChanges.getDescription());
        assertNotEquals(subtaskInManagerAfterCreate.getStatus(), subtaskInManagerAfterChanges.getStatus());
    }

    @Test
    void tasksInManagerCannotBeChangedAfterGetAllTasks() {
        Task task = new Task("Title", "Description", TaskStatus.NEW);
        inMemoryTaskManager.createTask(task);
        Task taskInManagerAfterCreate = inMemoryTaskManager.getAllTasks().getFirst();
        taskInManagerAfterCreate.setTitle("NewTitle");
        taskInManagerAfterCreate.setDescription("NewDescription");
        taskInManagerAfterCreate.setStatus(TaskStatus.IN_PROGRESS);
        taskInManagerAfterCreate.setId(-1);
        Task taskInManagerAfterChanges = inMemoryTaskManager.getTaskById(task.getId());

        assertNotEquals(taskInManagerAfterCreate.getId(), taskInManagerAfterChanges.getId());
        assertNotEquals(taskInManagerAfterCreate.getTitle(), taskInManagerAfterChanges.getTitle());
        assertNotEquals(taskInManagerAfterCreate.getDescription(), taskInManagerAfterChanges.getDescription());
        assertNotEquals(taskInManagerAfterCreate.getStatus(), taskInManagerAfterChanges.getStatus());

        List<Task> tasks = inMemoryTaskManager.getAllTasks();
        tasks.clear();

        assertEquals(1, inMemoryTaskManager.getAllTasks().size());
    }

    @Test
    void epicsInManagerCannotBeChangedAfterGetAllEpics() {
        Epic epic = new Epic("Title", "Description");
        inMemoryTaskManager.createEpic(epic);
        Epic epicInManagerAfterCreate = inMemoryTaskManager.getAllEpics().getFirst();
        epicInManagerAfterCreate.setTitle("NewTitle");
        epicInManagerAfterCreate.setDescription("NewDescription");
        epicInManagerAfterCreate.setStatus(TaskStatus.IN_PROGRESS);
        epicInManagerAfterCreate.setId(-1);
        Epic epicInManagerAfterChanges = inMemoryTaskManager.getEpicById(epic.getId());

        assertNotEquals(epicInManagerAfterCreate.getId(), epicInManagerAfterChanges.getId());
        assertNotEquals(epicInManagerAfterCreate.getTitle(), epicInManagerAfterChanges.getTitle());
        assertNotEquals(epicInManagerAfterCreate.getDescription(), epicInManagerAfterChanges.getDescription());
        assertNotEquals(epicInManagerAfterCreate.getStatus(), epicInManagerAfterChanges.getStatus());

        List<Epic> epics = inMemoryTaskManager.getAllEpics();
        epics.clear();

        assertEquals(1, inMemoryTaskManager.getAllEpics().size());
    }

    @Test
    void subtasksInManagerCannotBeChangedAfterGetAllSubtasks() {
        Epic epic = new Epic("Title", "Description");
        inMemoryTaskManager.createEpic(epic);
        Subtask subtask = new Subtask("Title", "Description", TaskStatus.NEW, epic.getId());
        inMemoryTaskManager.createSubtask(subtask);
        Subtask subtaskInManagerAfterCreate = inMemoryTaskManager.getAllSubtasks().getFirst();
        subtaskInManagerAfterCreate.setTitle("NewTitle");
        subtaskInManagerAfterCreate.setDescription("NewDescription");
        subtaskInManagerAfterCreate.setStatus(TaskStatus.IN_PROGRESS);
        subtaskInManagerAfterCreate.setId(-1);
        Subtask subtaskInManagerAfterChanges = inMemoryTaskManager.getSubtaskById(subtask.getId());

        assertNotEquals(subtaskInManagerAfterCreate.getId(), subtaskInManagerAfterChanges.getId());
        assertNotEquals(subtaskInManagerAfterCreate.getTitle(), subtaskInManagerAfterChanges.getTitle());
        assertNotEquals(subtaskInManagerAfterCreate.getDescription(), subtaskInManagerAfterChanges.getDescription());
        assertNotEquals(subtaskInManagerAfterCreate.getStatus(), subtaskInManagerAfterChanges.getStatus());

        List<Subtask> subtasks = inMemoryTaskManager.getAllSubtasks();
        subtasks.clear();

        assertEquals(1, inMemoryTaskManager.getAllSubtasks().size());
    }

    @Test
    void removeTaskById() {
        Task task = new Task("Title", "Description", TaskStatus.NEW);
        inMemoryTaskManager.createTask(task);
        Task deletedTask = inMemoryTaskManager.removeTaskById(task.getId());

        assertEquals(task, deletedTask);
        assertNull(inMemoryTaskManager.getTaskById(deletedTask.getId()));
        assertNull(inMemoryTaskManager.removeTaskById(-1));
    }

    @Test
    void removeEpicById() {
        Epic epic = new Epic("Title", "Description");
        inMemoryTaskManager.createEpic(epic);
        Subtask subtask = new Subtask("Title", "Description", TaskStatus.NEW, epic.getId());
        inMemoryTaskManager.createSubtask(subtask);
        Epic deletedEpic = inMemoryTaskManager.removeEpicById(epic.getId());

        assertEquals(epic, deletedEpic);
        assertNull(inMemoryTaskManager.getEpicById(deletedEpic.getId()));
        assertNull(inMemoryTaskManager.getSubtaskById(subtask.getId()));
        assertNull(inMemoryTaskManager.removeEpicById(-1));
    }

    @Test
    void removeSubtaskById() {
        Epic epic = new Epic("Title", "Description");
        inMemoryTaskManager.createEpic(epic);
        Subtask subtask = new Subtask("Title", "Description", TaskStatus.NEW, epic.getId());
        inMemoryTaskManager.createSubtask(subtask);
        Subtask deletedSubtask = inMemoryTaskManager.removeSubtaskById(subtask.getId());

        assertEquals(subtask, deletedSubtask);
        assertNull(inMemoryTaskManager.getSubtaskById(subtask.getId()));
        assertTrue(inMemoryTaskManager.getEpicById(epic.getId()).getSubtasksIds().isEmpty());
        assertNull(inMemoryTaskManager.removeSubtaskById(-1));
    }

    @Test
    void removeAllTasks() {
        Task firstTask = new Task("Title", "Description", TaskStatus.NEW);
        Task secondTask = new Task("Title", "Description", TaskStatus.NEW);
        inMemoryTaskManager.removeAllTasks();

        assertNotNull(inMemoryTaskManager.getAllTasks());
        assertTrue(inMemoryTaskManager.getAllTasks().isEmpty());

        inMemoryTaskManager.createTask(firstTask);
        inMemoryTaskManager.createTask(secondTask);

        assertEquals(2, inMemoryTaskManager.getAllTasks().size());

        inMemoryTaskManager.removeAllTasks();

        assertTrue(inMemoryTaskManager.getAllTasks().isEmpty());
    }

    @Test
    void removeAllEpics() {
        Epic firstEpic = new Epic("Title", "Description");
        Epic secondEpic = new Epic("Title", "Description");
        inMemoryTaskManager.removeAllEpics();

        assertNotNull(inMemoryTaskManager.getAllEpics());
        assertTrue(inMemoryTaskManager.getAllEpics().isEmpty());

        inMemoryTaskManager.createEpic(firstEpic);
        inMemoryTaskManager.createEpic(secondEpic);
        Subtask firstSubtask = new Subtask("Title", "Description", TaskStatus.NEW, firstEpic.getId());
        Subtask secondSubtask = new Subtask("Title", "Description", TaskStatus.NEW, secondEpic.getId());
        inMemoryTaskManager.createSubtask(firstSubtask);
        inMemoryTaskManager.createSubtask(secondSubtask);

        assertEquals(2, inMemoryTaskManager.getAllEpics().size());
        assertEquals(2, inMemoryTaskManager.getAllSubtasks().size());

        inMemoryTaskManager.removeAllEpics();

        assertTrue(inMemoryTaskManager.getAllEpics().isEmpty());
        assertTrue(inMemoryTaskManager.getAllSubtasks().isEmpty());
    }

    @Test
    void removeAllSubtasks() {
        Epic firstEpic = new Epic("Title", "Description");
        Epic secondEpic = new Epic("Title", "Description");
        inMemoryTaskManager.createEpic(firstEpic);
        inMemoryTaskManager.createEpic(secondEpic);
        Subtask firstSubtask = new Subtask("Title", "Description", TaskStatus.NEW, firstEpic.getId());
        Subtask secondSubtask = new Subtask("Title", "Description", TaskStatus.NEW, secondEpic.getId());

        assertNotNull(inMemoryTaskManager.getAllSubtasks());
        assertTrue(inMemoryTaskManager.getAllSubtasks().isEmpty());

        inMemoryTaskManager.createSubtask(firstSubtask);
        inMemoryTaskManager.createSubtask(secondSubtask);

        assertEquals(2, inMemoryTaskManager.getAllSubtasks().size());
        assertEquals(2, inMemoryTaskManager.getAllEpics().size());

        inMemoryTaskManager.removeAllSubtasks();

        assertTrue(inMemoryTaskManager.getAllSubtasks().isEmpty());
        assertTrue(inMemoryTaskManager.getEpicById(firstEpic.getId()).getSubtasksIds().isEmpty());
        assertTrue(inMemoryTaskManager.getEpicById(secondEpic.getId()).getSubtasksIds().isEmpty());
    }

    @Test
    void getSubtasksByEpicId() {
        Epic epic = new Epic("Title", "Description");
        inMemoryTaskManager.createEpic(epic);
        Subtask subtask = new Subtask("Title", "Description", TaskStatus.NEW, epic.getId());
        inMemoryTaskManager.createSubtask(subtask);

        assertNotNull(inMemoryTaskManager.getSubtasksByEpicId(-1));
        assertTrue(inMemoryTaskManager.getSubtasksByEpicId(-1).isEmpty());

        List<Subtask> subtasks = inMemoryTaskManager.getSubtasksByEpicId(epic.getId());

        assertEquals(1, subtasks.size());
        assertEquals(subtask, subtasks.getFirst());
    }

    @Test
    void getHistory() {
        Task task = new Task("Title", "Description", TaskStatus.NEW);
        Epic firstEpic = new Epic("Title", "Description");
        Epic secondEpic = new Epic("Title", "Description");
        inMemoryTaskManager.createTask(task);
        inMemoryTaskManager.createEpic(firstEpic);
        inMemoryTaskManager.createEpic(secondEpic);
        Subtask firstSubtask = new Subtask("Title", "Description", TaskStatus.NEW, firstEpic.getId());
        Subtask secondSubtask = new Subtask("Title", "Description", TaskStatus.NEW, secondEpic.getId());
        inMemoryTaskManager.createSubtask(firstSubtask);
        inMemoryTaskManager.createSubtask(secondSubtask);

        assertNotNull(inMemoryTaskManager.getHistory());
        assertTrue(inMemoryTaskManager.getHistory().isEmpty());

        inMemoryTaskManager.getTaskById(task.getId());
        inMemoryTaskManager.getEpicById(firstEpic.getId());
        inMemoryTaskManager.getSubtaskById(firstSubtask.getId());
        inMemoryTaskManager.getEpicById(secondEpic.getId());
        inMemoryTaskManager.getSubtaskById(secondSubtask.getId());

        List<Task> history = inMemoryTaskManager.getHistory();

        assertEquals(5, history.size());
        assertEquals(task.getId(), history.getFirst().getId());
        assertEquals(firstEpic.getId(), history.get(1).getId());
        assertEquals(firstSubtask.getId(), history.get(2).getId());
        assertEquals(secondEpic.getId(), history.get(3).getId());
        assertEquals(secondSubtask.getId(), history.get(4).getId());

        inMemoryTaskManager.removeTaskById(task.getId());
        history = inMemoryTaskManager.getHistory();

        assertEquals(4, history.size());
        assertEquals(firstEpic.getId(), history.getFirst().getId());
        assertEquals(firstSubtask.getId(), history.get(1).getId());
        assertEquals(secondEpic.getId(), history.get(2).getId());
        assertEquals(secondSubtask.getId(), history.get(3).getId());

        inMemoryTaskManager.removeEpicById(firstEpic.getId());
        history = inMemoryTaskManager.getHistory();

        assertEquals(2, history.size());
        assertEquals(secondEpic.getId(), history.getFirst().getId());
        assertEquals(secondSubtask.getId(), history.get(1).getId());

        inMemoryTaskManager.removeSubtaskById(secondSubtask.getId());
        history = inMemoryTaskManager.getHistory();

        assertEquals(1, history.size());
        assertEquals(secondEpic.getId(), history.getFirst().getId());

        inMemoryTaskManager.createTask(task);
        inMemoryTaskManager.createSubtask(secondSubtask);
        inMemoryTaskManager.getTaskById(task.getId());
        inMemoryTaskManager.getSubtaskById(secondSubtask.getId());
        inMemoryTaskManager.removeAllTasks();
        inMemoryTaskManager.removeAllSubtasks();
        history = inMemoryTaskManager.getHistory();

        assertEquals(1, history.size());
        assertEquals(secondEpic.getId(), history.getFirst().getId());

        inMemoryTaskManager.createSubtask(secondSubtask);
        inMemoryTaskManager.getSubtaskById(secondSubtask.getId());
        inMemoryTaskManager.removeAllEpics();
        history = inMemoryTaskManager.getHistory();

        assertTrue(history.isEmpty());
    }

    @Test
    void calculateEpicStatus() {
        Epic epic = new Epic("Title", "Description");
        inMemoryTaskManager.createEpic(epic);

        assertEquals(TaskStatus.NEW, inMemoryTaskManager.getEpicById(epic.getId()).getStatus());

        Subtask firstSubtask = new Subtask("Title", "Description", TaskStatus.DONE, epic.getId());
        Subtask secondSubtask = new Subtask("Title", "Description", TaskStatus.DONE, epic.getId());
        Subtask thirdSubtask = new Subtask("Title", "Description", TaskStatus.DONE, epic.getId());
        inMemoryTaskManager.createSubtask(firstSubtask);
        inMemoryTaskManager.createSubtask(secondSubtask);
        inMemoryTaskManager.createSubtask(thirdSubtask);

        assertEquals(TaskStatus.DONE, inMemoryTaskManager.getEpicById(epic.getId()).getStatus());

        firstSubtask.setStatus(TaskStatus.IN_PROGRESS);
        inMemoryTaskManager.updateSubtask(firstSubtask);

        assertEquals(TaskStatus.IN_PROGRESS, inMemoryTaskManager.getEpicById(epic.getId()).getStatus());

        firstSubtask.setStatus(TaskStatus.NEW);
        inMemoryTaskManager.updateSubtask(firstSubtask);

        assertEquals(TaskStatus.IN_PROGRESS, inMemoryTaskManager.getEpicById(epic.getId()).getStatus());

        secondSubtask.setStatus(TaskStatus.NEW);
        thirdSubtask.setStatus(TaskStatus.NEW);
        inMemoryTaskManager.updateSubtask(secondSubtask);
        inMemoryTaskManager.updateSubtask(thirdSubtask);

        assertEquals(TaskStatus.NEW, inMemoryTaskManager.getEpicById(epic.getId()).getStatus());
    }
}