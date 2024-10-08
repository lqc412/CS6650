package Client1;

import io.swagger.client.ApiClient;
import io.swagger.client.ApiException;
import io.swagger.client.ApiResponse;
import io.swagger.client.api.SkiersApi;
import io.swagger.client.model.LiftRide;

import java.util.Random;
import java.util.concurrent.CountDownLatch;

/**
 * SkierThread represents a thread that sends a set number of POST requests
 * to the server to simulate skier lift rides. The thread handles retries
 * and tracks the success or failure of each request.
 */
public class SkierThread extends Thread {

    private static final int RETRY_LIMIT = 5; // Number of retry attempts for failed requests
    private final int requestCount; // Number of POST requests to be sent by this thread
    private final CountDownLatch latch; // Countdown latch to signal thread completion

    /**
     * Constructs a SkierThread instance with the specified number of requests
     * and a CountDownLatch to track the thread's completion.
     *
     * @param requestCount the number of POST requests to send
     * @param latch        the latch to count down when the thread completes
     */
    public SkierThread(int requestCount, CountDownLatch latch) {
        this.requestCount = requestCount;
        this.latch = latch;
    }

    /**
     * Executes the thread's logic by sending POST requests to simulate lift rides.
     * Each request is retried up to the retry limit in case of failure.
     */
    @Override
    public void run() {
        SkiersApi apiInstance = new SkiersApi();
        ApiClient client = apiInstance.getApiClient();
//        String serverUrl = "http://localhost:8080/Server_war_exploded";
        String serverUrl = "http://54.200.139.240:8080/Server_war";
        client.setBasePath(serverUrl);
        Random random = new Random();

        // Loop to send the designated number of POST requests
        for (int i = 0; i < requestCount; i++) {
            LiftRide ride = new LiftRide().time(random.nextInt(361)).liftID(random.nextInt(41));
            SkierEvent skierEvent = new SkierEvent();

            // Retry logic for each request
            for (int retry = 0; retry < RETRY_LIMIT; retry++) {
                try {
                    long startTime = System.currentTimeMillis();
                    ApiResponse<Void> response = apiInstance.writeNewLiftRideWithHttpInfo(
                            ride,
                            skierEvent.getResortID(),
                            skierEvent.getSeasonID(),
                            skierEvent.getDayID(),
                            skierEvent.getSkierID()
                    );
                    SkierClient1.counter.incrementSuccessfulPost(1);
                    long endTime = System.currentTimeMillis();
                    // Optionally track the time taken for each request (useful in Client 2)
                    // RecordProcessor.records.add(new Record(startTime, "POST", endTime - startTime, response.getStatusCode()));
                    break;
                } catch (ApiException e) {
                    SkierClient1.counter.incrementFailedPost(1);
                    System.err.println("Exception when calling SkierApi#writeNewLiftRide, attempt " + (retry + 1) + " failed");
                    e.printStackTrace();
                }
            }

            // Phase I completion triggers Phase II
        }

        // Ensure proper countdown for both phases
        try {
            SkierClient1.latchToPhase2.countDown();
            latch.countDown();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
