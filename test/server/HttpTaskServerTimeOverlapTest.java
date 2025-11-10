package server;

import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HttpTaskServerTimeOverlapTest extends HttpTaskServerBaseTest {
    @Test
    void createTaskWithTimeOverlap() throws Exception {
        String task1Json = """
                {
                    "title": "Task 1",
                    "type": "TASK",
                    "startTime": "2024-01-15T10:00:00",
                    "duration": 60
                }
                """;
        HttpRequest request1 = HttpRequest.newBuilder()
                .uri(URI.create(urlBase + "/tasks"))
                .POST(HttpRequest.BodyPublishers.ofString(task1Json))
                .header("Content-Type", "application/json")
                .build();
        HttpResponse<String> response1 = client.send(request1, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response1.statusCode());

        String task2Json = """
                {
                    "title": "Task 2",
                    "type": "TASK",
                    "startTime": "2024-01-15T10:30:00",
                    "duration": 60
                }
                """;
        HttpRequest request2 = HttpRequest.newBuilder()
                .uri(URI.create(urlBase + "/tasks"))
                .POST(HttpRequest.BodyPublishers.ofString(task2Json))
                .header("Content-Type", "application/json")
                .build();
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
        assertEquals(406, response2.statusCode());
    }
}
