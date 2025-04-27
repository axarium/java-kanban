package server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import service.TaskManager;

import java.io.IOException;
import java.util.regex.Pattern;

public class HistoryHandler extends BaseHttpHandler {

    public HistoryHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    @Override
    protected void processGetRequest(HttpExchange httpExchange, String path) throws IOException {
        if (Pattern.matches("^/history$", path)) {
            sendText(httpExchange, gson.toJson(taskManager.getHistory()));
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