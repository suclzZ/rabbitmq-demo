package com.sucl.pc;

import com.rabbitmq.client.*;
import com.sucl.AmqpConnectionFactory;
import com.sucl.config.AmqpConfig;
import com.sucl.config.ExType;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author sucl
 * @date 2019/5/15
 */
public class DlxConsumer {

    public void consume() throws IOException {
        Connection connection = AmqpConnectionFactory.getConnection(new AmqpConfig());

        final Channel channel = connection.createChannel();//建立通信通道

        channel.queueDeclare(Common.QUEUE_DLX_Q1, false, false, false, null);//申明队列
        channel.exchangeDeclare(Common.EXCHANGE_DLX_X1, ExType.Direct.value(), false, false, false, null);//申明交换机
        channel.queueBind(Common.QUEUE_DLX_Q1, Common.EXCHANGE_DLX_X1, Common.ROUTING_DLX_KEY1);//将消息交换机与队列绑定

        channel.basicConsume(Common.QUEUE_DLX_Q1, true, "c2", false, false, null, new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                System.out.println("死信："+new String(body));
            }
        });
    }

    public static void main(String[] args) throws IOException {
        DlxConsumer consumer = new DlxConsumer();
        consumer.consume();
    }
}
