package task;

import java.util.ArrayList;

public class Epic extends Task{
    private ArrayList<Integer> subTaskIds;
    public Epic(String name, String description) {
        super(name, description, Status.NEW);
        subTaskIds = new ArrayList<>();
    }

    public ArrayList<Integer> getSubTaskIds() {
        return subTaskIds;
    }

    protected void addSubTaskId(int id) {
        subTaskIds.add(id);
    }

    protected void deleteSubTaskId(int id) {
        subTaskIds.remove(id);
    }

    protected void setStatus(Status status) {
        this.status = status;
    }


    @Override
    public String toString() {
        return "Epic{" +
                "id=" + id +
                ", subTaskIds.length=" + subTaskIds.size() +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                '}';
    }
}
