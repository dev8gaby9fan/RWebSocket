package com.fsh.rwebsocket.packets;


/**
 * 心跳包
 */
public class Alive  extends BasePacket{
    private String stmp;
    public Alive() {
        this.setPt(5);
    }
}
