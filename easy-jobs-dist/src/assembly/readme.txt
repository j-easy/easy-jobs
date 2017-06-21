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
3.3 Write a deployment descriptor. Here is an example:

```
[
    {
      "id": 1,
      "name": "my job",
      "class": "org.mycompany.jobs.MyJob",
      "method": "doWork"
    }
]
```

3.4 Start the server from the root directory with: java -cp "lib/*:jobs/*" -Deasy.jobs.server.config.file=path/to/deployment/descriptor org.jeasy.jobs.server.JobServer . If you are on windows use ';' as classpath separator.
3.4 Request job execution through the rest API: curl -X POST -d 'jobId=1&params=name=world' localhost:8080/requests

By default, Easy Jobs uses H2 database. This is fine for testing but not suitable for production usage.
In step 3.4, you can choose which database to use by including its driver in the classpath. For example, to use MySQL database, you can use this command:

java -cp "lib/*:jobs/*:drivers/mysql/*" -Deasy.jobs.database.config.file=path/to/database.properties org.jeasy.jobs.server.JobServer

where database.properties contains the following properties:

```
easy.jobs.database.config.url=
easy.jobs.database.config.user=
easy.jobs.database.config.password=
```