/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package army2.server;

import java.util.ArrayList;

/**
 *
 * @author HANI
 */
public class SpecialItemData {

    public static final class SpecialItemEntry {
        int id;
        int indexSale;
        String name;
        String detail;
        short[] ability;
        int buyXu;
        int buyLuong;
        short hanSD;
        boolean showChon;
        boolean onSale;
    }

    public static ArrayList<SpecialItemEntry> entrys;
    public static int nSaleItem;
   
    public static final SpecialItemEntry getSpecialItemById(int id) {
        SpecialItemEntry spiEntry = null;
        for(int i=0; i<entrys.size(); i++) {
            SpecialItemEntry spiEntry2 = entrys.get(i);
            if(spiEntry2.id == id) {
                spiEntry = spiEntry2;
                break;
            }
        }
        return spiEntry;
    }

    public static final SpecialItemEntry getSpecialItemByIndexSale(int indexSale) {
        SpecialItemEntry spiEntry = null;
        for(int i=0; i<entrys.size(); i++) {
            SpecialItemEntry spiEntry2 = entrys.get(i);
            if(spiEntry2.onSale && spiEntry2.indexSale == indexSale) {
                spiEntry = spiEntry2;
                break;
            }
        }
        return spiEntry;
    }
    
}
