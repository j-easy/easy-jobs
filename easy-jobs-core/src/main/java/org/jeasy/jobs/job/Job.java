package org.jeasy.jobs.job;

public class Job {

    private int id;
    private String name;
    private String description;

    public Job() {
    }

    public Job(int id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    // if no setter => org.hibernate.PropertyNotFoundException: Could not locate setter method for property [org.jeasy.jobs.job.Job#id]
    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
