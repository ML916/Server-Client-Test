package server;

import java.io.Serializable;

public class Pedestrian implements Serializable {
    public final int id;
    private int position;

    public static int numberOfPedestrians = 0;

    public Pedestrian(){
        this.id = Pedestrian.numberOfPedestrians;
        this.position = 0;
        Pedestrian.numberOfPedestrians++;
    }

    public Pedestrian(int position) {
        this.id = Pedestrian.numberOfPedestrians;
        this.position = position;
        Pedestrian.numberOfPedestrians++;
    }

    public int getPosition(){
        return position;
    }

    public void modifyObject(){
        position++;
    }
}
