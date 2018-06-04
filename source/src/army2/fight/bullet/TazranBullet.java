package army2.fight.bullet;

import army2.fight.Bullet;
import army2.fight.BulletManager;
import army2.fight.Player;

/**
 *
 * @author ASD
 */
public class TazranBullet extends Bullet {

    protected byte    quayLai;
    protected boolean addTZ;

    public TazranBullet(BulletManager bullMNG, byte bullId, int satThuong, Player pl, int X, int Y, int vx, int vy, int msg, int g100) {
        super(bullMNG, bullId, satThuong, pl, X, Y, vx, vy, msg, g100);
        this.quayLai = -1;
        this.addTZ   = (vx <= 0);
    }

    @Override
    public void nextXY() {
        super.nextXY();
        if(quayLai == 0) {
            if(addTZ)
                vx+=1;
            else
                vx-=1;
            quayLai = 1;
        } else if(quayLai == 1) {
            if(addTZ)
                vx+=2;
            else
                vx-=2;
        } else if(super.isMaxY)
            quayLai = 0;
    }

}
