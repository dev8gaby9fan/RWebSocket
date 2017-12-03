package com.fsh.rwebsocket.socket.response;

import com.fsh.rwebsocket.socket.WebSocketResponseMsgType;

public class WebSocketResponse {

    private WebSocketResponseMsgType type;

    public WebSocketResponse(WebSocketResponseMsgType type){
        this.type = type;
    }

    public WebSocketResponseMsgType getType() {
        return type;
    }
}
