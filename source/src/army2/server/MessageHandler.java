package army2.server;

import network.Message;
import network.IMessageHandler;
import java.io.IOException;

/**
 *
 * @author ASD
 */
public class MessageHandler implements IMessageHandler {

    private final ClientEntry client;

    MessageHandler(ClientEntry client) {
        this.client = client;
    }
    
    @Override
    public void onMessage(Message mss) {
        if(mss != null) {
            try {
                switch(mss.getCommand()) {
                    // Handsake
                    case -27:
                        client.hansakeMessage();
                        break;

                    // Gia han do
                    case -25:
                        if(client.user.getState() == User.State.Waiting)
                            client.user.giaHanMessage(mss);
                        break;

                    // Nhiem vu view
                    case -23:
                        if(client.user.getState() == User.State.Waiting)
                            client.user.missionMessage(mss);
                        break;

                    // Hop trang bi
                    case -18:
                        if(client.user.getState() == User.State.Waiting)
                            client.user.hopTBMessage(mss);
                        break;
                    
                    // Bang xep hang
                    case -14:
                        ServerManager.bangXHMessage(client.user, mss);
                        break;

                    // Clan item
                    case -12:
                        ServerManager.clanItemMessage(client.user, mss);
                        break;

                    // Luyen tap start
                    case -6:
                        if(client.user.getState() == User.State.Waiting)
                            client.user.luyentap.luyenTapMessage(client.user);
                        break;

                    // Disconnect
                    case -4:
                        client.closeMessage();
                        break;

                    // Do dac biet shop
                    case -3:
                        if(client.user.getState() == User.State.Waiting)
                            client.user.specialShopMessage(mss);
                        break;

                    // Mac set trang bi
                    case -2:
                        if(client.user.getState() == User.State.Waiting)
                            client.user.setSetMessage(mss);
                        break;

                    // Login
                    case 1:
                        if(client.user == null)
                            client.loginMessage(mss);
                        break;

                    // Send message
                    case 5:
                        ServerManager.userSendMessage(client.user, mss);
                        break;

                    // Den khu vuc
                    case 6:
                        if(client.user.getState() == User.State.Waiting)
                            ServerManager.getRoomsMessage(client.user);
                        break;

                    // Vao phong
                    case 7:
                        if(client.user.getState() == User.State.Waiting)
                            ServerManager.getRoomNumberMessage(client.user, mss);
                        break;

                    // Tham gia khu vuc
                    case 8:
                        if(client.user.getState() == User.State.Waiting)
                            ServerManager.joinRegionMessage(client.user, mss);
                        break;

                    // Chat Message
                    case 9:
                        if(client.user.getState() == User.State.Fighting)
                            client.user.fight.chatMessage(client.user, mss);
                        else if(client.user.getState() == User.State.WaitFight)
                            client.user.waitFight.chatMessage(client.user, mss);
                        break;

                    // Kick
                    case 11:
                        if(client.user.getState() == User.State.WaitFight)
                            client.user.waitFight.kickMessage(client.user, mss);
                        break;

                    // Roi khu vuc
                    case 15:
                        if(client.user.getState() == User.State.Fighting)
                            client.user.fight.leave(client.user);
                        else if(client.user.getState() == User.State.WaitFight)
                            client.user.waitFight.leave(client.user);
                        break;

                    // San sang
                    case 16:
                        if(client.user.getState() == User.State.WaitFight)
                            client.user.waitFight.readyMessage(client.user, mss);
                        break;

                    // Hop ngoc
                    case 17:
                        if(client.user.getState() == User.State.Waiting)
                            client.user.hopNgocMessage(mss);
                        break;

                    // Dat mat khau
                    case 18:
                        if(client.user.getState() == User.State.WaitFight)
                            client.user.waitFight.setPassMessage(client.user, mss);
                        break;

                    // Dat cuoc
                    case 19:
                        if(client.user.getState() == User.State.WaitFight)
                            client.user.waitFight.setMoneyMessage(client.user, mss);
                        break;

                    // Bat dau game
                    case 20:
                        if(client.user.getState() == User.State.WaitFight)
                            client.user.waitFight.startMessage(client);
                        break;

                    // Change location
                    case 21:
                        if(client.user.getState() == User.State.Fighting)
                            client.user.fight.changeLocationMessage(client.user, mss);
                        break;

                    // Shoot
                    case 22:
                    case 84:
                        if(client.user.getState() == User.State.Fighting)
                            client.user.fight.shootMessage(client.user, mss);
                        break;

                    // Notify Net Wait
                    case 23:
                        if(client.user.getState() == User.State.Fighting)
                            client.user.notifyNetWaitMessage();
                        break;

                    // Dung item
                    case 26:
                        if(client.user.getState() == User.State.Fighting)
                            client.user.fight.useItemMessage(client.user, mss);
                        break;

                    // Choi ngay
                    case 28:
                        if(client.user.getState() == User.State.Waiting)
                            ServerManager.enterShortPlayMessage(client.user, mss);
                        break;

                    // Xem ban be
                    case 29:
                        client.user.viewFriendsMessage();
                        break;

                    // Ket ban
                    case 32:
                        client.user.addFriendsMessage(mss);
                        break;

                    // Xoa ban
                    case 33:
                        client.user.deleteFriendsMessage(mss);
                        break;

                    // Xem thong tin
                    case 34:
                        client.user.viewTTMessage(mss);
                        break;

                    // Tim user
                    case 36:
                        client.user.findUserMessage(mss);
                        break;

                    // Bo luot
                    case 49:
                        if(client.user.getState() == User.State.Fighting)
                            client.user.fight.boLuotMessage(client.user);
                        break;

                    // Set XY
                    case 53:
                        if(client.user.getState() == User.State.Fighting)
                            client.user.fight.changeLocationMessage(client.user, mss);
                        break;

                    // Dat ten khu vuc
                    case 54:
                        if(client.user.getState() == User.State.WaitFight)
                            client.user.waitFight.setNameMessage(client.user, mss);
                        break;

                    // Dat so nguoi
                    case 56:
                        if(client.user.getState() == User.State.WaitFight)
                            client.user.waitFight.setMaxPlayerMessage(client.user, mss);
                        break;

                    // Set Item
                    case 68:
                        if(client.user.getState() == User.State.WaitFight)
                            client.user.waitFight.setItemMessage(client.user, mss);
                        break;

                    // Chon nhan vat
                    case 69:
                        if(client.user.getState() == User.State.Waiting)
                            client.user.selectNVMessage(mss);
                        break;

                    // Doi phe
                    case 71:
                        if(client.user.getState() == User.State.WaitFight)
                            client.user.waitFight.doiPheMessage(client.user, mss);
                        break;

                    // Mua item
                    case 72:
                        if(client.user.getState() == User.State.Waiting)
                            client.user.buyItemMessage(mss);
                        break;

                    // Mua nhan vat
                    case 74:
                        if(client.user.getState() == User.State.Waiting)
                            client.user.buyNVMessage(mss);
                        break;

                    // Chon ban do
                    case 75:
                        if(client.user.getState() == User.State.WaitFight)
                            client.user.waitFight.setMapMessage(client.user, mss);
                        break;

                    // Nap the cao
                    case 77:
                        ServerManager.napTheMessage(client.user, mss);
                        break;

                    // Tim ban choi
                    case 78:
                        if(client.user.getState() == User.State.WaitFight)
                            client.user.waitFight.findPlayerMessage(client.user, mss);
                        break;

                    // Remove bull
                    case 79:
                        if(client.user.getState() == User.State.Fighting)
                            client.user.fight.removeBullMessage(client.user, mss);
                        break;

                    // Đổi password
                    case 81:
                        if(client.user.getState() == User.State.Waiting)
                            client.user.changePassMessage(mss);
                        break;

                    // Luyen tap
                    case 83:
                        client.user.luyentap.startLuyenTapMessage(client.user, mss);
                        break;

                    // send get pack
                    case 90:
                        ServerManager.getPackMessage(client.user, mss);
                        break;

                    // Nang cap ok message
                    case 98:
                        if(client.user.getState() == User.State.Waiting)
                            client.user.nangcapOkMessage(mss);
                        break;

                    // Nang cap message
                    case 99:
                        if(client.user.getState() == User.State.Waiting)
                            client.user.sendPointAddInfo();
                        break;

                    // Mac trang bi
                    case 102:
                        if(client.user.getState() == User.State.Waiting)
                            client.user.setEquipMessage(mss);
                        break;

                    // Cua hang trang bi
                    case 103:
                        if(client.user.getState() == User.State.Waiting)
                            ServerManager.equipShopMessage(client.user);
                        break;

                    // Mua trang bi
                    case 104:
                        if(client.user.getState() == User.State.Waiting)
                            client.user.buyEquipMessage(mss);
                        break;

                    // Quay so
                    case 110:
                        if(client.user.getState() == User.State.Waiting)
                            client.user.quaysoMessage(mss);
                        break;

                    // Plastfrom
                    case 114:
                        client.plastfrom = mss.reader().readUTF();
                        break;

                    // Get clan icon
                    case 115:
                        ServerManager.getClanIconMessage(client, mss);
                        break;

                    // Top doi
                    case 116:
                        if(client.user.getState() == User.State.Waiting)
                            ServerManager.getTopTeamMessage(client.user, mss);
                        break;

                    // Get big icon
                    case 120:
                        ServerManager.getBigIconMessage(client, mss);
                        break;

                    // Nap tien message
                    case 122:
                        ServerManager.napTienMessage(client.user, mss);
                        break;
                        
                    // Get meterial icon
                    case 126:
                        ServerManager.getMaterialIconMessage(client, mss);
                        break;
                }
            } catch(IOException e) {
                System.out.println(e.toString());
            }
        }
    }

    @Override
    public void onConnectionFail() {
    }
    
    @Override
    public void onDisconnected() {
    }
    
    @Override
    public void onConnectOK() {
    }

}
