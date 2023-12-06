package cn.coderstory.rabbitmq.basic.deadmode;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * 死信模式
 * 出现死信场景: TTL到期 消息被拒（basicReject） 队列达到最大长度
 * 触发死信的队列需要设置绑定死信的交换机和路由键
 */
@Component("21")
public class main {
    private final Recv recv;
    private final Sender sender;

    public main(Recv recv, Sender sender, DeadRecv deadRecv) throws IOException, TimeoutException {
        this.recv = recv;
        this.sender = sender;
        deadRecv.recv();
        recv.recv();
        sender.send();
    }
}
