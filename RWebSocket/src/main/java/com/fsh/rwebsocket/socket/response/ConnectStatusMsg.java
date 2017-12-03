package com.fsh.rwebsocket.socket.response;

import com.fsh.rwebsocket.socket.ConnectStatus;
import com.fsh.rwebsocket.socket.WebSocketResponseMsgType;

import okhttp3.Response;

/**
 * WebSocket连接状态消息
 * 当连接状态发生变化时，就会发送这个消息
 */
public class ConnectStatusMsg extends WebSocketResponse {
    private ConnectStatus status;
    public ConnectStatusMsg(ConnectStatus status) {
        super(WebSocketResponseMsgType.CONNECTSTATUS);
        this.status = status;
    }

    public ConnectStatus getStatus(){
        return this.status;
    }
}
