package Client2;

import io.swagger.client.ApiClient;
import io.swagger.client.ApiException;
import io.swagger.client.ApiResponse;
import io.swagger.client.api.SkiersApi;
import io.swagger.client.model.LiftRide;

import java.util.Random;
import java.util.concurrent.CountDownLatch;

/**
 * SkThread is responsible for sending multiple POST requests to the skier API,
 * simulating skier lift rides. It handles retries for each request and records
 * response times and status codes for throughput analysis.
 */
public class SkThread extends Thread {

    private static final int RETRY_TIMES = 5;  // Number of retries for each request

    private final Integer numberOfRequests;    // Number of POST requests to send in this thread
    private final CountDownLatch curLatch;     // Latch to synchronize the completion of the thread

    /**
     * Constructs a SkThread with the specified number of requests and a latch
     * to count down when the thread has finished its work.
     *
     * @param numberOfRequests the number of POST requests to send in this thread
     * @param curLatch         the CountDownLatch to signal thread completion
     */
    public SkThread(Integer numberOfRequests, CountDownLatch curLatch) {
        this.numberOfRequests = numberOfRequests;
        this.curLatch = curLatch;
    }

    /**
     * Executes the thread logic to send POST requests to the skier API.
     * For each request, it retries up to {@code RETRY_TIMES} in case of failure
     * and records the response time and status code for each successful request.
     */
    @Override
    public void run() {
        SkiersApi apiInstance = new SkiersApi();
        ApiClient client = apiInstance.getApiClient();
        String serverUrl = "http://52.33.1.66:8080/Server_war";
        client.setBasePath(serverUrl);
        Random rand = new Random();

        // Loop to send the designated number of POST requests
        for (int i = 0; i < numberOfRequests; i++) {
            LiftRide ride = new LiftRide().time(rand.nextInt(361)).liftID(rand.nextInt(41));
            SkEvent skEvent = new SkEvent();

            // Retry logic for each request
            for (int j = 0; j < RETRY_TIMES; j++) {
                try {
                    long startTime = System.currentTimeMillis();
                    ApiResponse<Void> res = apiInstance.writeNewLiftRideWithHttpInfo(
                            ride,
                            skEvent.getResortID(),
                            skEvent.getSeasonID(),
                            skEvent.getDayID(),
                            skEvent.getSkierID()
                    );
                    SkClient2.counter.incrementSuccessfulPost(1);
                    long endTime = System.currentTimeMillis();

                    // Record the request details for analysis
                    RecordProcessor.records.add(new Record(startTime, "POST", endTime - startTime, res.getStatusCode()));
                    break;
                } catch (ApiException e) {
                    SkClient2.counter.incrementFailedPost(1);
                    System.err.println("Exception when calling SkierApi#writeNewLiftRide, tried " + j + " times");
                    e.printStackTrace();
                }
            }
        }

        // Signal that this thread is done
        try {
            SkClient2.latchToPhase2.countDown();
            curLatch.countDown();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
