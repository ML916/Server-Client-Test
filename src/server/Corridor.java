package server;

import javafx.collections.ObservableList;
import javafx.scene.shape.Circle;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

public class Corridor implements Serializable {
    private ArrayList<Pedestrian> pedestrianList;
    private final double width;
    private final double height;
    private transient ObservableList<Circle> circles;

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

    public ArrayList<Pedestrian> getPedestrianList() {
        return pedestrianList;
    }
}
