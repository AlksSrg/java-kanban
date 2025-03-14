package kanban.src.managers;

import kanban.src.tasks.Task;

import java.util.List;

public interface HistoryManager {//общие вид методов класса HistoryManager

    void add(Task task);//добавление в историю просмотров

    List<Task> getHistory();//Просмотр содержимого истории просмотров

}
