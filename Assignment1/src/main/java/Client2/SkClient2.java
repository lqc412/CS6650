package Client2;

import io.swagger.client.ApiClient;
import io.swagger.client.model.LiftRide;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.ConnectionPool;


import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * The SkClient2 class simulates multiple threads that generate and send
 * POST requests to simulate skier lift rides. The goal is to send 200,000
 * requests as fast as possible.
 */
public class SkClient2 {

    protected static Counter counter = new Counter();

    public static void main(String[] args) throws InterruptedException, IOException {

        // 记录程序开始时间
        long startTime = System.currentTimeMillis();

        // 创建一个线程安全的队列，用于存储 LiftRide 事件
        BlockingQueue<LiftRide> rideQueue = new LinkedBlockingQueue<>();

        // 启动 LiftRide 事件生成器，生成 200,000 个请求
        System.out.println("Generating 200,000 lift ride events...");
        LiftRideEventGenerator generator = new LiftRideEventGenerator(rideQueue, 200_000);
        generator.start();
        generator.join(); // 等待事件生成完成

        // 创建共享的 ApiClient 实例
        OkHttpClient httpClient = new OkHttpClient();
        httpClient.setConnectionPool(new ConnectionPool(50, 5, TimeUnit.MINUTES)); // 设置连接池
        ApiClient sharedClient = new ApiClient();
        sharedClient.setHttpClient(httpClient);
        sharedClient.setBasePath("http://35.162.51.174:8080/Server_war");

        // 第一阶段：启动 32 个线程，每个线程发送 1000 个请求
        int numPhase1Threads = 32;
        int numPhase1Requests = 1000;
        CountDownLatch phase1Latch = new CountDownLatch(numPhase1Threads);
        System.out.println("\nStarting Phase 1 with " + numPhase1Threads + " threads...");

        for (int i = 0; i < numPhase1Threads; i++) {
            SkThread skThread = new SkThread(rideQueue, phase1Latch, sharedClient, numPhase1Requests);
            skThread.start();
        }

        phase1Latch.await(); // 等待所有线程完成

        // 第二阶段：启动 64 个线程，发送剩余的请求
        int remainingRequests = 200_000 - numPhase1Threads * numPhase1Requests; // 剩余请求数
        int numPhase2Threads = 64;
        int numPhase2Requests = remainingRequests / numPhase2Threads;
        CountDownLatch phase2Latch = new CountDownLatch(numPhase2Threads);
        System.out.println("\nStarting Phase 2 with " + numPhase2Threads + " threads...");

        for (int i = 0; i < numPhase2Threads; i++) {
            SkThread skThread = new SkThread(rideQueue, phase2Latch, sharedClient, numPhase2Requests);
            skThread.start();
        }

        phase2Latch.await(); // 等待所有线程完成

        // 记录程序结束时间
        long endTime = System.currentTimeMillis();
        long wallTime = endTime - startTime; // 总运行时间，单位为毫秒

        // 计算结果并输出
        System.out.println("\nAll requests have been completed.");
        System.out.println("Number of successful requests: " + counter.getSuccessfulPosts());
        System.out.println("Number of failed requests: " + counter.getFailedPosts());

        // 调用 RecordProcessor 进行数据计算和输出，传入正确的开始和结束时间
        String outputFilePath = "./output.csv"; // 你可以修改输出文件路径
        RecordProcessor recordProcessor = new RecordProcessor(outputFilePath, startTime, endTime);
        recordProcessor.calculateOutput();

    }
}
