package com.fsh.rwebsocket.socket.response;

import com.fsh.rwebsocket.socket.WebSocketResponseMsgType;


public class TextMessageResponse extends WebSocketResponse {
    private String response;
    public TextMessageResponse(String response) {
        super(WebSocketResponseMsgType.MESSAGE_TEXT);
        this.response = response;
    }

    public String getResponse() {
        return this.response;
    }
}
