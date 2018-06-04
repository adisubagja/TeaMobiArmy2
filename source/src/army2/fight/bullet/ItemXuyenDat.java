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
public class ItemXuyenDat extends Bullet {
 
    private final int force;

    public ItemXuyenDat(BulletManager bullMNG, byte bullId, int satThuong, Player pl, int X, int Y, int vx, int vy, int msg, int g100, int force) {
        super(bullMNG, bullId, satThuong, pl, X, Y, vx, vy, msg, g100);
        this.force = force/2;
        this.isXuyenMap = true;
        this.isXuyenPlayer = false;
    }

    @Override
    public void nextXY() {
        frame++;
        short preX = X, preY = Y;
        X += vx; lastX = X;
        Y += vy; lastY = Y;
        short[] XYVC = bullMNG.getCollisionPoint(preX, preY, X, Y, isXuyenPlayer, isXuyenMap);
        if(XYVC != null) {
            collect = true;
            X = XYVC[0]; Y = XYVC[1];
            XArray.add((short)X);
            YArray.add((short)Y);
            if(this.isCanCollision)
                fm.mapMNG.collision(X, Y, this);
            return;
        }
        XArray.add((short)X);
        YArray.add((short)Y);
        if((X < -100) || (X > fm.mapMNG.getWidth() + 100) || (Y > fm.mapMNG.getHeight() + 200)) {
            XArray.add((short)X);
            YArray.add((short)Y);
            collect = true;
            return;
        }
        if(this.frame == force-1) {
            XArray.add((short)X);
            YArray.add((short)Y);
            this.collect = true;
            return;
        }
        vyTemp2 -= g100;
        if(Math.abs(vyTemp2) >= 100) {
            vy-=vyTemp2/100;
            vyTemp2 %= 100;
        }
        if(this.bullMNG.hasVoiRong) {
            for(BulletManager.VoiRong vr : this.bullMNG.voiRongs) {
                if(this.X >= vr.X-5 && this.X <= vr.X+10) {
                    this.vx-=2;
                    this.vy-=2;
                    break;
                }
            }
        }
    }

}
