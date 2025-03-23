package service;

import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTaskManagerTest {
    private FileBackedTaskManager fileBackedTaskManager;
    private Path tempFilePath;

    @BeforeEach
    void createNewFileBackedTaskManager() {
        try {
            tempFilePath = Files.createTempFile("test", ".csv");
            fileBackedTaskManager = new FileBackedTaskManager(tempFilePath);
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    @Test
    void createNewFileBackedTaskManagerWithPreFilledValues() {
        try {
            List<Task> tasks = new ArrayList<>();
            List<Epic> epics = new ArrayList<>();
            List<Subtask> subtasks = new ArrayList<>();
            Task task = Task.fromString("1,TASK,Title,NEW,Description");
            Epic epic = Epic.fromString("2,EPIC,Title,NEW,Description");
            Subtask subtask = Subtask.fromString("3,SUBTASK,Title,NEW,Description,2");
            tasks.add(task);
            epics.add(epic);
            subtasks.add(subtask);
            Path newTempFilePath = Files.createTempFile("test", ".csv");
            InMemoryTaskManager newFileBackedTaskManager = new FileBackedTaskManager(
                    tasks,
                    epics,
                    subtasks,
                    newTempFilePath
            );

            assertEquals(1, newFileBackedTaskManager.getAllTasks().size());
            assertEquals(1, newFileBackedTaskManager.getAllEpics().size());
            assertEquals(1, newFileBackedTaskManager.getAllSubtasks().size());
            assertEquals(task, newFileBackedTaskManager.getAllTasks().getFirst());
            assertEquals(epic, newFileBackedTaskManager.getAllEpics().getFirst());
            assertEquals(subtask, newFileBackedTaskManager.getAllSubtasks().getFirst());
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    @Test
    void createTask() {
        Task task = new Task("Title", "Description", TaskStatus.NEW);
        fileBackedTaskManager.createTask(task);
        Task taskInManager = fileBackedTaskManager.getTaskById(task.getId());

        assertEquals("Title", taskInManager.getTitle());
        assertEquals("Description", taskInManager.getDescription());
        assertEquals(TaskStatus.NEW, taskInManager.getStatus());
        assertTrue(taskInManager.getId() > 0);
        assertEquals(task, taskInManager);

        List<Task> tasksInManager = fileBackedTaskManager.getAllTasks();

        assertNotNull(tasksInManager);
        assertEquals(1, tasksInManager.size());
        assertEquals(taskInManager, tasksInManager.getFirst());

        FileBackedTaskManager newFileBackedTaskManager = FileBackedTaskManager.loadFromFile(tempFilePath);
        Task newTask = newFileBackedTaskManager.getTaskById(task.getId());

        assertEquals(task, newTask);

        fileBackedTaskManager.createTask(task);

        assertEquals(2, fileBackedTaskManager.getAllTasks().size());
        assertNotEquals(task, taskInManager);
    }

    @Test
    void taskInManagerCannotBeChangedWithoutUpdateMethod() {
        Task task = new Task("Title", "Description", TaskStatus.NEW);
        fileBackedTaskManager.createTask(task);
        task.setId(-1);
        task.setTitle("NewTitle");
        task.setDescription("NewDescription");
        task.setStatus(TaskStatus.IN_PROGRESS);
        Task taskInManager = fileBackedTaskManager.getAllTasks().getFirst();

        assertNotEquals(task.getId(), taskInManager.getId());
        assertNotEquals(task.getStatus(), taskInManager.getStatus());
        assertNotEquals(task.getDescription(), taskInManager.getDescription());
        assertNotEquals(task.getTitle(), taskInManager.getTitle());
    }

    @Test
    void createEpic() {
        Epic epic = new Epic("Title", "Description");
        fileBackedTaskManager.createEpic(epic);
        Epic epicInManager = fileBackedTaskManager.getEpicById(epic.getId());

        assertEquals("Title", epicInManager.getTitle());
        assertEquals("Description", epicInManager.getDescription());
        assertEquals(TaskStatus.NEW, epicInManager.getStatus());
        assertTrue(epicInManager.getId() > 0);
        assertEquals(epic, epicInManager);

        List<Epic> epicsInManager = fileBackedTaskManager.getAllEpics();

        assertNotNull(epicsInManager);
        assertEquals(1, epicsInManager.size());
        assertEquals(epicInManager, epicsInManager.getFirst());

        FileBackedTaskManager newFileBackedTaskManager = FileBackedTaskManager.loadFromFile(tempFilePath);
        Task newEpic = newFileBackedTaskManager.getEpicById(epic.getId());

        assertEquals(epic, newEpic);

        fileBackedTaskManager.createEpic(epic);

        assertEquals(2, fileBackedTaskManager.getAllEpics().size());
        assertNotEquals(epic, epicInManager);
    }

    @Test
    void epicInManagerCannotBeChangedWithoutUpdateMethod() {
        Epic epic = new Epic("Title", "Description");
        fileBackedTaskManager.createEpic(epic);
        epic.setId(-1);
        epic.setTitle("NewTitle");
        epic.setDescription("NewDescription");
        epic.setStatus(TaskStatus.IN_PROGRESS);
        Epic epicInManager = fileBackedTaskManager.getAllEpics().getFirst();

        assertNotEquals(epic.getId(), epicInManager.getId());
        assertNotEquals(epic.getStatus(), epicInManager.getStatus());
        assertNotEquals(epic.getDescription(), epicInManager.getDescription());
        assertNotEquals(epic.getTitle(), epicInManager.getTitle());
    }

    @Test
    void createSubtask() {
        Epic epic = new Epic("T", "D");
        fileBackedTaskManager.createEpic(epic);
        Subtask firstSubtask = new Subtask("T", "D", TaskStatus.NEW, epic.getId());
        fileBackedTaskManager.createSubtask(firstSubtask);
        Subtask subtaskInManager = fileBackedTaskManager.getSubtaskById(firstSubtask.getId());
        Epic epicInManager = fileBackedTaskManager.getEpicById(epic.getId());
        Subtask secondSubtask = new Subtask("T", "D", TaskStatus.NEW, subtaskInManager.getId());
        Subtask notCreatedSubtask = fileBackedTaskManager.createSubtask(secondSubtask);

        assertEquals("T", subtaskInManager.getTitle());
        assertEquals("D", subtaskInManager.getDescription());
        assertEquals(TaskStatus.NEW, subtaskInManager.getStatus());
        assertTrue(subtaskInManager.getId() > 0);
        assertEquals(firstSubtask, subtaskInManager);
        assertEquals(1, epicInManager.getSubtasksIds().size());
        assertEquals(subtaskInManager.getId(), epicInManager.getSubtasksIds().getFirst());
        assertNull(notCreatedSubtask);

        List<Subtask> subtasksInManager = fileBackedTaskManager.getAllSubtasks();

        assertNotNull(subtasksInManager);
        assertEquals(1, subtasksInManager.size());
        assertEquals(subtaskInManager, subtasksInManager.getFirst());

        FileBackedTaskManager newFileBackedTaskManager = FileBackedTaskManager.loadFromFile(tempFilePath);
        Task newSubtask = newFileBackedTaskManager.getSubtaskById(firstSubtask.getId());

        assertEquals(firstSubtask, newSubtask);

        fileBackedTaskManager.createSubtask(firstSubtask);

        assertEquals(2, fileBackedTaskManager.getAllSubtasks().size());
        assertNotEquals(firstSubtask, subtaskInManager);
    }

    @Test
    void subtaskInManagerCannotBeChangedWithoutUpdateMethod() {
        Epic epic = new Epic("Title", "Description");
        fileBackedTaskManager.createEpic(epic);
        Subtask subtask = new Subtask("Title", "Description", TaskStatus.NEW, epic.getId());
        fileBackedTaskManager.createSubtask(subtask);
        subtask.setId(-1);
        subtask.setTitle("NewTitle");
        subtask.setDescription("NewDescription");
        subtask.setStatus(TaskStatus.IN_PROGRESS);
        Subtask subtaskInManager = fileBackedTaskManager.getAllSubtasks().getFirst();

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

        fileBackedTaskManager.createTask(firstTask);
        secondTask.setId(firstTask.getId());
        fileBackedTaskManager.updateTask(secondTask);
        Task updatedTaskInManager = fileBackedTaskManager.getTaskById(secondTask.getId());

        assertEquals("NewTitle", updatedTaskInManager.getTitle());
        assertEquals("NewDescription", updatedTaskInManager.getDescription());
        assertEquals(TaskStatus.IN_PROGRESS, updatedTaskInManager.getStatus());
        assertEquals(firstTask, updatedTaskInManager);

        thirdTask.setId(-1);
        Task notUpdatedTask = fileBackedTaskManager.updateTask(thirdTask);
        FileBackedTaskManager newFileBackedTaskManager = FileBackedTaskManager.loadFromFile(tempFilePath);
        Task newTask = newFileBackedTaskManager.getTaskById(firstTask.getId());

        assertEquals(firstTask, newTask);
        assertEquals(1, newFileBackedTaskManager.getAllTasks().size());
        assertNull(notUpdatedTask);
    }

    @Test
    void updateEpic() {
        Epic firstEpic = new Epic("Title", "Description");
        Epic secondEpic = new Epic("NewTitle", "NewDescription");
        Epic thirdEpic = new Epic("Title", "Description");

        fileBackedTaskManager.createEpic(firstEpic);
        secondEpic.setId(firstEpic.getId());
        fileBackedTaskManager.updateEpic(secondEpic);
        Epic updatedEpicInManager = fileBackedTaskManager.getEpicById(secondEpic.getId());

        assertEquals("NewTitle", updatedEpicInManager.getTitle());
        assertEquals("NewDescription", updatedEpicInManager.getDescription());
        assertEquals(TaskStatus.NEW, updatedEpicInManager.getStatus());
        assertEquals(firstEpic, updatedEpicInManager);

        thirdEpic.setId(-1);
        Epic notUpdatedEpic = fileBackedTaskManager.updateEpic(thirdEpic);
        FileBackedTaskManager newFileBackedTaskManager = FileBackedTaskManager.loadFromFile(tempFilePath);
        Task newEpic = newFileBackedTaskManager.getEpicById(firstEpic.getId());

        assertEquals(firstEpic, newEpic);
        assertEquals(1, newFileBackedTaskManager.getAllEpics().size());
        assertNull(notUpdatedEpic);
    }

    @Test
    void updateSubtask() {
        Epic epic = new Epic("Title", "Description");
        fileBackedTaskManager.createEpic(epic);

        Subtask firstSubtask = new Subtask("T", "D", TaskStatus.NEW, epic.getId());
        Subtask secondSubtask = new Subtask("NT", "ND", TaskStatus.IN_PROGRESS, epic.getId());
        Subtask thirdSubtask = new Subtask("T", "D", TaskStatus.NEW, epic.getId());

        fileBackedTaskManager.createSubtask(firstSubtask);
        secondSubtask.setId(firstSubtask.getId());
        fileBackedTaskManager.updateSubtask(secondSubtask);
        Subtask updatedSubtaskInManager = fileBackedTaskManager.getSubtaskById(secondSubtask.getId());

        assertEquals("NT", updatedSubtaskInManager.getTitle());
        assertEquals("ND", updatedSubtaskInManager.getDescription());
        assertEquals(TaskStatus.IN_PROGRESS, updatedSubtaskInManager.getStatus());
        assertEquals(firstSubtask.getEpicId(), updatedSubtaskInManager.getEpicId());
        assertEquals(firstSubtask, updatedSubtaskInManager);

        thirdSubtask.setId(-1);
        Subtask notUpdatedSubtask = fileBackedTaskManager.updateSubtask(thirdSubtask);
        FileBackedTaskManager newFileBackedTaskManager = FileBackedTaskManager.loadFromFile(tempFilePath);
        Task newSubtask = newFileBackedTaskManager.getSubtaskById(firstSubtask.getId());

        assertEquals(firstSubtask, newSubtask);
        assertEquals(1, newFileBackedTaskManager.getAllEpics().size());
        assertNull(notUpdatedSubtask);
    }

    @Test
    void getTaskById() {
        Task task = new Task("Title", "Description", TaskStatus.NEW);
        fileBackedTaskManager.createTask(task);

        assertEquals(task, fileBackedTaskManager.getTaskById(task.getId()));
        assertNull(fileBackedTaskManager.getTaskById(-1));
    }

    @Test
    void taskInManagerCannotBeChangedAfterGetById() {
        Task task = new Task("Title", "Description", TaskStatus.NEW);
        fileBackedTaskManager.createTask(task);
        Task taskInManagerAfterCreate = fileBackedTaskManager.getTaskById(task.getId());
        taskInManagerAfterCreate.setTitle("NewTitle");
        taskInManagerAfterCreate.setDescription("NewDescription");
        taskInManagerAfterCreate.setStatus(TaskStatus.IN_PROGRESS);
        taskInManagerAfterCreate.setId(-1);
        Task taskInManagerAfterChanges = fileBackedTaskManager.getTaskById(task.getId());

        assertNotEquals(taskInManagerAfterCreate.getId(), taskInManagerAfterChanges.getId());
        assertNotEquals(taskInManagerAfterCreate.getTitle(), taskInManagerAfterChanges.getTitle());
        assertNotEquals(taskInManagerAfterCreate.getDescription(), taskInManagerAfterChanges.getDescription());
        assertNotEquals(taskInManagerAfterCreate.getStatus(), taskInManagerAfterChanges.getStatus());
    }

    @Test
    void getEpicById() {
        Epic epic = new Epic("Title", "Description");
        fileBackedTaskManager.createEpic(epic);

        assertEquals(epic, fileBackedTaskManager.getEpicById(epic.getId()));
        assertNull(fileBackedTaskManager.getEpicById(-1));
    }

    @Test
    void epicInManagerCannotBeChangedAfterGetById() {
        Epic epic = new Epic("Title", "Description");
        fileBackedTaskManager.createEpic(epic);
        Epic epicInManagerAfterCreate = fileBackedTaskManager.getEpicById(epic.getId());
        epicInManagerAfterCreate.setTitle("NewTitle");
        epicInManagerAfterCreate.setDescription("NewDescription");
        epicInManagerAfterCreate.setStatus(TaskStatus.IN_PROGRESS);
        epicInManagerAfterCreate.setId(-1);
        Epic epicInManagerAfterChanges = fileBackedTaskManager.getEpicById(epic.getId());

        assertNotEquals(epicInManagerAfterCreate.getId(), epicInManagerAfterChanges.getId());
        assertNotEquals(epicInManagerAfterCreate.getTitle(), epicInManagerAfterChanges.getTitle());
        assertNotEquals(epicInManagerAfterCreate.getDescription(), epicInManagerAfterChanges.getDescription());
        assertNotEquals(epicInManagerAfterCreate.getStatus(), epicInManagerAfterChanges.getStatus());
    }

    @Test
    void getSubtaskById() {
        Epic epic = new Epic("Title", "Description");
        fileBackedTaskManager.createEpic(epic);
        Subtask subtask = new Subtask("Title", "Description", TaskStatus.NEW, epic.getId());
        fileBackedTaskManager.createSubtask(subtask);

        assertEquals(subtask, fileBackedTaskManager.getSubtaskById(subtask.getId()));
        assertNull(fileBackedTaskManager.getSubtaskById(-1));
    }

    @Test
    void subtaskInManagerCannotBeChangedAfterGetById() {
        Epic epic = new Epic("Title", "Description");
        fileBackedTaskManager.createEpic(epic);
        Subtask subtask = new Subtask("Title", "Description", TaskStatus.NEW, epic.getId());
        fileBackedTaskManager.createSubtask(subtask);
        Subtask subtaskInManagerAfterCreate = fileBackedTaskManager.getSubtaskById(subtask.getId());
        subtaskInManagerAfterCreate.setTitle("NewTitle");
        subtaskInManagerAfterCreate.setDescription("NewDescription");
        subtaskInManagerAfterCreate.setStatus(TaskStatus.IN_PROGRESS);
        subtaskInManagerAfterCreate.setId(-1);
        Subtask subtaskInManagerAfterChanges = fileBackedTaskManager.getSubtaskById(subtask.getId());

        assertNotEquals(subtaskInManagerAfterCreate.getId(), subtaskInManagerAfterChanges.getId());
        assertNotEquals(subtaskInManagerAfterCreate.getTitle(), subtaskInManagerAfterChanges.getTitle());
        assertNotEquals(subtaskInManagerAfterCreate.getDescription(), subtaskInManagerAfterChanges.getDescription());
        assertNotEquals(subtaskInManagerAfterCreate.getStatus(), subtaskInManagerAfterChanges.getStatus());
    }

    @Test
    void tasksInManagerCannotBeChangedAfterGetAllTasks() {
        Task task = new Task("Title", "Description", TaskStatus.NEW);
        fileBackedTaskManager.createTask(task);
        Task taskInManagerAfterCreate = fileBackedTaskManager.getAllTasks().getFirst();
        taskInManagerAfterCreate.setTitle("NewTitle");
        taskInManagerAfterCreate.setDescription("NewDescription");
        taskInManagerAfterCreate.setStatus(TaskStatus.IN_PROGRESS);
        taskInManagerAfterCreate.setId(-1);
        Task taskInManagerAfterChanges = fileBackedTaskManager.getTaskById(task.getId());

        assertNotEquals(taskInManagerAfterCreate.getId(), taskInManagerAfterChanges.getId());
        assertNotEquals(taskInManagerAfterCreate.getTitle(), taskInManagerAfterChanges.getTitle());
        assertNotEquals(taskInManagerAfterCreate.getDescription(), taskInManagerAfterChanges.getDescription());
        assertNotEquals(taskInManagerAfterCreate.getStatus(), taskInManagerAfterChanges.getStatus());

        List<Task> tasks = fileBackedTaskManager.getAllTasks();
        tasks.clear();

        assertEquals(1, fileBackedTaskManager.getAllTasks().size());
    }

    @Test
    void epicsInManagerCannotBeChangedAfterGetAllEpics() {
        Epic epic = new Epic("Title", "Description");
        fileBackedTaskManager.createEpic(epic);
        Epic epicInManagerAfterCreate = fileBackedTaskManager.getAllEpics().getFirst();
        epicInManagerAfterCreate.setTitle("NewTitle");
        epicInManagerAfterCreate.setDescription("NewDescription");
        epicInManagerAfterCreate.setStatus(TaskStatus.IN_PROGRESS);
        epicInManagerAfterCreate.setId(-1);
        Epic epicInManagerAfterChanges = fileBackedTaskManager.getEpicById(epic.getId());

        assertNotEquals(epicInManagerAfterCreate.getId(), epicInManagerAfterChanges.getId());
        assertNotEquals(epicInManagerAfterCreate.getTitle(), epicInManagerAfterChanges.getTitle());
        assertNotEquals(epicInManagerAfterCreate.getDescription(), epicInManagerAfterChanges.getDescription());
        assertNotEquals(epicInManagerAfterCreate.getStatus(), epicInManagerAfterChanges.getStatus());

        List<Epic> epics = fileBackedTaskManager.getAllEpics();
        epics.clear();

        assertEquals(1, fileBackedTaskManager.getAllEpics().size());
    }

    @Test
    void subtasksInManagerCannotBeChangedAfterGetAllSubtasks() {
        Epic epic = new Epic("Title", "Description");
        fileBackedTaskManager.createEpic(epic);
        Subtask subtask = new Subtask("Title", "Description", TaskStatus.NEW, epic.getId());
        fileBackedTaskManager.createSubtask(subtask);
        Subtask subtaskInManagerAfterCreate = fileBackedTaskManager.getAllSubtasks().getFirst();
        subtaskInManagerAfterCreate.setTitle("NewTitle");
        subtaskInManagerAfterCreate.setDescription("NewDescription");
        subtaskInManagerAfterCreate.setStatus(TaskStatus.IN_PROGRESS);
        subtaskInManagerAfterCreate.setId(-1);
        Subtask subtaskInManagerAfterChanges = fileBackedTaskManager.getSubtaskById(subtask.getId());

        assertNotEquals(subtaskInManagerAfterCreate.getId(), subtaskInManagerAfterChanges.getId());
        assertNotEquals(subtaskInManagerAfterCreate.getTitle(), subtaskInManagerAfterChanges.getTitle());
        assertNotEquals(subtaskInManagerAfterCreate.getDescription(), subtaskInManagerAfterChanges.getDescription());
        assertNotEquals(subtaskInManagerAfterCreate.getStatus(), subtaskInManagerAfterChanges.getStatus());

        List<Subtask> subtasks = fileBackedTaskManager.getAllSubtasks();
        subtasks.clear();

        assertEquals(1, fileBackedTaskManager.getAllSubtasks().size());
    }

    @Test
    void removeTaskById() {
        Task task = new Task("Title", "Description", TaskStatus.NEW);
        fileBackedTaskManager.createTask(task);
        Task deletedTask = fileBackedTaskManager.removeTaskById(task.getId());
        FileBackedTaskManager newFileBackedTaskManager = FileBackedTaskManager.loadFromFile(tempFilePath);

        assertEquals(task, deletedTask);
        assertTrue(newFileBackedTaskManager.getAllTasks().isEmpty());
        assertNull(fileBackedTaskManager.getTaskById(deletedTask.getId()));
        assertNull(fileBackedTaskManager.removeTaskById(-1));
    }

    @Test
    void removeEpicById() {
        Epic epic = new Epic("Title", "Description");
        fileBackedTaskManager.createEpic(epic);
        Subtask subtask = new Subtask("Title", "Description", TaskStatus.NEW, epic.getId());
        fileBackedTaskManager.createSubtask(subtask);
        Epic deletedEpic = fileBackedTaskManager.removeEpicById(epic.getId());
        FileBackedTaskManager newFileBackedTaskManager = FileBackedTaskManager.loadFromFile(tempFilePath);

        assertEquals(epic, deletedEpic);
        assertTrue(newFileBackedTaskManager.getAllEpics().isEmpty());
        assertTrue(newFileBackedTaskManager.getAllSubtasks().isEmpty());
        assertNull(fileBackedTaskManager.getEpicById(deletedEpic.getId()));
        assertNull(fileBackedTaskManager.getSubtaskById(subtask.getId()));
        assertNull(fileBackedTaskManager.removeEpicById(-1));
    }

    @Test
    void removeSubtaskById() {
        Epic epic = new Epic("Title", "Description");
        fileBackedTaskManager.createEpic(epic);
        Subtask subtask = new Subtask("Title", "Description", TaskStatus.NEW, epic.getId());
        fileBackedTaskManager.createSubtask(subtask);
        Subtask deletedSubtask = fileBackedTaskManager.removeSubtaskById(subtask.getId());
        FileBackedTaskManager newFileBackedTaskManager = FileBackedTaskManager.loadFromFile(tempFilePath);

        assertEquals(subtask, deletedSubtask);
        assertTrue(newFileBackedTaskManager.getAllSubtasks().isEmpty());
        assertEquals(1, newFileBackedTaskManager.getAllEpics().size());
        assertNull(fileBackedTaskManager.getSubtaskById(subtask.getId()));
        assertTrue(fileBackedTaskManager.getEpicById(epic.getId()).getSubtasksIds().isEmpty());
        assertNull(fileBackedTaskManager.removeSubtaskById(-1));
    }

    @Test
    void removeAllTasks() {
        Task firstTask = new Task("Title", "Description", TaskStatus.NEW);
        Task secondTask = new Task("Title", "Description", TaskStatus.NEW);
        fileBackedTaskManager.removeAllTasks();
        FileBackedTaskManager newFileBackedTaskManager = FileBackedTaskManager.loadFromFile(tempFilePath);

        assertNotNull(newFileBackedTaskManager.getAllTasks());
        assertTrue(newFileBackedTaskManager.getAllTasks().isEmpty());
        assertNotNull(fileBackedTaskManager.getAllTasks());
        assertTrue(fileBackedTaskManager.getAllTasks().isEmpty());

        fileBackedTaskManager.createTask(firstTask);
        fileBackedTaskManager.createTask(secondTask);
        newFileBackedTaskManager = FileBackedTaskManager.loadFromFile(tempFilePath);

        assertEquals(2, fileBackedTaskManager.getAllTasks().size());
        assertEquals(2, newFileBackedTaskManager.getAllTasks().size());

        fileBackedTaskManager.removeAllTasks();
        newFileBackedTaskManager = FileBackedTaskManager.loadFromFile(tempFilePath);

        assertTrue(fileBackedTaskManager.getAllTasks().isEmpty());
        assertTrue(newFileBackedTaskManager.getAllTasks().isEmpty());
    }

    @Test
    void removeAllEpics() {
        Epic firstEpic = new Epic("Title", "Description");
        Epic secondEpic = new Epic("Title", "Description");
        fileBackedTaskManager.removeAllEpics();
        FileBackedTaskManager newFileBackedTaskManager = FileBackedTaskManager.loadFromFile(tempFilePath);

        assertNotNull(newFileBackedTaskManager.getAllEpics());
        assertTrue(newFileBackedTaskManager.getAllEpics().isEmpty());
        assertNotNull(fileBackedTaskManager.getAllEpics());
        assertTrue(fileBackedTaskManager.getAllEpics().isEmpty());

        fileBackedTaskManager.createEpic(firstEpic);
        fileBackedTaskManager.createEpic(secondEpic);
        Subtask firstSubtask = new Subtask("Title", "Description", TaskStatus.NEW, firstEpic.getId());
        Subtask secondSubtask = new Subtask("Title", "Description", TaskStatus.NEW, secondEpic.getId());
        fileBackedTaskManager.createSubtask(firstSubtask);
        fileBackedTaskManager.createSubtask(secondSubtask);
        newFileBackedTaskManager = FileBackedTaskManager.loadFromFile(tempFilePath);

        assertEquals(2, newFileBackedTaskManager.getAllEpics().size());
        assertEquals(2, newFileBackedTaskManager.getAllSubtasks().size());
        assertEquals(2, fileBackedTaskManager.getAllEpics().size());
        assertEquals(2, fileBackedTaskManager.getAllSubtasks().size());

        fileBackedTaskManager.removeAllEpics();
        newFileBackedTaskManager = FileBackedTaskManager.loadFromFile(tempFilePath);

        assertTrue(newFileBackedTaskManager.getAllEpics().isEmpty());
        assertTrue(newFileBackedTaskManager.getAllSubtasks().isEmpty());
        assertTrue(fileBackedTaskManager.getAllEpics().isEmpty());
        assertTrue(fileBackedTaskManager.getAllSubtasks().isEmpty());
    }

    @Test
    void removeAllSubtasks() {
        Epic firstEpic = new Epic("Title", "Description");
        Epic secondEpic = new Epic("Title", "Description");
        fileBackedTaskManager.createEpic(firstEpic);
        fileBackedTaskManager.createEpic(secondEpic);
        Subtask firstSubtask = new Subtask("Title", "Description", TaskStatus.NEW, firstEpic.getId());
        Subtask secondSubtask = new Subtask("Title", "Description", TaskStatus.NEW, secondEpic.getId());
        FileBackedTaskManager newFileBackedTaskManager = FileBackedTaskManager.loadFromFile(tempFilePath);

        assertNotNull(newFileBackedTaskManager.getAllSubtasks());
        assertTrue(newFileBackedTaskManager.getAllSubtasks().isEmpty());
        assertNotNull(fileBackedTaskManager.getAllSubtasks());
        assertTrue(fileBackedTaskManager.getAllSubtasks().isEmpty());

        fileBackedTaskManager.createSubtask(firstSubtask);
        fileBackedTaskManager.createSubtask(secondSubtask);
        newFileBackedTaskManager = FileBackedTaskManager.loadFromFile(tempFilePath);

        assertEquals(2, newFileBackedTaskManager.getAllSubtasks().size());
        assertEquals(2, newFileBackedTaskManager.getAllEpics().size());
        assertEquals(2, fileBackedTaskManager.getAllSubtasks().size());
        assertEquals(2, fileBackedTaskManager.getAllEpics().size());

        fileBackedTaskManager.removeAllSubtasks();
        newFileBackedTaskManager = FileBackedTaskManager.loadFromFile(tempFilePath);

        assertTrue(newFileBackedTaskManager.getAllSubtasks().isEmpty());
        assertTrue(newFileBackedTaskManager.getEpicById(firstEpic.getId()).getSubtasksIds().isEmpty());
        assertTrue(newFileBackedTaskManager.getEpicById(secondEpic.getId()).getSubtasksIds().isEmpty());
        assertTrue(fileBackedTaskManager.getAllSubtasks().isEmpty());
        assertTrue(fileBackedTaskManager.getEpicById(firstEpic.getId()).getSubtasksIds().isEmpty());
        assertTrue(fileBackedTaskManager.getEpicById(secondEpic.getId()).getSubtasksIds().isEmpty());
    }

    @Test
    void getSubtasksByEpicId() {
        Epic epic = new Epic("Title", "Description");
        fileBackedTaskManager.createEpic(epic);
        Subtask subtask = new Subtask("Title", "Description", TaskStatus.NEW, epic.getId());
        fileBackedTaskManager.createSubtask(subtask);

        assertNotNull(fileBackedTaskManager.getSubtasksByEpicId(-1));
        assertTrue(fileBackedTaskManager.getSubtasksByEpicId(-1).isEmpty());

        List<Subtask> subtasks = fileBackedTaskManager.getSubtasksByEpicId(epic.getId());

        assertEquals(1, subtasks.size());
        assertEquals(subtask, subtasks.getFirst());
    }

    @Test
    void getHistory() {
        Task task = new Task("Title", "Description", TaskStatus.NEW);
        Epic firstEpic = new Epic("Title", "Description");
        Epic secondEpic = new Epic("Title", "Description");
        fileBackedTaskManager.createTask(task);
        fileBackedTaskManager.createEpic(firstEpic);
        fileBackedTaskManager.createEpic(secondEpic);
        Subtask firstSubtask = new Subtask("Title", "Description", TaskStatus.NEW, firstEpic.getId());
        Subtask secondSubtask = new Subtask("Title", "Description", TaskStatus.NEW, secondEpic.getId());
        fileBackedTaskManager.createSubtask(firstSubtask);
        fileBackedTaskManager.createSubtask(secondSubtask);

        assertNotNull(fileBackedTaskManager.getHistory());
        assertTrue(fileBackedTaskManager.getHistory().isEmpty());

        fileBackedTaskManager.getTaskById(task.getId());
        fileBackedTaskManager.getEpicById(firstEpic.getId());
        fileBackedTaskManager.getSubtaskById(firstSubtask.getId());
        fileBackedTaskManager.getEpicById(secondEpic.getId());
        fileBackedTaskManager.getSubtaskById(secondSubtask.getId());

        List<Task> history = fileBackedTaskManager.getHistory();

        assertEquals(5, history.size());
        assertEquals(task.getId(), history.getFirst().getId());
        assertEquals(firstEpic.getId(), history.get(1).getId());
        assertEquals(firstSubtask.getId(), history.get(2).getId());
        assertEquals(secondEpic.getId(), history.get(3).getId());
        assertEquals(secondSubtask.getId(), history.get(4).getId());

        fileBackedTaskManager.removeTaskById(task.getId());
        history = fileBackedTaskManager.getHistory();

        assertEquals(4, history.size());
        assertEquals(firstEpic.getId(), history.getFirst().getId());
        assertEquals(firstSubtask.getId(), history.get(1).getId());
        assertEquals(secondEpic.getId(), history.get(2).getId());
        assertEquals(secondSubtask.getId(), history.get(3).getId());

        fileBackedTaskManager.removeEpicById(firstEpic.getId());
        history = fileBackedTaskManager.getHistory();

        assertEquals(2, history.size());
        assertEquals(secondEpic.getId(), history.getFirst().getId());
        assertEquals(secondSubtask.getId(), history.get(1).getId());

        fileBackedTaskManager.removeSubtaskById(secondSubtask.getId());
        history = fileBackedTaskManager.getHistory();

        assertEquals(1, history.size());
        assertEquals(secondEpic.getId(), history.getFirst().getId());

        fileBackedTaskManager.createTask(task);
        fileBackedTaskManager.createSubtask(secondSubtask);
        fileBackedTaskManager.getTaskById(task.getId());
        fileBackedTaskManager.getSubtaskById(secondSubtask.getId());
        fileBackedTaskManager.removeAllTasks();
        fileBackedTaskManager.removeAllSubtasks();
        history = fileBackedTaskManager.getHistory();

        assertEquals(1, history.size());
        assertEquals(secondEpic.getId(), history.getFirst().getId());

        fileBackedTaskManager.createSubtask(secondSubtask);
        fileBackedTaskManager.getSubtaskById(secondSubtask.getId());
        fileBackedTaskManager.removeAllEpics();
        history = fileBackedTaskManager.getHistory();

        assertTrue(history.isEmpty());
    }

    @Test
    void calculateEpicStatus() {
        Epic epic = new Epic("Title", "Description");
        fileBackedTaskManager.createEpic(epic);

        assertEquals(TaskStatus.NEW, fileBackedTaskManager.getEpicById(epic.getId()).getStatus());

        Subtask firstSubtask = new Subtask("Title", "Description", TaskStatus.DONE, epic.getId());
        Subtask secondSubtask = new Subtask("Title", "Description", TaskStatus.DONE, epic.getId());
        Subtask thirdSubtask = new Subtask("Title", "Description", TaskStatus.DONE, epic.getId());
        fileBackedTaskManager.createSubtask(firstSubtask);
        fileBackedTaskManager.createSubtask(secondSubtask);
        fileBackedTaskManager.createSubtask(thirdSubtask);

        assertEquals(TaskStatus.DONE, fileBackedTaskManager.getEpicById(epic.getId()).getStatus());

        firstSubtask.setStatus(TaskStatus.IN_PROGRESS);
        fileBackedTaskManager.updateSubtask(firstSubtask);

        assertEquals(TaskStatus.IN_PROGRESS, fileBackedTaskManager.getEpicById(epic.getId()).getStatus());

        firstSubtask.setStatus(TaskStatus.NEW);
        fileBackedTaskManager.updateSubtask(firstSubtask);

        assertEquals(TaskStatus.IN_PROGRESS, fileBackedTaskManager.getEpicById(epic.getId()).getStatus());

        secondSubtask.setStatus(TaskStatus.NEW);
        thirdSubtask.setStatus(TaskStatus.NEW);
        fileBackedTaskManager.updateSubtask(secondSubtask);
        fileBackedTaskManager.updateSubtask(thirdSubtask);

        assertEquals(TaskStatus.NEW, fileBackedTaskManager.getEpicById(epic.getId()).getStatus());
    }

    @Test
    void saveManagerStateInFile() {
        Task task = new Task("Title", "Description", TaskStatus.NEW);
        Epic epic = new Epic("Title", "Description");
        fileBackedTaskManager.createTask(task);
        fileBackedTaskManager.createEpic(epic);
        Subtask subtask = new Subtask("Title", "Description", TaskStatus.NEW, epic.getId());
        fileBackedTaskManager.createSubtask(subtask);
        List<String> lines = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(tempFilePath.toString(), UTF_8))) {
            while (br.ready()) {
                lines.add(br.readLine());
            }
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        assertEquals("id,type,title,status,description,epicId", lines.getFirst());
        assertEquals(String.format("%d,TASK,Title,NEW,Description", task.getId()), lines.get(1));
        assertEquals(String.format("%d,EPIC,Title,NEW,Description", epic.getId()), lines.get(2));
        assertEquals(String.format(
                "%d,SUBTASK,Title,NEW,Description,%d",
                subtask.getId(),
                subtask.getEpicId()
        ), lines.getLast());
    }

    @Test
    void successCreateManagerFromFile() {
        Task task = new Task("Title", "Description", TaskStatus.NEW);
        Epic epic = new Epic("Title", "Description");
        fileBackedTaskManager.createTask(task);
        fileBackedTaskManager.createEpic(epic);
        Subtask subtask = new Subtask("Title", "Description", TaskStatus.NEW, epic.getId());
        fileBackedTaskManager.createSubtask(subtask);
        FileBackedTaskManager newFileBackedTaskManager = FileBackedTaskManager.loadFromFile(tempFilePath);

        assertNotNull(newFileBackedTaskManager);
        assertNotNull(newFileBackedTaskManager.getAllTasks());
        assertNotNull(newFileBackedTaskManager.getAllEpics());
        assertNotNull(newFileBackedTaskManager.getAllSubtasks());
        assertNotNull(newFileBackedTaskManager.getHistory());
        assertEquals(1, newFileBackedTaskManager.getAllTasks().size());
        assertEquals(1, newFileBackedTaskManager.getAllEpics().size());
        assertEquals(1, newFileBackedTaskManager.getAllSubtasks().size());
        assertTrue(newFileBackedTaskManager.getHistory().isEmpty());

        assertEquals(task.getId(), newFileBackedTaskManager.getAllTasks().getFirst().getId());
        assertEquals(task.getTitle(), newFileBackedTaskManager.getAllTasks().getFirst().getTitle());
        assertEquals(task.getDescription(), newFileBackedTaskManager.getAllTasks().getFirst().getDescription());
        assertEquals(task.getStatus(), newFileBackedTaskManager.getAllTasks().getFirst().getStatus());

        assertEquals(epic.getId(), newFileBackedTaskManager.getAllEpics().getFirst().getId());
        assertEquals(epic.getTitle(), newFileBackedTaskManager.getAllEpics().getFirst().getTitle());
        assertEquals(epic.getDescription(), newFileBackedTaskManager.getAllEpics().getFirst().getDescription());
        assertEquals(epic.getStatus(), newFileBackedTaskManager.getAllEpics().getFirst().getStatus());

        assertEquals(subtask.getId(), newFileBackedTaskManager.getAllSubtasks().getFirst().getId());
        assertEquals(subtask.getTitle(), newFileBackedTaskManager.getAllSubtasks().getFirst().getTitle());
        assertEquals(subtask.getDescription(), newFileBackedTaskManager.getAllSubtasks().getFirst().getDescription());
        assertEquals(subtask.getStatus(), newFileBackedTaskManager.getAllSubtasks().getFirst().getStatus());
        assertEquals(subtask.getEpicId(), newFileBackedTaskManager.getAllSubtasks().getFirst().getEpicId());
    }

    @Test
    void successCreateManagerFromEmptyFile() {
        FileBackedTaskManager newFileBackedTaskManager = FileBackedTaskManager.loadFromFile(tempFilePath);

        assertNotNull(newFileBackedTaskManager);
        assertNotNull(newFileBackedTaskManager.getAllTasks());
        assertNotNull(newFileBackedTaskManager.getAllEpics());
        assertNotNull(newFileBackedTaskManager.getAllSubtasks());
        assertNotNull(newFileBackedTaskManager.getHistory());
        assertTrue(newFileBackedTaskManager.getAllTasks().isEmpty());
        assertTrue(newFileBackedTaskManager.getAllEpics().isEmpty());
        assertTrue(newFileBackedTaskManager.getAllSubtasks().isEmpty());
        assertTrue(newFileBackedTaskManager.getHistory().isEmpty());
    }

    @Test
    void errorCreateManagerFromFileWithIndexOutOfBoundsException() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(tempFilePath.toString(), UTF_8))) {
            bw.write("id,type,title,status,description,epicId\n1,TASK,Title,NEW");
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        FileBackedTaskManager newFileBackedTaskManager = FileBackedTaskManager.loadFromFile(tempFilePath);

        assertNotNull(newFileBackedTaskManager);
        assertNotNull(newFileBackedTaskManager.getAllTasks());
        assertNotNull(newFileBackedTaskManager.getAllEpics());
        assertNotNull(newFileBackedTaskManager.getAllSubtasks());
        assertNotNull(newFileBackedTaskManager.getHistory());
        assertTrue(newFileBackedTaskManager.getAllTasks().isEmpty());
        assertTrue(newFileBackedTaskManager.getAllEpics().isEmpty());
        assertTrue(newFileBackedTaskManager.getAllSubtasks().isEmpty());
        assertTrue(newFileBackedTaskManager.getHistory().isEmpty());
    }

    @Test
    void errorCreateManagerFromFileWithIllegalArgumentException() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(tempFilePath.toString(), UTF_8))) {
            bw.write("id,type,title,status,description,epicId\nid,TASK,Title,NEW,Description");
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        FileBackedTaskManager newFileBackedTaskManager = FileBackedTaskManager.loadFromFile(tempFilePath);

        assertNotNull(newFileBackedTaskManager);
        assertNotNull(newFileBackedTaskManager.getAllTasks());
        assertNotNull(newFileBackedTaskManager.getAllEpics());
        assertNotNull(newFileBackedTaskManager.getAllSubtasks());
        assertNotNull(newFileBackedTaskManager.getHistory());
        assertTrue(newFileBackedTaskManager.getAllTasks().isEmpty());
        assertTrue(newFileBackedTaskManager.getAllEpics().isEmpty());
        assertTrue(newFileBackedTaskManager.getAllSubtasks().isEmpty());
        assertTrue(newFileBackedTaskManager.getHistory().isEmpty());
    }

    @Test
    void errorCreateManagerFromFileWithManagerTaskTypeException() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(tempFilePath.toString(), UTF_8))) {
            bw.write("id,type,title,status,description,epicId\n1,TASK,Title,ERROR,Description");
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        FileBackedTaskManager newFileBackedTaskManager = FileBackedTaskManager.loadFromFile(tempFilePath);

        assertNotNull(newFileBackedTaskManager);
        assertNotNull(newFileBackedTaskManager.getAllTasks());
        assertNotNull(newFileBackedTaskManager.getAllEpics());
        assertNotNull(newFileBackedTaskManager.getAllSubtasks());
        assertNotNull(newFileBackedTaskManager.getHistory());
        assertTrue(newFileBackedTaskManager.getAllTasks().isEmpty());
        assertTrue(newFileBackedTaskManager.getAllEpics().isEmpty());
        assertTrue(newFileBackedTaskManager.getAllSubtasks().isEmpty());
        assertTrue(newFileBackedTaskManager.getHistory().isEmpty());
    }
}