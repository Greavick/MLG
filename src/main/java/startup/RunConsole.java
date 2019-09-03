package startup;

import engine.GameContainer;
import engine.gfx.Image;
import game.GameManager;
import util.LearnerType;

public class RunConsole {


    public static void main(String args[]){

        String modelPath = "";
        String levelPath = "/map01.png";
        boolean correctInput = true;
        boolean collectData = false;
        LearnerType lt = LearnerType.QLearn;
        int x = 40,y = 220, avg = 100, itsd = 1000;
        double alpha = 0.1,gamma = 0.99;

        // ( learnerType, alpha, gamma )
        if(args.length == 3){

            if(args[0].toUpperCase().equals("SARSA")) {
                lt = LearnerType.SARSA;
            } else if(args[0].toUpperCase().equals("QLEARN")){
                lt = LearnerType.QLearn;
            }else {
                System.out.println("Incorrect learner type. Choose either \"SARSA\" or \"QLearn\"");
                correctInput = false;
            }
            try{
                alpha = Double.parseDouble(args[1]);
                gamma = Double.parseDouble(args[2]);
                if(alpha < 0.0 || alpha > 1.0 || gamma < 0.0 || gamma > 1.0){
                    System.out.println("Alpha or Gamma are out of bounds <0.0,1.0>");
                    correctInput = false;
                }
            }catch (Exception e){
                System.out.println("Incorrect arguments, try: learnerType, alpha, gamma. Choose either \"SARSA\" or \"QLearn\"");
                correctInput = false;
            }

        // ( levelpath, x, y, learnerType, alpha, gamma )
        }else if(args.length == 6){
            levelPath = args[0];
            try{
                x = Integer.parseInt(args[1]);
                y = Integer.parseInt(args[2]);
            }catch (Exception e){
                System.out.println("Incorrect arguments, try: levelPath, agentX, agentY, learnerType, alpha, gamma. Choose either \"SARSA\" or \"QLearn\"");
                correctInput = false;
            }
            if(args[3].toUpperCase().equals("SARSA")) {
                lt = LearnerType.SARSA;
            } else if(args[3].toUpperCase().equals("QLEARN")){
                lt = LearnerType.QLearn;
            }else {
                System.out.println("Incorrect learner type. Choose either \"SARSA\" or \"QLearn\"");
                correctInput = false;
            }
            try{
                alpha = Double.parseDouble(args[4]);
                gamma = Double.parseDouble(args[5]);
                if(alpha < 0.0 || alpha > 1.0 || gamma < 0.0 || gamma > 1.0){
                    System.out.println("Alpha or Gamma are out of bounds <0.0,1.0>");
                    correctInput = false;
                }
            }catch (Exception e){
                System.out.println("Incorrect arguments, try: levelPath, agentX, agentY, learnerType, alpha, gamma. Choose either \"SARSA\" or \"QLearn\"");
                correctInput = false;
            }
        // ( levelpath, x, y, learnerType, model)
        }else if(args.length == 5){
            levelPath = args[0];
            try{
                x = Integer.parseInt(args[1]);
                y = Integer.parseInt(args[2]);
            }catch (Exception e){
                System.out.println("Incorrect arguments, try: levelPath, agentX, agentY, learnerType, alpha, gamma, modelPath. Choose either \"SARSA\" or \"QLearn\"");
                correctInput = false;
            }
            if(args[3].toUpperCase().equals("SARSA")) {
                lt = LearnerType.SARSA;
            } else if(args[3].toUpperCase().equals("QLEARN")){
                lt = LearnerType.QLearn;
            }else {
                System.out.println("Incorrect learner type. Choose either \"SARSA\" or \"QLearn\"");
                correctInput = false;
            }
            modelPath = args[4];
        // ( levelpath, x, y, learnerType, alpha, gamma, collectData, avg, itsd)
        }else if(args.length == 9) {
            levelPath = args[0];
            try {
                x = Integer.parseInt(args[1]);
                y = Integer.parseInt(args[2]);
            } catch (Exception e) {
                System.out.println("Incorrect arguments, try: levelPath, agentX, agentY, learnerType, alpha, gamma, modelPath. Choose either \"SARSA\" or \"QLearn\"");
                correctInput = false;
            }
            if (args[3].toUpperCase().equals("SARSA")) {
                lt = LearnerType.SARSA;
            } else if (args[3].toUpperCase().equals("QLEARN")) {
                lt = LearnerType.QLearn;
            } else {
                System.out.println("Incorrect learner type. Choose either \"SARSA\" or \"QLearn\"");
                correctInput = false;
            }
            try {
                alpha = Double.parseDouble(args[4]);
                gamma = Double.parseDouble(args[5]);
                if (alpha < 0.0 || alpha > 1.0 || gamma < 0.0 || gamma > 1.0) {
                    System.out.println("Alpha or Gamma are out of bounds <0.0,1.0>");
                    correctInput = false;
                }
            } catch (Exception e) {
                System.out.println("Incorrect arguments, try: levelPath, agentX, agentY, learnerType, alpha, gamma, modelPath. Choose either \"SARSA\" or \"QLearn\"");
                correctInput = false;
            }
            try {
                collectData = Boolean.parseBoolean(args[6]);
                avg = Integer.parseInt(args[7]);
                itsd = Integer.parseInt(args[8]);
                if (avg < 1 || itsd < 1) {
                    System.out.println("avg and itsd params have to be greater than 0");
                    correctInput = false;
                }
            } catch (Exception e) {
                System.out.println("Incorrect arguments, try: levelPath, agentX, agentY, learnerType, alpha, gamma, modelPath. Choose either \"SARSA\" or \"QLearn\"");
                correctInput = false;
            }
        }else{
            System.out.println("Wrong input parameters");
            correctInput = false;
        }

        GameManager gm;
        if (modelPath.equals("")){
            gm = new GameManager(levelPath, x, y, lt ,alpha,gamma,0.1);
        }else{
            gm = new GameManager(levelPath, x, y, lt , modelPath);
        }
        Image map = new Image(levelPath);
        gm.setAvgOf(avg);
        gm.setNrOfIterToSaveData(itsd);

        GameContainer gc = new GameContainer(gm);
        gc.setWindowParams(map.getWidth(),map.getHeight(),3f);
        gc.setCollectData(collectData);
        if(correctInput) {
            switch(lt){
                case QLearn:
                    System.out.println("QLearning : Alpha: " + alpha + ", Gamma: " + gamma );
                    break;
                case SARSA:
                    System.out.println("SARSA : Alpha: " + alpha + ", Gamma: " + gamma );
                    break;
            }
            gc.start();
        }
    }

}
