/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package army2.fight.bullet;

import army2.fight.Bullet;
import army2.fight.BulletManager;
import army2.fight.Player;

/**
 *
 * @author ASD
 */
public class ItemLaserDelay extends Bullet {
    
    public ItemLaserDelay(BulletManager bullMNG, byte id, int satThuong, Player pl, int X, int Y) {
        super(bullMNG, id, satThuong, pl, X, Y, 0, 0, 0, 0);
    }
    
    @Override
    public void nextXY() {
        super.frame++;
        if(super.frame == 25) {
            super.collect = true;
            this.fm.mapMNG.collision(this.X, this.Y, this);
        }
    }
    
}
