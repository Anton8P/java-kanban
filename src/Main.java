import manager.TaskManager;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;

public class Main {

    public static void main(String[] args) {

        TaskManager taskManager = new TaskManager();

        Task task1 = new Task("Задача 1", "Первая задача");
        int idTask1 = taskManager.createTask(task1);
        Task task2 = new Task("Задача 2", "Вторая задача");
        int idTask2 = taskManager.createTask(task2);
        Epic epic1 = new Epic("Эпик 1", "Эпик с двумя подзадачами");
        int idEpic1 = taskManager.createEpic(epic1);
        Subtask subtask1 = new Subtask("Подзадача 1 (Эпик 1)", "Подзадача 1 к Эпику 1", epic1);
        int idSub1 = taskManager.createSubtask(subtask1);
        Subtask subtask2 = new Subtask("Подзадача 2 (Эпик 1)", "Подзадача 2 к Эпику 1", epic1);
        int idSub2 = taskManager.createSubtask(subtask2);
        Epic epic2 = new Epic("Эпик 2", "Эпик с одной подзадачей");
        int idEpic2 = taskManager.createEpic(epic2);
        Subtask subtask3 = new Subtask("Подзадача 3 (Эпик 2)", "Подзадача 3 к Эпику 2", epic2);
        int idSub3 = taskManager.createSubtask(subtask3);



        


    }
}
