package task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private List<Integer> subTaskIds;
    private LocalDateTime endTime;

    public Epic(String name, String description) {
        super(name, description, Status.NEW, null, Duration.ofMinutes(0));
        subTaskIds = new ArrayList<>();
    }

    public Epic(Epic epic) {
        super(epic.getName(), epic.getDescription(), epic.getStatus(), null, Duration.ofMinutes(0));
        this.id = epic.getId();
        this.endTime = null;
        subTaskIds = new ArrayList<>();
    }

    public ArrayList<Integer> getSubTaskIds() {
        return new ArrayList<>(subTaskIds);
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    protected void addSubTaskId(int id) {
        if (this.id == id) {
            return;
        }

        if (subTaskIds == null) {
            subTaskIds = new ArrayList<>();
        }

        subTaskIds.add(id);
    }

    protected void deleteSubTaskId(Integer id) {
        subTaskIds.remove(id);
    }

    protected void setStatus(Status status) {
        this.status = status;
    }

    protected void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    protected void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    protected void addDuration(Duration duration) {
        if (this.duration == null) {
            this.duration = Duration.ofMinutes(0);
        }
        this.duration = this.duration.plus(duration);
    }


    @Override
    public String toString() {
        return "Epic{" +
                "subTaskIds=" + subTaskIds +
                ", endTime=" + endTime +
                ", id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", startTime=" + startTime +
                ", duration=" + duration +
                '}';
    }
}
