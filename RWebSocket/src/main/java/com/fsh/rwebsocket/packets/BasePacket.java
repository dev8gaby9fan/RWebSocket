package com.fsh.rwebsocket.packets;


import com.alibaba.fastjson.JSON;

public class BasePacket {
    private int pt;
    private int seq;

    public int getPt() {
        return pt;
    }

    public void setPt(int pt) {
        this.pt = pt;
    }

    public int getSeq() {
        return seq;
    }

    public void setSeq(int seq) {
        this.seq = seq;
    }

    @Override
    public String toString() {
        return "BasePacket{" +
                "pt=" + pt +
                ", seq=" + seq +
                '}';
    }

    public String toJsonString(){
        return JSON.toJSONString(this);
    }
}
