package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;


/**
 * Represents a Corridor, with all of its' properties such as the size and any people within it.

 */
public class Corridor implements Serializable {
    private ArrayList<Pedestrian> pedestrianList;
    private final double WIDTH;
    private final double HEIGHT;

    public Corridor(int numberOfPedestrians, double width, double height){
        this.WIDTH = width;
        this.HEIGHT = height;
        this.pedestrianList = new ArrayList<>();
        initPedestrianList(numberOfPedestrians);
    }

    public ArrayList<Pedestrian> pedestrianList(){
        return this.pedestrianList;
    }

    private void initPedestrianList(int numberOfPedestrians){
        Random random = new Random();
        for (int i = 0; i < numberOfPedestrians; i++){
            Pedestrian pedestrian = new Pedestrian(random.nextInt((int) WIDTH),random.nextInt((int) HEIGHT));
            pedestrianList.add(pedestrian);
        }
    }

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

    public double progressReport(){
        double pedestriansInGoal = 0;
        for (Pedestrian pedestrian: this.pedestrianList) {
            if(pedestrian.hasReachedGoal())
                pedestriansInGoal++;
        }
        return (pedestriansInGoal/this.pedestrianList.size());
    }
}
