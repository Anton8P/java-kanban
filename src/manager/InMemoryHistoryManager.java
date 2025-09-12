package manager;

import tasks.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {

    private final Map<Integer, Node<Task>> historyTask;
    private final List<Task> taskList;

    private Node<Task> head;
    private Node<Task> tail;
    private int sizeTaskList;
    private int size;

    public InMemoryHistoryManager() {
        this.historyTask = new HashMap<>();
        this.taskList = new ArrayList<>();
        this.head = null;
        this.tail = null;
        this.size = 0;
    }

    @Override
    public void remove(int taskId) {
        Node<Task> node = historyTask.get(taskId);
        if (node != null) {
            removeNode(node);
            historyTask.remove(taskId);
        }
    }

    @Override
    public List<Task> getHistory() {
        List<Task> listHistory = new ArrayList<>();
        Node<Task> headNode = head;
        while (headNode != null) {
            listHistory.add(headNode.task);
            headNode = headNode.next;
        }
        return listHistory;
    }

    @Override
    public void add(Task task) {
        int taskId = task.getId();
        if (historyTask.containsKey(taskId)) {
            removeNode(historyTask.get(taskId));
        }
        Node<Task> newNode = new Node<>(task);
        linkLast(newNode);
        historyTask.put(taskId, newNode);
    }

    private void linkLast(Node<Task> node) {
        if (tail == null) {
            head = node;
            tail = node;
        } else {
            tail.next = node;
            node.prev = tail;
            tail = node;
        }
        taskList.add(node.task);
        sizeTaskList++;
        size++;
    }

    private boolean removeNode(Node<Task> nodeToDelete) {
        if (nodeToDelete == null) {
            return false;
        }

        if (nodeToDelete.prev != null) {
            nodeToDelete.prev.next = nodeToDelete.next;
        } else {
            head = nodeToDelete.next;
        }

        if (nodeToDelete.next != null) {
            nodeToDelete.next.prev = nodeToDelete.prev;
        } else {
            tail = nodeToDelete.prev;
        }
        nodeToDelete.next = null;
        nodeToDelete.prev = null;
        nodeToDelete.task = null;
        size--;
        return true;
    }

    public int getSize() {
        return size;
    }

    public int getSizeTaskList() {
        return sizeTaskList;
    }

    private static class Node<T> {
        public Task task;
        public Node<Task> next;
        public Node<Task> prev;

        public Node(Task task) {
            this.task = task;
            this.next = null;
            this.prev = null;
        }
    }
}
