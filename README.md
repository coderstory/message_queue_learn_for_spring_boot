## RabbitMQ

### **模式**



创建服务器连接
```java
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
        // 通过工厂获取连接
        try {
            return factory.newConnection();
        } catch (IOException | TimeoutException exception) {
            throw new RuntimeException("RabbitMQ连接初始化失败", exception);
        }
    }
```

#### 基础模式 [只配置队列名]
> 生产者和消费者一对一的关系，生产者和消费者只需要指定相同的队列的名称即可。
生产者
```java
        try (Channel channel = getInstance().createChannel()) {
            // 创建一个队列 并开启持久化
            channel.queueDeclare("QUEUE_NAME", true, false, false, null);
            String msg = "this is a message";
            // 创建个消息配置 开启持久化 并设置有效期为五分钟
            AMQP.BasicProperties build = new AMQP.BasicProperties.Builder().deliveryMode(2).expiration(String.valueOf(5 * 60 * 1000)).build();
            // 发送一个消息 只配置了队列的名称 没有配置交换机的名称
            channel.basicPublish("", QUEUE_NAME, build, msg.getBytes());
        }
```
消费者
```java
        // 创建一个通道用于与服务器连接
        Channel channel = getInstance().createChannel();
        // 创建和绑定一个队列 如果队列已存在 则必须配置一致 否则会报错
        channel.queueDeclare("QUEUE_NAME", true, false, false, null);
        // 定义一个消费者
        DefaultConsumer defaultConsumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                log.warn("consumerTag {}", consumerTag);
                log.warn("envelope {}", envelope);
                log.warn("body {}", new String(body));
                // 手动确认  deliveryTag  multiple
                // channel.basicAck(envelope.getDeliveryTag(),false);
            }
        };
        // 绑定队列和消费者 并开启自动确认 哪怕消费失败 也会消费掉消息
        channel.basicConsume(QUEUE_NAME, true, defaultConsumer);
   
```
#### work模式 [只配置队列名(多个实例 名字相同)]
> work模式下存在一个生产者和多个消费者，单个消息仅被一个消费者消费。也是比较常用的方式。代码上和基础模式差不多，都只指定了队列的名称。只是消费者存在多个实例。
生产者
```java
        try(Channel channel = connectionUtil.getInstance().createChannel()) {
            channel.queueDeclare("QUEUE_NAME", true, false, false, null);
            AMQP.BasicProperties build = new AMQP.BasicProperties.Builder().deliveryMode(2).expiration(String.valueOf(5 * 60 * 1000)).build();
            IntStream.range(1, 20).parallel().forEach(value -> {
                try {
                    String msg = value + "  this is a message";
                    channel.basicPublish("", "QUEUE_NAME", build,msg.getBytes());
                    System.out.println(" [生产者] Sent '" + msg + "'");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }
```
消费者
```java
        // channel 无法使用try自动释放 否则后续监听失效
        Channel channel = connectionUtil.getInstance().createChannel();
        channel.queueDeclare(QUEUE_NAME, true, false, false, null);
        channel.basicQos(10);
        DefaultConsumer defaultConsumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                log.warn("Recv body {}", new String(body));
                // 手动确认  deliveryTag  multiple
                // channel.basicAck(envelope.getDeliveryTag(),false);
            }
        };
        // 自动确认模式 哪怕消费异常
        channel.basicConsume(QUEUE_NAME, true, defaultConsumer);
```
#### Fanout模式 [只配置队列名（不重复）和交换机]
> 一个生产者对应多个消费者，一个消费会被所有的消费者接受，属于广播模式。此模式下，需要定义一个交换机，配置交换机类型为```fanout```模式。队列需要绑定到这个交换机。
生产者
```java
        try (Channel channel = connectionUtil.getInstance().createChannel()) {
            channel.queueDeclare(QUEUE_NAME, true, false, false, null);
            // 创建exchange 指定交换机的名称和类型
            channel.exchangeDeclare(EXCHANGE_NAME, "fanout");
            AMQP.BasicProperties build = new AMQP.BasicProperties.Builder().deliveryMode(2).expiration(String.valueOf(5 * 60 * 1000)).build();
            String msg = "  this is a message";
            // 发送消息的时候 指定交换机的名字
            channel.basicPublish(EXCHANGE_NAME, "", build, msg.getBytes());
            System.out.println(" [生产者] Sent '" + msg + "'");
        }
```
消费者
```java
        // channel 无法使用try自动释放 否则后续监听失效
        Channel channel = connectionUtil.getInstance().createChannel();
        // 声明队列
        channel.queueDeclare(QUEUE_NAME, true, false, false, null);
        // 绑定队列到交换机
        channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, "");

        DefaultConsumer defaultConsumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                log.warn("Recv body {}", new String(body));
                // 手动确认  deliveryTag  multiple
                // channel.basicAck(envelope.getDeliveryTag(),false);
            }
        };
        // 自动确认模式 哪怕消费异常
        channel.basicConsume(QUEUE_NAME, true, defaultConsumer);
```
#### direct模式 [只配置队列名（不重复），交换机，路由键]
> 在Fanout模式上新增```路由键```，```队列```和```交换机```绑定之后，交换机是否会把消息发送到指定的队列，还需要进行路由键的筛选。如果消息的```路由键```符合队列的路由键才会被放置。
生产者
```java
        try (Channel channel = getInstance().createChannel()) {
            channel.queueDeclare("QUEUE_NAME", true, false, false, null);
            // 创建exchange name，type 类型不能填错
            channel.exchangeDeclare("EXCHANGE_NAME", "direct");
            AMQP.BasicProperties build = new AMQP.BasicProperties.Builder().deliveryMode(2).expiration(String.valueOf(5 * 60 * 1000)).build();
            String msg = "r_k_1  this is a message";
            // 发送消息需要指定交换机和路由键
            channel.basicPublish(EXCHANGE_NAME, "r_k_1", build, msg.getBytes());
            msg = "r_k_2  this is a message";
            channel.basicPublish(EXCHANGE_NAME, "r_k_2", build, msg.getBytes());
            System.out.println(" [生产者] Sent '" + msg + "'");
        }
```
消费者
```java
        // channel 无法使用try自动释放 否则后续监听失效
        Channel channel = getInstance().createChannel();
        // 声明队列
        channel.queueDeclare("QUEUE_NAME", true, false, false, null);
        // 绑定队列到交换机 队列名称 交换机名称 路由键
        channel.queueBind("QUEUE_NAME", "EXCHANGE_NAME", "r_k_2");
        DefaultConsumer defaultConsumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                log.warn("Recv2 body {}", new String(body));
            }
        };
        channel.basicConsume(QUEUE_NAME, true, defaultConsumer);
```
#### topic模式  [只配置队列名（不重复），交换机，路由键(带规则)]
> 在direct模式上，细分```路由键```，将```路由键```改成 ```单词.单词.单词```的形式，比如 ```auth.user.insert```,队列在绑定路由键的时候，允许使用 ```*``` 和 ```#```匹配单个或者多个单词，比如```auth.#```比配```auth```开头的所有消息

生产者
```java
        try (Channel channel = getInstance().createChannel()) {
            channel.queueDeclare("QUEUE_NAME", true, false, false, null);
            // 创建exchange name，type, durable 类型不能选错
            channel.exchangeDeclare(EXCHANGE_NAME, "topic", true);
            AMQP.BasicProperties build = new AMQP.BasicProperties.Builder().deliveryMode(2).expiration(String.valueOf(5 * 60 * 1000)).build();
            String msg = "item.insert  this is a message";
            // 交换机和路由键必须写全
            channel.basicPublish(EXCHANGE_NAME, "item.insert", build, msg.getBytes());
            msg = "pp.insert  this is a message";
            channel.basicPublish(EXCHANGE_NAME, "pp.insert", build, msg.getBytes());
            System.out.println(" [生产者] Sent '" + msg + "'");
        }
```
消费者
```java
        Channel channel = getInstance().createChannel();
        // 声明队列 每个消费者的队列名称必须独立不重复
        channel.queueDeclare("QUEUE_NAME", true, false, false, null);
        // 绑定队列到交换机 三要素必须写全
        channel.queueBind("QUEUE_NAME", "EXCHANGE_NAME", "*.insert");
        DefaultConsumer defaultConsumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                log.warn("Recv2 body {}", new String(body));
            }
        };
        channel.basicConsume(QUEUE_NAME, true, defaultConsumer);
```

#### 总结 
| 模式       | 场景                                           |
|----------|----------------------------------------------|
| basic模式  | 对一对场景                                        |
| work模式   | 消费者多实例                                       |
| fanout   | 一个消息要被多次消费                                   |
| direct模式 | 消费分阶梯层次 比如带等级的日志 用户表的不同类型操作 将同一个对象的多种类型合并到一起 |
| topic模式  | 聚合不同的子类型                                     |
