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
public class ChickyTrung extends Bullet {

    public ChickyTrung(BulletManager bullMNG, byte bullId, int satThuong, Player pl, int X, int Y, int vx, int vy, int msg, int g100) {
        super(bullMNG, bullId, satThuong, pl, X, Y, vx, vy, msg, g100);
    }

    @Override
    public void nextXY() {
        if(super.frame == 0) {
            short[] XYVC = bullMNG.getCollisionPoint(X, Y, X, (short)(Y+8), isXuyenPlayer, isXuyenMap);
            if(XYVC != null) {
                collect = true;
                X = XYVC[0]; Y = XYVC[1];
                XArray.add((short)X);
                YArray.add((short)Y);
                XArray.add((short)X);
                YArray.add((short)Y);
                if(this.isCanCollision)
                    fm.mapMNG.collision(X, Y, this);
                return;
            } else
                Y += 8;
        }
        super.nextXY();
    }
    
}
