package server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import service.TaskManager;

import java.io.IOException;
import java.util.regex.Pattern;

public class HistoryHandler extends BaseHttpHandler implements HttpHandler {

    public HistoryHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        try {
            String method = httpExchange.getRequestMethod();
            String path = httpExchange.getRequestURI().getPath();

            if (method.equals("GET")) {
                if (Pattern.matches("^/history$", path)) {
                    sendText(httpExchange, gson.toJson(taskManager.getHistory()));
                } else {
                    sendMethodNotAllowed(httpExchange);
                }
            } else {
                sendMethodNotAllowed(httpExchange);
            }
        } catch (IOException exception) {
            sendInternalServerError(httpExchange);
        }
    }
}