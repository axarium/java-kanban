package service;

import model.Task;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {
    private final Map<Integer, Node> history;
    private Node head;
    private Node tail;

    public InMemoryHistoryManager() {
        this.history = new HashMap<>();
    }

    @Override
    public void add(Task task) {
        Node node = new Node(null, tail, new Task(task));

        remove(task.getId());
        linkLast(node);
        history.put(task.getId(), node);
    }

    @Override
    public void remove(int id) {
        Node node = history.remove(id);
        removeNode(node);
    }

    @Override
    public List<Task> getHistory() {
        if (history.isEmpty()) {
            return new ArrayList<>();
        }

        return getTasks();
    }

    private List<Task> getTasks() {
        List<Task> resultList = new ArrayList<>(history.size());
        Node node = head;

        while (node != null) {
            resultList.add(new Task(node.value));
            node = node.next;
        }

        return resultList;
    }

    private void linkLast(Node node) {
        Node prevTail = tail;
        tail = node;

        if (prevTail == null) {
            head = node;
        } else {
            prevTail.next = node;
        }
    }

    private void removeNode(Node node) {
        if (node == null) {
            return;
        }

        Node nextNode = node.next;
        Node prevNode = node.prev;

        if (prevNode == null) {
            head = nextNode;
        } else {
            prevNode.next = nextNode;
        }

        if (nextNode == null) {
            tail = prevNode;
        } else {
            nextNode.prev = prevNode;
        }
    }

    private static class Node {
        private Node next;
        private Node prev;
        private final Task value;

        private Node(Node next, Node prev, Task value) {
            this.next = next;
            this.prev = prev;
            this.value = value;
        }
    }
}