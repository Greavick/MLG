package startup;

import engine.GameContainer;
import engine.gfx.Image;
import game.GameManager;
import util.LearnerType;

public class Run {


    public static void main(String args[]){

        //String modelPath = "C:\\temp\\QLearningModel.txt";
        String modelPath = "C:\\temp\\loopSARSA.txt";
        String levelPath = "/map02.png";
        Image map = new Image(levelPath);

        GameManager gm = new GameManager(levelPath, 40, 230, LearnerType.SARSA,0.1,1.0,0.1);
        gm.setAvgOf(50);

        GameContainer gc = new GameContainer(gm);
        gc.setWindowParams(map.getWidth(),map.getHeight(),3f);
        gc.setCollectData(false);
        gc.start();
    }
}
