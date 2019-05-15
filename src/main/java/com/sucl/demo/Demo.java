package com.sucl.demo;

import com.rabbitmq.client.*;
import com.sucl.AmqpConnectionFactory;
import com.sucl.config.AmqpConfig;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @author sucl
 * @date 2019/5/14
 */
public class Demo {

    public static void main(String[] args) throws IOException, TimeoutException {

        Connection connection = AmqpConnectionFactory.getConnection(new AmqpConfig());

        final Channel channel = connection.createChannel();

        channel.queueDeclare("q.demo",false,false,true,null);

        channel.basicPublish("","q.demo",null,"消息".getBytes());

        channel.basicConsume("q.demo",false,new DefaultConsumer(channel){
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                System.out.println(new String(body));
                channel.basicAck(envelope.getDeliveryTag(),false);
            }
        });
//        channel.close();
//        connection.close();

    }
}
