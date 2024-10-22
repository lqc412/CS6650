import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;

public class MultiThreadConsumer {

    private final static String QUEUE_NAME = "liftRideQueue";
    private final static int NUM_THREADS = 20; // 设置消费者线程数量

    public static void main(String[] argv) throws Exception {
        // 创建线程池
        ExecutorService executorService = Executors.newFixedThreadPool(NUM_THREADS);

        // 为每个线程创建一个消费者任务
        for (int i = 0; i < NUM_THREADS; i++) {
            executorService.execute(new ConsumerTask());
        }

        // 关闭线程池（可选）
        executorService.shutdown();
    }

    static class ConsumerTask implements Runnable {

        @Override
        public void run() {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost("localhost"); // 本地连接
            factory.setPort(5672);
            factory.setUsername("guest");
            factory.setPassword("guest");

            try {
                // 每个线程都有独立的连接和通道
                Connection connection = factory.newConnection();
                Channel channel = connection.createChannel();

                // 声明队列（确保队列存在）
                channel.queueDeclare(QUEUE_NAME, false, false, false, null);
                System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

                // 创建消费者，接收消息
                DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                    String message = new String(delivery.getBody(), "UTF-8");
                    System.out.println(" [x] Received '" + message + "' by Thread: " + Thread.currentThread().getName());
                    // 在此处可以对消息进行处理，例如将数据写入数据库
                };

                // 开始消费
                channel.basicConsume(QUEUE_NAME, true, deliverCallback, consumerTag -> {});

            } catch (IOException | TimeoutException e) {
                e.printStackTrace();
            }
        }
    }
}
