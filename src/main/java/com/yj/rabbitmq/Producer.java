package com.yj.rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class Producer {

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

        //4 通过Channel发送数据
        for (int i = 0; i < 5; i++){
            String msg = "Hello RabbitMQ!";
            channel.basicPublish("", "test001", null, msg.getBytes());
        }

        //5 由小到大关闭连接
        channel.close();
        connection.close();
    }
}
