package server;

import manager.NotFoundException;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.TaskStatus;
import tasks.TaskType;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class HttpTaskServerSubtasksTest extends HttpTaskServerBaseTest {
    @Test
    void createSubtask() throws Exception {
        Epic epic = new Epic("Test Epic", "Description");
        int epicId = manager.createEpic(epic);
        TaskDto subtaskDto = new TaskDto();
        subtaskDto.setTitle("Test Subtask");
        subtaskDto.setType(TaskType.SUBTASK);
        subtaskDto.setEpicId(epicId);
        String subtaskJson = gson.toJson(subtaskDto);
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
    void updateSubtask() throws Exception {
        Epic epic = new Epic("Test Epic", "Description");
        int epicId = manager.createEpic(epic);
        Subtask subtask = new Subtask("Original Subtask", "Description", epicId);
        int subtaskId = manager.createSubtask(subtask);
        TaskDto subtaskUpdateDto = new TaskDto();
        subtaskUpdateDto.setId(subtaskId);
        subtaskUpdateDto.setTitle("Updated Subtask");
        subtaskUpdateDto.setType(TaskType.SUBTASK);
        subtaskUpdateDto.setEpicId(epicId);
        subtaskUpdateDto.setStatus(TaskStatus.IN_PROGRESS);
        String updateJson = gson.toJson(subtaskUpdateDto);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(urlBase + "/subtasks"))
                .POST(HttpRequest.BodyPublishers.ofString(updateJson))
                .header("Content-Type", "application/json")
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
        Subtask updatedSubtask = manager.getSubtaskById(subtaskId);
        assertEquals("Updated Subtask", updatedSubtask.getTitle());
        assertEquals(TaskStatus.IN_PROGRESS, updatedSubtask.getStatus());
    }

    @Test
    void getAllSubtasks() throws Exception {
        Epic epic = new Epic("Test Epic", "Description");
        int epicId = manager.createEpic(epic);
        Subtask subtask1 = new Subtask("Subtask 1", "Description 1", epicId);
        Subtask subtask2 = new Subtask("Subtask 2", "Description 2", epicId);
        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(urlBase + "/subtasks"))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        TaskDto[] subtasks = gson.fromJson(response.body(), TaskDto[].class);
        assertEquals(2, subtasks.length);
    }

    @Test
    void getAllSubtasksEmpty() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(urlBase + "/subtasks"))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertEquals("[]", response.body().trim());
    }

    @Test
    void getSubtaskById() throws Exception {
        Epic epic = new Epic("Test Epic", "Description");
        int epicId = manager.createEpic(epic);
        Subtask subtask = new Subtask("Test Subtask", "Description", epicId);
        int subtaskId = manager.createSubtask(subtask);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(urlBase + "/subtasks/" + subtaskId))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        TaskDto responseSubtask = gson.fromJson(response.body(), TaskDto.class);
        assertEquals("Test Subtask", responseSubtask.getTitle());
        assertEquals(TaskType.SUBTASK, responseSubtask.getType());
        assertEquals(epicId, responseSubtask.getEpicId());
    }

    @Test
    void getSubtaskNotFound() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(urlBase + "/subtasks/555"))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
    }

    @Test
    void deleteSubtask() throws Exception {
        Epic epic = new Epic("Test Epic", "Description");
        int epicId = manager.createEpic(epic);
        Subtask subtask = new Subtask("Subtask to delete", "Description", epicId);
        int subtaskId = manager.createSubtask(subtask);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(urlBase + "/subtasks/" + subtaskId))
                .DELETE()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertThrows(NotFoundException.class, () -> manager.getSubtaskById(subtaskId));
    }

    @Test
    void createSubtaskWithoutEpicId() throws Exception {
        TaskDto subtaskDto = new TaskDto();
        subtaskDto.setTitle("Test Subtask");
        subtaskDto.setType(TaskType.SUBTASK);
        String subtaskJson = gson.toJson(subtaskDto);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(urlBase + "/subtasks"))
                .POST(HttpRequest.BodyPublishers.ofString(subtaskJson))
                .header("Content-Type", "application/json")
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(400, response.statusCode());
    }

    @Test
    void createSubtaskWithoutTitle() throws Exception {
        Epic epic = new Epic("Test Epic", "Description");
        int epicId = manager.createEpic(epic);
        TaskDto subtaskDto = new TaskDto();
        subtaskDto.setType(TaskType.SUBTASK);
        subtaskDto.setEpicId(epicId);
        String subtaskJson = gson.toJson(subtaskDto);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(urlBase + "/subtasks"))
                .POST(HttpRequest.BodyPublishers.ofString(subtaskJson))
                .header("Content-Type", "application/json")
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(400, response.statusCode());
    }

    @Test
    void createSubtaskWithWrongType() throws Exception {
        Epic epic = new Epic("Test Epic", "Description");
        int epicId = manager.createEpic(epic);
        TaskDto subtaskDto = new TaskDto();
        subtaskDto.setTitle("Test Subtask");
        subtaskDto.setType(TaskType.TASK);
        subtaskDto.setEpicId(epicId);
        String subtaskJson = gson.toJson(subtaskDto);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(urlBase + "/subtasks"))
                .POST(HttpRequest.BodyPublishers.ofString(subtaskJson))
                .header("Content-Type", "application/json")
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(400, response.statusCode());
    }

    @Test
    void createSubtaskWithTimeOverlap() throws Exception {
        Epic epic = new Epic("Test Epic", "Description");
        int epicId = manager.createEpic(epic);
        Subtask subtask1 = new Subtask("Subtask 1", "Description", epicId,
                LocalDateTime.now(), 60);
        manager.createSubtask(subtask1);
        TaskDto subtaskDto = new TaskDto();
        subtaskDto.setTitle("Subtask 2");
        subtaskDto.setType(TaskType.SUBTASK);
        subtaskDto.setEpicId(epicId);
        subtaskDto.setStartTime(LocalDateTime.now().plusMinutes(30));
        subtaskDto.setDuration(60L);
        String subtaskJson = gson.toJson(subtaskDto);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(urlBase + "/subtasks"))
                .POST(HttpRequest.BodyPublishers.ofString(subtaskJson))
                .header("Content-Type", "application/json")
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(406, response.statusCode());
    }
}