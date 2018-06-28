package server;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import model.Corridor;

import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;

public class Pedestrian implements Serializable {
    public final int ID;
    private double x;
    private double y;
    private final double defaultMovementX;
    private final double defaultMovementY;
    private transient Perception perception;
    private boolean hasReachedGoal;
    public static int numberOfPedestrians = 0;
    public transient Circle circle;

    public Pedestrian(double x, double y){
        this.ID = Pedestrian.numberOfPedestrians;
        this.x = x;
        this.y = y;
        this.circle = new Circle(this.x,this.y,3.5, Color.RED);
        defaultMovementX = 2;
        defaultMovementY = 0;
        this.hasReachedGoal = false;
        Pedestrian.numberOfPedestrians++;
    }

    private void readObject(ObjectInputStream in) throws Exception{
        in.defaultReadObject();
        this.circle = new Circle(this.x,this.y,3.5, Color.RED);
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null){
            return false;
        }
        if(!Pedestrian.class.isAssignableFrom(obj.getClass())){
            return false;
        }
        final Pedestrian other = (Pedestrian) obj;
        if(this.ID == other.ID){
            return true;
        }
        else{
            return false;
        }
    }

    public void move(Corridor corridor){
        this.perception = new Perception(corridor);
        double [] socialForce = calculateSocialForce();
        this.x += defaultMovementX + socialForce[0];
        this.y += defaultMovementY + socialForce[1];
    }

    private double[] calculateSocialForce() {
        double forceX = 0.0;
        double forceY = 0.0;
        double distanceToUpperWall = Math.abs(perception.upperWall - this.y);
        double distanceToLowerWall = Math.abs(perception.lowerWall - this.y);
        //TODO: Denna loop måste ta hänsyn till väggar och hantera avstånd korrekt
        for(Pedestrian pedestrian : perception.pedestrians){
            if (!this.equals(pedestrian)) {
                double distanceX = pedestrian.getX() - this.x;
                double distanceY = pedestrian.getY() - this.y;

                if(distanceX <= 4 && distanceY <= 4) {
                    forceX -= distanceX/2;
                    forceY -= distanceY/2;
                }
            }
        }
        if(Math.abs(forceY) >= distanceToUpperWall || Math.abs(forceY) >= distanceToLowerWall){
            if(distanceToLowerWall > distanceToUpperWall){
                forceY = distanceToUpperWall - 2;
            }
            else{
                forceY = distanceToLowerWall + 2;
            }
        }

        return new double[] {forceX, forceY};
    }

    public double getX(){
        return x;
    }

    public double getY(){
        return y;
    }

    public class Perception {
        public final double perceptionRadius = 20;
        public ArrayList<Pedestrian> pedestrians;
        public final double upperWall;
        public final double lowerWall;

        public Perception(Corridor corridor){
            pedestrians = new ArrayList<Pedestrian>();
            upperWall = 0;
            lowerWall = corridor.getHeight();
            initPerception(corridor);
        }

        public void initPerception(Corridor corridor){
            for (Pedestrian p:corridor.getPedestrianList()) {
                if(Math.sqrt(Math.pow(p.getX(),2)+Math.pow(p.getY(),2)) < perceptionRadius){
                    pedestrians.add(p);
                }
            }
        }
    }
}
