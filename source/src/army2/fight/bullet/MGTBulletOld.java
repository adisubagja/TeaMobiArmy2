package army2.fight.bullet;

import army2.fight.Bullet;
import army2.fight.BulletManager;
import army2.fight.Player;

/**
 *
 * @author ASD
 */
public class MGTBulletOld extends Bullet {

    private final int force;

    public MGTBulletOld(BulletManager bullMNG, byte bullId, int satThuong, Player pl, int X, int Y, int vx100, int vy100, int force) {
        super(bullMNG, bullId, satThuong, pl, X, Y, vx100, vy100, 0, 0);
        this.force = force;
    }

    @Override
    public void nextXY() {
        frame++;
        XArray.add((short)X);
        YArray.add((short)Y);
        if((X < -200) || (X > fm.mapMNG.Width + 200) || (Y > fm.mapMNG.Height+200)) {
            collect = true;
            return;
        }
        short preX = (short)X, preY = (short)Y;
        vxTemp  += Math.abs(vx);
        vyTemp  += Math.abs(vy);
        if(Math.abs(vxTemp) >= 100) {
            if(vx > 0)
                X+=vxTemp/100;
            else
                X-=vxTemp/100;
            vxTemp %= 100;
        }
        if(Math.abs(vyTemp) >= 100) {
            if(vy > 0)
                Y+=vyTemp/100;
            else
                Y-=vyTemp/100;
            vyTemp %= 100;
        }
        short[] XYVC = bullMNG.getCollisionPoint(preX, preY, (short)X, (short)Y, isXuyenPlayer, isXuyenMap);
        if(XYVC != null) {
            collect = true;
            X = XYVC[0]; Y = XYVC[1];
            XArray.add((short)X);
            YArray.add((short)Y);
            if(this.isCanCollision)
                fm.mapMNG.collision((short)X, (short)Y, this);
            return;
        }
        if(frame == force) {
            vy = (short)(-vy);
            vxTemp = 0;
            vyTemp = 0;
        }
    }

}
