package dataPacket;

import model.Corridor;
import model.Pedestrian;

import java.io.Serializable;
import java.util.ArrayList;

public class DataPacket implements Serializable {
    public Corridor corridor;
    public int connectionNumber;
    public int numberOfActiveConnections;
    public boolean isTerminated;

    public DataPacket(Corridor corridor, int connectionNumber, int numberOfActiveConnections, boolean isTerminated){
        this.corridor = corridor;
        this.connectionNumber = connectionNumber;
        this.numberOfActiveConnections = numberOfActiveConnections;
        this.isTerminated = isTerminated;
    }
}
