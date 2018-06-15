package server;

import java.io.Serializable;
import java.util.ArrayList;

public class Pedestrian implements Serializable {
    public final int ID;
    private int position = 0;
    private double x;
    private double y;
    private transient Perception perception;
    public static int numberOfPedestrians = 0;

    public Pedestrian(double x, double y){
        this.ID = Pedestrian.numberOfPedestrians;
        this.x = x;
        this.y = y;
        Pedestrian.numberOfPedestrians++;
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
        for (Pedestrian pedestrian : perception.pedestrians){
            if(!this.equals(pedestrian)){
                calculateSocialForce(pedestrian);
            }
        }
    }

    private void calculateSocialForce(Pedestrian pedestrian) {

    }

    public int getPosition(){
        return position;
    }

    public double getX(){
        return x;
    }

    public double getY(){
        return y;
    }

    public void modifyObject(){
        position++;
    }

    public class Perception {
        public final double perceptionRadius = 20;
        public ArrayList<Pedestrian> pedestrians;

        public Perception(Corridor corridor){
            pedestrians = new ArrayList<Pedestrian>();
            initPerception(corridor);
        }

        public void initPerception(Corridor corridor){
            for (Pedestrian p:corridor.getPedestrianList()) {
                if(Math.sqrt(Math.pow(p.x,2)+Math.pow(p.y,2)) < perceptionRadius){
                    pedestrians.add(p);
                }
            }
        }
    }
}
