package server.handlers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.TaskManager;
import server.LocalDateTimeAdapter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Map;

public abstract class BaseHttpHandler implements HttpHandler {

    protected final TaskManager taskManager;

    public BaseHttpHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    public boolean isNumber(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    protected static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .setPrettyPrinting()
            .create();

    protected void sendText(HttpExchange exchange, String text, int statusCode) throws IOException {
        byte[] response = text.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        exchange.sendResponseHeaders(statusCode, response.length);
        exchange.getResponseBody().write(response);
        exchange.close();
    }

    protected void sendText(HttpExchange exchange, String text) throws IOException {
        sendText(exchange, text, 200);
    }

    protected void sendNotFound(HttpExchange exchange) throws IOException {
        Map<String, String> error = Map.of("error", "Not Found");
        sendText(exchange, GSON.toJson(error), 404);
    }

    protected void sendHasOverlaps(HttpExchange exchange) throws IOException {
        Map<String, String> error = Map.of("error", "Not Acceptable");
        sendText(exchange, GSON.toJson(error), 406);
    }

    protected void sendInternalError(HttpExchange exchange) throws IOException {
        Map<String, String> error = Map.of("error", "Internal Server Error");
        sendText(exchange, GSON.toJson(error), 500);
    }

    protected void sendIncorrectRequest(HttpExchange exchange) throws IOException {
        Map<String, String> error = Map.of("error", "Incorrect Request");
        sendText(exchange, GSON.toJson(error), 400);
    }

}