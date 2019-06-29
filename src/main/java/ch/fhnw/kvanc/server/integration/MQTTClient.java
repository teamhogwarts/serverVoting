package ch.fhnw.kvanc.server.integration;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hivemq.client.mqtt.MqttClient;
import com.hivemq.client.mqtt.mqtt3.Mqtt3AsyncClient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import ch.fhnw.kvanc.server.domain.Vote;

/**
 * MQTTClient
 */
@Component
public class MQTTClient {
    private Logger logger = LoggerFactory.getLogger(MQTTClient.class);

    private Mqtt3AsyncClient mqttClient;

    @Value("${mqtt.server.host:mqtt.flespi.io}")
    private String serverHost;

    @Value("${mqtt.server.port:80}")
    private int serverPort;

    @Value("${mqtt.device.password}")
    private String password;

    @Value("${mqtt.device.username}")
    private String username;

    private AtomicBoolean isConnected = new AtomicBoolean(false);

    ObjectMapper mapper = new ObjectMapper();

    @PostConstruct
    public void afterPropertiesSet() {
        mqttClient = MqttClient.builder().useMqttVersion3().identifier("server_" + UUID.randomUUID().toString())
                .serverHost(serverHost).serverPort(serverPort).useWebSocketWithDefaultConfig().buildAsync();
        connect();
    }

    @PreDestroy
    public void tearDown() {
        if (isConnected.get() && mqttClient != null) {
            mqttClient.disconnect();
        }
    }

    private void connect() {
        mqttClient.connectWith().simpleAuth().username(username).password(password.getBytes()).applySimpleAuth().send()
                .whenComplete((mqtt3ConnAck, throwable) -> {
                    if (throwable != null) {
                        logger.error("MQTT connection error:{} ", throwable.getMessage());
                    } else {
                        this.isConnected.set(true);
                        logger.info("Successfully connected to '{}''", serverHost);
                    }
                });
    }

    private void publish(int yes, int no) {
        try {
            JsonNode node = mapper.createObjectNode();
            ((ObjectNode) node).put("yes", yes);
            ((ObjectNode) node).put("no", no);
            if (isConnected.get()) {
                mqttClient.publishWith().topic("voting")
                        .payload(mapper.writeValueAsString(node).getBytes(StandardCharsets.UTF_8)).send()
                        .whenComplete((mqtt3Publish, throwable) -> {
                            if (throwable != null) {
                                logger.error("Publishing data failed: {}", throwable.getMessage());
                                this.isConnected.set(false);
                                while (!isConnected.get()) {
                                    try {
                                        connect();
                                        Thread.sleep(100);
                                    } catch (InterruptedException e) {
                                        logger.error("Interrupted");
                                    }
                            }
                            // try again
                            publish(yes, no);
                        } else {
                            logger.debug("Sent MQTT message: yes {}, no {} ", yes, no);
                        }
                    });
            }
        } catch (Exception e) {
            logger.error("Failed to serialize mqtt messages", e);
        }
    }

    public void publish(List<Vote> votes) {
        int yes = 0;
        int no = 0;
        for (Vote vote : votes) {
            if (vote.isTrue()) {
                yes++;
            } else {
                no++;
            }
        }
        publish(yes, no);
    }

    public void reset() {
        publish(0, 0);
    }

}