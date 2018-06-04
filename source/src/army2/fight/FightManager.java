package army2.fight;

import army2.server.GameString;
import army2.server.ItemData;
import army2.server.ServerManager;
import army2.server.Until;
import army2.server.User;
import java.util.ArrayList;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import network.Message;
import army2.fight.boss.BigBoom;

/**
 *
 * @author ASD
 */
public class FightManager {

    private   final FightWait   wait;
    private   final User        userLt;

    protected boolean       ltap;
    protected byte          type;
    protected boolean       isBossTurn;
    protected byte          bossTurn;
    protected byte          playerTurn;
    protected byte          nTurn;
    protected byte          nHopQua;
    protected int           playerCount;
    protected int           allCount;
    protected int           WindX;
    protected int           WindY;
    protected boolean       isFight;
    protected final byte    timeCountMax = 30;

    public    Player[]      players;
    public    MapManager    mapMNG;
    public    BulletManager bullMNG;
    public    CountDownMNG  countDownMNG;

    public FightManager(User us, byte map) {
        this.wait         = null;
        this.ltap         = true;
        this.type         = 0;
        this.playerCount  = 1;
        this.playerTurn   = -1;
        this.nTurn        = 0;
        this.isBossTurn   = false;
        this.bossTurn     = 0;
        this.allCount     = 1;
        this.WindX        = 0;
        this.WindY        = 0;
        this.isFight      = false;
        this.nHopQua      = 0;
        this.mapMNG       = new MapManager(this);
        this.bullMNG      = new BulletManager(this);
        this.countDownMNG = null;
        this.userLt       = us;
        this.mapMNG.setMapId(map);
    }

    public FightManager(FightWait fo) {
        this.wait         = fo;
        this.userLt       = null;
        this.ltap         = false;
        this.type         = fo.type;
        this.playerCount  = 0;
        this.allCount     = 0;
        this.playerTurn   = -1;
        this.isBossTurn   = false;
        this.bossTurn     = 0;
        this.WindX        = 0;
        this.WindY        = 0;
        this.nHopQua      = 0;
        this.players      = new Player[ServerManager.maxElementFight];
        this.isFight      = false;
        this.mapMNG       = new MapManager(this);
        this.bullMNG      = new BulletManager(this);
        this.countDownMNG = new CountDownMNG(this, timeCountMax);
    }
    
    protected void setMap(byte map) {
        this.mapMNG.setMapId(map);
    }

    private void sendToTeam(Message ms) throws IOException {
        if(this.ltap) {
            this.userLt.sendMessage(ms);
            return;
        }
        for(int i = 0; i < ServerManager.maxPlayers; i++) {
            Player pl = this.players[i];
            if(pl == null || pl.us == null)
                continue;
            pl.us.sendMessage(ms);
        }
    }

    public void removeUser(User us) {
        synchronized(this.players) {
            for(int i=0; i<ServerManager.maxPlayers; i++)
                if(this.players[i].us.getIDDB() == us.getIDDB()) {
                    this.players[i] = null;
                    break;
                }
        }
    }

    public int getIndexByIDDB(int iddb) {
        for(int i = 0; i < ServerManager.maxPlayers; i++) {
            if(this.players[i] != null && this.players[i].us != null && this.players[i].us.getIDDB() == iddb)
                return i;
        }
        return -1;
    }
    
    public Player getPlayerTurn() {
        if(isBossTurn)
            return this.players[this.bossTurn];
        return this.players[this.playerTurn];
    }
    
    public Player getPlayerClosest(short X, short Y) {
        int XClosest = -1; Player plClosest = null;
        for(int i = 0; i < ServerManager.maxPlayers; i++) {
            Player pl = this.players[i];
            if(pl == null || pl.isDie)
                continue;
            int kcX = Math.abs(pl.X-X);
            if(XClosest == -1 || kcX < XClosest) {
                XClosest = kcX;
                plClosest = pl;
            }
        }
        return plClosest;
    }
    
    public int getWindX() {
        return this.WindX;
    }
    
    public int getWindY() {
        return this.WindY;
    }

    private void nextBoss() throws IOException {
        if(this.wait.map == 30) {
            int numBoss = (new int[]{4, 4, 5, 5, 6, 8, 8, 9, 9})[playerCount];
            for(int i = 0; i < numBoss; i++) {
                short X = (short)((i%2 == 0)? Until.nextInt(100, 200):Until.nextInt(950, 1000));
                short Y = (short)(50+40*Until.nextInt(3));
                players[allCount] = new BigBoom(this, (byte)12, "BOSS", (byte)allCount, 1500, X, Y);
                allCount++;
            }
        }
        int bossLen = this.allCount-ServerManager.maxPlayers;
        Message ms = new Message(89); DataOutputStream ds = ms.writer();
        ds.writeByte(bossLen);
        for(int i = 0; i < bossLen; i++) {
            Boss boss = (Boss)this.players[ServerManager.maxPlayers+i];
            ds.writeInt(-1);
            ds.writeUTF(boss.name);
            ds.writeInt(boss.HPMax);
            ds.writeByte(boss.idNV);
            ds.writeShort(boss.X);
            ds.writeShort(boss.Y);
        }
        ds.flush();
        this.sendToTeam(ms);
    }
    
    private void nextAngry() throws IOException {
        Message ms; DataOutputStream ds;
        for(int i = 0; i < ServerManager.maxPlayers; i++) {
            Player pl = this.players[i];
            if(pl == null)
                continue;
            if(pl.isUpdateAngry) {
                ms = new Message(113); ds = ms.writer();
                ds.writeByte(i);
                ds.writeByte(pl.angry);
                ds.flush();
                this.sendToTeam(ms);
                pl.isUpdateAngry = false;
            }
        }
    }

    private void calcMM() {
        for(int i = 0; i < ServerManager.maxPlayers; i++) {
            Player pl = this.players[i];
            if(pl == null)
                continue;
            pl.nextMM();
        }
    }
    
    private void nextMM() throws IOException {
        Message ms; DataOutputStream ds;
        for(int i = 0; i < ServerManager.maxPlayers; i++) {
            Player pl = this.players[i];
            if(pl == null)
                continue;
            if(pl.isMM) {
                if(i == this.playerTurn || pl.isUpdateHP) {
                    ms = new Message(100); ds = ms.writer();
                    ds.writeByte(i);
                    ds.flush();
                    this.sendToTeam(ms);
                }
                pl.isMM = false;
            }
        }
    }

    private void nextBiDoc() throws IOException {
        Message ms; DataOutputStream ds;
        for(int i = 0; i < ServerManager.maxPlayers; i++) {
            Player pl = this.players[i];
            if(pl == null)
                continue;
            if(pl.isBiDoc) {
                ms = new Message(108); ds = ms.writer();
                ds.writeByte(pl.index);
                ds.flush();
                this.sendToTeam(ms);
            }
        }
    }

    private void nextCantSee() throws IOException {
        Message ms; DataOutputStream ds;
        for(int i = 0; i < ServerManager.maxPlayers; i++) {
            Player pl = this.players[i];
            if(pl == null)
                continue;
            if(pl.cantSeeCount > 0) {
                ms = new Message(106); ds = ms.writer();
                ds.writeByte(0);
                ds.writeByte(pl.index);
                ds.flush();
                this.sendToTeam(ms);
            }
        }
    }

    private void nextCantMove() throws IOException {
        Message ms; DataOutputStream ds;
        for(int i = 0; i < ServerManager.maxPlayers; i++) {
            Player pl = this.players[i];
            if(pl == null)
                continue;
            if(pl.cantMoveCount > 0) {
                ms = new Message(107); ds = ms.writer();
                ds.writeByte(0);
                ds.writeByte(pl.index);
                ds.flush();
                this.sendToTeam(ms);
            }
        }
    }

    private void nextHP() throws IOException {
        Message ms; DataOutputStream ds;
        for(int i = 0; i < this.allCount; i++) {
            Player pl = this.players[i];
            if(pl == null)
                continue;
            if(pl.isUpdateHP) {
                ms = new Message(51); ds = ms.writer();
                ds.writeByte(i);
                ds.writeShort(pl.HP);
                ds.writeByte(pl.pixel);
                ds.flush();
                this.sendToTeam(ms);
                pl.isUpdateHP = false;
            }
        }
        this.nextXP();
        this.nextCUP();
        this.nextAngry();
    }

    private void nextXP() {
        for(int i = 0; i < ServerManager.maxPlayers; i++) {
            Player pl = this.players[i];
            if(pl == null)
                continue;
            if(pl.isUpdateXP) {
                try {
                    int oldXP = pl.us.getXP();
                    pl.us.updateXP(pl.XPUp, true);
                    int newXP = pl.us.getXP();
                    pl.AllXPUp += newXP-oldXP;
                } catch(IOException e) {
                    e.printStackTrace();
                }
                pl.isUpdateXP = false;
            }
        }
    }
    
    private void nextCUP() {
        for(int i = 0; i < ServerManager.maxPlayers; i++) {
            Player pl = this.players[i];
            if(pl == null)
                continue;
            if(pl.isUpdateCup) {
                try {
                    pl.us.updateDvong(pl.CupUp);
                } catch(IOException e) {
                    e.printStackTrace();
                }
                pl.isUpdateCup = false;
            }
        }
    }

    private void nextWind() throws IOException {
        Player pl = this.players[this.playerTurn];
        if(pl.ngungGioCount >= 0) {
            this.WindX = 0;
            this.WindY = 0;
            pl.ngungGioCount--;
        } else {
            if(Until.nextInt(0, 100) > 25) {
                this.WindX = Until.nextInt(-70, 70);
                this.WindY = Until.nextInt(-70, 70);
            }
        }
        Message ms = new Message(25); DataOutputStream ds = ms.writer();
        ds.writeByte(WindX);
        ds.writeByte(WindY);
        ds.flush();
        this.sendToTeam(ms);
    }

    private void huyVoHinh(int index) throws IOException {
        Message ms = new Message(80); DataOutputStream ds = ms.writer();
        ds.writeByte(index);
        ds.flush();
        this.sendToTeam(ms);
    }

    private void huyCantSee(int index) throws IOException {
        Message ms = new Message(106); DataOutputStream ds = ms.writer();
        ds.writeByte(1);
        ds.writeByte(index);
        ds.flush();
        this.sendToTeam(ms);
    }
/*
    private void huyCantMove(int index) throws IOException {
        Message ms = new Message(107); DataOutputStream ds = ms.writer();
        ds.writeByte(1);
        ds.writeByte(index);
        ds.flush();
        this.sendToTeam(ms);
    }
*/
    public void nextTurn() throws IOException {
        this.nTurn++;
        // Update XY Player
        for(int i = 0; i < this.allCount; i++) {
            Player pl = players[i];
            if(pl == null)
                continue;
            pl.chuanHoaXY();
        }
        if(this.ltap)
            this.playerTurn = 0;
        else {
            if(this.playerTurn == -1) {
                while(true) {
                    int next = Until.nextInt(ServerManager.maxPlayers);
                    if(this.players[next] != null) {
                        this.playerTurn = (byte)next;
                        break;
                    }
                }
                if(this.type == 5)
                    this.bossTurn = (byte)ServerManager.maxPlayers;
            } else {
                this.nextAngry();
                if(!isBossTurn) {
                    Player plTurn = null;
                    if(this.playerTurn >= 0 && this.playerTurn < this.players.length)
                        plTurn = this.players[this.playerTurn];
                    if(plTurn != null) {
                        this.players[this.playerTurn].isUsePow  = false;
                        this.players[this.playerTurn].isUseItem = false;
                        this.players[this.playerTurn].itemUsed  = -1;
                    }
                }
                if(this.countDownMNG != null)
                    this.countDownMNG.stopCount();
                if(this.type == 5) {
                    if(this.isBossTurn) {
                        this.isBossTurn = false;
                        int turn = this.playerTurn+1;
                        while(turn != this.playerTurn) {
                            if(turn == ServerManager.maxPlayers)
                                turn = 0;
                            if(this.players[turn] != null && !this.players[turn].isDie) {
                                this.playerTurn = (byte)turn;
                                break;
                            }
                            turn++;
                        }
                    } else {
                        this.isBossTurn = true;
                        byte turn = (byte)(this.bossTurn+1);
                        while(turn != this.bossTurn) {
                            if(turn == this.allCount)
                                turn = ServerManager.maxPlayers;
                            if(this.players[turn] != null && !this.players[turn].isDie) {
                                this.bossTurn = turn;
                                break;
                            }
                            turn++;
                        }
                    }
                } else {
                    byte turn = (byte)(this.playerTurn+1);
                    while(turn != this.playerTurn) {
                        if(turn == this.allCount)
                            turn = 0;
                        if(this.players[turn] != null && !this.players[turn].isDie) {
                            this.playerTurn = (byte)turn;
                            break;
                        }
                        turn++;
                    }
                }
            }
        }
        if(!isBossTurn) {
            Player pl = this.players[this.playerTurn];
            pl.buocDi = 0;
            if(pl.hutMauCount > 0)
                pl.hutMauCount--;
            if(pl.voHinhCount > 0) {
                pl.voHinhCount--;
                if(pl.voHinhCount == 0)
                    huyVoHinh(this.playerTurn);
            }
            if(pl.tangHinhCount > 0) {
                pl.tangHinhCount--;
                if(pl.tangHinhCount == 0)
                    huyVoHinh(this.playerTurn);
            }
            if(pl.cantSeeCount > 0) {
                pl.cantSeeCount--;
                if(pl.cantSeeCount == 0)
                    huyCantSee(this.playerTurn);
            }
            if(pl.cantMoveCount > 0) {
                pl.cantMoveCount--;
                if(pl.cantMoveCount == 0)
                    huyCantSee(this.playerTurn);
            }
            if(pl.isBiDoc) {
                pl.updateHP(-150);
                nextHP();
                if(pl.isDie) {
                    checkWin();
                    return;
                }
            }
            pl.updateAngry(10);
        } else {
            Player pl = this.players[this.bossTurn];
            pl.buocDi = 0;
        }
        if(this.bullMNG.hasVoiRong) {
            for(int i = 0; i < this.bullMNG.voiRongs.size(); i++) {
                BulletManager.VoiRong vr = this.bullMNG.voiRongs.get(i);
                vr.count--;
                if(vr.count < 0) {
                    this.bullMNG.voiRongs.remove(i);
                    i--;
                }
            }
            if(this.bullMNG.voiRongs.isEmpty())
                this.bullMNG.hasVoiRong = false;
        }
        for(int i = 0; i < this.bullMNG.boms.size(); i++) {
            BulletManager.BomHenGio bom = this.bullMNG.boms.get(i);
            bom.count--;
            if(bom.count == 1) {
                this.bullMNG.exploreBom(i);
                i--;
            }
        }
        Message ms = new Message(24); DataOutputStream ds = ms.writer();
        ds.writeByte(this.isBossTurn ? this.bossTurn : this.playerTurn);
        ds.flush();
        this.sendToTeam(ms);
        this.nextWind();
        if(this.ltap)
            return;
        this.countDownMNG.resetCount();
        if(this.isBossTurn) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    ((Boss)players[bossTurn]).turnAction();
                }
            }).start();
        }
    }
    
    private void checkWin() throws IOException {
        if(this.ltap)
            return;
        if(!isFight)
            return;
        // Next HP
        nextHP();
        nextCantSee();
        nextCantMove();
        nextBiDoc();
        if(FightManager.this.type == 5) {
            int nPlayerAlive = 0, nBossAlive = 0, i = 0;
            while(i < ServerManager.maxPlayers) {
                Player pl2 = players[i];
                if(pl2 != null && !pl2.isDie)
                    nPlayerAlive++;
                i++;
            }
            while(i < allCount) {
                Player pl2 = players[i];
                if(pl2 != null && !pl2.isDie)
                    nBossAlive++;
                i++;
            }
            if(nPlayerAlive == 0 || nBossAlive == 0) {
                if(nPlayerAlive == nBossAlive && nPlayerAlive == 0) {
                    if(isBossTurn)
                        fightComplete(-1);
                    else
                        fightComplete(1);
                } else if(nPlayerAlive == 0)
                    fightComplete(-1);
                else
                    fightComplete(1);
            } else {
                // Set next turn
                nextTurn();
            }
        } else {
            int nRedAlive = 0, nBlueAlive = 0;
            for(int i = 0; i < ServerManager.maxPlayers; i++) {
                Player pl2 = players[i];
                if(pl2 == null)
                    continue;
                if(!pl2.isDie) {
                    if(pl2.team)
                        nBlueAlive++;
                    else
                        nRedAlive++;
                }
            }
            if(nRedAlive == 0 || nBlueAlive == 0) {
                if((nRedAlive == nBlueAlive) && (nRedAlive == 0))
                    fightComplete(0);
                else if(nRedAlive == 0)
                    fightComplete(1);
                else
                    fightComplete(-1);
            } else {
                // Set next turn
                nextTurn();
            }
        }
    }

    private void fightComplete(int checkWin) throws IOException {
        this.isFight  = false;
        this.WindX    = 0;
        this.WindY    = 0;
        this.nHopQua  = 0;
        this.nTurn    = 0;
        this.bullMNG.hasVoiRong = false;
        this.bullMNG.voiRongs.clear();
        this.bullMNG.boms.clear();
        if(this.type == 5 && checkWin == 1) {
            for(int i = 0; i < ServerManager.maxPlayers; i++) {
                Player pl = this.players[i];
                if(pl != null)
                    pl.updateEXP(10);
            }
            this.nextXP();
        }
        Message ms; DataOutputStream ds;
        // Update Win
        for(int i = 0; i < ServerManager.maxPlayers; i++) {
            Player pl = this.players[i];
            if(pl == null || pl.us == null)
                continue;
            int win = pl.team ? checkWin:-checkWin;
            if(win == 1) {
                if(this.playerCount == 2)
                    pl.us.updateMission(0, 1);
                else if(this.playerCount >= 5)
                    pl.us.updateMission(17, 1);
                if(pl.idNV == 0)
                    pl.us.updateMission(13, 1);
                else if(pl.idNV == 1)
                    pl.us.updateMission(14, 1);
                else if(pl.idNV == 2)
                    pl.us.updateMission(15, 1);
                // UFO
                if(this.mapMNG.Id == 35)
                    pl.us.updateMission(2, 1);
                else if(this.mapMNG.Id == 36)
                    pl.us.updateMission(3, 1);
                else if(this.mapMNG.Id == 38 || this.mapMNG.Id == 39)
                    pl.us.updateMission(4, 1);
            }
            ms = new Message(50); ds = ms.writer();
            // Team win->0: hoa 1: win -1: thua
            ds.writeByte(win);
            // Null byte
            ds.writeByte(0);
            // money Bonus
            ds.writeInt(this.wait.money);
            ds.flush();
            pl.us.sendMessage(ms);
        }
        // Update All XP and CUP
        for(int i = 0; i < ServerManager.maxPlayers; i++) {
            Player pl = this.players[i];
            if(pl == null || pl.us == null)
                continue;
            ms = new Message(97); ds = ms.writer();
            ds.writeInt(pl.AllXPUp);
            ds.writeInt(pl.us.getXP());
            ds.writeInt(pl.us.getLevel()*(pl.us.getLevel()+1)*1000);
            ds.writeByte(0);
            ds.writeByte(pl.us.getLevelPercen());
            ds.flush();
            pl.us.sendMessage(ms);
        }
        for(int i = 0; i < ServerManager.maxPlayers; i++) {
            Player pl = this.players[i];
            if(pl == null || pl.us == null)
                continue;
            ms = new Message(-24); ds = ms.writer();
            ds.writeByte(pl.AllCupUp);
            ds.writeInt(pl.us.getDvong());
            ds.flush();
            pl.us.sendMessage(ms);
        }
        // Update Xu
        if(this.wait.money > 0) {
            for(int i = 0; i < ServerManager.maxPlayers; i++) {
                Player pl = this.players[i];
                if(pl == null || pl.us == null)
                    continue;
                int win = pl.team ? checkWin:-checkWin;
                if(win >= 0) {
                    pl.us.updateXu(this.wait.money*2);
                    ms = new Message(52); ds = ms.writer();
                    ds.writeInt(pl.us.getIDDB());
                    ds.writeInt(this.wait.money*2);
                    ds.writeInt(pl.us.getXu());
                    ds.flush();
                    sendToTeam(ms);
                }
            }
        }
        try {
            Thread.sleep(3000L);
        } catch(InterruptedException e) {
        }
        this.wait.fightComplete();
    }

    protected void startGame(int nTeamPointBlue, int nTeamPointRed) throws IOException {
        if(this.isFight)
            return;
        if(!this.ltap)
            this.setMap(this.wait.map);
        else
            this.mapMNG.setMapId(this.mapMNG.Id);
        this.playerTurn = -1;
        this.nTurn      = 0;
        this.WindX = 0;
        this.WindY = 0;
        this.isFight = true;
        if(this.ltap)
            this.playerCount = 1;
        else
            this.playerCount = this.wait.numPlayer;
        this.allCount = ServerManager.maxPlayers;
        if(this.ltap) {
            this.userLt.setFightManager(this);
            this.players      = new Player[ServerManager.maxPlayers];
            this.players[0]   = new Player(this, (byte)0, ServerManager.Xltap[0], ServerManager.Yltap[0], Player.getLuyenTapItem(), (byte)0, this.userLt);
            this.players[1]   = new Player(this, (byte)1, ServerManager.Xltap[1], ServerManager.Yltap[1], Player.getLuyenTapItem(), (byte)0, this.userLt);
            for(int i = 2; i < 8; i++)
                this.players[i] = null;
        } else {
            if(this.type == 5)
                this.nHopQua = (byte)(this.playerCount / 2);
            int[] location = new int[8];
            int count = 0;
            for(int i = 0; i < this.wait.maxPlayer; i++) {
                User us = this.wait.players[i];
                if(us == null) {
                    this.players[i] = null;
                    continue;
                }
                us.updateXu(-this.wait.money);
                us.setFightManager(this);
                short X, Y;
                int item[];
                int teamPoint;
                boolean exists;
                int locaCount = -1;
                do {
                    locaCount = Until.nextInt(this.mapMNG.XPlayerInit.length);
                    exists = false;
                    for(int j = 0; j < count; j++)
                        if(location[j] == locaCount) {
                            exists = true;
                            break;
                        }
                } while(exists);
                location[count++] = locaCount;
                X = this.mapMNG.XPlayerInit[locaCount];
                Y = this.mapMNG.YPlayerInit[locaCount];
                item = this.wait.item[i];
                for(int j = 0; j < 4; j++)
                    if(item[4+j] > 0)
                        us.updateItem((byte)(12+j), -1);
                if(this.type == 5 || i%2==0)
                    teamPoint = nTeamPointBlue;
                else
                    teamPoint = nTeamPointRed;
                this.players[i] = new Player(this, (byte)i, X, Y, item, teamPoint, us);
            }
        }
        this.bullMNG.mangNhenId = 200;
        this.sendFightInfoMessage();
        if(this.type == 5)
            nextBoss();
        this.nextTurn();
    }

    public void leave(User us) throws IOException {
        if(!this.isFight)
            return;
        int index = this.getIndexByIDDB(us.getIDDB());
        if(index == -1)
            return;
        Player pl = this.players[index];
        pl.HP    = 0;
        pl.isUpdateHP = true;
        pl.isDie = true;
        pl.us    = null;
        if(!this.ltap) {
            this.wait.leave(us);
            this.checkWin();
        }
        int upXP = us.getXP();
        us.updateXP(-5, false);
        upXP -= us.getXP();
        Message ms = new Message(9); DataOutputStream ds = ms.writer();
        ds.writeInt(us.getIDDB());
        ds.writeUTF(upXP > 0 ? String.format(GameString.leave1(), upXP) : GameString.leave2());
        ds.flush();
        sendToTeam(ms);
    }

    protected void sendFightInfoMessage() throws IOException {
        if(!this.isFight)
            return;
        // Update Xu
        if(!this.ltap && this.wait.money > 0) {
            for(int i = 0; i < ServerManager.maxPlayers; i++) {
                Player pl = this.players[i];
                if(pl == null || pl.us == null)
                    continue;
                Message ms = new Message(52); DataOutputStream ds = ms.writer();
                ds.writeInt(pl.us.getIDDB());
                ds.writeInt(-this.wait.money);
                ds.writeInt(pl.us.getXu());
                ds.flush();
                this.sendToTeam(ms);
            }
        }
        for(int i = 0; i < ServerManager.maxPlayers; i++) {
            Player pl = this.players[i];
            if(pl == null || pl.us == null)
                continue;
            Message ms = new Message(20); DataOutputStream ds = ms.writer();
            if(ltap) {
                short[] aw = this.userLt.getEquip();
                for(int j=0; j < 5; j++)
                    ds.writeShort(aw[j]);
            }
            // Null byte
            ds.writeByte(0);
            // Time Count
            if(ltap)
                ds.writeByte(0);
            else
                ds.writeByte(this.timeCountMax);
            // Team point
            ds.writeShort(pl.dongDoi);
            if(!this.ltap && this.wait.type == 7)
                ds.writeByte(8);
            // X, Y, HP
            for(int j = 0; j < this.players.length; j++) {
                Player pl2 = this.players[j];
                if(pl2 == null) {
                    ds.writeShort(-1);
                    continue;
                }
                ds.writeShort(pl2.X);
                ds.writeShort(pl2.Y);
                ds.writeShort(pl2.HPMax);
            }
            ds.flush();
            pl.us.sendMessage(ms);
        }
    }

    protected void countOut() throws IOException {
        if(this.isFight)
            nextTurn();
    }

    public void changeLocation(int index) throws IOException {
        Player pl = this.players[index];
        ServerManager.log("Player "+index+" change location X="+pl.X+" Y="+pl.Y);
        Message ms = new Message(21); DataOutputStream ds = ms.writer();
        ds.writeByte(index);
        ds.writeShort(pl.X);
        ds.writeShort(pl.Y);
        ds.flush();
        sendToTeam(ms);
        if(pl.Y > this.mapMNG.Height) {
            pl.isDie = true;
            pl.HP = 0;
            pl.isUpdateHP = true;
            checkWin();
        }
    }

    public void newShoot(int index, byte bullId, short arg, byte force, byte force2, byte nshoot, boolean ltap) throws IOException {
        ServerManager.log("New shoot index="+index+" bullId: "+bullId+" arg: "+arg+" force: "+force+" force2: "+force2+" nshoot: "+nshoot);
        final Player pl = this.players[index];
        short x = pl.X, y = pl.Y;
        if(!ltap)
            this.calcMM();
        bullMNG.addShoot(pl, bullId, arg, force, force2, nshoot);
        bullMNG.fillXY();
        if(!this.ltap)
            this.nextMM();
        ArrayList<Bullet> bullets = bullMNG.entrys;
        if(bullets.isEmpty())
            return;
        bullId = bullMNG.entrys.get(0).bullId;
        Message ms = new Message(ltap?84:22); DataOutputStream ds = ms.writer();
        // typeshoot
        byte typeshoot = 0;
        // Type shoot 0: pem buoc nhay 1: pem tang dan
        ds.writeByte(typeshoot);
        // Ban pow
        ds.writeByte(pl.isUsePow?1:0);
        // id trong phong
        ds.writeByte(index);
        // id dan
        ds.writeByte(bullId);
        // x, y, goc
        ds.writeShort(x);
        ds.writeShort(y);
        ds.writeShort(arg);
        // Apa or chicky: send force 2
        if(bullId == 17 || bullId == 19)
            ds.writeByte(bullMNG.force2);
        // dan laser
        if(bullId == 14 || bullId == 40) {
            // Goc
            ds.writeByte(0);
            // Null byte
            ds.writeByte(0);
        }
        // Send goc
        if(bullId == 44 || bullId == 45 || bullId == 47)
            ds.writeByte(0);
        // So lan ban
        ds.writeByte(nshoot);
        // So dan
        ds.writeByte(bullets.size());

        for(Bullet bull : bullets) {
            if(bullMNG.typeSC > 0)
                pl.us.updateMission(12, 1);
            ArrayList<Short> X = bull.XArray;
            ArrayList<Short> Y = bull.YArray;

            // Length
            ds.writeShort(X.size());

            if(typeshoot == 0) {
                for(int j = 0; j < X.size(); j++) {
                    if(j == 0) {
                        // Toa do x, y dau
                        ds.writeShort(X.get(0));
                        ds.writeShort(Y.get(0));
                    } else {
                        if((j == X.size() - 1) && bullId == 49) {
                            ds.writeShort(X.get(j));
                            ds.writeShort(Y.get(j));
                            ds.writeByte(bullMNG.mgtAddX);
                            ds.writeByte(bullMNG.mgtAddY);
                            break;
                        }
                        // Buoc nhay
                        ds.writeByte((byte)(X.get(j)-X.get(j-1)));
                        ds.writeByte((byte)(Y.get(j)-Y.get(j-1)));
                    }
                }
            } else if(typeshoot == 1) {
                for(int j = 0; j < X.size(); j++) {
                    // Toa do x, y thu j
                    ds.writeShort(X.get(j));
                    ds.writeShort(Y.get(j));
                }
            }
            if(bullId == 48) {
                // Lent
                ds.writeByte(1);
                for(int j=0; j<1; j++) {
                    // xHit, yHit
                    ds.writeShort(0);
                    ds.writeShort(0);
                }
            }
        }

        // Type Sieu cao
        ds.writeByte(bullMNG.typeSC);
        if(bullMNG.typeSC == 1 || bullMNG.typeSC ==2) {
            // X, Y super
            ds.writeShort(bullMNG.XSC);
            ds.writeShort(bullMNG.YSC);
        }
        ds.flush();
        bullMNG.reset();
        this.sendToTeam(ms);
        pl.isUseItem = false;
        pl.itemUsed = -1;
        if(this.ltap)
            nextTurn();
        else {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if(pl != null && !(pl instanceof Boss))
                            pl.netWait();
                        checkWin();
                    } catch(Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }

    public void chatMessage(User us, Message ms) throws IOException {
        int index = this.getIndexByIDDB(us.getIDDB());
        if(index == -1)
            return;
        String s = ms.reader().readUTF();
        ms = new Message(9); DataOutputStream ds = ms.writer();
        ds.writeInt(us.getIDDB());
        ds.writeUTF(s);
        ds.flush();
        this.sendToTeam(ms);
    }

    public void luyenTapMessage(User us) throws IOException {
        Message ms = new Message(-6); DataOutputStream ds = ms.writer();
        ds.writeByte(this.mapMNG.Id);
        ds.flush();
        us.sendMessage(ms);
    }

    public void startLuyenTapMessage(User us, Message ms) throws IOException {
        byte typeS = ms.reader().readByte();
        // 0: start game, 1: roi ltap
        if(typeS == 0) {
            if(us.getState() == User.State.Waiting)
                this.startGame((byte)0, (byte)0);
        } else if(typeS == 1) {
            if(us.getState() == User.State.Fighting) {
                this.isFight = false;
                ServerManager.enterWait(userLt);
                ms = new Message(83);
                userLt.sendMessage(ms);
            }
        }
    }

    public void changeLocationMessage(User us, Message ms) throws IOException {
        int index = this.getIndexByIDDB(us.getIDDB());
        if(index == -1)
            return;
        Player pl = this.players[index];
        short x = ms.reader().readShort();
        short y = ms.reader().readShort();
        pl.updateXY(x, y);
        changeLocation(index);
    }

    public void shootMessage(User us, Message ms) throws IOException {
        int index = this.getIndexByIDDB(us.getIDDB());
        if(index == -1 || index != this.playerTurn)
            return;
        Player pl = this.players[index];
        DataInputStream dis = ms.reader();
        // id dan
        byte bullId = dis.readByte();
        short x = dis.readShort();
        short y = dis.readShort();
        short arg = dis.readShort();
        // 2 luc
        byte force = dis.readByte();
        byte force2 = 0;
        // Neu la apa or chicky -> 2 luc
        if(bullId == 17 || bullId == 19)
            force2 = dis.readByte();
        // so lan ban
        byte nshoot = dis.readByte();
        if(this.ltap)
            pl.setXY(x, y);
        else if(x != pl.X && y != pl.Y)
            pl.updateXY(x, y);
        this.newShoot(index, bullId, arg, force, force2, nshoot, ltap);
    }

    public void boLuotMessage(User us) throws IOException {
        int index = this.getIndexByIDDB(us.getIDDB());
        if(index == -1 || index != this.playerTurn)
            return;
        // Set next turn
        nextTurn();
    }

    public void useItemMessage(User us, Message ms) throws IOException {
        int index = this.getIndexByIDDB(us.getIDDB());
        if(index == -1 || index != this.playerTurn)
            return;
        byte idItem = ms.reader().readByte();
        if(idItem < 0 || idItem > ItemData.nItemDcMang.length-1 && idItem != 100)
            return;
        Player pl = this.players[index];
        if(pl.isUseItem)
            return;
        int indexItem = -1;
        if(idItem != 100) {
            for(int i = 0; i < pl.item.length; i++) {
                if(pl.item[i] == idItem)
                    indexItem = i;
            }
            if(indexItem == -1)
                return;
        }
        ms = new Message(26); DataOutputStream ds = ms.writer();
        ds.writeByte(index);
        ds.writeByte(idItem);
        ds.flush();
        this.sendToTeam(ms);
        pl.isUseItem = true;
        pl.itemUsed = idItem;
        if(indexItem >= 0) {
            pl.us.updateItem(idItem, -1);
            pl.item[indexItem] = -1;
        }
        // HP
        if(idItem == 0) {
            pl.updateHP(350);
            this.nextHP();
        }
        // Di X2
        if(idItem == 3)
            pl.diX2 = true;
        // Tang hinh
        if(idItem == 4)
            pl.tangHinhCount = 5;
        // Ngung gio
        if(idItem == 5) {
            pl.ngungGioCount = 5;
            this.nextWind();
        }
        // HP dong doi
        if(idItem == 10) {
            int i = pl.team ? 0:1;
            for(; i < this.players.length; i+= 2) {
                Player pl2 = this.players[i];
                if(pl2 == null || pl2.isDie)
                    continue;
                pl2.updateHP(300);
            }
            this.nextHP();
        }
        // Tu sat
        if(idItem == 24)
            newShoot(index, (byte)50, (short)0, (byte)0, (byte)0, (byte)1, this.ltap);
        if(idItem == 27)
            newShoot(index, (byte)53, (short)0, (byte)0, (byte)0, (byte)1, this.ltap);
        // HP 50%
        if(idItem == 32) {
            pl.updateHP(pl.HPMax/2);
            this.nextHP();
        }
        // HP 100%
        if(idItem == 33) {
            pl.updateHP(pl.HPMax);
            this.nextHP();
        }
        // Vo hinh
        if(idItem == 34)
            pl.voHinhCount = 3;
        // Ma ca rong
        if(idItem == 35)
            pl.hutMauCount = 3;
        // Pow
        if(idItem == 100)
            if(pl.angry == 100) {
                pl.updateAngry(-100);
                pl.isUsePow = true;
            }
        if(idItem == 0 || idItem == 3 || idItem == 4 || idItem == 5 || idItem == 10 || idItem == 32 || idItem == 33 || idItem == 34 || idItem == 35 || idItem == 100)
            pl.itemUsed = -1;
    }

    public void removeBullMessage(User us, Message ms) throws IOException {
        int[] X, Y;
        int lent = ms.reader().readByte();
        X = new int[lent]; Y = new int[lent];
        for(int i = 0; i < lent; i++) {
            X[i] = ms.reader().readInt();
            Y[i] = ms.reader().readInt();
        }
    }
    
    public void addBom(int id, int X, int Y) throws IOException {
        Message ms = new Message(109); DataOutputStream ds = ms.writer();
        ds.writeByte(0);
        ds.writeByte(id);
        ds.writeInt(X);
        ds.writeInt(Y);
        ds.flush();
        this.sendToTeam(ms);
    }

    public void exploreBom(int id, int X, int Y, Bullet bull) throws IOException {
        this.mapMNG.collision((short)X, (short)Y, bull);
        Message ms = new Message(109); DataOutputStream ds = ms.writer();
        ds.writeByte(1);
        ds.writeByte(id);
        ds.flush();
        this.sendToTeam(ms);
    }
    
    public void updateCantSee(Player pl) throws IOException {
        pl.cantSeeCount = 5;
    }
    
    public void updateCantMove(Player pl) throws IOException {
        pl.cantMoveCount = 5;
    }
    
    public void updateBiDoc(Player pl) throws IOException {
        pl.isBiDoc = true;
    }

}
