package Client2;

import java.util.Random;

/**
 * The SkierEvent class is responsible for generating and holding the details
 * of a skier's event, including the resort ID, season ID, day ID, and skier ID.
 * These details are randomly generated for each skier event, simulating a ski lift ride.
 */
public class SkierEvent {

    private Integer resortID;
    private String seasonID;
    private String dayID;
    private Integer skierID;

    /**
     * Constructs a SkierEvent with randomly generated values for resortID and skierID.
     * The seasonID is set to "2024" and dayID is set to "1" by default.
     */
    public SkierEvent() {
        Random rand = new Random();
        this.resortID = rand.nextInt(11);
        this.seasonID = "2024";
        this.dayID = "1";
        this.skierID = rand.nextInt(100001);
    }

    /**
     * Gets the ID of the resort where the event took place.
     *
     * @return the resort ID
     */
    public Integer getResortID() {
        return resortID;
    }

    /**
     * Sets the ID of the resort where the event took place.
     *
     * @param resortID the resort ID
     */
    public void setResortID(Integer resortID) {
        this.resortID = resortID;
    }

    /**
     * Gets the season ID for the skier's event.
     *
     * @return the season ID
     */
    public String getSeasonID() {
        return seasonID;
    }

    /**
     * Sets the season ID for the skier's event.
     *
     * @param seasonID the season ID
     */
    public void setSeasonID(String seasonID) {
        this.seasonID = seasonID;
    }

    /**
     * Gets the day ID for the skier's event.
     *
     * @return the day ID
     */
    public String getDayID() {
        return dayID;
    }

    /**
     * Sets the day ID for the skier's event.
     *
     * @param dayID the day ID
     */
    public void setDayID(String dayID) {
        this.dayID = dayID;
    }

    /**
     * Gets the ID of the skier.
     *
     * @return the skier ID
     */
    public Integer getSkierID() {
        return skierID;
    }

    /**
     * Sets the ID of the skier.
     *
     * @param skierID the skier ID
     */
    public void setSkierID(Integer skierID) {
        this.skierID = skierID;
    }
}
