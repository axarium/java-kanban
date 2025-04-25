package server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import exception.NotFoundException;
import model.Epic;
import service.TaskManager;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

public class EpicsHandler extends BaseHttpHandler implements HttpHandler {

    public EpicsHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        try {
            String method = httpExchange.getRequestMethod();
            String path = httpExchange.getRequestURI().getPath();

            switch (method) {
                case "GET":
                    if (Pattern.matches("^/epics$", path)) {
                        sendText(httpExchange, gson.toJson(taskManager.getAllEpics()));
                        break;
                    }

                    if (Pattern.matches("^/epics/\\d+$", path)) {
                        int id = parseId(path.replace("/epics/", ""));
                        sendText(httpExchange, gson.toJson(taskManager.getEpicById(id)));
                        break;
                    }

                    if (Pattern.matches("^/epics/\\d+/subtasks$", path)) {
                        int id = parseId(
                                path.replace("/epics/", "")
                                .replace("/subtasks", "")
                        );
                        sendText(httpExchange, gson.toJson(taskManager.getSubtasksByEpicId(id)));
                        break;
                    }

                    sendMethodNotAllowed(httpExchange);
                    break;
                case "POST":
                    if (Pattern.matches("^/epics$", path)) {
                        Epic epic = gson.fromJson(
                                new String(httpExchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8),
                                Epic.class
                        );

                        if (epic.getId() == 0) {
                            taskManager.createEpic(epic);
                            sendResourceCreated(httpExchange);
                        } else {
                            sendText(httpExchange, gson.toJson(taskManager.updateEpic(epic)));
                        }
                    } else {
                        sendMethodNotAllowed(httpExchange);
                    }

                    break;
                case "DELETE":
                    if (Pattern.matches("^/epics/\\d+$", path)) {
                        int id = parseId(path.replace("/epics/", ""));
                        sendText(httpExchange, gson.toJson(taskManager.removeEpicById(id)));
                    } else {
                        sendMethodNotAllowed(httpExchange);
                    }

                    break;
                default:
                    sendMethodNotAllowed(httpExchange);
            }
        } catch (NotFoundException exception) {
            sendNotFound(httpExchange);
        } catch (NumberFormatException exception) {
            sendMethodNotAllowed(httpExchange);
        } catch (IOException exception) {
            sendInternalServerError(httpExchange);
        }
    }
}