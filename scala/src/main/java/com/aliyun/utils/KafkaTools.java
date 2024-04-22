package com.aliyun.utils;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static com.aliyun.utils.CommonConstans.KAFKA_DOCKER_BOOTSTRAP_SERVERS;

public class KafkaTools {
    private static final Logger LOG = LoggerFactory.getLogger(KafkaTools.class);

    public String bootstrapServers;

    public KafkaTools(String bootstrapServers) {
        this.bootstrapServers = bootstrapServers;
    }

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        // System.out.println(System.getProperty("file.encoding"));
        String[] s = new String[]{"{\"userName\":\"赵四31\",\"pwd\":\"lisi\",\"age\":13}",
                "{\"userName\":\"赵四41\",\"pwd\":\"lisi\",\"age\":14}",
                "{\"userName\":\"赵四51\",\"pwd\":\"lisi\",\"age\":15}"};
        // KafkaTools.sendMessage("logstest",
        // "{\"userName\":\"赵四\",\"pwd\":\"lisi\",\"age\":13}");
        /*
         * for (String a : s) { System.out.println(a); Thread.sleep(3000);
         * KafkaTools.sendMessage(topicName, jsonMessages); }
         */
        PropertiesUtil propertiesUtil = new PropertiesUtil("my.properties");
        new KafkaTools(propertiesUtil.get(KAFKA_DOCKER_BOOTSTRAP_SERVERS)).sendMessage("logstest", "{\"userName\":\"赵四31\",\"pwd\":\"lisi\",\"age\":13}");
    }

    /**
     * 私有静态方法，创建Kafka生产者
     *
     * @return KafkaProducer
     * @author IG
     * @Date 2017年4月14日 上午10:32:32
     * @version 1.0.0
     */
    public KafkaProducer<String, String> createProducer() {
        Properties properties = new Properties();
        properties.put("bootstrap.servers", getBootstrapServers());// 声明kafka
        // properties.put("value.serializer",
        // "org.apache.kafka.common.serialization.ByteArraySerializer");
        // properties.put("key.serializer",
        // "org.apache.kafka.common.serialization.ByteArraySerializer");
        properties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        properties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        return new KafkaProducer<String, String>((properties));
    }

    /**
     * 传入kafka约定的topicName,json格式字符串，发送给kafka集群
     *
     * @param topicName
     * @param jsonMessage
     * @author IG
     * @Date 2017年4月14日 下午1:29:09
     * @version 1.0.0
     */
    public Boolean sendMessage(String topicName, String jsonMessage) {
        Boolean flag = false;
        KafkaProducer<String, String> producer = createProducer();
        Future<RecordMetadata> metadataFuture = producer.send(new ProducerRecord<String, String>(topicName, jsonMessage));
        producer.flush();
        try {
            RecordMetadata metadata = metadataFuture.get();
        } catch (Exception e) {
            LOG.error(e.getMessage());
        } finally {
            producer.close();
        }
        return flag;
    }

    public String getBootstrapServers() {
        return bootstrapServers;
    }

    public void setBootstrapServers(String bootstrapServers) {
        this.bootstrapServers = bootstrapServers;
    }
}