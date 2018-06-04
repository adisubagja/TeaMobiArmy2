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
public class ItemMuaDan extends Bullet {
    
    public ItemMuaDan(BulletManager bullMNG, byte bullId, int satThuong, Player pl, int X, int Y, int vx, int vy, int msg, int g100) {
        super(bullMNG, bullId, satThuong, pl, X, Y, vx, vy, msg, g100);
        super.isCanCollision = false;
    }
    
    @Override
    public void nextXY() {
        super.nextXY();
        if(super.isMaxY) {
            this.collect = true;
            for(int i = 0; i < 10; i++) {
                XArray.add((short)X);
                YArray.add((short)Y);
            }
            this.bullMNG.addBullet(new Bullet(bullMNG, (byte)29, this.satThuong, pl, this.X+18,  this.Y-20, 2,  -1,  15, 60));
            this.bullMNG.addBullet(new Bullet(bullMNG, (byte)29, this.satThuong, pl, this.X-19,  this.Y-20, -3, -1,  15, 60));
            this.bullMNG.addBullet(new Bullet(bullMNG, (byte)29, this.satThuong, pl, this.X+16,  this.Y-23, 3,  -2,  15, 60));
            this.bullMNG.addBullet(new Bullet(bullMNG, (byte)29, this.satThuong, pl, this.X-17,  this.Y-23, 4,  -2,  15, 60));
            this.bullMNG.addBullet(new Bullet(bullMNG, (byte)29, this.satThuong, pl, this.X+14,  this.Y-26, 3,  -3,  15, 60));
            this.bullMNG.addBullet(new Bullet(bullMNG, (byte)29, this.satThuong, pl, this.X-15,  this.Y-26, -4, -3,  15, 60));
            this.bullMNG.addBullet(new Bullet(bullMNG, (byte)29, this.satThuong, pl, this.X+11,  this.Y-28, 3,  -4,  15, 60));
            this.bullMNG.addBullet(new Bullet(bullMNG, (byte)29, this.satThuong, pl, this.X-12,  this.Y-28, -4, -4,  15, 60));
            this.bullMNG.addBullet(new Bullet(bullMNG, (byte)29, this.satThuong, pl, this.X+8,   this.Y-30, 2,  -5,  15, 60));
            this.bullMNG.addBullet(new Bullet(bullMNG, (byte)29, this.satThuong, pl, this.X-9,   this.Y-30, -3, -5,  15, 60));
            this.bullMNG.addBullet(new Bullet(bullMNG, (byte)29, this.satThuong, pl, this.X+5,   this.Y-31, 1,  -6,  15, 60));
            this.bullMNG.addBullet(new Bullet(bullMNG, (byte)29, this.satThuong, pl, this.X-6,   this.Y-31, -2, -6,  15, 60));
            this.bullMNG.addBullet(new Bullet(bullMNG, (byte)29, this.satThuong, pl, this.X-2,   this.Y-31, 1,  -7,  15, 60));
        }
    }

}
