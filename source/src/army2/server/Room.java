package army2.server;

import army2.fight.FightWait;

/**
 *
 *
 * @author ASD
 */
public class Room {

    public byte id;
    public byte type;
    public String name;
    public int maxXu;
    public int minXu;
    public int minMap;
    public int maxMap;
    public final FightWait[] entrys;

    public Room(int id, int type, int maxEntrys) {
        this.id = (byte)id;
        this.type = (byte)type;
        byte maxPlayerInit = 0, map = 0;
        switch(type) {
            case 0:
                this.minXu = 0;
                this.maxXu = 1000;
                this.minMap = 0;
                this.maxMap = 29;
                maxPlayerInit = ServerManager.nPlayersInitRoom;
                map = ServerManager.initMap;
                break;
                
            case 1:
                this.minXu = 1000;
                this.maxXu = 10000;
                this.minMap = 0;
                this.maxMap = 29;
                maxPlayerInit = ServerManager.nPlayersInitRoom;
                map = ServerManager.initMap;
                break;

            case 2:
                this.minXu = 10000;
                this.maxXu = 100000;
                this.minMap = 0;
                this.maxMap = 29;
                maxPlayerInit = ServerManager.nPlayersInitRoom;
                map = ServerManager.initMap;
                break;

            case 3:
                this.minXu = 100000;
                this.maxXu = 1000000;
                this.minMap = 0;
                this.maxMap = 29;
                maxPlayerInit = ServerManager.nPlayersInitRoom;
                map = ServerManager.initMap;
                break;

            case 4:
                this.minXu = 10000;
                this.maxXu = 10000;
                this.minMap = 0;
                this.maxMap = 29;
                maxPlayerInit = ServerManager.nPlayersInitRoom;
                map = ServerManager.initMap;
                break;

            case 5:
                this.minXu = 100;
                this.maxXu = 100;
                this.minMap = 30;
                this.maxMap = 39;
                maxPlayerInit = ServerManager.maxPlayers;
                map = ServerManager.initMapBoss;
                break;

            case 6:
                this.minXu = 0;
                this.maxXu = Integer.MAX_VALUE;
                this.minMap = 0;
                this.maxMap = 29;
                maxPlayerInit = ServerManager.nPlayersInitRoom;
                map = ServerManager.initMap;
                break;
        }
        this.entrys = new FightWait[maxEntrys];
        for(int i=0; i<maxEntrys; i++)
            this.entrys[i] = new FightWait(this, this.type, (byte)i, ServerManager.maxPlayers, maxPlayerInit, map, (byte)Until.nextInt(0, 2));
    }

    protected int getFully() {
        int maxPlayers = 0;
        int player = 0;
        synchronized(entrys) {
            for(FightWait fw : entrys) {
                maxPlayers += fw.maxSetPlayer;
                player += fw.numPlayer;
            }
        }
        int perCent = player*100/maxPlayers;
        return (perCent < 50) ? 2 : ((perCent < 75) ? 1:0);
    }

}
