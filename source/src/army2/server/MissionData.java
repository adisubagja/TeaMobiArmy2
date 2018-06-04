/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package army2.server;

import java.util.ArrayList;

/**
 *
 * @author ASD
 */
public class MissionData {
    
    public static class MissionEntry {
        int index;
        byte level;
        String name;
        int require;
        String reward;
        int rewardXu;
        int rewardLuong;
        int rewardXP;
        int rewardCUP;
        MissDataEntry mDatE;
    }
    
    public static class MissDataEntry {
        int id;
        byte idNeed;
        ArrayList<MissionEntry> entrys;
    }
    
    public static ArrayList<MissDataEntry> entrys;

    public static void addMissionEntry(int id, byte idneed, MissionEntry mEntry) {
        System.out.println("Set mission id="+id+" idneed="+idneed);
        MissDataEntry mDatE = null;
        for(MissDataEntry mDatE1 : entrys) {
            if(mDatE1.id == id) {
                mDatE = mDatE1;
                break;
            }
        }
        if(mDatE == null) {
            mDatE = new MissDataEntry();
            mDatE.id = id;
            mDatE.idNeed = idneed;
            mDatE.entrys = new ArrayList<>();
            entrys.add(mDatE);
        }
        for(MissionEntry mE : mDatE.entrys) {
            if(mE.level == mEntry.level)
                return;
        }
        mEntry.mDatE = mDatE;
        mDatE.entrys.add(mEntry);
    }

    public static MissionEntry getMissionData(int index) {
        for(MissDataEntry mDatE1 : entrys) {
            for(MissionEntry me : mDatE1.entrys) {
                if(me.index == index)
                    return me;
            }
        }
        return null;
    }
    
}
