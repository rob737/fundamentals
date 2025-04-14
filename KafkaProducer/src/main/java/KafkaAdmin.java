import org.apache.kafka.clients.admin.*;

import java.util.Collections;
import java.util.Properties;

public class KafkaAdmin {
    public static void main(String[] args) {
        Properties config = new Properties();
        config.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");

        try {
            AdminClient kafkaAdminClient = AdminClient.create(config);

            NewTopic newTopic = new NewTopic("movie",7,(short) 1);

            CreateTopicsResult topicsResult = kafkaAdminClient.createTopics(Collections.singletonList(newTopic));

            topicsResult.all().get();

            System.out.println("Topic created : " + topicsResult.topicId("movie"));

        }catch (Exception ex){
            ex.printStackTrace();
        }
    }
}
