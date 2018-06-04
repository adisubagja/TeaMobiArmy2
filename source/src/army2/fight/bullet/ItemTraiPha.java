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
public class ItemTraiPha extends Bullet {
    
    public ItemTraiPha(BulletManager bullMNG, byte bullId, int satThuong, Player pl, int X, int Y, int vx, int vy, int msg, int g100) {
        super(bullMNG, bullId, satThuong, pl, X, Y, vx, vy, msg, g100);
        super.isCanCollision = false;
    }
    
    @Override
    public void nextXY() {
        super.nextXY();
        if(super.collect) {
            this.bullMNG.addBullet(new Bullet(bullMNG, (byte)12, this.satThuong, pl, this.X-8,  this.Y-493, -1, 2, 0, 100));
            this.bullMNG.addBullet(new Bullet(bullMNG, (byte)12, this.satThuong, pl, this.X+12, this.Y-496, 0, 1, 0, 100));
            this.bullMNG.addBullet(new Bullet(bullMNG, (byte)12, this.satThuong, pl, this.X-19, this.Y-505, -2, 1, 0, 100));
            this.bullMNG.addBullet(new Bullet(bullMNG, (byte)12, this.satThuong, pl, this.X+18, this.Y-505, 1, 1, 0, 100));
            this.bullMNG.addBullet(new Bullet(bullMNG, (byte)12, this.satThuong, pl, this.X+20, this.Y-512, 2, 0, 0, 100));
            this.bullMNG.addBullet(new Bullet(bullMNG, (byte)12, this.satThuong, pl, this.X-20, this.Y-512, -3, 0, 0, 100));
        }
    }

}
