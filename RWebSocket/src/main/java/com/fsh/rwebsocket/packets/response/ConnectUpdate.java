package com.fsh.rwebsocket.packets.response;

import com.fsh.rwebsocket.packets.BasePacket;


public class ConnectUpdate extends BasePacket {

    private String r;

    public String getR() {
        return r;
    }

    public void setR(String r) {
        this.r = r;
    }
}
