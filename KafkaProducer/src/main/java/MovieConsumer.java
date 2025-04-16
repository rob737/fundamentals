import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.Properties;

// The consumer is not thread-safe so it the responsibility of user to synchronize critical section accordingly.
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
    *
    * Setting enable.auto.commit means that offsets are committed automatically with a frequency controlled by the config auto.commit.interval.ms.
    * The deserializer settings specify how to turn bytes into objects. For example, by specifying string deserializers, we are saying that our record's key and value will just be simple strings.
    *
    * Manual Offset Control :
    * Instead of relying on the consumer to periodically commit consumed offsets,
    * users can also control when records should be considered as consumed and hence commit their offsets.
    * This is useful when the consumption of the messages is coupled with some processing logic
    * and hence a message should not be considered as consumed until it is completed processing.
    * We can use consumer.commitSync(); to facilitate this behaviour.
    *
    * Note: The committed offset should always be the offset of the next message that your application will read.
    * Thus, when calling commitSync(offsets) you should use nextRecordToBeProcessed.offset()
    * or if ConsumerRecords is exhausted already ConsumerRecords.nextOffsets() instead.
    * You should also add the leader epoch as commit metadata,
    * which can be obtained from ConsumerRecord.leaderEpoch() or ConsumerRecords.nextOffsets().
    *
    *
    * from chat gpt :
    *Consumer offsets are stored on the Kafka brokers, specifically in the internal topic __consumer_offsets, which is managed just like any other Kafka topic.
    *Offset is in key-value pair where key is a Combination of consumer group ID, topic, and partition
    *and value is the actual offset value and metadata like timestamp.
    * Continue : Manual Partition Assignment
    * */

    public static void main(String[] args) throws InterruptedException {
        Properties properties = new Properties();
        properties.setProperty("bootstrap.servers","localhost:9092");
        properties.setProperty("group.id","Bollywood");
        properties.setProperty("enable.auto.commit","false");
        properties.setProperty("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        properties.setProperty("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        properties.setProperty("auto.offset.reset","earliest");

        KafkaConsumer<String,String> kafkaConsumer = new KafkaConsumer<String, String>(properties);
// How is it deciding which partition to read from ?
        /*
        * There is something called as partition assignment strategy.
        * Assignment strategies that the client will use to distribute partition ownership amongst consumer instances
        * when group management is used.
        * Default in Kraft mode is CooperativeStickyAssignor.
        *
        * Read : https://kafka.apache.org/documentation/#consumerconfigs_partition.assignment.strategy
        * */
        kafkaConsumer.subscribe(Collections.singletonList("movie"));
/*
* Doubt : 1. what would happen if next offset is data which is irrelevant for the assigned consumer but producer
* has produced it to the corresponding partition?
* Ans : It is upto Consumer's application logic to filter out irrelevant message.
* 2. One consumer with many partitions then in which order it would read?
* Ans : Order of read is guaranteed within the partition but order of read among partition is not guaranteed.
* */
        while(true){ // Why is it not reading any data ?
            ConsumerRecords<String,String> records = kafkaConsumer.poll(Duration.ofMillis(100));

            System.out.println("Assigned Partitions: " + kafkaConsumer.assignment());
/*
* Topic __consumer_offsets is the topic that stores the offset based on hashing consumer group id, by default it has 50
* partitions.
* However, based on the hashId of the consumer group it would store the offsets of a particular group in a particular
* partition and it always stores the most recent offset.
*
* In case a consumer subscribes to multiple partitions,
* then offset in a partition is stored in the form of (consumer_group_id, topic, partition).,
* it is stored in logs and then transformed into concurrent hashmap with above value as key and offset metadata
* related information as object in value.
*
* Class: GroupMetadataManager Package: kafka.coordinator.group ey method: loadGroupsAndOffsets() → used to restore state from __consumer_offsets
*Note : Kafka doesn’t track offsets per consumer, it tracks offsets per consumer group per topic-partition.
*
* Very important : Each partition of a topic is assigned to exactly one consumer within a consumer group.
*
* Kafka does not store the consumer ID in the __consumer_offsets topic
*
* You can visualize the contents using the command :
*
* ./kafka-console-consumer.sh --topic __consumer_offsets --bootstrap-server localhost:9092 --from-beginning --max-messages 10 --formatter org.apache.kafka.tools.consumer.OffsetsMessageFormatter
*
* O/p something like :
*
* {
    "key": {
      "type": 1,
      "data": {
        "group": "Bollywood",
        "topic": "movie",
        "partition": 2
      }
    },
    "value": {
      "version": 3,
      "data": {
        "offset": 17,
        "leaderEpoch": -1,
        "metadata": "",
        "commitTimestamp": 1744685763835
      }
    }
  }
 * */
            for(ConsumerRecord<String,String> record : records)
                System.out.println(record);

            Thread.sleep(100);
            kafkaConsumer.commitSync();
            System.out.println("Completed Reading");
        }
    }

    /*
    * Output ::::
    *
    *
    * Assigned Partitions: [movie-1, movie-0, movie-5, movie-4, movie-3, movie-2, movie-6]
ConsumerRecord(topic = movie, partition = 1, leaderEpoch = 26, offset = 73, CreateTime = 1744777540387, deliveryCount = null, serialized key size = 1, serialized value size = 9, headers = RecordHeaders(headers = [], isReadOnly = false), key = 1, value = Movie : 1)
ConsumerRecord(topic = movie, partition = 0, leaderEpoch = 26, offset = 5, CreateTime = 1744777540389, deliveryCount = null, serialized key size = 1, serialized value size = 9, headers = RecordHeaders(headers = [], isReadOnly = false), key = 7, value = Movie : 7)
ConsumerRecord(topic = movie, partition = 5, leaderEpoch = 26, offset = 9, CreateTime = 1744777540388, deliveryCount = null, serialized key size = 1, serialized value size = 9, headers = RecordHeaders(headers = [], isReadOnly = false), key = 5, value = Movie : 5)
ConsumerRecord(topic = movie, partition = 4, leaderEpoch = 26, offset = 5, CreateTime = 1744777540389, deliveryCount = null, serialized key size = 2, serialized value size = 10, headers = RecordHeaders(headers = [], isReadOnly = false), key = 11, value = Movie : 11)
ConsumerRecord(topic = movie, partition = 3, leaderEpoch = 26, offset = 17, CreateTime = 1744777540370, deliveryCount = null, serialized key size = 1, serialized value size = 9, headers = RecordHeaders(headers = [], isReadOnly = false), key = 0, value = Movie : 0)
ConsumerRecord(topic = movie, partition = 3, leaderEpoch = 26, offset = 18, CreateTime = 1744777540388, deliveryCount = null, serialized key size = 1, serialized value size = 9, headers = RecordHeaders(headers = [], isReadOnly = false), key = 2, value = Movie : 2)
ConsumerRecord(topic = movie, partition = 3, leaderEpoch = 26, offset = 19, CreateTime = 1744777540388, deliveryCount = null, serialized key size = 1, serialized value size = 9, headers = RecordHeaders(headers = [], isReadOnly = false), key = 3, value = Movie : 3)
ConsumerRecord(topic = movie, partition = 3, leaderEpoch = 26, offset = 20, CreateTime = 1744777540388, deliveryCount = null, serialized key size = 1, serialized value size = 9, headers = RecordHeaders(headers = [], isReadOnly = false), key = 4, value = Movie : 4)
ConsumerRecord(topic = movie, partition = 3, leaderEpoch = 26, offset = 21, CreateTime = 1744777540389, deliveryCount = null, serialized key size = 1, serialized value size = 9, headers = RecordHeaders(headers = [], isReadOnly = false), key = 6, value = Movie : 6)
ConsumerRecord(topic = movie, partition = 3, leaderEpoch = 26, offset = 22, CreateTime = 1744777540389, deliveryCount = null, serialized key size = 1, serialized value size = 9, headers = RecordHeaders(headers = [], isReadOnly = false), key = 8, value = Movie : 8)
ConsumerRecord(topic = movie, partition = 3, leaderEpoch = 26, offset = 23, CreateTime = 1744777540389, deliveryCount = null, serialized key size = 2, serialized value size = 10, headers = RecordHeaders(headers = [], isReadOnly = false), key = 10, value = Movie : 10)
ConsumerRecord(topic = movie, partition = 3, leaderEpoch = 26, offset = 24, CreateTime = 1744777540389, deliveryCount = null, serialized key size = 2, serialized value size = 10, headers = RecordHeaders(headers = [], isReadOnly = false), key = 12, value = Movie : 12)
ConsumerRecord(topic = movie, partition = 3, leaderEpoch = 26, offset = 25, CreateTime = 1744777540390, deliveryCount = null, serialized key size = 2, serialized value size = 10, headers = RecordHeaders(headers = [], isReadOnly = false), key = 14, value = Movie : 14)
ConsumerRecord(topic = movie, partition = 2, leaderEpoch = 26, offset = 17, CreateTime = 1744777540389, deliveryCount = null, serialized key size = 1, serialized value size = 9, headers = RecordHeaders(headers = [], isReadOnly = false), key = 9, value = Movie : 9)
ConsumerRecord(topic = movie, partition = 6, leaderEpoch = 26, offset = 9, CreateTime = 1744777540389, deliveryCount = null, serialized key size = 2, serialized value size = 10, headers = RecordHeaders(headers = [], isReadOnly = false), key = 13, value = Movie : 13)
Completed Reading
    *
    *
    * */

}
