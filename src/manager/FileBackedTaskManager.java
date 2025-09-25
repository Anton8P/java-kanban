package manager;

import tasks.*;

import java.io.*;
import java.nio.file.Files;
import java.util.List;


public class FileBackedTaskManager extends InMemoryTaskManager {

    private final File autoSaveFile;

    public FileBackedTaskManager(File file) {
        autoSaveFile = file;
    }

    public static FileBackedTaskManager loadFromFile(File file) {

        FileBackedTaskManager manager = new FileBackedTaskManager(file);

        if (!file.exists()) {
            throw new ManagerSaveException("По данному пути файл/каталог не найден");
        }
        String content = null;
        try {
            content = Files.readString(file.toPath());
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка загрузки из файла: " + file, e);
        }

        String[] lines = content.split("\n");

        manager.getTasksMap().clear();
        manager.getEpicsMap().clear();
        manager.getSubtasksMap().clear();

        boolean allFieldsTitle = true;

        for (String line : lines) {
            if (allFieldsTitle) {
                allFieldsTitle = false;
                continue;
            }

            Task task = fromString(line);

            if (task != null) {

                if (task instanceof Epic) {
                    manager.addEpicDirectlyToMap((Epic) task);
                } else if (task instanceof Subtask) {
                    manager.addSubtaskDirectlyToMap((Subtask) task);
                } else {
                    manager.addTaskDirectlyToMap(task);
                }
            }
        }
        return manager;
    }

    public void save() {

        StringBuilder data = new StringBuilder();
        data.append("id,type,name,status,description,epic\n");

        for (Task task : super.getAllTasks()) {
            data.append(taskToString(task)).append("\n");
        }
        for (Epic epic : super.getAllEpics()) {
            data.append(taskToString(epic)).append("\n");
        }
        for (Subtask subtask : super.getAllSubtasks()) {
            data.append(taskToString(subtask)).append("\n");
        }

        try (FileWriter writer = new FileWriter(autoSaveFile)) {
            writer.write(data.toString());
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при сохранении в файл: " + autoSaveFile, e);
        }
    }

    public String taskToString(Task task) {
        if (task == null) {
            throw new ManagerSaveException("Задача не может быть null");
        }
        switch (task.getType()) {
            case EPIC:
                Epic epic = (Epic) task;
                return String.format("%d,%s,%s,%s,%s",
                        epic.getId(), epic.getType(), epic.getTitle(),
                        epic.getStatus(), epic.getDescription());
            case SUBTASK:
                Subtask subtask = (Subtask) task;
                return String.format("%d,%s,%s,%s,%s,%d",
                        subtask.getId(), subtask.getType(),
                        subtask.getTitle(), subtask.getStatus(),
                        subtask.getDescription(), subtask.getEpicId());
            case TASK:
            default:
                return String.format("%d,%s,%s,%s,%s",
                        task.getId(), task.getType(),
                        task.getTitle(), task.getStatus(),
                        task.getDescription());
        }
    }

    @Override
    public int createTask(Task taskDto) {
        if (taskDto == null) {
            throw new ManagerSaveException("Задача не может быть null");
        }
        int id = super.createTask(taskDto);
        save();
        return id;
    }

    @Override
    public int createEpic(Epic epicDto) {
        if (epicDto == null) {
            throw new ManagerSaveException("Эпик не может быть null");
        }
        int id = super.createEpic(epicDto);
        save();
        return id;
    }

    @Override
    public int createSubtask(Subtask subtaskDto) {
        if (subtaskDto == null) {
            throw new ManagerSaveException("Подзадача не может быть null");
        }
        int id = super.createSubtask(subtaskDto);
        save();
        return id;
    }

    @Override
    public boolean updateTask(Task newTask) {
        if (newTask == null) {
            throw new ManagerSaveException("Задача не может быть null");
        }
        boolean isUpdate = super.updateTask(newTask);
        save();
        return isUpdate;
    }

    @Override
    public boolean updateEpic(Epic newEpic) {
        if (newEpic == null) {
            throw new ManagerSaveException("Эпик не может быть null");
        }
        boolean isUpdate = super.updateEpic(newEpic);
        save();
        return isUpdate;
    }

    @Override
    public boolean updateSubtask(Subtask newSubtask) {
        if (newSubtask == null) {
            throw new ManagerSaveException("Подзадача не может быть null");
        }
        boolean isUpdate = super.updateSubtask(newSubtask);
        save();
        return isUpdate;
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        save();
    }

    @Override
    public void deleteAllSubtasks() {
        super.deleteAllSubtasks();
        save();
    }

    @Override
    public boolean deleteTaskById(int id) {
        boolean isDelete = super.deleteTaskById(id);
        save();
        return isDelete;
    }

    @Override
    public boolean deleteEpicById(int id) {
        boolean isDelete = super.deleteEpicById(id);
        save();
        return isDelete;
    }

    @Override
    public boolean deleteSubtaskById(int id) {
        boolean isDelete = super.deleteSubtaskById(id);
        save();
        return isDelete;
    }

    private static Task fromString(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }

        String[] parts = value.split(",");

        if (parts.length < 5) {
            throw new ManagerSaveException("Количество полей меньше минимального");
        }

        try {
            String idStr = parts[0].trim();
            int id = Integer.parseInt(idStr);
            TaskType type = TaskType.valueOf(parts[1].trim());
            String title = parts[2].trim();
            TaskStatus status = TaskStatus.valueOf(parts[3].trim());
            String description = parts[4].trim();

            switch (type) {
                case EPIC:
                    Epic epic = new Epic(title, description);
                    epic = (Epic) epic.withId(id).withStatus(status);
                    return epic;
                case SUBTASK:
                    if (parts.length < 6) {
                        throw new ManagerSaveException("Нехватает параметров для подзадачи");
                    }
                    int epicId = Integer.parseInt(parts[5].trim());
                    Subtask subtask = new Subtask(title, description, epicId);
                    subtask = (Subtask) subtask.withId(id).withStatus(status);
                    return subtask;
                case TASK:
                    Task task = new Task(title, description);
                    task = task.withId(id).withStatus(status);
                    return task;
                default:
                    throw new ManagerSaveException("Неизвестный тип задачи: " + type);
            }
        } catch (Exception e) {
            throw new ManagerSaveException("Ошибка парсинга: " + e.getMessage() + " в строке: " + value);
        }
    }

    public static void main(String[] args) {
        try {
            File testFile = File.createTempFile("test_task_load", ".txt");
            testFile.deleteOnExit();

            FileBackedTaskManager manager1 = new FileBackedTaskManager(testFile);

            Task task1 = new Task("Задача1", "Описание1");
            Task task2 = new Task("Задача2", "Описание2");
            Epic epic = new Epic("Эпик1", "Описание эпика1");
            int epicId1 = manager1.createEpic(epic);
            Subtask subtask1 = new Subtask("Подзадача1", "Описание подзадачи1", epicId1);
            Subtask subtask2 = new Subtask("Подзадача2", "Описание подзадачи2", epicId1);
            int taskId1 = manager1.createTask(task1);
            int taskId2 = manager1.createTask(task2);
            int subtaskId1 = manager1.createSubtask(subtask1);
            int subtaskId2 = manager1.createSubtask(subtask2);

            System.out.println("Количество задач в Менеджере1:");
            System.out.println("Задачи: " + manager1.getAllTasks().size());
            System.out.println("Эпики: " + manager1.getAllEpics().size());
            System.out.println("Подзадачи: " + manager1.getAllSubtasks().size());

            manager1.save();

            FileBackedTaskManager manager2 = FileBackedTaskManager.loadFromFile(testFile);

            System.out.println("Количество задач в Менеджере2:");
            System.out.println("Задачи: " + manager2.getAllTasks().size());
            System.out.println("Эпики: " + manager2.getAllEpics().size());
            System.out.println("Подзадачи: " + manager2.getAllSubtasks().size());

            System.out.println("Сравнение содержимого:");

            boolean tasksCount = manager1.getAllTasks().size() == manager2.getAllTasks().size();
            boolean epicsCount = manager1.getAllEpics().size() == manager2.getAllEpics().size();
            boolean subtasksCount = manager1.getAllSubtasks().size() == manager2.getAllSubtasks().size();

            System.out.println("Количество задач совпадает: " + tasksCount);
            System.out.println("Количество эпиков совпадает: " + epicsCount);
            System.out.println("Количество подзадач совпадает: " + subtasksCount);

            Task task1FromManager1 = manager1.getTaskById(taskId1);
            Task task1FromManager2 = manager2.getTaskById(taskId1);

            boolean task1Matches = task1FromManager1 != null && task1FromManager2 != null &&
                    task1FromManager1.getTitle().equals(task1FromManager2.getTitle()) &&
                    task1FromManager1.getDescription().equals(task1FromManager2.getDescription());

            System.out.println("Задача1 из Менеджера1 совпадает с Задачей1 из Менеджера2: " + task1Matches);

            Epic epicFromManager1 = manager1.getEpicById(epicId1);
            Epic epicFromManager2 = manager2.getEpicById(epicId1);

            boolean epic1Matches = epicFromManager1 != null && epicFromManager2 != null &&
                    epicFromManager1.getTitle().equals(epicFromManager2.getTitle()) && epicFromManager1.getDescription().equals(epicFromManager2.getDescription());

            System.out.println("Эпик из Менеджера1 совпадает с Эпиком из Менеджера2: " + epic1Matches);

            List<Subtask> subtasksFromManager1 = manager1.getAllSubtasksFromEpic(epicId1);
            List<Subtask> subtasksFromManager2 = manager2.getAllSubtasksFromEpic(epicId1);

            boolean subtasksLinksMatch = subtasksFromManager1.size() == subtasksFromManager2.size();
            System.out.println("Количество подзадач у эпиков Менеджера1 и Менеджера2 совпадают: " + subtasksLinksMatch);

        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка: " + e.getMessage());
        }


    }

}
