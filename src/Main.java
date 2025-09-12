import manager.HistoryManager;
import manager.Managers;
import manager.TaskManager;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

public class Main {

    public static void main(String[] args) {

        TaskManager taskManager = Managers.getDefault();
        HistoryManager historyManager = Managers.getDefaultHistory();

        Task task1 = new Task("Task 1", "Description to Task 1");
        Task task2 = new Task("Task 2", "Description to Task 2");
        Task task3 = new Task("Task 3", "Description to Task 3");
        Task task4 = new Task("Task 4", "Description to Task 4");
        Epic epic1 = new Epic("Epic 1", "Description to Epic 1");
        Epic epic2 = new Epic("Epic 2", "Description to Epic 2");
        Epic epic3 = new Epic("Epic 3", "Description to Epic 3");

        int id1 = taskManager.createTask(task1);
        int id2 = taskManager.createTask(task2);
        int id3 = taskManager.createTask(task3);
        int id4 = taskManager.createTask(task4);
        int id5 = taskManager.createEpic(epic1);
        int id6 = taskManager.createEpic(epic2);
        int id7 = taskManager.createEpic(epic3);


        Subtask subtask1 = new Subtask("Subtask 1", "Description to Subtask from Epic 1", id5);
        Subtask subtask2 = new Subtask("Subtask 2", "Description to Subtask from Epic 1", id5);
        Subtask subtask3 = new Subtask("Subtask 3", "Description to Subtask from Epic 2", id6);

        int id8 = taskManager.createSubtask(subtask1);
        int id9 = taskManager.createSubtask(subtask2);
        int id10 = taskManager.createSubtask(subtask3);

        historyManager.add(taskManager.getTaskById(id1));
        historyManager.add(taskManager.getTaskById(id2));
        historyManager.add(taskManager.getTaskById(id3));
        historyManager.add(taskManager.getTaskById(id4));
        historyManager.add(taskManager.getEpicById(id5));
        historyManager.add(taskManager.getEpicById(id6));
        historyManager.add(taskManager.getEpicById(id7));
        historyManager.add(taskManager.getSubtaskById(id8));
        historyManager.add(taskManager.getSubtaskById(id9));
        historyManager.add(taskManager.getSubtaskById(id10));

        for (Task task : taskManager.getHistory()) {
            System.out.println(task);
        }

        taskManager.deleteAllTasks();
        taskManager.deleteAllEpics();
        taskManager.deleteAllSubtasks();

        System.out.println("---");
        System.out.println(taskManager.getAllTasks());
        System.out.println("---");
        System.out.println(taskManager.getAllEpics());
        System.out.println("---");
        System.out.println(taskManager.getAllSubtasks());
        System.out.println("---");

        for (Task task : taskManager.getHistory()) {
            System.out.println(task);
        }
    }
}
