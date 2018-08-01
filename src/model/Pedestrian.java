package model;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;


public class Pedestrian implements Serializable {
    private double x;
    private double y;
    private double forceX;
    private double forceY;
    private final double defaultMovementX;
    private final double defaultMovementY;
    private boolean hasReachedGoal;
    private transient Perception perception;
    public final int ID;
    public final Direction DIRECTION;
    public static int numberOfPedestrians = 0;
    public transient Circle circle;

    public Pedestrian(){
        this.ID = Pedestrian.numberOfPedestrians;
        this.hasReachedGoal = false;
        DIRECTION = Direction.randomDirection();
        defaultMovementX = setDefaultMovement();
        defaultMovementY = 0;
        Pedestrian.numberOfPedestrians++;
        setupCircle();
    }

    public Pedestrian(double x, double y){
        this();
        this.x = x;
        this.y = y;
        setupCircle();
    }

    public Pedestrian(Corridor corridor){
        this();
        Random random = new Random();
        if (DIRECTION == Direction.RIGHT)
            x = 2;
        else
            x = corridor.getWidth() - 2;

        this.y = random.nextInt((int) corridor.getHeight());
        setupCircle();
    }

    private void setupCircle(){
        double circleRadius = 2.5;
        if (!hasReachedGoal) {
            switch(DIRECTION){
                case LEFT:
                    this.circle = new Circle(this.x, this.y, circleRadius, Color.RED);
                    break;
                case RIGHT:
                    this.circle = new Circle(this.x, this.y, circleRadius, Color.BLUE);
                    break;
            }
        }
        else {
            this.circle = new Circle (this.x, this.y, circleRadius, Color.GREEN);
            //this.circle = null;
        }
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
        setupCircle();
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
            this.x += defaultMovementX + forceX;
            this.y += defaultMovementY + forceY;
            hasReachedGoal = goalCheck(corridor);
        }
        setupCircle();
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
        forceX = 0;
        forceY = 0;

        for(Pedestrian otherPedestrian : perception.pedestrians){
            forceFromOtherPedestrian(otherPedestrian);
        }

        double distanceToUpperWall = Math.abs(perception.UPPER_WALL - this.y);
        System.out.println("Distance to upper wall: " + distanceToUpperWall);
        double distanceToLowerWall = Math.abs(perception.LOWER_WALL - this.y);
        System.out.println("Distance to lower wall: " + distanceToLowerWall);
        forcesFromWalls(distanceToUpperWall,distanceToLowerWall);
    }

    private void forceFromOtherPedestrian(Pedestrian otherPedestrian){
        if (!this.equals(otherPedestrian)) {
            double distanceX = Math.abs(otherPedestrian.getX() - this.x);
            double distanceY = Math.abs(otherPedestrian.getY() - this.y);

            switch (this.DIRECTION){
                case RIGHT:
                    if(this.x < otherPedestrian.getX()){
                        switch (otherPedestrian.DIRECTION){
                            //Figurer till höger om this som går åt höger
                            case RIGHT:
                                if(distanceX < 10 && distanceY < 10){
                                    if(this.y <= otherPedestrian.getY())
                                        forceY -= distanceY/2;
                                    else if(this.y >= otherPedestrian.getY())
                                        forceY += distanceY/2;
                                    forceX -= 0.5;
                                }
                                break;
                            //figurer till höger som går åt vänster
                            case LEFT:
                                if(distanceX < 10 && distanceY < 10){
                                    if(this.y <= otherPedestrian.getY())
                                        forceY -= distanceY/2;
                                    else if(this.y >= otherPedestrian.getY())
                                        forceY += distanceY/2;
                                    forceX -= 2;
                                }
                                break;
                        }
                    }
                    break;
                case LEFT:
                    if(this.x > otherPedestrian.getX()){
                        switch (otherPedestrian.DIRECTION){
                            //figurer till vänster som går åt höger
                            case RIGHT:
                                if(distanceX < 10 && distanceY < 10){
                                    if(this.y <= otherPedestrian.getY())
                                        forceY -= distanceY/2;
                                    else if(this.y >= otherPedestrian.getY())
                                        forceY += distanceY/2;
                                    forceX += 2;
                                }
                                break;
                            //figurer till vänter som går åt vänster
                            case LEFT:
                                if(distanceX < 10 && distanceY < 10) {
                                    if (this.y <= otherPedestrian.getY())
                                        forceY -= distanceY/2;
                                    else if (this.y >= otherPedestrian.getY())
                                        forceY += distanceY/2;
                                    forceX += 0.5;
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
            forceY += distanceToUpperWall/2 + 0.5;
        }
        else if (distanceToLowerWall < 10){
            forceY -= distanceToLowerWall/2 + 0.5;
        }
        if(distanceToUpperWall < distanceToLowerWall){
            if((this.y + forceY) < this.perception.UPPER_WALL){
                forceY = -(distanceToUpperWall) + 1;
            }
        }
        else if(distanceToUpperWall > distanceToLowerWall){
            if ((this.y + forceY) > this.perception.LOWER_WALL){
                forceY = (distanceToLowerWall) - 1;
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

    public class Perception {
        public final double perceptionRadius = 20;
        public ArrayList<Pedestrian> pedestrians;
        public final double UPPER_WALL;
        public final double LOWER_WALL;

        public Perception(Corridor corridor){
            pedestrians = new ArrayList<Pedestrian>();
            UPPER_WALL = 0;
            LOWER_WALL = corridor.getHeight();
            initPerception(corridor);
        }

        public void initPerception(Corridor corridor){
            for (Pedestrian p:corridor.getPedestrianList()) {
                if(Math.sqrt(Math.pow(x - p.getX(),2)+Math.pow(y - p.getY(),2)) <= perceptionRadius){
                    pedestrians.add(p);
                }
            }
        }
    }
}
