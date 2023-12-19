## Kafka

### 安装

1. 通过ZK启动服务
```bash
   kafka_2.13-3.6.1\bin\windows\zookeeper-server-start.bat  kafka_2.13-3.6.1\config\zookeeper.properties
   kafka_2.13-3.6.1\bin\windows\kafka-server-start.bat kafka_2.13-3.6.1\config\server.properties
  ```
2. 通过kraft启动服务
```bash
# 生成一个uuid
kafka_2.13-3.6.1\bin\windows\kafka-storage.bat random-uuid
# 通过这个uuid（jeRJ98H-QnqTlZzItjXO9Q）初始化配置 
kafka_2.13-3.6.1\bin\windowskafka-storage.sh format -t jeRJ98H-QnqTlZzItjXO9Q -c  kafka_2.13-3.6.1\config\kraft\server.properties
# 启动服务
kafka_2.13-3.6.1\bin\windows\kafka-server-start.bat kafka_2.13-3.6.1\config\kraft\server.properties
  ```
###  基本概念


| 名词            | 解释                                                         |
| --------------- | ------------------------------------------------------------ |
| Broker          | 消息中间件处理节点，一个Kafka节点就是一个broker，一个或者多个Broker可以组成一个[Kafka集群] |
| Topic           | Kafka根据topic对消息进行归类，发布到Kafka集群的每条消息都需要指定一个topic |
| Producer        | 消息生产者，向Broker发送消息的客户端                         |
| Consumer        | 消息消费者，从Broker读取消息的客户端                         |
| ConsumerGroup   | 每个Consumer属于一个特定的Consumer Group，一条消息可以被多个不同的Consumer Group消费，但是一个Consumer Group中只能有一个Consumer能够消费该消息 |
| Partition       | 物理上的概念，一个topic可以分为多个partition，每个partition内部消息是有序的 |
| Replica（副本） | 一个 topic 的每个分区都有若干个副本，一个 Leader 和若干个 Follower |
| Leader          | 每个分区多个副本的“主”，生产者发送数据的对象，以及消费者消费数据的对象都是 Leader |
| Follower        | 每个分区多个副本中的“从”，实时从 Leader 中同步数据，保持和 Leader 数据的同步。Leader 发生故障时，某个 Follower 会成为新的 Leader。 |



