package server;

import org.junit.jupiter.api.Test;
import tasks.TaskType;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HttpTaskServerTimeOverlapTest extends HttpTaskServerBaseTest {
    @Test
    void createTaskWithTimeOverlap() throws Exception {
        TaskDto task1Dto = new TaskDto();
        task1Dto.setTitle("Task 1");
        task1Dto.setType(TaskType.TASK);
        task1Dto.setStartTime(LocalDateTime.of(2024,1,15,10,0,0));
        task1Dto.setDuration(60L);
        String task1Json = gson.toJson(task1Dto);
        HttpRequest request1 = HttpRequest.newBuilder()
                .uri(URI.create(urlBase + "/tasks"))
                .POST(HttpRequest.BodyPublishers.ofString(task1Json))
                .header("Content-Type", "application/json")
                .build();
        HttpResponse<String> response1 = client.send(request1, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response1.statusCode());
        TaskDto task2Dto = new TaskDto();
        task2Dto.setTitle("Task 1");
        task2Dto.setType(TaskType.TASK);
        task2Dto.setStartTime(LocalDateTime.of(2024,1,15,10,30,0));
        task2Dto.setDuration(60L);
        String task2Json = gson.toJson(task2Dto);
        HttpRequest request2 = HttpRequest.newBuilder()
                .uri(URI.create(urlBase + "/tasks"))
                .POST(HttpRequest.BodyPublishers.ofString(task2Json))
                .header("Content-Type", "application/json")
                .build();
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
        assertEquals(406, response2.statusCode());
    }
}
