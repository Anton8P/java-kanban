package server;

import manager.NotFoundException;
import org.junit.jupiter.api.Test;
import tasks.Task;
import tasks.TaskStatus;
import tasks.TaskType;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class HttpTaskServerTasksTest extends HttpTaskServerBaseTest {
    @Test
    void getTasksEmpty() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(urlBase + "/tasks"))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertEquals("[]", response.body().trim());
    }

    @Test
    void createTask() throws Exception {
        TaskDto taskDto = new TaskDto();
        taskDto.setTitle("Test Task");
        taskDto.setType(TaskType.TASK);
        String taskJson = gson.toJson(taskDto);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(urlBase + "/tasks"))
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .header("Content-Type", "application/json")
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
        assertEquals(1, manager.getAllTasks().size());
    }

    @Test
    void getTaskById() throws Exception {
        Task task = new Task("Test Task", "Description");
        int taskId = manager.createTask(task);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(urlBase + "/tasks/" + taskId))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        TaskDto responseTask = gson.fromJson(response.body(), TaskDto.class);
        assertEquals("Test Task", responseTask.getTitle());
        assertEquals(TaskType.TASK, responseTask.getType());
    }

    @Test
    void updateTask() throws Exception {
        Task task = new Task("Original Task", "Description");
        int taskId = manager.createTask(task);
        TaskDto taskUpdateDto = new TaskDto();
        taskUpdateDto.setId(taskId);
        taskUpdateDto.setTitle("Updated Task");
        taskUpdateDto.setType(TaskType.TASK);
        taskUpdateDto.setStatus(TaskStatus.IN_PROGRESS);
        String updateJson = gson.toJson(taskUpdateDto);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(urlBase + "/tasks"))
                .POST(HttpRequest.BodyPublishers.ofString(updateJson))
                .header("Content-Type", "application/json")
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
        Task updatedTask = manager.getTaskById(taskId);
        assertEquals("Updated Task", updatedTask.getTitle());
        assertEquals(TaskStatus.IN_PROGRESS, updatedTask.getStatus());
    }

    @Test
    void deleteTask() throws Exception {
        Task task = new Task("Task to delete", "Description");
        int taskId = manager.createTask(task);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(urlBase + "/tasks/" + taskId))
                .DELETE()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertThrows(NotFoundException.class, () -> manager.getTaskById(taskId));
    }

    @Test
    void getTaskNotFound() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(urlBase + "/tasks/555"))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
    }

    @Test
    void createTaskInvalidJson() throws Exception {
        String invalidJson = "{ invalid json }";
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(urlBase + "/tasks"))
                .POST(HttpRequest.BodyPublishers.ofString(invalidJson))
                .header("Content-Type", "application/json")
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(400, response.statusCode());
    }

    @Test
    void createTaskWithoutTitle() throws Exception {
        TaskDto taskDto = new TaskDto();
        taskDto.setType(TaskType.TASK);
        String taskJson = gson.toJson(taskDto);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(urlBase + "/tasks"))
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .header("Content-Type", "application/json")
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(400, response.statusCode());
    }

    @Test
    void createTaskWithWrongType() throws Exception {
        TaskDto taskDto = new TaskDto();
        taskDto.setTitle("Test Task");
        taskDto.setType(TaskType.EPIC);
        String taskJson = gson.toJson(taskDto);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(urlBase + "/tasks"))
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .header("Content-Type", "application/json")
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(400, response.statusCode());
    }

    @Test
    void createTaskWithTimeOverlap() throws Exception {
        Task task1 = new Task("Task 1", "Description", LocalDateTime.now(), 60);
        manager.createTask(task1);
        TaskDto taskDto = new TaskDto();
        taskDto.setTitle("Task 2");
        taskDto.setType(TaskType.TASK);
        taskDto.setStartTime(LocalDateTime.now().plusMinutes(30));
        taskDto.setDuration(60L);
        String taskJson = gson.toJson(taskDto);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(urlBase + "/tasks"))
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .header("Content-Type", "application/json")
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(406, response.statusCode());
    }
}
