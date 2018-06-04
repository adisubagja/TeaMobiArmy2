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
public class ItemTeleport extends Bullet {

    public ItemTeleport(BulletManager bullMNG, byte bullId, int satThuong, Player pl, int X, int Y, int vx, int vy, int msg, int g100) {
        super(bullMNG, bullId, satThuong, pl, X, Y, vx, vy, msg, g100);
        super.isCanCollision = false;
        super.isXuyenPlayer = true;
    }

    @Override
    public void nextXY() {
        super.nextXY();
        if(super.collect)
            fm.getPlayerTurn().setXY((short)X, (short)Y);
    }
    
}
