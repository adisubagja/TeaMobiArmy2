/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package army2.fight;

/**
 *
 * @author ASD
 */
public abstract class Boss extends Player {

    public String name;

    public Boss(FightManager fightMNG, byte idGun, String name, byte location, int HPMax, short X, short Y) {
        super(fightMNG, location, X, Y, null, (byte)0, null);
        this.idNV      = idGun;
        this.HPMax     = HPMax;
        this.HP        = this.HPMax;
        this.name      = name;
        this.sTPercen  = 100;
        this.phongThu  = 0;
        this.mayMan    = 0;
    }

    protected abstract void turnAction();
    
}
