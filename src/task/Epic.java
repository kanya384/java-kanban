package task;

import java.util.ArrayList;

public class Epic extends Task {
    private final ArrayList<Integer> subTaskIds;

    public Epic(String name, String description) {
        super(name, description, Status.NEW);
        subTaskIds = new ArrayList<>();
    }

    public Epic(Epic epic) {
        super(epic.getName(), epic.getDescription(), epic.getStatus());
        this.id = epic.getId();
        subTaskIds = new ArrayList<>();
    }

    public ArrayList<Integer> getSubTaskIds() {
        return new ArrayList<>(subTaskIds);
    }

    protected void addSubTaskId(int id) {
        if (this.id == id) {
            return;
        }
        subTaskIds.add(id);
    }

    protected void deleteSubTaskId(Integer id) {
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
