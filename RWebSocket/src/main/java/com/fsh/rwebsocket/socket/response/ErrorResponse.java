package com.fsh.rwebsocket.socket.response;

import com.fsh.rwebsocket.socket.WebSocketResponseMsgType;


/**
 * WebSocket错误信息
 * 当内部发生错误时，发送此消息
 */
public class ErrorResponse extends WebSocketResponse {
    private String msg;
    private Throwable t;

    public ErrorResponse(String msg,Throwable t) {
        super(WebSocketResponseMsgType.ERROR_MESSAGE);
        this.msg = msg;
        this.t = t;
    }

    public String getMsg(){
        return msg;
    }

    public Throwable getThrowable(){
        return t;
    }
}
