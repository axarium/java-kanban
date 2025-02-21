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
    private InMemoryTaskManager inMemoryTaskManager = new InMemoryTaskManager();

    @BeforeEach
    void createNewInMemoryTaskManager() {
        inMemoryTaskManager = new InMemoryTaskManager();
    }

    @Test
    void createTask() {
        Task task = new Task("Title", "Description", TaskStatus.NEW);
        inMemoryTaskManager.createTask(task);

        assertEquals("Title", task.getTitle());
        assertEquals("Description", task.getDescription());
        assertEquals(TaskStatus.NEW, task.getStatus());
        assertTrue(task.getId() > 0);

        final List<Task> tasks = inMemoryTaskManager.getAllTasks();

        assertNotNull(tasks);
        assertEquals(1, tasks.size());
        assertEquals(task, tasks.getFirst());
    }

    @Test
    void createEpic() {
        Epic epic = new Epic("Title", "Description");
        inMemoryTaskManager.createEpic(epic);

        assertEquals("Title", epic.getTitle());
        assertEquals("Description", epic.getDescription());
        assertEquals(TaskStatus.NEW, epic.getStatus());
        assertTrue(epic.getId() > 0);

        final List<Epic> epics = inMemoryTaskManager.getAllEpics();

        assertNotNull(epics);
        assertEquals(1, epics.size());
        assertEquals(epic, epics.getFirst());
    }

    @Test
    void createSubtask() {
        Epic epic = new Epic("Title", "Description");
        inMemoryTaskManager.createEpic(epic);

        Subtask subtask1 = new Subtask("Title1", "Description1", TaskStatus.NEW, epic.getId());
        Subtask createdSubtask = inMemoryTaskManager.createSubtask(subtask1);

        Subtask subtask2 = new Subtask(
                "Title2",
                "Description2",
                TaskStatus.IN_PROGRESS,
                createdSubtask.getId()
        );
        Subtask notCreatedSubtask = inMemoryTaskManager.createSubtask(subtask2);

        assertEquals("Title1", createdSubtask.getTitle());
        assertEquals("Description1", createdSubtask.getDescription());
        assertEquals(TaskStatus.NEW, createdSubtask.getStatus());
        assertTrue(createdSubtask.getId() > 0);

        assertEquals(1, epic.getSubtasksIds().size());
        assertEquals(createdSubtask.getId(), epic.getSubtasksIds().getFirst());

        assertNull(notCreatedSubtask);

        final List<Subtask> subtasks = inMemoryTaskManager.getAllSubtasks();

        assertNotNull(subtasks);
        assertEquals(1, subtasks.size());
        assertEquals(subtask1, subtasks.getFirst());
    }

    @Test
    void updateTask() {
        Task task1 = new Task("Title1", "Description1", TaskStatus.NEW);
        Task task2 = new Task("Title2", "Description2", TaskStatus.IN_PROGRESS);
        Task task3 = new Task("Title3", "Description3", TaskStatus.DONE);

        inMemoryTaskManager.createTask(task1);
        task2.setId(task1.getId());
        final Task updatedTask = inMemoryTaskManager.updateTask(task2);

        assertEquals("Title2", updatedTask.getTitle());
        assertEquals("Description2", updatedTask.getDescription());
        assertEquals(TaskStatus.IN_PROGRESS, updatedTask.getStatus());
        assertEquals(task1, updatedTask);

        task3.setId(-1);
        final Task notUpdatedTask = inMemoryTaskManager.updateTask(task3);

        assertNull(notUpdatedTask);
    }

    @Test
    void updateEpic() {
        Epic epic1 = new Epic("Title1", "Description1");
        Epic epic2 = new Epic("Title2", "Description2");
        Epic epic3 = new Epic("Title3", "Description3");

        inMemoryTaskManager.createEpic(epic1);
        epic2.setId(epic1.getId());
        final Epic updatedEpic = inMemoryTaskManager.updateEpic(epic2);

        assertEquals("Title2", updatedEpic.getTitle());
        assertEquals("Description2", updatedEpic.getDescription());
        assertEquals(TaskStatus.NEW, updatedEpic.getStatus());
        assertEquals(epic1, updatedEpic);

        epic3.setId(-1);
        final Epic notUpdatedEpic = inMemoryTaskManager.updateEpic(epic3);

        assertNull(notUpdatedEpic);
    }

    @Test
    void updateSubtask() {
        Epic epic = new Epic("Title", "Description");
        inMemoryTaskManager.createEpic(epic);

        Subtask subtask1 = new Subtask("Title1", "Description1", TaskStatus.NEW, epic.getId());
        Subtask subtask2 = new Subtask("Title2", "Description2", TaskStatus.IN_PROGRESS, epic.getId());
        Subtask subtask3 = new Subtask("Title3", "Description3", TaskStatus.DONE, epic.getId());

        inMemoryTaskManager.createSubtask(subtask1);
        subtask2.setId(subtask1.getId());
        final Subtask updatedSubtask = inMemoryTaskManager.updateSubtask(subtask2);

        assertEquals("Title2", updatedSubtask.getTitle());
        assertEquals("Description2", updatedSubtask.getDescription());
        assertEquals(TaskStatus.IN_PROGRESS, updatedSubtask.getStatus());
        assertEquals(subtask1.getEpicId(), updatedSubtask.getEpicId());
        assertEquals(subtask1, updatedSubtask);

        subtask3.setId(-1);
        final Subtask notUpdatedSubtask = inMemoryTaskManager.updateSubtask(subtask3);

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
    void getEpicById() {
        Epic epic = new Epic("Title", "Description");
        inMemoryTaskManager.createEpic(epic);

        assertEquals(epic, inMemoryTaskManager.getEpicById(epic.getId()));
        assertNull(inMemoryTaskManager.getEpicById(-1));
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
        Task task1 = new Task("Title1", "Description1", TaskStatus.NEW);
        Task task2 = new Task("Title2", "Description2", TaskStatus.IN_PROGRESS);

        inMemoryTaskManager.createTask(task1);
        inMemoryTaskManager.createTask(task2);
        inMemoryTaskManager.removeAllTasks();

        assertTrue(inMemoryTaskManager.getAllTasks().isEmpty());
    }

    @Test
    void removeAllEpics() {
        Epic epic1 = new Epic("Title1", "Description1");
        Epic epic2 = new Epic("Title2", "Description2");
        inMemoryTaskManager.createEpic(epic1);
        inMemoryTaskManager.createEpic(epic2);

        Subtask subtask1 = new Subtask("Title1", "Description1", TaskStatus.NEW, epic1.getId());
        Subtask subtask2 = new Subtask("Title2", "Description2", TaskStatus.IN_PROGRESS, epic2.getId());
        inMemoryTaskManager.createSubtask(subtask1);
        inMemoryTaskManager.createSubtask(subtask2);

        inMemoryTaskManager.removeAllEpics();

        assertTrue(inMemoryTaskManager.getAllEpics().isEmpty());
        assertTrue(inMemoryTaskManager.getAllSubtasks().isEmpty());
    }

    @Test
    void removeAllSubtasks() {
        Epic epic1 = new Epic("Title1", "Description1");
        Epic epic2 = new Epic("Title2", "Description2");
        inMemoryTaskManager.createEpic(epic1);
        inMemoryTaskManager.createEpic(epic2);

        Subtask subtask1 = new Subtask("Title1", "Description1", TaskStatus.NEW, epic1.getId());
        Subtask subtask2 = new Subtask("Title2", "Description2", TaskStatus.IN_PROGRESS, epic2.getId());
        inMemoryTaskManager.createSubtask(subtask1);
        inMemoryTaskManager.createSubtask(subtask2);

        inMemoryTaskManager.removeAllSubtasks();

        assertTrue(inMemoryTaskManager.getAllSubtasks().isEmpty());
        assertTrue(inMemoryTaskManager.getEpicById(epic1.getId()).getSubtasksIds().isEmpty());
        assertTrue(inMemoryTaskManager.getEpicById(epic2.getId()).getSubtasksIds().isEmpty());
    }

    @Test
    void getSubtasksByEpic() {
        Epic epic = new Epic("Title", "Description");
        inMemoryTaskManager.createEpic(epic);

        Subtask subtask = new Subtask("Title", "Description", TaskStatus.NEW, epic.getId());
        inMemoryTaskManager.createSubtask(subtask);

        final List<Subtask> subtasks = inMemoryTaskManager.getSubtasksByEpic(epic);

        assertNotNull(subtasks);
        assertEquals(1, subtasks.size());
        assertEquals(subtask, subtasks.getFirst());
    }

    @Test
    void getHistory() {
        Task task = new Task("Title", "Description", TaskStatus.NEW);
        Epic epic = new Epic("Title", "Description");

        inMemoryTaskManager.createTask(task);
        inMemoryTaskManager.createEpic(epic);

        inMemoryTaskManager.getTaskById(task.getId());
        inMemoryTaskManager.getEpicById(epic.getId());

        Subtask subtask = new Subtask("Title", "Description", TaskStatus.NEW, epic.getId());
        inMemoryTaskManager.createSubtask(subtask);

        inMemoryTaskManager.getSubtaskById(subtask.getId());

        final List<Task> history = inMemoryTaskManager.getHistory();

        assertNotNull(history);
        assertEquals(4, history.size());
        assertEquals(task.getId(), history.getFirst().getId());
        assertEquals(epic.getId(), history.get(1).getId());
        assertEquals(epic.getId(), history.get(2).getId());
        assertEquals(subtask.getId(), history.get(3).getId());
    }

    @Test
    void calculateEpicStatus() {
        Epic epic = new Epic("Title", "Description");
        inMemoryTaskManager.createEpic(epic);

        assertEquals(TaskStatus.NEW, epic.getStatus());

        Subtask subtask1 = new Subtask("Title", "Description", TaskStatus.DONE, epic.getId());
        Subtask subtask2 = new Subtask("Title", "Description", TaskStatus.DONE, epic.getId());
        Subtask subtask3 = new Subtask("Title", "Description", TaskStatus.DONE, epic.getId());

        inMemoryTaskManager.createSubtask(subtask1);
        inMemoryTaskManager.createSubtask(subtask2);
        inMemoryTaskManager.createSubtask(subtask3);

        assertEquals(TaskStatus.DONE, epic.getStatus());

        subtask1.setStatus(TaskStatus.IN_PROGRESS);
        inMemoryTaskManager.updateSubtask(subtask1);

        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus());

        subtask1.setStatus(TaskStatus.NEW);
        inMemoryTaskManager.updateSubtask(subtask1);

        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus());

        subtask2.setStatus(TaskStatus.NEW);
        subtask3.setStatus(TaskStatus.NEW);
        inMemoryTaskManager.updateSubtask(subtask2);
        inMemoryTaskManager.updateSubtask(subtask3);

        assertEquals(TaskStatus.NEW, epic.getStatus());
    }
}