/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package army2.fight;

import army2.server.ServerManager;
import java.io.IOException;

/**
 *
 * @author ASD
 */
public class CountDownMNG implements Runnable {

    private final FightManager fightMNG;
    public  byte second;
    private byte countTime;
    private boolean startCount;
    private Thread countThread;

    public CountDownMNG(FightManager fightMNG, byte countTime) {
        this.fightMNG = fightMNG;
        this.startCount  = false;
        this.second = 0;
        this.countTime = countTime;
        this.countThread = null;
    }

    public final void setCountTime(byte count) {
        countTime = count;
    }

    public final void resetCount() {
        this.stopCount();
        second = countTime;
        startCount = true;
        ServerManager.log("SecondCount= " + second);
        countThread = new Thread(this);
        countThread.start();
    }

    public final void run() {
        try {
            while(startCount) {
                Thread.sleep(1000L);
                second--;
                if(second == -10) {
                    fightMNG.countOut();
                    startCount = false;
                }
            }
        } catch(IOException e) {
            e.printStackTrace();
        } catch(InterruptedException e1) {
            ServerManager.log("Stop count!");
        }
    }

    public final void stopCount() {
        startCount = false;
        if(this.countThread != null && this.countThread.isAlive())
            this.countThread.interrupt();
    }

}
