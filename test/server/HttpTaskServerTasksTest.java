package server;

import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
        String taskJson = """ 
                {
                    "title": "Test Task",
                    "type": "TASK"
                }
                """;

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
        String taskJson = """
                {
                    "title": "Test Task",
                    "type": "TASK"
                }
                """;
        HttpRequest createRequest = HttpRequest.newBuilder()
                .uri(URI.create(urlBase + "/tasks"))
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .header("Content-Type", "application/json")
                .build();
        HttpResponse<String> createResponse = client.send(createRequest, HttpResponse.BodyHandlers.ofString());
        String responseBody = createResponse.body();

        TaskDto createdTask = gson.fromJson(responseBody, TaskDto.class);
        int taskId = createdTask.getId();

        HttpRequest getRequest = HttpRequest.newBuilder()
                .uri(URI.create(urlBase + "/tasks/" + taskId))
                .GET()
                .build();
        HttpResponse<String> getResponse = client.send(getRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, getResponse.statusCode());
    }

    @Test
    void updateTask() throws Exception {
        String createJson = """
                {
                    "title": "Original Task",
                    "type": "TASK"
                }
                """;
        HttpRequest createRequest = HttpRequest.newBuilder()
                .uri(URI.create(urlBase + "/tasks"))
                .POST(HttpRequest.BodyPublishers.ofString(createJson))
                .header("Content-Type", "application/json")
                .build();
        HttpResponse<String> createResponse = client.send(createRequest, HttpResponse.BodyHandlers.ofString());
        TaskDto createdTask = gson.fromJson(createResponse.body(), TaskDto.class);

        String updateJson = """
                {
                    "id": %d,
                    "title": "Updated Task",
                    "type": "TASK",
                    "status": "IN_PROGRESS"
                }
                """.formatted(createdTask.getId());

        HttpRequest updateRequest = HttpRequest.newBuilder()
                .uri(URI.create(urlBase + "/tasks"))
                .POST(HttpRequest.BodyPublishers.ofString(updateJson))
                .header("Content-Type", "application/json")
                .build();
        HttpResponse<String> updateResponse = client.send(updateRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, updateResponse.statusCode());
    }

    @Test
    void deleteTask() throws Exception {
        String taskJson = """
                {
                    "title": "Task to delete",
                    "type": "TASK"
                }
                """;
        HttpRequest createRequest = HttpRequest.newBuilder()
                .uri(URI.create(urlBase + "/tasks"))
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .header("Content-Type", "application/json")
                .build();
        HttpResponse<String> createResponse = client.send(createRequest, HttpResponse.BodyHandlers.ofString());
        TaskDto createdTask = gson.fromJson(createResponse.body(), TaskDto.class);

        HttpRequest deleteRequest = HttpRequest.newBuilder()
                .uri(URI.create(urlBase + "/tasks/" + createdTask.getId()))
                .DELETE()
                .build();
        HttpResponse<String> deleteResponse = client.send(deleteRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, deleteResponse.statusCode());

        HttpRequest getRequest = HttpRequest.newBuilder()
                .uri(URI.create(urlBase + "/tasks/" + createdTask.getId()))
                .GET()
                .build();
        HttpResponse<String> getResponse = client.send(getRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, getResponse.statusCode());
    }

    @Test
    void getTaskNotFound() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(urlBase + "/tasks/999"))
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
        String taskJson = """
                {
                    "type": "TASK"
                }
                """;
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
        String taskJson = """
                {
                    "title": "Test Task",
                    "type": "EPIC"
                }
                """;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(urlBase + "/tasks"))
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .header("Content-Type", "application/json")
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(400, response.statusCode());
    }
}
