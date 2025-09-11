import manager.HistoryManager;
import manager.Managers;
import manager.TaskManager;
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

    }
}
