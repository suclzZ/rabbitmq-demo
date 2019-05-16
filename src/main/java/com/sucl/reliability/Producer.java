package com.sucl.reliability;

import com.rabbitmq.client.*;
import com.sucl.AmqpConnectionFactory;
import com.sucl.config.AmqpConfig;
import com.sucl.config.ExType;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
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
        Channel channel = connection.createChannel();
        channel.exchangeDeclare(RbCommon.EXCHANGE_X2,ExType.Direct.value(),false,false,false,null);//申明交换机
        
        Map<String, Object> args = new HashMap<>();
        channel.queueDeclare(RbCommon.QUEUE_Q2,false,false,false ,args);
        channel.queueBind(RbCommon.QUEUE_Q2,RbCommon.EXCHANGE_X2,RbCommon.BINDING_KEY2);//将消息交换机与队列绑定

        channel.confirmSelect();//确认机制
//        channel.basicQos(1);

        channel.addConfirmListener(new ConfirmListener() {
            @Override
            public void handleAck(long deliveryTag, boolean multiple) throws IOException {
                System.out.println("消息发送成功！ "+deliveryTag);
                msgCount.incrementAndGet();
                if(multiple){
                    System.out.println("批量处理！");
                }else{
                    System.out.println("单个处理！");
                }
            }

            @Override
            public void handleNack(long deliveryTag, boolean multiple) throws IOException {
                System.out.println("消息发送失败！ "+deliveryTag);
            }
        });

        channel.addReturnListener(new ReturnListener() {
            @Override
            public void handleReturn(int replyCode, String replyText, String exchange, String routingKey, AMQP.BasicProperties properties, byte[] body) throws IOException {
                System.out.println("重新处理消息！");
            }
        });

        int count = 0;
        while (count++ <100000){
            channel.basicPublish(RbCommon.EXCHANGE_X2,RbCommon.ROUTING_KEY2,false,false,null,(new Date()+message+count).getBytes());
        }

        if(channel.waitForConfirms()){
            channel.close();
            AmqpConnectionFactory.close(connection);
        }
    }

    public static void main(String[] args) throws IOException, TimeoutException, InterruptedException {
        Producer producer = new Producer();
        producer.produce("消息 ");
        System.out.println(producer.msgCount.intValue());
    }
}
