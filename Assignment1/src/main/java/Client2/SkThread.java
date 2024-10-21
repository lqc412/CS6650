package Client2;

import io.swagger.client.ApiClient;
import io.swagger.client.ApiException;
import io.swagger.client.ApiResponse;
import io.swagger.client.api.SkiersApi;
import io.swagger.client.model.LiftRide;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;

/**
 * SkThread is responsible for sending multiple POST requests to the skier API,
 * simulating skier lift rides. It handles retries for each request and records
 * response times and status codes for throughput analysis.
 */
public class SkThread extends Thread {
    private static final int RETRY_TIMES = 5;
    private BlockingQueue<LiftRide> rideQueue;
    private CountDownLatch curLatch;
    private ApiClient apiClient;
    private int numRequests;

    public SkThread(BlockingQueue<LiftRide> rideQueue, CountDownLatch curLatch, ApiClient apiClient, int numRequests) {
        this.rideQueue = rideQueue;
        this.curLatch = curLatch;
        this.apiClient = apiClient;
        this.numRequests = numRequests;
    }

    @Override
    public void run() {
        SkiersApi apiInstance = new SkiersApi(apiClient);

        for (int i = 0; i < numRequests; i++) {
            try {
                // 从共享队列中获取一个 LiftRide 事件
                LiftRide ride = rideQueue.take();
                SkEvent skierEvent = new SkEvent();

                // 尝试多次请求，最多重试 RETRY_TIMES 次
                for (int j = 0; j < RETRY_TIMES; j++) {
                    try {
                        long startTime = System.currentTimeMillis();
                        // 发送 POST 请求
                        ApiResponse<Void> res = apiInstance.writeNewLiftRideWithHttpInfo(
                                ride, skierEvent.getResortID(), skierEvent.getSeasonID(), skierEvent.getDayID(), skierEvent.getSkierID());
                        long endTime = System.currentTimeMillis();

                        // 记录成功请求的详细信息
                        RecordProcessor.records.add(new Record(startTime, "POST", endTime - startTime, res.getStatusCode()));
                        SkClient2.counter.incrementSuccessfulPost(1);
                        break; // 请求成功后退出重试循环
                    } catch (ApiException e) {
                        if (e.getCode() >= 400 && e.getCode() < 600) {
                            // 记录失败请求，并根据状态码决定是否重试
                            SkClient2.counter.incrementFailedPost(1);
                            System.err.println("请求失败，HTTP 状态码: " + e.getCode() + "，重试次数: " + (j + 1));
                        }
                        // 在重试次数达到上限时打印错误并停止重试
                        if (j == RETRY_TIMES - 1) {
                            System.err.println("请求多次失败，放弃请求");
                            e.printStackTrace();
                        }
                    }
                }
            } catch (InterruptedException e) {
                // 捕获线程中断异常
                System.err.println("线程被中断：" + e.getMessage());
                e.printStackTrace();
            }
        }

        // 当前线程处理完成，减少 CountDownLatch 的计数
        curLatch.countDown();
    }
}
