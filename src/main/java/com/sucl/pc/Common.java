package com.sucl.pc;

/**
 * @author sucl
 * @date 2019/5/14
 */
public class Common {
    /**
     * 消费队列
     */
    public static final String QUEUE_Q1 = "q.q1";
    /**
     * 死信队列
     */
    public static final String QUEUE_DLX_Q1 = "q.dlx.q1";
    /**
     * 消费发布交换机
     */
    public static final String EXCHANGE_X1 = "x.x1";
    /**
     * 死信交换机
     */
    public static final String EXCHANGE_DLX_X1 = "x.dlx.x1";
    /**
     * 消息发布routing key
     */
    public static final String ROUTING_KEY1 = "k.k1";
    /**
     * 消费队列binding key
     */
    public static final String BINDING_KEY1 = "k.k1";
    /**
     * 死信交换机routing key
     */
    public static final String ROUTING_DLX_KEY1 = "k.dlx.k1";

}
