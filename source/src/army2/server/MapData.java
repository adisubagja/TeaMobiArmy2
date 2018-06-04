package army2.server;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import java.io.File;

/**
 *
 * @author ASD
 */
public class MapData {

    public static final class MapDataEntry {
        public byte id;
        public String name;
        public String file;
        public byte[] data;
        public short bg;
        public short mapAddY;
        public short cl2AddY;
        public short inWaterAddY;
        public short bullEffShower;
        public short[] XPlayer;
        public short[] YPlayer;
    }
    
    public static final class MapBrickEntry {

        public int id;
        public int[] dat;
        public int Width;
        public int Height;

        MapBrickEntry(int id, int[] dat, int W, int H) {
            this.id = id;
            this.dat = dat;
            this.Width = W;
            this.Height = H;
        }

    }

    public static ArrayList<MapDataEntry> entrys;
    public static ArrayList<MapBrickEntry> brickEntrys;
    public static final short[] idNotColision = new short[]{70, 71, 73, 74, 75, 77, 78, 79, 97};

    public static boolean isNotColision(int id) {
        for(int i = 0; i < idNotColision.length; i++)
            if(id == idNotColision[i])
                return true;
        return false;
    }

    public static MapBrickEntry getMapBrickEntry(int id) {
        for(MapBrickEntry me : brickEntrys) {
            if(me.id == id)
                return me;
        }
        return null;
    }

    public static void loadMapBrick(int id) {
        ServerManager.log("Load Map Brick id="+id);
        try {
            BufferedImage img = ImageIO.read(new File("res/icon/map/"+id+".png"));
            int W = img.getWidth();
            int H = img.getHeight();
            int[] argb = new int[W*H];
            img.getRGB(0, 0, W, H, argb, 0, W);
            MapBrickEntry me = new MapBrickEntry(id, argb, W, H);
            brickEntrys.add(me);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean existsMapBrick(int id) {
        for(MapBrickEntry me : brickEntrys) {
            if(me.id == id)
                return true;
        }
        return false;
    }

}
