import manager.HistoryManager;
import manager.Managers;
import manager.TaskManager;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.util.List;

public class Main {

    public static void main(String[] args) {

        TaskManager taskManager = Managers.getDefault();
        HistoryManager historyManager = Managers.getDefaultHistory();

        Task task1 = new Task("Task 1", "Description 1");
        Task task2 = new Task("Task 2", "Description 2");
        Task task3 = new Task("Task 3", "Description 3");
        Task task4 = new Task("Task 4", "Description 4");

        int id1 = taskManager.createTask(task1);
        int id2 = taskManager.createTask(task2);
        int id3 = taskManager.createTask(task3);
        int id4 = taskManager.createTask(task4);

        historyManager.add(taskManager.getTaskById(id1));
        historyManager.add(taskManager.getTaskById(id2));
        historyManager.add(taskManager.getTaskById(id3));
        historyManager.add(taskManager.getTaskById(id4));

        List<Task> history = historyManager.getHistory();
        System.out.println(history);
        historyManager.add(taskManager.getTaskById(id2));
        List<Task> updatedHistory = historyManager.getHistory();
        System.out.println(updatedHistory);
        historyManager.remove(taskManager.getTaskById(id1).getId());
        List<Task> deletedHistory = historyManager.getHistory();
        System.out.println(deletedHistory);


//        Task task1 = new Task("Задача 1", "Первая задача");
//        int idTask1 = taskManager.createTask(task1);
//        Task task2 = new Task("Задача 2", "Вторая задача");
//        int idTask2 = taskManager.createTask(task2);
//        Epic epic = new Epic("Эпик", "Эпик с тремя подзадачами");
//        int idEpic = taskManager.createEpic(epic);
//        Subtask subtask1 = new Subtask("Подзадача 1", "Подзадача 1 к Эпику", idEpic);
//        int idSub1 = taskManager.createSubtask(subtask1);
//        Subtask subtask2 = new Subtask("Подзадача 2", "Подзадача 2 к Эпику", idEpic);
//        int idSub2 = taskManager.createSubtask(subtask2);
//        Subtask subtask3 = new Subtask("Подзадача 3", "Подзадача 3 к Эпику", idEpic);
//        int idSub3 = taskManager.createSubtask(subtask3);
//        Epic epicWithoutSubtasks = new Epic("Эпик без подзадач", "Эпик и ноль подзадач");
//        int epicWithoutSubtasksId = taskManager.createEpic(epicWithoutSubtasks);
//
//        taskManager.getTask(idTask1);
//        System.out.println(taskManager.getHistory());
//        System.out.println("-".repeat(5));
//        taskManager.getTask(idTask2);
//        System.out.println(taskManager.getHistory());
//        System.out.println("-".repeat(5));
//        taskManager.getEpic(idEpic);
//        System.out.println(taskManager.getHistory());
//        System.out.println("-".repeat(5));
//        taskManager.getTask(idTask1);
//        System.out.println(taskManager.getHistory());
//        System.out.println("-".repeat(5));
//        taskManager.getTask(idTask1);
//        System.out.println(taskManager.getHistory());
//        System.out.println("-".repeat(5));
//        taskManager.getSubtask(idSub1);
//        System.out.println(taskManager.getHistory());
//        System.out.println("-".repeat(5));
//        taskManager.getEpic(epicWithoutSubtasksId);
//        System.out.println(taskManager.getHistory());
//        System.out.println("-".repeat(5));
//        taskManager.getSubtask(idSub2);
//        System.out.println(taskManager.getHistory());
//        System.out.println("-".repeat(5));
//        taskManager.getSubtask(idSub3);
//        System.out.println(taskManager.getHistory());
//        System.out.println("-".repeat(5));
//        taskManager.getEpic(epicWithoutSubtasksId);
//        System.out.println(taskManager.getHistory());
//        System.out.println("-".repeat(5));
//        taskManager.getSubtask(idSub3);
//        System.out.println(taskManager.getHistory());
//        System.out.println("-".repeat(5));
//        taskManager.deleteEpicById(idEpic);
//        System.out.println(taskManager.getHistory());

    }
}
