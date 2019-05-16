可靠性问题：
1、生产者确认问题
我们知道，在消息从生产者发送到消息服务器是存在一个过程，如何确保消息到达服务器，目前是通过事务与confirm进行保障，但是不是就一定能够保证消息真实的到达并被服务器处理？
来源于网络：
    RabbitMQ还提供了一种生产者确认（publisher confirm）的模式，消息生产者可以通过 channel.confirmSelect() 方法把channel开启confirm模式，通过confirm模式的channel发布的消息都会指定一个唯一的消息ID（也就是deliveryTag，从1开始递增）。消息被发到RabbitMQ后，RabbitMQ会给生产者发送消息，消息内容有：发送消息时传递过去的deliveryTag；一个标志Ack/Nack（Ack表示成功发到了RabbitMQ交换机上，Nack表示发送失败）；还有一个multiple参数表示是否是批量确认，如果为false则表示单条确认，如果为true则表示到这个序号之前的所有消息都己经得到了处理。如果发送的是持久化消息，则在消息被成功写入磁盘之后才会发送给生产者确认消息

比如在生产者P发送消息到Broker，并处理完成，但是在返回处理结果时出现问题，那么Broker存在该条消息，但是生产者P却可能认为消息没有正常发送。

如果Broker处理过程出现问题，生产者没有收到ack消息会如何？是否会调用confirm回调？


