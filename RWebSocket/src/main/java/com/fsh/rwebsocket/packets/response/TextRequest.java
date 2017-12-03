package com.fsh.rwebsocket.packets.response;

import com.fsh.rwebsocket.packets.BasePacket;


public class TextRequest extends BasePacket {
    private String request;

    public TextRequest(String request){
        this();
        this.request = request;
    }

    public TextRequest(){
        this.setPt(120);
    }

    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }
}
