/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package army2.fight.bullet;

import army2.fight.Bullet;
import army2.fight.BulletManager;
import army2.fight.Player;
import army2.server.ServerManager;
import java.io.IOException;

/**
 *
 * @author ASD
 */
public class ItemDongBang extends Bullet {
    
    public ItemDongBang(BulletManager bullMNG, byte bullId, int satThuong, Player pl, int X, int Y, int vx, int vy, int msg, int g100) {
        super(bullMNG, bullId, satThuong, pl, X, Y, vx, vy, msg, g100);
    }
    
    @Override
    public void nextXY() {
        super.nextXY();
        if(super.collect) {
            for(int i = 0; i < ServerManager.maxPlayers; i++) {
                Player pl = fm.players[i];
                if(pl != null) {
                    int tamAH = Bullet.getTamAHByBullID(bullId);
                    int kcX = Math.abs(pl.X-X);
                    int kcY = Math.abs(pl.Y-pl.height/2-Y);
                    int kc  = (int)Math.sqrt(kcX*kcX+kcY*kcY);
                    if(!pl.isDie && pl.voHinhCount <= 0 && kc <= tamAH+pl.width/2) 
                        try {
                            this.fm.updateCantMove(pl);
                        } catch(IOException e) {
                            e.printStackTrace();
                        }
                }
            }
        }
    }

}
