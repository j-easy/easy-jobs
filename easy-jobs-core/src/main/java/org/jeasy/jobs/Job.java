package org.jeasy.jobs;

class Job {

    private int id;
    private String name;
    // todo add description for ui

    public Job() {
    }

    public Job(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    // if no setter => org.hibernate.PropertyNotFoundException: Could not locate setter method for property [org.jeasy.jobs.Job#id]
    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }
}
