package game;

import engine.GameContainer;
import engine.Renderer;

import java.util.ArrayList;

public abstract class GameObject {

    protected ArrayList<Move> moves;
    protected int time = 0;
    protected int state = 0;
    protected String tag;
    protected float posX, posY;
    protected int width, height;
    protected boolean dead = false;

    public abstract void update(GameContainer gc, GameManager gm, float dt);
    public abstract void render(GameContainer gc, Renderer r);

    public ArrayList<Move> getMoves() {
        return moves;
    }

    public int getTime() {
        return time;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public float getPosX() {
        return posX;
    }

    public void setPosX(float posX) {
        this.posX = posX;
    }

    public float getPosY() {
        return posY;
    }

    public void setPosY(float posY) {
        this.posY = posY;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public boolean isDead() {
        return dead;
    }

    public void setDead(boolean dead) {
        this.dead = dead;
    }
}
