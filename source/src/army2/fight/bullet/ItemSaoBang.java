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
public class ItemSaoBang extends Bullet {
    
    public ItemSaoBang(BulletManager bullMNG, byte bullId, int satThuong, Player pl, int X, int Y, int vx, int vy, int msg, int g100) {
        super(bullMNG, bullId, satThuong, pl, X, Y, vx, vy, msg, g100);
        super.isCanCollision = false;
    }
    
    @Override
    public void nextXY() {
        super.nextXY();
        if(super.collect) {
            this.bullMNG.addBullet(new Bullet(bullMNG, (byte)24, this.satThuong, pl, this.X,  this.Y-187, 0, 3, 0, 50));
            for(int i = 1; i < 7; i++)
                this.bullMNG.addBullet(new Bullet(bullMNG, (byte)24, this.satThuong, pl, this.X+i*(i % 2 == 0?30:-30),  this.Y-187, 0, Math.abs(3-i), 0, 50));
        }
    }

}
