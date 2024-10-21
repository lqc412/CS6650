package Client2;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.concurrent.CopyOnWriteArrayList;

public class RecordProcessor {

    protected static CopyOnWriteArrayList<Record> records = new CopyOnWriteArrayList<>();
    private String filePath;
    private long startTime;
    private long endTime;

    public RecordProcessor(String filePath, long startTime, long endTime) {
        this.filePath = filePath;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public void calculateOutput() throws IOException {
        Collections.sort(records);

        double min = Double.MAX_VALUE;
        double max = 0;
        double sum = 0;

        double median = records.get(records.size() / 2).getLatency();
        double p99 = records.get((int) (0.99 * records.size())).getLatency();

        for (Record record : records) {
            sum += record.getLatency();
            max = Math.max(max, record.getLatency());
            min = Math.min(min, record.getLatency());
        }

        double mean = sum / records.size();
        double wallTimeInSeconds = (endTime - startTime) / 1000.0; // 总运行时间，单位为秒
        double throughput = records.size() / wallTimeInSeconds; // 吞吐量：请求数 / 总运行时间

        // 打印统计结果
        System.out.println(records.size());
        System.out.println("Mean response time: " + mean + " ms");
        System.out.println("Median response time: " + median + " ms");
        System.out.println("Throughput: " + throughput + " requests/sec");
        System.out.println("99th percentile response time: " + p99 + " ms");
        System.out.println("Min response time: " + min + " ms");
        System.out.println("Max response time: " + max + " ms");

        // 写入 CSV 文件
        try (FileWriter csvWriter = new FileWriter(filePath)) {
            csvWriter.append("startTime,requestType,latency,responseCode\n");
            for (Record record : records) {
                csvWriter.append(record.toString());
            }
        }
    }
}
