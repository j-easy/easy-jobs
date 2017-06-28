***

<div align="center">
    <b><em>Easy Jobs</em></b><br>
    The simple, stupid job server for Java&trade;
</div>

<div align="center">

[![MIT license](http://img.shields.io/badge/license-MIT-brightgreen.svg?style=flat)](http://opensource.org/licenses/MIT)
[![Downloads](https://img.shields.io/github/downloads/j-easy/easy-jobs/total.svg)]()
[![Release](https://img.shields.io/badge/release-v0.2-green.svg)](https://github.com/j-easy/easy-jobs/releases)
[![Gitter](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/j-easy/easy-batch)

</div>

***

## Latest news

* 29/06/2017: Version 0.2 is released with some bug fixes and few enhancements. See all changes in details [here](https://github.com/j-easy/easy-jobs/releases).
* 22/06/2017: Version 0.1 is out! See what this first version brings to the table [here](https://github.com/j-easy/easy-jobs/releases).

# What is Easy Jobs?

Easy Jobs is a simple job server for Java. It allows you to define jobs and request their executions through a restful API.

# How does it work?

Easy Jobs stores meta-data of jobs in a relational database. Three tables are used: `job`, `job_request` and `job_execution`.
The job server polls the `job_request` table regularly looking for pending job requests.
When a job request comes in, the job server creates a job instance of the requested job and execute it:

<p align="center">
    <img src="https://raw.githubusercontent.com/wiki/j-easy/easy-jobs/images/easy-jobs.png" width="80%">
</p>

The job server uses a pool of worker threads to execute jobs.
Job requests are submitted through a restful API.

# How to use it ?

[Download](https://github.com/j-easy/easy-jobs/releases) the latest release and unzip it. You should get a directory with the following content:

```shell
$>cd easy-jobs-dist-0.2
$>tree -d
├── conf
├── drivers
│   ├── h2
│   └── mysql
├── jobs
└── lib
```

Run the job server with the following command:

```
java -cp "drivers/h2/*:lib/*" \
 -Deasy.jobs.database.config.file=$(pwd)/conf/database.properties \
 -Deasy.jobs.database.config.init=true \
 -Deasy.jobs.server.config.jobs.directory=$(pwd)/jobs \
 -Deasy.jobs.server.config.jobs.descriptor=$(pwd)/conf/jobs.yml \
 org.jeasy.jobs.server.JobServer
```

If you are on windows, use the following command:

```
java -cp "drivers/h2/*;lib/*" \
 -Deasy.jobs.database.config.file=%cd%\conf\database.properties \
 -Deasy.jobs.database.config.init=true \
 -Deasy.jobs.server.config.jobs.directory=%cd%\jobs \
 -Deasy.jobs.server.config.jobs.descriptor=%cd%\conf\jobs.yml \
 org.jeasy.jobs.server.JobServer
```

That's it! The job server should be up and running waiting for you to submit job requests on `localhost:8080/requests`. We will see how to submit job requests in a minute.

In the previous command, we used H2 database which is fine for testing but not recommended for production. You can use another [supported database](https://github.com/j-easy/easy-jobs/wiki/database-support) if you want.

The distribution comes with a sample job called `HelloWorldJob` located in the `jobs` directory. Here is its source code:

```java
public class HelloWorldJob {

    private String name;

    public void doWork() {
        System.out.println("Hello " + name);
    }

    // getter and setter for name
}
```

Jobs in Easy Jobs are regular Java classes. There is no annotation to add, no interface to implement or class to extend.
Your jobs are simple POJOs. Easy Jobs is not intrusive! But you have to tell it where to find your job using a job descriptor:

```yaml
---
id: 1
name: hello world job
class: HelloWorldJob
method: doWork
```

This job descriptor `jobs.yml` can be found in `conf` directory. It gives Easy Jobs all required information to identify your job and execute it when requested.
Let's first check if the `HelloWorldJob` is registered:

```json
$>curl localhost:8080/jobs
[
 {
  "id": 1,
  "name": "Hello World Job"
 }
]
```

Cool, the job server has successfully loaded the job. Now, we can submit a job execution request:

```shell
$>curl \
  --request POST \
  --header "Content-Type: application/json" \
  --data '{"jobId":"1", "name":"world"}' \
  localhost:8080/requests
```

The job server will pick up this request in the next polling run, create a job instance of the `HelloWorldJob` and execute it with parameter `name=world`.
Let's check job executions on the `/executions` endpoint:

```json
$>curl localhost:8080/executions
[
 {
  "id": 1,
  "requestId": 1,
  "jobExecutionStatus": "FINISHED",
  "jobExitStatus": "SUCCEEDED",
  "startDate": [
      2017, 6, 23, 9, 25, 13, 939000000
  ],
  "endDate": [
      2017, 6, 23, 9, 25, 13, 959000000
  ]
 }
]
```

Great! the job has been executed and finished successfully. You should have seen this in the server's log:

```
INFO: Received a new job request for job 1 with parameters {jobId=1, name=world}
INFO: Found 1 pending job request(s)
INFO: Creating a new job for request n° 1 with parameters [{"jobId":"1", "name":"world"}]
INFO: Submitted a new job for request n° 1
INFO: Processing job request with id 1
Hello world
INFO: Successfully processed job request with id 1
```

That's all! You can find more details on how to configure the server in the [wiki](https://github.com/j-easy/easy-jobs/wiki).

## Contribution

You are welcome to contribute to the project with pull requests on GitHub.

If you find a bug or want to request a feature, please use the [issue tracker](https://github.com/j-easy/easy-jobs/issues).

For any further question, you can use the [gitter channel](https://gitter.im/j-easy/easy-jobs).

## Awesome contributors

* [nabilov](https://github.com/nabilov)

Thank you for your contributions!

## License

Easy Jobs is released under the terms of the MIT license:

```
The MIT License (MIT)

Copyright (c) 2017 Mahmoud Ben Hassine (mahmoud.benhassine@icloud.com)

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
```
