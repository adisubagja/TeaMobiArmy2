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
public class ItemChuotGanBom extends Bullet {
    
    private final int nStep;
    private final boolean addX;
    private int nYRoi;

    public ItemChuotGanBom(BulletManager bullMNG, byte bullId, int satThuong, Player pl, int X, int Y, byte force, boolean addX) {
        super(bullMNG, bullId, satThuong, pl, addX?(X+1):(X-1), Y, 0, 0, 0, 0);
        this.nStep = force*3;
        this.addX = addX;
        this.nYRoi = 0;
    }
    
    @Override
    public void nextXY() {
        nYRoi++;
        for(int i = 0; i < nYRoi; i++) {
            if(this.fm.mapMNG.isCollision(X, Y)) {
                nYRoi = 0;
                break;
            }
            Y++;
        }
        byte step = 2;
        if(addX)
            X += step;
        else
            X -= step;
        if(this.fm.mapMNG.isCollision(X, (short)(Y - 5))) {
            if(addX)
                X -= step;
            else
                X += step;
        } else {
            for(int i = 4; i >= 0; i--) {
                if(this.fm.mapMNG.isCollision(X, (short)(Y - i))) {
                    Y -= i;
                    break;
                }
            }
        }
        if(this.Y > this.fm.mapMNG.getHeight() + 100) {
            XArray.add((short)X);
            YArray.add((short)Y);
            this.collect = true;
            return;
        }
        XArray.add((short)X);
        YArray.add((short)Y);
        if(super.frame == nStep) {
            super.collect = true;
            fm.mapMNG.collision(X, Y, this);
            return;
        }
        super.frame++;
    }

}
