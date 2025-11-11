package server.handlers;

import com.sun.net.httpserver.HttpExchange;
import manager.TaskManager;
import server.TaskDto;
import server.TaskDtoConverter;
import tasks.Task;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class PrioritizedHandler extends BaseHttpHandler {

    public PrioritizedHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            if ("GET".equals(exchange.getRequestMethod())) {
                handleGet(exchange);
            } else {
                sendNotFound(exchange);
            }
        } catch (Exception e) {
            sendInternalError(exchange);
        }
    }

    public void handleGet(HttpExchange exchange) throws IOException {
        List<Task> prioritized = taskManager.getPrioritizedTasks();
        List<TaskDto> prioritizedDto = prioritized.stream()
                .map(TaskDtoConverter::toDto)
                .collect(Collectors.toList());
        sendText(exchange, GSON.toJson(prioritizedDto));
    }
}