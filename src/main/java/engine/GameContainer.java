package engine;

import java.util.ArrayList;

public class GameContainer implements Runnable {

    private AbstractGame game;
    private Thread thread;
    private Window window;
    private Renderer renderer;
    private Input input;



    private int time = 0;
    private int width = 320;
    private int height = 240;
    private float scale = 3f;


    private String title = "Machine Learning Game";

    private boolean speedUp = false;
    private int speedIter = 0;
    protected boolean collectData = false;


    private boolean running = false;
    private final double UPDATE_CAP = 1.0 / 60.0;

    public GameContainer(AbstractGame game){
        this.game = game;

    }

    public void start(){
        window = new Window(this);
        thread = new Thread(this);
        renderer = new Renderer(this);
        input = new Input(this);
        thread.run();
    }

    public void run(){
        running = true;


        boolean render;
        double currentTime;
        double previousTime = System.nanoTime() / 1000000000.0;
        double passedTime;
        double unprocessedTime = 0;

        double frameTime = 0;

        game.init(this);


        while(running){
            render = true;
            currentTime = System.nanoTime() / 1000000000.0;
            passedTime = currentTime - previousTime;
            previousTime = currentTime;

            unprocessedTime += passedTime;
            frameTime += passedTime;

            while(unprocessedTime >= UPDATE_CAP){
                unprocessedTime -= UPDATE_CAP;
                render = true;



                input.update();
                game.update(this, (float) UPDATE_CAP);
                while(speedUp && (speedIter > 0 || speedIter == -1)){
                    input.update();
                    game.update(this, (float) UPDATE_CAP);
                    if(!speedUp)
                        time = 0;
                }


                if(frameTime >= 1.0){
                    frameTime = 0;
                    time++;
                }
            }

            if(render){
                renderer.clear();
                game.render(this, renderer);
                renderer.process();
                renderer.drawText("Iteration: " + game.iteration + "        Time: " + time ,0,0,0xff00ffff);
                window.update();
            }else{
                try{
                    Thread.sleep(1);
                }catch(InterruptedException e ){
                    e.printStackTrace();
                }
            }

        }
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setWindowParams(int width, int height, float scale){
        this.width = width;
        this.height = height;
        this.scale = scale;
    }

    public void setSpeedUp(boolean x){
        this.speedUp = x;
        this.speedIter = -1;
    }

    public void setSpeedUp(boolean x, int iter){
        this.speedUp = x;
        if(this.speedIter < 1){
            this.speedIter = iter;
        }
    }

    public void setTime(int time) {
        this.time = time;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Window getWindow() {
        return window;
    }

    public Input getInput() {
        return input;
    }

    public int getTime() {
        return time;
    }

    public void setCollectData(boolean bool){
        this.collectData = bool;
    }

    public boolean isCollectData(){
        return collectData;
    }

    public Renderer getRenderer() {
        return renderer;
    }

    public void speedIterReduce(){
        if(this.speedIter > 0)
            this.speedIter--;
    }
}
