package task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class SubTask extends Task {
    private final int epicId;

    public SubTask(String name, String description, Status status, LocalDateTime startTime, Duration duration, int epicId) {
        super(name, description, status, startTime, duration);
        this.epicId = epicId;
    }

    public SubTask(SubTask subTask) {
        super(subTask.getName(), subTask.getDescription(), subTask.getStatus(), subTask.getStartTime(), subTask.getDuration());
        this.id = subTask.getId();
        this.epicId = subTask.getEpicId();
    }

    public int getEpicId() {
        return epicId;
    }


    @Override
    public String toString() {
        String startTimeString = "";
        if (startTime != null) {
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yy HH:mm");
            startTimeString = startTime.format(dateTimeFormatter);
        }
        return "SubTask{" +
                "id=" + id +
                ", epicId=" + epicId +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", start_time=" + startTimeString +
                ", duration=" + duration.toMinutes() + "мин." +
                '}';
    }
}