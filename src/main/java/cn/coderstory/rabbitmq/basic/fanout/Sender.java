package cn.coderstory.rabbitmq.basic.fanout;

import cn.coderstory.rabbitmq.basic.ConnectionUtil;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

@Component("11")
public class Sender {
    private final ConnectionUtil connectionUtil;
    private final static String EXCHANGE_NAME = "EXCHANGE_NAME";
    private final static String QUEUE_NAME = "fanout_exchange_queue_1";

    public Sender(ConnectionUtil connectionUtil) {
        this.connectionUtil = connectionUtil;
    }

    public void send() throws IOException, TimeoutException {
        try (Channel channel = connectionUtil.getInstance().createChannel()) {
            channel.queueDeclare(QUEUE_NAME, true, false, false, null);
            // 创建exchange 指定交换机的名称和类型
            channel.exchangeDeclare(EXCHANGE_NAME, "fanout");
            AMQP.BasicProperties build = new AMQP.BasicProperties.Builder().deliveryMode(2).expiration(String.valueOf(5 * 60 * 1000)).build();
            String msg = "  this is a message";
            // 发送消息的时候 指定交换机的名字
            channel.basicPublish(EXCHANGE_NAME, "", build, msg.getBytes());
            System.out.println(" [生产者] Sent '" + msg + "'");
        }
    }
}
