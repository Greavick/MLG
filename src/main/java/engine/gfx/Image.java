package engine.gfx;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class Image {
    private int width, height;
    private int[] pixel;
    private boolean alpha = false;

    public Image(int[] pixel, int width, int height){
        this.pixel = pixel;
        this.width = width;
        this.height = height;
    }


    public Image(String path){
        BufferedImage image = null;

        try {
            image = ImageIO.read(Image.class.getResourceAsStream(path));
        } catch (IOException e) {
            e.printStackTrace();
        }

        width = image.getWidth();
        height = image.getHeight();
        pixel = image.getRGB(0,0, width, height,null,0, width);

        image.flush();
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

    public int[] getPixel() {
        return pixel;
    }

    public void setPixel(int[] pixel) {
        this.pixel = pixel;
    }

    public boolean isAlpha() {
        return alpha;
    }

    public void setAlpha(boolean alpha) {
        this.alpha = alpha;
    }
}
