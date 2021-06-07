package canal;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.ProducerConfig;

import redis.clients.jedis.Jedis;
import com.alibaba.fastjson.JSON;

public class SyncKafkaRedis {

	public static void SetRedis(String value, Jedis redisClient) {
        //value = "{\"data\":[{\"id\":\"5\",\"username\":\"liujinyuan\",\"password\":\"sfasfeddf\",\"addr\":\"changan\",\"phone\":\"18929323222\",\"nickname\":\"jinyuan\",\"__#alibaba_rds_row_id#__\":\"7\"}],\"database\":\"labex\",\"es\":1621584521000,\"id\":723,\"isDdl\":false,\"mysqlType\":{\"id\":\"int8\",\"username\":\"varchar(20)\",\"password\":\"varchar(20)\",\"addr\":\"varchar(40)\",\"phone\":\"varchar(11)\",\"nickname\":\"varchar(12)\",\"__#alibaba_rds_row_id#__\":\"bigint\"},\"old\":null,\"pkNames\":[\"__#alibaba_rds_row_id#__\"],\"sql\":\"\",\"sqlType\":{\"id\":-5,\"username\":12,\"password\":12,\"addr\":12,\"phone\":12,\"nickname\":12,\"__#alibaba_rds_row_id#__\":-5},\"table\":\"user\",\"ts\":1621584521441,\"type\":\"INSERT\"}";
        CanalBean canalBean = JSON.parseObject(value, CanalBean.class);
        boolean isDdl = canalBean.isDdl();
        String type = canalBean.getType();
        String database = canalBean.getDatabase();

        if ("labex".equals(database) && !isDdl) {
        	List<User> users = canalBean.getData();
        	if ("INSERT".equals(type)) {
                for (User user : users) {
                    String id = user.getId();
                    redisClient.set(id, JSON.toJSONString(user));
                }
            } else if ("UPDATE".equals(type)) {
                for (User user : users) {
                	String id = user.getId();
                    redisClient.set(id, JSON.toJSONString(user));
                }
            } else {
            	for (User user : users) {
            		String id = user.getId();
                    // Delete from Redis
                    redisClient.del(id);
                }
            }
        	System.out.println("rsync success!!");
        }
	}

	public static void main(String[] args) {
		Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, args[0]); // kafka addr
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, 30000);  
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 30);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, args[2]);  // group id    agrs[2]
        KafkaConsumer<String, String> consumer = new KafkaConsumer<String, String>(props);
        List<String> subscribedTopics =  new ArrayList<String>();
        String topicStr = args[1];  // topic   args[1]
        String[] topics = topicStr.split(",");
        for (String topic: topics) {
            subscribedTopics.add(topic.trim());
        }
        consumer.subscribe(subscribedTopics);

        Jedis jedis = new Jedis(args[3], 6379);  // redis addr args[3]
        jedis.auth(args[4]);   // password args[4]
        while (true){
            try {
                ConsumerRecords<String, String> records = consumer.poll(10);
                for (ConsumerRecord<String, String> record : records) {
                	String content = record.value();
                    System.out.println(String.format("Consume partition:%d offset:%d content: %s", record.partition(), record.offset(), record.value()));
                    SetRedis(content, jedis);
                }
            } catch (Exception e) {
                try {
                	System.out.println(e);
                    Thread.sleep(1000);
                    jedis.close();
                } catch (Throwable ignore) {

                }
                e.printStackTrace();
            }
        }
	}
}
