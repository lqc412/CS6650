package Client2;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * The RecordProcessor class is responsible for handling the processing of request records,
 * including writing them to a CSV file and calculating statistical metrics such as mean,
 * median, 99th percentile, and throughput based on recorded latency data.
 */
public class RecordProcessor {

    private FileWriter csvWriter;
    protected static CopyOnWriteArrayList<Record> records = new CopyOnWriteArrayList<>();

    /**
     * Constructs a RecordProcessor instance that writes output to the specified CSV file.
     * The CSV file will be initialized with a header row.
     *
     * @param filePath the path to the CSV file for storing the request records
     * @throws IOException if an I/O error occurs when opening the file
     */
    public RecordProcessor(String filePath) throws IOException {
        csvWriter = new FileWriter(filePath);
        csvWriter.append("startTime,requestType,latency,responseCode\n");
    }

    /**
     * Adds a single request record to the CSV file.
     *
     * @param record the record to add to the CSV
     * @throws IOException if an I/O error occurs when writing to the file
     */
    public void addRecordToCSV(Record record) throws IOException {
        csvWriter.append(record.toString());
    }

    /**
     * Calculates and prints key statistics based on the recorded request latencies.
     * Statistics include the mean, median, throughput, 99th percentile latency, and the
     * minimum and maximum latencies. The method also writes all records to the CSV file.
     *
     * @throws IOException if an I/O error occurs when writing records to the CSV file
     */
    public void calculateOutput() throws IOException {
        Collections.sort(records);

        double min = Double.MAX_VALUE;
        double max = 0;
        double sum = 0;

        double median = records.get((int) (0.5 * records.size())).getLatency();
        double p99 = records.get((int) (0.99 * records.size())).getLatency();

        for (Record record : records) {
            sum += record.getLatency();
            max = Math.max(max, record.getLatency());
            min = Math.min(min, record.getLatency());
            addRecordToCSV(record);
        }

        double mean = sum / records.size();
        double throughput = 1000 * records.size() / sum;

        System.out.println(records.size());
        System.out.println(sum);
        System.out.println(
                "Mean response time: " + mean + "\n"
                        + "Median response time: " + median + "\n"
                        + "Throughput: " + throughput + "\n"
                        + "99th response time: " + p99 + "\n"
                        + "Min and max response time: " + "min: " + min + " , max: " + max
        );
    }
}
