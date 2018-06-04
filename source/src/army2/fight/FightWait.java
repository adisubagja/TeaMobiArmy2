package army2.fight;

import army2.server.ClientEntry;
import army2.server.ItemData;
import army2.server.MapData;
import army2.server.Room;
import army2.server.ServerManager;
import army2.server.Until;
import army2.server.User;
import army2.server.GameString;
import network.Message;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 *
 * @author ASD
 */
public class FightWait {

    public final User[] players;
    public FightManager fight;
    private final Room parent;
    public final byte id;
    public final boolean[] readys;
    public final int[][]  item;
    public boolean started;
    public int numReady;
    public int maxSetPlayer;
    public int maxPlayerInit;
    public int maxPlayer;
    public int numPlayer;
    public boolean passSet;
    public String pass;
    public int money;
    public String name;
    public byte type;
    public byte teaFree;
    public byte map;
    public int boss;
    private Thread kickBoss;

    public FightWait(Room parent, byte type, byte id, byte maxPlayers, byte maxPlayerInit, byte map, byte teaFree) {
        this.parent = parent;
        this.id = id;
        this.maxPlayer     = maxPlayers;
        this.maxPlayerInit = maxPlayerInit;
        this.maxSetPlayer  = maxPlayerInit;
        this.numPlayer  = 0;
        this.numReady   = 0;
        this.players    = new User[maxPlayers];
        this.readys     = new boolean[maxPlayers];
        this.item       = new int[maxPlayers][8];
        this.type       = type;
        this.teaFree    = teaFree;
        this.money      = this.parent.minXu;
        this.name       = "";
        this.pass       = "";
        this.map        = map;
        this.fight      = new FightManager(this);
        this.started    = false;
        this.boss       = -1;
    }

    private void refreshFightWait() {
        this.maxSetPlayer = maxPlayerInit;
        this.numPlayer = 0;
        this.money = this.parent.minXu;
        this.name = "";
        this.pass = "";
        this.started = false;
        this.boss = -1;
    }

    public int getIndexByIDDB(int iddb) {
        for(int i = 0; i < this.players.length; i++) {
            User pl = this.players[i];
            if(pl == null)
                continue;
            if(pl.getIDDB() == iddb)
                return i;
        }
        return -1;
    }

    private void changeBoss(final int index) {
        this.boss = index;
        if(this.kickBoss != null)
            this.kickBoss.interrupt();
        this.kickBoss = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Sleep 5 min
                    Thread.sleep(300000L);
                    kick(index);
                } catch(InterruptedException|IOException e) {
                }
            }
        });
    }

    public void enterFireOval(User us) throws IOException {
        Message ms; DataOutputStream ds;
        if(this.money > us.getXu()) {
            ms = new Message(45); ds = ms.writer();
            ds.writeUTF(GameString.joinKVError2());
            ds.flush();
            us.sendMessage(ms);
            return;
        }
        if(this.numPlayer >= this.maxSetPlayer) {
            ms = new Message(45); ds = ms.writer();
            ds.writeUTF(GameString.joinKVError3());
            ds.flush();
            us.sendMessage(ms);
            return;
        }
        synchronized(this.players) {
            int bestLocation = -1;
            for(int i = 0; i < this.players.length; i++)
                if(this.players[i] == null) {
                    bestLocation = i;
                    break;
                }
            if(bestLocation == -1)
                return;
            us.setFightWait(this);
            ServerManager.removeWait(us);
            if(this.numPlayer == 0)
                this.changeBoss(bestLocation);
            // Send to team message 12
            ms = new Message(12); ds = ms.writer();
            // Location
            ds.writeByte(bestLocation);
            // iddb
            ds.writeInt(us.getIDDB());
            // clanId
            ds.writeShort(us.getClan());
            // name
            ds.writeUTF(us.getUserName());
            // level
            ds.writeByte(us.getLevel());
            // nv
            ds.writeByte(us.getNVUse());
            // equip
            for(int i = 0; i < 5; i++)
                ds.writeShort(us.getEquip()[i]);
            ds.flush();
            this.sendToTeam(ms);

            this.players[bestLocation] = us;
            this.readys[bestLocation] = false;
            numPlayer++;

            // Send mss 8
            ms = new Message(8); ds = ms.writer();
            // Chu phong
            ds.writeInt(this.players[this.boss].getIDDB());
            // Tien
            ds.writeInt(this.money);
            // 2 null byte
            ds.writeByte(0);
            ds.writeByte(0);
            for(int i=0; i<this.players.length; i++) {
                if(this.players[i]!=null) {
                    User us0 = players[i];

                    // IDDB
                    ds.writeInt(us0.getIDDB());
                    // Clan id
                        ds.writeShort(us0.getClan());
                    // Ten 
                    ds.writeUTF(us0.getUserName());
                    // 
                    ds.writeInt(0);
                    // 
                    ds.writeByte(i);
                    // Nhan vat
                    ds.writeByte(us0.getNVUse());
                    // Nhan vat data
                    for(int k = 0; k < 5; k++)
                        ds.writeShort(us0.getEquip()[k]);
                    // San sang
                    ds.writeBoolean(this.readys[i]);
                } else
                    ds.writeInt(-1);
            }
            ds.flush();
            us.sendMessage(ms);

            // Update khu vuc
            ms = new Message(76); ds = ms.writer();
            ds.writeByte(this.parent.id);
            ds.writeByte(this.id);
            ds.writeUTF(this.name);
            // Kieu ban do
            ds.writeByte(this.parent.type);
            ds.flush();
            us.sendMessage(ms);

            // Send map
            ms = new Message(75); ds = ms.writer();
            ds.writeByte(this.map);
            ds.flush();
            us.sendMessage(ms);
        }
    }

    private void kick(int index) throws IOException {
        Message ms; DataOutputStream ds;
        ms = new Message(11); ds = ms.writer();
        ds.writeShort(index);
        ds.writeInt(this.players[index].getIDDB());
        ds.writeUTF(GameString.kickString());
        ds.flush();
        sendToTeam(ms);
        leave(this.players[index]);
    }

    public void kickMessage(User us, Message ms) throws IOException {
        if(this.players[this.boss].getIDDB() != us.getIDDB())
            return;
        int iddb  = ms.reader().readInt();
        int index = getIndexByIDDB(iddb);
        if(index == -1)
            return;
        if(this.readys[index])
            return;
        kick(index);
    }

    public void leave(User us) throws IOException {
        this.leaveBoard(us);
        if(this.numPlayer == 0)
            return;
        Message ms = new Message(14); DataOutputStream ds = ms.writer();
        ds.writeInt(us.getIDDB());
        ds.writeInt(this.players[this.boss].getIDDB());
        ds.flush();
        this.sendToTeam(ms);
    }

    public void leaveBoard(User us) throws IOException {
        synchronized(this.players) {
            int max=ServerManager.maxPlayers, i;
            for(i=0; i<max; i++)
                if(this.players[i] != null && this.players[i].getIDDB() == us.getIDDB()) {
                    ServerManager.enterWait(this.players[i]);
                    this.players[i] = null;
                    if(this.boss == i) {
                        for(i=0; i<max; i++) {
                            if(this.players[i]!=null) {
                                this.changeBoss(i);
                                break;
                            }
                        }
                    }
                    this.numPlayer--;
                    if(this.numPlayer == 0)
                        refreshFightWait();
                    break;
                }
        }
    }

    public void fightComplete() throws IOException {
        // Chien xong, refresh fight wait
        Message ms; DataOutputStream ds;
        for(int i = 0; i < this.players.length; i++) {
            User us = this.players[i];
            if(us == null)
                continue;
            ms = new Message(112); ds = ms.writer();
            for(int j = 0; j < 4; j++)
                ds.writeByte(us.getItemNum(12+j));
            ds.flush();
            us.sendMessage(ms);
            us.setFightWait(this);
        }
        // Send map
        ms = new Message(75); ds = ms.writer();
        ds.writeByte(this.map);
        ds.flush();
        this.sendToTeam(ms);
    }

    public void sendToTeam(Message ms) throws IOException {
        for(User pl : players) {
            if(pl != null)
                pl.sendMessage(ms);
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

    public void setPassMessage(User us, Message ms) throws IOException {
        if(this.players[this.boss].getIDDB() != us.getIDDB())
            return;
        String matkhau = ms.reader().readUTF();
        if(matkhau == null || matkhau.equals("")) {
            this.passSet = false;
            this.pass = "";
        } else {
            this.passSet = true;
            this.pass = matkhau;
        }
    }
    
    public void setMoneyMessage(User us, Message ms) throws IOException {
        if(this.players[this.boss].getIDDB() != us.getIDDB())
            return;
        int xu = ms.reader().readInt();
        if(xu < this.parent.minXu || xu > this.parent.maxXu) {
            ms = new Message(45); DataOutputStream ds = ms.writer();
            ds.writeUTF(String.format(GameString.datCuocError1(), this.parent.minXu, this.parent.maxXu));
            ds.flush();
            us.sendMessage(ms);
            return;
        }
        this.money = xu;
        ms = new Message(19); DataOutputStream ds = ms.writer();
        ds.writeShort(0);
        ds.writeInt(xu);
        ds.flush();
        this.sendToTeam(ms);
    }
    
    public void startMessage(ClientEntry cl) throws IOException {
        Message ms; DataOutputStream ds;
        if(this.players[this.boss].getIDDB() != cl.user.getIDDB())
            return;
        // Kiem tra neu ko ai ss
        if(this.numReady == 0) {
            ms = new Message(45); ds = ms.writer();
            ds.writeUTF(GameString.startGameError1());
            ds.flush();
            cl.sendMessage(ms);
            return;
        }
        // kiem tra san sang va item
        int nRed = 0, nBlue = 0, nTeamPointRed = 0, nTeamPointBlue = 0;
        int[][] itemMap = new int[this.players.length][];
        for(int i = 0; i < this.players.length; i++) {
            User pl = this.players[i];
            if(pl == null)
                continue;
            if(this.type == 5) {
                nBlue++;
                nTeamPointBlue += pl.getAbility()[4];
            } else {
                if(i%2 == 0) {
                    nBlue++;
                    nTeamPointBlue += pl.getAbility()[4];
                } else {
                    nRed++;
                    nTeamPointRed += pl.getAbility()[4];
                }
            }
            if(this.boss != i && !readys[i]) {
                ms = new Message(45); ds = ms.writer();
                ds.writeUTF(String.format(GameString.startGameError2(), pl.getUserName()));
                ds.flush();
                cl.sendMessage(ms);
                return;
            }
            int lent = ItemData.entrys.size();
            itemMap[i] = new int[lent];
            int j;
            for(j = 0; j < lent; j++)
                itemMap[i][j] = 0;
            for(j = 0; j < 8; j++) {
                int itemSelect = this.item[i][j];
                if(itemSelect < 0 || itemSelect > lent)
                    continue;
                itemMap[i][itemSelect]++;
                if(j > 4)
                    itemMap[i][12+j-4]++;
                if((itemMap[i][itemSelect] > ItemData.nItemDcMang[itemSelect]) ||
                   (itemMap[i][itemSelect] > pl.getItemNum(itemSelect)) ||
                   (j > 4 && pl.getItemNum(12+j-4) == 0)) {
                    ms = new Message(45); ds = ms.writer();
                    ds.writeUTF(String.format(GameString.startGameError3(), pl.getUserName(), j));
                    ds.flush();
                    this.sendToTeam(ms);
                    return;
                }
            }
        }
        if(this.type != 5 && nBlue != nRed) {
            ms = new Message(45); ds = ms.writer();
            ds.writeUTF(GameString.startGameError4());
            ds.flush();
            cl.sendMessage(ms);
            return;
        }
        this.fight.startGame(Until.getTeamPoint(nTeamPointBlue, nBlue), Until.getTeamPoint(nTeamPointRed, nRed));
    }
    
    public void setNameMessage(User us, Message ms) throws IOException {
        if(this.players[this.boss].getIDDB() != us.getIDDB())
            return;
        String nameS = ms.reader().readUTF();
        if(nameS == null || nameS.equals("")) {
            this.name = "";
        } else
            this.name = nameS;
    }
    
    public void setMaxPlayerMessage(User us, Message ms) throws IOException {
        if(this.players[this.boss].getIDDB() != us.getIDDB())
            return;
        byte numPL = ms.reader().readByte();
        if((numPL>0)&&(numPL<9)&&(numPL%2==0)&&(numPlayer<numPL))
            this.maxSetPlayer = numPL;
    }
    
    public void doiPheMessage(User us, Message ms) throws IOException {
        int i, j=0;
        synchronized(this.players) {
            for(i=this.players.length-1; i>=0; i--) {
                if((this.players[i] != null) && (this.players[i].getIDDB() == us.getIDDB())) {
                    if(i%2==0) j=1; else j=0;
                    for(; j<this.players.length; j+=2) {
                        if(this.players[j]==null) {
                            this.players[j] = us;
                            this.players[i] = null;
                            break;
                        }
                    }
                    break;
                }
            }
        }
        if(i>=0) {
            ms = new Message(71); DataOutputStream ds = ms.writer();
            ds.writeInt(us.getIDDB());
            ds.writeByte(j);
            ds.flush();
            this.sendToTeam(ms);
        }
    }

    public void setMapMessage(User us, Message ms) throws IOException {
        if(this.players[this.boss].getIDDB() != us.getIDDB())
            return;
        byte map = ms.reader().readByte();
        DataOutputStream ds;
        if(this.map < this.parent.minMap || this.map > this.parent.maxMap) {
            ms = new Message(45); ds = ms.writer();
            if(this.parent.maxMap == this.parent.minMap)
                ds.writeUTF(String.format(GameString.selectMapError1_1(), MapData.entrys.get(this.parent.minMap).name));
            else if(this.parent.maxMap == this.parent.minMap+1)
                ds.writeUTF(String.format(GameString.selectMapError1_2(), MapData.entrys.get(this.parent.minMap).name, MapData.entrys.get(this.parent.maxMap).name));
            else
                ds.writeUTF(GameString.selectMapError1_3());
            ds.flush();
            us.sendMessage(ms);
            return;
        }
        this.map = map;
        if(this.map == 27) {
            int numbMap = MapData.entrys.size() - ServerManager.numMapBoss;
            while(this.map == 27)
                this.map = (byte)Until.nextInt(numbMap);
        }
        ms = new Message(75); ds = ms.writer();
        ds.writeByte(map);
        ds.flush();
        this.sendToTeam(ms);
    }

    public void findPlayerMessage(User us, Message ms) throws IOException {
        boolean find = ms.reader().readBoolean();
        int iđdb = ms.reader().readInt();
        DataOutputStream ds;
        if(find) {
            if(this.players[this.boss].getIDDB() != us.getIDDB())
                return;
            User[] uss = ServerManager.findWaitPlayers(us);
            ms = new Message(78); ds = ms.writer();
            ds.writeBoolean(true);
            ds.writeByte(uss.length);
            for(int i = 0; i < uss.length; i++) {
                User us0 = uss[i];
                ds.writeUTF(us0.getUserName());
                ds.writeInt(us0.getIDDB());
                ds.writeByte(us0.getNVUse());
                ds.writeInt(us0.getXu());
                ds.writeByte(us0.getLevel());
                ds.writeByte(us0.getLevelPercen());

                for(int j=0; j<5; j++)
                    ds.writeShort(us0.getEquip()[j]);
            }
            ds.flush();
            us.sendMessage(ms);
        } else {
            User us1 = ServerManager.getUser(iđdb);
            if(us1 == null) {
                ms = new Message(45); ds = ms.writer();
                ds.writeUTF(GameString.inviteError1());
                ds.flush();
                us.sendMessage(ms);
                return;
            }
            if(us1.getState() != User.State.Waiting) {
                ms = new Message(45); ds = ms.writer();
                ds.writeUTF(GameString.inviteError2());
                ds.flush();
                us.sendMessage(ms);
                return;
            }
            ms = new Message(78); ds = ms.writer();
            ds.writeBoolean(false);
            ds.writeUTF(String.format(GameString.inviteMessage(), us.getUserName()));
            ds.writeByte(this.parent.id);
            ds.writeByte(this.id);
            ds.writeUTF(this.pass);
            ds.flush();
            us1.sendMessage(ms);
        }
    }
    
    public void readyMessage(User us, Message ms) throws IOException {
        boolean ready = ms.reader().readBoolean();
        if(this.players[this.boss].getIDDB() == us.getIDDB())
            return;
        int index = this.getIndexByIDDB(us.getIDDB());
        if(index == -1)
            return;
        if(this.readys[index] != ready) {
            this.readys[index] = ready;
            if(ready)
                this.numReady++;
            else
                this.numReady--;
        }
        ms = new Message(16); DataOutputStream ds = ms.writer();
        ds.writeInt(us.getIDDB());
        ds.writeBoolean(ready);
        ds.flush();
        this.sendToTeam(ms);
    }
    
    public void setItemMessage(User us, Message ms) throws IOException {
        synchronized(this.players) {
            int index = -1;
            for(int i=0; i<this.players.length; i++) {
                if((this.players[i] != null) && (this.players[i].getIDDB() == us.getIDDB())) {
                    index = i;
                    break;
                }
            }
            if(index == -1)
                return;
            for(int i = 0; i < 8; i++)
                this.item[index][i] = ms.reader().readByte();
        }
    }

}

