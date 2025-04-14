
// The consumer is not thread-safe.
public class MovieConsumer {

    /*
    * There are two types of position :
    * 1. position : it gives the next highest offset which can be consumed by the consumer.
    * 2. committed position : It stores the offset which is used as a checkpoint in case consumer crashes and on
    * getting reinstated can start consuming from this position (commitSync and commitAsync methods can be used for this)
    * */
}
