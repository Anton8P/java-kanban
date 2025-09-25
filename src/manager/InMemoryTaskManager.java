package manager;

import tasks.Task;
import tasks.Epic;
import tasks.Subtask;
import tasks.TaskStatus;

import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Integer, Epic> epics = new HashMap<>();
    private final Map<Integer, Subtask> subtasks = new HashMap<>();
    private final HistoryManager historyManager = Managers.getDefaultHistory();
    private int generatedId = 1;

    @Override
    public int createTask(Task taskDto) {
        if (taskDto == null) {
            return 0;
        }
        int taskNewId;
        if (taskDto.getId() <= 0 || isIdAlreadyExists(taskDto.getId())) {
            taskNewId = getGeneratedId();
        } else {
            taskNewId = taskDto.getId();
        }
        Task task = taskDto.withId(taskNewId);
        if (task == null) {
            return 0;
        }
        tasks.put(taskNewId, task);
        return taskNewId;
    }

    @Override
    public int createEpic(Epic epicDto) {
        if (epicDto == null) {
            return 0;
        }
        int epicNewId;
        if (epicDto.getId() <= 0 || isIdAlreadyExists(epicDto.getId())) {
            epicNewId = getGeneratedId();
        } else {
            epicNewId = epicDto.getId();
        }
        Epic epic = epicDto.withId(epicNewId);
        if (epic == null) {
            return 0;
        }
        epics.put(epicNewId, epic);
        return epicNewId;
    }

    @Override
    public int createSubtask(Subtask subtaskDto) {
        if (subtaskDto == null) {
            return 0;
        }
        int subtaskNewId;
        if (subtaskDto.getId() <= 0 || isIdAlreadyExists(subtaskDto.getId())) {
            subtaskNewId = getGeneratedId();
        } else {
            subtaskNewId = subtaskDto.getId();
        }
        Subtask subtask = subtaskDto.withId(subtaskNewId);
        if (subtask == null) {
            return 0;
        }
        int epicId = subtask.getEpicId();
        Epic epic = epics.get(epicId);
        if (epic == null || subtask.getId() == epic.getId()) {
            return 0;
        }
        Epic updatedEpic = epic.addSubtaskId(subtaskNewId);
        subtasks.put(subtaskNewId, subtask);
        epics.put(epicId, updatedEpic);
        updateEpicStatus(subtask.getEpicId());
        return subtaskNewId;
    }

    @Override
    public boolean updateTask(Task newTask) {
        if (newTask == null || !tasks.containsKey(newTask.getId())) {
            return false;
        }
        Task oldTask = tasks.get(newTask.getId());
        Task updatedTask = new Task(
                oldTask.getId(),
                oldTask.getType(),
                newTask.getTitle(),
                newTask.getDescription(),
                newTask.getStatus()
        );
        tasks.put(updatedTask.getId(), updatedTask);
        return true;
    }

    @Override
    public boolean updateEpic(Epic newEpic) {
        if (newEpic == null || !epics.containsKey(newEpic.getId())) {
            return false;
        }
        Epic oldEpic = epics.get(newEpic.getId());
        Epic updatedEpic = new Epic(
                oldEpic.getId(),
                oldEpic.getType(),
                newEpic.getTitle(),
                newEpic.getDescription(),
                oldEpic.getStatus(),
                oldEpic.getSubtasksId()
        );
        epics.put(updatedEpic.getId(), updatedEpic);
        return true;
    }

    @Override
    public boolean updateSubtask(Subtask newSubtask) {
        if (newSubtask == null || !subtasks.containsKey(newSubtask.getId())) {
            return false;
        }
        Subtask oldSubtask = subtasks.get(newSubtask.getId());
        if (oldSubtask.getEpicId() != newSubtask.getEpicId()) {
            return false;
        }
        Subtask updatedSubtask = new Subtask(
                oldSubtask.getId(),
                oldSubtask.getType(),
                newSubtask.getTitle(),
                newSubtask.getDescription(),
                newSubtask.getStatus(),
                oldSubtask.getEpicId()
        );
        subtasks.put(updatedSubtask.getId(), updatedSubtask);
        updateEpicStatus(updatedSubtask.getEpicId());
        return true;
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
        Task task = tasks.get(id);
        if (task != null) {
            historyManager.add(task);
        }
        return task;
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epic = epics.get(id);
        if (epic != null) {
            historyManager.add(epic);
        }
        return epic;
    }

    @Override
    public Subtask getSubtaskById(int id) {
        Subtask subtask = subtasks.get(id);
        if (subtask != null) {
            historyManager.add(subtask);
        }
        return subtask;
    }

    @Override
    public ArrayList<Subtask> getAllSubtasksFromEpic(int epicId) {
        ArrayList<Subtask> allSubtasks = new ArrayList<>();
        for (Subtask subtask : subtasks.values()) {
            if (subtask.getEpicId() == epicId) {
                allSubtasks.add(subtask);
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
        for (Task task : tasks.values()) {
            historyManager.remove(task.getId());
        }
        tasks.clear();
    }

    @Override
    public void deleteAllEpics() {
        for (Epic epic : epics.values()) {
            for (Integer subtask : epic.getSubtasksId()) {
                historyManager.remove(subtask);
            }
            historyManager.remove(epic.getId());
            epic.getSubtasksId().clear();
        }
        subtasks.clear();
        epics.clear();
    }

    @Override
    public void deleteAllSubtasks() {
        for (Subtask subtask : subtasks.values()) {
            historyManager.remove(subtask.getId());
        }
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
        historyManager.remove(id);
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
        historyManager.remove(id);
        for (Integer subId : epic.getSubtasksId()) {
            historyManager.remove(subId);
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
        historyManager.remove(id);
        boolean isDelete = subtasks.remove(id) != null;
        updateEpicStatus(subtask.getEpicId());
        return isDelete;
    }

    protected Map<Integer, Task> getTasksMap() {
        return tasks;
    }

    protected Map<Integer, Epic> getEpicsMap() {
        return epics;
    }

    protected Map<Integer, Subtask> getSubtasksMap() {
        return subtasks;
    }

    protected void addTaskDirectlyToMap(Task task) {
        if (task != null && task.getId() != 0) {
            tasks.put(task.getId(), task);
        }
    }

    protected void addEpicDirectlyToMap(Epic epic) {
        if (epic != null && epic.getId() != 0) {
            epics.put(epic.getId(), epic);
        }
    }

    protected void addSubtaskDirectlyToMap(Subtask subtask) {
        if (subtask != null && subtask.getId() != 0) {
            subtasks.put(subtask.getId(), subtask);
        }
    }

    private void updateEpicStatus(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic == null) {
            return;
        }
        List<Subtask> subtasksFromEpic = getSubtasksByEpicId(epicId);
        if (subtasksFromEpic.isEmpty()) {
            Epic updatedEpic = epic.withStatus(TaskStatus.NEW);
            epics.put(epicId, updatedEpic);
            return;
        }
        boolean allTasksNew = true;
        boolean allTasksDone = true;
        for (Subtask subtask : subtasksFromEpic) {
            if (subtask.getStatus() != TaskStatus.NEW) {
                allTasksNew = false;
            }
            if (subtask.getStatus() != TaskStatus.DONE) {
                allTasksDone = false;
            }
            if (!allTasksNew && !allTasksDone) {
                break;
            }
        }
        TaskStatus newStatus;
        if (allTasksNew) {
            newStatus = TaskStatus.NEW;
        } else if (allTasksDone) {
            newStatus = TaskStatus.DONE;
        } else {
            newStatus = TaskStatus.IN_PROGRESS;
        }
        Epic updatedEpic = epic.withStatus(newStatus);
        epics.put(epicId, updatedEpic);
    }

    private int getGeneratedId() {
        int newId = generatedId;
        while (isIdAlreadyExists(newId)) {
            newId++;
        }
        generatedId = newId + 1;
        return newId;
    }

    private boolean isIdAlreadyExists(int id) {
        return tasks.containsKey(id) || epics.containsKey(id) || subtasks.containsKey(id);
    }

    private List<Subtask> getSubtasksByEpicId(int epicId) {
        List<Subtask> result = new ArrayList<>();
        for (Subtask subtask : subtasks.values()) {
            if (subtask.getEpicId() == epicId) {
                result.add(subtask);
            }
        }
        return result;
    }
}




