package com.sucl.im;

import com.rabbitmq.client.*;
import com.sucl.AmqpConnectionFactory;
import com.sucl.config.AmqpConfig;
import com.sucl.config.ExType;

import java.io.IOException;
import java.util.Scanner;

/**
 * @author sucl
 * @date 2019/5/14
 */
public class ImSender {
    public static String EXCHANGE_IM1 = "x.im1";
    private Connection connection = AmqpConnectionFactory.getConnection(new AmqpConfig());

    public void start() throws IOException {
        final Channel channel = connection.createChannel();
        channel.exchangeDeclare(ImSender.EXCHANGE_IM1,ExType.Direct.value(),false,false,false,null);

        AMQP.Queue.DeclareOk q = channel.queueDeclare();
        channel.queueBind(q.getQueue(),ImSender.EXCHANGE_IM1,"rk2");//将消息交换机与队列绑定
        channel.basicConsume(q.getQueue(),true,"c2",false,false,null,new DefaultConsumer(channel){
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                System.out.println(new String(body));
            }
        });

        Scanner scanner = new Scanner(System.in);
        while (true){
            String msg = scanner.nextLine();
            channel.basicPublish(EXCHANGE_IM1,"rk1",false,false,null,msg.getBytes());
        }

    }

    public static void main(String[] args) throws IOException {
        ImSender sender = new ImSender();
        sender.start();
    }
}
