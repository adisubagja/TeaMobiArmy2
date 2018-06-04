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
public class ItemLaser extends Bullet {

    public ItemLaser(BulletManager bullMNG, byte bullId, int satThuong, Player pl, int X, int Y, int vx, int vy, int msg, int g100) {
        super(bullMNG, bullId, satThuong, pl, X, Y, vx, vy, msg, g100);
        super.isCanCollision = false;
    }
    
    @Override
    public void nextXY() {
        super.nextXY();
        if(super.collect)
            this.bullMNG.addBullet(new ItemLaserDelay(bullMNG, (byte)15, this.satThuong, super.pl, this.X, this.Y));
    }

}
