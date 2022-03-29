package it.unibo.radarSystem22_4.comm.interfaces;

public interface IApplMessage {

    public String msgId();
    public String msgType();
    public String msgSender();
    public String msgReceiver();
    public String msgContent();
    public String msgNum();

    public boolean isDispatch();
    public boolean isRequest();
    public boolean isReply();
    public boolean isEvent();

    
}
