package org.jeasy.jobs.admin.web;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

class DashboardStatistics {

    private int registeredJobs;
    private int runningExecutions;
    private int failedExecutions;
    private int totalExecutions;
    private int pendingRequests;
    private List<LocalDate> lastThreeDays;
    private List<Integer> failedExecutionsForTheLastThreeDays;
    private List<Integer> succeededExecutionsForTheLastThreeDays;
    private List<LocalDateTime> lastTwelveHours;
    private List<Integer> receivedRequestsForTheLastTwelveHours;

    public int getRegisteredJobs() {
        return registeredJobs;
    }

    public void setRegisteredJobs(int registeredJobs) {
        this.registeredJobs = registeredJobs;
    }

    public int getRunningExecutions() {
        return runningExecutions;
    }

    public void setRunningExecutions(int runningExecutions) {
        this.runningExecutions = runningExecutions;
    }

    public int getFailedExecutions() {
        return failedExecutions;
    }

    public void setFailedExecutions(int failedExecutions) {
        this.failedExecutions = failedExecutions;
    }

    public int getTotalExecutions() {
        return totalExecutions;
    }

    public void setTotalExecutions(int totalExecutions) {
        this.totalExecutions = totalExecutions;
    }

    public int getPendingRequests() {
        return pendingRequests;
    }

    public void setPendingRequests(int pendingRequests) {
        this.pendingRequests = pendingRequests;
    }

    public List<LocalDate> getLastThreeDays() {
        return lastThreeDays;
    }

    public void setLastThreeDays(List<LocalDate> lastThreeDays) {
        this.lastThreeDays = lastThreeDays;
    }

    public List<Integer> getFailedExecutionsForTheLastThreeDays() {
        return failedExecutionsForTheLastThreeDays;
    }

    public void setFailedExecutionsForTheLastThreeDays(List<Integer> failedExecutionsForTheLastThreeDays) {
        this.failedExecutionsForTheLastThreeDays = failedExecutionsForTheLastThreeDays;
    }

    public List<Integer> getSucceededExecutionsForTheLastThreeDays() {
        return succeededExecutionsForTheLastThreeDays;
    }

    public void setSucceededExecutionsForTheLastThreeDays(List<Integer> succeededExecutionsForTheLastThreeDays) {
        this.succeededExecutionsForTheLastThreeDays = succeededExecutionsForTheLastThreeDays;
    }

    public List<LocalDateTime> getLastTwelveHours() {
        return lastTwelveHours;
    }

    public void setLastTwelveHours(List<LocalDateTime> lastTwelveHours) {
        this.lastTwelveHours = lastTwelveHours;
    }

    public List<Integer> getReceivedRequestsForTheLastTwelveHours() {
        return receivedRequestsForTheLastTwelveHours;
    }

    public void setReceivedRequestsForTheLastTwelveHours(List<Integer> receivedRequestsForTheLastTwelveHours) {
        this.receivedRequestsForTheLastTwelveHours = receivedRequestsForTheLastTwelveHours;
    }

    public int getPercentageOfFailedExecutions() {
        return ((int)(getFailedExecutions() * 100.0 / getTotalExecutions() + 0.5));
    }

    /*
     * Utility methods to format data
     */

    public String getFormattedLastThreeDays() {
        return join(getLastThreeDays(), " ");
    }

    public String getFormattedSucceededExecutionsForTheLastThreeDays() {
        return join(getSucceededExecutionsForTheLastThreeDays(), " ");
    }

    public String getFormattedFailedExecutionsForTheLastThreeDays() {
        return join(getFailedExecutionsForTheLastThreeDays(), " ");
    }

    public String getFormattedLastTwelveHours() {
        return join(getLastTwelveHours().stream().map(LocalDateTime::getHour).collect(toList()), "h ");
    }

    public String getFormattedReceivedRequestsForTheLastTwelveHours() {
        return join(getReceivedRequestsForTheLastTwelveHours(), " ");
    }

    private <T> String join(List<T> elements, String delimiter) {
        return elements.stream().map(Object::toString).collect(joining(delimiter));
    }
}
