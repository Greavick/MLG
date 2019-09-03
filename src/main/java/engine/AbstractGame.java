package engine;


public abstract class AbstractGame {
    public int iteration = 1 ;
    public abstract void update(GameContainer gc, float dt);
    public abstract void render(GameContainer gc, Renderer r);
    public abstract void init(GameContainer gc);
}
