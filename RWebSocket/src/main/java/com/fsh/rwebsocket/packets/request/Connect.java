package com.fsh.rwebsocket.packets.request;

import com.fsh.rwebsocket.packets.BasePacket;
import com.fsh.rwebsocket.util.DiviceUtil;

public class Connect extends BasePacket {
    private String appid = "App";
    private String clientid;
    private String ver = "1.0";
    public Connect() {
        this.setPt(1);
        this.clientid = DiviceUtil.getDeviceID();
    }

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getVer() {
        return ver;
    }

    public void setVer(String ver) {
        this.ver = ver;
    }

    public String getClientid() {
        return clientid;
    }

    public void setClientid(String clientid) {
        this.clientid = clientid;
    }

    @Override
    public String toString() {
        return "Connect{" +
                "appid='" + appid + '\'' +
                ", clientid='" + clientid + '\'' +
                ", ver='" + ver + '\'' +
                '}';
    }
}
