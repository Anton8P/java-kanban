package server;

import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HttpTaskServerSubtasksTest extends HttpTaskServerBaseTest {
    @Test
    void createSubtask() throws Exception {
        String epicJson = """
                {
                    "title": "Test Epic",
                    "type": "EPIC"
                }
                """;
        HttpRequest epicRequest = HttpRequest.newBuilder()
                .uri(URI.create(urlBase + "/epics"))
                .POST(HttpRequest.BodyPublishers.ofString(epicJson))
                .header("Content-Type", "application/json")
                .build();
        HttpResponse<String> epicResponse = client.send(epicRequest, HttpResponse.BodyHandlers.ofString());
        TaskDto createdEpic = gson.fromJson(epicResponse.body(), TaskDto.class);

        String subtaskJson = """
                {
                    "title": "Test Subtask",
                    "type": "SUBTASK",
                    "epicId": %d
                }
                """.formatted(createdEpic.getId());

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(urlBase + "/subtasks"))
                .POST(HttpRequest.BodyPublishers.ofString(subtaskJson))
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
        assertEquals(1, manager.getAllSubtasks().size());
    }

    @Test
    void getAllSubtasks() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(urlBase + "/subtasks"))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertEquals("[]", response.body().trim());
    }

    @Test
    void createSubtaskWithoutEpicId() throws Exception {
        String subtaskJson = """
                {
                    "title": "Test Subtask",
                    "type": "SUBTASK"
                }
                """;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(urlBase + "/subtasks"))
                .POST(HttpRequest.BodyPublishers.ofString(subtaskJson))
                .header("Content-Type", "application/json")
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(400, response.statusCode());
    }

    @Test
    void getSubtaskById() throws Exception {
        String epicJson = """
                {"title": "Epic", "type": "EPIC"}""";
        HttpRequest epicRequest = HttpRequest.newBuilder()
                .uri(URI.create(urlBase + "/epics"))
                .POST(HttpRequest.BodyPublishers.ofString(epicJson))
                .header("Content-Type", "application/json")
                .build();
        HttpResponse<String> epicResponse = client.send(epicRequest, HttpResponse.BodyHandlers.ofString());
        TaskDto epic = gson.fromJson(epicResponse.body(), TaskDto.class);

        String subtaskJson = """
                {
                    "title": "Test Subtask",
                    "type": "SUBTASK",
                    "epicId": %d
                }
                """.formatted(epic.getId());
        HttpRequest createRequest = HttpRequest.newBuilder()
                .uri(URI.create(urlBase + "/subtasks"))
                .POST(HttpRequest.BodyPublishers.ofString(subtaskJson))
                .header("Content-Type", "application/json")
                .build();
        HttpResponse<String> createResponse = client.send(createRequest, HttpResponse.BodyHandlers.ofString());
        TaskDto subtask = gson.fromJson(createResponse.body(), TaskDto.class);

        HttpRequest getRequest = HttpRequest.newBuilder()
                .uri(URI.create(urlBase + "/subtasks/" + subtask.getId()))
                .GET()
                .build();
        HttpResponse<String> getResponse = client.send(getRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, getResponse.statusCode());
    }
}
