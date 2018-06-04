package army2.fight;

import army2.server.MapData;
import army2.server.Until;
import java.util.ArrayList;
import static army2.server.MapData.MapDataEntry;

/**
 *
 * @author ASD
 */
public class MapManager {

    private final FightManager fm;
    public short Width;
    public short Height;
    protected int Id;
    protected ArrayList<MapEntry> entrys;
    protected short[] XPlayerInit;
    protected short[] YPlayerInit;

    public MapManager(FightManager fm) {
        this.fm = fm;
        this.Id = 0;
        this.entrys = new ArrayList<>();
        this.Width  = 0;
        this.Height = 0;
    }

    public MapManager(FightManager fm, int map) {
        this.fm = fm;
        this.entrys = new ArrayList<>();
        this.Id = map;
    }

    public int getWidth() {
        return this.Width;
    }

    public int getHeight() {
        return this.Height;
    }

    public void setMapId(int id) {
        this.Id = id;
        int i, off;

        byte ab[] = null;
        for(i = 0; i < MapData.entrys.size(); i++) {
            MapDataEntry mEntry = MapData.entrys.get(i);
            if(mEntry.id == id)
                ab = mEntry.data;
        }
        if(ab == null)
            return;

        off = 0;
        this.Width  = Until.getShort(ab, off); off+=2;
        this.Height = Until.getShort(ab, off); off+=2;
        byte len = ab[off++];

        for(i = 0; i < len; i++) {
            int brickId = ab[off];

            if(!MapData.existsMapBrick(brickId))
                MapData.loadMapBrick(brickId);

            MapEntry me;
            if(MapData.existsMapBrick(brickId)) {
                MapData.MapBrickEntry mB = MapData.getMapBrickEntry(brickId);
                me = new MapEntry(brickId, Until.getShort(ab, off+1), Until.getShort(ab, off+3), mB.dat, (short)mB.Width, (short)mB.Height,!MapData.isNotColision(brickId));
            } else
                me = new MapEntry(brickId, Until.getShort(ab, off+1), Until.getShort(ab, off+3), null, (short)0, (short)0, !MapData.isNotColision(brickId));
            entrys.add(me);
            off += 5;
        }
        int nPlayerPoint = ab[off++];
        this.XPlayerInit = new short[nPlayerPoint];
        this.YPlayerInit = new short[nPlayerPoint];
        for(i = 0; i < nPlayerPoint; i++) {
            this.XPlayerInit[i] = Until.getShort(ab, off); off+=2;
            this.YPlayerInit[i] = Until.getShort(ab, off); off+=2;
        }
    }
    
    public final void addEntry(MapEntry me) {
        this.entrys.add(me);
    }

    public final boolean isCollision(short X, short Y) {
        for(MapEntry m : entrys)
            if(m.isCollision(X, Y))
                return true;
        return false;
    }

    public final void collision(short X, short Y, Bullet bull) {
        for(MapEntry m : entrys)
            m.collision(X, Y, bull);
        for(int i = 0; i < fm.allCount; i++) {
            Player pl = fm.players[i];
            if(pl != null)
                pl.collision(X, Y, bull);
        }
        for(int i = 0; i < fm.bullMNG.boms.size(); i++) {
            BulletManager.BomHenGio bom = fm.bullMNG.boms.get(i);
            while(!fm.mapMNG.isCollision((short)bom.X, (short)bom.Y)) {
                bom.Y++;
                for(int j = 0; j < 14; j++)
                    if(fm.mapMNG.isCollision((short)((bom.X - 7) + i), (short)bom.Y))
                        break;
                if(bom.Y > fm.mapMNG.Height) {
                    fm.bullMNG.removeBom(i);
                    break;
                }
            }
        }
    }
    
}
