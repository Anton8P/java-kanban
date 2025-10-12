package manager;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.util.ArrayList;
import java.util.List;

public interface TaskManager {

    int createTask(Task task);

    int createEpic(Epic epic);

    int createSubtask(Subtask subtask);

    boolean updateTask(Task newTask);

    boolean updateEpic(Epic newEpic);

    boolean updateSubtask(Subtask newSubtask);

    ArrayList<Task> getAllTasks();

    ArrayList<Epic> getAllEpics();

    ArrayList<Subtask> getAllSubtasks();

    Task getTaskById(int id);

    Epic getEpicById(int id);

    Subtask getSubtaskById(int id);

    List<Subtask> getAllSubtasksByEpicId(int epicId);

    List<Task> getHistory();

    void deleteAllTasks();

    void deleteAllEpics();

    void deleteAllSubtasks();

    boolean deleteTaskById(int id);

    boolean deleteEpicById(int id);

    boolean deleteSubtaskById(int id);
}
