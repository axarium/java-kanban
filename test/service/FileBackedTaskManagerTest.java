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
import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTaskManagerTest extends TaskManagerTest {
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
    private FileBackedTaskManager fileBackedTaskManager;
    private Path tempFilePath;

    @BeforeEach
    void createNewFileBackedTaskManager() {
        try {
            tempFilePath = Files.createTempFile("test", ".csv");
            fileBackedTaskManager = new FileBackedTaskManager(tempFilePath);
            taskManager = new FileBackedTaskManager(tempFilePath);
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    @Test
    void saveManagerStateInFile() {
        Task task = new Task(
                "Title",
                "Description",
                TaskStatus.NEW,
                currentDate,
                Duration.ofMinutes(60)
        );
        Epic epic = new Epic("Title", "Description");
        fileBackedTaskManager.createTask(task);
        fileBackedTaskManager.createEpic(epic);
        Subtask subtask = new Subtask(
                "Title",
                "Description",
                TaskStatus.NEW,
                currentDate.plusDays(1),
                Duration.ofMinutes(120),
                epic.getId()
        );
        fileBackedTaskManager.createSubtask(subtask);
        List<String> lines = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(tempFilePath.toString(), UTF_8))) {
            while (br.ready()) {
                lines.add(br.readLine());
            }
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        assertEquals("id,type,title,status,description,startTime,duration,endTime,epicId", lines.getFirst());
        assertEquals(String.format(
                "%d,TASK,Title,NEW,Description,%s,60,%s",
                task.getId(),
                task.getStartTime().format(dateTimeFormatter),
                task.getEndTime().format(dateTimeFormatter)
        ), lines.get(1));
        assertEquals(String.format(
                "%d,EPIC,Title,NEW,Description,%s,120,%s",
                epic.getId(),
                subtask.getStartTime().format(dateTimeFormatter),
                subtask.getEndTime().format(dateTimeFormatter)
        ), lines.get(2));
        assertEquals(String.format(
                "%d,SUBTASK,Title,NEW,Description,%s,120,%s,%d",
                subtask.getId(),
                subtask.getStartTime().format(dateTimeFormatter),
                subtask.getEndTime().format(dateTimeFormatter),
                subtask.getEpicId()
        ), lines.getLast());
    }

    @Test
    void successCreateManagerFromFile() {
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
        Epic epic = new Epic("Title", "Description");
        fileBackedTaskManager.createTask(task);
        fileBackedTaskManager.createTask(taskWithoutStartTime);
        fileBackedTaskManager.createEpic(epic);
        Subtask subtask = new Subtask(
                "Title",
                "Description",
                TaskStatus.DONE,
                currentDate.plusDays(1),
                Duration.ofMinutes(120),
                epic.getId()
        );
        fileBackedTaskManager.createSubtask(subtask);
        Task taskInManager = fileBackedTaskManager.getTaskById(task.getId());
        Task taskWithoutStartTimeInManager = fileBackedTaskManager.getTaskById(taskWithoutStartTime.getId());
        Epic epicInManager = fileBackedTaskManager.getEpicById(epic.getId());
        Subtask subtaskInManager = fileBackedTaskManager.getSubtaskById(subtask.getId());
        FileBackedTaskManager newFileBackedTaskManager = FileBackedTaskManager.loadFromFile(tempFilePath);

        assertNotNull(newFileBackedTaskManager);
        assertNotNull(newFileBackedTaskManager.getAllTasks());
        assertNotNull(newFileBackedTaskManager.getAllEpics());
        assertNotNull(newFileBackedTaskManager.getAllSubtasks());
        assertNotNull(newFileBackedTaskManager.getHistory());
        assertEquals(2, newFileBackedTaskManager.getAllTasks().size());
        assertEquals(1, newFileBackedTaskManager.getAllEpics().size());
        assertEquals(1, newFileBackedTaskManager.getAllSubtasks().size());
        assertEquals(2, newFileBackedTaskManager.getPrioritizedTasks().size());
        assertTrue(newFileBackedTaskManager.getHistory().isEmpty());

        List<Task> allTasks = newFileBackedTaskManager.getAllTasks();
        List<Epic> allEpics = newFileBackedTaskManager.getAllEpics();
        List<Subtask> allSubtasks = newFileBackedTaskManager.getAllSubtasks();

        assertEquals(taskInManager.getId(), allTasks.getFirst().getId());
        assertEquals(taskInManager.getTitle(), allTasks.getFirst().getTitle());
        assertEquals(taskInManager.getDescription(), allTasks.getFirst().getDescription());
        assertEquals(taskInManager.getStartTime().withNano(0), allTasks.getFirst().getStartTime());
        assertEquals(taskInManager.getDuration().toMinutes(), allTasks.getFirst().getDuration().toMinutes());
        assertEquals(taskInManager.getEndTime().withNano(0), allTasks.getFirst().getEndTime());
        assertEquals(taskInManager.getStatus(), allTasks.getFirst().getStatus());

        assertEquals(taskWithoutStartTimeInManager.getId(), allTasks.get(1).getId());
        assertEquals(taskWithoutStartTimeInManager.getTitle(), allTasks.get(1).getTitle());
        assertEquals(taskWithoutStartTimeInManager.getDescription(), allTasks.get(1).getDescription());
        assertNull(allTasks.get(1).getStartTime());
        assertNull(allTasks.get(1).getDuration());
        assertNull(allTasks.get(1).getEndTime());
        assertEquals(taskWithoutStartTimeInManager.getStatus(), allTasks.get(1).getStatus());

        assertEquals(epicInManager.getId(), allEpics.getFirst().getId());
        assertEquals(epicInManager.getTitle(), allEpics.getFirst().getTitle());
        assertEquals(epicInManager.getDescription(), allEpics.getFirst().getDescription());
        assertEquals(epicInManager.getStatus(), allEpics.getFirst().getStatus());
        assertEquals(epicInManager.getStartTime().withNano(0), allEpics.getFirst().getStartTime());
        assertEquals(epicInManager.getDuration().toMinutes(), allEpics.getFirst().getDuration().toMinutes());
        assertEquals(epicInManager.getEndTime().withNano(0), allEpics.getFirst().getEndTime());

        assertEquals(subtaskInManager.getId(), allSubtasks.getFirst().getId());
        assertEquals(subtaskInManager.getTitle(), allSubtasks.getFirst().getTitle());
        assertEquals(subtaskInManager.getDescription(), allSubtasks.getFirst().getDescription());
        assertEquals(subtaskInManager.getStatus(), allSubtasks.getFirst().getStatus());
        assertEquals(subtaskInManager.getStartTime().withNano(0), allSubtasks.getFirst().getStartTime());
        assertEquals(subtaskInManager.getDuration().toMinutes(), allSubtasks.getFirst().getDuration().toMinutes());
        assertEquals(subtaskInManager.getEndTime().withNano(0), allSubtasks.getFirst().getEndTime());
        assertEquals(subtaskInManager.getEpicId(), allSubtasks.getFirst().getEpicId());
    }

    @Test
    void successCreateManagerFromEmptyFile() {
        FileBackedTaskManager newFileBackedTaskManager = FileBackedTaskManager.loadFromFile(tempFilePath);

        assertNotNull(newFileBackedTaskManager);
        assertNotNull(newFileBackedTaskManager.getAllTasks());
        assertNotNull(newFileBackedTaskManager.getAllEpics());
        assertNotNull(newFileBackedTaskManager.getAllSubtasks());
        assertNotNull(newFileBackedTaskManager.getHistory());
        assertNotNull(newFileBackedTaskManager.getPrioritizedTasks());
        assertTrue(newFileBackedTaskManager.getAllTasks().isEmpty());
        assertTrue(newFileBackedTaskManager.getAllEpics().isEmpty());
        assertTrue(newFileBackedTaskManager.getAllSubtasks().isEmpty());
        assertTrue(newFileBackedTaskManager.getHistory().isEmpty());
        assertTrue(newFileBackedTaskManager.getPrioritizedTasks().isEmpty());
    }

    @Test
    void errorCreateManagerFromFileWithIndexOutOfBoundsException() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(tempFilePath.toString(), UTF_8))) {
            bw.write("id,type,title,status,description,startTime,duration,endTime,epicId\n1,TASK,Title,NEW");
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        FileBackedTaskManager newFileBackedTaskManager = FileBackedTaskManager.loadFromFile(tempFilePath);

        assertNotNull(newFileBackedTaskManager);
        assertNotNull(newFileBackedTaskManager.getAllTasks());
        assertNotNull(newFileBackedTaskManager.getAllEpics());
        assertNotNull(newFileBackedTaskManager.getAllSubtasks());
        assertNotNull(newFileBackedTaskManager.getHistory());
        assertNotNull(newFileBackedTaskManager.getPrioritizedTasks());
        assertTrue(newFileBackedTaskManager.getAllTasks().isEmpty());
        assertTrue(newFileBackedTaskManager.getAllEpics().isEmpty());
        assertTrue(newFileBackedTaskManager.getAllSubtasks().isEmpty());
        assertTrue(newFileBackedTaskManager.getHistory().isEmpty());
        assertTrue(newFileBackedTaskManager.getPrioritizedTasks().isEmpty());
    }

    @Test
    void errorCreateManagerFromFileWithIllegalArgumentException() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(tempFilePath.toString(), UTF_8))) {
            bw.write("id,type,title,status,description,startTime,duration,endTime,epicId\n"
                    + "id,TASK,Title,NEW,Description,14.01.2001 11:00:00,60,14.01.2001 12:00:00"
            );
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        FileBackedTaskManager newFileBackedTaskManager = FileBackedTaskManager.loadFromFile(tempFilePath);

        assertNotNull(newFileBackedTaskManager);
        assertNotNull(newFileBackedTaskManager.getAllTasks());
        assertNotNull(newFileBackedTaskManager.getAllEpics());
        assertNotNull(newFileBackedTaskManager.getAllSubtasks());
        assertNotNull(newFileBackedTaskManager.getHistory());
        assertNotNull(newFileBackedTaskManager.getPrioritizedTasks());
        assertTrue(newFileBackedTaskManager.getAllTasks().isEmpty());
        assertTrue(newFileBackedTaskManager.getAllEpics().isEmpty());
        assertTrue(newFileBackedTaskManager.getAllSubtasks().isEmpty());
        assertTrue(newFileBackedTaskManager.getHistory().isEmpty());
        assertTrue(newFileBackedTaskManager.getPrioritizedTasks().isEmpty());
    }
}