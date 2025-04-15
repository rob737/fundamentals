
// The consumer is not thread-safe.
public class MovieConsumer {

    /*
    * There are two types of position :
    * 1. position : it gives the next highest offset which can be consumed by the consumer.
    * 2. committed position : It stores the offset which is used as a checkpoint in case consumer crashes and on
    * getting reinstated can start consuming from this position (commitSync and commitAsync methods can be used for this)
    * */

    /*
    * Each consumer is part of a consumer group identified by group.id.
    * No two consumers from the same consumer group will consume messages from the same partition of a topic
    * simultaneously.
    * Consumers first needs to subscribe to a topic where in they are assigned a consumer group and then they
    * poll the subscribed topic.
    *
    * After subscribing to a set of topics, the consumer will automatically join the group when poll(Duration) is invoked.
    *
    * Underneath the covers, the consumer sends periodic heartbeats to the server.
    * If the consumer crashes or is unable to send heartbeats for a duration of session.timeout.ms,
    * then the consumer will be considered dead and its partitions will be reassigned to some other consumers within the same consumer group.
    * */
}
