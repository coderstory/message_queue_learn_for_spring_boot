package cn.coderstory.rabbitmq.basic.workmode;

import cn.coderstory.rabbitmq.basic.ConnectionUtil;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.TimeoutException;
import java.util.stream.IntStream;

@Component("7")
public class Sender {
    private final ConnectionUtil connectionUtil;
    private final static String QUEUE_NAME = "work_queue";

    public Sender(ConnectionUtil connectionUtil) {
        this.connectionUtil = connectionUtil;
    }

    public void send() throws IOException, TimeoutException {
        try(Channel channel = connectionUtil.getInstance().createChannel()) {
            channel.queueDeclare("QUEUE_NAME", true, false, false, null);

            AMQP.BasicProperties build = new AMQP.BasicProperties.Builder().deliveryMode(2).expiration(String.valueOf(5 * 60 * 1000)).build();
            IntStream.range(1, 20).parallel().forEach(value -> {
                try {
                    String msg = value + "  this is a message";
                    channel.basicPublish("", QUEUE_NAME, build,msg.getBytes());
                    System.out.println(" [生产者] Sent '" + msg + "'");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }
}
