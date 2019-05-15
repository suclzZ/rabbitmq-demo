package com.sucl.pc;

import com.rabbitmq.client.*;
import com.sucl.AmqpConnectionFactory;
import com.sucl.config.AmqpConfig;
import com.sucl.config.ExType;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 消费者
 * @author sucl
 * @date 2019/5/14
 */
public class Consumer {

    public void consume() throws IOException {
        Connection connection = AmqpConnectionFactory.getConnection(new AmqpConfig());

        final Channel channel = connection.createChannel();//建立通信通道

        channel.exchangeDeclare(Common.EXCHANGE_DLX_X1,ExType.Direct.value());
        Map<String, Object> args = new HashMap<>();
        args.put("x-dead-letter-exchange",Common.EXCHANGE_DLX_X1);//死信队列
        args.put("x-dead-letter-routing-key", Common.ROUTING_DLX_KEY1);//死信routing key 默认取
        args.put("x-expires", 30000);//ms 队列过期时间
        args.put("x-message-ttl", 12000);//消息过期时间
        /**
         * 队列名称、是否持久化、是否被该连接独占(只对申明连接可见，断开连接删除)、自动删除、参数
         */
        channel.queueDeclare(Common.QUEUE_Q1,false,false,false,args);//申明队列
        /**
         * 交换机名称、交换机类型、是否持久化、是否自动删除、是否内部使用、参数
         */
        channel.exchangeDeclare(Common.EXCHANGE_X1,ExType.Direct.value(),false,false,false,null);//申明交换机
        /**
         * 队列名称、交换机名称、binding Key
         */
        channel.queueBind(Common.QUEUE_Q1,Common.EXCHANGE_X1,Common.BINDING_KEY1);//将消息交换机与队列绑定
        /**
         *  队列名称、自动ACK、消费者标记、非本地、是否被该连接独占、参数
         *  与basicGet对比，get 只取了队列里面的第一条消息
         *  一种是主动去取，一种是监听模式
         */
        channel.basicConsume(Common.QUEUE_Q1,false,"c1",false,false,null,new DefaultConsumer(channel){
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                System.out.println(new String(body));
                //消息索引
                // 批量确认
                channel.basicAck(envelope.getDeliveryTag(),false);
                //其中 deliveryTag 和 requeue 的含义可以参考 basicReject 方法。 multiple 参数
                //设置为 false 则表示拒绝编号为 deliveryT坷的这一条消息，这时候 basicNack 和 basicReject 方法一样;
                // multiple 参数设置为 true 则表示拒绝 deliveryTag 编号之前所 有未被当前消费者确认的消息。
//                channel.basicNack(envelope.getDeliveryTag(),false,false);
                //一次只能拒绝一条
                //其中 deliveryTag 可以看作消息的编号 ，它是一个 64 位的长整型值，最大值是 9223372036854775807
                // requeue 参数设置为 true，则 RabbitMQ 会重新将这条消息存入队列，以便可以发送给下一个订阅的消费者;
                // requeue 参数设置为 false，则 RabbitMQ 立即会把消息从队列中移除，而不会把它发送给新的消费者
//                channel.basicReject(envelope.getDeliveryTag(),false);
            }
        });
        //requeue默认true，消息重入队列，requeue=true，发送给新的consumer，false发送给相同的consumer
//        channel.basicRecover(true);
        //取消消费者的订阅
//        channel.basicCancel("consumerTag");
    }

    public static void main(String[] args) throws IOException {
        Consumer consumer = new Consumer();
        consumer.consume();
    }
}
