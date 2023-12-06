package cn.coderstory.rabbitmq.basic.fanout;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * 订阅模式 fanout 广播
 * 每个消费者使用独立的队列 （通道绑定唯一的队列）
 * 每个队列需要绑定相同的exchange
 * 消费者的队列需要绑定相同的exchange
 * fanout模式下消息会被exchange放置到所有绑定这个exchange的队列
 * 一个生产者对应多个消费者 消息可重复消费
 */
@Component("8")
public class main {
    public main(Recv recv, Recv2 recv2, Sender sender) throws IOException, TimeoutException {
//        // 第一次执行的时候先走send 创建好exchange 才行
//        recv2.recv();
//        recv.recv();
//        sender.send();
    }
}
