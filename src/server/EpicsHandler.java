package server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import model.Epic;
import service.TaskManager;

import java.io.IOException;
import java.util.regex.Pattern;

public class EpicsHandler extends BaseHttpHandler {

    public EpicsHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    @Override
    protected void processGetRequest(HttpExchange httpExchange, String path) throws IOException {
        if (Pattern.matches("^/epics$", path)) {
            sendText(httpExchange, gson.toJson(taskManager.getAllEpics()));
            return;
        }

        if (Pattern.matches("^/epics/\\d+$", path)) {
            int id = parseId(path.replace("/epics/", ""));
            sendText(httpExchange, gson.toJson(taskManager.getEpicById(id)));
            return;
        }

        if (Pattern.matches("^/epics/\\d+/subtasks$", path)) {
            int id = parseId(
                    path.replace("/epics/", "")
                            .replace("/subtasks", "")
            );
            sendText(httpExchange, gson.toJson(taskManager.getSubtasksByEpicId(id)));
            return;
        }

        sendMethodNotAllowed(httpExchange);
    }

    @Override
    protected void processPostRequest(HttpExchange httpExchange, String path) throws IOException {
        if (Pattern.matches("^/epics$", path)) {
            Epic epic = gson.fromJson(
                    readJsonFromRequestBody(httpExchange),
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
    }

    @Override
    protected void processDeleteRequest(HttpExchange httpExchange, String path) throws IOException {
        if (Pattern.matches("^/epics/\\d+$", path)) {
            int id = parseId(path.replace("/epics/", ""));
            sendText(httpExchange, gson.toJson(taskManager.removeEpicById(id)));
        } else {
            sendMethodNotAllowed(httpExchange);
        }
    }
}