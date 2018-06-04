package army2.fight;

import army2.fight.bullet.ChickyBullet;
import army2.fight.bullet.ApaBullet;
import army2.fight.bullet.TazranBullet;
import army2.fight.bullet.MGTBulletNew;
import army2.fight.bullet.MGTBulletOld;
import army2.fight.bullet.ItemBomB52;
import army2.fight.bullet.ItemTeleport;
import army2.fight.bullet.ItemKhoangDat;
import army2.fight.bullet.ItemLaser;
import army2.fight.bullet.ItemChuotGanBom;
import army2.fight.bullet.ItemVoiRong;
import army2.fight.bullet.ItemMuaDan;
import army2.fight.bullet.ItemSaoBang;
import army2.fight.bullet.ItemToNhen;
import army2.fight.bullet.ItemTraiPha;
import army2.fight.bullet.ItemXuyenDat;
import army2.fight.bullet.ItemKhoangDat2;
import army2.fight.bullet.ItemBomMu;
import army2.fight.bullet.ItemDongBang;
import army2.fight.bullet.ItemKhoiDoc;
import army2.fight.bullet.ItemTuSat;
import army2.fight.bullet.ItemBomHenGio;
import army2.fight.bullet.BigBoomBum;
import army2.fight.bullet.SmallBoomBum;
import army2.server.ServerManager;
import army2.server.Until;
import java.util.ArrayList;
import java.io.IOException;

/**
 *
 * @author ASD
 */
public class BulletManager {

    public static class VoiRong {

        public int X;
        public int Y;
        public int count;

        public VoiRong(int X, int Y, int count) {
            this.X = X;
            this.Y = Y;
            this.count = count;
        }
        
    }
    
    public static class BomHenGio {
        
        public int id;
        public int X;
        public int Y;
        public int count;
        public Bullet bull;
        
        public BomHenGio(int id, Bullet bull, int count) {
            this.id = id;
            this.X  = bull.X;
            this.Y  = bull.Y;
            this.count = count;
            this.bull = bull;
        }
        
    }
    public    FightManager fm;
    protected ArrayList<Bullet> entrys;
    protected byte force2;

    public    int mangNhenId;
    public    boolean hasVoiRong;
    public    ArrayList<VoiRong> voiRongs;
    public    ArrayList<BomHenGio> boms;
    public    byte  mgtAddX;
    public    byte  mgtAddY;
    public    byte  typeSC;
    public    short XSC;
    public    short YSC;
    public    short arg;
    public    short XPL;
    public    short YPL;

    public BulletManager(FightManager fm) {
        this.fm = fm;
        this.entrys = new ArrayList<>();
        this.voiRongs = new ArrayList<>();
        this.boms = new ArrayList<>();
        this.hasVoiRong = false;
        this.force2 = -1;
        this.mangNhenId = 200;
        this.XSC = 0;
        this.YSC = 0;
        this.typeSC = 0;
    }
    
    public void addBom(ItemBomHenGio bull) {
        BomHenGio bom = new BomHenGio(this.boms.size(), bull, 5);
        boms.add(bom);
        try {
            fm.addBom(bom.id, bull.X, bull.Y);
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
    
    public void removeBom(int id) {
        boms.remove(id);
    }
    
    public void exploreBom(int id) {
        BomHenGio bom = this.boms.get(id);
        try {
            fm.exploreBom(id, bom.X, bom.Y, bom.bull);
        } catch(IOException e) {
            e.printStackTrace();
        }
        boms.remove(id);
    }
    
    public void addBullet(Bullet bul) {
        this.entrys.add(bul);
    }

    public void addShoot(Player pl, byte bull, short angle, byte force, byte force2, byte nshoot) {
        this.XPL = pl.X;
        this.YPL = pl.Y;
        this.arg = angle;
        this.force2 = force2;
        this.typeSC = 0;
        int x, y, vx, vy, idGun;

        x  = pl.X+(20*Until.cos(angle)>>10);
        y  = pl.Y-12-(20*Until.sin(angle)>>10);
        vx =  (force * Until.cos(angle) >> 10);
        vy = -(force * Until.sin(angle) >> 10);

        idGun = pl.idNV;
        if(nshoot > 2 || nshoot < 1)
            return;
        for(int k = 0; k < nshoot; k++) {
            switch(bull) {
            // Gunner
            case 0:
                if(pl.itemUsed >= 0 || idGun != 0)
                    return;
                entrys.add(new Bullet(this, (byte)0, (pl.isUsePow ? 630 : (nshoot == 2 ? 210:280)), pl, x, y, vx, vy, 80, 100));
                break;

            // Aka
            case 1:
                if(pl.itemUsed >= 0 || idGun != 1)
                    return;
                int nBull = pl.isUsePow ? 6:2;
                for(int i = 0; i < nBull; i++)
                    entrys.add(new Bullet(this, (byte)1, nshoot==2? 109:145, pl, x, y, vx, vy, 50, 50));
                break;

            // Electric
            case 2:
                if(pl.itemUsed >= 0 || idGun != 2)
                    return;
                int nBulls = pl.isUsePow ? 5:3;
                for(int i=0; i<nBulls; i++) {
                    int arg = angle+i*5;
                    x  = pl.X+(20*Until.cos(arg)>>10);
                    y  = pl.Y-12-(20*Until.sin(arg)>>10);
                    vx =  (force * Until.cos(arg) >> 10);
                    vy = -(force * Until.sin(arg) >> 10);
                    entrys.add(new Bullet(this, (byte)2, nshoot==2? 75:100, pl, x, y, vx, vy, 80, 60));
                    if(i == 0)
                        continue;
                    arg = angle-i*5;
                    x  = pl.X+(20*Until.cos(arg)>>10);
                    y  = pl.Y-12-(20*Until.sin(arg)>>10);
                    vx =  (force * Until.cos(arg) >> 10);
                    vy = -(force * Until.sin(arg) >> 10);
                    entrys.add(new Bullet(this, (byte)2, nshoot==2? 75:100, pl, x, y, vx, vy, 80, 60));
                }
                break;

            // Item bom huy diet
            case 4:
                if(pl.itemUsed != 8)
                    return;
                pl.us.updateMission(5, 1);
                entrys.add(new ItemBomB52(this, (byte)4, 600, pl, x, y, vx, vy, 0, 80));
                break;

            // Item bay
            case 5:
                if(pl.itemUsed != 1)
                    return;
                pl.isMM = false;
                entrys.add(new ItemTeleport(this, (byte)5, 0, pl, x, y, vx, vy, 0, 80));
                break;

            // Item bom pha dat
            case 6:
                if(pl.itemUsed != 6)
                    return;
                for(int i=0; i<3; i++)
                    entrys.add(new Bullet(this, (byte)6, 200, pl, x, y, vx, vy, 70, 90));
                break;

            // Item luu dan
            case 7:
                if(pl.itemUsed != 7)
                    return;
                entrys.add(new Bullet(this, (byte)7, 500, pl, x, y, vx, vy, 70, 80));
                break;
            
            // Item to nhen
            case 8:
                if(pl.itemUsed != 9)
                    return;
                entrys.add(new ItemToNhen(this, (byte)8, 300, pl, x, y, vx, vy, 70, 70));
                break;
            
            // TODO
            // Kingkong
            case 9:
                if(pl.itemUsed >= 0 || idGun != 3)
                    return;
                int arg2 = angle - 6;
                for(int i=0; i<4; i++, arg2+=4) {
                    x  = pl.X+(20*Until.cos(arg2)>>10);
                    y  = pl.Y-12-(20*Until.sin(arg2)>>10);
                    vx =  (force * Until.cos(arg2) >> 10);
                    vy = -(force * Until.sin(arg2) >> 10);
                    entrys.add(new Bullet(this, (byte)9, pl.isUsePow? 210:(nshoot==2? 79:105), pl, x, y, vx, vy, 40, 90));
                }
                break;

            // Rocket
            case 10:
                if(pl.itemUsed >= 0 || idGun != 4)
                    return;
                for(int i = 0; i < 3; i++)
                    entrys.add(new Bullet(this, (byte)10, pl.isUsePow? 240:(nshoot==2? 80:107), pl, x, y, vx, vy, 50, 80));
                break;

            // Granos
            case 11:
                if(pl.itemUsed >= 0 || idGun != 5)
                    return;
                for(int i = 0; i < 5; i++)
                    entrys.add(new Bullet(this, (byte)11, pl.isUsePow? 140:(nshoot==2? 47:62), pl, x, y, vx, vy, 30, 90));
                break;

            // Item dan voi rong
            case 13:
                if(pl.itemUsed != 17)
                    return;
                pl.isMM = false;
                entrys.add(new ItemVoiRong(this, (byte)13, 0, pl, x, y, vx, vy, 50, 120));
                break;
                
            // Item dan laser
            case 14:
                if(pl.itemUsed != 16)
                    return;
                entrys.add(new ItemLaser(this, (byte)14, 500, pl, x, y, vx, vy, 10, 50));
                break;
                
            // Item dan trai pha
            case 16:
                if(pl.itemUsed != 11)
                    return;
                entrys.add(new ItemTraiPha(this, (byte)16, 200, pl, x, y, vx, vy, 0, 100));
                break;

            // TODO
            // Apache
            case 17:
                if(pl.itemUsed >= 0 || idGun != 8)
                    return;
                entrys.add(new ApaBullet(this, (byte)17, pl.isUsePow? 216:(nshoot==2? 81:108), pl, x, y, vx, vy, 30, 100, force, force2));
                break;

            // Chicky
            case 19:
                if(pl.itemUsed >= 0 || idGun != 6)
                    return;
                entrys.add(new ChickyBullet(this, (byte)19, pl.isUsePow? 500: (nshoot==2? 169:225), pl, x, y, vx, vy, 20, 50, force2));
                break;

            // Tazan
            case 21:
                if(pl.itemUsed >= 0 || idGun != 7)
                    return;
                entrys.add(new TazranBullet(this, (byte)21, pl.isUsePow? 800: (nshoot==2? 225:340), pl, x, y, vx, vy, 10, 50));
                break;

            // Item chuot gan bom
            case 22:
                if(pl.itemUsed != 18)
                    return;
                entrys.add(new ItemChuotGanBom(this, (byte)22, 500, pl, x, y, force, angle < 89));
                break;

            // Item Sao Bang
            case 23:
                if(pl.itemUsed != 21)
                    return;
                entrys.add(new ItemSaoBang(this, (byte)23, 200, pl, x, y, vx, vy, 20, 100));
                break;

            // Item Dan xuyen dat
            case 25:
                if(pl.itemUsed != 20)
                    return;
                vy = (force * Until.sin(-angle) >> 10);
                entrys.add(new ItemXuyenDat(this, (byte)25, 500, pl, x, y, vx, vy, 0, -50, force));
                break;

            // TODO
            // Item ten lua
            case 26:
                if(pl.itemUsed != 19)
                    return;
                entrys.add(new Bullet(this, (byte)26, 200, pl, x, y, vx, vy, 30, 60));
                break;

            // Item mua dan
            case 28:
                if(pl.itemUsed != 22)
                    return;
                vx = 0;
                vy = -force/2;
                entrys.add(new ItemMuaDan(this, (byte)28, 200, pl, x, y, vx, vy, 0, 20));
                break;

            // Item khoang dat
            case 30:
                if(pl.itemUsed != 23)
                    return;
                pl.isMM = false;
                entrys.add(new ItemKhoangDat(this, (byte)30, pl, pl.X, pl.Y, force));
                break;

            // Big boom bum
            case 31:
                if(idGun != 12)
                    return;
                entrys.add(new BigBoomBum(this, (byte)31, 2500, pl));
                break;

            // Small boom bum
            case 32:
                if(idGun != 11)
                    return;
                entrys.add(new SmallBoomBum(this, (byte)32, 600, pl));
                break;

            // Magenta
            case 49:
                if(pl.itemUsed >= 0 || idGun != 9)
                    return;
                if(ServerManager.mgtBullNew)
                    entrys.add(new MGTBulletNew(this, (byte)49, pl.isUsePow? 1000: (nshoot==2? 308:400), pl, x, y, vx, vy, 40, 70));
                else {
                    vx =  (1600 * Until.cos(angle) >> 10);
                    vy = -(1600 * Until.sin(angle) >> 10);
                    entrys.add(new MGTBulletOld(this, (byte)59, pl.isUsePow? 1000: (nshoot==2? 308:400), pl, x, y, vx, vy, force));
                }
                break;
                
            // TODO
            // Item tu sat
            case 50:
                if(pl.itemUsed != 24)
                    return;
                entrys.add(new ItemTuSat(this, (byte)50, 1500, pl));
                break;

            // Item bom mu
            case 51:
                if(pl.itemUsed != 25)
                    return;
                pl.isMM = false;
                entrys.add(new ItemBomMu(this, (byte)51, 0, pl, x, y, vx, vy, 5, 60));
                break;

            // Item Khoang dat 2
            case 52:
                if(pl.itemUsed != 26)
                    return;
                entrys.add(new ItemKhoangDat2(this, (byte)52, 500, pl, x, y, vx, vy, 10, 100));
                break;

            // TODO
            // Item UFO
            case 53:
                if(pl.itemUsed != 27)
                    return;
                break;

            // Item Dong Bang
            case 54:
                if(pl.itemUsed != 28)
                    return;
                pl.isMM = false;
                entrys.add(new ItemDongBang(this, (byte)54, 0, pl, x, y, vx, vy, 0, 80));
                break;
                
            // Item Khoi Doc
            case 55:
                if(pl.itemUsed != 29)
                    return;
                pl.isMM = false;
                entrys.add(new ItemKhoiDoc(this, (byte)55, 150, pl, x, y, vx, vy, 6, 60));
                break;

            // Item To nhen 2
            case 56:
                if(pl.itemUsed != 30)
                    return;
                int arg3 = angle - 5;
                for(int i=0; i<3; i++, arg3+=5) {
                    x  = pl.X+(20*Until.cos(arg3)>>10);
                    y  = pl.Y-12-(20*Until.sin(arg3)>>10);
                    vx =  (force * Until.cos(arg3) >> 10);
                    vy = -(force * Until.sin(arg3) >> 10);
                    entrys.add(new ItemToNhen(this, (byte)56, 300, pl, x, y, vx, vy, 70, 70));
                }
                break;

            // Item Bom hen gio
            case 57:
                if(pl.itemUsed != 31)
                    return;
                pl.isMM = false;
                entrys.add(new ItemBomHenGio(this, (byte)57, 600, pl, x, y, vx, vy, 0, 120));
                break;
            }
        }
    }

    public void fillXY() {
        boolean hasNext;
        do {
            hasNext = false;
            for(int i = 0; i < this.entrys.size(); i++) {
                Bullet bull = this.entrys.get(i);
                if((bull == null) || bull.isCollect())
                    continue;
                hasNext = true;
                bull.nextXY();
            }
        } while(hasNext);
    }

    public void reset() {
        this.entrys.clear();
    }

    public short[] getCollisionPoint(short X1, short Y1, short X2, short Y2, boolean isXuyenPlayer, boolean isXuyenMap) {
        int  Dx = X2 - X1;
        int  Dy = Y2 - Y1;
        byte x_unit  = 0;
        byte y_unit  = 0;
        byte x_unit2 = 0;
        byte y_unit2 = 0;
        if(Dx < 0)
            x_unit = x_unit2 = -1;
        else if(Dx > 0)
            x_unit = x_unit2 = 1;
        if(Dy < 0)
            y_unit = y_unit2 = -1;
        else if(Dy > 0)
            y_unit = y_unit2 = 1;
        int k1 = Math.abs(Dx);
        int k2 = Math.abs(Dy);
        if(k1 > k2) {
            y_unit2 = 0;
        } else {
            k1 = Math.abs(Dy);
            k2 = Math.abs(Dx);
            x_unit2 = 0;
        }
        int k = k1 >> 1;
        short X = X1, Y = Y1;
        for(int i = 0; i <= k1; i++) {
            if(!isXuyenMap) {
                if(fm.mapMNG.isCollision(X, Y))
                    return new short[] {X, Y};
            }
            if(!isXuyenPlayer) {
                for(int j = 0; j < fm.allCount; j++) {
                    Player pl = fm.players[j];
                    if(pl != null) {
                        if(pl.isCollision(X, Y))
                            return new short[] {X, Y};
                    }
                }
            }
            k += k2;
            if(k >= k1) {
                k -= k1;
                X += x_unit;
                Y += y_unit;
            } else {
                X += x_unit2;
                Y += y_unit2;
            }
        }
        return null;
    }

}
