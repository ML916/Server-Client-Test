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

    /**
     * Initiates a Pedestrian object on the given x and y coordinates
     * @param x
     * @param y
     */
    public Pedestrian(double x, double y){
        this();
        this.x = x;
        this.y = y;
    }

    /**
     * Initiates a Pedestrian object on a random side of the corridor
     * @param corridor
     */
    public Pedestrian(Corridor corridor){
        this();
        Random random = new Random();
        if (DIRECTION == Direction.RIGHT)
            x = 2;
        else
            x = corridor.getWidth() - 2;

        this.y = random.nextInt((int) corridor.getHeight());
    }

    /**
     * Sets the default movement speed depending on a given direction
     * @return The default movement speed in a given direction
     */
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

    /**
     * A Pedestrian object is equal to another Pedestrian if the ID is equal
     * @param obj Object to compare the pedestrian with
     * @return Whether the ID is equal or not
     */
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

    /**
     * Moves a pedestrian within the corridor
     * @param corridor
     */
    public void move(Corridor corridor){
        if (!hasReachedGoal) {
            this.perception = new Perception(corridor);
            calculateSocialForce();
            this.x += defaultMovementX + forcesOnXAxis;
            this.y += defaultMovementY + forcesOnYAxis;
            hasReachedGoal = goalCheck(corridor);
        }
    }

    /**
     * Checks if the Pedestrian object has reached its destination within the Corridor.
     * @param corridor The corridor the pedestrian is located in
     * @return true or false, depending on if the pedestrian has reached its goal
     */
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

    /**
     * Calculates all forces affecting the Pedestrian
     */
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

    /**
     * Calculates social forces affecting the Pedestrian from another pedestrian object.
     * socialForce will be pushed in the opposite direction of otherPedestrian, if DIRECTION is different.
     * If the otherPedestrian is headed in the same direction it will either slow down or speed up this Pedestrian, depending on if it is ahead or behind this Pedestrian.
     * @param otherPedestrian Another pedestrian affecting this pedestrian
     */
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

    /**
     * Calculates social force from the walls of the corridor so that the pedestrian does not walk into the wall.
     * @param distanceToUpperWall Distance to upper wall of the corridor
     * @param distanceToLowerWall Distance to lower wall of the corridor
     */
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
            for (Pedestrian p:corridor.getPedestrianList()) {
                if(Math.sqrt(Math.pow(x - p.getX(),2)+Math.pow(y - p.getY(),2)) <= perceptionRadius){
                    pedestrians.add(p);
                }
            }
        }
    }

    /**
     * Direction is an enum representing whether the pedestrian is heading left or right in the corridor
     */
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
