package network;

public interface IMessageHandler {

    public void onMessage(Message message);
    public void onConnectionFail();
    public void onDisconnected();
    public void onConnectOK();
    
}
