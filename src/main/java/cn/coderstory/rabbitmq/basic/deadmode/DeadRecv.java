package cn.coderstory.rabbitmq.basic.deadmode;

import cn.coderstory.rabbitmq.basic.ConnectionUtil;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component("20")
public class DeadRecv {
    private final ConnectionUtil connectionUtil;
    private final static String QUEUE_NAME = "a_queue_name4";

    public DeadRecv(ConnectionUtil connectionUtil) {
        this.connectionUtil = connectionUtil;
    }

    /**
     * 处理死信的消费者
     * 需要定义死信的交换机 和 路由键
     * 死信本身和普通的交换机上一样的
     */
    public void recv() throws IOException {
        // 创建一个通道用于与服务器连接
        Channel channel = connectionUtil.getInstance().createChannel();
        // 创建和绑定一个队列 如果队列已存在 则必须配置一致 否则会报错
        channel.queueDeclare(QUEUE_NAME, true, false, false, null);
        channel.exchangeDeclare("DEAD_EXCHANGE","direct");
        channel.queueBind(QUEUE_NAME,"DEAD_EXCHANGE","dead");
        // 定义一个消费者
        DefaultConsumer defaultConsumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                System.out.println("触发死信");
                System.out.println(new String(body));
            }
        };
        channel.basicConsume(QUEUE_NAME, false, defaultConsumer);
    }
}
