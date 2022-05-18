### Key-Value Store
Replicated Distributed Key-Value Store

# Bin's personal Notes: 

# Client Interaction with Key-Value store
—> 1. Update (insert/remove)   2. Lookup
* Clients contacting PRIMARY NODE (directly) may use all operations on the key-value store
* Clients contacting SECONDARY NODES can ONLY use lookup

## Update Commands (@ Primary Node ONLY)

### Insert(key, value, id) 
* “Primary node, please add this (key, value) pair into the store AND replicate it to the secondaries”
* Id => client-chosen unique identifier for this request
* REPLY: OperationAck(id)
* FAILURE: OperationFailure(id)
    * FAILURE = inability to confirm the operation within 1 second
    * ADD MORE INFO HERE AFTER SECTION 10 

### Remove(key, id)
* “Primary node, please remove this key (and its corresponding value) from the storage (primary) and then from the secondaries (BOTH)
* REPLY: OperationAck(id)
* FAILURE: OperationFailure(id)

### Get(key, id) [LOOKUP]
* “Hey primary/secondary node, please look up the ‘current’ value assigned with this key in the storage 
* REPLY: GetResult(key, valueOption, id) 
    * valueOption = Some(value) or None [if key is not present in the replica]

# System Behaviour and Guarantees of Consistency

e.g. 
Insert("key1", "a", 100) 
Insert("key2", "1", 200) 
Insert("key1", "b", 300) 
Insert("key2", "2", 400) 
“Waiting for successful ack before proceeding with next” 
## Message Ordering guarantee (for clients of PRIMARY REPLICA) 
* Should NOT see: 
    * key1 contain b => key1 contain a (BAD b/c a was written before b) 
    * I.E. UPDATES should be seen in order 
* CAN see: 
    * key1:b => key2:1
    * key2:2 => key1:a
    * I.E. Ordering guarantee only applies between read/write to the SAME KEY and NOT across keys

## Message Ordering guarantee (for clients of SECONDARY REPLICA) 
* Note: during a conversation, the replica does not change
* SAME RULES AS PRIMARY and … 
* Client reading from secondary should EVENTUALLY READ: 
    * key1:b
    * key2:2

## Message Ordering guarantee (for clients messaging DIFFERENT REPLICAS)
* Client MAY observe different values during time window when an update is disseminated 
* BUT as stated above, all replicas must EVENTUALLY READ: 
    * key1:b
    * key2:2
* KEY TERM: Eventual Consistency

## Durability Guarantees of updates for clients messaging the PRIMARY replica
* Insert/Remove must be responded with either OperationAck(id) or OperationFailure(id) 
* This response message must be sent within at MOST 1 second after the update command was processed ==> Use ActorSystem’s timer resolution

### A positive OperationAck reply must be sent as soon as… 
* The requested change has been handed down to the PERSISTENCE actor class (provided) and a CORRESPONDING ACK has been received from it
    * NOTE: persistence module fails randomly from time to time —> TASK: keep it alive while RETRYING unACKed persistence operations until they succeed
* Replication of the requested change has been INITIATED and all SECONDARY replicas have ACKed the replication of the update 
    * If replicas (SECONDARY) leave the cluster [I.e. by sending a new Replicas message to PRIMARY], the outstanding ACKs of these replicas must be waived
        * Can lead to the generation of an OperationAck triggered indirectly by the Replicas message [to leave the cluster]
* NEGATIVE OperationFailure reply is sent if the above two conditions for sending OperationAck are not met within 1 second maximum response time

## Consistency in situation of FAILED PERSISTENCE or FAILED REPLICATION
* If the most recent write fails (i.e. OperationFailure is returned), replication may have succeeded in some replicas and not in others (UNSTABLE STATE). 
    * Eventual Consistency property is waived in this situation (for simplification of the project) 

## Result for a pending update
Insert("key1", "a", 100) 
<await confirmation> 
Insert("key1", "b", 300) 
Get("key1") 
* Reply for the last two requests may arrive in ANY order and the reply of GET => can be either ‘a’ or ‘b’

# The Mediator
* DEF: EXTERNAL SUBSYSTEM (Provided) with following simple protocol: 
    * New replicas (replica nodes) must first SEND a JOIN MESSAGE to the Mediator signalling that they are ready to be used
        * Response from Mediator: 
            * JoinedPrimary - for first node to join 
            * JoinedSecondary - for subsequent nodes that join
    * Replicas Message (to PRIMARY replica) whenever it receives the Join message
        * Contains set of available replica nodes including the PRIMARY and ALL SECONDARIES

# The Replicas
1. Send Join message to Mediator
2. Receive JoinedPrimary OR JoinedSecondary => CHOOSE ACCORDING BEHAVIOUR (perhaps .become(primary) is used? ) 

## Primary Replica features
* Accepts UPDATE and LOOKUP operations from clients 
* Must replicate changes to the secondary replicas of the system
* Respond to changes in membership (whenever it receives a Replicas message from the mediator) 
    * Start replicating to newly joined nodes. Following steps:
        * Receives Replicas message (of new replica ’s join) 
        * Allocate new Actor of type REPLICATOR for the new replica
    * Stop replicating to nodes that have left - and terminating it (section 7)
        * Corresponding REPLICATOR must be terminated

## Secondary Replica features
* Accepts LOOKUP Operation (Get) from clients
* Must accept replication events, updating their current state (section 7) 

# Replication Protocol
* Aim: Synchronise between nodes
* New actor class REPLICATOR: 
    * Aim: accept update events and propagate the changes to its corresponding replica (i.e. exactly 1-to-1 matching between replicator and secondary replica)
    * When replicator is being created, the PRIMARY REPLICA must forward UPDATE events for EVERY KEY-VALUE PAIR it currently holds to the replicator 
* Two pairs of messages

## Replication Protocol pt. 1: Replicate - Replicated (PRIMARY REPLICA —> REPLICATORS)
* Aim: (PRIMARY) replica actor => replicator
* Replicate(key, valueOption, id)
    * Sent by primary replica to replicator to initiate the replication of given update to the key
        * INSERT: valueOption => Some(value)
        * REMOVE: valueOption => None
* Replicated(key, id)
    * Sent as reply once replication that update is completed 
    * See: SnapshotAck

## Replication Protocol pt. 2: Snapshot - SnapshotAck
* Aim: used by replicator when communicating with its partner replica (SECONDARY) 
* Snapshot(key, valueOption, seq)
    * Sent by the corresponding Replicator to the appropriate SECONDARY replica to indicate a new state of the given key 
        * INSERT: valueOption => Some(value)
        * REMOVE: valueOption => None
    * Sender reference of snapshot messages MUST be the Replicator actor (NOT PRIMARY)
    * seq: SEQUENCE NUMBER to enforce total ordering between the updates
        * Updates for a given SECONDARY replica must be processed in contiguous ascending sequence number order
        * Each replicator uses its own number sequence (keeps its own id counter) starting at 0
        * If GREATER THAN EXPECTED seq arrives => that snapshot must be IGNORED (no state change; no reaction)
        * IF LESS THAN EXPECTED seq arrives => IGNORED AND ACKNOWLEDGED: 
* SnapshotAck(key, seq) 
    * Sent by secondary replica as soon as the update is PERSISTED LOCALLY by the secondary replica
        * Replica might never send this reply if it is unable to persist the update
    * ACK is sent IMMEDIATELY for requests whose seq number is less than the next expected number
    * Expected Number is the maximum of: 
        * Previously expected number (in case of receiving less than expected seq number) 
        * The sequence number just ack’ed, incremented by one 
* REPLICATOR may handle multiple snapshots of a given key in PARALLEL (i.e. replication has been initiated but not yet completed)
    * It is allowed to batch changes before sending them to the secondary replica
    * e.g. REPLICATOR RECEIVES: Replicate("a_key", Some("value1"), id1)  & Replicate("a_key", Some("value2"), id2) before it got to send Snapshot message for a_key to its replica
    * Then, these two messages could then result in : Snapshot("a_key", Some("value2"), seq) — skipping the state where a_key contains the value value1
* MUST CONSIDER: case where either Snapshot message or its corresponding SnapshotAck msg is LOST on the way
    * Replicator must PERIODICALLY RETRANSMIT all unACK’ed changes 
    * Every 100 milliseconds (msg is lost(?) or retransmission)
    * A lost Snapshot message will lead to a resend at most 200 milliseconds after the Replicate request was received 
        * Use ActorSystem’s scheduler service

# PERSISTENCE
* Each replica submits incoming UPDATES to the local PERSISTENCE Actor and wait for its ACK before confirming the update to teh requester
    * Primary: 
        * requester = client which sent an insert/remove 
        * Confirmation = OperationAck
    * Secondary: 
        * Requester = Replicator (sending a Snapshot) 
        * Confirmation = SnapshotAck
* MESSAGE TYPES: 
    * Persist(key, valueOption, id) 
        * Sent to Persistence actor to request the given state to be persisted 
    * Persisted(key, id)
        * Sent by Persistence actor as reply in case the corresponding request was successful 
            * Failure => NO REPLY
* PROVIDED Persistence implementation is UNRELIABLE: 
    * Fails with exception and not ack current request
        * REPLICA actor must CREATE and appropriately SUPERVISE the Persistence actor 
            * Can experiment with different designs based on resuming, restarting, or stopping and recreating the Persistence actor (using Akka’s mechanism for actor supervision)  - https://doc.akka.io/docs/akka/2.5.26/general/supervision.html
    * The replica’s code below restarts the failed persistence actor when necessary: 
        * override def supervisorStrategy: OneForOneStrategy = OneForOneStrategy() { case _: PersistenceException => Restart}
    * EXPECTATION: Persist is retired before the 1 second response timeout in case persistence failed
    * Persist message id must match that which was used in the first request for this particular update
