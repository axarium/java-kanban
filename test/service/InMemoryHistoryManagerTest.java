package service;

import model.Task;
import model.TaskStatus;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    private static final LocalDateTime currentDate = LocalDateTime.now();
    private InMemoryHistoryManager inMemoryHistoryManager;

    @BeforeEach
    void createNewInMemoryHistoryManager() {
        inMemoryHistoryManager = new InMemoryHistoryManager();
    }

    @Test
    void createInMemoryHistoryManager() {
        assertNotNull(inMemoryHistoryManager.getHistory());
        assertTrue(inMemoryHistoryManager.getHistory().isEmpty());
    }

    @Test
    void addTaskInHistory() {
        Task task = new Task(
                "Title",
                "Description",
                TaskStatus.NEW,
                currentDate,
                Duration.ofMinutes(60)
        );
        task.setId(1);
        inMemoryHistoryManager.add(task);
        Task taskInHistory = inMemoryHistoryManager.getHistory().getFirst();

        assertEquals(1, inMemoryHistoryManager.getHistory().size());
        assertEquals(task, taskInHistory);
    }

    @Test
    void taskInHistoryCannotBeChanged() {
        Task task = new Task(
                "Title",
                "Description",
                TaskStatus.NEW,
                currentDate,
                Duration.ofMinutes(60)
        );
        task.setId(1);
        inMemoryHistoryManager.add(task);
        Task copyTaskFromHistory = inMemoryHistoryManager.getHistory().getFirst();
        copyTaskFromHistory.setId(2);
        copyTaskFromHistory.setTitle("NewTitle");
        copyTaskFromHistory.setDescription("NewDescription");
        copyTaskFromHistory.setStatus(TaskStatus.IN_PROGRESS);
        copyTaskFromHistory.setStartTime(currentDate.plusDays(1));
        copyTaskFromHistory.setDuration(Duration.ofMinutes(120));

        assertEquals(1, inMemoryHistoryManager.getHistory().getFirst().getId());
        assertEquals("Title", inMemoryHistoryManager.getHistory().getFirst().getTitle());
        assertEquals("Description", inMemoryHistoryManager.getHistory().getFirst().getDescription());
        assertEquals(TaskStatus.NEW, inMemoryHistoryManager.getHistory().getFirst().getStatus());
        assertEquals(currentDate, inMemoryHistoryManager.getHistory().getFirst().getStartTime());
        assertEquals(60, inMemoryHistoryManager.getHistory().getFirst().getDuration().toMinutes());
        assertEquals(currentDate.plusMinutes(60), inMemoryHistoryManager.getHistory().getFirst().getEndTime());
    }

    @Test
    void addDuplicateInHistory() {
        Task firstTask = new Task(
                "Title",
                "Description",
                TaskStatus.NEW,
                currentDate,
                Duration.ofMinutes(60)
        );
        firstTask.setId(1);
        Task secondTask = new Task(
                "NewTitle",
                "NewDescription",
                TaskStatus.IN_PROGRESS,
                currentDate.plusDays(1),
                Duration.ofMinutes(120)
        );
        secondTask.setId(1);
        inMemoryHistoryManager.add(firstTask);
        inMemoryHistoryManager.add(secondTask);
        Task taskInHistory = inMemoryHistoryManager.getHistory().getFirst();

        assertEquals(1, inMemoryHistoryManager.getHistory().size());
        assertEquals(1, taskInHistory.getId());
        assertEquals("NewTitle", taskInHistory.getTitle());
        assertEquals("NewDescription", taskInHistory.getDescription());
        assertEquals(TaskStatus.IN_PROGRESS, taskInHistory.getStatus());
        assertEquals(currentDate.plusDays(1), taskInHistory.getStartTime());
        assertEquals(120, taskInHistory.getDuration().toMinutes());
        assertEquals(currentDate.plusDays(1).plusMinutes(120), taskInHistory.getEndTime());
    }

    @Test
    void removeTasksFromHistory() {
        Task firstTask = new Task("Title", "Description");
        Task secondTask = new Task("Title", "Description");
        Task thirdTask = new Task("Title", "Description");
        Task fourthTask = new Task("Title", "Description");
        firstTask.setId(1);
        secondTask.setId(2);
        thirdTask.setId(3);
        fourthTask.setId(4);
        inMemoryHistoryManager.add(firstTask);
        inMemoryHistoryManager.add(secondTask);
        inMemoryHistoryManager.add(thirdTask);
        inMemoryHistoryManager.add(fourthTask);
        inMemoryHistoryManager.remove(3);
        List<Task> tasksInHistory = inMemoryHistoryManager.getHistory();

        assertEquals(3, tasksInHistory.size());
        assertEquals(firstTask, tasksInHistory.getFirst());
        assertEquals(secondTask, tasksInHistory.get(1));
        assertEquals(fourthTask, tasksInHistory.get(2));

        inMemoryHistoryManager.remove(4);
        tasksInHistory = inMemoryHistoryManager.getHistory();

        assertEquals(2, tasksInHistory.size());
        assertEquals(firstTask, tasksInHistory.getFirst());
        assertEquals(secondTask, tasksInHistory.get(1));

        inMemoryHistoryManager.remove(1);
        tasksInHistory = inMemoryHistoryManager.getHistory();

        assertEquals(1, tasksInHistory.size());
        assertEquals(secondTask, tasksInHistory.getFirst());

        inMemoryHistoryManager.remove(2);
        tasksInHistory = inMemoryHistoryManager.getHistory();

        assertTrue(tasksInHistory.isEmpty());
    }
}