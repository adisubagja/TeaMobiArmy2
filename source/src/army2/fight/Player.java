package army2.fight;

import army2.server.ServerManager;
import army2.server.Until;
import army2.server.User;
import java.io.IOException;

/**
 *
 * @author ASD
 */
public class Player {

    protected FightManager fightMNG;
    protected User us;
    protected boolean team;
    protected short idBullet;
    public    short X;
    public    short Y;
    public    short width;
    public    short height;
    protected int itemInit[];
    protected int item[];
    protected byte itemUsed;
    protected boolean isUseItem;
    protected boolean isUsePow;
    public    boolean isDie;
    protected boolean isUpdateHP;
    protected boolean isUpdateAngry;
    protected boolean isUpdateXP;
    protected boolean isUpdateCup;
    protected int     XPUp;
    protected int     CupUp;
    protected int     AllXPUp;
    protected int     AllCupUp;

    protected byte  idNV;
    protected byte  index;
    protected short gunId;
    protected int   angry;
    protected byte  pixel;
    protected int   HP;
    protected int   HPMax;
    protected int   sTPercen;
    protected int   phongThu;
    protected int   mayMan;
    protected int   dongDoi;
    protected byte  ngungGioCount;
    protected byte  hutMauCount;
    protected byte  tangHinhCount;
    public    byte  voHinhCount;
    protected byte  cantSeeCount;
    protected byte  cantMoveCount;
    protected boolean isBiDoc;
    protected boolean diX2;
    protected byte    buocDi;
    protected byte    theLuc;
    protected boolean isMM;

    public Player(FightManager fightMNG, byte location, short X, short Y, int item[], int teamPoint, User us) {
        this.fightMNG  = fightMNG;
        this.index     = location;
        this.team      = fightMNG.type == 5 || location % 2 == 0;
        this.idBullet  = -1;
        this.gunId     = -1;
        this.X         = X;
        this.Y         = Y;
        this.theLuc    = 60;
        this.width     = 24;
        this.height    = 24;
        this.itemInit  = item;
        if(item != null) {
            this.item      = new int[item.length];
            System.arraycopy(item, 0, this.item, 0, item.length);
        }
        this.us        = us;
        this.itemUsed  = -1;
        this.isUseItem = false;
        this.isUsePow  = false;
        this.isDie     = false;
        this.angry     = 0;
        this.pixel     = 25;
        this.dongDoi   = teamPoint;
        this.ngungGioCount = 0;
        this.hutMauCount = 0;
        this.voHinhCount = 0;
        this.cantSeeCount = 0;
        this.cantMoveCount = 0;
        this.isBiDoc = false;
        this.diX2 = false;
        this.buocDi = 0;
        this.isMM = false;
        this.isUpdateAngry = false;
        this.isUpdateHP    = false;
        this.isUpdateXP    = false;
        this.XPUp      = 0;
        this.CupUp     = 0;
        this.idNV      = 0;
        this.HPMax     = 0;
        this.sTPercen  = 0;
        this.phongThu  = 0;
        this.mayMan    = 0;
        this.HP        = 0;
        if(us == null)
            return;
        this.idBullet  = us.getIDBullet();
        this.gunId     = us.getGunId();
        this.idNV      = us.getNVUse();
        int[] ability  = us.getAbility();
        this.HPMax     = ability[0]+teamPoint*10;
        this.sTPercen  = ability[1]+teamPoint/3;
        this.phongThu  = ability[2]+teamPoint*10;
        this.mayMan    = ability[3]+teamPoint*10;
        this.HP        = this.HPMax;
    }

    public static int[] getLuyenTapItem() {
        return new int[] {0, 0, 0, 0, 0, 0, 0, 0};
    }

    public final void setXY(short X, short Y) {
        if(X >= 0 && X < this.fightMNG.mapMNG.Width && Y < this.fightMNG.mapMNG.Height) {
            this.X = X;
            this.Y = Y;
        }
    }

    public final void updateXY(short X, short Y) {
        while(X != this.X || Y != this.Y) {
            int preX = this.X;
            int preY = this.Y;
            if(X < this.X)
                move(false);
            else if(X > this.X)
                move(true);
            // if ko di chuyen dc
            if(preX == this.X && preY <= this.Y)
                return;
        }
    }

    public void chuanHoaXY() {
        while(this.Y < this.fightMNG.mapMNG.Height+200) {
            if(this.fightMNG.mapMNG.isCollision(X, Y))
                return;
            Y++;
        }
    }
    
    protected void move(boolean addX) {
        if(this.cantMoveCount > 0)
            return;
        byte step = 1;
        if(this.diX2)
            step = 2;
        if(buocDi > theLuc)
            return;
        buocDi++;
        if(addX)
            X += step;
        else
            X -= step;
        if(fightMNG.mapMNG.isCollision(X, (short)(Y - 5))) {
            buocDi--;
            if(addX)
                X -= step;
            else
                X += step;
            return;
        }
        for(int i = 4; i >= 0; i--)
            if(this.fightMNG.mapMNG.isCollision(X, (short)(Y - i))) {
                Y -= i;
                return;
            }
        this.chuanHoaXY();
    }

    public final void updateHP(int addHP) {
        this.isUpdateHP = true;
        this.HP += addHP;
        if(this.HP <= 0)
            this.HP = 0;
        else if(this.HP < 10)
            this.HP = 10;
        else if(this.HP > this.HPMax)
            this.HP = this.HPMax;
        int oldPixel = this.pixel;
        this.pixel = (byte)(this.HP*25/this.HPMax);
        if(addHP < 0)
            this.updateAngry((oldPixel-pixel)*4);
        if(this.HP <= 0)
            die();
    }

    public final void updateAngry(int addAngry) {
        this.isUpdateAngry = true;
        this.angry += addAngry;
        if(this.angry < 0)
            this.angry = 0;
        if(this.angry > 100)
            this.angry = 100;
    }
    
    public final void updateEXP(int addXP) throws IOException {
        if(us == null)
            return;
        this.isUpdateXP = true;
        this.XPUp = addXP;
    }

    public final void updateCUP(int addCup) throws IOException {
        if(us == null)
            return;
        this.isUpdateCup = true;
        this.CupUp = addCup;
        this.AllCupUp += addCup;
    }

    private void die() {
        this.isDie = true;
        if(us != null)
            us.notifyNetWait();
    }

    public void netWait() {
        this.fightMNG.countDownMNG.second += 2;
        if(us != null)
            us.netWait();
    }

    public void notifyNetWait() {
        if(us != null)
            us.notifyNetWait();
    }

    public boolean isCollision(short X, short Y) {
        if(this.voHinhCount > 0)
            return false;
        return Until.inRegion(X, Y, this.X-this.width/2, this.Y-this.height, this.width, this.height);
    }

    public void collision(short bx, short by, Bullet bull) {
        if(this.fightMNG.ltap)
            return;
        int tamAH = Bullet.getTamAHByBullID(bull.bullId);
        // Neu la tz or apa or chicky or rocket dung pow-> no rong gap doi
        if(bull.pl.isUsePow && (bull.pl.idNV == 3 || bull.pl.idNV == 4 || bull.pl.idNV == 6 || bull.pl.idNV == 7 || bull.pl.idNV == 8))
            tamAH = tamAH*2;
        if(this.isDie || this.voHinhCount > 0 || !Until.intersecRegions(X, Y, width, height, bx, by, tamAH*2, tamAH*2))
            return;
        if((bull.bullId == 31 || bull.bullId == 32) && this.index >=ServerManager.maxPlayers)
            return;
        int kcX = Math.abs(X-bx);
        int kcY = Math.abs(Y-this.height/2-by);
        int kc  = (int)Math.sqrt(kcX*kcX+kcY*kcY);
        int dame = bull.satThuong;
        if(bull.pl.isMM)
            dame = dame << 1;
        if(kc > this.width/2)
            dame = dame - ((dame*(kc-this.width/2))/tamAH);
        int PhongThu = this.phongThu;
        if(dame > 0) {
            if(bull.typeSC > 0) {
                if(bull.typeSC == 1) {
                    fightMNG.bullMNG.typeSC = 1;
                    dame = dame*11/10; // x1.1
                } else if(bull.typeSC == 2) {
                    fightMNG.bullMNG.typeSC = 1;
                    dame = dame*6/5; // x1.2
                } else if(bull.typeSC == 4) {
                    fightMNG.bullMNG.typeSC = 2;
                    dame = dame*13/10; // x1.3
                }
                fightMNG.bullMNG.XSC    = bull.XmaxY;
                fightMNG.bullMNG.YSC    = bull.maxY;
            }
            if(this.isMM)
                PhongThu += 3000;
        }
        // Tru phong thu
        dame = dame - Math.round((float)(dame*PhongThu)/4000);
        if(dame > 0) {
            int oldHP = this.HP;
            this.updateHP(-dame);
            if(bull.pl instanceof Boss)
                return;
            bull.pl.us.updateMission(1, oldHP-this.HP);
            if(bull.pl.hutMauCount > 0)
                bull.pl.updateHP(dame/2);
            // Neu ban chet + xp va dvong
            if(this.isDie) {
                // Tarzan
                if(this.idNV == 7)
                    bull.pl.us.updateMission(6, 1);
                if(this.idNV == 6)
                    bull.pl.us.updateMission(7, 1);
                if(this.idNV == 9)
                    bull.pl.us.updateMission(8, 1);
                try {
                    // Ban dong doi -5xp -5cup
                    if(this.fightMNG.type != 5 && this.team == bull.pl.team) {
                        bull.pl.updateCUP(-5);
                        bull.pl.updateEXP(-5);
                        return;
                    }
                    if(this.fightMNG.type == 5 && !(this instanceof Boss))
                        return;
                    if(this instanceof Boss) {
                        if(idNV == 11)
                            bull.pl.updateEXP(2);
                        if(idNV == 12)
                            bull.pl.updateEXP(4);
                    } else {
                        int cupCL = bull.pl.us.getDvong()-this.us.getDvong();
                        int cupAdd = (3000-cupCL)/100;
                        int levelPL = this.us.getLevel();
                        bull.pl.updateCUP(cupAdd);
                        bull.pl.updateEXP(2+(levelPL/2));
                        updateCUP(-cupAdd);
                        updateEXP(-(levelPL/3));
                    }
                } catch(IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    public void nextMM() {
        this.isMM = Until.nextInt(4000) <= this.mayMan;
    }

}
