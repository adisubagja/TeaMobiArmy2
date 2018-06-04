package army2.fight;

import java.util.ArrayList;

/**
 *
 * @author ASD
 */
public class Bullet {

    protected FightManager  fm;
    protected BulletManager bullMNG;
    protected boolean       collect;
    protected byte          bullId;
    protected int           satThuong;
    protected short         X;
    protected short         Y;
    protected short         lastX;
    protected short         lastY;
    protected short         vx;
    protected short         vy;
    protected short         ax100;
    protected short         ay100;
    protected short         g100;
    protected short         vxTemp;
    protected short         vyTemp;
    protected short         vyTemp2;
    protected boolean       isMaxY;
    protected short         XmaxY;
    protected short         maxY;
    protected short         frame;
    protected byte          typeSC;
    protected Player        pl;
    protected boolean       isXuyenPlayer;
    protected boolean       isXuyenMap;
    protected boolean       isCanCollision;
    public ArrayList<Short> XArray;
    public ArrayList<Short> YArray;

    public static byte getHoleByBulletId(byte bullId) {
        switch(bullId) {
            case 0: // '\0'
            case 32: // ' '
                return 3;

            case 1: // '\001'
                return 1;

            case 2: // '\002'
                return 0;

            case 6: // '\006'
                return 6;

            case 7: // '\007'
            case 31: // '\037'
            case 37: // '%'
                return 7;

            case 3: // '\003'
                return 9;

            case 9: // '\t'
                return 5;

            case 10: // '\n'
                return 4;

            case 11: // '\013'
                return 2;

            case 12: // '\f'
                return 6;

            case 15: // '\017'
                return 7;

            case 22: // '\026'
                return 7;

            case 24: // '\030'
                return 3;

            case 25: // '\031'
                return 8;

            case 19: // '\023'
                return 2;

            case 20: // '\024'
                return 0;

            case 27: // '\033'
                return 1;

            case 17: // '\021'
            case 18: // '\022'
                return 2;
                
            case 30: // '\036'
                return 0;

            case 21: // '\025'
                return 2;

            case 42: // '*'
            case 43: // '+'
                return 7;

            case 44: // ','
                return 2;

            case 45: // '-'
                return 7;

            case 47: // '/'
                return 8;

            case 48: // '0'
                return 3;

            case 52: // '4'
                return 3;

            case 57: // '9'
                return 7;

            default:
                return 0;
        }
    }

    public static int getTamAHByBullID(int bullId) {
        switch(bullId) {
            case 0:
                return 18;
                
            case 1:
                return 11;

            case 2:
                return 16;

            case 3:
                return 28;

            case 6:
                return 20;

            case 7:
                return 28;

            case 8:
                return 20;

            case 9:
                return 16;

            case 10:
                return 17;

            case 11:
                return 11;

            case 12:
                return 20;

            case 15:
                return 28;

            case 17:
            case 18:
                return 11;

            case 19:
                return 11;

            case 20:
                return 16;

            case 21:
                return 11;

            case 22:
                return 28;

            case 24:
                return 18;

            case 25:
                return 7;

            case 27:
                return 11;

            case 30:
                return 16;

            case 31:
                return 30;
                
            case 32:
                return 30;

            case 42:
            case 43:
                return 28;

            case 44:
                return 11;

            case 45:
                return 28;

            case 47:
                return 7;

            case 48:
                return 18;

            case 49:
                return 16;
                
            case 50:
                return 30;

            case 51:
                return 30;

            case 52:
                return 18;

            case 54:
                return 30;

            case 55:
                return 30;

            case 57:
                return 28;
                
            case 59:
                return 16;
        }
        return 0;
    }
    
    public static boolean isItem(int bullId) {
        return bullId == 4 || bullId == 14 || bullId == 16 || bullId == 23
                || bullId == 28;
    }

    public static boolean isChicApa(int bullId) {
        return bullId == 17 || bullId == 19;
    }

    public Bullet(BulletManager bullMNG, byte bullId, int satThuong, Player pl, int X, int Y, int vx, int vy, int msg, int g100) {
        this.fm        = bullMNG.fm;
        this.bullMNG   = bullMNG;
        this.bullId    = bullId;
        this.satThuong = (satThuong*pl.sTPercen)/100;
        this.pl        = pl;
        this.X         = (short)X;
        this.Y         = (short)Y;
        this.lastX     = (short)X;
        this.lastY     = (short)Y;
        this.vx        = (short)vx;
        this.vy        = (short)vy;
        this.ax100     = (short)(bullMNG.fm.WindX*msg/100);
        this.ay100     = (short)(bullMNG.fm.WindY*msg/100);
        this.g100      = (short)g100;
        this.vxTemp    = 0;
        this.vyTemp    = 0;
        this.vyTemp2   = 0;
        this.collect   = false;
        this.isMaxY    = false;
        this.XmaxY     = -1;
        this.maxY      = -1;
        this.frame     = 0;
        this.typeSC    = 0;
        this.XArray    = new ArrayList<>();
        this.YArray    = new ArrayList<>();
        this.isXuyenPlayer   = false;
        this.isXuyenMap      = false;
        this.isCanCollision  = true;
    }
    
    public boolean isCollect() {
        return this.collect;
    }

    public void nextXY() {
        frame++;
        this.XArray.add((short)X);
        this.YArray.add((short)Y);
        if((X < -200) || (X > fm.mapMNG.Width + 200) || (Y > fm.mapMNG.Height+200)) {
            collect = true;
            return;
        }
        short preX = X, preY = Y;
        X += vx; lastX = X;
        Y += vy; lastY = Y;
        short[] XYVC = bullMNG.getCollisionPoint(preX, preY, X, Y, isXuyenPlayer, isXuyenMap);
        if(XYVC != null) {
            collect = true;
            X = XYVC[0]; Y = XYVC[1];
            XArray.add((short)X);
            YArray.add((short)Y);
            if(pl.itemUsed == -1 && !pl.isUsePow) {
                if(this.isMaxY) {
                    if(this.Y-this.maxY > 350 && this.Y - this.maxY < 450)
                        this.typeSC = 1;
                    else if(this.Y - this.maxY >= 450)
                        this.typeSC = 2;
                }
                if((pl.gunId == 2 || pl.gunId == 3) && (Math.abs(lastX - XArray.get(0)) > 375))
                    this.typeSC = 4;
            }
            if(this.isCanCollision)
                fm.mapMNG.collision(X, Y, this);
            return;
        }
        vxTemp  += Math.abs(ax100);
        vyTemp  += Math.abs(ay100);
        vyTemp2 += g100;
        if(Math.abs(vxTemp) >= 100) {
            if(ax100 > 0)
                vx+=vxTemp/100;
            else
                vx-=vxTemp/100;
            vxTemp %= 100;
        }
        if(Math.abs(vyTemp) >= 100) {
            if(ay100 > 0)
                vy+=vyTemp/100;
            else
                vy-=vyTemp/100;
            vyTemp %= 100;
        }
        if(Math.abs(vyTemp2) >= 100) {
            vy+=vyTemp2/100;
            vyTemp2 %= 100;
        }
        if(vy > 0 && !isMaxY) {
            isMaxY = true;
            XmaxY = X;
            maxY = Y;
        }
        if(this.bullMNG.hasVoiRong) {
            for(BulletManager.VoiRong vr : this.bullMNG.voiRongs) {
                if(this.X >= vr.X-5 && this.X <= vr.X+10) {
                    this.vx-=2;
                    this.vy-=2;
                    break;
                }
            }
        }
    }

}
