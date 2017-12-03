package com.fsh.rwebsocket.packets.request;

import com.alibaba.fastjson.JSONObject;


public interface JSONRequest {
    String toJsonString();

    JSONObject toJSONObject();
}
