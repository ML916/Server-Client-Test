package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

public class Corridor implements Serializable {
    public ArrayList<Pedestrian> pedestrianList;
    private final double width;
    private final double height;
    //public transient ObservableList<Circle> pedestrianList;

    public Corridor(int numberOfPedestrians, double width, double height){
        this.width = width;
        this.height = height;
        this.pedestrianList = new ArrayList<>();
        initPedestrianList(numberOfPedestrians);
    }

    private void initPedestrianList(int numberOfPedestrians){
        Random random = new Random();
        for (int i = 0; i < numberOfPedestrians; i++){
            Pedestrian pedestrian = new Pedestrian(random.nextInt((int) width),random.nextInt((int) height));
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

    public void addNewPedestrian(){
        synchronized (this) {
            Pedestrian pedestrian = new Pedestrian(this);
            pedestrianList.add(pedestrian);
        }
    }

    public boolean removePedestriansInGoalArea(){
        boolean isRemovingPedestrian = false;
        synchronized(this) {
            for (Pedestrian pedestrian : this.pedestrianList) {
                if (pedestrian.hasReachedGoal()) {
                    pedestrianList.remove(pedestrian);
                    isRemovingPedestrian = true;
                }
            }
        }
        return isRemovingPedestrian;
    }

    public double getWidth(){
        return width;
    }

    public double getHeight(){
        return height;
    }

    public ArrayList<Pedestrian> getPedestrianList() {
        return pedestrianList;
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
