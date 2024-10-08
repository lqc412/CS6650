package Client1;

/**
 * Counter class is used to keep track of the number of successful and failed POST requests
 * made by the SkierThread instances. The methods are synchronized to ensure thread safety 
 * when multiple threads are incrementing the counts concurrently.
 */
public class Counter {
    private int successfulPosts;  // Stores the count of successful POST requests
    private int failedPosts;      // Stores the count of failed POST requests

    /**
     * Constructs a Counter with initial counts of 0 for both successful and failed posts.
     */
    public Counter() {
        this.successfulPosts = 0;
        this.failedPosts = 0;
    }

    /**
     * Increments the count of successful POST requests.
     *
     * @param increment the amount to increase the successful post count by
     */
    public synchronized void incrementSuccessfulPost(int increment) {
        this.successfulPosts += increment;
    }

    /**
     * Increments the count of failed POST requests.
     *
     * @param increment the amount to increase the failed post count by
     */
    public synchronized void incrementFailedPost(int increment) {
        this.failedPosts += increment;
    }

    /**
     * Retrieves the current count of successful POST requests.
     *
     * @return the number of successful POST requests
     */
    public int getSuccessfulPosts() {
        return this.successfulPosts;
    }

    /**
     * Retrieves the current count of failed POST requests.
     *
     * @return the number of failed POST requests
     */
    public int getFailedPosts() {
        return this.failedPosts;
    }
}
