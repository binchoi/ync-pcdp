# YSC4231: Code for the Lectures

Yale-NUS College

Examples from the Lectures on Parallel, Concurrent and Distributed Programming (YSC4231)

## Building and Running

### Requirements 

* [Java SE Development Kit 11](https://www.oracle.com/sg/java/technologies/javase-jdk11-downloads.html)
* [Scala Build Tool](https://www.scala-sbt.org/), `sbt` (version >=1.1.6)
* [Scala](https://www.scala-lang.org/download/) (version >= 2.12.6) - to run the standalone artefact

### Building and Testing the Project

To compile and run the entire test suite, execute the following command in the terminal from the root folder of the project:

```
sbt test
```

To execute a sample script, run

```
sbt
```

and then, from the `sbt` console, run

```
runMain basic.HelloWorld
```

To exit the console, type `exit`.
