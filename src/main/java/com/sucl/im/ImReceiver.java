package com.sucl.im;

import com.rabbitmq.client.*;
import com.sucl.AmqpConnectionFactory;
import com.sucl.config.AmqpConfig;
import com.sucl.config.ExType;
import com.sucl.pc.Common;

import java.io.IOException;

/**
 * @author sucl
 * @date 2019/5/14
 */
public class ImReceiver {
    public static String Queue_IM1 = "q.im1";

    public void start(){
        Connection connection = AmqpConnectionFactory.getConnection(new AmqpConfig());

        try {
            final Channel channel = connection.createChannel();

            channel.queueDeclare(Queue_IM1,false,false,false,null);

            channel.exchangeDeclare(ImSender.EXCHANGE_IM1,ExType.Direct.value(),false,false,false,null);//申明交换机

            channel.queueBind(Queue_IM1,ImSender.EXCHANGE_IM1,"rk1");//将消息交换机与队列绑定

            channel.basicConsume(Queue_IM1,true,"c1",false,false,null,new DefaultConsumer(channel){
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                    System.out.println("接收消息： "+new String(body));
                    channel.basicPublish(ImSender.EXCHANGE_IM1,"rk2",null,("我收到消息了"+new String(body)).getBytes());
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        ImReceiver receiver = new ImReceiver();
        receiver.start();
    }
}
