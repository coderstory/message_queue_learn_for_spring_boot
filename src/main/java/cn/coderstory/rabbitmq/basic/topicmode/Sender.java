package cn.coderstory.rabbitmq.basic.topicmode;

import cn.coderstory.rabbitmq.basic.ConnectionUtil;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

@Component("19")
public class Sender {
    private final ConnectionUtil connectionUtil;
    private final static String EXCHANGE_NAME = "EXCHANGE_NAME2";
    private final static String QUEUE_NAME = "fanout_exchange_queue_1";

    public Sender(ConnectionUtil connectionUtil) {
        this.connectionUtil = connectionUtil;
    }

    public void send() throws IOException, TimeoutException {
        try (Channel channel = connectionUtil.getInstance().createChannel()) {
            channel.queueDeclare(QUEUE_NAME, true, false, false, null);
            // 创建exchange name，type, durable
            channel.exchangeDeclare(EXCHANGE_NAME, "topic",true);

            AMQP.BasicProperties build = new AMQP.BasicProperties.Builder().deliveryMode(2).expiration(String.valueOf(5 * 60 * 1000)).build();

            String msg = "item.insert  this is a message";
            channel.basicPublish(EXCHANGE_NAME, "item.insert", build, msg.getBytes());


              msg = "pp.insert  this is a message";
            channel.basicPublish(EXCHANGE_NAME, "pp.insert", build, msg.getBytes());

            System.out.println(" [生产者] Sent '" + msg + "'");

        }


    }

}
