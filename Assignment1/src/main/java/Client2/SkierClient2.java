package Client2;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * The SkierClient2 class simulates multiple phases of skier lift ride requests
 * sent to a server using multi-threading. The class handles synchronization
 * between different phases using CountDownLatch, ensuring that each phase starts
 * and finishes in a sequential manner.
 */
public class SkierClient2 {

    protected static CountDownLatch latchToPhase2 = new CountDownLatch(1);
    protected static CountDownLatch latchToPhase3 = new CountDownLatch(1);
    protected static Counter counter = new Counter();

    /**
     * The main method orchestrates the multi-phase request simulation. It starts
     * by executing Phase I, followed by Phase II, Phase III, and Phase IV. Each phase
     * launches a specific number of threads that each send a defined number of POST requests.
     *
     * @param args Command-line arguments (not used)
     * @throws InterruptedException If thread execution is interrupted while waiting
     * @throws IOException          If an I/O error occurs during file writing
     */
    public static void main(String[] args) throws InterruptedException, IOException {

        // Phase I
        long start = System.currentTimeMillis();
        int numP1Threads = 32;
        int numP1Requests = 1000;
        CountDownLatch curLatch1 = new CountDownLatch(numP1Threads);
        doPhase("phase1", numP1Threads, numP1Requests, curLatch1);

        // Begin Phase II
        latchToPhase2.await();
        int numP2Threads = 28;
        int numP2Requests = 2000;
        CountDownLatch curLatch2 = new CountDownLatch(numP2Threads);
        doPhase("phase2", numP2Threads, numP2Requests, curLatch2);

        // Ensure Phase III starts after Phase II completes
        curLatch2.await();
        latchToPhase3.countDown();

        // Begin Phase III
        latchToPhase3.await();
        int numP3Threads = 28;
        int numP3Requests = 2000;
        CountDownLatch curLatch3 = new CountDownLatch(numP3Threads);
        doPhase("phase3", numP3Threads, numP3Requests, curLatch3);

        // Ensure Phase IV starts after Phase III completes
        curLatch3.await();
        latchToPhase3.countDown();
        int numP4Threads = 28;
        int numP4Requests = 2000;
        CountDownLatch curLatch4 = new CountDownLatch(numP4Threads);
        doPhase("phase4", numP4Threads, numP4Requests, curLatch4);

        // Wait for all phases to complete
        curLatch1.await();
        curLatch2.await();
        curLatch3.await();
        curLatch4.await();
        long end = System.currentTimeMillis();

        // Calculate and print results
        long wallTime = end - start;
        int success = counter.getSuccessfulPosts();
        int failed = counter.getFailedPosts();
        long throughput = 1000 * (success + failed) / wallTime;

        System.out.println("\nClient Part 1 Result:");
        System.out.println("-".repeat(30));
        System.out.println("Number of successful requests sent: " + success);
        System.out.println("Number of unsuccessful requests: " + failed);
        System.out.println("The total run time (wall time): " + wallTime + " milliseconds");
        System.out.println("The total throughput per second: " + throughput);

        // Process records
        System.out.println("\nClient Part 2 Result:");
        System.out.println("-".repeat(30));
        new RecordProcessor("./output.csv").calculateOutput();

        System.out.println("Phase duration: " + (end - start));
    }

    /**
     * Executes a phase by creating and starting the specified number of threads.
     * Each thread sends a given number of POST requests to simulate lift rides.
     * The method waits for all threads in the phase to finish before proceeding.
     *
     * @param phaseName        The name of the phase (used for logging purposes)
     * @param numberOfThreads  The number of threads to start in this phase
     * @param numOfRequests    The number of requests each thread will send
     * @param curLatch         The CountDownLatch to synchronize the phase's execution
     * @throws InterruptedException If thread execution is interrupted while waiting
     */
    private static void doPhase(String phaseName, int numberOfThreads, int numOfRequests, CountDownLatch curLatch) throws InterruptedException {
        System.out.println(phaseName + " is starting...");
        for (int i = 0; i < numberOfThreads; i++) {
            SkierThread skierThread = new SkierThread(numOfRequests, curLatch);
            skierThread.start();
        }
        curLatch.await(); // Wait for all threads to complete
        System.out.println(phaseName + " completed " + (numOfRequests * numberOfThreads) + " requests");
    }
}
