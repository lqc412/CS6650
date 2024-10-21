package Client2;

import java.util.Random;
import java.util.concurrent.BlockingQueue;
import io.swagger.client.model.LiftRide;


class LiftRideEventGenerator extends Thread {
    private BlockingQueue<LiftRide> rideQueue;
    private int numberOfRequests;

    public LiftRideEventGenerator(BlockingQueue<LiftRide> rideQueue, int numberOfRequests) {
        this.rideQueue = rideQueue;
        this.numberOfRequests = numberOfRequests;
    }

    @Override
    public void run() {
        Random rand = new Random();
        for (int i = 0; i < numberOfRequests; i++) {
            LiftRide ride = new LiftRide().time(rand.nextInt(360) + 1).liftID(rand.nextInt(40) + 1);
            try {
                rideQueue.put(ride);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
