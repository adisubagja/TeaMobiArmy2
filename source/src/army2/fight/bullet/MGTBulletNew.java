package army2.fight.bullet;

import army2.fight.Bullet;
import army2.fight.BulletManager;
import army2.server.Until;
import army2.fight.Player;

/**
 *
 * @author ASD
 */
public class MGTBulletNew extends Bullet {

    public MGTBulletNew(BulletManager bullMNG, byte bullId, int satThuong, Player pl, int X, int Y, int vx, int vy, int msg, int g100) {
        super(bullMNG, bullId, satThuong, pl, X, Y, vx, vy, msg, g100);
        bullMNG.mgtAddX = 0;
        bullMNG.mgtAddY = 0;
    }

    @Override
    public void nextXY() {
        super.nextXY();
        if(super.isMaxY) {
            this.collect = true;
            int nextX = super.X-super.XArray.get(0);
            int nextY = super.Y-super.YArray.get(0);
            int arg = Until.getArg(nextX, nextY);
            nextX = (16 * Until.cos(arg) >> 10);
            nextY = (16 * Until.sin(arg) >> 10);
            short x = super.XArray.get(super.XArray.size()-1);
            short y = super.YArray.get(super.YArray.size()-1);
            while(true) {
                if((x < -100) || (x > fm.mapMNG.getWidth() + 100) || (y > fm.mapMNG.getHeight() + 100))
                    break;
                short[] XYVC = bullMNG.getCollisionPoint(x, y, (short)(x+nextX), (short)(y-nextY), false, false);
                if(XYVC != null) {
                    x = XYVC[0]; y = XYVC[1];
                    break;
                }
                x += nextX;
                y -= nextY;
            }
            fm.mapMNG.collision(x, y, this);
            XArray.add((short)x);
            YArray.add((short)y);
            bullMNG.mgtAddX = (byte)nextX;
            bullMNG.mgtAddY = (byte)nextY;
        }
    }

}
