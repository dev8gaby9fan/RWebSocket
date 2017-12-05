package com.fsh.webscoket;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fsh.rwebsocket.packets.request.JSONRequest;

public class RequestPacket extends BaseDescription implements JSONRequest {;
    private BasePacket p;

    public RequestPacket(BasePacket p) {
        this.p = p;
    }


    public BasePacket getP() {
        return p;
    }

    public void setP(BasePacket p) {
        this.p = p;
    }


    @Override
    public String toString() {
        return "RequestPacket{" +
                "p_ver=" + this.p_ver +
                ", p_no=" + this.p_no +
                ", p=" + p +
                '}';
    }

    public String toJsonString(){
        return JSON.toJSONString(this);
    }
}
