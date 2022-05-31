# 💡 Topic

- A collection of code written throughout the course Parallel, Concurrent, and Distributed Programming

# 📝 Summary

This is a compilation of the reports and code I've produced during my Parallel, Concurrent, and Distributed Programming course (08.2021-12.2021). 
The topics covered include: 

- Functional and Imperative Programming in Scala
- Mutual Exclusion
- Concurrent Objects
- Reasoning about Consensus in Shared Memory
- Read-Modify-Write Operations
- Spin Locks
- Monitors and Semaphores
- Read-Write Locks
- Concurrent Queues; Stacks and Elimination

- Skiplists
- Data Race Detection in Practice
- Futures in Java
- Futures and Asynchronous Computations in Scala
- Using and Implementing Data-Parallel Collections
- Actor-Based Concurrency and Actor Supervision
- Distributed Consensus; Paxos

# 🛠 Tech Stack

`Scala`, `Java`, `akka`

# 🤚🏻 Part

- I have written all files/code except those that specify another author (`@author ...`)

# 🤔 Learned

- I gained a basic understanding of functional programming and its distinct convenience and versatility
- I learned more about the components of computers and programming languages while analyzing bugs and phenomena unique to concurrent programs (i.e. those that cannot 
occur in sequential programs)
- I experienced the various use cases of parallel, concurrent, and distributed programming and their respective pros-and-cons

# 📖 Open-Source Analysis: Apache Tomcat

I wrote a [report](YSC4231-data-race-report.pdf) that reviews the source code of [Apache Tomcat](https://github.com/apache/tomcat), a project that utilizes concurrent/parallel programming. 
In the report, I search for potential data races in their code and suggest solutions to resolve them.

# 📚 Distributed, Fault-Tolerant Replicated Key-Value Store

As the final project for Distributed Programming, I implemented a distributed, fault-tolerant key-value store 
([source code](final-kvstore-binchoi)) and wrote a [report](final-kvstore-binchoi/YSC4231-final-report.pdf) that narrates the development process.

#### Sections
* Theory Assignment 1 (Intro)
* Programming Assignment 1 (Scala Basics)
* Theory Assignment 2 (Mutual Exclusion)
* Programming Assignment 2 (Mutual Exclusion)
* Theory Assignment 3 (Concurrent Objects)
* Programming Assignment 3 (Blocking Synchronisation)
* Programming Assignment 4 (Concurrent Lists)
* Midterm (Thread Pool)
* Research Mini-Project (Detecting Data Races in Java Projects)
* Programming Assignment 5 (Futures and Promises)
* Programming Assignment 6 (Parallel Collections)
* Programming Assignment 7 (Actors)
* Final Project (Distributed Key-Value Store)
