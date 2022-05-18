# 💡 Topic

- 평행, 동시, 분산 프로그래밍의 기초를 다룬 심화 수업에서 작성한 코드 모음집

# 📝 Summary

3학년 1학기에 수강한 평행, 동시, 분산 프로그래밍 수업에서 작성한 코드와 리포트 모음집으로 다음과 같은 개념들과 주제들을 다루었습니다: 

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

- `@author ...` 주석이 달리지 않은 코드는 모두 제가 작성한 코드입니다

# 🤔 Learned

- 기초적인 함수형 프로그래밍에 대해 배우고 사용해보며 함수형 프로그래밍 특유의 편리성과 유연함을 경험함
- 순차적 프로그래밍에선 경험해보지 못했던 예측하기 어려운 에러들을 분석하고 디버깅하며 컴퓨터와 프로그래밍 언어의 component들에 대한 이해도를 높임
- 평행, 동시, 분산 프로그래밍의 다양한 응용과 장단점에 대해 배움

# 📖 Open-Source Analysis: Apache Tomcat

수업에서 배운 개념들을 토대로 평행/동시 프로그래밍을 사용하는 프로젝트 **Apache Tomcat**의 [source code](https://github.com/apache/tomcat)를 리뷰하고 데이터 레이스 오류를 찾아 해결하는 [리포트](YSC4231-data-race-report.pdf)를 작성하였습니다.

# 📚 Distributed, Fault-Tolerant Replicated Key-Value Store

분산 프로그래밍의 마지막 프로젝트로 **분산형 결함허용 key-value 저장소**를 [구현](final-kvstore-binchoi)하고 그에 대한 [리포트](final-kvstore-binchoi/YSC4231-final-report.pdf)를 작성하였습니다.

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
