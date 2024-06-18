package task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class Epic extends Task {
    private final ArrayList<Integer> subTaskIds;
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

    protected void setDuration(Duration duration) {
        this.duration = duration;
    }

    @Override
    public String toString() {
        String startTimeString = "";
        String endTimeString = "";

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yy HH:mm");

        if (startTime != null) {
            startTimeString = startTime.format(dateTimeFormatter);
        }

        if (endTime != null) {
            endTimeString = endTime.format(dateTimeFormatter);
        }

        return "Epic{" +
                "id=" + id +
                ", subTaskIds.length=" + subTaskIds.size() +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", start_time=" + startTimeString +
                ", duration=" + duration.toMinutes() + "мин." +
                ", end_time=" + endTimeString +
                '}';
    }
}
