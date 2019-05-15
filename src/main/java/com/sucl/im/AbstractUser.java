package com.sucl.im;

import com.rabbitmq.client.*;
import com.sucl.AmqpConnectionFactory;
import com.sucl.config.AmqpConfig;
import com.sucl.config.ExType;

import java.io.IOException;
import java.util.Scanner;
import java.util.UUID;

/**
 * @author sucl
 * @date 2019/5/15
 */
public abstract class AbstractUser {
    private static String EXCHANGE = "x.user";

    private String id = UUID.randomUUID().toString();
    private String name;
    private Connection connection = AmqpConnectionFactory.getConnection(new AmqpConfig());

    public AbstractUser(String name){
        this.name = name;
    }

    public void start() throws IOException {
        System.out.println(name +" 上线了..");
        Channel channel = connection.createChannel();
        channel.basicQos(1);//流量控制

        String queueName = getResQueue();
        channel.exchangeDeclare(EXCHANGE,ExType.Direct.value());
        channel.queueDeclare(queueName,false,false,false,null);
        channel.queueBind(queueName,EXCHANGE,getResBindingKey());
        //专门接收的线程
        new Thread(()->{
            try {
                channel.basicConsume(queueName,false,new DefaultConsumer(channel){
                    @Override
                    public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                        if(!id.equals(properties.getCorrelationId())){
                            System.out.println(properties.getAppId()+" : "+new String(body));
                            channel.basicAck(envelope.getDeliveryTag(),false);
                        }else{
                            channel.basicNack(envelope.getDeliveryTag(),false,true);
                        }
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
        //专门发送的线程
        new Thread(()->{
            Scanner scanner = new Scanner(System.in);
            AMQP.BasicProperties properties = new AMQP.BasicProperties().builder().correlationId(id).appId(name).build();
            while (true){
                try {
                    channel.basicPublish(EXCHANGE,getPusRoutingKey(),properties,scanner.nextLine().getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 发送消息时指定routing key，在接收方binding key需要与之对应
     * @return
     */
    public abstract String getPusRoutingKey();
    /**
     * 接收消息的队列
     * @return
     */
    protected String getResQueue(){
        return "q.jr";
    }
    /**
     * 接收消息的binding key，与发送方的routing key对应
     * @return
     */
    public abstract String getResBindingKey();
}
