package cn.coderstory.rabbitmq.basic;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

@Component
public class ConnectionUtil {

    public Connection getInstance() {
        //定义连接工厂
        ConnectionFactory factory = new ConnectionFactory();
        //设置服务地址
        factory.setHost("127.0.0.1");
        //端口
        factory.setPort(5672);
        //设置账号信息，用户名、密码、vhost
        factory.setVirtualHost("/");
        factory.setUsername("coderstory");
        factory.setPassword("123456");
        // 通过工程获取连接
        try {
            return factory.newConnection();
        } catch (IOException | TimeoutException exception) {
            throw new RuntimeException("RabbitMQ连接初始化失败", exception);
        }
    }


}
