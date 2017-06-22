package org.jeasy.jobs.server;

public class JobWithNoPublicExecutionMethod {

    private String parameter;

    public JobWithNoPublicExecutionMethod() {
    }

    private void doWork() {
        System.out.println("Hello " + parameter);
    }

    public String getParameter() {
        return parameter;
    }

    public void setParameter(String parameter) {
        this.parameter = parameter;
    }
}
