package eu.execom.todolistgrouptwo.model;

/**
 * Model representing a task.
 */
public class Task {

    private long id;

    private String title;

    private String description;

    private boolean finished;

    public Task() {
    }

    public Task(String title, String description,boolean finished) {
        this.title = title;
        this.description = description;
        this.finished = finished;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    @Override
    public String toString() {
        return "Task{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
