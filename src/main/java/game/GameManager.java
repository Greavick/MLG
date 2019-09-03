package game;

import com.github.chen0040.rl.learning.actorcritic.ActorCriticLearner;
import com.github.chen0040.rl.learning.qlearn.QLearner;
import com.github.chen0040.rl.learning.sarsa.SarsaLearner;

import engine.AbstractGame;
import engine.GameContainer;
import engine.Renderer;
import engine.gfx.Image;
import util.LearnerType;

import java.util.ArrayList;

import static util.FileUtil.*;

public class GameManager extends AbstractGame {

    // Pixel Spec. of the Game
    static final int TILE_SIZE = 1;
    private int levelW,levelH;
    private boolean[] collision;
    private int[] special;

    // Agent Spec.
    private int stateCount = 100000;
    private int actionCount = 7;
    private int agentX;
    private int agentY;

    // Game Objects (Agents)
    private ArrayList<GameObject> objects = new ArrayList<>();

    // Data Collection properties
    private String gameData = "";
    private int avgOf = 50;
    private double avg = 0;
    private int nrOfIterToSaveData = 1000;

    LearnerType learnerType;

    SarsaLearner sarsaLearner;
    QLearner qLearner;


    boolean modelLoaded = false;

    public GameManager(String levelPath, int agentX, int agentY, LearnerType learnerType, double alpha, double gamma, double initialQ) {
        this.learnerType = learnerType;
        this.agentX = agentX;
        this.agentY = agentY;
        loadLevel(levelPath);
        objects.add(new Agent(agentX,agentY, this));
        switch(learnerType){
            case QLearn:
                qLearner = new QLearner(stateCount,actionCount, alpha, gamma, initialQ);
//                System.out.println("QLearning : Alpha: " + alpha + ", Gamma: " + gamma );
                break;
            case SARSA:
                sarsaLearner = new SarsaLearner(stateCount,actionCount, alpha, gamma, initialQ);
//                System.out.println("SARSA : Alpha: " + alpha + ", Gamma: " + gamma );
                break;
            default:
                System.out.print("Not supported learner. Default one will be used instead.");
                qLearner = new QLearner(stateCount,actionCount, alpha, gamma, initialQ);
//                System.out.println("QLearning : Alpha: " + alpha + ", Gamma: " + gamma );
        }
    }

    public GameManager(String levelPath, int agentX, int agentY, LearnerType learnerType){
        this(levelPath, agentX, agentY, learnerType, 0.1, 0.91, 0.1);
    }

    public GameManager(String levelPath, int agentX, int agentY, LearnerType learnerType, double alpha, double gamma){
        this(levelPath, agentX, agentY, learnerType, alpha, gamma, 0.1);
    }

    public GameManager(String levelPath, int agentX, int agentY, LearnerType learnerType, String modelPath){
        this(levelPath, agentX, agentY, learnerType);
        String jsonModel = readAllBytesFromFile(modelPath);
        switch(learnerType){
            case QLearn:
                qLearner = QLearner.fromJson(jsonModel);
                modelLoaded = true;
                break;
            case SARSA:
                sarsaLearner = SarsaLearner.fromJson(jsonModel);
                modelLoaded = true;
                break;
            default:
                System.out.print("Not supported learner. Model cannot be loaded.");
                modelLoaded = false;
                break;
        }
    }


    public void update(GameContainer gc, float dt) {

        double maxVal = 0;

        for(int i = 0; i < objects.size(); i++){
            objects.get(i).update(gc,this,dt);
            if(objects.get(i).isDead()){
                if(gc.isCollectData()){
                    if(iteration % nrOfIterToSaveData == 0){
                        saveData(gameData);
                        gameData = "";
                    }
                    gameData += (objects.get(i).getTime() + " ; ");
                }
                ArrayList<Move> moves = objects.get(i).getMoves();
                avg += objects.get(i).time;
                if(iteration % avgOf == 0){
                    System.out.println( avg / avgOf );
                    avg = 0;
                }
                for(int j = moves.size()-1; j >=0; --j){
                    Move move = moves.get(j);
                    if(maxVal * 1.25 < moves.size() && moves.size() > 1000000){
                        if(learnerType.equals(LearnerType.SARSA)){
                            saveModel(sarsaLearner.toJson(),moves.size());
                        }else if(learnerType.equals(LearnerType.QLearn)){
                            saveModel(qLearner.toJson(),moves.size());
                        }
                        maxVal = moves.size();
                    }
                    if(learnerType.equals(LearnerType.SARSA)){
                        if(j == moves.size()-1) j--;
                        move = moves.get(j);
                        Move nextMove = moves.get(j+1);
                        sarsaLearner.update(move.oldState, move.action, move.newState, nextMove.action,move.reward);
                    } else if(learnerType.equals(LearnerType.QLearn)){
                        qLearner.update(move.oldState, move.action, move.newState, move.reward);
                    }
                }
                objects.remove(i);
                gc.speedIterReduce();
                i--;
            }
        }
        if(objects.size() == 0){
            objects.add(new Agent(agentX,agentY,this));
            gc.setTime(0);
            this.iteration++;
        }

    }

    public void render(GameContainer gc, Renderer r) {

        for(int y = 0; y < levelH; y++){
            for(int x = 0; x < levelW;x++) {
                if(collision[x + y * levelW]) {
                    r.drawFillRect(x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE, 0xff0f0f0f);
                }else if(special[x + y * levelW] == 1) {
                    r.drawFillRect(x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE,TILE_SIZE, 0xffffff00);
                }else if(special[x + y * levelW] == 2) {
                    r.drawFillRect(x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE,TILE_SIZE, 0xff0000ff);
                }else{
                    r.drawFillRect(x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE,TILE_SIZE, 0xffffffff);
                }
            }
        }

        for(GameObject obj : objects){
            obj.render(gc,r);
        }
    }

    @Override
    public void init(GameContainer gc) {}

    private void loadLevel(String path){
        Image levelImage = new Image(path);

        levelH = levelImage.getHeight();
        levelW = levelImage.getWidth();
        collision = new boolean[levelH * levelW];
        // enum to be implemented for special type instead of int
        special = new int[levelH * levelW];

        for(int y = 0; y < levelImage.getHeight(); y++){
            for(int x = 0; x < levelImage.getWidth();x++){

                // BLACK pixel -> collision
                if(levelImage.getPixel()[x + y * levelImage.getWidth()] == 0xff000000){
                    collision[x + y * levelImage.getWidth()] = true;
                }
                // YELLOW pixel -> spec (1)
                else if(levelImage.getPixel()[x + y * levelImage.getWidth()] == 0xffffff00){
                    special[x + y * levelImage.getWidth()] = 1;
                    collision[x + y * levelImage.getWidth()] = true;
                }
                // BLUE pixel -> spec (2)
                else if(levelImage.getPixel()[x + y * levelImage.getWidth()] == 0xff0000ff) {
                    special[x + y * levelImage.getWidth()] = 2;
                    collision[x + y * levelImage.getWidth()] = false;
                }else{
                    collision[x + y * levelImage.getWidth()] = false;
                }
            }
        }
    }

    public void addObject(GameObject object){
        objects.add(object);
    }

    public boolean getCollision(int x, int y){
        if(x < 0 || x >= levelW || y < 0 || y >=levelH){
            return true;
        }
        return collision[x + y * levelW];
    }

    public int getSpecial(int x, int y) {
        if(x < 0 || x >= levelW || y < 0 || y >=levelH){
            return 0;
        }
        return special[x + y * levelW];
    }

    public void setAvgOf(int avgOf) {
        this.avgOf = avgOf;
    }

    public void setNrOfIterToSaveData(int nrOfIterToSaveData) {
        this.nrOfIterToSaveData = nrOfIterToSaveData;
    }
}
