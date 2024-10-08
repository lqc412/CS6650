package Client2;

/**
 * The Counter class is responsible for keeping track of the number of successful and
 * failed POST requests during the client's execution. It provides synchronized methods
 * for incrementing these counts to ensure thread safety in a multi-threaded environment.
 */
public class Counter {

    private int successfulPosts;  // The count of successful POST requests
    private int failedPosts;      // The count of failed POST requests

    /**
     * Constructs a Counter instance and initializes the successful and failed
     * request counts to zero.
     */
    public Counter() {
        this.successfulPosts = 0;
        this.failedPosts = 0;
    }

    /**
     * Synchronized method to increment the count of successful POST requests.
     *
     * @param increment the number to add to the successful post count
     */
    public synchronized void incrementSuccessfulPost(int increment) {
        this.successfulPosts += increment;
    }

    /**
     * Synchronized method to increment the count of failed POST requests.
     *
     * @param increment the number to add to the failed post count
     */
    public synchronized void incrementFailedPost(int increment) {
        this.failedPosts += increment;
    }

    /**
     * Returns the total count of successful POST requests.
     *
     * @return the number of successful POST requests
     */
    public int getSuccessfulPosts() {
        return this.successfulPosts;
    }

    /**
     * Returns the total count of failed POST requests.
     *
     * @return the number of failed POST requests
     */
    public int getFailedPosts() {
        return this.failedPosts;
    }
}
