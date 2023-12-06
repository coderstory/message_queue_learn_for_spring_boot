package cn.coderstory.rabbitmq.basic.fanout;

import cn.coderstory.rabbitmq.basic.ConnectionUtil;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component("10")
public class Recv2 {
    private final ConnectionUtil connectionUtil;
    private final static String EXCHANGE_NAME = "EXCHANGE_NAME";
    private final static String QUEUE_NAME = "fanout_exchange_queue_2";

    public Recv2(ConnectionUtil connectionUtil) {
        this.connectionUtil = connectionUtil;
    }

    public void recv() throws IOException {
        Channel channel = connectionUtil.getInstance().createChannel();
        // 声明队列
        channel.queueDeclare(QUEUE_NAME, true, false, false, null);
        // 绑定队列到交换机 需要指定交换机的名字和队列的名字
        channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, "");
        DefaultConsumer defaultConsumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                log.warn("Recv2 body {}", new String(body));
            }
        };
        // 自动确认模式 哪怕消费异常
        channel.basicConsume(QUEUE_NAME, true, defaultConsumer);
    }
}
