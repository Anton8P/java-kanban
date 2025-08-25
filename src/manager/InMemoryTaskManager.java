package manager;

import tasks.Task;
import tasks.Epic;
import tasks.Subtask;
import tasks.TaskStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private final HistoryManager historyManager = Managers.getDefaultHistory();
    private int generatedId = 1;

    @Override
    public int getGeneratedId() {
        return generatedId++;
    }

    private boolean isIdAlreadyExists(int id) {
        return tasks.containsKey(id) || epics.containsKey(id) || subtasks.containsKey(id);
    }

    @Override
    public int createTask(Task task) {
        if (task == null) {
            return 0;
        }
        int taskId = task.getId();
        if (isIdAlreadyExists(taskId)) {
            taskId = getGeneratedId();
        }
        tasks.put(taskId, task);
        return taskId;
    }

    @Override
    public int createEpic(Epic epic) {
        if (epic == null) {
            return 0;
        }
        int epicId = epic.getId();
        if (isIdAlreadyExists(epicId)) {
            epicId = getGeneratedId();
        }
        epics.put(epicId, epic);
        return epicId;
    }

    @Override
    public int createSubtask(Subtask subtask) {
        if (subtask == null) {
            return 0;
        }
        Epic epic = epics.get(subtask.getEpicId());
        if (epic == null || subtask.getId() == epic.getId()) {
            return 0;
        }
        int subtaskId = subtask.getId();
        if (isIdAlreadyExists(subtaskId)) {
            subtaskId = getGeneratedId();
        }
        subtasks.put(subtaskId, subtask);
        epic.addSubtaskId(subtask);
        updateEpicStatus(subtask.getEpicId());
        return subtaskId;
    }

    @Override
    public boolean updateTask(Task newTask) {
        if (newTask == null) {
            return false;
        }
        if (!tasks.containsKey(newTask.getId())) {
            System.out.println("Task not found.");
            return false;
        }
        tasks.put(newTask.getId(), newTask);
        return true;
    }

    @Override
    public boolean updateEpic(Epic newEpic) {
        if (newEpic == null) {
            return false;
        }
        if (!epics.containsKey(newEpic.getId())) {
            System.out.println("Epic not found.");
            return false;
        }
        Epic oldEpic = epics.get(newEpic.getId());
        newEpic.getSubtasksId().addAll(oldEpic.getSubtasksId());
        epics.put(newEpic.getId(), newEpic);
        return true;
    }

    @Override
    public boolean updateSubtask(Subtask newSubtask) {
        if (newSubtask == null) {
            return false;
        }
        if (!subtasks.containsKey(newSubtask.getId())) {
            System.out.println("Subtask not found.");
            return false;
        }
        Subtask oldSubtask = subtasks.get(newSubtask.getId());
        Epic oldEpic = epics.get(oldSubtask.getEpicId());
        Epic newEpic = epics.get(newSubtask.getEpicId());
        if (!oldEpic.equals(newEpic)) {
            return false;
        }
        subtasks.put(newSubtask.getId(), newSubtask);
        updateEpicStatus(newSubtask.getEpicId());
        return true;
    }

    @Override
    public void updateEpicStatus(int epicId) {
        Epic epic = getEpicById(epicId);

        if (epic == null) {
            return;
        }

        ArrayList<Subtask> subtasksFromEpic = getAllSubtasksFromEpic(epicId);

        if (subtasksFromEpic.isEmpty()) {
            epic.setStatus(TaskStatus.NEW);
            return;
        }

        boolean allTasksNew = true;
        boolean allTasksDone = true;

        for (Subtask subtask : subtasksFromEpic) {
            if (subtask == null) {
                continue;
            }

            if (subtask.getStatus() != TaskStatus.NEW) {
                allTasksNew = false;
            }
            if (subtask.getStatus() != TaskStatus.DONE) {
                allTasksDone = false;
            }
        }

        if (allTasksNew) {
            epic.setStatus(TaskStatus.NEW);
        } else if (allTasksDone) {
            epic.setStatus(TaskStatus.DONE);
        } else {
            epic.setStatus(TaskStatus.IN_PROGRESS);
        }
    }

    @Override
    public Task getTask(int id) {
        if (tasks.get(id) != null) {
            historyManager.add(tasks.get(id));
        }
        return tasks.get(id);
    }

    @Override
    public Epic getEpic(int id) {
        if (epics.get(id) != null) {
            historyManager.add(epics.get(id));
        }
        return epics.get(id);
    }

    @Override
    public Subtask getSubtask(int id) {
        if (subtasks.get(id) != null) {
            historyManager.add(subtasks.get(id));
        }
        return subtasks.get(id);
    }

    @Override
    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public ArrayList<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public ArrayList<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public Task getTaskById(int id) {
        return tasks.get(id);
    }

    @Override
    public Epic getEpicById(int id) {
        return epics.get(id);
    }

    @Override
    public Subtask getSubtaskById(int id) {
        return subtasks.get(id);
    }

    @Override
    public ArrayList<Subtask> getAllSubtasksFromEpic(int epicId) {
        ArrayList<Subtask> allSubtasks = new ArrayList<>();
        Epic epic = epics.get(epicId);
        if (epic != null) {
            for (int subtaskId : epic.getSubtasksId()) {
                Subtask subtask = subtasks.get(subtaskId);
                if (subtask != null) {
                    allSubtasks.add(subtask);
                }
            }
        }
        return allSubtasks;
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public void deleteAllTasks() {
        tasks.clear();
    }

    @Override
    public void deleteAllEpics() {
        for (Epic epic : epics.values()) {
            epic.getSubtasksId().clear();
        }
        epics.clear();
        subtasks.clear();
    }

    @Override
    public void deleteAllSubtasks() {
        for (Epic epic : epics.values()) {
            epic.getSubtasksId().clear();
            updateEpicStatus(epic.getId());
        }
        subtasks.clear();
    }

    @Override
    public boolean deleteTaskById(int id) {
        if (!tasks.containsKey(id)) {
            return false;
        }
        return tasks.remove(id) != null;
    }

    @Override
    public boolean deleteEpicById(int id) {
        Epic epic = epics.get(id);
        if (epic == null) {
            return false;
        }
        for (Integer subtaskId : epic.getSubtasksId()) {
            subtasks.remove(subtaskId);
        }
        epic.getSubtasksId().clear();
        return epics.remove(id) != null;
    }

    @Override
    public boolean deleteSubtaskById(int id) {
        Subtask subtask = subtasks.get(id);
        if (subtask == null) {
            return false;
        }
        Epic epic = epics.get(subtask.getEpicId());
        epic.removeSubtaskId(id);
        updateEpicStatus(subtask.getEpicId());
        return subtasks.remove(id) != null;
    }
}




