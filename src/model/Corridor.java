package model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.shape.Circle;
import server.Pedestrian;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Corridor implements Serializable {
    public ArrayList<Pedestrian> pedestrianList;
    private final double width;
    private final double height;
    private final List<CorridorListener> listeners = new ArrayList<>();
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

    public double getWidth(){
        return width;
    }

    public double getHeight(){
        return height;
    }

    public ArrayList<Pedestrian> getPedestrianList() {
        return pedestrianList;
    }
}
