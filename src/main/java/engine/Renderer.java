package engine;

import engine.gfx.*;
import engine.gfx.Font;
import engine.gfx.Image;

import java.awt.image.DataBufferInt;
import java.util.ArrayList;
import java.util.Comparator;

public class Renderer {


    private int[] pixel;
    private int[] zBuffer;
    private int spaceWidth;
    private int spaceHeight;

    private ArrayList<ImageRequest> imageRequests = new ArrayList<>();


    private int zDepth = 0;
    private boolean processing = false;

    private Font font = Font.STANDARD;

    public Renderer(GameContainer gc){

        spaceWidth = gc.getWidth();
        spaceHeight = gc.getHeight();
        pixel = ((DataBufferInt) gc.getWindow().getImage().getRaster().getDataBuffer()).getData();
        zBuffer = new int[pixel.length];
    }



    public void clear(){
        for(int i =  0; i < pixel.length; i++){
            pixel[i] = 0;
            zBuffer[i] = 0;
        }
    }

    public void process(){

        processing = true;
        imageRequests.sort(Comparator.comparingInt(o -> o.zDepth));
        imageRequests.stream().forEach(ir -> {
            setzDepth(ir.zDepth);
            drawImage(ir.image, ir.offX, ir.offY);
        });


        for(int i = 0; i < pixel.length; i++){
            pixel[i] = ((((pixel[i] >> 16) & 0xff)) << 16 | (((pixel[i] >> 8) & 0xff)) << 8 | ((pixel[i] & 0xff)));
        }

        imageRequests.clear();
        processing = false;
    }

    public void setPixel(int x, int y, int value){

        int alpha = ((value >> 24) & 0xff);


        if(x < 0 || x >= spaceWidth || y < 0 || y >=  spaceHeight || alpha == 0 ){
            return;
        }

        int index = x + y * spaceWidth;

        if(zBuffer[index] > zDepth){
            return;
        }

        zBuffer[index] = zDepth;

        if(alpha == 255){
            pixel[index] = value;
        }
        else{
            int pixelColor = pixel[y * spaceWidth + x];
            int r = ((pixelColor >> 16) & 0xff) - (int)((((pixelColor >> 16) & 0xff) - ((value >> 16) & 0xff)) * (alpha / 255f));
            int g = ((pixelColor >> 8) & 0xff) - (int)((((pixelColor >> 8) & 0xff) - ((value >> 8) & 0xff)) * (alpha / 255f));
            int b = (pixelColor & 0xff) - (int)(((pixelColor & 0xff) - (value & 0xff)) * (alpha / 255f));

            pixel[index] = (r << 16 | g << 8 | b);
        }
    }

    
    public void drawText(String text, int offsetX, int offsetY, int color){
        int offset = 0;

        for(int i = 0; i < text.length(); i++){
            int unicode = text.codePointAt(i);

            for(int y = 0; y < font.getFontImage().getHeight(); y++){
                for(int x = 0; x < font.getWidths()[unicode]; x++){
                    if(font.getFontImage().getPixel()[(x + font.getOffsets()[unicode]) + y * font.getFontImage().getWidth()] == 0xffffffff){
                        setPixel(x + offsetX + offset, y + offsetY, color);
                    }
                }
            }
            offset += font.getWidths()[unicode];
        }
    }

    public void drawImage(Image image, int offX, int offY){

        if(image.isAlpha() && !processing){
            imageRequests.add(new ImageRequest(image,zDepth,offX,offY));
            return;
        }

        if(offX < -image.getWidth() || offX >= spaceWidth || offY < -image.getHeight()  || offY >=  spaceHeight) return;

        int newX = 0;
        int newY = 0;
        int newWidth = image.getWidth();
        int newHeight = image.getHeight();


        if(offX < 0){
            newX -= offX;
        }

        if(offY < 0){
            newY -= offY;
        }

        if(newWidth + offX >= spaceWidth){
           newWidth -= newWidth + offX - spaceWidth;
        }

        if(newHeight + offY >=  spaceHeight){
            newHeight -= newHeight + offY -  spaceHeight;
        }

        for(int y = newY; y < newHeight; y++){
            for(int x = newX; x < newWidth; x++){
                setPixel(x + offX, y + offY, image.getPixel()[x + y * image.getWidth()]);
            }
        }
    }

    public void drawFillRect(int offX, int offY, int width, int height, int color){

        if(offX < -width || offX >= spaceWidth || offY < -height  || offY >=  spaceHeight) return;

        int newX = 0, newY = 0;
        int newWidth = width;
        int newHeight = height;

        if(offX < 0){ newX -= offX; }
        if(offY < 0){ newY -= offY; }
        if(newWidth + offX >= spaceWidth){ newWidth -= newWidth + offX - spaceWidth; }
        if(newHeight + offY >=  spaceHeight){ newHeight -= newHeight + offY -  spaceHeight; }

        for(int y = newY; y < newHeight; y++){
            for(int x = newX; x < newWidth; x++){
                setPixel(x + offX, y + offY,color);
            }
        }
    }

    public void setzDepth(int zDepth) {
        this.zDepth = zDepth;
    }

}
