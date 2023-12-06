package cn.coderstory.rabbitmq.basic.directmode;

import cn.coderstory.rabbitmq.basic.ConnectionUtil;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

@Component("15")
public class Sender {
    private final ConnectionUtil connectionUtil;
    private final static String EXCHANGE_NAME = "EXCHANGE_NAME1";
    private final static String QUEUE_NAME = "fanout_exchange_queue_1";

    public Sender(ConnectionUtil connectionUtil) {
        this.connectionUtil = connectionUtil;
    }

    public void send() throws IOException, TimeoutException {
        try (Channel channel = connectionUtil.getInstance().createChannel()) {
            channel.queueDeclare(QUEUE_NAME, true, false, false, null);
            // 创建exchange name，type
            channel.exchangeDeclare(EXCHANGE_NAME, "direct");

            AMQP.BasicProperties build = new AMQP.BasicProperties.Builder().deliveryMode(2).expiration(String.valueOf(5 * 60 * 1000)).build();

            String msg = "r_k_1  this is a message";
            channel.basicPublish(EXCHANGE_NAME, "r_k_1", build, msg.getBytes());


              msg = "r_k_2  this is a message";
            channel.basicPublish(EXCHANGE_NAME, "r_k_2", build, msg.getBytes());

            System.out.println(" [生产者] Sent '" + msg + "'");

        }


    }

}
