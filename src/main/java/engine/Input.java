package engine;

import java.awt.event.*;

public class Input implements KeyListener {

    private GameContainer gc;

    private final int NUMBER_OF_KEYS = 256;

    private boolean[] currentKeys = new boolean[NUMBER_OF_KEYS];
    private boolean[] previousKeys = new boolean[NUMBER_OF_KEYS];


    public Input(GameContainer gc){
        this.gc = gc;
        gc.getWindow().getCanvas().addKeyListener(this);
    }

    public void update(){
        for(int i = 0; i < NUMBER_OF_KEYS; i++){
            previousKeys[i] = currentKeys[i];
        }
    }

    public boolean isKey(int keyCode) {

        return currentKeys[keyCode];
    }

    public boolean isKeyUp(int keyCode) {

        return !currentKeys[keyCode] && previousKeys[keyCode];
    }

    public boolean isKeyDown(int keyCode) {

        return currentKeys[keyCode] && !previousKeys[keyCode];
    }

    public void keyTyped(KeyEvent e) {

    }

    public void keyPressed(KeyEvent e) {

        currentKeys[e.getKeyCode()] = true;

    }

    public void keyReleased(KeyEvent e) {

        currentKeys[e.getKeyCode()] = false;
    }

}
