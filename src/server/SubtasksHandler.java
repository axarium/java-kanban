package server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import model.Subtask;
import service.TaskManager;

import java.io.IOException;
import java.util.regex.Pattern;

public class SubtasksHandler extends BaseHttpHandler {

    public SubtasksHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    @Override
    protected void processGetRequest(HttpExchange httpExchange, String path) throws IOException {
        if (Pattern.matches("^/subtasks$", path)) {
            sendText(httpExchange, gson.toJson(taskManager.getAllSubtasks()));
            return;
        }

        if (Pattern.matches("^/subtasks/\\d+$", path)) {
            int id = parseId(path.replace("/subtasks/", ""));
            sendText(httpExchange, gson.toJson(taskManager.getSubtaskById(id)));
            return;
        }

        sendMethodNotAllowed(httpExchange);
    }

    @Override
    protected void processPostRequest(HttpExchange httpExchange, String path) throws IOException {
        if (Pattern.matches("^/subtasks$", path)) {
            Subtask subtask = gson.fromJson(
                    readJsonFromRequestBody(httpExchange),
                    Subtask.class
            );

            if (subtask.getId() == 0) {
                sendText(httpExchange, gson.toJson(taskManager.createSubtask(subtask)));
            } else {
                sendText(httpExchange, gson.toJson(taskManager.updateSubtask(subtask)));
            }
        } else {
            sendMethodNotAllowed(httpExchange);
        }
    }

    @Override
    protected void processDeleteRequest(HttpExchange httpExchange, String path) throws IOException {
        if (Pattern.matches("^/subtasks/\\d+$", path)) {
            int id = parseId(path.replace("/subtasks/", ""));
            sendText(httpExchange, gson.toJson(taskManager.removeSubtaskById(id)));
        } else {
            sendMethodNotAllowed(httpExchange);
        }
    }
}