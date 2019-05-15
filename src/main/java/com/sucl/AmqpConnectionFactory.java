package com.sucl;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.sucl.config.AmqpConfig;
import com.sucl.exception.AmqpException;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeoutException;

/**
 * @author sucl
 * @date 2019/5/14
 */
public class AmqpConnectionFactory {

    /**
     *
     * @param config
     * @return
     */
    public static Connection getConnection(AmqpConfig config){
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost(config.getHost());
        connectionFactory.setVirtualHost(config.getVirtualHost());
        connectionFactory.setPort(config.getPort());
        connectionFactory.setUsername(config.getUsername());
        connectionFactory.setPassword(config.getPassword());
        String message;
        try {
            return connectionFactory.newConnection();
        } catch (IOException e) {
            message = e.getMessage();
            e.printStackTrace();
        } catch (TimeoutException e) {
            message = e.getMessage();
            e.printStackTrace();
        }
        throw new AmqpException("连接获取失败："+message);
    }

    public static Connection getConnection2(AmqpConfig config){
        ConnectionFactory factory = new ConnectionFactory();
        // 连接格式：amqp://userName:password@hostName:portNumber/virtualHost
        String uri = String.format("amqp://%s:%s@%s:%d%s", config.getUsername(), config.getPassword(), config.getHost(),
                config.getPort(), config.getVirtualHost());
        String message;
        try {
            factory.setUri(uri);
            factory.setVirtualHost(config.getVirtualHost());
            return factory.newConnection();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
        throw new AmqpException("连接获取失败");
    }

    public static void close(Connection connection){
        if(connection!=null){
            try {
                connection.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
