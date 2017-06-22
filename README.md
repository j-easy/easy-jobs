***

<div align="center">
    <b><em>Easy Jobs</em></b><br>
    The simple, stupid job server for Java&trade;
</div>

<div align="center">

[![MIT license](http://img.shields.io/badge/license-MIT-brightgreen.svg?style=flat)](http://opensource.org/licenses/MIT)
[![Downloads](https://img.shields.io/github/downloads/j-easy/easy-jobs/total.svg)]()
[![Release](https://img.shields.io/badge/release-v0.1-green.svg)](https://github.com/j-easy/easy-jobs/releases)
[![Gitter](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/j-easy/easy-batch)

</div>

***

## Latest news

* 22/06/2017: Version 0.1 is out! See what this first version brings to the table [here](https://github.com/j-easy/easy-jobs/releases).

# What is Easy Jobs?

Easy Jobs is a simple job server for Java. It allows you to define jobs and request their executions through a restful API.

# How does it work?

Easy Jobs stores meta-data of jobs in a relational database. Three tables are used: `job`, `job_request` and `job_execution`.
The job server polls the `job_request` table regularly looking for pending job requests.
When a job request comes in, the job server creates a job instance of the requested job and execute it:

![easy jobs](https://raw.githubusercontent.com/wiki/j-easy/easy-jobs/images/easy-jobs.png)

The job server uses a pool of worker threads to execute jobs.
Job requests are submitted through a restful API.

# How to use it ?

You can get up and running in a few steps:

### 1. Write your job

Jobs in Easy Jobs are regular Java classes. Here is an example:

```java
public class HelloWorldJob {

    private String name;

    public void doWork() {
        System.out.println("Hello " + name);
    }

    // getter and setter for name
}
```

Note there is no annotation to add, no interface to implement or class to extend.
Your jobs are simple POJOs. Easy Jobs is non-intrusive!

### 2. Download Easy Jobs server

[Download](https://github.com/j-easy/easy-jobs/releases) the latest release and unzip it. You should get a directory with the following content:

```shell
$>cd easy-jobs-dist-0.1
$>tree -d
├── drivers
│   ├── h2
│   └── mysql
├── jobs
└── lib
```

Put your compiled job classes (or packaged jars) in the `jobs` directory.
This is optional, all you need is to have them in the classpath when you run the server.

### 3. Write a deployment descriptor

Easy Jobs server needs to know which jobs you want to deploy and how to run them. For this, you need to write a job descriptor:

```json
[
    {
      "id": 1,
      "name": "Hello World Job",
      "class": "org.mycompany.jobs.HelloWorldJob",
      "method": "doWork"
    }
]
```

This job descriptor tells Easy Jobs which are job classes and methods to execute.

### 4. Start the server and submit job execution requests

To run the server, you can use the following command:

```
java -cp "lib/*:jobs/*:drivers/h2/*" \
 -Deasy.jobs.server.config.file=path/to/deployment/descriptor \
 -Deasy.jobs.server.config.database.init=true \
  org.jeasy.jobs.server.JobServer
```

The server should now be ready to accept job execution requests on `localhost:8080/requests`. Let's submit a job request:

```shell
$>curl -X POST --data '{"jobId":"1", "name":"world"}' localhost:8080/requests
```

The job server will pick up this request in the next polling run, create a job instance of the `HelloWorldJob` and execute it with parameter `name=world`.

That's it! You can find more details on how to configure the server in the [wiki](https://github.com/j-easy/easy-jobs/wiki).

## Contribution

You are welcome to contribute to the project with pull requests on GitHub.

If you find a bug or want to request a feature, please use the [issue tracker](https://github.com/j-easy/easy-jobs/issues).

For any further question, you can use the [gitter channel](https://gitter.im/j-easy/easy-jobs).

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
