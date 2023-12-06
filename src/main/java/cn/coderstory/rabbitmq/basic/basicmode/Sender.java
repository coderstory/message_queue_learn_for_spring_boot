package cn.coderstory.rabbitmq.basic.basicmode;

import cn.coderstory.rabbitmq.basic.ConnectionUtil;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

@Component("3")
public class Sender {
    private final ConnectionUtil connectionUtil;
    private final static String QUEUE_NAME = "a_queue_name";

    public Sender(ConnectionUtil connectionUtil) {
        this.connectionUtil = connectionUtil;
    }

    public void send() throws IOException, TimeoutException {
        // 创建一个通道 用于与服务器连接
        try (Channel channel = connectionUtil.getInstance().createChannel()) {
            // 创建一个队列 并开启持久化
            channel.queueDeclare(QUEUE_NAME, true, false, false, null);
            String msg = "this is  a message";
            // 创建个消息配置 开启持久化 并设置有效期为五分钟
            AMQP.BasicProperties build = new AMQP.BasicProperties.Builder().deliveryMode(2).expiration(String.valueOf(5 * 60 * 1000)).build();
            // 发送一个消息 只配置了队列的名称 没有配置交换机的名称
            channel.basicPublish("", QUEUE_NAME, build, msg.getBytes());
        }
    }
}
