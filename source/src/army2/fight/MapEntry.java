package army2.fight;

import army2.fight.Bullet;
import army2.server.Until;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 *
 * @author ASD
 */
public class MapEntry {

    public static int[] mangNhenData;
    public static int mangNhenW;
    public static int mangNhenH;
    public static final int[][] holeDatas;
    public static final int[]   holeW;
    public static final int[]   holeH;
    public int id;
    public int argb[];
    public short width;
    public short height;
    public short X;
    public short Y;
    public boolean canColision;
        
    static {
        holeDatas = new int[10][];
        holeW     = new int[10];
        holeH     = new int[10];
        BufferedImage img;
        try {
            img = ImageIO.read(new File("res/effect/hole/mangnhen.png"));
            mangNhenW = img.getWidth();
            mangNhenH = img.getHeight();
            mangNhenData = new int[mangNhenW*mangNhenH];
            img.getRGB(0, 0, mangNhenW, mangNhenH, mangNhenData, 0, mangNhenW);
        } catch(IOException e) {
            e.printStackTrace();
        }
        try {
            img = ImageIO.read(new File("res/effect/hole/h32x26.png"));
            holeW[0] = img.getWidth();
            holeH[0] = img.getHeight();
            holeDatas[0] = new int[holeW[0]*holeH[0]];
            img.getRGB(0, 0, holeW[0], holeH[0], holeDatas[0], 0, holeW[0]);
        } catch(IOException e) {
            e.printStackTrace();
        }
        try {
            img = ImageIO.read(new File("res/effect/hole/smallhole.png"));
            holeW[1] = img.getWidth();
            holeH[1] = img.getHeight();
            holeDatas[1] = new int[holeW[1]*holeH[1]];
            img.getRGB(0, 0, holeW[1], holeH[1], holeDatas[1], 0, holeW[1]);
        } catch(IOException e) {
            e.printStackTrace();
        }
        try {
            img = ImageIO.read(new File("res/effect/hole/smallhole.png"));
            holeW[2] = img.getWidth();
            holeH[2] = img.getHeight();
            holeDatas[2] = new int[holeW[2]*holeH[2]];
            img.getRGB(0, 0, holeW[2], holeH[2], holeDatas[2], 0, holeW[2]);
        } catch(IOException e) {
            e.printStackTrace();
        }
        try {
            img = ImageIO.read(new File("res/effect/hole/h36x30.png"));
            holeW[3] = img.getWidth();
            holeH[3] = img.getHeight();
            holeDatas[3] = new int[holeW[3]*holeH[3]];
            img.getRGB(0, 0, holeW[3], holeH[3], holeDatas[3], 0, holeW[3]);
        } catch(IOException e) {
            e.printStackTrace();
        }
        try {
            img = ImageIO.read(new File("res/effect/hole/rocket.png"));
            holeW[4] = img.getWidth();
            holeH[4] = img.getHeight();
            holeDatas[4] = new int[holeW[4]*holeH[4]];
            img.getRGB(0, 0, holeW[4], holeH[4], holeDatas[4], 0, holeW[4]);
        } catch(IOException e) {
            e.printStackTrace();
        }
        try {
            img = ImageIO.read(new File("res/effect/hole/rangehole.png"));
            holeW[5] = img.getWidth();
            holeH[5] = img.getHeight();
            holeDatas[5] = new int[holeW[5]*holeH[5]];
            img.getRGB(0, 0, holeW[5], holeH[5], holeDatas[5], 0, holeW[5]);
        } catch(IOException e) {
            e.printStackTrace();
        }
        try {
            img = ImageIO.read(new File("res/effect/hole/hrangcua.png"));
            holeW[6] = img.getWidth();
            holeH[6] = img.getHeight();
            holeDatas[6] = new int[holeW[6]*holeH[6]];
            img.getRGB(0, 0, holeW[6], holeH[6], holeDatas[6], 0, holeW[6]);
        } catch(IOException e) {
            e.printStackTrace();
        }
        try {
            img = ImageIO.read(new File("res/effect/hole/hgrenade.png"));
            holeW[7] = img.getWidth();
            holeH[7] = img.getHeight();
            holeDatas[7] = new int[holeW[7]*holeH[7]];
            img.getRGB(0, 0, holeW[7], holeH[7], holeDatas[7], 0, holeW[7]);
        } catch(IOException e) {
            e.printStackTrace();
        }
        try {
            img = ImageIO.read(new File("res/effect/hole/h14x12.png"));
            holeW[8] = img.getWidth();
            holeH[8] = img.getHeight();
            holeDatas[8] = new int[holeW[8]*holeH[8]];
            img.getRGB(0, 0, holeW[8], holeH[8], holeDatas[8], 0, holeW[8]);
        } catch(IOException e) {
            e.printStackTrace();
        }
        try {
            img = ImageIO.read(new File("res/effect/hole/h55x50.png"));
            holeW[9] = img.getWidth();
            holeH[9] = img.getHeight();
            holeDatas[9] = new int[holeW[9]*holeH[9]];
            img.getRGB(0, 0, holeW[9], holeH[9], holeDatas[9], 0, holeW[9]);
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public MapEntry(int id, short x, short y, int[] data, short W, short H, boolean show) {
        this.X = x;
        this.Y = y;
        this.id = id;
        this.canColision = show;
        this.setData(data, W, H);
    }

    public final void setData(int[] data, short W, short H) {
        this.width = W;
        this.height = H;
        this.argb = new int[data.length];
        for(int i = 0; i < data.length; i++)
            this.argb[i] = data[i];
    }

    public final boolean isCollision(short X, short Y) {
        return (Until.inRegion(X, Y, this.X, this.Y, this.width, this.height)
                    && Until.isNotAlpha(this.getARGB(X - this.X, Y - this.Y)));
    }

    public final void collision(short bx, short by, Bullet bull) {
        int index = Bullet.getHoleByBulletId(bull.bullId);
        int w = holeW[index];
        int h = holeH[index];
        int[] argbS = holeDatas[index];
        if(!this.canColision || !Until.intersecRegions(bx - w / 2, by - h / 2, w, h, this.X, this.Y, this.width, this.height))
            return;
        bx -= X + w / 2;
        by -= Y + h / 2;
        for(int i = 0; i < h; i++)
            for(int j = 0; j < w; j++)
                if(Until.inRegion(bx + j, by + i, 0, 0, width, height))
                    if(argbS[i * w + j] == 0xffff0000 && Until.isNotAlpha(getARGB(bx + j, by + i)))
                        this.argb[(by + i) * width + bx + j] = 0xff000000;
                    else if(argbS[i * w + j] == 0xff000000)
                        this.argb[(by + i) * width + bx + j] = 0;
    }

    public final int getARGB(int x, int y) {
        return argb[y * width + x];
    }

}
