package server.handlers;

import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import manager.NotFoundException;
import manager.TaskManager;
import server.TaskDto;
import server.TaskDtoConverter;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskType;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

public class EpicsHandler extends BaseHttpHandler {

    public EpicsHandler(TaskManager taskManager) {
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
            if (path.equals("/epics")) {
                List<Epic> epics = taskManager.getAllEpics();
                List<TaskDto> epicsDto = epics.stream()
                        .map(TaskDtoConverter::toDto)
                        .collect(Collectors.toList());
                sendText(exchange, GSON.toJson(epicsDto), 200);
            } else if (path.startsWith("/epics/")) {
                if (path.endsWith("/subtasks")) {
                    String strIdWithSubtasks = path.substring("/epics/".length(), path.length() - "/subtasks".length());
                    if (isNumber(strIdWithSubtasks)) {
                        int idWithSubtasks = Integer.parseInt(strIdWithSubtasks);
                        try {
                            List<Subtask> subtasks = taskManager.getAllSubtasksByEpicId(idWithSubtasks);
                            List<TaskDto> subtasksDto = subtasks.stream()
                                    .map(TaskDtoConverter::toDto)
                                    .collect(Collectors.toList());
                            sendText(exchange, GSON.toJson(subtasksDto), 200);
                        } catch (NotFoundException e) {
                            sendNotFound(exchange);
                        }
                    } else {
                        sendNotFound(exchange);
                    }
                } else {
                    String strId = path.substring("/epics/".length());
                    if (isNumber(strId)) {
                        int id = Integer.parseInt(strId);
                        try {
                            Epic epic = taskManager.getEpicById(id);
                            TaskDto taskDto = TaskDtoConverter.toDto(epic);
                            sendText(exchange, GSON.toJson(taskDto));
                        } catch (NotFoundException e) {
                            sendNotFound(exchange);
                        }
                    } else {
                        sendNotFound(exchange);
                    }
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
            if (path.equals("/epics")) {
                String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);

                try {
                    TaskDto taskDto = GSON.fromJson(body, TaskDto.class);

                    if (taskDto.getTitle() == null || taskDto.getTitle().isBlank()) {
                        sendIncorrectRequest(exchange);
                        return;
                    }

                    if (taskDto.getType() == null) {
                        taskDto.setType(TaskType.EPIC);
                    }

                    if (taskDto.getType() != TaskType.EPIC) {
                        sendIncorrectRequest(exchange);
                        return;
                    }

                    Task task = TaskDtoConverter.toEntity(taskDto);

                    Epic epic = (Epic) task;

                    if (taskDto.getId() == null || taskDto.getId() == 0) {
                        int id = taskManager.createEpic(epic);
                        taskDto.setId(id);
                        sendText(exchange, GSON.toJson(taskDto), 201);
                    } else {
                        taskManager.updateEpic(epic);
                        sendText(exchange, GSON.toJson(taskDto), 201);
                    }
                } catch (JsonSyntaxException e) {
                    sendIncorrectRequest(exchange);
                } catch (NotFoundException e) {
                    sendNotFound(exchange);
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
            if (path.startsWith("/epics/")) {
                String strId = path.substring("/epics/".length());
                if (isNumber(strId)) {
                    int id = Integer.parseInt(strId);
                    taskManager.deleteEpicById(id);
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