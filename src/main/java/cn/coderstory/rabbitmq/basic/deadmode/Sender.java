package cn.coderstory.rabbitmq.basic.deadmode;

import cn.coderstory.rabbitmq.basic.ConnectionUtil;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

@Component("23")
public class Sender {
    private final ConnectionUtil connectionUtil;
    private final static String QUEUE_NAME = "rev";

    public Sender(ConnectionUtil connectionUtil) {
        this.connectionUtil = connectionUtil;
    }

    /**
     * 需要想要触发死信 队列必须设置 死信的交换机和路由键
     */
    public void send() throws IOException, TimeoutException {
        // 创建一个通道 用于与服务器连接
        try (Channel channel = connectionUtil.getInstance().createChannel()) {
            Map<String, Object> arguments = new HashMap<>();
            arguments.put("x-dead-letter-exchange","DEAD_EXCHANGE");
            //设置死信routing key
            arguments.put("x-dead-letter-routing-key","dead");
            // 设置正常队列长度的限制
            // arguments.put("x-max-length",6);
            channel.queueDeclare(QUEUE_NAME, true, false, false, arguments);
            String msg = "this is a message";
            AMQP.BasicProperties build = new AMQP.BasicProperties.Builder().deliveryMode(2).expiration(String.valueOf(5 * 60 * 1000)).build();
            channel.basicPublish("", QUEUE_NAME, build, msg.getBytes());
        }
    }
}
