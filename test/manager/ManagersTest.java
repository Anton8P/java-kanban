package manager;

import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Task;


import static org.junit.jupiter.api.Assertions.*;

class ManagersTest {

    @Test
    void getDefaultReturnIsNotNull() {
        TaskManager taskManager = Managers.getDefault();
        Epic epic = new Epic("Test Epic", "Description", taskManager.getGeneratedId());
        int idTask = taskManager.createEpic(epic);
        assertNotNull(taskManager, "Возвращаемый объект Менеджера не может быть Null");
        assertNotNull(epic, "Объект не может быть Null");
        assertEquals(1, idTask, "id должен быть создан");
    }

    @Test
    void getDefaultHistory() {
        HistoryManager historyManager = Managers.getDefaultHistory();
        Task task = new Task("Test Task", "Description", 555);
        historyManager.add(task);
        Task taskFromHistory = historyManager.getHistory().getFirst();
        assertNotNull(taskFromHistory, "Полученный из Истории объект не может быть Null");
        assertNotNull(historyManager, "Возвращаемый объект Менеджера Истории не может быть Null");
    }

}