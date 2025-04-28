package server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import service.TaskManager;

import java.io.IOException;
import java.util.regex.Pattern;

public class PrioritizedHandler extends BaseHttpHandler {

    public PrioritizedHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    @Override
    protected void processGetRequest(HttpExchange httpExchange, String path) throws IOException {
        if (Pattern.matches("^/prioritized$", path)) {
            sendText(httpExchange, gson.toJson(taskManager.getPrioritizedTasks()));
        } else {
            sendMethodNotAllowed(httpExchange);
        }
    }

    @Override
    protected void processPostRequest(HttpExchange httpExchange, String path) throws IOException {
        sendMethodNotAllowed(httpExchange);
    }

    @Override
    protected void processDeleteRequest(HttpExchange httpExchange, String path) throws IOException {
        sendMethodNotAllowed(httpExchange);
    }
}