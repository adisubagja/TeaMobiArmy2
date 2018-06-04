/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package army2.fight.bullet;

import army2.fight.Bullet;
import army2.fight.BulletManager;
import army2.fight.MapEntry;
import army2.fight.Player;

/**
 *
 * @author ASD
 */
public class ItemToNhen extends Bullet {

    public ItemToNhen(BulletManager bullMNG, byte bullId, int satThuong, Player pl, int X, int Y, int vx, int vy, int msg, int g100) {
        super(bullMNG, bullId, satThuong, pl, X, Y, vx, vy, msg, g100);
    }
    
    @Override
    public void nextXY() {
        super.nextXY();
        if(super.collect)
            this.fm.mapMNG.addEntry(new MapEntry(this.bullMNG.mangNhenId++, (short)(X - 21), (short)(Y - 20), MapEntry.mangNhenData, (short)MapEntry.mangNhenW, (short)MapEntry.mangNhenH, true));
    }

}
