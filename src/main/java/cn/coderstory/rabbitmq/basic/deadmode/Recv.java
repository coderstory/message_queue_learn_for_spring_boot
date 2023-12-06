package cn.coderstory.rabbitmq.basic.deadmode;

import cn.coderstory.rabbitmq.basic.ConnectionUtil;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component("22")
public class Recv {
    private final ConnectionUtil connectionUtil;
    private final static String QUEUE_NAME = "dead_queue";

    public Recv(ConnectionUtil connectionUtil) {
        this.connectionUtil = connectionUtil;
    }

    /**
     * 触发死信的消费者 一样需要配置队列的死信设置
     * 接受消息的时候调用basicNack触发死信机制
     */
    public void recv() throws IOException {
        // 创建一个通道用于与服务器连接
        Channel channel = connectionUtil.getInstance().createChannel();
        Map<String, Object> arguments = new HashMap<>();
        arguments.put("x-dead-letter-exchange","DEAD_EXCHANGE");
        //设置死信routing key
        arguments.put("x-dead-letter-routing-key","dead");
        //设置正常队列长度的限制
        //arguments.put("x-max-length",6);
        channel.queueDeclare(QUEUE_NAME, true, false, false, arguments);
        DefaultConsumer defaultConsumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                channel.basicNack(envelope.getDeliveryTag(),false,false);
            }
        };
        channel.basicConsume(QUEUE_NAME, false, defaultConsumer);
    }
}
