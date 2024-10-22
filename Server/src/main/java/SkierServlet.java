import com.google.gson.Gson;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;
import entity.LiftRide;
import entity.ResponseMsg;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * SkierServlet is a servlet that handles HTTP POST requests for skier lift ride data.
 * It validates incoming requests and sends them as messages to RabbitMQ.
 */
@WebServlet(name = "SkierServlet", value = "/skiers/*")
public class SkierServlet extends HttpServlet {

    private static final String QUEUE_NAME = "liftRideQueue";
    private Connection connection;
    private BlockingQueue<Channel> channelPool;

    @Override
    public void init() throws ServletException {
        super.init();
        try {
            // 创建 RabbitMQ 连接工厂
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost("localhost"); // 本地测试 RabbitMQ，生产环境中需要用对应的主机地址
            factory.setPort(5672);
            factory.setUsername("guest");
            factory.setPassword("guest");

            // 创建连接
            connection = factory.newConnection();

            // 初始化通道池
            int poolSize = 10; // 可以根据系统负载调整通道池大小
            channelPool = new LinkedBlockingQueue<>(poolSize);
            for (int i = 0; i < poolSize; i++) {
                channelPool.add(connection.createChannel());
            }
        } catch (Exception e) {
            throw new ServletException("Failed to initialize RabbitMQ connection", e);
        }
    }

    @Override
    public void destroy() {
        super.destroy();
        // 关闭 RabbitMQ 连接和通道
        try {
            for (Channel channel : channelPool) {
                channel.close();
            }
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");

        String urlPath = req.getPathInfo();
        Gson gson = new Gson();

        if (urlPath == null || urlPath.isEmpty()) {
            res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            res.getWriter().write("Missing parameters in URL path");
            return;
        }

        String[] urlParts = urlPath.split("/");

        if (!isUrlValid(urlParts)) {
            res.setStatus(HttpServletResponse.SC_NOT_FOUND);
            ResponseMsg msg = new ResponseMsg("URL NOT FOUND");
            res.getWriter().write(gson.toJson(msg));
            return;
        }

        try {
            // 1. 读取客户端发送的请求数据
            String requestData = req.getReader().lines().reduce("", (accumulator, actual) -> accumulator + actual);
            System.out.println("Received data: " + requestData);

            // 2. 从请求中解析 LiftRide 数据
            LiftRide liftRide = gson.fromJson(requestData, LiftRide.class);
            if (liftRide == null) {
                throw new IllegalArgumentException("Invalid LiftRide data");
            }
            System.out.println("Parsed LiftRide: " + liftRide);

            // 从通道池中获取一个通道
            Channel channel = channelPool.take();

            try {
                // 3. 发送消息到 RabbitMQ
                String message = gson.toJson(liftRide);
                channel.basicPublish("", QUEUE_NAME, MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes("UTF-8"));
                System.out.println(" [x] Sent '" + message + "' to RabbitMQ");

                // 设置响应状态和返回信息
                res.setStatus(HttpServletResponse.SC_CREATED);
                ResponseMsg msg = new ResponseMsg("Successful Created");
                res.getWriter().write(gson.toJson(msg));
            } finally {
                // 将通道放回通道池
                channelPool.put(channel);
            }
        } catch (IllegalArgumentException e) {
            // 处理请求体数据错误的情况
            res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            ResponseMsg msg = new ResponseMsg("Invalid LiftRide Data: " + e.getMessage());
            res.getWriter().write(gson.toJson(msg));
            System.err.println("Invalid LiftRide Data: " + e.getMessage());
        } catch (Exception ex) {
            // 处理其他异常并返回 500 响应
            res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            ResponseMsg msg = new ResponseMsg("Internal Server Error: Failed to Create LiftRide");
            res.getWriter().write(gson.toJson(msg));
            System.err.println("Failed to process request: " + ex.getMessage());
            ex.printStackTrace();
        }
    }



    /**
     * Validates the URL path to ensure it matches the expected format and contains valid values.
     *
     * @param urlPath A String array representing the parts of the URL path.
     * @return boolean - true if the URL path is valid, false otherwise.
     */
    private boolean isUrlValid(String[] urlPath) {
        if (urlPath.length == 8) {
            return urlPath[1].chars().allMatch(Character::isDigit) && urlPath[2].equals("seasons") &&
                    urlPath[3].chars().allMatch(Character::isDigit) && urlPath[4].equals("days") &&
                    urlPath[5].chars().allMatch(Character::isDigit) && urlPath[6].equals("skiers") &&
                    urlPath[7].chars().allMatch(Character::isDigit) && Integer.parseInt(urlPath[5]) >= 1 &&
                    Integer.parseInt(urlPath[5]) <= 365;
        }
        return false;
    }
}
