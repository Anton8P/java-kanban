import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private int nextId = 1;

    public int createTask(Task task) {
        if (task == null) {
            return 0;
        }
        int taskId = nextId;
        task.setId(taskId);
        tasks.put(taskId, task);
        nextId++;
        return taskId;
    }

    public Task getTask(int id) {
        return tasks.get(id);
    }

    public boolean updateTask(Task newTask) {
        if (newTask == null) {
            return false;
        }
        int id = newTask.getId();
        if (!tasks.containsKey(id)) {
            return false;
        }
        tasks.put(id, newTask);
        return true;
    }

    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    public void deleteAllTasks() {
        tasks.clear();
    }

    public Task getTaskById(int id) {
        return tasks.get(id);
    }

    public boolean deleteTaskById(int id) {
        if (!tasks.containsKey(id)) {
            return false;
        }
        return tasks.remove(id) != null;
    }

    public int createEpic(Epic epic) {
        if (epic == null) {
            return 0;
        }
        int epicId = nextId;
        epic.setId(epicId);
        epics.put(epicId, epic);
        nextId++;
        return epicId;
    }

    public Epic getEpic(int id) {
        return epics.get(id);
    }

    public boolean updateEpic(Epic newEpic) {
        if (newEpic == null) {
            return false;
        }
        int id = newEpic.getId();
        if (!epics.containsKey(id)) {
            return false;
        }
        Epic oldEpic = epics.get(id);
        newEpic.getSubtasks().addAll(oldEpic.getSubtasks());
        epics.put(id, newEpic);
        return true;
    }

    public ArrayList<Subtask> getAllSubtasksFromEpic(int epicId) {
        ArrayList<Subtask> allSubtask = new ArrayList<>();
        for (Subtask subtask : subtasks.values()) {
            if (subtask.getEpic().getId() == epicId) {
                allSubtask.add(subtask);
            }
        }
        return allSubtask;
    }

    public ArrayList<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    public void deleteAllEpics() {
        for (Epic epic : epics.values()) {
            epic.getSubtasks().clear();
        }
        epics.clear();
        subtasks.clear();
    }

    public Epic getEpicById(int id) {
        return epics.get(id);
    }

    public boolean deleteEpicById(int id) {
        Epic epic = epics.get(id);
        if (epic == null) {
            return false;
        }
        for (Subtask subtask : epic.getSubtasks()) {
            subtasks.remove(subtask.getId());
        }
        epic.getSubtasks().clear();
        return epics.remove(id) != null;
    }

    public int createSubtask(Subtask subtask) {
        if (subtask == null) {
            return 0;
        }
        int subtaskId = nextId;
        subtask.setId(subtaskId);
        subtasks.put(subtaskId, subtask);
        subtask.getEpic().addSubtask(subtask);
        nextId++;
        subtask.getEpic().updateStatus();
        return subtaskId;
    }

    public Subtask getSubtask(int id) {
        return subtasks.get(id);
    }

    public boolean updateSubtask(Subtask newSubtask) {
        if (newSubtask == null) {
            return false;
        }
        int id = newSubtask.getId();
        if (!subtasks.containsKey(id)) {
            return false;
        }
        Subtask oldSubtask = subtasks.get(id);
        if (!oldSubtask.getEpic().equals(newSubtask.getEpic())) {
            return false;
        }
        subtasks.put(id, newSubtask);
        newSubtask.getEpic().updateStatus();
        return true;
    }

    public ArrayList<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    public void deleteAllSubtasks() {
        for (Epic epic : epics.values()) {
            epic.getSubtasks().clear();
        }
        subtasks.clear();
    }

    public Subtask getSubtaskById(int id) {
        return subtasks.get(id);
    }

    public boolean deleteSubtaskById(int id) {
        Subtask subtask = subtasks.get(id);
        if (subtask == null) {
            return false;
        }
        subtask.getEpic().removeSubtask(subtask);
        subtask.getEpic().updateStatus();
        return subtasks.remove(id) != null;
    }
}




