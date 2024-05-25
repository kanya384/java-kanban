package task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private final HashMap<Integer, Node<Task>> tasks = new HashMap<>();
    private Node<Task> firstNode = null;
    private Node<Task> lastNode = null;

    @Override
    public void add(Task task) {

        if (tasks.get(task.getId()) != null) {
            remove(task.getId());
        }

        if (task instanceof Epic) {
            Epic epicToInsert = new Epic((Epic) task);
            linkLast(epicToInsert);
            return;
        } else if (task instanceof SubTask) {
            SubTask subTaskToInsert = new SubTask((SubTask) task);
            linkLast(subTaskToInsert);
            return;
        }

        Task taskToInsert = new Task(task);
        linkLast(taskToInsert);
    }

    @Override
    public void remove(int id) {
        removeNode(tasks.get(id));
    }


    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    private boolean isEmpty() {
        return firstNode == null;
    }

    private void linkLast(Task task) {
        final Node<Task> newNode = new Node<>(null, task, null);

        if (isEmpty()) {
            firstNode = newNode;
        } else {
            lastNode.setNext(newNode);
            newNode.setPrev(lastNode);
        }

        lastNode = newNode;
        tasks.put(lastNode.getItem().getId(), lastNode);
    }

    private List<Task> getTasks() {
        ArrayList<Task> tasksList = new ArrayList<>();

        Node<Task> curNode = firstNode;

        while (curNode != null) {
            tasksList.add(curNode.getItem());
            curNode = curNode.getNext();
        }

        return tasksList;
    }

    private void removeNode(Node<Task> node) {
        if (node == null) {
            return;
        }

        if (node == firstNode) {
            firstNode = node.getNext();
        } else {
            node.getPrev().setNext(node.getNext());
        }

        if (node == lastNode) {
            lastNode = node.getPrev();
        } else  {
            node.getNext().setPrev(node.getPrev());
        }

        tasks.remove(node.getItem().getId());
    }
}
