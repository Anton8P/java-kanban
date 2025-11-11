package server.handlers;

import com.sun.net.httpserver.HttpExchange;
import manager.TaskManager;
import server.TaskDto;
import server.TaskDtoConverter;
import tasks.Task;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class HistoryHandler extends BaseHttpHandler {

    public HistoryHandler(TaskManager taskManager) {
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

    private void handleGet(HttpExchange exchange) throws IOException {
        List<Task> history = taskManager.getHistory();
        List<TaskDto> historyDto = history.stream()
                .map(TaskDtoConverter::toDto)
                .collect(Collectors.toList());
        sendText(exchange, GSON.toJson(historyDto));
    }
}