package Client2;

/**
 * The Record class represents a record of a single HTTP request made by the client.
 * It stores details about the request, such as its start time, request type (e.g., POST),
 * latency (the time taken to complete the request), and the response code returned by the server.
 * This class also implements the {@link Comparable} interface to allow comparison of records based
 * on their latency values.
 */
public class Record implements Comparable<Record> {

    private long startTime;
    private String requestType;
    private long latency;
    private int responseCode;

    /**
     * Constructs a new Record object with the specified request details.
     *
     * @param startTime    the timestamp when the request was made
     * @param requestType  the type of the request (e.g., POST)
     * @param latency      the latency of the request in milliseconds
     * @param responseCode the HTTP response code returned by the server
     */
    public Record(long startTime, String requestType, long latency, int responseCode) {
        this.startTime = startTime;
        this.requestType = requestType;
        this.latency = latency;
        this.responseCode = responseCode;
    }

    /**
     * Returns a string representation of the record.
     * The format is: startTime,requestType,latency,responseCode
     *
     * @return a string representation of the record
     */
    @Override
    public String toString() {
        return startTime + "," + requestType + "," + latency + "," + responseCode + "\n";
    }

    /**
     * Gets the start time of the request.
     *
     * @return the start time of the request
     */
    public long getStartTime() {
        return startTime;
    }

    /**
     * Sets the start time of the request.
     *
     * @param startTime the new start time of the request
     */
    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    /**
     * Gets the type of the request (e.g., POST).
     *
     * @return the type of the request
     */
    public String getRequestType() {
        return requestType;
    }

    /**
     * Sets the type of the request.
     *
     * @param requestType the new type of the request
     */
    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }

    /**
     * Gets the latency of the request in milliseconds.
     *
     * @return the latency of the request
     */
    public long getLatency() {
        return latency;
    }

    /**
     * Sets the latency of the request.
     *
     * @param latency the new latency of the request in milliseconds
     */
    public void setLatency(long latency) {
        this.latency = latency;
    }

    /**
     * Gets the HTTP response code returned by the server.
     *
     * @return the HTTP response code
     */
    public int getResponseCode() {
        return responseCode;
    }

    /**
     * Sets the HTTP response code.
     *
     * @param responseCode the new HTTP response code
     */
    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    /**
     * Compares this record to another record based on their latency values.
     *
     * @param o the other record to compare to
     * @return a negative integer, zero, or a positive integer as this record's latency is
     *         less than, equal to, or greater than the specified record's latency
     */
    @Override
    public int compareTo(Record o) {
        return (int) (this.getLatency() - o.getLatency());
    }
}
