package server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import service.TaskManager;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public abstract class BaseHttpHandler {
    protected final TaskManager taskManager;
    protected final Gson gson;

    public BaseHttpHandler(TaskManager taskManager, Gson gson) {
        this.taskManager = taskManager;
        this.gson = gson;
    }

    protected void sendText(HttpExchange httpExchange, String response) throws IOException {
        try (httpExchange) {
            httpExchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
            httpExchange.sendResponseHeaders(200, response.getBytes(StandardCharsets.UTF_8).length);

            try (OutputStream os = httpExchange.getResponseBody()) {
                os.write(response.getBytes(StandardCharsets.UTF_8));
            }
        }
    }

    protected void sendResourceCreated(HttpExchange httpExchange) throws IOException {
        try (httpExchange) {
            httpExchange.sendResponseHeaders(201, -1);
        }
    }

    protected void sendMethodNotAllowed(HttpExchange httpExchange) throws IOException {
        try (httpExchange) {
            httpExchange.sendResponseHeaders(405, -1);
        }
    }

    protected void sendInternalServerError(HttpExchange httpExchange) throws IOException {
        try (httpExchange) {
            httpExchange.sendResponseHeaders(500, -1);
        }
    }

    protected void sendNotFound(HttpExchange httpExchange) throws IOException {
        try (httpExchange) {
            httpExchange.sendResponseHeaders(404, -1);
        }
    }

    protected void sendOverlap(HttpExchange httpExchange) throws IOException {
        try (httpExchange) {
            httpExchange.sendResponseHeaders(406, -1);
        }
    }

    protected String readJsonFromRequestBody(HttpExchange httpExchange) throws IOException {
        return new String(httpExchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
    }

    protected int parseId(String id) {
        return Integer.parseInt(id);
    }
}