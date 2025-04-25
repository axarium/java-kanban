package server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import exception.NotFoundException;
import exception.OverlapException;
import model.Task;
import service.TaskManager;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

public class TasksHandler extends BaseHttpHandler implements HttpHandler {

    public TasksHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        try {
            String method = httpExchange.getRequestMethod();
            String path = httpExchange.getRequestURI().getPath();

            switch (method) {
                case "GET":
                    if (Pattern.matches("^/tasks$", path)) {
                        sendText(httpExchange, gson.toJson(taskManager.getAllTasks()));
                        break;
                    }

                    if (Pattern.matches("^/tasks/\\d+$", path)) {
                        int id = parseId(path.replace("/tasks/", ""));
                        sendText(httpExchange, gson.toJson(taskManager.getTaskById(id)));
                        break;
                    }

                    sendMethodNotAllowed(httpExchange);
                    break;
                case "POST":
                    if (Pattern.matches("^/tasks$", path)) {
                        Task task = gson.fromJson(
                                new String(httpExchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8),
                                Task.class
                        );

                        if (task.getId() == 0) {
                            sendText(httpExchange, gson.toJson(taskManager.createTask(task)));
                        } else {
                            sendText(httpExchange, gson.toJson(taskManager.updateTask(task)));
                        }
                    } else {
                        sendMethodNotAllowed(httpExchange);
                    }

                    break;
                case "DELETE":
                    if (Pattern.matches("^/tasks/\\d+$", path)) {
                        int id = parseId(path.replace("/tasks/", ""));
                        sendText(httpExchange, gson.toJson(taskManager.removeTaskById(id)));
                    } else {
                        sendMethodNotAllowed(httpExchange);
                    }

                    break;
                default:
                    sendMethodNotAllowed(httpExchange);
            }
        } catch (OverlapException exception) {
            sendOverlap(httpExchange);
        } catch (NotFoundException exception) {
            sendNotFound(httpExchange);
        } catch (NumberFormatException exception) {
            sendMethodNotAllowed(httpExchange);
        } catch (IOException exception) {
            sendInternalServerError(httpExchange);
        }
    }
}