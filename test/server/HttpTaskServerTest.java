package server;

import com.google.gson.Gson;
import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.Managers;
import service.TaskManager;
import util.EpicsListTypeToken;
import util.SubtasksListTypeToken;
import util.TasksListTypeToken;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HttpTaskServerTest {
    private final TaskManager taskManager = Managers.getDefault();
    private final HttpTaskServer taskServer = new HttpTaskServer(taskManager);
    private final Gson gson = taskServer.getGson();
    static final LocalDateTime currentDate = LocalDateTime.now();

    public HttpTaskServerTest() throws IOException {

    }

    @BeforeEach
    public void startServer() {
        taskManager.removeAllTasks();
        taskManager.removeAllSubtasks();
        taskManager.removeAllEpics();
        taskServer.start();
    }

    @AfterEach
    public void stopServer() {
        taskServer.stop();
    }

    @Test
    public void createTask() throws IOException, InterruptedException {
        Task task = new Task(
                "Title",
                "Description",
                TaskStatus.NEW,
                currentDate,
                Duration.ofMinutes(60)
        );
        String taskJson = gson.toJson(task);

        try (HttpClient client = HttpClient.newHttpClient()) {
            URI uri = URI.create("http://localhost:8080/tasks");
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(uri)
                    .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            assertEquals(200, response.statusCode());

            List<Task> tasksFromManager = taskManager.getAllTasks();

            assertNotNull(tasksFromManager);
            assertEquals(1, tasksFromManager.size());
        }
    }

    @Test
    public void updateTask() throws IOException, InterruptedException {
        Task task = new Task(
                "Title",
                "Description",
                TaskStatus.NEW,
                currentDate,
                Duration.ofMinutes(60)
        );
        taskManager.createTask(task);

        task.setTitle("NewTitle");
        task.setDescription("NewDescription");
        task.setStatus(TaskStatus.IN_PROGRESS);
        task.setStartTime(currentDate.plusDays(1));
        task.setDuration(Duration.ofMinutes(120));

        String taskJson = gson.toJson(task);

        try (HttpClient client = HttpClient.newHttpClient()) {
            URI uri = URI.create("http://localhost:8080/tasks");
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(uri)
                    .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            assertEquals(200, response.statusCode());

            List<Task> tasksFromManager = taskManager.getAllTasks();
            Task taskFromManager = tasksFromManager.getFirst();

            assertNotNull(tasksFromManager);
            assertEquals(1, tasksFromManager.size());
            assertEquals("NewTitle", taskFromManager.getTitle());
            assertEquals("NewDescription", taskFromManager.getDescription());
            assertEquals(TaskStatus.IN_PROGRESS, taskFromManager.getStatus());
            assertEquals(currentDate.plusDays(1).withNano(0), taskFromManager.getStartTime());
            assertEquals(120, taskFromManager.getDuration().toMinutes());
            assertEquals(
                    currentDate.plusDays(1).withNano(0).plusMinutes(120),
                    taskFromManager.getEndTime()
            );
        }
    }

    @Test
    public void getTask() throws IOException, InterruptedException {
        Task task = new Task(
                "Title",
                "Description",
                TaskStatus.NEW,
                currentDate,
                Duration.ofMinutes(60)
        );
        taskManager.createTask(task);

        try (HttpClient client = HttpClient.newHttpClient()) {
            URI uri = URI.create("http://localhost:8080/tasks/" + task.getId());

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(uri)
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            assertEquals(200, response.statusCode());

            Task taskFromManager = gson.fromJson(response.body(), Task.class);

            assertNotNull(taskFromManager);
            assertEquals("Title", taskFromManager.getTitle());
            assertEquals("Description", taskFromManager.getDescription());
            assertEquals(TaskStatus.NEW, taskFromManager.getStatus());
            assertEquals(currentDate.withNano(0), taskFromManager.getStartTime());
            assertEquals(60, taskFromManager.getDuration().toMinutes());
            assertEquals(currentDate.withNano(0).plusMinutes(60), taskFromManager.getEndTime());
        }
    }

    @Test
    public void getAllTasks() throws IOException, InterruptedException {
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
        taskManager.createTask(firstTask);
        taskManager.createTask(secondTask);

        try (HttpClient client = HttpClient.newHttpClient()) {
            URI uri = URI.create("http://localhost:8080/tasks");

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(uri)
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            assertEquals(200, response.statusCode());

            List<Task> tasksFromManager = gson.fromJson(response.body(), new TasksListTypeToken().getType());

            assertNotNull(tasksFromManager);
            assertEquals(2, tasksFromManager.size());
            assertEquals(firstTask, tasksFromManager.getFirst());
            assertEquals(secondTask, tasksFromManager.get(1));
        }
    }

    @Test
    public void deleteTask() throws IOException, InterruptedException {
        Task task = new Task(
                "Title",
                "Description",
                TaskStatus.NEW,
                currentDate,
                Duration.ofMinutes(60)
        );
        taskManager.createTask(task);

        try (HttpClient client = HttpClient.newHttpClient()) {
            URI uri = URI.create("http://localhost:8080/tasks/" + task.getId());

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(uri)
                    .DELETE()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            assertEquals(200, response.statusCode());

            List<Task> tasksFromManager = taskManager.getAllTasks();

            assertNotNull(tasksFromManager);
            assertTrue(tasksFromManager.isEmpty());
        }
    }

    @Test
    public void createEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Title", "Description");
        String epicJson = gson.toJson(epic);

        try (HttpClient client = HttpClient.newHttpClient()) {
            URI uri = URI.create("http://localhost:8080/epics");
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(uri)
                    .POST(HttpRequest.BodyPublishers.ofString(epicJson))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            assertEquals(201, response.statusCode());

            List<Epic> epicsFromManager = taskManager.getAllEpics();

            assertNotNull(epicsFromManager);
            assertEquals(1, epicsFromManager.size());
        }
    }

    @Test
    public void updateEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Title", "Description");
        taskManager.createEpic(epic);

        epic.setTitle("NewTitle");
        epic.setDescription("NewDescription");

        String epicJson = gson.toJson(epic);

        try (HttpClient client = HttpClient.newHttpClient()) {
            URI uri = URI.create("http://localhost:8080/epics");
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(uri)
                    .POST(HttpRequest.BodyPublishers.ofString(epicJson))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            assertEquals(200, response.statusCode());

            List<Epic> epicsFromManager = taskManager.getAllEpics();
            Epic epicFromManager = epicsFromManager.getFirst();

            assertNotNull(epicsFromManager);
            assertEquals(1, epicsFromManager.size());
            assertEquals("NewTitle", epicFromManager.getTitle());
            assertEquals("NewDescription", epicFromManager.getDescription());
        }
    }

    @Test
    public void getEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Title", "Description");
        taskManager.createEpic(epic);

        try (HttpClient client = HttpClient.newHttpClient()) {
            URI uri = URI.create("http://localhost:8080/epics/" + epic.getId());

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(uri)
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            assertEquals(200, response.statusCode());

            Epic epicFromManager = gson.fromJson(response.body(), Epic.class);

            assertNotNull(epicFromManager);
            assertEquals("Title", epicFromManager.getTitle());
            assertEquals("Description", epicFromManager.getDescription());
            assertEquals(TaskStatus.NEW, epicFromManager.getStatus());
            assertEquals(Duration.ZERO, epicFromManager.getDuration());
            assertNull(epicFromManager.getStartTime());
            assertNull(epicFromManager.getEndTime());
        }
    }

    @Test
    public void getAllEpics() throws IOException, InterruptedException {
        Epic firstEpic = new Epic("Title", "Description");
        Epic secondEpic = new Epic("Title", "Description");
        taskManager.createEpic(firstEpic);
        taskManager.createEpic(secondEpic);

        try (HttpClient client = HttpClient.newHttpClient()) {
            URI uri = URI.create("http://localhost:8080/epics");

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(uri)
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            assertEquals(200, response.statusCode());

            List<Epic> epicsFromManager = gson.fromJson(response.body(), new EpicsListTypeToken().getType());

            assertNotNull(epicsFromManager);
            assertEquals(2, epicsFromManager.size());
            assertEquals(firstEpic, epicsFromManager.getFirst());
            assertEquals(secondEpic, epicsFromManager.get(1));
        }
    }

    @Test
    public void deleteEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Title", "Description");
        taskManager.createEpic(epic);

        try (HttpClient client = HttpClient.newHttpClient()) {
            URI uri = URI.create("http://localhost:8080/epics/" + epic.getId());

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(uri)
                    .DELETE()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            assertEquals(200, response.statusCode());

            List<Epic> epicsFromManager = taskManager.getAllEpics();

            assertNotNull(epicsFromManager);
            assertTrue(epicsFromManager.isEmpty());
        }
    }

    @Test
    public void createSubtask() throws IOException, InterruptedException {
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
        String subtaskJson = gson.toJson(subtask);

        try (HttpClient client = HttpClient.newHttpClient()) {
            URI uri = URI.create("http://localhost:8080/subtasks");
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(uri)
                    .POST(HttpRequest.BodyPublishers.ofString(subtaskJson))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            assertEquals(200, response.statusCode());

            List<Subtask> subtasksFromManager = taskManager.getAllSubtasks();

            assertNotNull(subtasksFromManager);
            assertEquals(1, subtasksFromManager.size());
        }
    }

    @Test
    public void updateSubtask() throws IOException, InterruptedException {
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

        subtask.setTitle("NewTitle");
        subtask.setDescription("NewDescription");
        subtask.setStatus(TaskStatus.IN_PROGRESS);
        subtask.setStartTime(currentDate.plusDays(1));
        subtask.setDuration(Duration.ofMinutes(120));

        String subtaskJson = gson.toJson(subtask);

        try (HttpClient client = HttpClient.newHttpClient()) {
            URI uri = URI.create("http://localhost:8080/subtasks");
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(uri)
                    .POST(HttpRequest.BodyPublishers.ofString(subtaskJson))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            assertEquals(200, response.statusCode());

            List<Subtask> subtasksFromManager = taskManager.getAllSubtasks();
            Subtask subtaskFromManager = subtasksFromManager.getFirst();

            assertNotNull(subtasksFromManager);
            assertEquals(1, subtasksFromManager.size());
            assertEquals("NewTitle", subtaskFromManager.getTitle());
            assertEquals("NewDescription", subtaskFromManager.getDescription());
            assertEquals(TaskStatus.IN_PROGRESS, subtaskFromManager.getStatus());
            assertEquals(currentDate.plusDays(1).withNano(0), subtaskFromManager.getStartTime());
            assertEquals(120, subtaskFromManager.getDuration().toMinutes());
            assertEquals(
                    currentDate.plusDays(1).withNano(0).plusMinutes(120),
                    subtaskFromManager.getEndTime()
            );
        }
    }

    @Test
    public void getSubtask() throws IOException, InterruptedException {
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

        try (HttpClient client = HttpClient.newHttpClient()) {
            URI uri = URI.create("http://localhost:8080/subtasks/" + subtask.getId());

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(uri)
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            assertEquals(200, response.statusCode());

            Subtask subtaskFromManager = gson.fromJson(response.body(), Subtask.class);

            assertNotNull(subtaskFromManager);
            assertEquals("Title", subtaskFromManager.getTitle());
            assertEquals("Description", subtaskFromManager.getDescription());
            assertEquals(TaskStatus.NEW, subtaskFromManager.getStatus());
            assertEquals(currentDate.withNano(0), subtaskFromManager.getStartTime());
            assertEquals(60, subtaskFromManager.getDuration().toMinutes());
            assertEquals(currentDate.withNano(0).plusMinutes(60), subtaskFromManager.getEndTime());
        }
    }

    @Test
    public void getAllSubtasks() throws IOException, InterruptedException {
        Epic epic = new Epic("Title", "Description");
        taskManager.createEpic(epic);

        Subtask firstSubtask = new Subtask(
                "Title",
                "Description",
                TaskStatus.NEW,
                currentDate,
                Duration.ofMinutes(60),
                epic.getId()
        );
        Subtask secondSubtask = new Subtask(
                "Title",
                "Description",
                TaskStatus.NEW,
                currentDate.plusDays(1),
                Duration.ofMinutes(120),
                epic.getId()
        );
        taskManager.createSubtask(firstSubtask);
        taskManager.createSubtask(secondSubtask);

        try (HttpClient client = HttpClient.newHttpClient()) {
            URI uri = URI.create("http://localhost:8080/subtasks");

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(uri)
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            assertEquals(200, response.statusCode());

            List<Subtask> subtasksFromManager = gson.fromJson(response.body(), new SubtasksListTypeToken().getType());

            assertNotNull(subtasksFromManager);
            assertEquals(2, subtasksFromManager.size());
            assertEquals(firstSubtask, subtasksFromManager.getFirst());
            assertEquals(secondSubtask, subtasksFromManager.get(1));
        }
    }

    @Test
    public void deleteSubtask() throws IOException, InterruptedException {
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

        try (HttpClient client = HttpClient.newHttpClient()) {
            URI uri = URI.create("http://localhost:8080/subtasks/" + subtask.getId());

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(uri)
                    .DELETE()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            assertEquals(200, response.statusCode());

            List<Subtask> subtasksFromManager = taskManager.getAllSubtasks();

            assertNotNull(subtasksFromManager);
            assertTrue(subtasksFromManager.isEmpty());
        }
    }

    @Test
    public void getHistory() throws IOException, InterruptedException {
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
        taskManager.createTask(firstTask);
        taskManager.createTask(secondTask);
        taskManager.getTaskById(firstTask.getId());
        taskManager.getTaskById(secondTask.getId());

        try (HttpClient client = HttpClient.newHttpClient()) {
            URI uri = URI.create("http://localhost:8080/history");

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(uri)
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            assertEquals(200, response.statusCode());

            List<Task> tasksFromHistory = gson.fromJson(response.body(), new TasksListTypeToken().getType());

            assertNotNull(tasksFromHistory);
            assertEquals(2, tasksFromHistory.size());
            assertEquals(firstTask, tasksFromHistory.getFirst());
            assertEquals(secondTask, tasksFromHistory.get(1));
        }
    }

    @Test
    public void getPrioritizedTasks() throws IOException, InterruptedException {
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
        taskManager.createTask(firstTask);
        taskManager.createTask(secondTask);

        try (HttpClient client = HttpClient.newHttpClient()) {
            URI uri = URI.create("http://localhost:8080/prioritized");

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(uri)
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            assertEquals(200, response.statusCode());

            List<Task> tasksFromHistory = gson.fromJson(response.body(), new TasksListTypeToken().getType());

            assertNotNull(tasksFromHistory);
            assertEquals(2, tasksFromHistory.size());
            assertEquals(firstTask, tasksFromHistory.getFirst());
            assertEquals(secondTask, tasksFromHistory.get(1));
        }
    }

    @Test
    public void getMethodNotAllowedResponse() throws IOException, InterruptedException {
        try (HttpClient client = HttpClient.newHttpClient()) {
            URI uri = URI.create("http://localhost:8080/tasks");

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(uri)
                    .DELETE()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            assertEquals(405, response.statusCode());
        }
    }

    @Test
    public void getNotFoundResponse() throws IOException, InterruptedException {
        try (HttpClient client = HttpClient.newHttpClient()) {
            URI uri = URI.create("http://localhost:8080/tasks/1");

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(uri)
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            assertEquals(404, response.statusCode());
        }
    }

    @Test
    public void getOverlapResponse() throws IOException, InterruptedException {
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
                currentDate.plusMinutes(30),
                Duration.ofMinutes(120)
        );
        taskManager.createTask(firstTask);

        String taskJson = gson.toJson(secondTask);

        try (HttpClient client = HttpClient.newHttpClient()) {
            URI uri = URI.create("http://localhost:8080/tasks");

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(uri)
                    .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            assertEquals(406, response.statusCode());
        }
    }
}