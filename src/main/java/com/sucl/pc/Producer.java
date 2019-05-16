package com.sucl.pc;

import com.rabbitmq.client.*;
import com.sucl.AmqpConnectionFactory;
import com.sucl.config.AmqpConfig;
import com.sucl.config.ExType;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 生产者
 * @author sucl
 * @date 2019/5/14
 */
public class Producer {
    private AtomicInteger msgCount = new AtomicInteger(0);

    public void produce(String message) throws IOException, TimeoutException, InterruptedException {
        Connection connection = AmqpConnectionFactory.getConnection(new AmqpConfig());
        //基于信道的通信
        Channel channel = connection.createChannel();
        /**
         * 交换机名称、交换机类型、是否持久化、是否自动删除、是否内部使用、参数
         */
        channel.exchangeDeclare(Common.EXCHANGE_X1,ExType.Direct.value(),false,false,false,null);//申明交换机

        //为了保证先启动该类，交换机没有绑定队列导致消息丢失，优先处理，在消费者中也会有以下内容
        channel.exchangeDeclare(Common.EXCHANGE_DLX_X1,ExType.Direct.value());
        channel.queueDeclare(Common.QUEUE_DLX_Q1, false, false, false, null);//申明死信队列
        channel.queueBind(Common.QUEUE_DLX_Q1, Common.EXCHANGE_DLX_X1, Common.ROUTING_DLX_KEY1);//将消息交换机与队列绑定
        Map<String, Object> args = new HashMap<>();
        args.put("x-dead-letter-exchange",Common.EXCHANGE_DLX_X1);//死信交换机
        args.put("x-dead-letter-routing-key", Common.ROUTING_DLX_KEY1);
        args.put("x-expires", 30000);//ms 队列过期时间
        args.put("x-message-ttl", 12000);//消息过期时间
        channel.queueDeclare(Common.QUEUE_Q1,false,false,false ,args);
        channel.queueBind(Common.QUEUE_Q1,Common.EXCHANGE_X1,Common.BINDING_KEY1);//将消息交换机与队列绑定

        channel.confirmSelect();//确认机制

        channel.addConfirmListener(new ConfirmListener() {
            @Override
            public void handleAck(long deliveryTag, boolean multiple) throws IOException {
//                System.out.println("消息发送成功！ "+deliveryTag);
                msgCount.incrementAndGet();
            }

            @Override
            public void handleNack(long deliveryTag, boolean multiple) throws IOException {
                System.out.println("消息发送失败！ "+deliveryTag);
            }
        });
        /**
         * mandatory=ture
         */
        channel.addReturnListener(new ReturnListener() {
            @Override
            public void handleReturn(int replyCode, String replyText, String exchange, String routingKey, AMQP.BasicProperties properties, byte[] body) throws IOException {
                System.out.println("重新处理消息！");
            }
        });
        /**
         * 消息发送到指定交换机、routing key、是否重发、是否、基础属性、消息内容
         *  mandatory:(true)没有队列，消息返回;(false)没有队列，消息丢弃
         *  immediate:(true)没有消费者，消息返回;(false)
         */
        int count = 0;
//        AMQP.BasicProperties properties = new AMQP.BasicProperties().builder().expiration("12000").build();
        while (count++ <100000){
//            TimeUnit.MILLISECONDS.sleep(500);
            channel.basicPublish(Common.EXCHANGE_X1,Common.ROUTING_KEY1,false,false,null,(new Date()+message+count).getBytes());
        }

//        channel.close();
//
//        AmqpConnectionFactory.close(connection);
    }

    public static void main(String[] args) throws IOException, TimeoutException, InterruptedException {
        Producer producer = new Producer();
        producer.produce("消息 ");
        System.out.println(producer.msgCount.intValue());
    }
}
