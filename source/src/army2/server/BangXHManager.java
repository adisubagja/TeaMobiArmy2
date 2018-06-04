/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package army2.server;

import java.util.ArrayList;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Timer;
import java.util.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimerTask;


/**
 *
 * @author ASD
 */
public class BangXHManager {
    
    public static class BangXHEntry {
        int index;
        int iddb;
        int nXH;
    }

    public static final String[] bangXHString  = new String[]{"DANH DỰ", "CAO THỦ", "ĐẠI GIA XU", "ĐẠI GIA LƯỢNG"};
    public static final String[] bangXHString1 = new String[]{"Danh dự", "xp", "xu", "lượng"};
    @SuppressWarnings("unchecked")
	public static final ArrayList<BangXHEntry> bangXH[] = new ArrayList[4];
    public static final Timer t = new Timer(true);

    public static void init() {
        for(int i = 0; i < bangXH.length; i++)
            bangXH[i] = new ArrayList<>();
        Calendar cl = GregorianCalendar.getInstance();
        Date d = new Date();
        cl.setTime(d);
        cl.set(Calendar.HOUR_OF_DAY, 0);
        cl.set(Calendar.MINUTE, 0);
        cl.set(Calendar.SECOND, 0);
        cl.add(Calendar.DATE, 1);
        t.schedule(new TimerTask() {
            @Override
            public void run() {
                System.out.println("Refresh BXH");
                for(int i = 0; i < bangXHString.length; i++)
                    refreshXH(i);
            }
        }, cl.getTime(), 86400000L);
    }

    public static void refreshXH(int type) {
        bangXH[type].clear();
        ArrayList<BangXHEntry> bxh = bangXH[type];
        switch(type) {
            case 0:
                try {
                    int i = 0;
                    ResultSet red = SQLManager.stat.executeQuery("SELECT `id`,`dvong` FROM `armymem` ORDER BY `dvong` DESC ;");
                    while(red.next()) {
                        int iddb = red.getInt("id");
                        int dvong = red.getInt("dvong");
                        if(iddb < 3)
                            continue;
                        if(i < 100) {
                            BangXHEntry bXHE = new BangXHEntry();
                            bXHE.iddb = iddb;
                            bXHE.index = i;
                            bXHE.nXH = dvong;
                            bxh.add(bXHE);
                        }
                        SQLManager.stat.executeUpdate("UPDATE `armymem` SET `top`='"+i+"' WHERE `id`="+iddb+" LIMIT 1;");
                        i++;
                    }
                } catch(SQLException e) {
                    e.printStackTrace();
                }
                break;

            case 1:
                try {
                    int i = 0;
                    ResultSet red = SQLManager.stat.executeQuery("SELECT `id`,`xpMax` FROM `armymem` ORDER BY `xpMax` DESC LIMIT 0, 100;");
                    while(red.next()) {
                        int iddb = red.getInt("id");
                        int xpMax = red.getInt("xpMax");
                        if(iddb < 3)
                            continue;
                        BangXHEntry bXHE = new BangXHEntry();
                        bXHE.iddb = iddb;
                        bXHE.index = i++;
                        bXHE.nXH = xpMax;
                        bxh.add(bXHE);
                    }
                } catch(SQLException e) {
                    e.printStackTrace();
                }
                break;

            case 2:
                try {
                    int i = 0;
                    ResultSet red = SQLManager.stat.executeQuery("SELECT `id`,`xu` FROM `armymem` ORDER BY `xu` DESC LIMIT 0, 100;");
                    while(red.next()) {
                        int iddb = red.getInt("id");
                        int xu = red.getInt("xu");
                        if(iddb < 3)
                            continue;
                        BangXHEntry bXHE = new BangXHEntry();
                        bXHE.iddb = iddb;
                        bXHE.index = i++;
                        bXHE.nXH = xu;
                        bxh.add(bXHE);
                    }
                } catch(SQLException e) {
                    e.printStackTrace();
                }
                break;

            case 3:
                try {
                    int i = 0;
                    ResultSet red = SQLManager.stat.executeQuery("SELECT `id`,`luong` FROM `armymem` ORDER BY `luong` DESC LIMIT 0, 100;");
                    while(red.next()) {
                        int iddb = red.getInt("id");
                        int luong = red.getInt("luong");
                        if(iddb < 3)
                            continue;
                        BangXHEntry bXHE = new BangXHEntry();
                        bXHE.iddb = iddb;
                        bXHE.index = i++;
                        bXHE.nXH = luong;
                        bxh.add(bXHE);
                    }
                } catch(SQLException e) {
                    e.printStackTrace();
                }
                break;
        }
    }
    
    public static final BangXHEntry[] getBangXH(int type, int page) {
        ArrayList<BangXHEntry> bxh = bangXH[type];
        int index = page*10, lent = 0;
        if(index < bxh.size())
            lent = (bxh.size()-index)>10?10:(bxh.size()-index);
        BangXHEntry[] bxhA = new BangXHEntry[lent];
        for(int i = 0; i < lent; i++)
            bxhA[i] = bxh.get(index+i);
        return bxhA;
    }
    
}
