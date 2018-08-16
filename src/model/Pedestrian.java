package model;

import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.*;

/**
 * Represents a pedestrian.
 * The pedestrians behaviour is defined by the functions within this class.
 */
public class Pedestrian implements Serializable {
    private double x;
    private double y;
    private double forcesOnXAxis;
    private double forcesOnYAxis;
    private final double defaultMovementX;
    private final double defaultMovementY;
    private boolean hasReachedGoal;
    private transient Perception perception;
    private static int numberOfPedestrians = 0;
    public final int ID;
    public final Direction DIRECTION;


    public Pedestrian(){
        this.ID = Pedestrian.numberOfPedestrians;
        this.hasReachedGoal = false;
        DIRECTION = Direction.randomDirection();
        defaultMovementX = setDefaultMovement();
        defaultMovementY = 0;
        Pedestrian.numberOfPedestrians++;
    }

    public Pedestrian(double x, double y){
        this();
        this.x = x;
        this.y = y;
    }

    public Pedestrian(Corridor corridor){
        this();
        Random random = new Random();
        if (DIRECTION == Direction.RIGHT)
            x = 2;
        else
            x = corridor.getWidth() - 2;

        this.y = random.nextInt((int) corridor.getHeight());
    }

    private double setDefaultMovement(){
        switch(DIRECTION){
            case LEFT:
                return -3;
            case RIGHT:
                return 3;
            default:
                return 0;
        }
    }

    private void readObject(ObjectInputStream in) throws Exception{
        in.defaultReadObject();
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
        if (!hasReachedGoal) {
            this.perception = new Perception(corridor);
            calculateSocialForce();
            this.x += defaultMovementX + forcesOnXAxis;
            this.y += defaultMovementY + forcesOnYAxis;
            hasReachedGoal = goalCheck(corridor);
        }
    }

    private boolean goalCheck(Corridor corridor){
        switch (DIRECTION){
            case LEFT:
                if(this.x <= 10){
                    return true;
                }
                else
                    return false;
            case RIGHT:
                if (this.x >= corridor.getWidth()-10)
                    return true;
                else
                    return false;
        }
        return false;
    }

    private void calculateSocialForce() {
        forcesOnXAxis = 0;
        forcesOnYAxis = 0;
        for(Pedestrian otherPedestrian : perception.pedestrians){
            forceFromOtherPedestrian(otherPedestrian);
        }
        double distanceToUpperWall = Math.abs(perception.UPPER_WALL - this.y);
        double distanceToLowerWall = Math.abs(perception.LOWER_WALL - this.y);
        forcesFromWalls(distanceToUpperWall,distanceToLowerWall);
    }

    private void forceFromOtherPedestrian(Pedestrian otherPedestrian){
        if (!this.equals(otherPedestrian)) {
            double distanceX = Math.abs(otherPedestrian.getX() - this.x);
            double distanceY = Math.abs(otherPedestrian.getY() - this.y);
            final double forceOnXFromOppositeDirection = 2;
            final double forceOnXFromSameDirection = 1.1;
            switch (this.DIRECTION){
                case RIGHT:
                    if(this.x < otherPedestrian.getX()){
                        switch (otherPedestrian.DIRECTION){
                            case RIGHT:
                                if(distanceX < 10 && distanceY < 10){
                                    if(this.y <= otherPedestrian.getY())
                                        forcesOnYAxis -= distanceY/2;
                                    else if(this.y >= otherPedestrian.getY())
                                        forcesOnYAxis += distanceY/2;
                                    if(otherPedestrian.getX() > this.x)
                                        forcesOnXAxis -= forceOnXFromSameDirection;
                                }
                                break;
                            case LEFT:
                                if(distanceX < 10 && distanceY < 10){
                                    if(this.y <= otherPedestrian.getY())
                                        forcesOnYAxis -= distanceY/2;
                                    else if(this.y >= otherPedestrian.getY())
                                        forcesOnYAxis += distanceY/2;
                                    if(otherPedestrian.getX() > this.x)
                                        forcesOnXAxis -= forceOnXFromOppositeDirection;
                                }
                                break;
                        }
                    }
                    break;
                case LEFT:
                    if(this.x > otherPedestrian.getX()){
                        switch (otherPedestrian.DIRECTION){
                            case RIGHT:
                                if(distanceX < 10 && distanceY < 10){
                                    if(this.y <= otherPedestrian.getY())
                                        forcesOnYAxis -= distanceY/2;
                                    else if(this.y >= otherPedestrian.getY())
                                        forcesOnYAxis += distanceY/2;
                                    if(otherPedestrian.getX() < this.x)
                                        forcesOnXAxis += forceOnXFromOppositeDirection;
                                }
                                break;
                            case LEFT:
                                if(distanceX < 10 && distanceY < 10) {
                                    if (this.y <= otherPedestrian.getY())
                                        forcesOnYAxis -= distanceY/2;
                                    else if (this.y >= otherPedestrian.getY())
                                        forcesOnYAxis += distanceY/2;
                                    if(otherPedestrian.getX() < this.x)
                                        forcesOnXAxis += forceOnXFromSameDirection;
                                }
                                break;
                        }
                    }
                    break;
            }
        }
    }

    private void forcesFromWalls(double distanceToUpperWall, double distanceToLowerWall){
        if(distanceToUpperWall < 10){
            forcesOnYAxis += distanceToUpperWall/4 + 0.5;
        }
        else if (distanceToLowerWall < 10){
            forcesOnYAxis -= distanceToLowerWall/4 + 0.5;
        }
        if(distanceToUpperWall < distanceToLowerWall){
            if((this.y + forcesOnYAxis) < this.perception.UPPER_WALL){
                forcesOnYAxis = -(distanceToUpperWall) + 1;
            }
        }
        else if(distanceToUpperWall > distanceToLowerWall){
            if ((this.y + forcesOnYAxis) > this.perception.LOWER_WALL){
                forcesOnYAxis = (distanceToLowerWall) - 1;
            }
        }
    }

    public double getX(){
        return x;
    }

    public double getY(){
        return y;
    }

    public boolean hasReachedGoal() {
        return hasReachedGoal;
    }

    /**
     * Perception is a container class, holding objects which are within reach of effecting a pedestrian.
     */
    private class Perception {
        private final double perceptionRadius = 20;
        private ArrayList<Pedestrian> pedestrians;
        private final double UPPER_WALL;
        private final double LOWER_WALL;

        public Perception(Corridor corridor){
            pedestrians = new ArrayList<Pedestrian>();
            UPPER_WALL = 0;
            LOWER_WALL = corridor.getHeight();
            initPerception(corridor);
        }

        private void initPerception(Corridor corridor){
            for (Pedestrian p:corridor.pedestrianList()) {
                if(Math.sqrt(Math.pow(x - p.getX(),2)+Math.pow(y - p.getY(),2)) <= perceptionRadius){
                    pedestrians.add(p);
                }
            }
        }
    }

    public enum Direction {
        LEFT, RIGHT;

        private static final List<Direction> VALUES = Collections.unmodifiableList(Arrays.asList(Direction.values()));
        private static final int SIZE = VALUES.size();
        private static final Random RANDOM = new Random();

        public static Direction randomDirection(){
            return VALUES.get(RANDOM.nextInt(SIZE));
        }
    }
}
