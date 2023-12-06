package cn.coderstory.rabbitmq.basic.basicmode;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * 基本消息模式
 * 一个生产者对应一个消费者
 * 仅存在队列的定义
 */
@Component("1")
public class main {
   private final Recv recv;
    private final Sender sender;

    public main(Recv recv, Sender sender) throws IOException, TimeoutException {
        this.recv = recv;
        this.sender = sender;
//        sender.send();
//        recv.recv();
    }
}
