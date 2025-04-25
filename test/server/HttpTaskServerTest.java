package server;

import com.google.gson.Gson;
import model.Epic;
import model.Task;
import model.TaskStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.Managers;
import service.TaskManager;

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
    public void addTask() throws IOException, InterruptedException {
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
        String taskJson = gson.toJson(task);

        try (HttpClient client = HttpClient.newHttpClient()) {
            URI uri = URI.create("http://localhost:8080/tasks");
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(uri)
                    .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            int taskId = gson.fromJson(response.body(), Task.class).getId();
            task.setId(taskId);
            task.setTitle("NewTitle");
            task.setDescription("NewDescription");
            task.setStatus(TaskStatus.IN_PROGRESS);
            task.setStartTime(currentDate.plusDays(1));
            task.setDuration(Duration.ofMinutes(120));
            taskJson = gson.toJson(task);

            request = HttpRequest.newBuilder()
                    .uri(uri)
                    .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                    .build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());

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
        String taskJson = gson.toJson(task);

        try (HttpClient client = HttpClient.newHttpClient()) {
            URI uri = URI.create("http://localhost:8080/tasks");
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(uri)
                    .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            int taskId = gson.fromJson(response.body(), Task.class).getId();
            uri = URI.create("http://localhost:8080/tasks/" + taskId);

            request = HttpRequest.newBuilder()
                    .uri(uri)
                    .GET()
                    .build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());

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
    public void deleteTask() throws IOException, InterruptedException {
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

            int taskId = gson.fromJson(response.body(), Task.class).getId();
            uri = URI.create("http://localhost:8080/tasks/" + taskId);

            request = HttpRequest.newBuilder()
                    .uri(uri)
                    .DELETE()
                    .build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());

            assertEquals(200, response.statusCode());

            List<Task> tasksFromManager = taskManager.getAllTasks();

            assertNotNull(tasksFromManager);
            assertTrue(tasksFromManager.isEmpty());
        }
    }

    @Test
    public void addEpic() throws IOException, InterruptedException {
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
        String epicJson = gson.toJson(epic);

        try (HttpClient client = HttpClient.newHttpClient()) {
            URI uri = URI.create("http://localhost:8080/epics");
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(uri)
                    .POST(HttpRequest.BodyPublishers.ofString(epicJson))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            int epicId = gson.fromJson(response.body(), Epic.class).getId();
            epic.setId(epicId);
            epic.setTitle("NewTitle");
            epic.setDescription("NewDescription");
            epicJson = gson.toJson(epic);

            request = HttpRequest.newBuilder()
                    .uri(uri)
                    .POST(HttpRequest.BodyPublishers.ofString(epicJson))
                    .build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());

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
        String epicJson = gson.toJson(epic);

        try (HttpClient client = HttpClient.newHttpClient()) {
            URI uri = URI.create("http://localhost:8080/epics");
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(uri)
                    .POST(HttpRequest.BodyPublishers.ofString(epicJson))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            int epicId = gson.fromJson(response.body(), Epic.class).getId();
            uri = URI.create("http://localhost:8080/epics/" + epicId);

            request = HttpRequest.newBuilder()
                    .uri(uri)
                    .GET()
                    .build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());

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
    public void deleteEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Title", "Description");
        String epicJson = gson.toJson(epic);

        try (HttpClient client = HttpClient.newHttpClient()) {
            URI uri = URI.create("http://localhost:8080/epics");
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(uri)
                    .POST(HttpRequest.BodyPublishers.ofString(epicJson))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            int epicId = gson.fromJson(response.body(), Epic.class).getId();
            uri = URI.create("http://localhost:8080/epics/" + epicId);

            request = HttpRequest.newBuilder()
                    .uri(uri)
                    .DELETE()
                    .build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());

            assertEquals(200, response.statusCode());

            List<Epic> epicsFromManager = taskManager.getAllEpics();

            assertNotNull(epicsFromManager);
            assertTrue(epicsFromManager.isEmpty());
        }
    }
}