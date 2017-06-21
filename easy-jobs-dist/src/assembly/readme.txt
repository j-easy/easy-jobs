Easy Jobs
=========

1. What is Easy Jobs ?
======================

Easy Jobs is a simple stupid job server for Java. It allows to you to define jobs and request their executions through a restful API.

2. How does it work ?
=====================

Easy Jobs stores meta-data in a relational database. The following tables are used:

* job : defines jobs
* job_request : contains job requests
* job_execution : contains job executions

The job server will poll the job_request table regularly and submit a job for each request.
The job server uses a pool of worker threads to execute jobs.

3. How to use it ?
==================

3.1 Develop your jobs
3.2 Put compiled classes (or packaged jars) in the "jobs" folder
3.3 Start the server from the root directory with: java -cp "lib/*:jobs/*:drivers/h2/*" org.jeasy.jobs.server.JobServer
3.4 Request job execution through the rest API: curl -X POST -d 'jobId=1&params=name=world' localhost:8080/requests

In step 3.3, you can choose which database to use by including one of the supported drivers and add it to the classpath.