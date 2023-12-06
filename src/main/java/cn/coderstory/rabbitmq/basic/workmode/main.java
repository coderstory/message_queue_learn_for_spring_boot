package cn.coderstory.rabbitmq.basic.workmode;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Work模式
 * 一个生产者 多个消费者
 * 一个消息只能消费一次
 * 代码和basic_mode基本一致 只是创建了多个消费者
 */
@Component("4")
public class main {


    public main(Recv recv, Recv2 recv2,Sender sender) throws IOException, TimeoutException {
//        recv2.recv();
//        recv.recv();
//        sender.send();
    }
}
