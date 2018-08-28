package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;


/**
 * Represents a Corridor, with all of its properties such as the size and any pedestrians within it.
 */
public class Corridor implements Serializable {
    private ArrayList<Pedestrian> pedestrianList;
    private final double WIDTH;
    private final double HEIGHT;
    private final int INITIAL_NUMBER_OF_PEDESTRIANS;

    /**
     * Creates a Corridor object.
     * @param numberOfPedestrians The starting number of pedestrians within the corridor
     * @param width The corridors width
     * @param height The corridors height
     */
    public Corridor(int numberOfPedestrians, double width, double height){
        this.WIDTH = width;
        this.HEIGHT = height;
        this.pedestrianList = new ArrayList<>();
        this.INITIAL_NUMBER_OF_PEDESTRIANS = numberOfPedestrians;
        initPedestrianList();
    }

    public ArrayList<Pedestrian> getPedestrianList(){
        return this.pedestrianList;
    }

    /**
     * Initiates the pedestrianList
     */
    public void initPedestrianList(){
        Random random = new Random();
        pedestrianList.clear();
        for (int i = 0; i < INITIAL_NUMBER_OF_PEDESTRIANS; i++){
            Pedestrian pedestrian = new Pedestrian(random.nextInt((int) WIDTH),random.nextInt((int) HEIGHT));
            pedestrianList.add(pedestrian);
        }
    }

    /**
     * A function for moving pedestrians within a certain segment of the corridor.
     * @see Pedestrian
     * @param startOfSegment Represents the start of the corridor segment handled by this method
     * @param endOfSegment Represents the end of the corridor segment handled by this method
     * @return Returns a list of the pedestrians moved within the corridor segment.
     */
    public ArrayList<Pedestrian> pedestriansMovedWithinSegment(double startOfSegment, double endOfSegment){
        ArrayList<Pedestrian> movedPedestrians = new ArrayList<>();
        for(Pedestrian p: this.pedestrianList){
            if((p.getX() >= startOfSegment) && (p.getX() < endOfSegment)) {
                p.move(this);
                movedPedestrians.add(p);
            }
        }
        return movedPedestrians;
    }

    /**
     * Replaces a pedestrian object within the corridor if, if one exists with the same ID.
     * @see Pedestrian
     * @param pedestrian The pedestrian object to find and replace within the corridor
     */
    public void editPedestrianInCorridor(Pedestrian pedestrian) {
        if (this.pedestrianList.contains(pedestrian)) {
            if (pedestrian.hasReachedGoal()) {
                synchronized (this) {
                    this.pedestrianList.remove(pedestrian);
                }
            } else
                this.pedestrianList.set(this.pedestrianList.indexOf(pedestrian), pedestrian);
        }
    }

    /**
     * Adds a new pedestrian in the corridor on a random side of the corridor
     * @see Pedestrian
     */
    public void addNewPedestrian(){
        synchronized (this) {
            Pedestrian pedestrian = new Pedestrian(this);
            pedestrianList.add(pedestrian);
        }
    }

    public double getWidth(){
        return WIDTH;
    }

    public double getHeight(){
        return HEIGHT;
    }
}
