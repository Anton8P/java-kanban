package server.handlers;

import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.NotFoundException;
import manager.TaskManager;
import manager.TimeOverlapException;
import server.TaskDtoConverter;
import server.TaskDto;
import tasks.Task;
import tasks.TaskType;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;


public class TasksHandler extends BaseHttpHandler implements HttpHandler {

    private final TaskManager taskManager;

    public TasksHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
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
            if (path.equals("/tasks")) {
                List<Task> taskList = taskManager.getAllTasks();
                List<TaskDto> tasksDto = taskList.stream()
                        .map(TaskDtoConverter::toDto)
                        .collect(Collectors.toList());
                sendText(exchange, GSON.toJson(tasksDto));
            } else if (path.startsWith("/tasks/")) {
                String strId = path.substring("/tasks/".length());
                if (isNumber(strId)) {
                    int id = Integer.parseInt(strId);
                    try {
                        Task task = taskManager.getTaskById(id);
                        TaskDto taskDto = TaskDtoConverter.toDto(task);
                        sendText(exchange, GSON.toJson(taskDto));
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
            if (path.equals("/tasks")) {
                String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                try {
                    TaskDto taskDto = GSON.fromJson(body, TaskDto.class);

                    if (taskDto.getTitle() == null || taskDto.getTitle().isBlank()) {
                        sendIncorrectRequest(exchange);
                        return;
                    }

                    if (taskDto.getType() == null) {
                        taskDto.setType(TaskType.TASK);
                    }

                    if (taskDto.getType() != TaskType.TASK) {
                        sendIncorrectRequest(exchange);
                        return;
                    }

                    Task task = TaskDtoConverter.toEntity(taskDto);

                    if (taskDto.getId() == null || taskDto.getId() == 0) {
                        int id = taskManager.createTask(task);
                        taskDto.setId(id);
                        sendText(exchange, GSON.toJson(taskDto), 201);

                    } else {
                        taskManager.updateTask(task);
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
            if (path.startsWith("/tasks/")) {
                String strId = path.substring("/tasks/".length());
                if (isNumber(strId)) {
                    int id = Integer.parseInt(strId);
                    taskManager.deleteTaskById(id);
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


