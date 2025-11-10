package server;

import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HttpTaskServerEpicsTest extends HttpTaskServerBaseTest {
    @Test
    void createEpic() throws Exception {
        String epicJson = """
                {
                    "title": "Test Epic",
                    "type": "EPIC"
                }
                """;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(urlBase + "/epics"))
                .POST(HttpRequest.BodyPublishers.ofString(epicJson))
                .header("Content-Type", "application/json")
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
        assertEquals(1, manager.getAllEpics().size());
    }

    @Test
    void getEpicSubtasks() throws Exception {
        String epicJson = """
                {"title": "Epic", "type": "EPIC"}""";
        HttpRequest epicRequest = HttpRequest.newBuilder()
                .uri(URI.create(urlBase + "/epics"))
                .POST(HttpRequest.BodyPublishers.ofString(epicJson))
                .header("Content-Type", "application/json")
                .build();
        HttpResponse<String> epicResponse = client.send(epicRequest, HttpResponse.BodyHandlers.ofString());
        TaskDto epic = gson.fromJson(epicResponse.body(), TaskDto.class);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(urlBase + "/epics/" + epic.getId() + "/subtasks"))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertEquals("[]", response.body().trim());
    }

    @Test
    void getEpicNotFound() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(urlBase + "/epics/999"))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
    }
}

