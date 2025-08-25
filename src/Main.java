import manager.Managers;
import manager.TaskManager;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

public class Main {

    public static void main(String[] args) {

        TaskManager taskManager = Managers.getDefault();

        Task task1 = new Task("Задача 1", "Первая задача", taskManager.getGeneratedId());
        int idTask1 = taskManager.createTask(task1);
        Task task2 = new Task("Задача 2", "Вторая задача", taskManager.getGeneratedId());
        int idTask2 = taskManager.createTask(task2);
        Epic epic1 = new Epic("Эпик 1", "Эпик с двумя подзадачами", taskManager.getGeneratedId());
        int idEpic1 = taskManager.createEpic(epic1);
        Subtask subtask1 = new Subtask("Подзадача 1 (Эпик 1)", "Подзадача 1 к Эпику 1", taskManager.getGeneratedId(), epic1.getId());
        int idSub1 = taskManager.createSubtask(subtask1);
        Subtask subtask2 = new Subtask("Подзадача 2 (Эпик 1)", "Подзадача 2 к Эпику 1", taskManager.getGeneratedId(), epic1.getId());
        int idSub2 = taskManager.createSubtask(subtask2);
        Epic epic2 = new Epic("Эпик 2", "Эпик с одной подзадачей", taskManager.getGeneratedId());
        int idEpic2 = taskManager.createEpic(epic2);
        Subtask subtask3 = new Subtask("Подзадача 3 (Эпик 2)", "Подзадача 3 к Эпику 2", taskManager.getGeneratedId(), epic2.getId());
        int idSub3 = taskManager.createSubtask(subtask3);

        taskManager.getTask(idTask1);
        taskManager.getTask(idTask2);
        taskManager.getEpic(idEpic1);
        taskManager.getEpic(idEpic2);
        taskManager.getSubtask(idSub1);
        taskManager.getSubtask(idSub2);
        taskManager.getSubtask(idSub3);

        printAllTasks(taskManager);

    }

    private static void printAllTasks(TaskManager manager) {
        System.out.println("Задачи:");
        for (Task task : manager.getAllTasks()) {
            System.out.println(task);
        }
        System.out.println("Эпики:");
        for (Task epic : manager.getAllEpics()) {
            System.out.println(epic);

            for (Task task : manager.getAllSubtasksFromEpic(epic.getId())) {
                System.out.println("--> " + task);
            }
        }
        System.out.println("Подзадачи:");
        for (Task subtask : manager.getAllSubtasks()) {
            System.out.println(subtask);
        }

        System.out.println("История:");
        for (Task task : manager.getHistory()) {
            System.out.println(task);
        }
    }
}
