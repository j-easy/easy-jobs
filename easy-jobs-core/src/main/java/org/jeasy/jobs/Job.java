package org.jeasy.jobs;

class Job {

    private int id;
    private String name;
    // todo add description for ui

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

}
