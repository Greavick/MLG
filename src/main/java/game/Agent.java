package game;

import engine.GameContainer;
import engine.Renderer;
import util.LearnerType;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Arrays;

import static util.FileUtil.saveModel;



public class Agent extends GameObject {

    static final double PI2 = Math.PI * 2;

    private int tileX, tileY;
    private float offX, offY;

    // -------- Agent Learning Variables -------- //

    private int currentState;
    private int newState;
    private int action;
    private double reward = 0;
    private int stateCount = 10;
    private int sc2 = stateCount * stateCount;
    private int sc3 = stateCount * stateCount;
    private int sc4 = stateCount * stateCount;

    // -------- Agent Movement Variables -------- //

    private float angle;  // angle in radians
    private double turnSpeedDiv = Math.PI / 20; // defines how fast player can trun (the higher the lower turn angle)
    private float speed = 60; // speed of the player;
    private float speedX; //speed on X axis
    private float speedY; //speed on Y axis
    private ArrayList<Point> sensorMarkers; // sensor positions on wall
    private ArrayList<Integer> sensor = new ArrayList<>();
    private ArrayList<Double> sensorAngles = new ArrayList<>();




    public Agent(int posX, int posY, GameManager gm) {
        this.tag = "agent";
        this.tileX = posX;
        this.tileY = posY;
        this.offX = 0;
        this.offY = 0;
        this.posX = posX * GameManager.TILE_SIZE;
        this.posY = posY * GameManager.TILE_SIZE;
        this.angle = 0; // - (float)Math.PI/10;
        this.speedX = (float) (speed * Math.cos(angle));
        this.speedY = (float) (speed * Math.sin(angle));
        sensor.addAll(Arrays.asList(0,0,0,0,0));
        sensorAngles.addAll(Arrays.asList(0.0,0.0,0.0,0.0,0.0));
        sensorMarkers = new ArrayList<Point>(){{
            add(new Point(0,0));
            add(new Point(0,0));
            add(new Point(0,0));
            add(new Point(0,0));
            add(new Point(0,0));
        }};
        this.width = 1;
        this.height = 1;
        this.moves = new ArrayList<>();
        init(gm);

    }

    private void init(GameManager gm){
        int distanceToWall;
        boolean wallNotFound = true;
        float nextPosX = (int) posX;
        float nextPosY = (int) posY;


        sensorAngles.set(0,angle - Math.PI/2);
        sensorAngles.set(1,angle - Math.PI/4);
        sensorAngles.set(2,(double)angle);
        sensorAngles.set(3,angle + Math.PI/4);
        sensorAngles.set(4,angle + Math.PI/2);

        for(int i = 0; i < sensor.size(); i++){
            while(wallNotFound){
                nextPosX = nextPosX + (1.0f * (float)Math.cos(sensorAngles.get(i)));
                nextPosY = nextPosY + (1.0f * (float)Math.sin(sensorAngles.get(i)));
                if(gm.getCollision(Math.round(nextPosX),Math.round(nextPosY))){
                    distanceToWall = (int) Math.sqrt(Math.pow(nextPosX - posX,2)+Math.pow(nextPosY - posY,2));
                    sensorMarkers.get(i).setLocation(nextPosX,nextPosY);
                    sensor.set(i,distanceToWall);
                    nextPosX = Math.round(posX);
                    nextPosY = Math.round(posY);
                    wallNotFound = false;
                }
            }
            wallNotFound = true;
        }

        newState = (castToState(sensor.get(0)) * sc4)
                + (castToState(sensor.get(1)) * sc3)
                + (castToState(sensor.get(2)) * sc2)
                + (castToState(sensor.get(3)) * stateCount)
                + castToState(sensor.get(4));

    }


    @Override
    public void update(GameContainer gc, GameManager gm, float dt) {

        // ----- 1. Select Action ----- //

        if(gm.modelLoaded){
            if(gm.learnerType.equals(LearnerType.SARSA)) action = gm.sarsaLearner.getModel().actionWithMaxQAtState(currentState,null).getIndex();
            else action =  gm.qLearner.getModel().actionWithMaxQAtState(currentState, null).getIndex();
        }else{
            if(gm.learnerType.equals(LearnerType.SARSA)) action = gm.sarsaLearner.selectAction(currentState).getIndex();
            else action = gm.qLearner.selectAction(currentState).getIndex();
        }

        // ----- 2. Update World ----- //

        if(gc.getInput().isKey(KeyEvent.VK_O)) gc.setSpeedUp(true);
        if(gc.getInput().isKey(KeyEvent.VK_I)) gc.setSpeedUp(true, 500);
        if(gc.getInput().isKey(KeyEvent.VK_P)) gc.setSpeedUp(false);
        if(gc.getInput().isKey(KeyEvent.VK_S)) {
            if(gm.learnerType.equals(LearnerType.SARSA)) saveModel(gm.sarsaLearner.toJson(),this.moves.size());
            else saveModel(gm.qLearner.toJson(),this.moves.size());
        }

        switch(action){
            case 1:
                angle -= turnSpeedDiv * 0.7777;
                break;
            case 2:
                angle += turnSpeedDiv * 0.7777;
                break;
            case 3:
                angle -= turnSpeedDiv * 0.3333;
                break;
            case 4:
                angle += turnSpeedDiv * 0.3333;
                break;
            case 5:
                angle -= turnSpeedDiv * 0.1666;
                break;
            case 6:
                angle += turnSpeedDiv * 0.1666;
                break;
            default:
                break;
        }

        if(angle > (PI2)) angle -= (PI2);
        else if(angle < -(PI2)) angle += (PI2);

        speedX = (float) (speed * Math.cos(angle));
        speedY = (float) (speed * Math.sin(angle));

        offX += dt * speedX;
        offY += dt * speedY;

        float tempAngle = angle;
        if (tempAngle < (PI2)) tempAngle += (Math.PI *2);

        if(tempAngle > Math.PI){
            if(tempAngle > Math.PI * 3/2){
                if(gm.getCollision(tileX + width, tileY)|| gm.getCollision(tileX + width, tileY + height)
                        || gm.getCollision(tileX, tileY + height) || gm.getCollision(tileX + width, tileY + height)){

                    setDead(true);
                    time /= 60;
                }
            }else{
                if(gm.getCollision(tileX - 1, tileY)|| gm.getCollision(tileX - 1, tileY + height)
                        || gm.getCollision(tileX, tileY + height) || gm.getCollision(tileX + width, tileY + height)){

                    setDead(true);
                    time /= 60;
                }
            }

        }else{
            if(tempAngle > Math.PI /2){
                if(gm.getCollision(tileX - 1, tileY)|| gm.getCollision(tileX - 1, tileY - height)
                        || gm.getCollision(tileX, tileY - 1) || gm.getCollision(tileX + width, tileY - 1)){

                    setDead(true);
                    time /= 60;
                }
            }else{
                if(gm.getCollision(tileX + 1, tileY)|| gm.getCollision(tileX + 1, tileY - height)
                || gm.getCollision(tileX, tileY - 1) || gm.getCollision(tileX + width, tileY - 1)){

                    setDead(true);
                    time /= 60;
                }
            }
        }

        while(offY > GameManager.TILE_SIZE / 2){
            tileY++;
            offY -= GameManager.TILE_SIZE;
        }

        while(offY < - GameManager.TILE_SIZE / 2){
            tileY--;
            offY += GameManager.TILE_SIZE;
        }
        while(offX > GameManager.TILE_SIZE / 2){
            tileX++;
            offX -= GameManager.TILE_SIZE;
        }

        while(offX < - GameManager.TILE_SIZE / 2){
            tileX--;
            offX += GameManager.TILE_SIZE;
        }

        posX = tileX * GameManager.TILE_SIZE + offX;
        posY = tileY * GameManager.TILE_SIZE + offY;



        // ----- 3. Calculate Reward and New State ----- //

        // Sensor update

        int distanceToWall;
        boolean wallNotFound = true;
        float nextPosX = (int) posX;
        float nextPosY = (int) posY;


        sensorAngles.set(0,angle - Math.PI/2);
        sensorAngles.set(1,angle - Math.PI/4);
        sensorAngles.set(2,(double) angle);
        sensorAngles.set(3,angle + Math.PI/4);
        sensorAngles.set(4,angle + Math.PI/2);

        for(int i = 0; i < sensor.size(); i++){
            while(wallNotFound){
                nextPosX = nextPosX + (float)Math.cos(sensorAngles.get(i));
                nextPosY = nextPosY + (float)Math.sin(sensorAngles.get(i));
                if(gm.getCollision(Math.round(nextPosX),Math.round(nextPosY))){
                    distanceToWall = (int) (Math.sqrt(Math.pow(nextPosX - posX,2) + Math.pow(nextPosY - posY,2)));
                    sensorMarkers.get(i).setLocation(nextPosX,nextPosY);
                    sensor.set(i,distanceToWall);
                    nextPosX = (int)(posX);
                    nextPosY = (int)(posY);
                    wallNotFound = false;
                }
            }
            wallNotFound = true;
        }

        newState = (castToState(sensor.get(0)) * sc4)
                + (castToState(sensor.get(1)) * sc3)
                + (castToState(sensor.get(2)) * sc2)
                + (castToState(sensor.get(3)) * stateCount)
                + castToState(sensor.get(4));

        // Agent reward


        if(!isDead()){
            reward = 0.1;
            int temp = 0;
            for(double d : sensor){
                temp += d;
            }
//            temp += sensor.get(2);
//            temp += sensor.get(3) + sensor.get(3) + sensor.get(3);
//            temp += sensor.get(4);
            reward = (reward * temp / 100 );
        }else{
            reward = -1000;
        }

        // ----- 4. Add Move To Learner ----- //

        this.moves.add(new Move(currentState, action, newState, reward)); // Add the experience to the history

        currentState = newState;
        time ++;
    }

    private int castToState(int dist){
            if(dist > 75)
                return 9;
            if(dist > 50)
                return 8;
            if(dist > 30)
                return 7;
            if(dist > 20)
                return 6;
            if(dist > 14)
                return 5;
            if(dist > 10)
                return 4;
            if(dist > 7)
                return 3;
            if(dist > 4)
                return 2;
            if(dist > 2)
                return 1;
            else
                return 0;
    }

    public ArrayList<Move> getMoves() {
        return moves;
    }

    @Override
    public void render(GameContainer gc, Renderer r) {
        if(!isDead()){
            r.drawFillRect((int) posX, (int) posY, width, height, 0xff00ff00);

            // Sensor wall detection drawing
            r.drawFillRect(sensorMarkers.get(0).x, sensorMarkers.get(0).y, 1, 1, 0xffff0000);
            r.drawFillRect(sensorMarkers.get(1).x, sensorMarkers.get(1).y, 1, 1, 0xffff0000);
            r.drawFillRect(sensorMarkers.get(2).x, sensorMarkers.get(2).y, 1, 1, 0xffff0000);
            r.drawFillRect(sensorMarkers.get(3).x, sensorMarkers.get(3).y, 1, 1, 0xffff0000);
            r.drawFillRect(sensorMarkers.get(4).x, sensorMarkers.get(4).y, 1, 1, 0xffff0000);
        }
        else {
            r.drawFillRect((int) posX, (int) posY, width, height, 0xffff2f00);
        }

    }
}
