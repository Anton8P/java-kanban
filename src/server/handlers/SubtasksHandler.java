package server.handlers;

import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import manager.NotFoundException;
import manager.TaskManager;
import manager.TimeOverlapException;
import server.TaskDto;
import server.TaskDtoConverter;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskType;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

public class SubtasksHandler extends BaseHttpHandler {

    public SubtasksHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String path = exchange.getRequestURI().getPath();
            String method = exchange.getRequestMethod();

            switch (method) {
                case "GET":
                    handleGet(exchange, path);
                    break;
                case "POST":
                    handlePost(exchange, path);
                    break;
                case "DELETE":
                    handleDelete(exchange, path);
                    break;
                default:
                    sendNotFound(exchange);
            }

        } catch (Exception e) {
            sendInternalError(exchange);
        }
    }

    private void handleGet(HttpExchange exchange, String path) throws IOException {
        try {
            if (path.equals("/subtasks")) {
                List<Subtask> subtasks = taskManager.getAllSubtasks();
                List<TaskDto> tasksDto = subtasks.stream()
                        .map(TaskDtoConverter::toDto)
                        .collect(Collectors.toList());
                sendText(exchange, GSON.toJson(tasksDto));
            } else if (path.startsWith("/subtasks/")) {
                String strId = path.substring("/subtasks/".length());
                if (isNumber(strId)) {
                    int id = Integer.parseInt(strId);
                    try {
                        Subtask subtask = taskManager.getSubtaskById(id);
                        TaskDto subtaskDto = TaskDtoConverter.toDto(subtask);
                        sendText(exchange, GSON.toJson(subtaskDto));
                    } catch (NotFoundException e) {
                        sendNotFound(exchange);
                    }
                } else {
                    sendNotFound(exchange);
                }
            } else {
                sendNotFound(exchange);
            }
        } catch (Exception e) {
            sendInternalError(exchange);
        }
    }

    private void handlePost(HttpExchange exchange, String path) throws IOException {
        try {
            if (path.equals("/subtasks")) {
                String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);

                try {
                    TaskDto taskDto = GSON.fromJson(body, TaskDto.class);

                    if (taskDto.getTitle() == null || taskDto.getTitle().isBlank()) {
                        sendIncorrectRequest(exchange);
                        return;
                    }

                    if (taskDto.getEpicId() == null) {
                        sendIncorrectRequest(exchange);
                        return;
                    }

                    if (taskDto.getType() == null) {
                        taskDto.setType(TaskType.SUBTASK);
                    }

                    if (taskDto.getType() != TaskType.SUBTASK) {
                        sendIncorrectRequest(exchange);
                        return;
                    }

                    Task task = TaskDtoConverter.toEntity(taskDto);

                    Subtask subtask = (Subtask) task;

                    if (taskDto.getId() == null || taskDto.getId() == 0) {
                        int id = taskManager.createSubtask(subtask);
                        taskDto.setId(id);
                        sendText(exchange, GSON.toJson(taskDto), 201);
                    } else {
                        taskManager.updateSubtask(subtask);
                        sendText(exchange, GSON.toJson(taskDto), 201);
                    }

                } catch (JsonSyntaxException e) {
                    sendIncorrectRequest(exchange);
                } catch (NotFoundException e) {
                    sendNotFound(exchange);
                } catch (TimeOverlapException e) {
                    sendHasOverlaps(exchange);
                }
            } else {
                sendNotFound(exchange);
            }
        } catch (Exception e) {
            sendInternalError(exchange);
        }
    }

    private void handleDelete(HttpExchange exchange, String path) throws IOException {
        try {
            if (path.startsWith("/subtasks/")) {
                String strId = path.substring("/subtasks/".length());
                if (isNumber(strId)) {
                    int id = Integer.parseInt(strId);
                    taskManager.deleteSubtaskById(id);
                    sendText(exchange, "", 200);
                } else {
                    sendNotFound(exchange);
                }
            } else {
                sendNotFound(exchange);
            }
        } catch (Exception e) {
            sendInternalError(exchange);
        }
    }
}