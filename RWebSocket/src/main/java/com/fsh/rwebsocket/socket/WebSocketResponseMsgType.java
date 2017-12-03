package com.fsh.rwebsocket.socket;

public enum WebSocketResponseMsgType {
    /**
     * 文本消息
     */
    MESSAGE_TEXT(0),
    /**
     * 二进制消息
     */
    MESSAGE_BINARY(1),
    /**
     * 物理连接成功之后，返回的信息
     */
    CONNECTSTATUS(2),
    /**
     * 错误消息
     */
    ERROR_MESSAGE(3);
    int value;
    WebSocketResponseMsgType(int value){
        this.value = value;
    }

    public int value(){
        return this.value;
    }
}
