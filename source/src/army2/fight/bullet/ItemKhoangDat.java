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
public class ItemKhoangDat extends Bullet {

    private final int nFrame;

    public ItemKhoangDat(BulletManager bullMNG, byte bullId, Player pl, int X, int Y, byte force) {
        super(bullMNG, bullId, 0, pl, X, Y, 0, 0, 0, 0);
        this.nFrame = force*2;
    }
    
    @Override
    public void nextXY() {
        this.Y+=2;
        this.frame++;
        this.XArray.add((short)X);
        this.YArray.add((short)Y);
        if(this.fm.mapMNG.isCollision(X, Y))
            this.fm.mapMNG.collision(X, Y, this);
        if(this.frame == nFrame)
            this.collect = true;
    }
    
}
