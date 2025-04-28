package server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import exception.NotFoundException;
import exception.OverlapException;
import service.TaskManager;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public abstract class BaseHttpHandler implements HttpHandler {
    protected final TaskManager taskManager;
    protected final Gson gson;

    public BaseHttpHandler(TaskManager taskManager, Gson gson) {
        this.taskManager = taskManager;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        try {
            String method = httpExchange.getRequestMethod();
            String path = httpExchange.getRequestURI().getPath();

            switch (method) {
                case "GET":
                    processGetRequest(httpExchange, path);
                    break;
                case "POST":
                    processPostRequest(httpExchange, path);
                    break;
                case "DELETE":
                    processDeleteRequest(httpExchange, path);
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

    protected abstract void processGetRequest(HttpExchange httpExchange, String path) throws IOException;

    protected abstract void processPostRequest(HttpExchange httpExchange, String path) throws IOException;

    protected abstract void processDeleteRequest(HttpExchange httpExchange, String path) throws IOException;

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