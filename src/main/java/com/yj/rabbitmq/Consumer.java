package com.yj.rabbitmq;

import com.rabbitmq.client.*;

public class Consumer {

    public static void main(String[] args) throws Exception{

        //1 创建一个ConnectionFactory
        ConnectionFactory connectionFactory = new ConnectionFactory();

        connectionFactory.setHost("120.78.203.39");
        connectionFactory.setPort(5672);
        connectionFactory.setVirtualHost("/");
        connectionFactory.setUsername("admin");
        connectionFactory.setPassword("admin");

        //2 通过连接工厂创建连接
        Connection connection = connectionFactory.newConnection();

        //3 通过connection创建一个Channel
        Channel channel = connection.createChannel();

        //4 申明一个队列
        String queueName = "test001";
        //队列名字， 服务器重启不删除队列， channel独占队列， 没有绑定关系自动删除， 扩展参数
        channel.queueDeclare(queueName, true, false, false, null);

        //5 创建消费者
        QueueingConsumer queueingConsumer = new QueueingConsumer(channel);


        //6 设置channel
        // 第二个参数是是否发回确认收到的消息
        channel.basicConsume(queueName, true, queueingConsumer);



        while (true){
            //7 获取消息
            QueueingConsumer.Delivery delivery = queueingConsumer.nextDelivery();
            String msg = new String(delivery.getBody());
            System.err.println("消费端：" + msg);
            //Envelope envelope = delivery.getEnvelope();
        }



    }
}
