package server;

import manager.NotFoundException;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.TaskType;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class HttpTaskServerEpicsTest extends HttpTaskServerBaseTest {
    @Test
    void createEpic() throws Exception {
        TaskDto epicDto = new TaskDto();
        epicDto.setTitle("Test Epic");
        epicDto.setType(TaskType.EPIC);
        String epicJson = gson.toJson(epicDto);
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
    void updateEpic() throws Exception {
        Epic epic = new Epic("Original Epic", "Description");
        int epicId = manager.createEpic(epic);
        TaskDto epicUpdateDto = new TaskDto();
        epicUpdateDto.setId(epicId);
        epicUpdateDto.setTitle("Updated Epic");
        epicUpdateDto.setType(TaskType.EPIC);
        epicUpdateDto.setDescription("Updated Description");
        String updateJson = gson.toJson(epicUpdateDto);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(urlBase + "/epics"))
                .POST(HttpRequest.BodyPublishers.ofString(updateJson))
                .header("Content-Type", "application/json")
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
        Epic updatedEpic = manager.getEpicById(epicId);
        assertEquals("Updated Epic", updatedEpic.getTitle());
        assertEquals("Updated Description", updatedEpic.getDescription());
    }

    @Test
    void getAllEpics() throws Exception {
        Epic epic1 = new Epic("Epic 1", "Description 1");
        Epic epic2 = new Epic("Epic 2", "Description 2");
        manager.createEpic(epic1);
        manager.createEpic(epic2);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(urlBase + "/epics"))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        TaskDto[] epics = gson.fromJson(response.body(), TaskDto[].class);
        assertEquals(2, epics.length);
    }

    @Test
    void getEpicById() throws Exception {
        Epic epic = new Epic("Test Epic", "Description");
        int epicId = manager.createEpic(epic);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(urlBase + "/epics/" + epicId))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        TaskDto responseEpic = gson.fromJson(response.body(), TaskDto.class);
        assertEquals("Test Epic", responseEpic.getTitle());
        assertEquals(TaskType.EPIC, responseEpic.getType());
    }

    @Test
    void getEpicSubtasks() throws Exception {
        Epic epic = new Epic("Test Epic", "Description");
        int epicId = manager.createEpic(epic);
        Subtask subtask1 = new Subtask("Subtask 1", "Description 1", epicId);
        Subtask subtask2 = new Subtask("Subtask 2", "Description 2", epicId);
        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(urlBase + "/epics/" + epicId + "/subtasks"))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        TaskDto[] subtasks = gson.fromJson(response.body(), TaskDto[].class);
        assertEquals(2, subtasks.length);
    }

    @Test
    void getEpicSubtasksEmpty() throws Exception {
        Epic epic = new Epic("Test Epic", "Description");
        int epicId = manager.createEpic(epic);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(urlBase + "/epics/" + epicId + "/subtasks"))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertEquals("[]", response.body().trim());
    }

    @Test
    void getEpicNotFound() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(urlBase + "/epics/555"))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
    }

    @Test
    void getEpicSubtasksNotFound() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(urlBase + "/epics/555/subtasks"))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
    }

    @Test
    void deleteEpic() throws Exception {
        Epic epic = new Epic("Epic to delete", "Description");
        int epicId = manager.createEpic(epic);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(urlBase + "/epics/" + epicId))
                .DELETE()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertThrows(NotFoundException.class, () -> manager.getEpicById(epicId));
    }

    @Test
    void createEpicWithoutTitle() throws Exception {
        TaskDto epicDto = new TaskDto();
        epicDto.setType(TaskType.EPIC);
        String epicJson = gson.toJson(epicDto);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(urlBase + "/epics"))
                .POST(HttpRequest.BodyPublishers.ofString(epicJson))
                .header("Content-Type", "application/json")
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(400, response.statusCode());
    }

    @Test
    void createEpicWithWrongType() throws Exception {
        TaskDto epicDto = new TaskDto();
        epicDto.setTitle("Test Epic");
        epicDto.setType(TaskType.TASK);
        String epicJson = gson.toJson(epicDto);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(urlBase + "/epics"))
                .POST(HttpRequest.BodyPublishers.ofString(epicJson))
                .header("Content-Type", "application/json")
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(400, response.statusCode());
    }
}