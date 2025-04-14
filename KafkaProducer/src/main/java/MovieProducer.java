import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class MovieProducer {
    public static void main(String[] args) throws ExecutionException, InterruptedException {

        // Producer Configs : https://kafka.apache.org/documentation.html#producerconfigs
        Properties properties = new Properties();
        properties.put("bootstrap.servers","localhost:9092");
        properties.put("key.serializer","org.apache.kafka.common.serialization.StringSerializer");
        properties.put("value.serializer","org.apache.kafka.common.serialization.StringSerializer");
        properties.put("linger.ms",1);
        properties.put("partitioner.class","CustomPartitioner");

        // Check acks, partitioner.class, batch.size

        Producer<String, String> movieProducer = new KafkaProducer<>(properties);

        List<Future<RecordMetadata>> recordMetadataList = new ArrayList<>();
        for(int i=0; i<15; i++)
            recordMetadataList.add(movieProducer.send(new ProducerRecord<String, String>("movie",String.valueOf(i),"Movie : " + i)));

        // On broker, there will be a log file corresponding to each partition which will store the messages
        for(int i=0; i<15; i++){
            RecordMetadata recordMetadata = recordMetadataList.get(i).get();
            System.out.printf("Record %s details :  partition : %s offset : %s \n",i, recordMetadata.partition(),recordMetadata.offset() );
        }

        // Messages are stored in Broker based on the property retention.ms corresponding to topic and by default it is 604800000 (7 days)
        movieProducer.close();
    }
}
