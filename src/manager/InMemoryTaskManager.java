package manager;

import tasks.Task;
import tasks.Epic;
import tasks.Subtask;
import tasks.TaskStatus;

import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    protected int generatedId = 1;
    protected final Map<Integer, Task> tasks = new HashMap<>();
    protected final Map<Integer, Epic> epics = new HashMap<>();
    protected final Map<Integer, Subtask> subtasks = new HashMap<>();
    protected final HistoryManager historyManager = Managers.getDefaultHistory();
    protected final TreeSet<Task> prioritizedTasks = new TreeSet<>(
            Comparator.comparing(Task::getStartTime, Comparator.nullsLast(Comparator.naturalOrder()))
                    .thenComparing(Task::getId)
    );

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    @Override
    public int createTask(Task task) {
        if (task == null) {
            throw new ManagerSaveException("Задача не может быть null");
        }
        if (isTasksOverlapInTime(task)) {
            throw new TimeOverlapException("Задача '" + task.getTitle() +
                    "' пересекается по времени с уже существующей задачей");
        }
        int taskNewId;
        if (task.getId() <= 0 || isIdAlreadyExists(task.getId())) {
            taskNewId = getGeneratedId();
        } else {
            taskNewId = task.getId();
        }
        Task createdTask = task.withId(taskNewId);
        if (createdTask == null) {
            return 0;
        }
        tasks.put(taskNewId, createdTask);
        if (createdTask.getStartTime() != null) {
            prioritizedTasks.add(createdTask);
        }
        return taskNewId;
    }

    @Override
    public int createEpic(Epic epic) {
        if (epic == null) {
            throw new ManagerSaveException("Эпик не может быть null");
        }
        int epicNewId;
        if (epic.getId() <= 0 || isIdAlreadyExists(epic.getId())) {
            epicNewId = getGeneratedId();
        } else {
            epicNewId = epic.getId();
        }
        Epic createdEpic = epic.withId(epicNewId);
        if (createdEpic == null) {
            return 0;
        }
        epics.put(epicNewId, createdEpic);
        return epicNewId;
    }

    @Override
    public int createSubtask(Subtask subtask) {
        if (subtask == null) {
            throw new ManagerSaveException("Подзадача не может быть null");
        }
        if (isTasksOverlapInTime(subtask)) {
            throw new TimeOverlapException("Задача '" + subtask.getTitle() +
                    "' пересекается по времени с уже существующей задачей");
        }
        int subtaskNewId;
        if (subtask.getId() <= 0 || isIdAlreadyExists(subtask.getId())) {
            subtaskNewId = getGeneratedId();
        } else {
            subtaskNewId = subtask.getId();
        }
        Subtask createdSubtask = subtask.withId(subtaskNewId);
        int epicId = createdSubtask.getEpicId();
        Epic epic = epics.get(epicId);
        if (epic == null || createdSubtask.getId() == epic.getId()) {
            return 0;
        }
        Epic updatedEpic = epic.addSubtaskId(subtaskNewId);
        subtasks.put(subtaskNewId, createdSubtask);
        epics.put(epicId, updatedEpic);
        updateEpicStatus(createdSubtask.getEpicId());
        calculateEpicTime(updatedEpic.getId());
        if (createdSubtask.getStartTime() != null) {
            prioritizedTasks.add(createdSubtask);
        }
        return subtaskNewId;
    }

    @Override
    public boolean updateTask(Task newTask) {
        if (newTask == null || !tasks.containsKey(newTask.getId())) {
            throw new ManagerSaveException("Task is null or not find in tasks");
        }
        if (isTasksOverlapInTime(newTask)) {
            throw new TimeOverlapException("Задача '" + newTask.getTitle() +
                    "' пересекается по времени с уже существующей задачей");
        }
        Task oldTask = tasks.get(newTask.getId());
        Task updatedTask = new Task(
                oldTask.getId(),
                oldTask.getType(),
                newTask.getTitle(),
                newTask.getDescription(),
                newTask.getStatus(),
                newTask.getStartTime(),
                newTask.getDuration().toMinutes()
        );
        tasks.put(updatedTask.getId(), updatedTask);
        prioritizedTasks.remove(oldTask);
        if (newTask.getStartTime() != null) {
            prioritizedTasks.add(newTask);
        }
        return true;
    }

    @Override
    public boolean updateEpic(Epic newEpic) {
        if (newEpic == null || !epics.containsKey(newEpic.getId())) {
            throw new ManagerSaveException("Epic is null or not find in epics");
        }
        Epic oldEpic = epics.get(newEpic.getId());
        Epic updatedEpic = new Epic(
                oldEpic.getId(),
                newEpic.getType(),
                newEpic.getTitle(),
                newEpic.getDescription(),
                oldEpic.getStatus(),
                oldEpic.getSubtasksAllIds(),
                oldEpic.getStartTime(),
                oldEpic.getDuration().toMinutes(),
                oldEpic.getEndTime()
        );
        epics.put(updatedEpic.getId(), updatedEpic);
        updateEpicStatus(updatedEpic.getId());
        calculateEpicTime(updatedEpic.getId());
        return true;
    }

    @Override
    public boolean updateSubtask(Subtask newSubtask) {
        if (newSubtask == null || !subtasks.containsKey(newSubtask.getId())) {
            throw new ManagerSaveException("Subtask is null or not find in subtasks");
        }
        if (isTasksOverlapInTime(newSubtask)) {
            throw new TimeOverlapException("Задача '" + newSubtask.getTitle() +
                    "' пересекается по времени с уже существующей задачей");
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
                oldSubtask.getEpicId(),
                newSubtask.getStartTime(),
                newSubtask.getDuration().toMinutes()
        );
        subtasks.put(updatedSubtask.getId(), updatedSubtask);
        updateEpicStatus(updatedSubtask.getEpicId());
        calculateEpicTime(updatedSubtask.getEpicId());
        prioritizedTasks.remove(oldSubtask);
        if (newSubtask.getStartTime() != null) {
            prioritizedTasks.add(newSubtask);
        }
        return true;
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
    public List<Subtask> getAllSubtasksByEpicId(int epicId) {
        return subtasks.values().stream()
                .filter(subtask -> subtask.getEpicId() == epicId)
                .toList();
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public void deleteAllTasks() {
        for (Task task : tasks.values()) {
            historyManager.remove(task.getId());
            prioritizedTasks.remove(task);
        }
        tasks.clear();
    }

    @Override
    public void deleteAllEpics() {
        for (Epic epic : epics.values()) {
            for (Integer subtaskId : epic.getSubtasksAllIds()) {
                historyManager.remove(subtaskId);
            }
            historyManager.remove(epic.getId());
            epic.getSubtasksAllIds().clear();
        }

        prioritizedTasks.removeAll(subtasks.values());
        subtasks.clear();
        epics.clear();
    }

    @Override
    public void deleteAllSubtasks() {
        for (Subtask subtask : subtasks.values()) {
            historyManager.remove(subtask.getId());
            prioritizedTasks.remove(subtask);
        }
        for (Epic epic : epics.values()) {
            epic.getSubtasksAllIds().clear();
            updateEpicStatus(epic.getId());
            calculateEpicTime(epic.getId());
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
        for (Integer subtaskId : epic.getSubtasksAllIds()) {
            subtasks.remove(subtaskId);
        }
        historyManager.remove(id);
        for (Integer subId : epic.getSubtasksAllIds()) {
            historyManager.remove(subId);
        }
        epic.getSubtasksAllIds().clear();
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
        calculateEpicTime(subtask.getEpicId());
        return isDelete;
    }

    protected void calculateEpicTime(int epicId) {
        Epic epic = getEpicById(epicId);
        if (epic == null) {
            return;
        }

        List<Integer> subtaskIds = epic.getSubtasksAllIds();

        LocalDateTime earliestSubtask = null;
        long totalDuration = 0;
        LocalDateTime latestSubtask = null;

        for (Integer subtaskId : subtaskIds) {
            Subtask subtask = getSubtaskById(subtaskId);
            if (subtask == null) {
                continue;
            }

            LocalDateTime subtaskStart = subtask.getStartTime();
            LocalDateTime subtaskEnd = subtask.getEndTime();

            if (subtaskStart != null) {
                if (earliestSubtask == null || subtaskStart.isBefore(earliestSubtask)) {
                    earliestSubtask = subtaskStart;
                }
            }

            if (subtaskEnd != null) {
                if (latestSubtask == null || subtaskEnd.isAfter(latestSubtask)) {
                    latestSubtask = subtaskEnd;
                }
            }
            totalDuration += subtask.getDuration().toMinutes();
        }

        Epic updatedTimeEpic = epic.withTimes(earliestSubtask, totalDuration, latestSubtask);
        epics.put(epicId, updatedTimeEpic);
    }

    protected boolean isTasksOverlapInTime(Task newTask) {
        if (newTask.getStartTime() == null) {
            return false;
        }
        LocalDateTime newTaskEndTime = newTask.getEndTime();
        if (newTaskEndTime == null && newTask.getStartTime() != null) {
            newTaskEndTime = newTask.getStartTime().plus(newTask.getDuration());
        }
        if (newTaskEndTime == null) {
            return false;
        }

        final LocalDateTime finalEndTime = newTaskEndTime;

        return getPrioritizedTasks().stream()
                .filter(task -> task.getStartTime() != null && task.getEndTime() != null)
                .filter(task -> !task.equals(newTask))
                .anyMatch(existingTask ->
                        existingTask.getStartTime().isBefore(finalEndTime) &&
                                existingTask.getEndTime().isAfter(newTask.getStartTime())
                );
    }

    private void updateEpicStatus(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic == null) {
            return;
        }
        List<Subtask> subtasksFromEpic = getAllSubtasksByEpicId(epicId);
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
}




