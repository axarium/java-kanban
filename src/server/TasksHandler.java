package server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import model.Task;
import service.TaskManager;

import java.io.IOException;
import java.util.regex.Pattern;

public class TasksHandler extends BaseHttpHandler {

    public TasksHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    @Override
    protected void processGetRequest(HttpExchange httpExchange, String path) throws IOException {
        if (Pattern.matches("^/tasks$", path)) {
            sendText(httpExchange, gson.toJson(taskManager.getAllTasks()));
            return;
        }

        if (Pattern.matches("^/tasks/\\d+$", path)) {
            int id = parseId(path.replace("/tasks/", ""));
            sendText(httpExchange, gson.toJson(taskManager.getTaskById(id)));
            return;
        }

        sendMethodNotAllowed(httpExchange);
    }

    @Override
    protected void processPostRequest(HttpExchange httpExchange, String path) throws IOException {
        if (Pattern.matches("^/tasks$", path)) {
            Task task = gson.fromJson(
                    readJsonFromRequestBody(httpExchange),
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
    }

    @Override
    protected void processDeleteRequest(HttpExchange httpExchange, String path) throws IOException {
        if (Pattern.matches("^/tasks/\\d+$", path)) {
            int id = parseId(path.replace("/tasks/", ""));
            sendText(httpExchange, gson.toJson(taskManager.removeTaskById(id)));
        } else {
            sendMethodNotAllowed(httpExchange);
        }
    }
}