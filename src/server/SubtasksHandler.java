package server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import exception.NotFoundException;
import exception.OverlapException;
import model.Subtask;
import service.TaskManager;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

public class SubtasksHandler extends BaseHttpHandler implements HttpHandler {

    public SubtasksHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        try {
            String method = httpExchange.getRequestMethod();
            String path = httpExchange.getRequestURI().getPath();

            switch (method) {
                case "GET":
                    if (Pattern.matches("^/subtasks$", path)) {
                        sendText(httpExchange, gson.toJson(taskManager.getAllSubtasks()));
                        break;
                    }

                    if (Pattern.matches("^/subtasks/\\d+$", path)) {
                        int id = parseId(path.replace("/subtasks/", ""));
                        sendText(httpExchange, gson.toJson(taskManager.getSubtaskById(id)));
                        break;
                    }

                    sendMethodNotAllowed(httpExchange);
                    break;
                case "POST":
                    if (Pattern.matches("^/subtasks$", path)) {
                        Subtask subtask = gson.fromJson(
                                new String(httpExchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8),
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

                    break;
                case "DELETE":
                    if (Pattern.matches("^/subtasks/\\d+$", path)) {
                        int id = parseId(path.replace("/subtasks/", ""));
                        sendText(httpExchange, gson.toJson(taskManager.removeSubtaskById(id)));
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