package cn.coderstory.rabbitmq.springplus;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 通过创建bean的方式配置队列和交换机
 */
@Configuration
public class Config {
    //队列 起名：TestDirectQueue
    @Bean
    public Queue TestDirectQueue() {
        // return new Queue("TestDirectQueue",true);
        return QueueBuilder.durable("TestDirectQueue").build();
    }

    //Direct交换机
    @Bean
    DirectExchange TestDirectExchange() {
        // return new DirectExchange("TestDirectExchange");
        return ExchangeBuilder.directExchange("TestDirectExchange").build();
    }

    //绑定  将队列和交换机绑定, 并设置用于匹配键：TestDirectRouting
    @Bean
    Binding bindingDirect() {
        //return new Binding(TestDirectQueue(),null, Binding.DestinationType.QUEUE,"TestDirectExchange","TestDirectRouting",null);
         return BindingBuilder.bind(TestDirectQueue()).to(TestDirectExchange()).with("TestDirectRouting");
    }

}
