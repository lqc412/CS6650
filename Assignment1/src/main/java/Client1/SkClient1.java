package Client1;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * Client1 class is responsible for simulating a multi-threaded client
 * that performs various phases of HTTP requests to a skier server.
 * The phases simulate the load of skier lift rides at a ski resort.
 */
public class SkClient1 {

    protected static CountDownLatch latchToPhase2 = new CountDownLatch(1);
    protected static Counter counter = new Counter();

    /**
     * The main method is the entry point of the program. It starts by executing
     * two main phases of multi-threaded requests with configurable thread counts
     * and request sizes. Each phase runs a set number of threads that send requests
     * to the server, and the results are calculated once all phases are complete.
     *
     * @param args command line arguments
     * @throws InterruptedException when the thread is interrupted while waiting
     * @throws IOException          when an I/O error occurs
     */
    public static void main(String[] args) throws InterruptedException, IOException {
        // Phase I
        long phase1Start = System.currentTimeMillis();
        int numP1Threads = 32;
        int numP1Requests = 1000;
        CountDownLatch phase1Latch = new CountDownLatch(numP1Threads);
        executePhase("Phase1", numP1Threads, numP1Requests, phase1Latch);
        long phase1End = System.currentTimeMillis();

        long phase1Duration = phase1End - phase1Start;
        int phase1Requests = numP1Threads * numP1Requests;
        double phase1Throughput = (double) phase1Requests / (phase1Duration / 1000.0);

        System.out.println("\nPhase 1 Result:");
        System.out.println("-".repeat(30));
        System.out.println("Phase 1 Duration: " + phase1Duration + " ms");
        System.out.println("Phase 1 Throughput: " + phase1Throughput + " requests/sec");

        latchToPhase2.await();

        // Phase II
        int numP2Threads = 112;
        int numP2Requests = 1500;
        CountDownLatch phase2Latch = new CountDownLatch(numP2Threads);
        executePhase("Phase2", numP2Threads, numP2Requests, phase2Latch);

        phase1Latch.await();
        phase2Latch.await();
        long end = System.currentTimeMillis();

        long wallTime = end - phase1Start;
        int success = counter.getSuccessfulPosts();
        int failed = counter.getFailedPosts();
        long throughput = 1000 * (success + failed) / wallTime;

        System.out.println("\nClient Result:");
        System.out.println("-".repeat(30));
        System.out.println("Number of successful requests: " + success);
        System.out.println("Number of failed requests: " + failed);
        System.out.println("Total wall time: " + wallTime + " ms");
        System.out.println("Total Throughput (requests/sec): " + throughput);
        System.out.println("Phase duration: " + (end - phase1Start) + " ms");
    }

    /**
     * Executes a phase of requests by creating the specified number of threads,
     * each sending the specified number of requests. After starting all threads,
     * it waits for all of them to complete.
     *
     * @param phaseName       name of the phase (for logging purposes)
     * @param numberOfThreads the number of threads to spawn
     * @param numOfRequests   the number of requests each thread should send
     * @param latch           a CountDownLatch used for synchronization
     * @throws InterruptedException if the thread is interrupted while waiting for all threads to finish
     */
    private static void executePhase(String phaseName, int numberOfThreads, int numOfRequests, CountDownLatch latch) throws InterruptedException {
        System.out.println(phaseName + " is starting...");
        for (int i = 0; i < numberOfThreads; i++) {
            SkThread skierThread = new SkThread(numOfRequests, latch);
            skierThread.start();
        }
        latch.await();
        System.out.println(phaseName + " completed " + (numOfRequests * numberOfThreads) + " requests");
    }
}
