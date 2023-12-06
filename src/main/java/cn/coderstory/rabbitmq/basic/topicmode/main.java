package cn.coderstory.rabbitmq.basic.topicmode;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * 消息可以被重复消费
 * 消息到达exchange，查询exchange绑定的队列，如果队列的路由key和消息的路由key一致
 * 则消息会放入队列中，每一个客户端都是独立的队列
 */
@Component("16")
public class main {
    public main(Recv recv, Recv2 recv2, Sender sender) throws IOException, TimeoutException {
        // 第一次执行的时候先走send 创建好exchange 才行、
//        sender.send();
//        recv2.recv();
//        recv.recv();

    }
}
