import org.apache.kafka.clients.producer.Partitioner;
import org.apache.kafka.common.Cluster;

import java.util.Map;

public class CustomPartitioner implements Partitioner {
    // 2nd paramter represents key
    // first paramter represents topic name
    @Override
    public int partition(String s, Object o, byte[] bytes, Object o1, byte[] bytes1, Cluster cluster) {
        System.out.println("String S" + s);
        // group multiples of 2 together
        String key = o.toString();
        if(Integer.parseInt(key) % 2 == 0 )
            return 3;
        return Integer.parseInt(key) % 7;
    }

    @Override
    public void close() {

    }

    @Override
    public void configure(Map<String, ?> map) {

    }
}
