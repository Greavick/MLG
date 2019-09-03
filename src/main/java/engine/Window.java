package engine;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;

public class Window {

    private JFrame frame;
    private BufferedImage image;
    private Canvas canvas;
    private Graphics graphics;
    private BufferStrategy bufferStrategy;

    public Window(GameContainer gc){
        image = new BufferedImage(gc.getWidth(),gc.getHeight(),BufferedImage.TYPE_INT_RGB);
        canvas = new Canvas();
        Dimension dim = new Dimension((int)(gc.getWidth() * gc.getScale()),(int)  (gc.getHeight() * gc.getScale()));
        canvas.setPreferredSize(dim);
        canvas.setMaximumSize(dim);
        canvas.setMinimumSize(dim);

        frame = new JFrame(gc.getTitle());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.add(canvas, BorderLayout.CENTER);
        frame.pack();
        frame.setResizable(false);
        frame.setVisible(true);

        canvas.createBufferStrategy(2);
        bufferStrategy = canvas.getBufferStrategy();
        graphics = bufferStrategy.getDrawGraphics();

    }

    public void update(){

        graphics.drawImage(image,0,0,canvas.getWidth(),canvas.getHeight(),null);
        bufferStrategy.show();
    }

    public BufferedImage getImage() {
        return image;
    }

    public Canvas getCanvas() {
        return canvas;
    }

    public JFrame getFrame() {
        return frame;
    }
}
