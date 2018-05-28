package server;

import java.io.Serializable;

public class Pedestrian implements Serializable {
    public final int id;
    private int position = 0;
    private double x = 0;
    private double y = 0;

    public Perception perception;

    public static int numberOfPedestrians = 0;

    public Pedestrian(){
        this.id = Pedestrian.numberOfPedestrians;
        this.position = 0;
        this.perception = new Perception();
        Pedestrian.numberOfPedestrians++;
    }

    public Pedestrian(int position) {
        this.id = Pedestrian.numberOfPedestrians;
        this.position = position;
        Pedestrian.numberOfPedestrians++;
    }

    public Pedestrian(double x, double y){
        this.id = Pedestrian.numberOfPedestrians;
        this.x = x;
        this.y = y;
        this.perception = new Perception();
        Pedestrian.numberOfPedestrians++;
    }

    public int getPosition(){
        return position;
    }

    public void modifyObject(){
        position++;
    }

    public class Perception implements Serializable {
        public double centerX;
        public double centerY;

        public Perception(){
            this.centerX = x;
            this.centerY = y;
        }
    }
}
