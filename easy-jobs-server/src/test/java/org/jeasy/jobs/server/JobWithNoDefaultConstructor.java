package org.jeasy.jobs.server;

public class JobWithNoDefaultConstructor {

    private String parameter;

    public JobWithNoDefaultConstructor(String parameter) {
        this.parameter = parameter;
    }

    public String getParameter() {
        return parameter;
    }

    public void setParameter(String parameter) {
        this.parameter = parameter;
    }
}
