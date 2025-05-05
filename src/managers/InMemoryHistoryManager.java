package managers;

import tasks.Task;
import tools.Node;

import java.util.*;
import java.util.stream.Collectors;


public class InMemoryHistoryManager implements HistoryManager { //реализация методов HistoryManager
    private Map<Integer, Node<Task>> history = new HashMap<>();
    private Node<Task> head;
    private Node<Task> tail;

    @Override
    public void add(Task task) {
        if (task != null) {
            history.put(task.getTaskId(), linkLast(task));
        }
    }

    @Override
    public List<Task> getHistory() { //Просмотр содержимого истории просмотров
        return getTasksHistory();
    }

    @Override
    public void remove(Integer id) { //удаление истории
        history.remove(id);
    }

    private List<Task> getTasksHistory() {
        return history.values().stream()
                .sorted(Comparator.comparing(node -> node.getTask().getTaskId()))
                .map(Node::getTask)
                .collect(Collectors.toCollection(LinkedList::new));
    }

    private Node<Task> linkLast(Task task) {
        if (history.containsKey(task.getTaskId())) {
            removeNode(history.get(task.getTaskId()));
        }
        final Node<Task> oldTail = tail;
        final Node<Task> newTail = new Node<>(oldTail, task, null);
        tail = newTail;
        history.put(task.getTaskId(), newTail);
        if (oldTail == null) {
            head = newTail;
        } else {
            oldTail.setNext(newTail);
        }
        return newTail;
    }

    private void removeNode(Node<Task> node) {
        if (node != null) {
            final Node<Task> next = node.getNext();
            final Node<Task> previous = node.getPrevious();

            if (head == node && tail == node) {
                head = null;
                tail = null;
            } else if (head == node && tail != node) {
                head = next;
                head.setPrevious(null);
            } else if (head != node && tail == node) {
                tail = previous;
                tail.setNext(null);
            } else {
                previous.setNext(next);
                next.setPrevious(previous);
            }
        }
    }

}


